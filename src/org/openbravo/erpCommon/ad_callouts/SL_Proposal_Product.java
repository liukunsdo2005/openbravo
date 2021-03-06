/*
 *************************************************************************
 * The contents of this file are subject to the Openbravo  Public  License
 * Version  1.1  (the  "License"),  being   the  Mozilla   Public  License
 * Version 1.1  with a permitted attribution clause; you may not  use this
 * file except in compliance with the License. You  may  obtain  a copy of
 * the License at http://www.openbravo.com/legal/license.html 
 * Software distributed under the License  is  distributed  on  an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific  language  governing  rights  and  limitations
 * under the License. 
 * The Original Code is Openbravo ERP. 
 * The Initial Developer of the Original Code is Openbravo SLU 
 * All portions are Copyright (C) 2001-2010 Openbravo SLU 
 * All Rights Reserved. 
 * Contributor(s):  ______________________________________.
 ************************************************************************
 */
package org.openbravo.erpCommon.ad_callouts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openbravo.base.secureApp.HttpSecureAppServlet;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.utils.FormatUtilities;
import org.openbravo.xmlEngine.XmlDocument;

public class SL_Proposal_Product extends HttpSecureAppServlet {
  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
      ServletException {
    VariablesSecureApp vars = new VariablesSecureApp(request);
    if (vars.commandIn("DEFAULT")) {
      String strChanged = vars.getStringParameter("inpLastFieldChanged");
      if (log4j.isDebugEnabled())
        log4j.debug("CHANGED: " + strChanged);
      String strPriceStd = vars.getNumericParameter("inpmProductId_PSTD");
      String strTabId = vars.getStringParameter("inpTabId");
      String strmProductId = vars.getStringParameter("inpmProductId");
      try {
        printPage(response, vars, strPriceStd, strTabId, strmProductId);
      } catch (ServletException ex) {
        pageErrorCallOut(response);
      }
    } else
      pageError(response);
  }

  private void printPage(HttpServletResponse response, VariablesSecureApp vars, String strPriceStd,
      String strTabId, String strmProductId) throws IOException, ServletException {
    if (log4j.isDebugEnabled())
      log4j.debug("Output: dataSheet");
    XmlDocument xmlDocument = xmlEngine.readXmlTemplate(
        "org/openbravo/erpCommon/ad_callouts/CallOut").createXmlDocument();

    StringBuffer resultado = new StringBuffer();
    SLProposalProductData[] data = null;
    if (strmProductId != null && !strmProductId.equals("")) {
      data = SLProposalProductData.select(this, strmProductId);
    } else {
      data = SLProposalProductData.set();
    }
    resultado.append("var calloutName='SL_Proposal_Product';\n\n");
    resultado.append("var respuesta = new Array(");
    if (log4j.isDebugEnabled())
      log4j.debug("strPriceStd*******************" + strPriceStd);
    resultado.append("new Array(\"inpprice\", " + (strPriceStd.equals("") ? "0" : strPriceStd)
        + "),");
    resultado.append("new Array(\"inpproductValue\", \"" + FormatUtilities.replaceJS(data[0].value)
        + "\"),\n");
    resultado.append("new Array(\"inpproductName\", \"" + FormatUtilities.replaceJS(data[0].name)
        + "\"),\n");
    resultado.append("new Array(\"inpproductDescription\", \""
        + FormatUtilities.replaceJS(data[0].description) + "\")");

    resultado.append(");");
    xmlDocument.setParameter("array", resultado.toString());
    xmlDocument.setParameter("frameName", "appFrame");
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println(xmlDocument.print());
    out.close();
  }
}
