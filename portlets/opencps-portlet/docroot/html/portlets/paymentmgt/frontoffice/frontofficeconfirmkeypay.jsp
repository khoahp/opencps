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
	HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
	
	long paymentFileId = ParamUtil.getLong(originalRequest, "paymentFileId",0);
	long dossierId = ParamUtil.getLong(originalRequest, "dossierId",0);
	long serviceInfoId = ParamUtil.getLong(originalRequest, "serviceInfoId",0);
	
	PaymentFile paymentFile = new PaymentFileImpl();
	Dossier dossier = new DossierImpl();
	ServiceInfo serviceInfo = new ServiceInfoImpl();
	PaymentConfig paymentConfig = new PaymentConfigImpl();
	DictItem dictItem = new DictItemImpl();
	
	try{
	
		if (paymentFileId > 0) {
			paymentFile = PaymentFileLocalServiceUtil.getPaymentFile(paymentFileId);
	
			if (Validator.isNotNull(paymentFile) && paymentFile.getPaymentConfig() > 0) {
				
				paymentConfig =
					PaymentConfigLocalServiceUtil.getPaymentConfig(paymentFile.getPaymentConfig());
				
				if(Validator.isNotNull(paymentConfig) && paymentConfig.getPaymentGateType() > 0){
					dictItem =  DictItemLocalServiceUtil.getDictItem(paymentConfig.getPaymentGateType());
				}
	
			}
	
		}
		if (dossierId > 0) {
			dossier = DossierLocalServiceUtil.getDossier(dossierId);
	
		}
		if (serviceInfoId > 0) {
			serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(serviceInfoId);
	
		}
	}catch(Exception e){
		
	}
%>

<portlet:renderURL var="backURL">
	<portlet:param 
		name="mvcPath"
		value="/html/portlets/paymentmgt/frontoffice/frontofficepaymentlist.jsp" 
	/>	
</portlet:renderURL>

<liferay-ui:header
	backURL="<%= backURL.toString() %>"
	title="payment-list"
/>

<% if(Validator.isNotNull(paymentFile) &&  Validator.isNotNull(dossier) && Validator.isNotNull(serviceInfo)){ %>
	<c:choose>
		<c:when test="<%=paymentFile.getPaymentStatus() == PaymentMgtUtil.PAYMENT_STATUS_APPROVED %>">
			<div class="alert alert-success">
				<liferay-ui:message key="paygate-success"></liferay-ui:message>
			</div>
				<div class="lookup-result">
					<table>
						<tr>
							<td class="col-left"><liferay-ui:message key="reception-no"></liferay-ui:message></td>
							<td class="col-right"><%= dossier.getReceptionNo()%></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="service-name"></liferay-ui:message></td>
							<td class="col-right"><%= serviceInfo.getServiceName() %></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="administration-name"></liferay-ui:message></td>
							<td class="col-right"><%= dossier.getGovAgencyName() %></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="payment-name"></liferay-ui:message></td>
							<td class="col-right"><%=paymentFile.getPaymentName()%></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="amount"></liferay-ui:message></td>
							<td class="col-right"><%= NumberFormat.getInstance(new Locale("vi", "VN")).format(paymentFile.getAmount()) %></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="trans_id"></liferay-ui:message></td>
							<td class="col-right"><%=paymentFile.getKeypayTransactionId() %></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="paygate_id"></liferay-ui:message></td>
							<td class="col-right"><%=paymentFile.getKeypayGoodCode() %></td>
						</tr>
						<tr>
							<td class="col-left"><liferay-ui:message key="paygate_name"></liferay-ui:message></td>
							<td class="col-right">
								<%=dictItem.getItemCode()%>
							</td>
						</tr>
					</table>	
				</div>	
		</c:when>
		<c:otherwise>
			<div class="alert alert-error">
				<liferay-ui:error key="paygate-alert">:<%=paymentFile.getPaymentGateStatusCode() %></liferay-ui:error>
				
			</div>
		</c:otherwise>
	</c:choose>
<%}else{ %>
	<div class="alert alert-error">
		<liferay-ui:error key="payment-data-alert"></liferay-ui:error>
	</div>
<%} %>

<%! private static Log _log = LogFactoryUtil.getLog("/html/portlets/paymentmgt/frontoffice/frontofficeconfirmkeypay");%>