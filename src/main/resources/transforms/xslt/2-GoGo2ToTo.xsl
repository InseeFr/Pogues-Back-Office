<xsl:stylesheet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pogues="http://xml.insee.fr/schema/applis/pogues" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs pogues" version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/><!-- Ce template permet de gérer les chevauchements entre pogues:GoTo :
        Q1 : pogues:GoTo 4
        Q2 : pogues:GoTo 5
        
        Au final :
        Q1
        IfBegin 1
            Q2
            IfBegin 2
                Q3
            IfEnd 2
        IfEnd 1
        IfBegin 2 (condition : 2 ou pas 1)
            Q4
        IfEnd 2
        Q5
    -->
    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="pogues:Child">
        <xsl:variable name="lElement" select="@id"/>
        <xsl:variable name="premierDessus" select="(preceding::pogues:Child[pogues:IfTrue=$lElement]/@id)[1]"/>
        <xsl:if test="exists($premierDessus)"><!-- On ferme le premier BeginIfType qui pointe sur l'élément et tous les BeginIfType qui suivent et qui ne sont pas fermés -->
            <xsl:variable name="lesElementsPasses" select="preceding::pogues:Child/@id[preceding::pogues:Child[@id=$premierDessus]]                  | ancestor::pogues:Child/@id[preceding::pogues:Child[@id=$premierDessus]]"/>
            <xsl:for-each select="preceding::pogues:Child[@type='BeginIfType' and (preceding::pogues:Child/@id=$premierDessus                                                    or self::pogues:Child/@id=$premierDessus)                                             and not(index-of($lesElementsPasses,pogues:IfTrue)&gt;0)]">
                <xsl:sort select="position()" order="descending"/>
                <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="@id"/>
                    <xsl:attribute name="type">EndIfType</xsl:attribute>
                </xsl:element>
            </xsl:for-each><!-- On rouvre les BeginIfType qui ont été fermés, parce qu'ils pointent plus loin -->
            <xsl:for-each select="preceding::pogues:Child[@type='BeginIfType' and preceding::pogues:Child/@id=$premierDessus                                                and not(pogues:IfTrue = $lElement)                                                and not(index-of($lesElementsPasses,pogues:IfTrue)&gt;0)]"><!-- Eléments situés entre la source du filtre à ouvrir, exclue, et la question où l'on doit rouvrir, incluse -->
                <xsl:variable name="ciblesIntermediaires" select="following::pogues:Child[exists(following::pogues:Child[@id=$lElement])                     or @id=$lElement]/@id"/>
                <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="@id"/>
                    <xsl:attribute name="type">BeginIfType</xsl:attribute>
                    <xsl:element name="Description" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:value-of select="'Réouverture du pogues:GoTo '"/>
                        <xsl:value-of select="@id"/>
                        <xsl:value-of select="' depuis le Child '"/>
                        <xsl:value-of select="$lElement"/>
                    </xsl:element>
                    <xsl:element name="Expression" namespace="http://xml.insee.fr/schema/applis/pogues"><!-- On conserve la condition qui pointe plus loin -->
                        <xsl:value-of select="concat('(',pogues:Expression,')')"/><!-- On y ajoute le fait qu'il peut ne pas être nécessaire                   --><!-- Car il suffit qu'il soit filtré                                         --><!-- et qu'un des filtres qui mènent à lui et se terminent avant la question --><!-- soit faux                                                               --><!-- La source du filtre est avant celle du filtre à rouvrir                         --><!-- La cible du filtre est avant la question et après la source du filtre à rouvrir -->
                        <xsl:for-each select="preceding::pogues:Child[@type='BeginIfType' and index-of($ciblesIntermediaires,pogues:IfTrue)&gt;0]">
                            <xsl:choose>
                                <xsl:when test="substring(pogues:Expression,1,4)='not '">
                                    <xsl:value-of select="concat(' or ',substring(pogues:Expression,5))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat(' or not (',pogues:Expression,')')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>
        </xsl:if><!-- On recopie tout notre Child -->
        <xsl:copy>
            <xsl:apply-templates select="* | @*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="pogues:IfTrue"/><!-- cas général : on recopie la structure initiale sans y retoucher -->
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>