<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!-- TODO : remplir... -->
    <!-- root :  -->
    <xsl:template match="publication-sans-sommaire">
            <xsl:apply-templates select="chapo/paragraphe"/>

            <xsl:apply-templates select="blocs/*//paragraphe"/>
    </xsl:template>
</xsl:stylesheet>