xquery version "3.0";
module namespace init="http://www.insee.fr/collectes/init";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace rest="http://exquery.org/ns/restxq";
declare namespace output="http://www.w3.org/2010/xslt-xquery-serialization";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

(:TEST MARIE:)
declare
%rest:GET
%rest:path("/collectes/init/helloworld")
%rest:query-param("racine", "{$racine}","")
function init:helloworld($racine as xs:string*) as item()+{
    let $log := util:log("INFO", "helloworldinit - *****************************************************")     
    let $message := common:racine($racine)||' Hello World!nnnn'
    return (common:rest-response(202),
    <results>
        <message>{$message}</message>
    </results>)
};

(: fonctions utilitaires :)

(: créé la collection $collection dans la collection $racine si nécessaire, lui affecte les droits $rights (en "rwxrwxrwx") et retourne le chemin complet vers cette collection : $racine||"/"|| :)
declare function local:create-collection-with-rights($racine as xs:string*, $collection as xs:string*, $rights as xs:string*){
let $t := if(xmldb:collection-available($racine||'/'||$collection)) 
            then (
                 let $a:=  if(fn:ends-with($racine ,"/"))then($racine)else($racine||"/")||$collection 
                 return $a) 
            else(
                 let $a:=xmldb:create-collection($racine, $collection)
                 return $a)
let $sm:=sm:chmod(xs:anyURI($t), "rwxrwxr-x")
return $t
};
(:Check or create directories and collections:)
declare function local:check($enquete as xs:string*,$racine as xs:string*) {
   let $log := util:log("INFO", concat("check : create collection - enquete : ", $enquete,", racine : ",$racine))
   let $racine-enquete :=common:racine($racine) 
   let $enquete2:=fn:lower-case($enquete)   
   let $t := local:create-collection-with-rights($racine-enquete,$enquete2,"rwxrwxr-x")      
    let $log := util:log("INFO", "check : collection return : "|| $t)
    return $t
};

(:get path to a init file of a questionnaire:)
declare function local:get-chemin( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*)   {
    let $log := util:log("INFO", concat("local:get-chemin - enquete : ", $enquete,", modele : ",$modele,", unite : ",$unite,", racine : ",$racine ))
    let $racine-enquete :=common:racine($racine)     
  (:  let $enquete2:=fn:lower-case($enquete):)
    let $col := common:calcol($unite)
    let $doc := fn:lower-case(concat($racine-enquete,$enquete,'/',$modele,'/data/init/',$col,'/',$unite,'.xml'))
    let $log := util:log("INFO", concat("local:get-chemin",$doc))
    return $doc
};

(:~ 
 : Update an existing interrogation or store a new one. The interrogation XML is read from the request body.
:)
declare
    %rest:GET
    %rest:path("/collectes/init/check/{$enquete}")    
    %rest:query-param("racine", "{$racine}","")
function init:check($enquete as xs:string*,$racine as xs:string*) as item()+ {
    let $log := util:log("INFO", concat("init:check - $enquete : ", $enquete,", racine : ",$racine ))    
    let $t := local:check($enquete,$racine)
    let $codeRetour := if($t="") then(500) else(200) (:417 Expectation Failed :)
    return (common:rest-response($codeRetour), $t)
}; 
(: get questionnaire :)
declare
    %rest:GET
    %rest:path("/collectes/init/{$enquete}/{$modele}/{$unite}")
    %rest:query-param("racine", "{$racine}","")
function init:get-init ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*) as item()+  {
    let $log := util:log("INFO", concat("init:get-init - enquete : ", $enquete,", modele : ",$modele,", unite : ",$unite,", racine : ",$racine ))
    let $doc := local:get-chemin($enquete,$modele,$unite,$racine)

return  
    if (doc-available($doc)) then (common:rest-response(200),doc($doc))
    else (common:rest-response(404))
};
(: delete questionnaire :)
declare
    %rest:DELETE
    %rest:path("/collectes/init/{$enquete}/{$modele}/{$unite}")
    %rest:query-param("racine", "{$racine}","")
