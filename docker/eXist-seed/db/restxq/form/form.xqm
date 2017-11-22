xquery version "3.0";
module namespace form="http://www.insee.fr/collectes/form";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace xhtml="http://www.w3.org/1999/xhtml";
(:TEST MARIE:)  

declare
%rest:GET
%rest:path("/collectes/form/helloworld")
%rest:query-param("racine", "{$racine}","")
function form:helloworld($racine as xs:string*) as item()+{
    let $log := util:log("INFO", "helloworldinit - *****************************************************")     
    let $message := common:racine($racine)||' Hello World!nnnn'
    return (common:rest-response(202),
    <results>
        <message>{$message}</message>
    </results>)
}; 
(:INSTANCE POUR QUESTIONNAIRE:)
declare
%rest:GET
%rest:path("/collectes/form/{$enquete}/{$modele}/{$unite}")
%rest:query-param("racine", "{$racine}","")
function form:get-instance ($enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*) as item()* 
{
(:let $racine-enquete := '/db/testCei/fr':) 
let $col := common:calcol($unite)
let $racine :=common:racine($racine)
let $doc-user := concat( $racine,'/',$enquete,'/',$modele,'/data/',$col,'/',$unite,'.xml')
let $doc-prerempli := concat( $racine,'/',$enquete,'/',$modele,'/data/init/',$col,'/',$unite,'.xml')
    (:important de renvoyer un élément vide (0 descendant) pour l'affichage du formulaire (cf le form.xhtml instance('control'):)
    
    let $head:=<headers/>(:common:header-restxq-orbeon():)
    let $uriBase:=common:getProperty("stromae.uri.base")
return 
    if (not(doc-available($doc-prerempli))) 
    then (<vide/>)
    else (    
        if (doc-available($doc-user)) then (common:getHttpRestOrbeon($doc-user)//form            
        )else (common:getHttpRestOrbeon($doc-prerempli)//form)       
      )
};

(:Recuperation du formulaire:)
declare
%rest:GET
%rest:path("/collectes/formulaire/{$enquete}/{$modele}")
%rest:query-param("racine", "{$racine}","")
function form:get-instance ($enquete as xs:string*, $modele as xs:string*,$racine as xs:string*) as node()* 
{
(:let $racine-enquete := '/db/testCei/fr':) 
let $racine :=common:racine($racine)
let $formulaire := concat( $racine,'/',$enquete,'/',$modele,'/form/form.xhtml')
    (:important de renvoyer un élément vide (0 descendant) pour l'affichage du formulaire (cf le form.xhtml instance('control'):)
return 
    if (not(doc-available($formulaire))) 
    then (<vide/>)
    else common:getHttpRestOrbeon($formulaire)//xhtml:html 
   (: doc($formulaire):)
};