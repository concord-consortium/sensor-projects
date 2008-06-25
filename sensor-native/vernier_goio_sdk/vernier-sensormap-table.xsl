<?xml version="1.0"?>
<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html>
      <body>
      <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="SensorMap">
	<table rules="all" frame="box" >
	  <tr>
	      <th>LocalName</th>
	      <th>Product</th>
	      <th>ID(s)</th>
	      <th>Legacy</th>
	      <th>SensorFamily</th>
	      <th>sname_base</th>
	      <th>Calibrations</th>
      </tr>
      <xsl:apply-templates>
	  <xsl:sort select="sname_base"/>
	</xsl:apply-templates>
	</table>
  </xsl:template>

  <xsl:template match="Sensor">
     <tr><td><xsl:value-of select="LocalName"/></td>
	 <td><xsl:value-of select="@Product"/></td>
	 <td>
	 <!-- with xpath 2.0 the following is possible but since that isn't support the mess below is needed 
	 <xsl:value-of select="distinct-values(CalibrationList/Calibration/@IDValue)" separator=", "/>
	  -->
 <xsl:for-each select="CalibrationList/Calibration[not(@IDValue=preceding-sibling::Calibration/@IDValue)]">
              <xsl:value-of select="@IDValue"/>
              <xsl:if test="position()!=last()">
                <br/>
              </xsl:if>
          </xsl:for-each>
 	 </td>
	 <td><xsl:value-of select="@Legacy"/></td>
	 <td><xsl:value-of select="SensorFamily"/></td>
	 <td><xsl:value-of select="sname_base"/></td>
	 <td><table>
		<xsl:apply-templates select="CalibrationList/Calibration"/>
		</table>
	 </td>
	</tr>
  </xsl:template>

  <xsl:template match="Calibration">
	<tr>
	    <td><xsl:value-of select="@UserName"/></td>
		<td><xsl:value-of select="@Units"/></td>
		<td><xsl:value-of select="Equation/@Type"/></td>
		<xsl:if test="Equation/@Type = 1">
			<td><xsl:value-of select="0.002 * Equation/@K1"/>
			<xsl:value-of select="@Units"/></td>	
		</xsl:if>
            <td>params: <xsl:value-of select="Equation/@K0"/>, 
            <xsl:value-of select="Equation/@K1"/>, 
            <xsl:value-of select="Equation/@K2"/></td>
      </tr>
  </xsl:template>
</xsl:stylesheet>