xquery version "3.0";
module namespace reponse="http://www.insee.fr/collectes/reponse";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";



declare namespace rest="http://exquery.org/ns/restxq";
declare variable $reponse:version := "20171206-OR" ;

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/reponse/helloworld'}</xqm><version>{$modelt:version}</version><message>{"Chuck Norris peut faire du feu en frottant deux glaçons."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/reponse/helloworld")
function reponse:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/reponse/helloworld'}</xqm><version>{$reponse:version}</version><message>{"Chuck Norris peut faire du feu en frottant deux glaçons."}</message></resultat>
    return (common:rest-response(200),$mess)
};


(:GET REPONSE:) 
declare
%rest:GET
%rest:path("/collectes/reponse/{$enquete}/{$modele}/{$unite}")
function reponse:get-reponse ( $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*) as node()* 
{
let $col := common:calcol($unite)
let $doc := concat('/db/orbeon/fr/',$enquete,'/',$modele,'/data/',$col,'/',$unite,'.xml')
return 
if (doc-available($doc)) then (doc($doc))
else (common:rest-response(404,"Fichier reponse introuvable"))
};

(:GET ALL REPONSE:)
declare
%rest:GET
%rest:path("/collectes/reponse/{$enquete}")
function reponse:get-all-reponse ( $enquete as xs:string*) as node()* 
{
let $racine-enquete-full := concat('/db/orbeon/fr/',$enquete,'/')
return
<r>{
for $collection-modele in xmldb:get-child-collections($racine-enquete-full)
let $collection-data :=
    if (xmldb:collection-available(concat($racine-enquete-full,$collection-modele,'/data')))
    then (concat($racine-enquete-full,$collection-modele,'/data/'))
    else ()
for $collection-data-nonvide in $collection-data[.!='']    
for $collection-unite in  xmldb:get-child-collections($collection-data-nonvide)[.!='init']
let $doc := concat($collection-data,$collection-unite)
for $nom-fichier in xmldb:get-child-resources($doc)
let $fichier := concat($doc,'/',$nom-fichier)
return doc($fichier)
}</r>
};

(:POST REPONSE:)
declare
%rest:POST("{$body}")
%rest:path("/collectes/reponse/{$enquete}/{$modele}/{$unite}")
%rest:query-param("ongletproof", "{$ongletproof}","youpi")

(:par defaut, le service enregistre; si on renseigne ongletproof avec oui, les expedie=oui ne pourront pas etre modifiés par le repondant :)

function reponse:post-reponse-reexpedition ($body as document-node()*, $enquete as xs:string*, $modele as xs:string*,$unite as xs:string*,$ongletproof as xs:string*) 
as node()* 
{
let $col := common:calcol($unite)
let $col-save := concat('/db/orbeon/fr/',$enquete,'/',$modele,'/data/',$col)
let $col-root := concat('/db/orbeon/fr/',$enquete,'/',$modele,'/data/')
let $col-saved := if(xmldb:collection-available($col-save)) then($col-save) else(xmldb:create-collection($col-root,$col))
let $doc-user := concat($col-save,'/',$unite,'.xml')
return  if (doc($doc-user)//expedie=$ongletproof)
                then(common:rest-response(404,"Sauvegarde a échoué. questionnaire déjà expédié"))
                else(
                let $save:=xmldb:store($col-save,concat($unite,'.xml'),$body)
                 return if (empty($save)) then (common:rest-response(500,"Sauvegarde a échoué"))
              else (common:rest-response(201,"Sauvegarde réussie"))              
    )
};


