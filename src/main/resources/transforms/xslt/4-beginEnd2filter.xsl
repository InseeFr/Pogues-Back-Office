<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="xs pogues"
    version="2.0">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Ce template permet de transformer les couples BeginIfType / EndIfType 
        en FilterType avec une balise IfTrue contenant les éléments intermédiaires.
        Il s'appuie sur un template de base qui va chercher le premier enfant de chaque élément et le frère suivant.
    -->

    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="node()[position()=1]"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- cas général : on recopie l'élément, au sein duquel on appelle le premier enfant et après lequel on appelle le frère suivant -->
    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()[position()=1]"/>
        </xsl:copy>
        <xsl:apply-templates select="following-sibling::node()[1]"/>
    </xsl:template>

    <!-- On ne recopie par les EndIf et on n'appelle pas le frère qui les suit, car le BeginIf s'en charge -->
    <xsl:template match="pogues:Child[@type='EndIfType']"/>

    <xsl:template match="pogues:Child[@type='BeginIfType']">
        <!-- S'il y a des éléments Child de types autres que des BeginIfType et EndIfType compris entre le Begin et le End,
        Alors on a un objet de type FilterType non vide -->
        <xsl:if test="iatddi:isCommon(following-sibling::pogues:Child[not(@type='BeginIfType' or @type='EndIfType')]/@id,
                                            following-sibling::pogues:Child[@type='EndIfType' and @id=current()/@id][1]
                                                /preceding-sibling::pogues:Child[not(@type='BeginIfType' or @type='EndIfType')]/@id)='true'">
            <xsl:copy>
                <xsl:copy-of select="@id"/>
                <xsl:attribute name="type">FilterType</xsl:attribute>
                <xsl:copy-of select="pogues:Description"/>
                <xsl:copy-of select="pogues:Expression"/>
                <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:apply-templates select="following-sibling::node()[1]"/>
                </xsl:element>
            </xsl:copy>
        </xsl:if>
        <!-- Appel du frère qui suit le EndIfType, sans appel au filtre, qui est vide -->
        <xsl:apply-templates select="following-sibling::pogues:Child[@type='EndIfType' and @id=current()/@id][1]
                                                                    /following-sibling::node()[1]"/>                
            
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Fonction pour identifier si 2 listes ont des éléments communs</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatddi:isCommon">
        <xsl:param name="list1"/>
        <xsl:param name="list2"/>
        <xsl:variable name="isCommon">
            <xsl:choose>
                <xsl:when test="empty($list1)">false</xsl:when>
                <xsl:when test="empty($list2[1])">false</xsl:when>
                <xsl:when test="index-of($list1,$list2[1])>0">true</xsl:when>
                <xsl:when test="empty($list2[2])">false</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="iatddi:isCommon($list1,$list2[position()>1])"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$isCommon"/>
    </xsl:function>

</xsl:stylesheet>
