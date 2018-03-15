xquery version "3.0";
(:~
 : module qui permet d'affecter des droits 'rwxrwxr-x' au répertoire/à la collection, pour l'utilisateur et le groupe souhaités ou ceux par défaut que l'ont trouve dans le fichier "/db/restxq/properties.xml" pour la base de stromae. 
 :)
module namespace permission = "http://www.insee.fr/coltrane/permission";

declare variable $permission:uriProperties :="/db/restxq/properties.xml";
declare variable $permission:currentBase :="stromae";

declare variable $permission:properties:=doc($permission:uriProperties)//entry[starts-with(@key, $permission:currentBase)];
declare variable $permission:defaultGroup:=$permission:properties[@key=$permission:currentBase||".group"]/text();
declare variable $permission:defaultUser:=$permission:properties[@key=$permission:currentBase||".user.default"]/text();

declare variable $permission:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /permission/helloworld
 :
 : @return 200 + <resultat><xqm>{'/permission/helloworld'}</xqm><version>{$verif:version}</version><message>{"Un jour, Chuck Norris a fait la blague « j'ai volé ton nez » à Mickael Jackson."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/permission/helloworld")
function permission:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/permission/helloworld'}</xqm><version>{$permission:version}</version><message>{"Un jour, Chuck Norris a fait la blague « j'ai volé ton nez » à Mickael Jackson."}</message></resultat>
    return ($mess)
};

(::::::::::::::::::::::::DROITS:::::::::::::::::::::::::::::::)
(:~
 : on crée les users et le group, on ajoute/modifie le group comme primary group aux user,
 :
 : on set les drtoi par défaut des en lecture + écriture pour le groupe (juste lecture au reste du monde). 
 :
 : on enlève les mots de passe du fichier propriété
 :
 : TODO Impossible de mettre les droit en exécution par défaut. Pourquoi ? => quand on livre des restxq, faire appel aux modofs de permissions d'ici, sinon c'est bon
 :) 
declare function permission:create-user($properties,$currentBase) {
    let $log:=util:log("INFO", "permission:create-user - start")
    let $group:=$properties[@key=$currentBase||".group"]/text()
    let $temp := if (sm:group-exists($group)) then () else sm:create-group($group)
    let $temp2 := for $entryUser in $properties[starts-with(@key,$currentBase||".user.login")]
                    let $key:=fn:tokenize($entryUser/@key,"/")[last()] 
                    let $log:=util:log("INFO", "permission:create-user - user="||$entryUser||" exist="||sm:user-exists($entryUser/text()))
                    let $t:= if (sm:user-exists($entryUser/text())) 
                        then (sm:set-user-primary-group($entryUser/text(),$group)) 
                        else (sm:create-account($entryUser/text(),$properties[@key=$currentBase||".user.mdp."||$entryUser]/text(),$group))
                    let $defaultRights:=sm:set-umask($entryUser/text(),2)
                return()
    let $temp3 := update delete doc($permission:uriProperties)//entry[contains(@key,'.mdp.')]         
   return (for $group in sm:get-groups()
     return <group>{$group}
            {for $user in sm:get-group-members($group)
             return  <user>
                         {$user} 
                         - primary group: {sm:get-user-primary-group($user)}
                         - is dba :{sm:is-dba($user)}
                         - umask :{sm:get-umask($user)}
                         - umask mode :{sm:octal-to-mode(util:int-to-octal(sm:get-umask($user)))}
                     </user> }
             </group>)
};
(: on chnage les permission sur la liste de fichier passé en param :)
declare function permission:change-permission($dir as xs:string,$userDefault as xs:string,$groupDefault as xs:string) {
    let $log:= util:log("INFO", "permission:change-permission[$dir="||$dir||", $userDefault="||$userDefault||", $groupDefault="||$groupDefault||"]")
    let $ici:=permission:change-permission-element($dir, $userDefault, $groupDefault)
    let $subdir :=  if(xmldb:collection-available($dir) and $ici/@modifiable=true()) then (
            for $child in xmldb:get-child-collections($dir)
            return permission:change-permission($dir||"/"||$child, $userDefault, $groupDefault)
        ) else()
    let $subrec := if(xmldb:collection-available($dir) and $ici/@modifiable=true())then (
            for $child in xmldb:get-child-resources($dir)
            return(permission:change-permission-element($dir||"/"||$child, $userDefault, $groupDefault))  
        ) else()
    return(<dir id="{$dir}" owner="{$ici/@owner}" group="{$ici/@group}" mode="{$ici/@mode}"  modifiable="{$ici/@modifiable}">{$subrec}{$subdir}</dir>)
};
declare function permission:change-permission-element($element as xs:string,$userDefault as xs:string,$groupDefault as xs:string) {
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
    let $log := util:log("DEBUG", "permission:change-permission-element - retour ["||$retour/@id ||", owner="|| $retour/@owner||", group="|| $retour/@group||", mode="|| $retour/@mode||", modifiable="|| $retour/@modifiable||"]")
    return ($retour)
};
(::::::::::::::::::::::FIN DROITS:::::::::::::::::::::::::::::::)


declare function permission:change-permission-element($element as xs:string) {
permission:change-permission-element($element,$permission:defaultUser,$permission:defaultGroup)
};
declare function permission:change-permission($dir as xs:string) {
permission:change-permission($dir,$permission:defaultUser,$permission:defaultGroup)
};

declare function permission:create-user() {
permission:create-user($permission:properties,$permission:currentBase)
};
