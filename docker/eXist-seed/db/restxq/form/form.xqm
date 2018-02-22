xquery version "3.0";
module namespace form="http://www.insee.fr/collectes/form";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
declare namespace xhtml="http://www.w3.org/1999/xhtml";

declare variable $form:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/model/helloworld'}</xqm><version>{$modelt:version}</version><message>{"Chuck Norris a réussi à trouver la page 404."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/form/helloworld")
function form:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/form/helloworld'}</xqm><version>{$form:version}</version><message>{"Chuck Norris a réussi à trouver la page 404."}</message></resultat>
    return (common:rest-response(200),$mess)
};

(:INSTANCE POUR QUESTIONNAIRE:)
declare
%rest:GET
%rest:path("/collectes/form/{$enquete}/{$modele}/{$unite}")
function form:get-instance($enquete as xs:string*, $modele as xs:string*,$unite as xs:string*) as item()* 
{
let $col := common:calcol($unite)
let $doc-user := concat( '/db/orbeon/fr/',$enquete,'/',$modele,'/data/',$col,'/',$unite,'.xml')
let $doc-prerempli := concat( '/db/orbeon/fr/',$enquete,'/',$modele,'/data/init/',$col,'/',$unite,'.xml')
return if (doc-available($doc-user)) 
            then (doc($doc-user)//form)
             else (if(doc-available($doc-prerempli))
                        then(doc($doc-prerempli)//form)
                        else(<vide/>)       
      )
};

(:Recuperation du formulaire:)
declare
%rest:GET
%rest:path("/collectes/formulaire/{$enquete}/{$modele}")
function form:get-instance ($enquete as xs:string*, $modele as xs:string*) as node()* 
{
let $formulaire := concat('/db/orbeon/fr/',$enquete,'/',$modele,'/form/form.xhtml')
return 
    if (not(doc-available($formulaire))) 
    then (<vide/>)
    else doc($formulaire)//xhtml:html 
};