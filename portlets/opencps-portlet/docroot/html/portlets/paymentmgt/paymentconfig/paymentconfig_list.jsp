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
	 * along with this program. If not, see <http://www.gnu.org/licenses/>
	 */
%>
<%@ include file="../init.jsp"%>

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("tabs1", PortletKeys.PAYMENTCONFIG_TAB);
 %>
 
 <div
	class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">
	
	<liferay-ui:search-container
		searchContainer="<%=new PaymentConfigSearch(renderRequest,
						SearchContainer.DEFAULT_DELTA, iteratorURL)%>">

		<liferay-ui:search-container-results>
			<%
				List<PaymentConfig> paymentConfigs = PaymentConfigLocalServiceUtil
								.getPaymentConfigs(QueryUtil.ALL_POS,
										QueryUtil.ALL_POS);

						int totalSize = PaymentConfigLocalServiceUtil
								.getPaymentConfigsCount();

						pageContext.setAttribute("results", paymentConfigs);
						pageContext.setAttribute("total", totalSize);
			%>

		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row
			className="org.opencps.paymentmgt.model.PaymentConfig"
			modelVar="paymentConfig" keyProperty="paymentConfigId">
			<%
				row.setClassName("opencps-searchcontainer-row");
				row.addText(String.valueOf(row.getPos() + 1));
				row.addText(DateTimeUtil.convertDateToString(
						paymentConfig.getCreateDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));

				row.addText(DateTimeUtil.convertDateToString(
						paymentConfig.getModifiedDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));

				row.addText(paymentConfig.getPaymentConfigNo());
				
				DictItem dictItem = new DictItemImpl();
				
				dictItem =  DictItemLocalServiceUtil.getDictItem(paymentConfig.getPaymentGateType());

				row.addText(dictItem.getItemCode());

				row.addJSP("center", SearchEntry.DEFAULT_VALIGN,
						templatePath + "/paymentconfig_action.jsp",
						config.getServletContext(), request, response);
				row.setParameter(WebKeys.REDIRECT_URL, iteratorURL);
			%>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator type="opencs_page_iterator" />
	</liferay-ui:search-container>
</div>

<%!private static Log _log = LogFactoryUtil
			.getLog("html.portlets.paymentmgt.paymentconfig.paymentconfig_list");%>