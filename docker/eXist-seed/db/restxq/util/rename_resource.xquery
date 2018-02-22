xquery version "3.1";
let $chemin_old:=request:get-parameter("chemin_old","")
let $nom_new:=request:get-parameter("nom_new","")
let $a:=xmldb:rename($chemin_old,$nom_new)
    return $a