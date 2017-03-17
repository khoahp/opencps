<%@page import="org.opencps.statisticsmgt.service.GovagencyLevelLocalServiceUtil"%>
<%@page import="org.opencps.statisticsmgt.model.GovagencyLevel"%>
<%@page import="org.opencps.datamgt.util.comparator.DictItemTreeIndexComparator"%>
<%@page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.statisticsmgt.util.StatisticsUtil"%>

<%@ include file="../init.jsp" %>

<liferay-util:include page='<%=templatePath + "toptabs.jsp" %>' servletContext="<%=application %>" />

<%
	List<DictItem> dictItems = new ArrayList<DictItem>();
	try {
		DictCollection dictCollection = DictCollectionLocalServiceUtil
				.getDictCollection(
						scopeGroupId,
						PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);
		
		OrderByComparator orderByComparator = new DictItemTreeIndexComparator(true);
		
		dictItems = DictItemLocalServiceUtil
				.getDictItemsByDictCollectionId(dictCollection
						.getDictCollectionId(), orderByComparator);
	} catch (Exception e) {

	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("tabs1", templatePath + "cofiguration-level");
	iteratorURL.setParameter("mvcPath", templatePath + "administration_level.jsp");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("gov-name");
	headerNames.add("tree-index");
	headerNames.add("level");
%>
<portlet:actionURL name="updateAdministrationLevel" var="updateAdministrationLevelURL">
	<portlet:param name="redirectURL" value="<%=currentURL %>"/>
</portlet:actionURL>
<aui:form action="<%=updateAdministrationLevelURL.toString() %>" name="fm" method="post">
	<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
		<liferay-ui:search-container 
			searchContainer="<%= new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, iteratorURL, null, null) %>"
			total="<%= dictItems.size() %>"
		>
			<%
				searchContainer.setHeaderNames(headerNames);
			%>
			<liferay-ui:search-container-results>
				<%
					
					total = dictItems.size();
					results = dictItems;
					
					pageContext.setAttribute("results", results);
					pageContext.setAttribute("total", total);				
				%>
			</liferay-ui:search-container-results>	
				<liferay-ui:search-container-row 
					className="org.opencps.datamgt.model.DictItem" 
					modelVar="dictItem" 
					keyProperty="dictItemId"
				>
					<%
						
						int level = StringUtil.count(dictItem.getTreeIndex(), StringPool.PERIOD);
					%>
					
					<liferay-util:buffer var="treeName">
						<div>
							<%
								StringBuffer buffer = new StringBuffer();
								for(int l = 0; l < level; l++){
									if(l == level -1){
										buffer.append("|_____");
									}else{
										buffer.append("|_____</br>");
									}
									
								}
								
							%>
							<%=buffer.toString() + dictItem.getItemName(locale) %>
						</div>
					</liferay-util:buffer>
					
					<liferay-util:buffer var="treeIndex">
						<div class="">
							<%= dictItem.getTreeIndex()%>
						</div>
					</liferay-util:buffer>
					
					<liferay-util:buffer var="administrationLevel">
						<aui:input name="govCode" type="hidden" value="<%=dictItem.getItemCode() %>"/>
						<%
							GovagencyLevel govagencyLevel = null;
							try{
								govagencyLevel = GovagencyLevelLocalServiceUtil.getGovagencyLevelByG_GC(scopeGroupId, dictItem.getItemCode());
							}catch(Exception e){
								
							}
							
							int administrationLevel = 0;
							
							if(govagencyLevel != null){
								administrationLevel = govagencyLevel.getAdministrationLevel();
							}
							
						%>
						<aui:select name="level" label="<%=StringPool.BLANK %>">
							<aui:option value="0" selected="<%=administrationLevel == 0 %>"><liferay-ui:message key="tw"/></aui:option>
							<aui:option value="1" selected="<%=administrationLevel == 1 %>"><liferay-ui:message key="province"/></aui:option>
							<aui:option value="2" selected="<%=administrationLevel == 2 %>"><liferay-ui:message key="district"/></aui:option>
							<aui:option value="3" selected="<%=administrationLevel == 3 %>"><liferay-ui:message key="ward"/></aui:option>
						</aui:select>
						
					</liferay-util:buffer>
					
					<%
					
						/* 
					    // no column
						row.addText(String.valueOf(row.getPos() + 1 + searchContainer.getStart()));
						*/
						row.setClassName("opencps-searchcontainer-row");
						
						row.addText(treeName);
						
						row.addText(dictItem.getTreeIndex());
						
						row.addText(administrationLevel);
						
					%>	
				</liferay-ui:search-container-row> 
			
			<liferay-ui:search-iterator type="opencs_page_iterator"/>
		</liferay-ui:search-container>
		
		<aui:row>
			<aui:col width="100">
				<aui:button value="update" name="update" type="submit"/>
			</aui:col>
		</aui:row>
	</div>
</aui:form>
