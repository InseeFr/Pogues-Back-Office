xquery version "3.0";
module namespace model="http://www.insee.fr/collectes/model";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
  

declare variable $model:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/model/helloworld'}</xqm><version>{$modelt:version}</version><message>{"Chuck Norris ne ment jamais, c'est la vérité qui se trompe."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/model/helloworld")
function model:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/model/helloworld'}</xqm><version>{$model:version}</version><message>{"Chuck Norris ne s'est jamais rendu à l'école. Chuck Norris ne se rend jamais."}</message></resultat>
    return (common:rest-response(200),$mess)
};

(:MODEL:) 
declare
%rest:GET
%rest:path("/model/{$enquete}/{$unite}")
function model:get-model ($enquete as xs:string*, $unite as xs:string*) as node()*
{
    
let $racine-enquete-full := concat('/db/orbeon/fr/',lower-case($enquete),'/')
let $col := common:calcol($unite)
let $cheminfic := concat('/data/init/',$col,'/',$unite,'.xml')
let $model := 
    for $collection-modele in xmldb:get-child-collections($racine-enquete-full)
    let $chemintotal := concat($racine-enquete-full,$collection-modele,$cheminfic) where doc-available($chemintotal) 
    return $collection-modele 

return 
<model>{$model}</model>

};