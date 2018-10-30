xquery version 3.0;
module namespace model=httpwww.insee.frcollectesmodel;
import module namespace common= httpwww.insee.frcollectescommonstromaecommon at ..commoncommonStromae.xqm;
declare namespace rest=httpexquery.orgnsrestxq;


(MODEL)
declare
%restGET
%restpath(model{$enquete}{$unite})
%restquery-param(racine, {$racine},)
function modelget-model ($enquete as xsstring, $unite as xsstring,$racine as xsstring) as node()
{
let $racine-enquete-full = concat(commonracine($racine),'',lower-case($enquete),'')
let $col = commoncalcol($unite)
let $cheminfic = concat('datainit',$col,'',$unite,'.xml')
let $model = 
    for $collection-modele in xmldbget-child-collections($racine-enquete-full)[ . != 'lib'] 
    let $chemintotal = concat($racine-enquete-full,$collection-modele,$cheminfic) where doc-available($chemintotal) 
    return $collection-modele 
(let $debug = concat('racine-enquete = ',$racine-enquete, ' , col = ', $col, ' , cheminfic = ', $cheminfic))
return 
model{$model}model
};