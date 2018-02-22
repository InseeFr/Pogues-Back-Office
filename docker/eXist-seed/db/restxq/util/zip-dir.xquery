xquery version "3.0";
(:~
: utilisé par le batch getrestxq : sert à zipper tout le répertoire restxq, qui est ensuite récupéré par le batch
:)
let $tmp := if(xmldb:collection-available("/db/tmp"))then("/db/tmp")else(xmldb:create-collection("/db","tmp"))
let $dir := request:get-parameter("dir","/db/restxq")
let $zip-file := compression:zip(xs:anyURI($dir),true())
let $zip-name:="resxq-stromae-"||format-dateTime(current-dateTime(),"[Y,4][M,2][D,2][H,2][m,2][s,2]")||".zip"
let $sto := xmldb:store($tmp,$zip-name,$zip-file,'application/zip')
return $sto