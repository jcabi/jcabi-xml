<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:j="http://www.jcabi.com"
    version="2.0" exclude-result-prefixes="j">
    <xsl:include href="second.xsl"/>
    <xsl:template match="/">
        <result>
            <xsl:value-of select="j:format('5.76')"/>
        </result>
    </xsl:template>
</xsl:stylesheet>
