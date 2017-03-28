<%@page import="org.opencps.processmgt.permissions.ProcessOrderPermission"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.util.ActionKeys"%>
<%@page import="com.liferay.portal.service.permission.PortletPermissionUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ include file="../init.jsp" %>
<%

	String[] names = new String[]{"statistics", "cofiguration-level"};

	String value = ParamUtil.getString(request, "tabs1", "statistics");

	List<String> urls = new ArrayList<String>();
	
	PortletURL manualStatisticURL = renderResponse.createRenderURL();
	manualStatisticURL.setParameter("mvcPath", templatePath + "statistics.jsp");
	manualStatisticURL.setParameter("tabs1", "statistics");
	urls.add(manualStatisticURL.toString());
	
	PortletURL configurationLevelURL = renderResponse.createRenderURL();
	configurationLevelURL.setParameter("mvcPath", templatePath + "administration_level.jsp");
	configurationLevelURL.setParameter("tabs1", "cofiguration-level");
	urls.add(configurationLevelURL.toString());
	
%>
<div class="opencps-toptabs">
	<div class="container">
		<liferay-ui:tabs
			names="<%= StringUtil.merge(names) %>"
			param="tabs1"
			url0="<%=urls != null && urls.size() > 0 ? urls.get(0): StringPool.BLANK %>"
			url1="<%=urls != null && urls.size() > 1 ? urls.get(1): StringPool.BLANK %>"
		/>
	</div>
</div>