function init:delete-init ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*) as item()+  {
    let $log := util:log("INFO", concat("init:delete-init - enquete : ", $enquete,", modele : ",$modele,", unite : ",$unite,", racine : ",$racine ))
    let $doc := local:get-chemin($enquete,$modele,$unite,$racine)
    let $t := xmldb:remove($racine-enquete||$enquete||'/'||$modele||'/data/init/'||$col, $unite||'.xml')
return  (common:rest-response(202),$t)
    (:if (doc-available($doc)) then (
    xmldb:remove
    common:rest-response(200),doc($doc))
    else (common:rest-response(404)):)
};
(:~ 
 : Update an existing interrogation or store a new one. The interrogation XML is read from the request body.
:)
declare
    %rest:PUT("{$content}")
    %rest:path("/collectes/init/{$enquete}")
    %rest:query-param("racine", "{$racine}","")
function init:initForm($enquete as xs:string*,$content as node()*,$racine as xs:string*) as item()+ {
    let $log := util:log("INFO", "initForm - enquete : "|| $enquete||", content, racine : "||$racine)
    let $idModele := fn:lower-case($content/Questionnaire/@idModel[1])
    let $idQuestionnaire := $content/Questionnaire/InformationsGenerales/Organisation/Identifiant/text()

    let $log := util:log("INFO", "initForm - modele : "|| $idModele||", idQuestionnaire : "||$idQuestionnaire)    
   
    let $chemin-enquete := local:check($enquete,$racine)       
    let $col := common:calcol($idQuestionnaire)
    let $t0 := local:create-collection-with-rights($chemin-enquete,$idModele,"rwxrwxr-x")  
    let $t1 := local:create-collection-with-rights($t0,"data","rwxrwxr-x")      
    let $t2 := local:create-collection-with-rights($t1,"init","rwxrwxr-x")      
    let $t  := local:create-collection-with-rights($t2,$col,"rwxrwxr-x")  
      
    let $log := util:log("INFO", "initForm - collection d'init : "|| $t)    
    let $cheminFinal := (
           (: si existe déjà : on écrase. if(doc-available($t||'/'||$idQuestionnaire||".xml")) then (
                409 (\: conflit :\)
           ) else( :)
                let $xform :=if(doc-available($chemin-enquete||"/"||$idModele||"/form/form.xhtml")) then($chemin-enquete||"/"||$idModele||"/form/form.xhtml")else("formDefault.xhtml")
                let $xmlMapping :=if(doc-available($chemin-enquete||"/mapping.xml")) then($chemin-enquete||"/mapping.xml")else("mappingDefault.xml")
                   
                let $log:= util:log("INFO", "initForm - $xform :"||$xform) 
                let $log:= util:log("INFO", "initForm - $xmlMapping :"||$xmlMapping) 
                let $params :=   <parameters>
                                    <param name="mappingFileURI" value="{$xmlMapping}"/>
                                    <param name="formFileURI" value="{$xform}"/>
                                 </parameters>
                let $log:= util:log("INFO", "initForm - param transfo xslt :"||$params) 
                
                (:let $contentTransfo:=transform:transform($content, doc('/db/orbeon/restxq/init/mappingInitForm.xsl'), $params):)
                let $transform:= doc('mappingInitForm.xsl')
                let $contentTransfo:=transform:transform($content,$transform, $params)
                (: transfo avec instance xforms  et xml contenant un mapping, ce dernier intégré au questionnaire 
                http://en.wikibooks.org/wiki/XQuery/XQuery_and_XSLT :
                transform:transform($input as node()?, $stylesheet as item(), $params as node()?) node()?
                $input is the node tree to be transformed
                $stylesheet  is either a URI or a node to be transformed.  If it is an URI, it can either point to an external 
                location or to an XSL stored in the db by using the 'xmldb:' scheme.
                $params are the optional XSLT name/value parameters with the following structure:
                <parameters> 
                   <param name="param-name1" value="param-value1"/>
                </parameters>
                :) 
                 let $t:=xmldb:store($t, $idQuestionnaire||".xml",$contentTransfo)                
                let $sm:=sm:chmod(xs:anyURI($t), "rwxrwxr-x")
                let $log := util:log("INFO", concat("initForm - store chemin : ",$t ))
                return $t
           )  
   let $codeRetour := if($cheminFinal="") then(500) else(201)  (:417 Expectation Failed - 500 plutôt pour erreur interne webservice :)
                  
   let $log := util:log("INFO", concat("initForm - codeRetour : ", $codeRetour,", chemin : ",$cheminFinal ))
   return (common:rest-response($codeRetour), $cheminFinal)
}; 