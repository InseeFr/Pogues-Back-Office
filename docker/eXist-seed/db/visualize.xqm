xquery version "3.1";

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
import module namespace common="http://www.insee.fr/collectes/commonstromae/common" at "/db/restxq/common/commonStromae.xqm";

(:************ Vizualise Questionnaire  *************:)

declare
%rest:GET
%rest:path("/visualize")
function pogues:get-visualize()  as item()+{
    let $mess:=<resultat><xqm>{'/visualize'}</xqm><message>{"It's work"}</message></resultat>
    return (common:rest-response(200),$mess)
};



(:************ Vizualise Questionnaire  *************:)

declare
  %rest:POST("{$body}")
  %rest:path("/visualize/{$dataCollection}/{$model}")
function pogues:post-publish ($body as node()*, $dataCollection as xs:string*, $model as xs:string*)    
{
let $dataCollection := lower-case($dataCollection)
let $orbeon-root := '/db/orbeon/fr'
let $dataCollection-root := concat($orbeon-root,'/',$dataCollection)
let $model-root := concat($dataCollection-root,'/',$model)
let $model-form-folder := concat($model-root,'/form')
let $model-data-folder := concat($model-root,'/data')
let $model-data-init-folder := concat($model-data-folder,'/init')
let $model-data-init-collection-folder := concat($model-data-init-folder,'/',$collection)

let $unite := xs:string('123456789')
let $collection := xs:string('15')

let $ip_orbeon := doc('/db/properties.xml')/properties/host_orbeon/ip/text()
let $port_orbeon := doc('/db/properties.xml')/properties/host_orbeon/port/text()
let $url := concat('http://',$ip_orbeon,':',$port_orbeon,'/rmesstromae/fr/',$dataCollection,'/', $model, '/new?unite-enquete=',$unite)

return 
( common:create-collection-if-necessary($dataCollection-root),
  common:create-collection-if-necessary($model-root),
  common:create-collection-if-necessary($model-form-folder),
  common:create-collection-if-necessary($model-data-folder),
  common:create-collection-if-necessary($model-data-init-folder),
  common:create-collection-if-necessary($model-data-init-collection-folder),
  xmldb:store($model-form-folder, 'form.xhtml', $body),
  xmldb:store($model-data-init-collection-folder, '123456789.xml', doc(concat($model-form-folder,'/form.xhtml'))//xf:instance[@id='fr-form-instance']/form),
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


