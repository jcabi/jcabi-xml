<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:j="http://www.jcabi.com"
    version="2.0" exclude-result-prefixes="j">
    <xsl:function name="j:format">
        <xsl:param name="value"/>
        <xsl:value-of select="format-number($value, '0')"/>
    </xsl:function>
</xsl:stylesheet>
