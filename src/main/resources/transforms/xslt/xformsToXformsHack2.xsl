<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:saxon="http://saxon.sf.net/" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:sql="http://orbeon.org/oxf/xml/sql" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:exf="http://www.exforms.org/exf/1-0" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope">
    <xsl:template match="/">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>    
    </xsl:template>
   
    <xsl:template match="xhtml:html">
        <xhtml:html xmlns:xhtml="http://www.w3.org/1999/xhtml"
            xmlns:xf="http://www.w3.org/2002/xforms"
            xmlns:xs="http://www.w3.org/2001/XMLSchema"
            xmlns:fn="http://www.w3.org/2005/xpath-functions"
            xmlns:ev="http://www.w3.org/2001/xml-events"
            xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
            xmlns:fr="http://orbeon.org/oxf/xml/form-runner">
            <xsl:apply-templates select="node()"/>
        </xhtml:html>
    </xsl:template>
    
    <xsl:template match="*">
        <xsl:element name="{name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="xf:hint">
        <xsl:variable name="temp" select="fn:tokenize(@ref, '/')[2]"/>
        <xsl:variable name="hint" select="//resources/resource/*[name()=$temp]/hint"/>
        <xsl:choose>
            <xsl:when test="$hint=''"/>                            
            <xsl:otherwise>
                <xsl:element name="{name()}">
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="node()"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <xsl:template match="xf:help">
        <xsl:variable name="temp" select="fn:tokenize(@ref, '/')[2]"/>
        <xsl:variable name="help" select="//resources/resource/*[name()=$temp]/help"/>
        <xsl:choose>
            <xsl:when test="$help=''"/>                            
            <xsl:otherwise>
                <xsl:element name="{name()}">
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="node()"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <xsl:template match="xf:alert">
        <xsl:variable name="temp" select="fn:tokenize(@ref, '/')[2]"/>
        <xsl:variable name="alert" select="//resources/resource/*[name()=$temp]/alert"/>
        <xsl:choose>
            <xsl:when test="$alert=''"/>                            
            <xsl:otherwise>
                <xsl:element name="{name()}">
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="node()"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <xsl:template match="*[@xxf:order='label control hint help alert']">
        <xsl:element name="{name()}">
            <xsl:copy-of select="@*[not(name()='xxf:order')]"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="//resources/resource/*/hint|//resources/resource/*/help|//resources/resource/*/alert">
        <xsl:choose>
            <xsl:when test=".=''"/>
            <xsl:otherwise>
                <xsl:element name="{name()}">
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="node()"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>    
    
    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>    
</xsl:stylesheet>