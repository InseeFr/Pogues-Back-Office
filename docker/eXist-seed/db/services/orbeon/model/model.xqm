xquery version "3.0";
module namespace model="http://www.insee.fr/collectes/model";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace rest="http://exquery.org/ns/restxq";


(:MODEL:)
declare
%rest:GET
%rest:path("/model/{$enquete}/{$unite}")
%rest:query-param("racine", "{$racine}","")
function model:get-model ($enquete as xs:string*, $unite as xs:string*,$racine as xs:string*) as node()*
{
let $racine-enquete-full := concat(common:racine($racine),'/',lower-case($enquete),'/')
let $col := common:calcol($unite)
let $cheminfic := concat('/data/init/',$col,'/',$unite,'.xml')
let $model := 
    for $collection-modele in xmldb:get-child-collections($racine-enquete-full)[ . != 'lib'] 
    let $chemintotal := concat($racine-enquete-full,$collection-modele,$cheminfic) where doc-available($chemintotal) 
    return $collection-modele 
(:let $debug := concat('racine-enquete = ',$racine-enquete, ' , col = ', $col, ' , cheminfic = ', $cheminfic):)
return 
<model>{$model}</model>
};