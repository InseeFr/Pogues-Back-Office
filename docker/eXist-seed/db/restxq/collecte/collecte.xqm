xquery version "3.0";
module namespace collecte="http://www.insee.fr/coltrane/collecte";
import module namespace common="http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
import module namespace model="http://www.insee.fr/collectes/model" at "../model/model.xqm";
import module namespace form="http://www.insee.fr/collectes/form" at "../form/form.xqm";
declare namespace xhtml="http://www.w3.org/1999/xhtml";

declare variable $collecte:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/collecte/helloworld'}</xqm><version>{$contact:version}</version><message>{"Chuck Norris ne ment jamais, c'est la vérité qui se trompe."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/collecte/helloworld")
function collecte:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/collecte/helloworld'}</xqm><version>{$collecte:version}</version><message>{"Chuck Norris ne ment jamais, c'est la vérité qui se trompe."}</message></resultat>
    return (common:rest-response(200),$mess)
};

(:Recuperation du formulaire et de l'instance de personnalisation:) 
declare
%rest:GET
%rest:path("/collecte/formulaire/personnalise/{$campagne}/{$unite}")
function collecte:get-formulaire-personnalise ($campagne as xs:string*, $unite as xs:string*) as node()* 
{
let $racine := ''
let $modele := model:get-model($campagne, $unite)
let $instance := form:get-instance($campagne, $modele/text(), $unite)
let $formulaire := form:get-instance($campagne, $modele/text(), $racine)
return 
    <formulaire-perso>
        <formulaire>{$formulaire}</formulaire>
        <instance>{$instance}</instance>
    </formulaire-perso>
};




declare
%rest:GET
%rest:path("/collecte/etatQuestionnaire/{$enquete}/{$id-ue}")
function collecte:etatQuestionnaire($enquete as xs:string*, $id-ue as xs:string*) as node()*{
let $racine-enquete := concat('/db/orbeon/fr/',lower-case($enquete),'/')
let $model := model:get-model($enquete,upper-case($id-ue))
let $col := common:calcol(upper-case($id-ue))
let $cheminReponse := concat($racine-enquete,$model,'/data/',$col,'/',upper-case($id-ue),'.xml') 
let $cheminInit := concat($racine-enquete,$model,'/data/init/',$col,'/',upper-case($id-ue),'.xml') 
let $etat:=if(doc-available($cheminInit))
            then(
                if(doc-available($cheminReponse)) 
                then(
                    if(doc($cheminReponse)/form/stromae/util/expedie/text()='oui') 
                    then("1") 
                    else("0")
                    )
                else("0")
                )
            else("2")
let $date:=if($etat="1") then(let $a:=tokenize(doc($cheminReponse)/form/stromae/util/dateHeure/text()," ")[1] return tokenize($a,"-")[3]||tokenize($a,"-")[2]||tokenize($a,"-")[1]) else()
return <EtatQuestionnaire>
    <EtatExpedition>{$etat}</EtatExpedition>
    <DateExpedition>{$date}</DateExpedition>
    </EtatQuestionnaire>                                               
};





