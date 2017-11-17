<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    exclude-result-prefixes="xsl xhtml r l">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/*">
        <xsl:apply-templates select="//r:CodeList"/>
    </xsl:template>

    <xsl:template match="r:CodeList">
        <CodeList id="{r:ID}">
            <Name>
                <xsl:value-of select="r:UserID"/>
            </Name>
            <Label>
                <xsl:call-template name="labelCodeListFormated">
                    <xsl:with-param name="ID" select="r:ID"/>
                </xsl:call-template>
            </Label>
            <xsl:apply-templates select="l:Code"/>
        </CodeList>
    </xsl:template>

    <xsl:template match="l:Code">
        <xsl:variable name="Codes">
            <xsl:apply-templates select="l:Code">
                <xsl:with-param name="IdParent" select="r:ID"/>
            </xsl:apply-templates>
        </xsl:variable>
        <Code id="{r:ID}">
            <Value>
                <xsl:value-of select="r:Value"/>
            </Value>
            <Label>
                <xsl:call-template name="labelCodeFormatedLevel1">
                    <xsl:with-param name="ID" select="r:CategoryReference/r:ID"/>
                </xsl:call-template>
            </Label>
        </Code>
        <xsl:copy-of select="$Codes"/>
    </xsl:template>

    <xsl:template match="l:Code/l:Code">
        <xsl:param name="IdParent"/>
        <xsl:variable name="Codes">
            <xsl:apply-templates select="l:Code">
                <xsl:with-param name="IdParent" select="r:ID"/>
            </xsl:apply-templates>
        </xsl:variable>
        <Code id="{r:ID}">
            <Value>
                <xsl:value-of select="r:Value"/>
            </Value>
            <Label>
                <xsl:call-template name="labelCodeFormatedLevel2">
                    <xsl:with-param name="ID" select="r:CategoryReference/r:ID"/>
                </xsl:call-template>
            </Label>
            <Parent>
                <xsl:value-of select="$IdParent"/>
            </Parent>
        </Code>
        <xsl:copy-of select="$Codes"/>
    </xsl:template>

    <xsl:template name="labelCodeFormatedLevel1">
        <xsl:param name="ID"/>
        <xsl:apply-templates select="//l:Category[r:ID=$ID]/r:Label/r:Content"/>
    </xsl:template>

    <xsl:template name="labelCodeFormatedLevel2">
        <xsl:param name="ID"/>
        <xsl:apply-templates select="//l:Category[r:ID=$ID]/r:Label/r:Content"/>
    </xsl:template>

    <xsl:template name="labelCodeListFormated">
        <xsl:param name="ID"/>
        <xsl:apply-templates select="//r:CodeList[r:ID=$ID]/r:Label/r:Content"/>
    </xsl:template>

</xsl:transform>
