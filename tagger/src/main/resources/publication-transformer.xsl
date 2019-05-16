<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:nerpipeline="fr.insee.stamina.nlp.FrenchNERPipeline">
    <xsl:output indent="yes"/>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="normalize-space(.)"/>
    </xsl:template>

    <xsl:template match="paragraphe | titre">
        <xsl:copy>
            <xsl:value-of select="nerpipeline:run(.)" disable-output-escaping="yes"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>