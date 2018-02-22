xquery version "3.0";  
import module namespace usermanager="http://exist-db.org/apps/dashboard/userManager"
at "/db/apps/dashboard/plugins/userManager/userManager.xqm";   

for $user in usermanager:list-users()//user
return string($user || ":" || sm:get-user-primary-group($user) || ";")   

