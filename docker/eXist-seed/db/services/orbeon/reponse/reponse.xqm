xquery version "3.0";
module namespace reponse="http://www.insee.fr/collectes/reponse";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace rest="http://exquery.org/ns/restxq";


(:GET REPONSE:)
declare
%rest:GET
%rest:path("/collectes/reponse/{$enquete}/{$modele}/{$unite}")
%rest:query-param("racine", "{$racine}","")
function reponse:get-reponse ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*) as node()* 
{
let $col := common:calcol($unite)
let $doc := concat(common:racine($racine),'/',$enquete,'/',$modele,'/data/',$col,'/',$unite,'.xml')
return 
if (doc-available($doc)) then (doc($doc))
else (common:rest-response(404,"Fichier reponse introuvable"))
};


(:POST REPONSE:)
declare
%rest:POST("{$body}")
%rest:path("/collectes/reponse/{$enquete}/{$modele}/{$unite}")
%rest:query-param("racine", "{$racine}","")
function reponse:post-reponse ($body as document-node()*, $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$racine as xs:string*) 
as node()* 
{
(:let $racine-enquete := '/db/testCei/fr' :)
let $col := common:calcol($unite)
let $racine-enquete:=common:racine($racine)
let $col-save := concat($racine-enquete,'/',$enquete,'/',$modele,'/data/',$col)
let $col-root := concat($racine-enquete,'/',$enquete,'/',$modele,'/data/')
let $col-saved := if(xmldb:collection-available($col-save)) then(concat($racine-enquete,'/',$enquete,'/',$modele,'/data/',$col)) else(xmldb:create-collection($col-root,$col))
let $doc-user := concat($col-save,'/',$unite,'.xml')
return  
    if (xmldb:collection-available($col-save)) 
    then (
        let $save := 
            if (doc-available($doc-user)) then (
                let $del := xmldb:remove($col-save,concat($unite,'.xml'))
                return xmldb:store($col-save,concat($unite,'.xml'),$body)
             )
             else xmldb:store($col-save,concat($unite,'.xml'),$body)			 
        return
            if (empty($save)) then (common:rest-response(500,"Sauvegarde a échoué"))
              else (
				let $droit := sm:chown($doc-user,'guest')		
				let $droit := sm:chgrp($doc-user,'guest')		
				let $droit := sm:chown($col-save,'guest')		
				let $droit := sm:chgrp($col-save,'guest')		
				return common:rest-response(201,"Sauvegarde réussie")
			  )              
    )
    else (common:rest-response(404,"Sauvegarde a échoué. Collection n'existe pas"))
};

