<xsl:stylesheet xmlns:pr="ddi:ddiprofile:3_2" xmlns:c="ddi:conceptualcomponent:3_2" xmlns:d="ddi:datacollection:3_2" xmlns:g="ddi:group:3_2" xmlns:cm="ddi:comparative:3_2" xmlns:l="ddi:logicalproduct:3_2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pogues="http://xml.insee.fr/schema/applis/pogues" xmlns:r="ddi:reusable:3_2" xmlns:s="ddi:studyunit:3_2" xmlns:xs="http://www.w3.org/2001/XMLSchema" xs:schemaLocation="ddi:instance:3_2 testq1a.xsd" exclude-result-prefixes="xs" version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="agencemaj">INSEE</xsl:param>
    <xsl:param name="enquete" select="//pogues:Questionnaire/pogues:Name"/>
    <xsl:variable name="monagence">
        <r:Agency>
            <xsl:value-of select="//pogues:Questionnaire/@agency"/>
        </r:Agency>
    </xsl:variable>
    <xsl:key name="DeclarationList" match="//pogues:Declaration" use="concat(pogues:Text/text(),@declarationType)"/>
    <xsl:template match="/">
        <DDIInstance>
            <xsl:copy-of select="$monagence"/>
            <r:ID>
                <xsl:value-of select="concat($agencemaj, '-',$enquete)"/>
            </r:ID>
            <r:Version>0.1.0</r:Version>
            <r:Citation>
                <r:Title>
                    <r:String xml:lang="fr-FR">De Pogues vers ENO : DDI déréférencé</r:String>
                    <r:String xml:lang="en-IE">From Pogues to ENO: dereferenced DDI</r:String>
                </r:Title>
            </r:Citation>
            <s:StudyUnit>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','SU')"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <d:Instrument xmlns="ddi:studyunit:3_2" xmlns:a="ddi:archive:3_2">
                    <xsl:copy-of select="$monagence"/>
                    <r:ID>v1</r:ID>
                    <r:Version>0.1.0</r:Version>
                    <r:Label>
                        <r:Content xml:lang="fr-FR">Questionnaire DDI 3.2 déréférencé de
                            Pogues</r:Content>
                    </r:Label>
                    <d:TypeOfInstrument>DDI XFORMS</d:TypeOfInstrument>
                    <d:ControlConstructReference>
                        <xsl:copy-of select="$monagence"/>
                        <d:Sequence xmlns="ddi:instance:3_2">
                            <xsl:copy-of select="$monagence"/>
                            <r:ID>
                                <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','SEQ')"/>
                            </r:ID>
                            <r:Version>0.1.0</r:Version>
                            <r:Label>
                                <r:Content>
                                    <xsl:value-of select="pogues:Questionnaire/pogues:Label/text()"/>
                                </r:Content>
                            </r:Label>
                            <xsl:apply-templates select="pogues:Questionnaire/pogues:Declaration"/>
                            <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">Modele</d:TypeOfSequence>
                            <xsl:apply-templates select="pogues:Questionnaire/pogues:Child">
                                <xsl:with-param name="sequenceNumber"/>
                            </xsl:apply-templates>
                            <xsl:apply-templates select="pogues:Questionnaire/pogues:Control"/>
                        </d:Sequence>
                    </d:ControlConstructReference>
                </d:Instrument>
            </s:StudyUnit>
        </DDIInstance>
    </xsl:template>
    <xsl:template match="pogues:Child">
        <xsl:param name="sequenceNumber"/>
        <Text>ni séquence, ni question</Text>
        <xsl:message>enfant ni séquence, ni question</xsl:message>
    </xsl:template>
    <xsl:template match="pogues:Child[@xsi:type='SequenceType']">
        <xsl:param name="sequenceNumber"/>
        <xsl:variable name="depth" select="@depth"/>
        <xsl:variable name="depth1" select="$depth -1"/>
        <xsl:variable name="seqNum">
            <xsl:value-of select="concat($sequenceNumber,'-')"/>
            <xsl:number count="pogues:Child[@depth=$depth]" level="any" format="1" from="pogues:Child[@depth=$depth1]"/>
        </xsl:variable>
        <d:ControlConstructReference>
            <xsl:copy-of select="$monagence"/>
            <d:Sequence>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-SEQ',$seqNum)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:Label>
                    <r:Content xml:lang="fr-FR">
                        <xsl:if test="pogues:Label/text() !=''">
                            <xsl:call-template name="formatLabel">
                                <xsl:with-param name="myString" select="pogues:Label/text()"/>
                            </xsl:call-template>
                        </xsl:if>
                    </r:Content>
                </r:Label>
                <xsl:apply-templates select="pogues:Declaration"/>
                <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">
                    <xsl:choose>
                        <xsl:when test="$depth='0'">Modele</xsl:when>
                        <xsl:when test="$depth='1'">Module</xsl:when>
                        <xsl:when test="$depth='2'">Paragraphe</xsl:when>
                        <xsl:when test="$depth='3'">Groupe</xsl:when>
                        <xsl:otherwise>Profondeur non définie</xsl:otherwise>
                    </xsl:choose>
                </d:TypeOfSequence>
                <xsl:apply-templates select="pogues:Child">
                    <xsl:with-param name="sequenceNumber" select="$seqNum"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="pogues:Control"/>
            </d:Sequence>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>Sequence</r:TypeOfObject>
        </d:ControlConstructReference>
    </xsl:template>
    <xsl:template match="pogues:Child[@type='FilterType']">
        <xsl:param name="sequenceNumber"/>
        <xsl:variable name="numITE">
            <xsl:number count="pogues:Child[@type='FilterType']" level="any"/>
        </xsl:variable>
        <xsl:variable name="debutNomVariable" select="concat($agencemaj, '-',$enquete,'-ITEIP-',$numITE,'-')"/>
        <d:ControlConstructReference>
            <xsl:copy-of select="$monagence"/>
            <d:IfThenElse>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','ITE','-',$numITE)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <d:IfCondition>
                    <xsl:call-template name="Command">
                        <xsl:with-param name="expression" select="pogues:Expression"/>
                        <xsl:with-param name="debutNomVariable" select="$debutNomVariable"/>
                    </xsl:call-template>
                </d:IfCondition>
                <d:ThenConstructReference>
                    <xsl:copy-of select="$monagence"/>
                    <d:Sequence>
                        <xsl:copy-of select="$monagence"/>
                        <r:ID>
                            <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','SEQI','-',$numITE)"/>
                        </r:ID>
                        <r:Version>0.1.0</r:Version>
                        <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">Cachable</d:TypeOfSequence>
                        <xsl:apply-templates select="pogues:IfTrue/pogues:Child">
                            <xsl:with-param name="sequenceNumber" select="$sequenceNumber"/>
                        </xsl:apply-templates>
                    </d:Sequence>
                    <r:Version>0.1.0</r:Version>
                    <r:TypeOfObject>Sequence</r:TypeOfObject>
                </d:ThenConstructReference>
                <xsl:if test="pogues:IfFalse!=''">
                    <d:ElseConstructReference>
                        <xsl:copy-of select="$monagence"/>
                        <d:Sequence>
                            <xsl:copy-of select="$monagence"/>
                            <r:ID>
                                <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','SEQIE','-',$numITE)"/>
                            </r:ID>
                            <r:Version>0.1.0</r:Version>
                            <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">Cachable</d:TypeOfSequence>
                            <xsl:apply-templates select="pogues:IfFalse/pogues:Child">
                                <xsl:with-param name="sequenceNumber" select="$sequenceNumber"/>
                            </xsl:apply-templates>
                        </d:Sequence>
                        <r:Version>0.1.0</r:Version>
                        <r:TypeOfObject>Sequence</r:TypeOfObject>
                    </d:ElseConstructReference>
                </xsl:if>
            </d:IfThenElse>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>IfThenElse</r:TypeOfObject>
        </d:ControlConstructReference>
    </xsl:template>
    <xsl:template match="pogues:Child[@xsi:type='QuestionType']">
        <xsl:param name="sequenceNumber"/>
        <xsl:variable name="qccount">
            <xsl:value-of select="concat($sequenceNumber,'-')"/>
            <xsl:number count="pogues:Child[@xsi:type='QuestionType']" format="1" level="any" from="pogues:Child[@xsi:type='SequenceType']"/>
        </xsl:variable>
        <xsl:variable name="questionType">
            <xsl:choose>
                <xsl:when test="pogues:ResponseStructure">
                    <Name>d:QuestionGrid</Name>
                    <TypeOfObject>QuestionGrid</TypeOfObject>
                    <Structure>d:StructuredMixedGridResponseDomain</Structure>
                    <Sigle>QG</Sigle>
                </xsl:when>
                <xsl:otherwise>
                    <Name>d:QuestionItem</Name>
                    <TypeOfObject>QuestionItem</TypeOfObject>
                    <Structure>d:StructuredMixedResponseDomain</Structure>
                    <Sigle>QI</Sigle>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <d:ControlConstructReference>
            <xsl:copy-of select="$monagence"/>
            <d:QuestionConstruct>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-QC',$qccount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <xsl:apply-templates select="pogues:Declaration[@position='BEFORE_QUESTION_TEXT']"/>
                <r:QuestionReference>
                    <xsl:copy-of select="$monagence"/>
                    <xsl:element name="{$questionType/Name}">
                        <xsl:copy-of select="$monagence"/>
                        <r:ID>
                            <xsl:value-of select="concat($agencemaj, '-',$enquete,'-',$questionType/Sigle,$qccount)"/>
                        </r:ID>
                        <r:Version>0.1.0</r:Version>
                        <xsl:apply-templates select="pogues:Response" mode="outParameter">
                            <xsl:with-param name="qccount" select="$qccount"/>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="pogues:Response" mode="binding">
                            <xsl:with-param name="qccount" select="$qccount"/>
                        </xsl:apply-templates>
                        <d:QuestionText>
                            <d:LiteralText>
                                <d:Text xml:lang="fr-FR">
                                    <xsl:if test="pogues:Label/text() !=''">
                                        <xsl:call-template name="formatLabel">
                                            <xsl:with-param name="myString" select="pogues:Label/text()"/>
                                        </xsl:call-template>
                                    </xsl:if>
                                </d:Text>
                            </d:LiteralText>
                        </d:QuestionText>
                        <xsl:apply-templates select="pogues:ResponseStructure/pogues:Dimension[@dimensionType='PRIMARY']"/>
                        <xsl:apply-templates select="pogues:ResponseStructure/pogues:Dimension[@dimensionType='SECONDARY']"/>
                        <xsl:choose>
                            <xsl:when test="@questionType='MULTIPLE_CHOICE' or @questionType='TABLE'">
                                <xsl:element name="{$questionType/Structure}">
                                    <xsl:apply-templates select="pogues:Response" mode="responseDomain">
                                        <xsl:with-param name="qccount" select="$qccount"/>
                                    </xsl:apply-templates>
                                </xsl:element>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates select="pogues:Response/pogues:Datatype" mode="responseDomain">
                                    <xsl:with-param name="qccount" select="$qccount"/>
                                </xsl:apply-templates>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:apply-templates select="pogues:Declaration[not(@position='BEFORE_QUESTION_TEXT')]"/>
                    </xsl:element>
                    <r:Version>0.1.0</r:Version>
                    <r:TypeOfObject>
                        <xsl:value-of select="$questionType/TypeOfObject"/>
                    </r:TypeOfObject>
                </r:QuestionReference>
            </d:QuestionConstruct>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>QuestionConstruct</r:TypeOfObject>
        </d:ControlConstructReference>
        <xsl:apply-templates select="pogues:Control"/>
    </xsl:template>
    <xsl:template match="pogues:Response" mode="outParameter">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(preceding-sibling::pogues:Response)+1"/>
        <r:OutParameter isArray="false">
            <xsl:copy-of select="$monagence"/>
            <r:ID>
                <xsl:value-of select="concat($agencemaj, '-',$enquete,'-QOP',$qccount,'-',$opcount)"/>
            </r:ID>
            <r:Version>0.1.0</r:Version>
            <r:ParameterName>
                <r:String xml:lang="fr-FR">
                    <xsl:value-of select="../pogues:Name"/>
                    <xsl:if test="preceding-sibling::pogues:Response or following-sibling::pogues:Response">
                        <xsl:variable name="responsePosition">
                            <xsl:number count="pogues:Response" format="1" from="pogues:Child"/>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="../pogues:ResponseStructure/pogues:Dimension[@dimensionType='SECONDARY']"><!-- Il y a 2 dimensions -->
                                <xsl:variable name="dimension1Length">
                                    <xsl:value-of select="../pogues:ResponseStructure/pogues:Dimension[@dimensionType='PRIMARY']/@dimension1Length"/>
                                </xsl:variable>
                                <xsl:value-of select="concat('_',(($responsePosition -1) mod $dimension1Length)+1,                                                              '_',floor(($responsePosition -1) div $dimension1Length)+1)"/>
                            </xsl:when>
                            <xsl:otherwise><!-- Il n'y a qu'une Dimension -->
                                <xsl:value-of select="concat('_',$responsePosition)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                </r:String>
            </r:ParameterName>
        </r:OutParameter>
    </xsl:template>
    <xsl:template match="pogues:Response" mode="binding">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(preceding-sibling::pogues:Response)+1"/>
        <xsl:call-template name="binding">
            <xsl:with-param name="source" select="concat($agencemaj, '-',$enquete,'-RDOP',$qccount,'-',$opcount)"/>
            <xsl:with-param name="target" select="concat($agencemaj, '-',$enquete,'-QOP',$qccount,'-',$opcount)"/>
            <xsl:with-param name="typeOfObject">OutParameter</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="pogues:Response[not(../pogues:ResponseStructure)]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <d:ResponseDomainInMixed>
            <xsl:apply-templates select="pogues:Datatype" mode="responseDomain">
                <xsl:with-param name="qccount" select="$qccount"/>
            </xsl:apply-templates>
        </d:ResponseDomainInMixed>
    </xsl:template>
    <xsl:template match="pogues:Response[../pogues:ResponseStructure]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <d:GridResponseDomain>
            <xsl:apply-templates select="pogues:Datatype" mode="responseDomain">
                <xsl:with-param name="qccount" select="$qccount"/>
            </xsl:apply-templates>
            <d:GridAttachment>
                <d:CellCoordinatesAsDefined>
                    <xsl:variable name="responsePosition" select="position()"/>
                    <xsl:choose>
                        <xsl:when test="../pogues:ResponseStructure/pogues:Dimension[@dimensionType='SECONDARY']"><!-- Il y a 2 dimensions -->
                            <xsl:variable name="dimension1Length">
                                <xsl:value-of select="../pogues:ResponseStructure/pogues:Dimension[@dimensionType='PRIMARY']/@dimension1Length"/>
                            </xsl:variable>
                            <d:SelectDimension>
                                <xsl:attribute name="rank" select="1"/>
                                <xsl:attribute name="rangeMinimum" select="(($responsePosition -1) mod $dimension1Length)+1"/>
                                <xsl:attribute name="rangeMaximum" select="(($responsePosition -1) mod $dimension1Length)+1"/>
                            </d:SelectDimension>
                            <d:SelectDimension>
                                <xsl:attribute name="rank" select="2"/>
                                <xsl:attribute name="rangeMinimum" select="floor(($responsePosition -1) div $dimension1Length)+1"/>
                                <xsl:attribute name="rangeMaximum" select="floor(($responsePosition -1) div $dimension1Length)+1"/>
                            </d:SelectDimension>
                        </xsl:when>
                        <xsl:otherwise><!-- Il n'y a qu'une Dimension -->
                            <d:SelectDimension>
                                <xsl:attribute name="rank" select="1"/>
                                <xsl:attribute name="rangeMinimum" select="$responsePosition"/>
                                <xsl:attribute name="rangeMaximum" select="$responsePosition"/>
                            </d:SelectDimension>
                        </xsl:otherwise>
                    </xsl:choose>
                </d:CellCoordinatesAsDefined>
            </d:GridAttachment>
        </d:GridResponseDomain>
    </xsl:template><!-- Dimension : CodeList ou Roster -->
    <xsl:template match="pogues:Dimension">
        <xsl:variable name="minimumRequired" select="substring-before(@dynamic,'-')"/>
        <xsl:variable name="maximumAllowed" select="substring-after(@dynamic,'-')"/>
        <d:GridDimension displayCode="false" displayLabel="false">
            <xsl:attribute name="rank">
                <xsl:choose>
                    <xsl:when test="@dimensionType='PRIMARY'">1</xsl:when>
                    <xsl:when test="@dimensionType='SECONDARY'">2</xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:choose><!-- Dimension de type CodeList -->
                <xsl:when test="pogues:CodeListReference">
                    <xsl:variable name="dimensionCodeList" select="pogues:CodeListReference"/>
                    <d:CodeDomain>
                        <r:CodeListReference>
                            <xsl:copy-of select="$monagence"/>
                            <xsl:apply-templates select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$dimensionCodeList]"/>
                            <r:Version>0.1.0</r:Version>
                            <r:TypeOfObject>CodeList</r:TypeOfObject>
                        </r:CodeListReference>
                    </d:CodeDomain>
                </xsl:when><!-- Dimension de type Roster -->
                <xsl:otherwise>
                    <xsl:variable name="minimumRequired" select="substring-before(@dynamic,'-')"/>
                    <xsl:variable name="maximumAllowed" select="substring-after(@dynamic,'-')"/>
                    <xsl:element name="d:Roster">
                        <xsl:attribute name="baseCodeValue">1</xsl:attribute>
                        <xsl:attribute name="codeIterationValue">1</xsl:attribute>
                        <xsl:attribute name="minimumRequired" select="$minimumRequired"/>
                        <xsl:if test="$maximumAllowed != ''">
                            <xsl:attribute name="maximumAllowed" select="$maximumAllowed"/>
                        </xsl:if>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
        </d:GridDimension>
    </xsl:template>
    <xsl:template match="pogues:Datatype[@typeName='DATE'          and not (exists(../pogues:CodeListReference))]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(../preceding-sibling::pogues:Response)+1"/>
        <d:DateTimeDomainReference>
            <r:Agency>fr.insee</r:Agency>
            <r:ID>INSEE-COMMUN-MNR-DateTimedate</r:ID>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>ManagedDateTimeRepresentation</r:TypeOfObject>
            <r:OutParameter isArray="false">
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-RDOP',$qccount,'-',$opcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:DateTimeRepresentationReference>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ManagedDateTimeRepresentation>
                        <xsl:copy-of select="$monagence"/>
                        <r:ID>INSEE-COMMUN-MNR-DateTimedate</r:ID>
                        <r:Version>0.1.0</r:Version>
                        <r:DateFieldFormat>jj/mm/aaaa</r:DateFieldFormat>
                        <r:DateTypeCode codeListID="DARES-DTC-CV">date</r:DateTypeCode>
                    </r:ManagedDateTimeRepresentation>
                    <r:Version>0.1.0</r:Version>
                    <r:TypeOfObject>ManagedDateTimeRepresentation</r:TypeOfObject>
                </r:DateTimeRepresentationReference>
            </r:OutParameter>
        </d:DateTimeDomainReference>
    </xsl:template>
    <xsl:template match="pogues:Datatype[@typeName='TEXT'          and not (exists(../pogues:CodeListReference))]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(../preceding-sibling::pogues:Response)+1"/>
        <xsl:variable name="longMax" select="pogues:MaxLength/text()"/>
        <d:TextDomain maxLength="{$longMax}">
            <xsl:if test="exists(pogues:Pattern/text())">
                <xsl:attribute name="regExp">
                    <xsl:value-of select="pogues:Pattern/text()"/>
                </xsl:attribute>
            </xsl:if>
            <r:OutParameter isArray="false">
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-RDOP',$qccount,'-',$opcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:TextRepresentation maxLength="{$longMax}"/>
            </r:OutParameter>
        </d:TextDomain>
    </xsl:template>
    <xsl:template match="pogues:Datatype[@typeName='NUMERIC'         and not (exists(../pogues:CodeListReference))]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(../preceding-sibling::pogues:Response)+1"/>
        <xsl:variable name="numericRepresentation"><!-- Si on a un élément qui permet de désigner un MNR, alors on l'utilise, sinon : -->
            <r:NumberRange>
                <r:Low isInclusive="true">
                    <xsl:choose>
                        <xsl:when test="pogues:Minimum">
                            <xsl:value-of select="pogues:Minimum"/>
                        </xsl:when>
                        <xsl:when test="../pogues:Minimum">
                            <xsl:value-of select="../pogues:Minimum"/>
                        </xsl:when>
                        <xsl:otherwise>0</xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="pogues:Decimals &gt; 0">
                        <xsl:value-of select="'.'"/>
                        <xsl:for-each select="1 to pogues:Decimals">
                            <xsl:value-of select="'0'"/>
                        </xsl:for-each>
                    </xsl:if>
                </r:Low>
                <r:High isInclusive="true">
                    <xsl:choose>
                        <xsl:when test="pogues:Maximum">
                            <xsl:value-of select="pogues:Maximum"/>
                        </xsl:when>
                        <xsl:when test="../pogues:Maximum">
                            <xsl:value-of select="../pogues:Maximum"/>
                        </xsl:when>
                        <xsl:otherwise>999999999</xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="pogues:Decimals &gt; 0">
                        <xsl:value-of select="'.'"/>
                        <xsl:for-each select="1 to pogues:Decimals">
                            <xsl:value-of select="'0'"/>
                        </xsl:for-each>
                    </xsl:if>
                </r:High>
            </r:NumberRange>
        </xsl:variable>
        <d:NumericDomain>
            <xsl:if test="pogues:Decimals &gt; 0">
                <xsl:attribute name="decimalPositions">
                    <xsl:value-of select="pogues:Decimals"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="$numericRepresentation"/>
            <r:NumericTypeCode codeListID="INSEE-CIS-NTC-CV">Format Numérique</r:NumericTypeCode>
            <r:OutParameter isArray="false">
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-RDOP',$qccount,'-',$opcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
            </r:OutParameter>
        </d:NumericDomain>
    </xsl:template>
    <xsl:template match="pogues:Datatype[@typeName='BOOLEAN'         and not (exists(../pogues:CodeListReference))]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(../preceding-sibling::pogues:Response)+1"/>
        <d:NominalDomain>
            <r:GenericOutputFormat>
                <xsl:attribute name="codeListID" select="concat($agencemaj,'-GOF-CV')"/>caseacocher</r:GenericOutputFormat>
            <r:OutParameter isArray="false">
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-RDOP',$qccount,'-',$opcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:CodeRepresentation>
                    <r:CodeSubsetInformation>
                        <r:IncludedCode>
                            <r:CodeReference>
                                <xsl:copy-of select="$monagence"/>
                                <l:Code levelNumber="1" isDiscrete="true">
                                    <xsl:copy-of select="$monagence"/>
                                    <r:ID>INSEE-COMMUN-CL-Booleen-1</r:ID>
                                    <r:Version>0.1.0</r:Version>
                                    <r:CategoryReference>
                                        <xsl:copy-of select="$monagence"/>
                                        <l:Category>
                                            <xsl:copy-of select="$monagence"/>
                                            <r:ID>INSEE-COMMUN-CA-Booleen-1</r:ID>
                                            <r:Version>0.1.0</r:Version>
                                            <r:Label>
                                                <r:Content xml:lang="fr-FR">
                                                    <xsl:if test="pogues:Label/text() !=''">
                                                        <xsl:call-template name="formatLabel">
                                                            <xsl:with-param name="myString" select="pogues:Label/text()"/>
                                                        </xsl:call-template>
                                                    </xsl:if>
                                                </r:Content>
                                            </r:Label>
                                        </l:Category>
                                        <r:Version>0.1.0</r:Version>
                                        <r:TypeOfObject>Category</r:TypeOfObject>
                                    </r:CategoryReference>
                                    <r:Value>1</r:Value>
                                </l:Code>
                                <r:Version>0.1.0</r:Version>
                                <r:TypeOfObject>Code</r:TypeOfObject>
                            </r:CodeReference>
                        </r:IncludedCode>
                    </r:CodeSubsetInformation>
                </r:CodeRepresentation>
                <r:DefaultValue/>
            </r:OutParameter>
            <r:ResponseCardinality maximumResponses="1"/>
        </d:NominalDomain>
    </xsl:template>
    <xsl:template match="pogues:Datatype[exists(../pogues:CodeListReference)]" mode="responseDomain">
        <xsl:param name="qccount"/>
        <xsl:variable name="opcount" select="count(../preceding-sibling::pogues:Response)+1"/>
        <xsl:variable name="listeCodes" select="../pogues:CodeListReference"/>
        <xsl:variable name="typeAffichageListe">
            <xsl:choose>
                <xsl:when test="@visualizationHint='CHECKBOX'">caseacocher</xsl:when>
                <xsl:when test="@visualizationHint='RADIO'">boutonradio</xsl:when>
                <xsl:when test="@visualizationHint='DROPDOWN'">listederoulante</xsl:when>
                <xsl:otherwise>type d'affichage liste de réponses non définie</xsl:otherwise>
            </xsl:choose>
        </xsl:variable><!--        <xsl:variable name="libListeCodes"
            select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$listeCodes]/pogues:Name/text()"/>
