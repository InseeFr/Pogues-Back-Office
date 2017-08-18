<xsl:stylesheet xmlns:myrmes="temp/tempRmes.xml"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues" xmlns:rdf="http://rdf.insee.fr"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:rmes="http://dvrmessnclas01.ad.insee.intra:8080/datalift"
    xmlns:sparql="http://www.w3.org/2005/sparql-results#" exclude-result-prefixes="xs pogues"
    version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <!-- Ce template permet :
        - de transformer les IfFalse en IfTrue, posés au bon endroit
        - de trier / fusionner lorsque plusieurs Goto partent de la même question
        - de prendre la négation des filtres au départ des questions 
        (on remplace la condition qui permet de sauter des questions par celle qui permet de les afficher) 
    -->
    <!-- Liste des endroits qui sont le begin d'un else -->
    <xsl:variable name="InitElse" select="//pogues:IfTrue[../pogues:IfFalse]/text()"/>
    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>
    <!-- verrue des contrôles + IfThenElse : transformation des else en then + tri-fusion lorsqu'il y a plusieurs Goto qui partent du même endroit  -->
    <xsl:template match="pogues:Child">
        <!-- On rajoute les Goto issus des IfElse précédents -->
        <xsl:if test="index-of($InitElse,@id)&gt;0">
            <xsl:for-each select="preceding::pogues:GoTo[pogues:IfTrue=current()/@id]">
                <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="concat('_',@id,'_')"/>
                    <xsl:attribute name="type">BeginIfType</xsl:attribute>
                    <xsl:copy-of select="pogues:Description"/>
                    <xsl:element name="Expression"
                        namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:choose>
                            <xsl:when test="substring(pogues:Expression,1,3)='not '">
                                <xsl:value-of
                                    select="substring(pogues:Expression,4,string-length(pogues:Expression)-3)"
                                />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('not (',pogues:Expression,')')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                    <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:value-of select="pogues:IfFalse"/>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>
        </xsl:if>
        <xsl:copy>
            <!-- On recopie tout, sauf les GoTo, qui sont gérés à vide par le template correspondant -->
            <xsl:apply-templates select="* | @*"/>
            <!-- Verrue : On crée les contrôles à partir des GoTo qui n'ont pas de IfTrue
            L'expression est la même ; le message d'erreur est transporté par la Description -->
            <!--<xsl:for-each select="pogues:GoTo[not(pogues:IfTrue) or pogues:IfTrue='']">
                <xsl:element name="Control" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="@id"/>
                    <xsl:attribute name="criticity">ERROR</xsl:attribute>
                    <xsl:copy-of select="pogues:Description"/>
                    <xsl:copy-of select="pogues:Expression"/>
                    <xsl:element name="FailMessage" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:value-of select="pogues:Description"/>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>-->
        </xsl:copy>
        <!-- On rajoute les Goto du Child triés de la cible la plus lointaine dans le questionnaire à la plus proche -->
        <!-- On ne prend que ceux pour lesquels IfTrue existe : les autres sont utilisés pour générer les contrôles -->
        <xsl:for-each select="pogues:GoTo[pogues:IfTrue!='']">
            <!-- tri selon la position de la cible dans le questionnaire : 
                    on compte le nombre de Child intermédiaires entre le Goto et la cible -->
            <xsl:sort
                select="count(following::pogues:Child[following::pogues:Child[@id=current()/pogues:IfTrue]])"
                data-type="number" order="descending"/>
            <!-- s'il y a plusieurs GoTo qui pointent sur la même cible, on les traite via le premier -->
            <xsl:if
                test="empty(preceding-sibling::pogues:GoTo[pogues:IfTrue=current()/pogues:IfTrue])">
                <xsl:element name="Child" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:apply-templates select="@*"/>
                    <xsl:attribute name="type">BeginIfType</xsl:attribute>
                    <xsl:element name="Description"
                        namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:value-of select="pogues:Description"/>
                        <xsl:for-each
                            select="following-sibling::pogues:GoTo[pogues:IfTrue=current()/pogues:IfTrue]">
                            <xsl:value-of select="concat(' ',pogues:Description)"/>
                        </xsl:for-each>
                    </xsl:element>
                    <xsl:element name="Expression"
                        namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:choose>
                            <xsl:when test="../@xsi:type='QuestionType'">
                                <xsl:sequence select="'not ('"/>
                                <xsl:value-of select="pogues:Expression"/>
                                <xsl:for-each
                                    select="following-sibling::pogues:GoTo[pogues:IfTrue=current()/pogues:IfTrue]">
                                    <xsl:value-of select="concat(') or (',pogues:Expression)"/>
                                </xsl:for-each>
                                <xsl:sequence select="')'"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:sequence select="'('"/>
                                <xsl:value-of select="pogues:Expression"/>
                                <xsl:for-each
                                    select="following-sibling::pogues:GoTo[pogues:IfTrue=current()/pogues:IfTrue]">
                                    <xsl:value-of select="concat(') and (',pogues:Expression)"/>
                                </xsl:for-each>
                                <xsl:sequence select="')'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                    <xsl:copy-of select="pogues:IfTrue"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <!-- verrue : gestion des NominalDomain -->
    <!--    <xsl:template match="pogues:Datatype[@typeName='TEXT' and pogues:MaxLength='0' 
        and not (exists(../pogues:CodeListReference))]">
        <xsl:copy>
            <xsl:attribute name="typeName">BOOLEAN</xsl:attribute>
            <xsl:attribute name="xsi:type">BooleanDatatypeType</xsl:attribute>
        </xsl:copy>
    </xsl:template>
