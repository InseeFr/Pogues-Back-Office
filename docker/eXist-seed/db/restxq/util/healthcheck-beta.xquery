xquery version "3.0";
import module namespace http = "http://expath.org/ns/http-client";
declare namespace rest="http://exquery.org/ns/restxq";


declare function local:getProperty($key as xs:string*) as xs:string* {
   doc("../properties.xml")/properties/entry[@key=$key]/text()
};

declare function local:send-requests() as node()*{
    let $results:=for $function in doc('healthcheck-data.xml')//Helloworld
        let $uri:=local:getProperty("stromae.uri.base")||'exist/restxq'||$function
        let $response := httpclient:get($uri, true(), <headers/>)
        let $result-statut := if (contains($response//xqm,'helloworld')) then 'ok' else 'ko'
        return <result statut="{$result-statut}">
             <uri>{$uri}</uri>
                {$response//resultat}         
            </result>

    return <results nbOk="{count($results[@statut='ok'])}" nbKo="{count($results[@statut='ko'])}">
    <KO>{$results[@statut='ko']/uri}</KO><InfoTestOk>{$results[@statut='ok']/resultat/xqm}</InfoTestOk>
    </results>

};

let $test := local:send-requests()
return $test
