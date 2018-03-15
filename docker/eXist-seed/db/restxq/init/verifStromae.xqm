xquery version "3.0";
module namespace verif="http://www.insee.fr/coltrane/orbeon/verif";
import module namespace common= "http://www.insee.fr/collectes/commonstromae/common" at "../common/commonStromae.xqm";
 
declare variable $verif:version := "20171206-OR";

(:~
 : HELLOWORLD
 :
 : GET /contact/helloworld
 :
 : @return 200 + <resultat><xqm>{'/coltrane/verif/helloworld'}</xqm><version>{$verif:version}</version><message>{"Chuck Norris comprend Jean-Claude Van Damme."}</message></resultat>
:)

declare
%rest:GET
%rest:path("/coltrane/verif/helloworld")
function verif:helloworld() as item()+{
    let $mess:=<resultat><xqm>{'/coltrane/verif/helloworld'}</xqm><version>{$verif:version}</version><message>{"Chuck Norris comprend Jean-Claude Van Damme."}</message></resultat>
    return (common:rest-response(200),$mess)
};


declare
%rest:POST("{$content}")
%rest:path("/coltrane/verif/{$enquete}")
function verif:get-verif ( $enquete as xs:string*, $content as node()*)   {
let $log := util:log("INFO", concat("init:get-init - enquete : ", $enquete))
let $erreur:=
    <Erreurs>
         <Modeles>{
             for $modele in $content//Modeles/Modele/@idModele
             let $formulaire := '/db/orbeon/fr/'||lower-case($enquete)||'/'||lower-case($modele)||'/form/form.xhtml'
             return if (doc-available($formulaire)) then () else ( 
                 <Modele idModele="{$modele}">Le fichier form.xhtml du modèle de questionnaire {lower-case($modele)} de la campagne {$enquete} n'existe pas</Modele>
                 )
         }
         </Modeles>
    </Erreurs>
return if (not(empty($erreur//Modele)))then (
        common:rest-response(404, 'il manque des documents'),$erreur
    )else(
        common:rest-response(200, 'ok il ne manque pas de documents')
    )
};