-->
    <!-- verrue : gestion des QuestionGrid -->
    <!--<xsl:template match="pogues:Response[position()=last() and position()>1 and pogues:CodeListReference and pogues:Datatype[pogues:MaxLength='0']]">
        <xsl:element name="Dimension" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="rank">1</xsl:attribute>
            <xsl:value-of select="pogues:CodeListReference"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="pogues:Response[position()=last() and position()>2 and pogues:CodeListReference and pogues:Datatype[pogues:MaxLength='2']]">
        <xsl:element name="Dimension" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="rank">2</xsl:attribute>
            <xsl:value-of select="pogues:CodeListReference"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="pogues:Response[position()=last()-1 and position()>1 and pogues:CodeListReference 
                        and following-sibling::pogues:Response[pogues:Datatype[pogues:MaxLength='2'] and pogues:CodeListReference]]">
        <xsl:element name="Dimension" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="rank">1</xsl:attribute>
            <xsl:value-of select="pogues:CodeListReference"/>
        </xsl:element>
    </xsl:template>-->
    <!-- verrue : gestion des Code sans Value -->
    <xsl:template match="pogues:CodeList/pogues:Code[not(pogues:Value)]">
        <xsl:copy>
            <xsl:element name="Value" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of select="count(preceding-sibling::pogues:Code)+1"/>
            </xsl:element>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    <!-- verrue : gestion des Code avec Value à blanc -->
    <xsl:template match="pogues:CodeList/pogues:Code/pogues:Value[not(text()) or text()='']">
        <xsl:copy>
            <xsl:value-of select="count(parent::pogues:Code/preceding-sibling::pogues:Code)+1"/>
        </xsl:copy>
    </xsl:template>
    <!-- verrue : rajout du nombre d'éléments dans les Dimension PRIMARY pour calculer plus facilement les coordonnées des tableaux à 2 dimensions -->
    <xsl:template match="pogues:Dimension[@dimensionType='PRIMARY']">
        <xsl:copy>
            <xsl:attribute name="dimension1Length">
                <xsl:choose>
                    <xsl:when test="@dynamic = '0'">
                        <!-- La première dimension est une Liste de codes -->
                        <xsl:variable name="dimension1CL">
                            <xsl:value-of select="pogues:CodeListReference"/>
                        </xsl:variable>
                        <xsl:value-of
                            select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$dimension1CL]/count(pogues:Code)"
                        />
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- La première dimension est un roster -->
                        <xsl:variable name="maximumAllowed" select="substring-after(@dynamic,'-')"/>
                        <xsl:choose>
                            <!-- C'est un tableau dynamique -->
                            <xsl:when test="$maximumAllowed=''">
                                <xsl:value-of select="'1'"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- C'est un tableau standard avec roster -->
                                <xsl:value-of select="$maximumAllowed"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    <!-- verrue : création de la Dimension SECONDARY pour les tableaux à 2 dimensions définis sans -->
    <xsl:template
        match="pogues:ResponseStructure[not(pogues:Dimension[@dimensionType='SECONDARY'])         and parent::pogues:Child/@questionType='TABLE']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:element name="Dimension" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:attribute name="dimensionType">SECONDARY</xsl:attribute>
                <xsl:attribute name="dynamic">0</xsl:attribute>
                <xsl:element name="CodeListReference"
                    namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:value-of select="concat(../pogues:Name,'-dim2')"/>
                </xsl:element>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    <!-- verrue : création de la CodeList de la Dimension SECONDARY créée ci-dessus -->
    <xsl:template match="pogues:CodeLists">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates
                select="//pogues:ResponseStructure[not(pogues:Dimension[@dimensionType='SECONDARY'])                 and parent::pogues:Child/@questionType='TABLE']"
                mode="codeList"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="pogues:ResponseStructure" mode="codeList">
        <xsl:element name="CodeList" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="id" select="concat(replace(../pogues:Name,':',''),'-dim2')"/>
            <xsl:element name="Name" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of
                    select="concat('Deuxieme_dimension_du_tableau_',replace(../pogues:Name,':',''))"
                />
            </xsl:element>
            <xsl:element name="Label" namespace="http://xml.insee.fr/schema/applis/pogues">
                <!--<xsl:value-of select="concat('Deuxieme dimension du tableau ',replace(../pogues:Label,'^.*\} ',''))"/>-->
                <xsl:value-of select="concat('Dim2 ',replace(../pogues:Label,'^.*\} ',''))"/>
            </xsl:element>
            <xsl:apply-templates select="pogues:Dimension[@dimensionType='MEASURE']" mode="codeList"
            />
        </xsl:element>
    </xsl:template>
    <xsl:template match="pogues:Dimension" mode="codeList">
        <xsl:element name="Code" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:element name="Value" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of
                    select="count(preceding-sibling::pogues:Dimension[@dimensionType='MEASURE'])+1"
                />
            </xsl:element>
            <xsl:element name="Label" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of select="pogues:Label"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- verrue : transformation des CodeListSpecification en éléments de CodeLists -->
    <xsl:template match="pogues:CodeListSpecification">
        <!--<xsl:variable name="RMESList" select="document(encode-for-uri(pogues:retrievalQuery))"/>-->
        <xsl:variable name="listID" select="@id"/>
        <xsl:element name="CodeList" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="id" select="@id"/>
            <xsl:choose>
                <xsl:when test="pogues:Name">
                    <xsl:copy-of select="pogues:Name"/>
                </xsl:when>
                <xsl:otherwise>
                    <Name>
                        <xsl:value-of select="@id"/>
                    </Name>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="pogues:Label">
                    <xsl:copy-of select="pogues:Label"/>
                </xsl:when>
                <xsl:when test="pogues:Name">
                    <xsl:value-of select="concat('Liste des codes de la nomenclature ',pogues:Name)"
                    />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('Liste des codes de la nomenclature ',@id)"/>
                </xsl:otherwise>
            </xsl:choose>
            <!--            <xsl:value-of select="replace(replace(pogues:retrievalQuery,'&lt;','<'),'&gt;','>')"/>-->
            <!--<xsl:value-of select="concat('http://dvrmessnclas01.ad.insee.intra:8080/sparql?query=PREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%20',
                                         encode-for-uri(replace(replace(pogues:retrievalQuery,'&lt;','<'),'&gt;','>')))"/>-->
            <!--<xsl:variable name="RMESList" select="document(concat('http://dvrmessnclas01.ad.insee.intra:8080/sparql?query=PREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%20',
                                                                  encode-for-uri(replace(replace(pogues:retrievalQuery,'&lt;','<'),'&gt;','>'))))"/>
            -->
            <!--<xsl:variable name="RMESList" select="doc('http://www.tuteurs.ens.fr/internet/web/html/bases.html#s3')"/>-->
            <!--<xsl:variable name="RMESList" select="document('http://10.20.110.72:8079/exist/restxq/bonjour')"/>-->
            <!--<xsl:variable name="RMESList" select="doc('http://10.20.110.72:8079/exist/rest/db/bug.xml')"/>-->
            <!--            <xsl:variable name="RMESList" select="doc('http://localhost:8080/exist/rest/db/123456789.xml')"/>
-->
            <!--<xsl:copy-of select="$RMESList"/>-->
            <xsl:variable name="RMESList" select="document('temp/tempRmes.xml')"/>
            <!--<xsl:value-of select="$RMESList//sparql:sparql[@id=$listID]"/>-->
            <xsl:apply-templates
                select="$RMESList/sparql:ids/sparql:sparql[@id=$listID]/sparql:results/sparql:result[descendant::sparql:literal/@xml:lang='fr']"
            />
        </xsl:element>
    </xsl:template>
    <xsl:template match="sparql:result">
        <xsl:element name="Code" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:element name="Value" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of select="sparql:binding[@name='code']/sparql:literal"/>
            </xsl:element>
            <xsl:element name="Label" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of
                    select="sparql:binding[@name='intitule']/sparql:literal[@xml:lang='fr']"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="pogues:Label">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="starts-with(text(),'##{')">
                    <xsl:value-of select="replace(text(),'^.*\} ','')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="text()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    <!-- La non-réponse, ça casse tout... -->
    <xsl:template match="pogues:NonResponseModality"/>
    <!-- cas général : on recopie la structure initiale sans y retoucher -->
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
