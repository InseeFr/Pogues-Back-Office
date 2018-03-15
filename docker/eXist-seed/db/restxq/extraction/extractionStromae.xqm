xquery version "3.0";
module namespace extraction="http://www.insee.fr/coltrane/extractions";
declare option exist:output-size-limit "10000000";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
import module namespace model="http://www.insee.fr/collectes/model" at "../model/model.xqm";

declare variable $extraction:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/extraction/helloworld'}</xqm><version>{$extraction:version}</version><message>{"Quand Chuck Norris entre dans la cuisine, Brian sort."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/extraction/helloworld")
function extraction:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/extraction/helloworld'}</xqm><version>{$extraction:version}</version><message>{"Quand Chuck Norris entre dans la cuisine, Brian sort."}</message></resultat>
    return (common:rest-response(200),$mess)
};

(:~
 : met les questionnaire envoyés après la date à extrait = non
 :
 : GET /extractions/revert/{$enquete} 
 : @param $dateToRevert de la forme aaaammjj (la forme aaaa-mm-jj marche aussi, mais pas aaaa/mm/jj)
 :)
declare
%rest:GET
%rest:path("/extractions/revert/{$enquete}")
%rest:query-param("dateToRevert", "{$dateToRevert}","")
function extraction:extraction-revert-enquete( $enquete as xs:string*, $dateToRevert as xs:string*) as node()* 
{
let $log := util:log("INFO", "extraction:extraction-revert[$enquete="||$enquete||", $dateToRevert="||$dateToRevert||"]")  
let $racine-enquete :='/db/orbeon/fr/'||$enquete 

let $datetemp := if (matches($dateToRevert,'^\D*(\d{4})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*$')) 
                 then replace($dateToRevert, '^\D*(\d{4})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*(\d{2})\D*$', '$1-$2-$3T$4:$5:$6')
                 else replace($dateToRevert, '^\D*(\d{4})\D*(\d{2})\D*(\d{2})\D*$', '$1-$2-$3T00:00:00')

let $datedate:=xs:dateTime($datetemp)
let $tokenize:=tokenize(upper-case($enquete),"-")
return 

    <Campagne idSource="{$tokenize[1]}" millesime="{$tokenize[2]}" idPeriode="{$tokenize[3]}" >
    <Traitement><Type>Revert date : {$dateToRevert}</Type></Traitement>
    <Questionnaires>
    {
    for $model in xmldb:get-child-collections($racine-enquete)     
    return                  
        if(xmldb:collection-available($racine-enquete||"/"||$model||'/data'))
        then(
        for $hash-reponse in xmldb:get-child-collections($racine-enquete||"/"||$model||'/data/')[.!='init']   
        return                  
            for $reponse in xmldb:get-child-resources($racine-enquete||"/"||$model||'/data/'||$hash-reponse)
            let $docReponse := doc($racine-enquete||"/"||$model||'/data/'||$hash-reponse||'/'||$reponse)
            let $dateReponse := $docReponse/form/stromae/util/dateHeure/text()
            return
                if(matches($dateReponse,'^(\d{2})-(\d{2})-(\d{4}).*$') and $datedate<=xs:dateTime(replace($dateReponse,'^(\d{2})-(\d{2})-(\d{4}).*(\d{2}):(\d{2}).*$','$3-$2-$1T$4:$5:00')))then(
                let $update:= update replace $docReponse//util/extrait with <extrait>non</extrait> 
                let $dateQuestionnaire:= xs:dateTime(replace($dateReponse,'^(\d{2})-(\d{2})-(\d{4}).*(\d{2}):(\d{2}).*$','$3-$2-$1T$4:$5:00'))
                return <Questionnaire idModele="{$model}">
                        <Traitement>
                            <Resultat>
                                <Code>200</Code>
                                <Libelle>date réponse : {$dateQuestionnaire} extrait : {$docReponse//util/extrait}</Libelle>
                            </Resultat>
                        </Traitement>
                        <Identifiant>{substring-before($reponse,'.xml')}</Identifiant> 
                    </Questionnaire>      
                )else()  )
                else()}
    </Questionnaires>
</Campagne>
};




(:~
 : TODO stream le result ? pas possible => enregistrer le zip
 : TODO En fait on peut jsute prendre ceux qui sont là? est-ce qu'on a besoin de ceux qui sont suivis?
 :
 : requete le suivi d'une enquête, et compresse les pdfs trouvé (par défaut, seulement les envoyé et non extrait). Tous les pdf sont à la racine du zip. Ils sont supprimé après constitution du zip
 :
 : POST /extractions/pdf/{$enquete}
 : @param $all = true ou false (par défaut) : si true renvoie les pdfs de tous les questionnaire renvoyé par le suivi, si non ne compresse que ceux qui sont expédié et non encore extrait 
 : return 200 + l'url du zip ou 404 si pas de pdf à extraire.
 :)


declare
%rest:POST("{$content}")
%rest:path("/extractions/pdf/{$enquete}")
%rest:query-param("expedie", "{$expedie}","true")
%rest:query-param("vague99", "{$vague99}","false")
%rest:query-param("dateEnvoi", "{$dateEnvoi}","")
function extraction:extraction-enquete-pdf-ok($enquete as xs:string*,$expedie as xs:string*,$vague99 as xs:string*,$dateEnvoi as xs:string*,$content as node()*, $all as xs:string*) as item()*{
let $log := util:log("INFO", "extraction:extraction-enquete-pdf-ok[$enquete="||$enquete||", $expedie="||$expedie||", $vague99="||$vague99||", $dateEnvoi="||$dateEnvoi||", $content, $all="||$all||"]")
return 
let $tmp-enquete := xmldb:create-collection("/db/tmp",$enquete)
let $suivis := local:extraction-enquete-pdf-suivi($enquete,$expedie,$vague99,$dateEnvoi,$content,$tmp-enquete)
return if(not(empty(xmldb:get-child-resources($tmp-enquete))))then(
        let $zip-file := compression:zip(xs:anyURI($tmp-enquete),false())
        let $zip-name:=$enquete||"-"||format-dateTime(current-dateTime(),"[Y,4][M,2][D,2][H,2][m,2][s,2]")||".zip"
        let $sto := xmldb:store("/db/tmp",$zip-name,$zip-file,'application/zip')
        let $remove := xmldb:remove($tmp-enquete)
        return (common:rest-response(200,"zip des pdfs envoyés"),$sto)
    )else(
    let $remove := xmldb:remove($tmp-enquete)
    return(common:rest-response(404),$suivis))
       
};

declare function local:isFromVague99($vague99 as xs:string*,$content as node()*, $idUniteEnquetee as xs:string*){
if(xs:boolean($vague99))then(some $i in $content//Interrogation/@idUniteEnquetee satisfies $i=$idUniteEnquetee
                            )else(not(some $i in $content//Interrogation/@idUniteEnquetee satisfies $i=$idUniteEnquetee))
};

declare function local:dateEnvoiSup($dateEnvoi as xs:string*,$dateHeure as xs:string*){
if($dateEnvoi=""
    )then(true()
    )else(
        (datetime:parse-dateTime($dateEnvoi,"yyyyMMddHHmmss")-datetime:parse-dateTime($dateHeure,"dd-MM-yyyy à HH:mm"))
        div xs:dayTimeDuration('PT1M') 
        >0)
};


declare function local:extraction-enquete-pdf-suivi($enquete as xs:string*,$expedie as xs:string*,$vague99 as xs:string*,$dateEnvoi as xs:string*,$content as node()*,$tmp-enquete as xs:string) as item()*{
let $log := util:log("INFO", "extraction:extraction-enquete-suivi[$enquete="||$enquete||", $expedie="||$expedie||", $vague99="||$vague99||", $dateEnvoi="||$dateEnvoi||", $content, $tmp-enquete="||$tmp-enquete||"]")
let $suivisTout := extraction:getExpedie($enquete)
let $suivis:=$suivisTout//Suivi[
    extrait='non' and reponse='oui'
    and (if(xs:boolean($expedie))then(expedie='oui')else(true()))
    and (local:isFromVague99($vague99,$content,@idUniteEnquetee))
    and (local:dateEnvoiSup($dateEnvoi,dateHeure))
   ]
return 
    if( not(empty($suivis)))then(
        for $suivi in $suivis
        let $resource:= $enquete||"_"||$suivi/@idUniteEnquetee
        let $source:= '/db/orbeon/fr/'||$enquete||"/"||$suivi/@idModele||"/pdf/"       
        return 
            if(util:binary-doc-available($source||$resource||".pdf")
            )then(xmldb:move($source,$tmp-enquete,$resource||".pdf")
            )else(xmldb:store($tmp-enquete,$resource||".txt","le fichier pdf n'existe pas")) 
     )else() 
};


(:~
 : renvoie la liste des questionnaires qui reçu une réponse (fichier xml de réponse créé) et qui n'ont pas encore été extrait.
 : pour ceux qui ont été envoyé, envoie les réponses (dans <InformationsPersonnalisees>), formattée grâce à extraction:formattage-reponse
 :
 : TODO rajouter dateDerniereModif="{$docReponse//util/dateHeure}" ???
 :
 : POST /extractions/{$enquete}
 : @param expedie : si on se limite aux questionnaire expédié ou non
 : @param vague99 : si on se limite aux questionnaire de la vague 99 ou non (les identifiant de la vague 99 sont passé dans le content)
 :)

 
declare
%rest:POST("{$content}")
%rest:path("/extractions/{$enquete}")
%rest:query-param("expedie", "{$expedie}","true")
%rest:query-param("vague99", "{$vague99}","false")
%rest:query-param("dateEnvoi", "{$dateEnvoi}","")
function extraction:extraction-enquete ( $enquete as xs:string*,$expedie as xs:string*,$vague99 as xs:string*,$dateEnvoi as xs:string*,$content as node()*) as node()* 
{
let $log := util:log("INFO", "extraction:extraction-enquete[$enquete="||$enquete||", $expedie="||$expedie||", $vague99="||$vague99||", $dateEnvoi="||$dateEnvoi||"]")  
let $racine-enquete := '/db/orbeon/fr/'||$enquete
let $enqueteToken := tokenize(upper-case($enquete),'-')
return try{
let $campagne:=
<Campagne  idSource="{$enqueteToken[1]}" millesime="{$enqueteToken[2]}" idPeriode="{$enqueteToken[3]}">
    <Traitement><Type>ExtractionReponses</Type></Traitement>
    <Questionnaires>
    {
    for $model in xmldb:get-child-collections($racine-enquete)
        let $collection-model := concat($racine-enquete,'/',$model,'/data/')
        return                
        if(xmldb:collection-available($collection-model))
        then(
        for $hash-reponse in xmldb:get-child-collections($collection-model)[.!='init']         
            let $collection-reponse := concat($collection-model,$hash-reponse)
            return            
            for $reponse in xmldb:get-child-resources($collection-reponse)[
                                local:isFromVague99($vague99,$content,substring-before(.,'.xml'))
                            ]       
            let $docReponse := doc($collection-reponse||'/'||$reponse)
            return
                if(not(xs:boolean($expedie)) and $docReponse//util/expedie = 'non')
                then(
                    let $dh:=if(fn:contains($docReponse//util/dateHeure/text(),'à'))then($docReponse//util/dateHeure/text())else(fn:format-dateTime(fn:current-dateTime(),'[D01]-[M01]-[Y0001] à [H01]:[m01]'))
                    let $a:=<Questionnaire idModele="{$model}" >
                        <InformationsGenerales>
                            <UniteEnquetee>
                                <Identifiant>{substring-before($reponse,'.xml')}</Identifiant>                  
                            </UniteEnquetee>                        
                        </InformationsGenerales>
                        <InformationsPersonnalisees>
                            <Variable idVariable="dateHeure" type="N/A">
                            <Valeur type="ancienne"/>
                            <Valeur type="nouvelle">{$dh}</Valeur></Variable>
                        </InformationsPersonnalisees>                    
                    </Questionnaire>    
                    return $a
                )else(
                    if($docReponse//util/expedie = 'oui' and $docReponse//util/extrait = 'non' and local:dateEnvoiSup($dateEnvoi,$docReponse//util/dateHeure))
                    then(
                        <Questionnaire idModele="{$model}" >
                             <InformationsGenerales>
                                 <UniteEnquetee>
                                     <Identifiant>{substring-before($reponse,'.xml')}</Identifiant>                  
                                 </UniteEnquetee>                        
                             </InformationsGenerales>
                            
                             <InformationsPersonnalisees>
                             { let $extrait := update replace $docReponse//util/extrait with <extrait>oui</extrait>
                             return local:formattage-reponse($docReponse/form,doc($collection-model||'init/'||$hash-reponse||'/'||$reponse)/form)}       
                             </InformationsPersonnalisees>                             
                         </Questionnaire>         
                    )else()
                )) else()
                                
    }
    </Questionnaires>
</Campagne>

let $m:=if(not(xs:boolean($expedie)))then("répondu(s) dont "|| count($campagne//Questionnaire[count(descendant::Variable)>1]))else()
let $message:=count($campagne//Questionnaire)||" questionnaire(s) " 
    || $m
    ||" extrait(s)"
return (common:rest-response(200,$message),$campagne)

}catch *{ common:rest-response(400,$err:code||" "||$err:description ) }  
};



declare function local:formattage-reponse($rawReponse as node()*, $init as node()*) as node()*
{
let $log := util:log("INFO", "extraction:extraction-enquete[$rawReponse, $init]")  

for $node in $rawReponse/*
return typeswitch($node)
    case element(Groupe) return
        <Groupe>{
            $node/@*,
            local:formattage-reponse($node,$init/Groupe[@idGroupe=$node/@idGroupe])
        }</Groupe>
     case element(Variable) return
        <Variable>{
         $node/@*,
         <Valeur type="ancienne">{$init//Variable[@idVariable=$node/@idVariable]/text()}</Valeur>,
         <Valeur type="nouvelle">{$node/text()}</Valeur>
        }</Variable>
    case element() return
            local:formattage-reponse($node,$init)
     default return ()
};





declare
%rest:GET
%rest:path("/extractions/suivi/{$enquete}")
function extraction:getExpedie($enquete as xs:string*) as item()*
{
let $log := util:log("INFO", "extraction:getExpedie[$enquete="||$enquete||"]") 
let $racine-enquete := '/db/orbeon/fr/'||$enquete||'/'
return try{
<Suivis idCampagne='{$enquete}'>
{    
for $model in xmldb:get-child-collections($racine-enquete)
        let $collection-model := $racine-enquete||$model||'/data/init/'
        let $collection-model-reponse := $racine-enquete||$model||'/data/'
        return                
            if(xmldb:collection-available($collection-model))then(
        for $hash-reponse in xmldb:get-child-collections($collection-model)         
            let $collection-init := concat($collection-model,$hash-reponse)
            return            
            for $init in xmldb:get-child-resources($collection-init)
                let $cheminReponse := $collection-model-reponse||$hash-reponse||'/'||$init
                let $reponseAvalaible := doc-available($cheminReponse)
                let $docReponse := if($reponseAvalaible) then(doc($collection-model-reponse||$hash-reponse||'/'||$init)) else('')            
                return
                    <Suivi idUniteEnquetee='{substring-before($init,'.xml')}' idModele='{$model}' hashRepertoire='{$hash-reponse}'>
                        <reponse>{if($reponseAvalaible) then('oui') else('non')}</reponse>
                        <expedie>{if($reponseAvalaible) then($docReponse//util/expedie/text()) else('non')}</expedie>
                        <dateHeure>{if($reponseAvalaible) then($docReponse//util/dateHeure/text()) else('non')}</dateHeure>
                        <extrait>{if($reponseAvalaible) then($docReponse//util/extrait/text()) else('non')}</extrait>                                                            
                    </Suivi>)
                    else()
}
</Suivis>
}catch *{ common:rest-response(400,$err:code||" "||$err:description ) }            
};


declare
%rest:GET
%rest:path("/collectes/suivi/{$enquete}/{$id-ue}")
function extraction:getExpedieUE($enquete as xs:string*,$id-ue as xs:string*) as node()*
{
let $log := util:log("INFO", "extraction:getExpedieUE[$enquete="||$enquete||", $id-ue="||$id-ue||"]") 
let $racine-enquete := concat('/db/orbeon/fr/',lower-case($enquete),'/')
let $model := model:get-model(lower-case($enquete),upper-case($id-ue))
let $col := common:calcol(upper-case($id-ue))
let $cheminReponse := concat($racine-enquete,$model,'/data/',$col,'/',upper-case($id-ue),'.xml') 
let $reponseAvalaible := doc-available($cheminReponse)
let $docReponse := if($reponseAvalaible) then(doc($cheminReponse)) else('')            
return
    <Suivi idUnite='{upper-case($id-ue)}' idModele='{$model}' hashRepertoire='{$col}'>
        <reponse>{if($reponseAvalaible) then('oui') else('non')}</reponse>
        <expedie>{if($reponseAvalaible) then($docReponse//util/expedie/text()) else('non')}</expedie>
        <dateHeure>{if($reponseAvalaible) then($docReponse//util/dateHeure/text()) else('non')}</dateHeure>
        <extrait>{if($reponseAvalaible) then($docReponse//util/extrait/text()) else('non')}</extrait>                                                            
    </Suivi>            
};

declare
%rest:GET
%rest:path("/extractions/suivi/campagnes")
function extraction:getCampagnes() as node()*
{
let $log := util:log("INFO", "extraction:getCampagnes")  
return
    <Campagnes>
    {
    for $campagne in xmldb:get-child-collections('/db/orbeon/fr/'||$enquete)
        let $suivi:=extraction:getExpedie($campagne)
    return     
        <Campagne   idCampagne="{$campagne}" 
                    totalResponse="{count($suivi//reponse[text()='oui'])}" 
                    totalExpedie="{count($suivi//expedie[text()='oui'])}" 
                    totalExtraction="{count($suivi//extrait[text()='oui'])}"/>
          }
    </Campagnes>
};
