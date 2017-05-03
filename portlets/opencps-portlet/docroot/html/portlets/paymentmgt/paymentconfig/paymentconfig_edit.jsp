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
<%@ include file="../init.jsp"%>

<%
	long paymentConfigId = ParamUtil.getLong(request, PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID, 0L);
	PaymentConfig paymentConfig = new PaymentConfigImpl();
	
	if(paymentConfigId > 0){
		paymentConfig = PaymentConfigLocalServiceUtil.getPaymentConfig(paymentConfigId);
	}
	
	DataMgtUtils dataMgtUtils = new DataMgtUtils();
	List<DictItem> paymentGates = new ArrayList<DictItem>();
	
	paymentGates = dataMgtUtils.getDictItemList(themeDisplay.getScopeGroupId(), PortletPropsValues.DM_PAYMENT_GATE);
	
%>	

<div class="opencps-bound-wrapper pd20 default-box-shadow">

	<c:choose>
		<c:when test="<%= true %>">


			<portlet:actionURL var="updatePaymentConfigURL"
				name="updatePaymentConfigWithServiceProcess">
			</portlet:actionURL>

			<liferay-ui:success key="update-payment-config-success"
				message="update-payment-config-success" />

			<liferay-ui:error key="update-payment-config-error"
				message="update-payment-config-error" />

			<aui:form action="<%=updatePaymentConfigURL.toString()%>"
				method="post" name="fm" id="fm">
				<aui:input type="hidden"
					id="<%=PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID%>"
					name="<%=PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID%>"
					value="<%=String.valueOf(paymentConfigId)%>" />

				<aui:row>
					<aui:col width="50">
						<aui:select
							id="<%=PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE%>"
							name="<%=PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE%>"
							label="payment-gate-type">
							<%
								for (DictItem paymentGate : paymentGates) {
									
									boolean selected = false;
									
									if(paymentGate.getDictItemId() == paymentConfig.getPaymentGateType()){
										selected = true;
									}
									
							%>
								<aui:option selected="<%=selected %>"
									value="<%=paymentGate.getDictItemId()%>"><%=paymentGate.getItemCode()%></aui:option>
							<%
								}
							%>
						</aui:select>
					</aui:col>
					
					<aui:col width="50">
						<aui:input 
							id="<%=PaymentConfigDisplayTerms.PAYMENT_STATUS%>"
							name="<%=PaymentConfigDisplayTerms.PAYMENT_STATUS%>"
							label="active"
							type="checkbox"
							checked="<%=paymentConfig.getStatus() %>"
							>
							
						</aui:input>
					</aui:col>
				</aui:row>
				
				<aui:row>
					<aui:col width="50">
						<aui:input name="<%=PaymentConfigDisplayTerms.PAYMENT_CONFIG_NO%>"
							id="<%=PaymentConfigDisplayTerms.PAYMENT_CONFIG_NO%>"
							cssClass="input100"
							label="payment-config-no"
							type="text"
							value="<%=paymentConfig.getPaymentConfigNo() %>">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getBankInfo()%>"
							id="<%=PaymentConfigDisplayTerms.BANK_INFO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.BANK_INFO%>" type="textarea"
							label="bank-info">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getPlaceInfo()%>"
							id="<%=PaymentConfigDisplayTerms.PLACE_INFO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.PLACE_INFO%>"
							type="textarea" label="place-info">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col>
						<div class="navbar">
							<div class="navbar-inner">
								<a class="brand"> <liferay-ui:message
										key="keypay-configuration"></liferay-ui:message>
								</a>
							</div>
						</div>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getKeypayDomain()%>"
							id="<%=PaymentConfigDisplayTerms.KEYPAY_DOMAIN%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.KEYPAY_DOMAIN%>"
							label="keypay-domain">
							<aui:validator name="required"></aui:validator>
							<aui:validator name="url" />
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getKeypayVersion()%>"
							id="<%=PaymentConfigDisplayTerms.KEYPAY_VERSION%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.KEYPAY_VERSION%>"
							label="keypay-version">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input
							value="<%=paymentConfig.getKeypayMerchantCode()%>"
							id="<%=PaymentConfigDisplayTerms.KEYPAY_MERCHANT_CODE%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.KEYPAY_MERCHANT_CODE%>"
							label="keypay-merchant-code">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input
							value="<%=paymentConfig.getKeypaySecureKey()%>"
							id="<%=PaymentConfigDisplayTerms.KEYPAY_SECURE_KEY%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.KEYPAY_SECURE_KEY%>"
							label="keypay-secure-key">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col>
						<div class="navbar">
							<div class="navbar-inner">
								<a class="brand"> <liferay-ui:message
										key="invoice-configuration"></liferay-ui:message>
								</a>
							</div>
						</div>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getGovAgencyTaxNo()%>"
							id="<%=PaymentConfigDisplayTerms.GOV_AGENCY_TAX_NO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.GOV_AGENCY_TAX_NO%>"
							label="gov-agency-tax-no">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input
							value="<%=paymentConfig.getInvoiceTemplateNo()%>"
							id="<%=PaymentConfigDisplayTerms.INVOICE_TEMPLATE_NO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.INVOICE_TEMPLATE_NO%>"
							label="invoice-template-no">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getInvoiceIssueNo()%>"
							id="<%=PaymentConfigDisplayTerms.INVOICE_ISSUE_NO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.INVOICE_ISSUE_NO%>"
							label="invoice-issue-no">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getInvoiceLastNo()%>"
							id="<%=PaymentConfigDisplayTerms.INVOICE_LAST_NO%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.INVOICE_LAST_NO%>"
							label="invoice-last-no">
							<aui:validator name="required"></aui:validator>
							<aui:validator name="maxLength"
								errorMessage="no-more-than-7-characters">7</aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>
				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=paymentConfig.getReturnUrl()%>"
							id="<%=PaymentConfigDisplayTerms.RETURN_URL%>"
							cssClass="input100"
							name="<%=PaymentConfigDisplayTerms.RETURN_URL%>"
							label="system-domain-url-return">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>
				<aui:row>
					<aui:col>
						<aui:button cssClass="pull-right" id="previewButton"
							name="previewButton" value="view-report-template"></aui:button>
					</aui:col>
				</aui:row>
				<aui:row>
					<aui:col>
						<aui:input value="<%=paymentConfig.getReportTemplate()%>"
							id="<%=PaymentConfigDisplayTerms.REPORT_TEMPLATE%>"
							type="textarea" cssClass="input100"
							name="<%= PaymentConfigDisplayTerms.REPORT_TEMPLATE %>">
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col>
						<aui:button name="submit" type="submit" value="submit" />
					</aui:col>
				</aui:row>

			</aui:form>

			<portlet:actionURL name="setReportTemplateTemp" var="setReportTemplateTempURL" />	
			
			<aui:script>
				AUI().use('aui-base',
				'aui-io-plugin-deprecated',
				'liferay-util-window',
				'liferay-portlet-url',
				'aui-dialog-iframe-deprecated',
				function(A) {
					A.one('#<portlet:namespace />previewButton').on('click',
					function(event){
						var reportTemplate = A.one('#<portlet:namespace /><%= PaymentConfigDisplayTerms.REPORT_TEMPLATE %>').get("value");
		
						A.io.request(
								'<%= setReportTemplateTempURL.toString() %>',
								{
									method: 'POST',
								    dataType : 'json',
								    data:{   
								    	<portlet:namespace /><%= PaymentConfigDisplayTerms.REPORT_TEMPLATE %>: reportTemplate
								    },   
								    on: {
								        success: function(event, id, obj) {
											var url =Liferay.PortletURL.createRenderURL();
											url.setPortletId("<%= themeDisplay.getPortletDisplay().getId() %>");
											url.setWindowState('pop_up');
											url.setParameter("mvcPath", '<%= templatePath + "preview_report.jsp" %>');
											//url.setParameter("<%= PaymentConfigDisplayTerms.REPORT_TEMPLATE %>", reportTemplate);
											var paymentConfigId = A.one('#<portlet:namespace /><%= PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID %>').get("value");
											url.setParameter("<%= PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID %>", paymentConfigId);
											var popUpWindow=Liferay.Util.Window.getWindow(
											{
												dialog: {
													centered: true,
													constrain2view: true,
													modal: true,
													resizable: false,
													width: 920
												}
											}
											).plug(
												A.Plugin.DialogIframe,
												{
													autoLoad: false,
													iframeCssClass: 'dialog-iframe',
													uri:url.toString()
												}).render();
												popUpWindow.show();
												popUpWindow.titleNode.html('<%= LanguageUtil.get(pageContext, "preview-report-template") %>');
												popUpWindow.io.start();
									
										},
										error: function(){
										}
									}
								}
							);				
					});
				});
			</aui:script>
		
		</c:when>
		
		<c:otherwise>
			<liferay-ui:message key="do-not-have-permission"></liferay-ui:message>
		</c:otherwise>
	
	</c:choose>
</div>