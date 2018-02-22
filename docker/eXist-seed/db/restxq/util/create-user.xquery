xquery version "3.0";
import module namespace permission =  "http://www.insee.fr/coltrane/permission"  at "/db/restxq/util//permission.xqm";
let $r:=permission:create-user()
return $r