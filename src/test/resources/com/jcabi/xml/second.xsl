<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:j="http://www.jcabi.com" version="2.0" exclude-result-prefixes="j">
  <xsl:template name="j:format">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value, '0')"/>
  </xsl:template>
</xsl:stylesheet>