-->
        <xsl:variable name="libListeCodes">
            <xsl:choose>
                <xsl:when test="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$listeCodes]/pogues:Name/text() != ''">
                    <xsl:value-of select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$listeCodes]/pogues:Name/text()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="replace(//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$listeCodes]/pogues:Label/text(),' ','_')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <d:CodeDomain>
            <r:GenericOutputFormat codeListID="{$agencemaj}'-GOF-CV'">
                <xsl:value-of select="$typeAffichageListe"/>
            </r:GenericOutputFormat>
            <r:CodeListReference>
                <xsl:copy-of select="$monagence"/>
                <xsl:apply-templates select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=$listeCodes]"/>
                <r:Version>0.1.0</r:Version>
                <r:TypeOfObject>CodeList</r:TypeOfObject>
            </r:CodeListReference>
            <r:OutParameter isArray="false">
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','RDOP',$qccount,'-',$opcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:CodeRepresentation>
                    <r:CodeListReference>
                        <xsl:copy-of select="$monagence"/>
                        <r:ID>
                            <xsl:value-of select="concat($agencemaj, '-',$enquete,'-CL-',$libListeCodes)"/>
                        </r:ID>
                        <r:Version>0.1.0</r:Version>
                        <r:TypeOfObject>CodeList</r:TypeOfObject>
                    </r:CodeListReference>
                </r:CodeRepresentation>
            </r:OutParameter><!-- Ici, il faudra le vrai nombre de réponses attendu -->
            <r:ResponseCardinality maximumResponses="1"/>
        </d:CodeDomain>
    </xsl:template>
    <xsl:template match="pogues:CodeList">
        <xsl:variable name="libListeCodes">
            <xsl:choose>
                <xsl:when test="pogues:Name/text() != ''">
                    <xsl:value-of select="pogues:Name/text()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="replace(pogues:Label/text(),' ','_')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <l:CodeList>
            <xsl:copy-of select="$monagence"/>
            <r:ID>
                <xsl:value-of select="concat($agencemaj, '-',$enquete,'-CL-',$libListeCodes)"/>
            </r:ID>
            <r:Version>0.1.0</r:Version>
            <r:Label>
                <r:Content xml:lang="fr-FR">
                    <xsl:value-of select="pogues:Label"/>
                </r:Content>
            </r:Label>
            <l:HierarchyType>Regular</l:HierarchyType>
            <l:Level levelNumber="1">
                <l:CategoryRelationship>Ordinal</l:CategoryRelationship>
            </l:Level>
            <xsl:apply-templates select="pogues:Code">
                <xsl:with-param name="libListeCodes" select="$libListeCodes"/>
            </xsl:apply-templates>
        </l:CodeList>
    </xsl:template>
    <xsl:template match="pogues:Code">
        <xsl:param name="libListeCodes"/>
        <l:Code levelNumber="1" isDiscrete="true">
            <xsl:copy-of select="$monagence"/>
            <r:ID>
                <xsl:value-of select="concat($agencemaj, '-',$enquete,'-CL-',$libListeCodes,'-',position())"/>
            </r:ID>
            <r:Version>0.1.0</r:Version>
            <r:CategoryReference>
                <xsl:copy-of select="$monagence"/>
                <l:Category>
                    <xsl:copy-of select="$monagence"/>
                    <r:ID>
                        <xsl:value-of select="concat($agencemaj, '-',$enquete,'-CA-',$libListeCodes,'-',position())"/>
                    </r:ID>
                    <r:Version>0.1.0</r:Version>
                    <r:Label>
                        <r:Content xml:lang="fr-FR">
                            <xsl:if test="pogues:Label/text() !=''">
                                <xsl:call-template name="formatLabel">
                                    <xsl:with-param name="myString" select="pogues:Label/text()"/>
                                </xsl:call-template>
                            </xsl:if>
                        </r:Content>
                    </r:Label>
                </l:Category>
                <r:Version>0.1.0</r:Version>
                <r:TypeOfObject>Category</r:TypeOfObject>
            </r:CategoryReference>
            <r:Value>
                <xsl:value-of select="pogues:Value"/>
            </r:Value>
        </l:Code>
    </xsl:template>
    <xsl:template match="pogues:Datatype" mode="responseDomain">
        <Text>format de réponse non reconnu</Text>
        <xsl:message>format de réponse non reconnu</xsl:message>
    </xsl:template>
    <xsl:template match="pogues:Control">
        <xsl:variable name="CIcount" select="count(preceding::pogues:Control)+1"/>
        <xsl:variable name="debutNomVariable" select="concat($agencemaj, '-',$enquete,'-CIIP-',$CIcount,'-')"/>
        <d:ControlConstructReference>
            <xsl:copy-of select="$monagence"/>
            <d:ComputationItem>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','CI','-',$CIcount)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <xsl:apply-templates select="pogues:FailMessage">
                    <xsl:with-param name="idCI">
                        <xsl:value-of select="concat('CI-',$CIcount)"/>
                    </xsl:with-param>
                </xsl:apply-templates>
                <r:CommandCode>
                    <xsl:call-template name="Command">
                        <xsl:with-param name="expression" select="pogues:Expression"/>
                        <xsl:with-param name="debutNomVariable" select="$debutNomVariable"/>
                    </xsl:call-template>
                </r:CommandCode>
            </d:ComputationItem>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>ComputationItem</r:TypeOfObject>
        </d:ControlConstructReference>
    </xsl:template>
    <xsl:template match="pogues:FailMessage">
        <xsl:param name="idCI"/>
        <d:InterviewerInstructionReference>
            <xsl:copy-of select="$monagence"/>
            <d:Instruction>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-','II-IC','-',$idCI)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <d:InstructionName>
                    <r:String xml:lang="fr-FR">Consigne</r:String>
                </d:InstructionName>
                <d:InstructionText>
                    <d:LiteralText>
                        <d:Text xml:lang="fr-FR">
                            <xsl:if test="text() !=''">
                                <xsl:call-template name="formatLabel">
                                    <xsl:with-param name="myString" select="text()"/>
                                </xsl:call-template>
                            </xsl:if>
                        </d:Text>
                    </d:LiteralText>
                </d:InstructionText>
            </d:Instruction>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>Instruction</r:TypeOfObject>
        </d:InterviewerInstructionReference>
    </xsl:template>
    <xsl:template match="pogues:Declaration">
        <xsl:variable name="instructionTypeID">
            <xsl:choose>
                <xsl:when test="(@declarationType='INSTRUCTION')">II</xsl:when>
                <xsl:when test="(@declarationType='HELP')">IH</xsl:when>
                <xsl:when test="(@declarationType='COMMENT')">IC</xsl:when>
                <xsl:when test="(@declarationType='WARNING')">IW</xsl:when>
                <xsl:otherwise>ERREUR</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="instructionTypeLabel">
            <xsl:choose>
                <xsl:when test="(@declarationType='INSTRUCTION')">Consigne</xsl:when>
                <xsl:when test="(@declarationType='HELP')">Aide</xsl:when>
                <xsl:when test="(@declarationType='COMMENT')">Commentaire</xsl:when>
                <xsl:when test="(@declarationType='WARNING')">Attention</xsl:when>
                <xsl:otherwise>ERREUR : type de déclaration inconnu</xsl:otherwise>
            </xsl:choose>
        </xsl:variable><!-- Cette variable calcule la position de la première déclaration qui a le même contenu que celle en cours
                Ceci permet de fusionner les déclarations en doublon -->
        <xsl:variable name="insNum">
            <xsl:variable name="idDeclaration" select="concat(pogues:Text/text(),@declarationType)"/>
            <xsl:apply-templates select="//pogues:Declaration[generate-id(.)=generate-id(key('DeclarationList',$idDeclaration)[1])]" mode="declarationPosition"/>
        </xsl:variable>
        <d:InterviewerInstructionReference>
            <xsl:copy-of select="$monagence"/>
            <d:Instruction>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="concat($agencemaj, '-',$enquete,'-', $instructionTypeID,'-',$insNum)"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <d:InstructionName>
                    <r:String xml:lang="fr-FR">
                        <xsl:if test="$instructionTypeLabel !=''">
                            <xsl:value-of select="$instructionTypeLabel"/>
                        </xsl:if>
                    </r:String>
                </d:InstructionName>
                <d:InstructionText>
                    <d:LiteralText>
                        <d:Text xml:lang="fr-FR">
                            <xsl:call-template name="formatLabel">
                                <xsl:with-param name="myString" select="pogues:Text/text()"/>
                            </xsl:call-template>
                        </d:Text>
                    </d:LiteralText>
                </d:InstructionText>
            </d:Instruction>
            <r:Version>0.1.0</r:Version>
            <r:TypeOfObject>Instruction</r:TypeOfObject>
        </d:InterviewerInstructionReference>
    </xsl:template>
    <xsl:template match="pogues:Declaration" mode="declarationPosition">
        <xsl:number count="pogues:Declaration" level="any"/>
    </xsl:template>
    <xsl:template name="Command">
        <xsl:param name="expression"/>
        <xsl:param name="debutNomVariable"/><!-- on supprime les caractères avant le premier ${ et après le dernier }, 
            puis en remplace chaque zone entre } et ${ par un caractère d'espacement -->
        <xsl:variable name="listeVar">
            <xsl:value-of select="replace(replace(replace($expression,                                                           '^[^\$\{]*\$\{', ''),                                                           '\}[^\$\{]*\$\{',' '),                                                           '\}[^\$\{]*$','')"/>
        </xsl:variable><!-- suppression des doublons et rajout du nom de la variable DDI -->
        <xsl:variable name="tokenVarList">
            <Liste>
                <xsl:for-each select="distinct-values(tokenize($listeVar,' '))">
                    <xsl:variable name="nbZero" select="string-length(string(count(distinct-values(tokenize($listeVar,' ')))))                         -string-length(string(position()))"/>
                    <Var>
                        <xsl:attribute name="num">
                            <xsl:choose>
                                <xsl:when test="$nbZero=0">
                                    <xsl:value-of select="concat($debutNomVariable,position())"/>
                                </xsl:when>
                                <xsl:when test="$nbZero=1">
                                    <xsl:value-of select="concat($debutNomVariable,'0',position())"/>
                                </xsl:when>
                                <xsl:when test="$nbZero=2">
                                    <xsl:value-of select="concat($debutNomVariable,'00',position())"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat($debutNomVariable,'000',position())"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:value-of select="."/>
                    </Var>
                </xsl:for-each>
            </Liste>
        </xsl:variable>
        <r:Command>
            <r:ProgramLanguage>xpath</r:ProgramLanguage>
            <xsl:for-each select="$tokenVarList/Liste/Var">
                <xsl:call-template name="inParameter">
                    <xsl:with-param name="numParametre" select="@num"/>
                </xsl:call-template>
            </xsl:for-each>
            <xsl:for-each select="$tokenVarList/Liste/Var">
                <xsl:call-template name="binding">
                    <xsl:with-param name="source" select="concat($agencemaj, '-',$enquete,'-QOP-',translate(.,'SQR',''))"/>
                    <xsl:with-param name="target" select="@num"/>
                    <xsl:with-param name="typeOfObject">InParameter</xsl:with-param>
                </xsl:call-template>
            </xsl:for-each><!-- Appel à la fonction de remplacement --><!-- Ne pas mettre la parenthèse fermante dans la zone remplacée, car sinon, elle se retrouve en double dans l'expression finale -->
            <r:CommandContent>
                <xsl:value-of select="replace(pogues:replaceVarExpression($tokenVarList,$expression),                     'NUM\(([^\)]*)\)', 'number(if ($1='''') then ''0'' else $1)')"/>
            </r:CommandContent>
        </r:Command>
    </xsl:template>
    <xsl:template name="binding">
        <xsl:param name="source"/>
        <xsl:param name="target"/>
        <xsl:param name="typeOfObject"/>
        <r:Binding>
            <r:SourceParameterReference>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="$source"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:SourceParameterReference>
            <r:TargetParameterReference>
                <xsl:copy-of select="$monagence"/>
                <r:ID>
                    <xsl:value-of select="$target"/>
                </r:ID>
                <r:Version>0.1.0</r:Version>
                <r:TypeOfObject>
                    <xsl:value-of select="$typeOfObject"/>
                </r:TypeOfObject>
            </r:TargetParameterReference>
        </r:Binding>
    </xsl:template>
    <xsl:template name="inParameter">
        <xsl:param name="numParametre"/>
        <r:InParameter isArray="false">
            <xsl:copy-of select="$monagence"/>
            <r:ID>
                <xsl:value-of select="$numParametre"/>
            </r:ID>
            <r:Version>0.1.0</r:Version>
            <r:ParameterName>
                <r:String xml:lang="fr-FR"/>
            </r:ParameterName>
        </r:InParameter>
    </xsl:template><!-- Fonction qui remplace les variables définies dans les formules par leur équivalent DDI -->
    <xsl:function name="pogues:replaceVarExpression" as="xs:string">
        <xsl:param name="listeVar"/>
        <xsl:param name="expression"/>
        <xsl:variable name="quot">"</xsl:variable>
        <xsl:variable name="apos">'</xsl:variable>
        <xsl:choose>
            <xsl:when test="not($listeVar/Liste/Var)">
                <xsl:sequence select="$expression"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="ListeVar2">
                    <Liste>
                        <xsl:copy-of select="$listeVar/Liste/Var[position() &gt; 1]"/>
                    </Liste>
                </xsl:variable>
                <xsl:sequence select="pogues:replaceVarExpression($ListeVar2,                                                                     replace(translate($expression,$quot,$apos),                                                                          concat('\$\{',$listeVar/Liste/Var[1]/text(),'\}'),                                                                         $listeVar/Liste/Var[1]/@num)                                                                   )"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function><!-- Teste s'il y a besoin de mise en forme et rajoute les balises xhtml:p autour du libellé mis en forme -->
    <xsl:template name="formatLabel">
        <xsl:param name="myString"/>
        <xsl:variable name="myString2">
            <xsl:choose>
                <xsl:when test="starts-with($myString,'##{')">
                    <xsl:value-of select="replace($myString,'^.*\} ','')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$myString"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable><!--<xsl:value-of select="$myString2"/>-->
        <xsl:variable name="myFormattedString" select="pogues:format2($myString2)"/>
        <xsl:choose>
            <xsl:when test="$myFormattedString = $myString2">
                <xsl:value-of select="$myString2"/>
            </xsl:when>
            <xsl:otherwise>
                <xhtml:p>
                    <xsl:value-of select="replace($myFormattedString,'&amp;','&amp;amp;')" disable-output-escaping="yes"/>
                </xhtml:p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template><!-- Met le libellé en forme de manière récursive -->
    <xsl:function name="pogues:format2" as="xs:string">
        <xsl:param name="myString"/>
        <xsl:choose><!-- Italique -->
            <xsl:when test="contains($myString,'_') and contains(substring-after($myString,'_'),'_')">
                <xsl:value-of select="pogues:format2(replace($myString, '_(.*?)_', '&lt;xhtml:i&gt;$1&lt;/xhtml:i&gt;'))"/>
            </xsl:when><!-- Gras -->
            <xsl:when test="contains($myString,'**') and contains(substring-after($myString,'**'),'**')">
                <xsl:value-of select="pogues:format2(replace($myString, '\*\*(.*?)\*\*', '&lt;xhtml:b&gt;$1&lt;/xhtml:b&gt;'))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$myString"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
</xsl:stylesheet>