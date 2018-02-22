xquery version "3.0";
import module namespace dbutil="http://exist-db.org/xquery/dbutil"
at "xmldb:exist:///db/apps/shared-resources/content/dbutils.xql";

declare function local:comptage($user as xs:string,$owners as xs:string*) as xs:string{
    let $listowner :=
    for $owner in  $owners
    where $owner=$user
    return $owner
    return string(count($listowner))

};

let $owners := dbutil:scan-collections(xs:anyURI("/db/orbeon"),function($collection){
                dbutil:scan-resources(xs:anyURI($collection),function($resource){
                    if (xmldb:get-mime-type($resource)="application/xml")
                    then xmldb:get-owner(util:collection-name(xs:string($resource)),util:document-name(xs:string($resource)))
                    else ()
                    })
                })

return 

element {QName("http://www.w3.org/1999/xhtml", "html")} {
    <head><title>userboard</title></head>,
    <body><h1>comptage fichiers</h1>
        <p>nombre de fichiers par utilisateur</p>
        <table border="2">
            <tr><th>user</th><th>primary-group</th><th>nb fichier</th></tr>
            {
                for $user in sm:list-users()
                return <tr><td valign="top">{ $user }</td><td>{sm:get-user-primary-group($user)}</td><td>{local:comptage($user,$owners)}</td></tr>
            }
        </table>
    </body>
}