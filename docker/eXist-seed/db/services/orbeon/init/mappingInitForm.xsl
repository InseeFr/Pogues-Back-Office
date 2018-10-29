<xsl:stylesheet xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:saxon="http://saxon.sf.net/" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:sql="http://orbeon.org/oxf/xml/sql" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:exf="http://www.exforms.org/exf/1-0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fb="http://orbeon.org/oxf/xml/form-builder" xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope" exclude-result-prefixes="#all" version="2.0">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/><!-- Génération d'une instance xForms d'initialisation.
        On itère sur une instance vide, extraite d'un formulaire (passé en paramètre), la structure de l'instance est issue de la génération du questionnaire.
        Pour chaque élément, on le recopie en récupérant une éventuelle valeur initialisée au sein d'un fichier d'initialisation.
        Comme, les variables de l'instance n'ont pas nécessairement le même nommage que les variables métiers, il est nécessaire de s'appuyer sur un fichier de mapping.
        Ce fichier de mapping est généré par ailleurs en s'appuyant sur le variableScheme. 
        Il est de la forme :
        <nomVariableMetier idDDI='nomVariableDDI'/>
        ...
        Attention, une même variable métier peut apparaître plusieurs fois (correspondance avec des variables DDI issues de modèles de questionnaire différents).
    --><!-- ******************* Paramètres de la transformation ************************************************ --><!-- Fichier contenant le mapping entre les variables métier (issus du fichier de personnalisation et les variables DDI issues de la description du questionnaire.
        Le template de Mapping est de la forme <nomVariableMétier idDDI="nomVariableDDI"/> -->
    <xsl:param name="mappingFileURI" select="'mappingDefault.xml'"/><!-- Fichier contenant le forms pour lequel l'instance est prévue. 
        A noter que la transformation s'appuie sur une structure du code Xforms avec l'instance xforms attendue contenu dans un élemnt <form>. -->
    <xsl:param name="formFileURI" select="'formDefault.xhtml'"/><!-- ******************** Calculs préalables stockés sous forme de variable globale. ********************** --><!-- On récupère l'instance XForms comprise sous l'élément <form> du Xforms. -->
    <xsl:variable name="instanceSample" select="document($formFileURI)//form"/><!-- Comme la transformation va itérer sur l'output, on stocke l'input en variable. -->
    <xsl:variable name="input" select="."/><!-- On consolide fichier d'input et de mapping, à terme cette consolidation à vocation à figurer directement dans l'input. -->
    <xsl:variable name="inputMappe">
        <xsl:call-template name="inputMappe"/>
    </xsl:variable><!-- Template qui produit un fichier perso et mapping consolidé, l'arbre temporaire produit est de la forme :
        <variableMetier>
               <aliasDDI>VariableDDI1</aliasDDI>
               <aliasDDI>VariableDDI2</aliasDDI>
               valeurVariableMetier
        </variableMetier>
        On ajoute à chaque variable du fichier d'initialisation, des éléments <aliasDDI/> correspondant aux noms des variables DDI correspondantes.
        A noter qu'une même variable métier peut pointer vers plusieurs variables DDI, mais celles-ci doivent être exclusives les unes des autres,
        c'est-à-dire ne pas figurer au sein d'un même formulaire mais au sein de modèles de formulaires différents.-->
    <xsl:template name="inputMappe"><!-- La racine du fichier d'initialisation contenant les infos dédiés à la perso des questionnaires est <InformationsPersonnalisees/> -->
        <xsl:for-each select="//InformationsPersonnalisees//*"><!-- On récupère l'ensemble des aliasDDI qui sont sous la forme de valeurs d'attributs @idDDI au sein du fichier de mapping, mais on remonte tous les éléments "mappant" (permet d'éviter de sortir en double les éléments avec sans mapping). -->
            <xsl:variable name="alias" select="document($mappingFileURI)//*[name()=current()/name()]"/><!-- On recopie l'élément courant. -->
            <xsl:copy><!-- On lui ajoute ses alias, à noter qu'un alias vide est créé quand il y a présence de l'élément dans l'instance de mapping mais pas d'idDDI 
                    (correspondance parfaite entre nom de la variable métier et nom de la variable dans l'instance xforms) -->
                <xsl:for-each select="$alias">
                    <aliasDDI>
                        <xsl:value-of select="./@idDDI"/>
                    </aliasDDI>
                </xsl:for-each>
                <xsl:value-of select="text()"/>
            </xsl:copy>
        </xsl:for-each>
    </xsl:template><!-- Template racine qui va "switcher" en itérant sur un arbre instanceSample passé en paramètre, l'input initial est stocké dans une variable globale. -->
    <xsl:template match="/"><!-- On applique la transformation sur l'instance xforms example. -->
        <xsl:apply-templates select="$instanceSample"/>
    </xsl:template><!-- Racine de l'instance, on la recopie à l'identique. -->
    <xsl:template match="form">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template><!-- Elément de l'instance. Ils sont susceptibles d'avoir une valeur et des alias.  -->
    <xsl:template match="form//*[not(name() ='stromae')]"><!-- On récupère l'éventuelle valeur en recherchant dans l'input 'mappé' (aka avec les aliasDDI ajoutés), l'élément ayant le même nom ou un aliasDDI de même nom. -->
        <xsl:variable name="value" select="$inputMappe//*[name()=current()/name() or aliasDDI = current()/name()]/text()"/><!-- On recopie la structure de l'instance à l'identique, en ajoutant la valeur éventuellement récupérée (si rien, on ajoute une valeur vide). -->
        <xsl:copy>
            <xsl:value-of select="$value"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="form/stromae">
        <xsl:copy>
            <xsl:apply-templates/><!-- On ajoute ici toutes les variables métier non mappé (soit par une correspondance avec un idDDI, soit par une correspondance parfaite des noms) -->
            <externals>
                <xsl:copy-of select="$inputMappe/*[not(aliasDDI)]"/>
            </externals>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>