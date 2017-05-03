<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="com.liferay.portal.security.auth.AuthTokenUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.accountmgt.model.Business"%>
<%@page import="org.opencps.dossiermgt.search.DossierPartDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.service.DossierPartLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.backend.util.AutoFillFormData"%>
<%@page import="org.opencps.accountmgt.model.Citizen"%>
<%@page import="org.opencps.util.AccountUtil"%>
<%@page import="org.opencps.dossiermgt.bean.AccountBean"%>
<%
/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
%>

<%@ include file="/init.jsp"%>

<%
	long dossierPartId = ParamUtil.getLong(request,
			DossierPartDisplayTerms.DOSSIERPART_DOSSIERPARTID, 0);

	String formData = StringPool.BLANK;
	String sampleData = StringPool.BLANK;
	String alpacaSchema = StringPool.BLANK;

	String auTock = AuthTokenUtil.getToken(request);

	if (dossierPartId > 0) {

		DossierPart dossierPart = DossierPartLocalServiceUtil
				.getDossierPart(dossierPartId);

		alpacaSchema = dossierPart != null
				&& Validator.isNotNull(dossierPart.getFormScript()) ? dossierPart
				.getFormScript().replaceAll(
						"\\?p_auth=REPLACEKEY",
						"/group-id/" + themeDisplay.getScopeGroupId()
								+ "?p_auth=" + auTock)
				: StringPool.BLANK;

		Citizen ownerCitizen = null;
		Business ownerBusiness = null;

		formData = AutoFillFormData.dataBinding(sampleData,
				ownerCitizen, ownerBusiness, 0);

	}
%>

<aui:form name="fm" method="post">

	<div id="dynamicForm"></div>
	
	<aui:fieldset>
		<c:if test="<%=Validator.isNotNull(alpacaSchema) %>">
			<aui:button type="button" value="save" name="save" cssClass="saveForm"/>
		</c:if>
	</aui:fieldset>

</aui:form>

<aui:script>

var alpacaSchema = <%=Validator.isNotNull(alpacaSchema) ? alpacaSchema : PortletConstants.UNKNOW_ALPACA_SCHEMA%>;
var formData = '<%=formData%>';

AUI().ready(function(A){
	
	if(alpacaSchema.options != 'undefined' && alpacaSchema.schema != 'undefined'){
		
		if(formData != ''){
			alpacaSchema.data = JSON.parse(formData);
		}
		
		//Overwrite function
		alpacaSchema.postRender = function(control){
			$(".saveForm").click(function(e) {
				//Liferay.Util.getOpener().Liferay.fire('turnOnOverlaymask');
				var formData = control.getValue();
				$("#<portlet:namespace />formData" ).val(JSON.stringify(formData));
				
				//Validate form submit
				var errorMessage = '';

				$('div[class*="has-error"] :input').each(function( index ) {
					
					if($(this).val() != null){
						errorMessage += ( index + 1 ) + '\n';
					}
				  
				});
				
				//console.log("Alpacajs-required: "+errorMessage);
				
				if(errorMessage.length == 0){
				
					$("#<portlet:namespace />fm" ).submit();
				
				}else{
					
					alert( '<%=LanguageUtil.get(pageContext, "fields-required") %>');
					
				}
				
		    });
			
			$(".alpaca-field-table").delegate('select.alpaca-control', 'change', function(){   
				  var listbox = $('#'+$(this).attr('id') + ' option:selected');
				  var idText = $(this).attr('name') + "Text";
				  var hiddenInput = $("input[name='"+idText+"']");
				  hiddenInput.val(listbox.text());
				});
		};
	
	}
	var el = $("#dynamicForm");
	
	Alpaca(el, alpacaSchema);
});
</aui:script>