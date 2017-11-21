xquery version "3.0";
module namespace collecte="http://www.insee.fr/coltrane/collecte";
import module namespace common="http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
import module namespace model="http://www.insee.fr/collectes/model" at "../model/model.xqm";
import module namespace form="http://www.insee.fr/collectes/form" at "../form/form.xqm";
declare namespace xhtml="http://www.w3.org/1999/xhtml";
(:TEST MARIE:)
declare
%rest:GET
%rest:path("/collecte/formulaire/helloworld")
%rest:query-param("racine", "{$racine-enquete}","")
function collecte:helloworld($racine-enquete as xs:string*) as item()+{
    let $log := util:log("INFO", "collecte:helloworld - racine : "||$racine-enquete||"*****************************************************")     
    let $message := common:racine($racine-enquete) || 'Hello World!'
    return(
        common:rest-response(202),
    <results>
        <message>{$message}</message>
    </results>)
}; 
 
(:Recuperation du formulaire et de l'instance de personnalisation:) 
declare
%rest:GET
%rest:path("/collecte/formulaire/personnalise/{$campagne}/{$unite}")
function collecte:get-formulaire-personnalise ($campagne as xs:string*, $unite as xs:string*) as node()* 
{
let $racine := ''
let $modele := model:get-model($campagne, $unite, $racine)
let $instance := form:get-instance($campagne, $modele/text(), $unite, $racine)
let $formulaire := form:get-instance($campagne, $modele/text(), $racine)
return 
    (:<bim>{$modele/text()}</bim>:)
   
    <formulaire-perso>
        <formulaire>{$formulaire}</formulaire>
        <instance>{$instance}</instance>
    </formulaire-perso>
};