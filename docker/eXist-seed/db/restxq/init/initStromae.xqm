xquery version "3.0";
module namespace init="http://www.insee.fr/coltrane/orbeon/init";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace colerr="http://www.insee.fr/coltrane/error";
declare namespace col="http://www.insee.fr/coltrane";

declare variable $init:okk-init := <Resultat><Code>401</Code><Libelle>Infos Persos créées</Libelle></Resultat> ;
declare variable $init:err-init := <Resultat><Code>411</Code><Libelle>erreur - Infos Persos non créées</Libelle></Resultat> ;


declare variable $init:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/init/helloworld'}</xqm><version>{$init:version}</version><message>{"Quand la tartine de Chuck Norris tombe, la confiture change de côté."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/coltrane/init/helloworld")
function init:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/init/helloworld'}</xqm><version>{$init:version}</version><message>{"Quand la tartine de Chuck Norris tombe, la confiture change de côté."}</message></resultat>
    return (common:rest-response(200),$mess)
};


(: get questionnaire :)
declare
    %rest:GET
    %rest:path("/coltrane/init/{$enquete}/{$modele}/{$unite}")
function init:get-init ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*) as item()+  {
    let $log := util:log("INFO", concat("init:get-init - enquete : ", $enquete,", modele : ",$modele,", unite : ",$unite))
    let $doc := fn:lower-case(concat('/db/orbeon/fr/',$enquete,'/',$modele,'/data/init/',$col,'/',$unite,'.xml'))

return  
    if (doc-available($doc)) then (common:rest-response(200),doc($doc))
    else (common:rest-response(404))
};

(: delete questionnaire :)
declare
    %rest:DELETE
    %rest:path("/coltrane/init/{$enquete}/{$modele}/{$unite}")
function init:delete-init ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*) as item()+  {
    let $log := util:log("INFO", concat("init:delete-init - enquete : ", $enquete,", modele : ",$modele,", unite : ",$unite))
    let $t := xmldb:remove($racine-enquete||$enquete||'/'||$modele||'/data/init/'||$col, $unite||'.xml')
return  (common:rest-response(202),$t)
};

