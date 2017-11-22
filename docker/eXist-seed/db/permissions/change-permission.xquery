xquery version "3.0";
import module namespace permission =  "http://www.insee.fr/coltrane/permission"  at "./permission.xqm";
let $dir:=request:get-parameter("dir","/db/restxq")
return permission:change-permission($dir)