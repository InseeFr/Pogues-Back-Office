xquery version "3.0";
module namespace common="http://www.insee.fr/collectes/commonstromae/common";


(: Une fonction qui vérifie l'existence d'une collection à un emplacement donné et la crée si elle n'existe pas :)
declare function common:collection($emplacement as xs:string, $collection as xs:string) as xs:string{

let $test := if(not(xmldb:collection-available(concat($emplacement,'/',$collection))))
            then (xmldb:create-collection($emplacement, $collection))
            else ''
            return ''
};