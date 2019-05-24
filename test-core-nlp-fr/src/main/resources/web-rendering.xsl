<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:template match="*">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="publication-sans-sommaire">
        <html lang="fr">
            <head>
                <title><xsl:value-of select="titre"/></title>
                <link rel="stylesheet" href="web-rendering.css"/>
            </head>
            <body bgcolor="#ffffff">
                <xsl:apply-templates select="titre | chapo | blocs//paragraphes | sources//paragraphes | definitions//paragraphes | blocs/bloc/figure//titre"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="titre">
        <h2 align="center"> <xsl:apply-templates/> </h2>
    </xsl:template>

    <xsl:template match="paragraphe">
        <p align="center"> <xsl:apply-templates/> </p>
    </xsl:template>

    <xsl:template match="STAT-CPT">
        <mark class="ner">
            <xsl:apply-templates/>
            (<a href="{@uri}">
              <xsl:value-of select="@id"/>
            </a>)
        </mark>
    </xsl:template>
</xsl:stylesheet>