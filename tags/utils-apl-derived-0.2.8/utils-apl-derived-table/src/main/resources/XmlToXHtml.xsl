<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/table">
        <table>
            <xsl:attribute name="id">
        <xsl:value-of select="metaData/tableName" />
        </xsl:attribute>
            <thead>
                <tr>
                    <xsl:for-each select="metaData/columnTitles/columnTitle">
                        <th>
                            <xsl:value-of select="." />
                        </th>
                    </xsl:for-each>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="rows/row">
                    <tr>
                        <xsl:for-each select="string">
                            <td>
                                <xsl:value-of select="." />
                            </td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>

</xsl:stylesheet> 