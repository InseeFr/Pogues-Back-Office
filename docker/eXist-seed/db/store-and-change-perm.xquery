xquery version "3.0";
 
(: étape 1 - Déclaration d'une fonction récursive de stockage des fichiers de manière unitaire pour qu'ils soient pris en compte par eXist comme un service RestXQ :)

declare function local:store-again-collections($col as xs:string*){
    let $subcollection :=
        for $subcol in xmldb:get-child-collections($col)
        return local:store-again-collections($col||"/"||$subcol)
    let $files:=
        for $file in xmldb:get-child-resources($col)[ends-with(., ".xqm")]
        let $contents:=util:binary-doc($col||"/"||$file)
        let $s:=xmldb:store($col, $file, $contents)
        return $file
    return <col>{$subcollection}{$files}</col>
};

declare function local:store-again-file($dir as xs:string*, $file as xs:string*){
    let $contents:=util:binary-doc($dir||"/"||$file)
    let $s:=xmldb:store($dir, $file, $contents)
    return <file>{$file}</file>
};

(: étape 2 - Déclaration de deux fonctions de modification de droits change-permission et change-permission-element, l'une pour parcourir l'autre poiur changer unitairement les élements :)

declare function local:change-permission($dir as xs:string*,$userDefault as xs:string*,$groupDefault as xs:string*) {
    let $ici:=local:change-permission-element($dir, $userDefault, $groupDefault)
    let $subdir :=  if(xmldb:collection-available($dir) and $ici/@modifiable=true()) then (
            for $child in xmldb:get-child-collections($dir)
            return local:change-permission($dir||"/"||$child, $userDefault, $groupDefault)
        ) else()
    let $subrec := if(xmldb:collection-available($dir) and $ici/@modifiable=true())then (
            for $child in xmldb:get-child-resources($dir)
            return(local:change-permission-element($dir||"/"||$child, $userDefault, $groupDefault))  
        ) else()
    return(<dir id="{$dir}" owner="{$ici/@owner}" group="{$ici/@group}" mode="{$ici/@mode}"  modifiable="{$ici/@modifiable}">{$subrec}{$subdir}</dir>)
};
declare function local:change-permission-element($element as xs:string,$userDefault as xs:string,$groupDefault as xs:string) {
    let $modifiable := if(starts-with($element, "/db/system") or starts-with($element, "/db/apps"))then(
            false()
        )else(
            let $temp := sm:chown(xs:anyURI($element), $userDefault)
            let $temp := sm:chgrp(xs:anyURI($element), $groupDefault)
            let $temp :=sm:chmod(xs:anyURI($element), 'rwxrwxr-x') 
                
            (: si on veut des permisson plus spécifique
                let $temp := if ((contains($element, '.xqm')) or (contains($element, '.xquery'))) 
                 then sm:chmod(xs:anyURI($element), 'rwxrwxr-x') (\:on est obligé de mettre les x pour lister les collections:\)
                 else sm:chmod(xs:anyURI($element), 'rwxrwxr-x'):)
            return true()
        )
    let $perm := sm:get-permissions(xs:anyURI($element))
    let $retour :=  <element id="{$element}" owner="{$perm/sm:permission/@owner}" group="{$perm/sm:permission/@group}" mode="{$perm/sm:permission/@mode}" modifiable="{$modifiable}"/>
    return ($retour)
};

(: étape 3 - Appel de la méthode store-again sur la collection /db/services/ et le fichier visualize.xqm et stockage dans la variable restore :)

let $restore:= local:store-again-collections("/db/services")
let $restore2:= local:store-again-file("/db","visualize.xqm")

(: étape 4 - Appel de la méthode change permission sur la collection /orbeon, le user_stromae du groupe Appli en devient le propriétaire  :)

let $resperm1:= local:change-permission("/db/orbeon/fr","guest","guest")
let $resperm2:= local:change-permission-element("/db/orbeon","user_stromae","user_stromae")
let $resperm3:= local:change-permission-element("/db/orbeon/fr","user_stromae","user_stromae")

(:  étape 5 - retourne un résultat de type  :)
let $finaleRes := <log><store-again>{$restore}{$restore2}</store-again><permission>{$resperm1}{$resperm2}{$resperm3}</permission></log> 
return $finaleRes


