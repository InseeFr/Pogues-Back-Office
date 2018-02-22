xquery version "3.0";
module namespace common="http://www.insee.fr/collectes/commonstromae/common";


declare namespace functx = "http://www.functx.com";
declare namespace http="http://expath.org/ns/http-client";

declare variable $common:version := "20171206-OR";
(:~
 : HELLOWORLD
 :
 : GET /common/helloworld
 :
 : @return 200 + <resultat><xqm>{'/common/helloworld'}</xqm><version>{$common:version}</version><message>{"Chuck Norris a piraté le Pentagone. Avec un grille-pain."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/common/helloworld")
function common:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/common/helloworld'}</xqm><version>{$common:version}</version><message>{"Chuck Norris a piraté le Pentagone. Avec un grille-pain."}</message></resultat>
    return (common:rest-response(200),$mess)
};


declare function common:rest-response($status as xs:int*) as node()* {
   let $niveauLog := if($status>=200 and $status <300)then "INFO" else "ERROR"
    let $log := util:log($niveauLog, concat("rest-response - $status : ", $status)) 
    let $restResponse :=  
            <rest:response>
               <http:response status="{$status}">
                   <http:header name="Content-Type" value="application/xml; charset=utf-8"/>
               </http:response>
           </rest:response>
    return $restResponse
};
 
declare function common:rest-response($status as xs:int*, $message as xs:string*) as node()* {
   let $niveauLog := if($status>=200 and $status <300)then "INFO" else "ERROR"
    let $log := util:log($niveauLog, concat("rest-response - $status : ", $status)) 
    let $restResponse :=  
            <rest:response>
               <http:response status="{$status}" reason="{$message}">
                   <http:header name="Content-Type" value="application/xml; charset=utf-8"/>
               </http:response>
           </rest:response>
    return $restResponse
};


declare function common:create-collection-if-necessary($chemin as xs:string*) as xs:string*{
if (xmldb:collection-available($chemin)) then (
(: Ne fait rien si la collection concernée existe :)$chemin
) else (
    (:Découpage de l'url envoyée en $first (tous les caractères avant le dernier / de l'url envoyée) et $last (les caractères situés après le dernier / de l'url envoyée):)
    let $first := replace($chemin,'^(.*)/.*','$1')
    let $last := replace ($chemin,'^.*/','')
    (: Vérification de l'existence ou non de la collection mère, en relançant la méthode de manière récursive :)
    let $f := common:create-collection-if-necessary($first)
    let $a:=xmldb:create-collection($f, $last)
    return $f||"/"||$last
)
};

(: fonction pour calculer le répertoire dans lequel est le questionnaire :)
declare function common:calcol ($arg as xs:string?) as xs:integer* {
    let $NOMBRE-COLLECTIONS := 30
    let $LETTRES := 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
    let $spli := functx:chars(translate($arg,$LETTRES,'0000000000000000000000000000000000000000000000000000'))
    let $tot := for $s in $spli return xs:integer($s)
    let $col := sum($tot) mod $NOMBRE-COLLECTIONS
    return $col
};

declare function functx:chars ($arg as xs:string?) as xs:string* {
    for $ch in string-to-codepoints($arg)
    return codepoints-to-string($ch)
};