xquery version "3.0";
module namespace common="http://www.insee.fr/collectes/commonstromae/common";
declare namespace rest="http://exquery.org/ns/restxq";
declare namespace functx = "http://www.functx.com";

declare variable $common:racine-racine := '/db/' ;
declare variable $common:repertoire-default := 'orbeon/fr/' ;

(: log et retourne une réponse rest, avec le statut passé en paramètre. Le message de <http:response> n'est jamais affiché => inutile :)
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
(: log et retourne une réponse rest, avec le statut passé en paramètre. Le message de <http:response> n'est jamais affiché => inutile :)
declare function common:rest-response($status as xs:int*, $message as xs:string*) as node()* {
   let $niveauLog := if($status>=200 and $status <300)then "INFO" else "ERROR"
    let $log := util:log($niveauLog, concat("rest-response - $status : ", $status)) 
    let $restResponse :=  
            <rest:response>
               <http:response status="{$status}" message="{$message}">
                   <http:header name="Content-Type" value="application/xml; charset=utf-8"/>
               </http:response>
           </rest:response>
    return $restResponse
};
(: fonction qui revoie la racine de la base : utilise la base par défaut (/db, ou /db/orbeon), 
ajoute soit le répertoire par défaut défini ici, soit celui passé en paramètre qui provient d'un query
vérifie que le chemin renvoyé correspond à un dossier : avec / à la fin, le rajoute le cas échéant :)
declare function common:racine($racine-query as xs:string*) as xs:string* {
    let $racine := if($racine-query="") then ($common:racine-racine||$common:repertoire-default) else ($common:racine-racine||$racine-query)    
    let $racine2 := if(fn:ends-with($racine ,"/"))then($racine)else($racine||"/")
    let $log := util:log("INFO", concat("racine - $racine-query : ", $racine-query, " - racine définie : ",$racine2)) 
    return $racine2
};

(: fonction utilitaire pour calculer le répertoire dans lequel est le questionnaire :)
declare function functx:chars ($arg as xs:string?) as xs:string* {
    for $ch in string-to-codepoints($arg)
    return codepoints-to-string($ch)
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


declare function common:mapping($enquete as xs:string,$racine-query as xs:string?) as xs:string {
let $racine := common:racine($racine-query)
return concat($racine,$enquete,'/mapping.xml')
};

declare function common:mapping($enquete as xs:string) as xs:string {
let $mapping := common:mapping($enquete,'')
return $mapping
};

(: Une fonction qui vérifie l'existence d'une collection à un emplacement donné et la crée si elle n'existe pas :)
declare function common:collection($emplacement as xs:string, $collection as xs:string) as xs:string{

let $test := if(not(xmldb:collection-available(concat($emplacement,'/',$collection))))
            then (xmldb:create-collection($emplacement, $collection))
            else ''
            return ''
};