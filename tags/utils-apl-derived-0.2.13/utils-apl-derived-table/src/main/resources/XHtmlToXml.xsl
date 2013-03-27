<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/table">
        <table>
            <metaData>
                <tableName>
                    <xsl:value-of select="@id" />
                </tableName>
                <columnTitles>
                    <xsl:for-each select="thead/tr/th">
                        <columnTitle>
                            <xsl:value-of select="." />
                        </columnTitle>
                    </xsl:for-each>
                </columnTitles>
            </metaData>
            <rows>
                <xsl:for-each select="tbody/tr|tr">
                    <row>
                        <xsl:for-each select="td">
                            <string>
                                <xsl:value-of select="." />
                            </string>
                        </xsl:for-each>
                    </row>
                </xsl:for-each>
            </rows>
        </table>
    </xsl:template>

</xsl:stylesheet> 