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
	ResultRow row =	(ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

	PaymentFile paymentFile = (PaymentFile)row.getObject();
	
	int paymentStatus = paymentFile.getPaymentStatus();
	
	List<String> paymentOption = ListUtil.toList(StringUtil.split(paymentFile.getPaymentOptions()));
	
	boolean isCash = paymentOption.contains(PaymentRequestGenerator.PAY_METHOD_CASH);
	boolean isKeypay = paymentOption.contains(PaymentRequestGenerator.PAY_METHOD_KEYPAY);
	boolean isBank = paymentOption.contains(PaymentRequestGenerator.PAY_METHOD_BANK);
	
	String chuHoSo = (String)request.getAttribute("chuHoSo");
%>

<portlet:renderURL var="keypayTransaction">
	<portlet:param name="<%= PaymentFileDisplayTerms.PAYMENT_FILE_ID %>"
		value="<%=String.valueOf(paymentFile.getPaymentFileId()) %>" />
	<portlet:param name="mvcPath"
		value="/html/portlets/paymentmgt/frontoffice/frontofficekeypay.jsp" />
	<portlet:param name="redirectURL" value="<%= currentURL %>" />
	<portlet:param name="backURL" value="<%=currentURL %>"/>
	
</portlet:renderURL>

<portlet:renderURL var="bankTransaction">
	<portlet:param name="<%= PaymentFileDisplayTerms.PAYMENT_FILE_ID %>"
		value="<%=String.valueOf(paymentFile.getPaymentFileId()) %>" />
	<portlet:param name="mvcPath"
		value="/html/portlets/paymentmgt/frontoffice/frontofficeconfirmbank.jsp" />
	<portlet:param name="redirectURL" value="<%= currentURL %>" />
	<portlet:param name="backURL" value="<%=currentURL %>"/>
	<portlet:param name="chuHoSo" value="<%=chuHoSo %>"/>
</portlet:renderURL>


<c:if test="<%= paymentStatus == PaymentMgtUtil.PAYMENT_STATUS_REQUESTED
|| paymentStatus == PaymentMgtUtil.PAYMENT_STATUS_REJECTED%>">
	<c:if test="<%= Validator.isNotNull(paymentFile.getKeypayUrl()) %>">
    	<a class="button" href="<%= paymentFile.getKeypayUrl() %>" ><liferay-ui:message key="keypay-transaction" ></liferay-ui:message></a> <br/>
    </c:if>
    <a class="button" href="<%=bankTransaction.toString() %>" ><liferay-ui:message key="bank-transaction" ></liferay-ui:message></a> 
    
</c:if>
    