declare
%rest:GET
%rest:path("/collectes/init/perso/{$perso}")
%rest:query-param("detaillog", "{$detaillog}","log")
function local:initForm-no-mapping($perso as xs:string*,$detaillog as xs:string*) as item()+{
let $log:= util:log("INFO","init:init-from-perso[$perso="||$perso||", $detaillog="||$detaillog||']')
return try{
    let $uriFichier :='/db/tmp/init/'||$perso
    let $log:= util:log("INFO","init:init-from-perso[$uriFichier="||$uriFichier||"]")    
    let $fichierExiste:= if(doc-available($uriFichier))then()else(error(xs:QName('colerr:docNonAvailable'),"le fichier de perso "||$uriFichier||" n'existe pas"  ))
    let $initFile := doc($uriFichier)
let $isOuverture:= if(not($initFile/Campagne/Traitement/Type) or $initFile/Campagne/Traitement/Type/text()='Ouverture' or $initFile/Campagne/Traitement/Type/text()='Ouverture sans courrier' or $initFile/Campagne/Traitement/Type/text()='Ouverture avec courrier')then()else(error(xs:QName('colerr:notTreatable'),"le fichier de perso "||$uriFichier||" n'est pas à traiter","(traitement : " ||$initFile/Campagne/Traitement/Type||")" ))
  
    let $campagne :=lower-case($initFile//@idSource||"-"||$initFile//@millesime||"-"||$initFile//@idPeriode)
    let $log:= util:log("INFO", "init:init-from-perso[$perso="||$perso||", $campagne="||$campagne||"]")

    let $chemin-campagne := '/db/orbeon/fr/'||$campagne   

   let $modeles:= for $modele in distinct-values($initFile//Questionnaire/@idModele)
                   return if(doc-available($chemin-campagne||"/"||lower-case($modele)||"/form/form.xhtml"))then(
                        common:create-collection-if-necessary($chemin-campagne||"/"||lower-case($modele)||"/data/init")                   
                   )else(
                    error( xs:QName('colerr:docNonAvailable'),"le fichier form de la campagne "||$campagne||" du modèle "||$modele||" n'existe pas"  )
                    )                  
   let $jobInit := <Resultats>{
        for $questionnaire in $initFile//Questionnaire
            let $jobInitq := local:initForm($chemin-campagne,$questionnaire)
            let $fichierinitial:=if($detaillog='fichierinitial')then(local:add-traitement($questionnaire,$jobInitq))else()
            let $log:=if($detaillog='retourerreur')then(<Resultat><IdUniteeEnquetee>{$questionnaire//Identifiant/text()}</IdUniteeEnquetee>{$jobInitq/*}</Resultat>)else($jobInitq)
         return $log
         }</Resultats>
         
     let $infos:="init:init-from-perso : l'initialisation s'est bien passée pour " ||count($jobInit/a[Resultat=$init:okk-init])|| " questionnaires et mal pour "||count($jobInit/a[Resultat=$init:err-init])
    let $log:= util:log("INFO", $infos)
    return (common:rest-response(201), 
        if($detaillog='retourerreur')then($jobInit)else($infos)
        )

}catch *{ 
      let $log:= util:log("ERROR","init:init-from-perso - "||$err:code ||" : "||$err:description ||" - "||$err:value||"]")     
      return (common:rest-response(404),$err:code ||" : "||$err:description ||" - "||$err:value ) 
      }
}; 
(:~ 
 : Nouvelle version sans mapping 
 : Update an existing interrogation or store a new one. No check
:)
declare function local:initForm($chemin-enquete as xs:string*,$questionnaire as node()*) as item()+ {
    let $log := util:log("DEBUG", "local:initForm - $chemin-enquete : "|| $chemin-enquete||", content ")
    let $idModele := lower-case($questionnaire/descendant-or-self::Questionnaire/@idModele[1])
    let $idQuestionnaire := $questionnaire/descendant-or-self::Questionnaire/InformationsGenerales/UniteEnquetee/Identifiant/text()
   let $log := util:log("DEBUG", "initForm - modele : "|| $idModele||", idQuestionnaire : "||$idQuestionnaire)      
  
    let $chemin-init := common:create-collection-if-necessary($chemin-enquete||"/"||$idModele||"/data/init/"|| common:calcol($idQuestionnaire))
    let $xform :=$chemin-enquete||"/"||$idModele||"/form/form.xhtml"
    let $log := util:log("DEBUG", "initForm - collection d'init : "|| $chemin-init||",$xform :"||$xform)   
    let $doc:=doc($xform)//form
    let $form-init:= 
         <form>{$doc/@*,local:transform3($doc/*,$questionnaire/InformationsPersonnalisees)
        }</form>
   let $cheminFinal:=xmldb:store($chemin-init, $idQuestionnaire||".xml",$form-init)    
   let $retour := if(doc-available($cheminFinal)) then($init:okk-init) else($init:err-init)  
                  
   let $log := util:log("INFO", "local:initForm : $idQuestionnaire"|| $idQuestionnaire||", retour : "|| $retour/Resultat||", chemin : "||$cheminFinal )
   return (<a>{$retour}</a>)

}; 


(:~
: <li>Si à la racine du form, y'a un <Groupe>, on copie le groupe de même identifiant du fichier de perso, 
: et on continue les traitements uniquement sur des données de ce fichier
: <li>Si on tombe sur une <Variable>, on copie ce qu'il y a dans la <Valeur> de la variable de même identifiant du fichier de perso. 
: Les identifiants doivent être unique au sein d'un même groupe, et en dehors des groupe doivent être unique (pas le même qu'un dans un groupe)
: <li>Si on tombe sur un autre élément : Si c'est du texte, on enlève le préfixe xhtml. Sinon on copie et on continue les traitement du l'intérieur
:)
declare function local:transform3($nodes,  $input) {
    for $node in $nodes
    return typeswitch($node)          
            case element(Groupe) return                  
                <Groupe>{
                    $node/@*,
                    local:transform3($input/Groupe[@idGroupe=$node/@idGroupe]/*,  
                        $input/Groupe[@idGroupe=$node/@idGroupe])
                }</Groupe>
            case element(Variable) return                  
                <Variable>{
                    $node/@*,
                    $input//Variable[@idVariable=$node/@idVariable]/Valeur/text() 
                }</Variable>
            case element() return
                element { local-name($node) } {
                    $node/@*,
                    if(empty($node/*))then(
                        replace($node/text(),'xhtml:','')
                    )else(
                        local:transform3($node/*,$input))
                    }
            default return
                $node
};



(:~
 : TODO mettre dans common.xqm ? : pas pour l'instant, c'est utilisé qu'ici
 : fonction qui permet d'ajouter en premier fils de $nodes un élément <Traitement>{$resultat}</Traitement>, ou, si <Traitement> est déjà présent, d'ajouter en dernier fils de ce dernier le noeud $resultat  
 :)
declare function local:add-traitement($nodes as node()*,$resultat as node()*) as item()*  {
(: avec update, pas moyen de mettre d'abord Traitement pour ceux qui en ont besooin, puis Résultat pour tous:)
if(not($nodes/Traitement)) then(
    update insert <Traitement>{$resultat}</Traitement> into $nodes
)else(                       
    update insert $resultat into $nodes/Traitement
)  
};
