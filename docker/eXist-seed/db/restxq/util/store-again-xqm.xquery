xquery version "3.0";
declare function local:store-again($col as xs:string*){
    let $subcollection :=
        for $subcol in xmldb:get-child-collections($col)
        return local:store-again($col||"/"||$subcol)
    let $files:=
        for $file in xmldb:get-child-resources($col)[ends-with(., ".xqm")]
        let $contents:=util:binary-doc($col||"/"||$file)
        let $s:=xmldb:store($col, $file, $contents)
        return $file
    return <col>{$subcollection}{$files}</col>
};

let $restore:= local:store-again("/db/restxq")
return util:eval(xs:anyURI("/db/restxq/util/healthcheck-beta.xquery"))
