<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="xs pogues"
    version="2.0">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Ce template permet de gérer les chevauchements entre GoTo et séquence :
        Si on part du milieu du paragraphe 1 pour arriver au milieu du paragraphe 3, il faut couper le GoTo en 3 :
        - fermer rouvrir en sortant du paragraphe 1
        - fermer rouvrir en entrant dans le paragraphe 3
    -->

    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="pogues:Child[@xsi:type='SequenceType']">

        <xsl:variable name="lElement" select="@id"/>
        
        <xsl:variable name="endApres" select="following::pogues:Child[@type='EndIfType']/@id"/>
        <xsl:variable name="beginAvant" select="preceding::pogues:Child[@type='BeginIfType']/@id"/>
<!--        <xsl:value-of select="concat($lElement,'-\-\-\-')"/>
        <xsl:value-of select="$endApres"/>
        <xsl:value-of select="'-\-\-\-'"/>
        <xsl:value-of select="$beginAvant"/>
-->        
        <!-- Liste des filtres qui se ferment au sein de la séquence et se sont ouverts avant
            EndIfType est un descendant de la séquence ; BeginIfType ne l'est pas -->
<!--        <xsl:variable name="lonelyIfEnd">
            <xsl:variable name="lonelyIfEndWithDouble">
                <xsl:value-of select="descendant::pogues:Child[@xsi:type='EndIfType' and index-of($beginAvant,@id)>0]/@id"/>    
            </xsl:variable>
            <xsl:if test="$lonelyIfEndWithDouble!=''">
                <xsl:for-each select="distinct-values($lonelyIfEndWithDouble)">
                    <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:attribute name="id" select="."/>
                        <xsl:attribute name="xsi:type">EndIfType</xsl:attribute>
                    </xsl:element>                    
                </xsl:for-each>                
            </xsl:if>
        </xsl:variable>-->
        <xsl:variable name="lonelyIfEnd">
            <xsl:copy-of select="descendant::pogues:Child[@type='EndIfType' and index-of($beginAvant,@id)>0]"/>
        </xsl:variable>
        <!-- Tous les begin qui ont un identifiant parmi les lonelyIfEnd.
        Pour chaque identifiant, on n'utilisera que le dernier s'il y en a plusieurs à cause des chevauchements de GoTo qui créent des doublons d'identifiant -->
        <xsl:variable name="lonelyIfEndBegin">
            <xsl:copy-of select="preceding::pogues:Child[@type='BeginIfType' and index-of($lonelyIfEnd/pogues:Child/@id,@id)>0]"/>
        </xsl:variable>
        
        <!-- Liste des filtres qui s'ouvrent au sein de la séquence et ne se ferment qu'après
            BeginIfType est un descendant de la séquence ; EndIfType ne l'est pas -->
        <!--<xsl:variable name="lonelyIfBeginWithoutDouble">
            <xsl:variable name="lonelyIfBeginWithDouble">
                <xsl:value-of select="descendant::pogues:Child[@xsi:type='BeginIfType' and index-of($endApres,@id)>0]/@id"/>    
            </xsl:variable>
            <xsl:for-each select="distinct-values($lonelyIfBeginWithDouble)">
                <ID><xsl:value-of select="."/></ID>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="lonelyIfBegin">
            <xsl:for-each select="$lonelyIfBeginWithoutDouble/ID">
                <xsl:copy-of select="descendant::pogues:Child[@xsi:type='BeginIfType' and @id=.][last()]"/>
            </xsl:for-each>
        </xsl:variable>-->
        <xsl:variable name="lonelyIfBegin">
            <xsl:copy-of select="descendant::pogues:Child[@type='BeginIfType' and index-of($endApres,@id)>0]"/>
        </xsl:variable>
<!--        <xsl:copy-of select="$lonelyIfBegin"/>
        <xsl:copy-of select="$lonelyIfEnd"/>
        <xsl:copy-of select="$lonelyIfEndBegin"/>
-->        <xsl:for-each select="$lonelyIfEnd/pogues:Child">
            <xsl:if test="not(index-of(preceding-sibling::pogues:Child/@id,current()/@id)>0)">
                <xsl:copy-of select="."/>
            </xsl:if>
        </xsl:for-each>
        <!--<xsl:copy-of select="$lonelyIfEnd"/>-->
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="pogues:Name"/>
            <xsl:apply-templates select="pogues:Label"/>
            <xsl:apply-templates select="pogues:Declaration"/>

            <!-- On rouvre les Begin correspondant aux End ; 
                s'il y en a plusieurs à cause de chevauchements de Goto, on prend le dernier -->
            <xsl:for-each select="$lonelyIfEnd/pogues:Child">
                <xsl:sort select="position()" data-type="number" order="descending"/>
                <xsl:if test="not(index-of(preceding-sibling::pogues:Child/@id,current()/@id)>0)">
                    <xsl:copy-of select="$lonelyIfEndBegin/pogues:Child[@id=current()/@id][last()]"/>    
                </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates select="pogues:Child"/>
            <xsl:for-each select="$lonelyIfBegin/pogues:Child">
                <xsl:sort select="position()" data-type="number" order="descending"/>
                <xsl:if test="not(index-of(preceding-sibling::pogues:Child/@id,current()/@id)>0)">
                    <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:attribute name="id" select="@id"/>
                        <xsl:attribute name="type">EndIfType</xsl:attribute>
                    </xsl:element>                    
                </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates select="pogues:Control"/>
        </xsl:copy>
        <!--<xsl:copy-of select="$lonelyIfBegin"/>-->
        <xsl:for-each select="$lonelyIfBegin/pogues:Child">
            <xsl:if test="not(index-of(preceding-sibling::pogues:Child/@id,current()/@id)>0)">
                <xsl:copy-of select="."/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- cas général : on recopie la structure initiale sans y retoucher -->
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
