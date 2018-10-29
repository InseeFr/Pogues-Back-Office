xquery version "3.0";

module namespace pogues="http://xml.insee.fr/schema/applis/pogues";
declare namespace pogmod = "http://id.insee.fr/apps/pogues-model";
declare namespace request = "http://exist-db.org/xquery/request";
declare namespace xmldb = "http://exist-db.org/xquery/xmldb";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare namespace rest="http://exquery.org/ns/restxq";
declare namespace functx = "http://www.functx.com";
declare namespace compression = "http://exist-db.org/xquery/compression";
declare namespace ngram = "http://exist-db.org/xquery/ngram";
declare namespace xf="http://www.w3.org/2002/xforms";
declare namespace xhtml="http://www.w3.org/1999/xhtml";
declare namespace http="http://expath.org/ns/http-client";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "xmldb:exist:///db/services/orbeon/common/commonStromae.xqm";

(:************ Vizualise Questionnaire  *************:)


declare
 %rest:GET
 %rest:path("/visualize")
 %rest:produces("text/xml")
function pogues:get-visualize ()   
{
  let $retour := 'toto'
  return 
(     
<rest:response>
    <http:response status="200">    
    	<http:header name="Access-Control-Allow-Headers" value="Location, Content-Type" />
    	<http:header name="Access-Control-Allow-Origin" value="*" />
    </http:response>
</rest:response>,  
    $retour
  )
}
;


(:************ Vizualise Questionnaire  *************:)

declare
  %rest:POST("{$body}")
  %rest:path("/visualize/{$dataCollection}/{$model}")
function pogues:post-publish ($body as node()*, $dataCollection as xs:string*, $model as xs:string*)    
{
let $dataCollection := lower-case($dataCollection)
let $orbeon-root := '/db/orbeon/fr'
let $a := common:collection($orbeon-root,$dataCollection)
let $dataCollection-root := concat($orbeon-root,'/',$dataCollection)
let $b := common:collection($dataCollection-root,$model)
let $model-root := concat($dataCollection-root,'/',$model)
let $c := common:collection($model-root,'form')
let $d := common:collection($model-root,'data')
let $model-form-folder := concat($model-root,'/form')
let $model-data-folder := concat($model-root,'/data')
let $e := common:collection($model-data-folder,'init')
let $model-data-init-folder := concat($model-data-folder,'/init')

let $form := xmldb:store($model-form-folder, 'form.xhtml', $body)

let $unite := xs:string('123456789')
let $collection := xs:string('15')
let $f := common:collection($model-data-init-folder,$collection)
let $model-data-init-collection-folder := concat($model-data-init-folder,'/',$collection)

let $perso := xmldb:store($model-data-init-collection-folder, '123456789.xml', doc(concat($model-form-folder,'/form.xhtml'))//xf:instance[@id='fr-form-instance']/form)

let $ip_orbeon := doc('/db/properties.xml')/properties/host_orbeon/ip/text()
let $port_orbeon := doc('/db/properties.xml')/properties/host_orbeon/port/text()
let $url := concat('http://',$ip_orbeon,':',$port_orbeon,'/rmesstromae/fr/',$dataCollection,'/', $model, '/new?unite-enquete=',$unite)

return 
(     
<rest:response>
    <http:response status="200">    
    	<http:header name="Access-Control-Allow-Headers" value="Location, Content-Type" />
    	<http:header name="Access-Control-Allow-Origin" value="*" />
    </http:response>
</rest:response>,  
    $url
  )
}
;


