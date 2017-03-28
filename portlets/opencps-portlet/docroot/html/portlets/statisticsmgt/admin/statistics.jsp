<%@page import="org.opencps.statisticsmgt.util.StatisticsUtil"%>

<%@ include file="../init.jsp" %>

<liferay-util:include page='<%=templatePath + "toptabs.jsp" %>' servletContext="<%=application %>" />

<liferay-portlet:actionURL var="doStatisticsURL" name="doStatistics"/>

<aui:form name="fm" action="<%=doStatisticsURL %>" method="post">
	<aui:input name="groupId" value="<%=scopeGroupId %>" type="hidden"/>
	<aui:fieldset>
		<aui:col width="50">
			<aui:select name="month">
				<aui:option value="0"><liferay-ui:message key="all"/></aui:option>
				<%
					for(int m = 1 ; m <= 12; m ++){
						%>
							<aui:option value="<%=m %>"><%=m %></aui:option>
						<%
					}
				%>
			</aui:select>
		</aui:col>
		
		<aui:col width="50">
			<aui:select name="year">
				<%
					for(int y = 2016 ; y <= 2030; y ++){
						%>
							<aui:option value="<%=y %>"><%=y %></aui:option>
						<%
					}
				%>
			</aui:select>
		</aui:col>
	</aui:fieldset>
	<aui:row>
		<aui:col width="100">
			<aui:button name="stats" value="statatistics" type="submit" />
		</aui:col>
	</aui:row>
</aui:form>

<%-- <aui:script>
	AUI().ready(function(A){
		
		var stats = A.one('#<portlet:namespace/>stats');
		
		if(stats){
			stats.on('click', function(){
				submitForm(document.<portlet:namespace />fm);
			});
		}
	});
</aui:script>
 --%>