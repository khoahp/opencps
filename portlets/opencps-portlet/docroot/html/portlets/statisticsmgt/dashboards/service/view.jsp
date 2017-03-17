<%@page	import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@ include file="init.jsp"%>

<%
	int totalServiceConfigLevel4 = ServiceConfigLocalServiceUtil
			.countByLevel(scopeGroupId, 4);
	int totalServiceConfigLevel3 = ServiceConfigLocalServiceUtil
			.countByLevel(scopeGroupId, 3);
	int totalServiceConfigLevel2 = ServiceConfigLocalServiceUtil
			.countByLevel(scopeGroupId, 2);
%>

<div class="dashboard-wrapper">
	<div class="service-config-label span3">
		<liferay-ui:message key="service-config-count"/>
	</div>
	<div class="span3 service-config-count">
		<span>
			<liferay-ui:message key="service-config-level-4"/> :
		</span>
		<span class="total-count">
			 <%=totalServiceConfigLevel4 %>
		</span>
	</div>
	<div class="span3 service-config-count">
		<span>
			<liferay-ui:message key="service-config-level-3"/> :
		</span>
		<span class="total-count">
			 <%=totalServiceConfigLevel3 %>
		</span>
	
	
	</div>
	<div class="span3 service-config-count">
		<span>
			<liferay-ui:message key="service-config-level-2"/> :
		</span>
		<span class="total-count">
			 <%=totalServiceConfigLevel2 %>
		</span>
	</div>
</div>