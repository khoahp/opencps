<%@page import="org.opencps.lucenequery.util.LuceneMenuUtil"%>
<%@page import="org.opencps.lucenequery.service.LuceneMenuLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.model.LuceneMenu"%>
<%@page import="org.opencps.lucenequery.service.LuceneMenuGroupLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.model.LuceneMenuGroup"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.lucenequery.LuceneQuery"%>
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.search.SearchEngineUtil"%>
<%@page import="com.liferay.portal.kernel.search.Hits"%>
<%@page import="com.liferay.portal.kernel.search.SearchContextFactory"%>
<%@page import="com.liferay.portal.kernel.search.SearchContext"%>
<%@page import="com.liferay.portal.kernel.search.BooleanQuery"%>
<%@ include file="../init.jsp"%>

<%
	Layout linkToPageLayout = null;

	if(Validator.isNotNull(layoutUUID)){
		
		try{
			linkToPageLayout = LayoutLocalServiceUtil.getLayoutByUuidAndCompanyId(layoutUUID, company.getCompanyId());
		}catch(Exception e){}
	}
%>


<liferay-portlet:renderURL 
	var="linkToPageURL" 
	plid="<%=linkToPageLayout != null ? linkToPageLayout.getPlid() : plid %>" 
	portletName="<%=targetPortletName %>" 
	windowState="<%=LiferayWindowState.NORMAL.toString() %>"
/>

<c:if test="<%=menuGroupIds != null &&  menuGroupIds.length > 0%>">
	<ul class="lucene-menu-wrapper">
		<li>
		<%
		
			for(int i = 0; i < menuGroupIds.length; i++){
				
				LuceneMenuGroup luceneMenuGroup = null;
				
				List<LuceneMenu> treeMenu = new ArrayList<LuceneMenu>();
				
				long menuGroupId = GetterUtil.getLong(menuGroupIds[i]);
				
				
				int currentLevel = -1;
				
				if(menuGroupId > 0){
					List<LuceneMenu> rootMenuItems = new ArrayList<LuceneMenu>();
					try{
						
						luceneMenuGroup = LuceneMenuGroupLocalServiceUtil.getLuceneMenuGroup(menuGroupId);
						
						rootMenuItems = LuceneMenuLocalServiceUtil.getLuceneMenusByG_MG_L(scopeGroupId, menuGroupId, startLevel);
						
						treeMenu = LuceneMenuUtil.buildTreeMenu(rootMenuItems, treeMenu, scopeGroupId, menuGroupId);
						
					}catch(Exception e){
						continue;
					}
				}
				
				if(treeMenu != null && luceneMenuGroup != null){

					%>
						<span class="lucene-menu-header"><%=luceneMenuGroup.getName() %></span>
					<%
					
					SearchContext searchContext = SearchContextFactory.getInstance(request);
					 
					for(LuceneMenu menuItem : treeMenu){
						
						Hits hits = null;
						
						List<String> paramNames = new ArrayList<String>();
					
						String tempURL = linkToPageURL.toString();
						
						LuceneQuery luceneQuery = new LuceneQuery(menuItem.getPattern(), 
								menuItem.getParamValues(), menuItem.getParamTypes(), searchContext);
						
						if(luceneQuery.getQuery() != null){
							hits = SearchEngineUtil.search(searchContext, luceneQuery.getQuery());
						}
						
						if(luceneQuery.getParamNames() != null){
							paramNames = luceneQuery.getParamNames();
						}
						int count = 0;
						for(String paramName : paramNames){
							Object object = luceneQuery.getParams().get(count);
							String paramValue = StringPool.BLANK;
							if(object != null){
								Class<?> clazz = luceneQuery.getParamTypes().get(count);
								if(clazz.equals(long.class)){
									paramValue = String.valueOf(GetterUtil.getLong(object));
								}else if(clazz.equals(String.class)){
									paramValue = object.toString();
								}else if(clazz.equals(boolean.class)){
									paramValue = String.valueOf(GetterUtil.getBoolean(object));
								}else if(clazz.equals(double.class)){
									paramValue = String.valueOf(GetterUtil.getDouble(object));
								}else if(clazz.equals(short.class)){
									paramValue = String.valueOf(GetterUtil.getShort(object));
								}else if(clazz.equals(int.class)){
									paramValue = String.valueOf(GetterUtil.getInteger(object));
								}else if(clazz.equals(float.class)){
									paramValue = String.valueOf(GetterUtil.getFloat(object));
								}else if(clazz.equals(Date.class)){
									paramValue = DateTimeUtil.convertDateToString((Date)object, DateTimeUtil._VN_DATE_FORMAT);
								}
							}
							String tempParam = StringPool.AMPERSAND + StringPool.UNDERLINE + targetPortletName + 
										StringPool.UNDERLINE + paramName + StringPool.EQUAL + paramValue;
							tempURL += tempParam;
							count ++;
						}
					
						if(menuItem.getLevel() > currentLevel){
							//open <ul><li>
							%>
								<ul  class='<%="menu-group group-level-" + menuItem.getLevel()%>'>
									<li class='<%="menu-item level-" + menuItem.getLevel()%>'>
										<i class="fa fa-caret-right" aria-hidden="true"></i>
										<aui:a href="<%=tempURL.toString() %>">
											<span class="item-name">
												<%=menuItem.getName() %>
											</span>
											<span class="item-value">
												<%=hits != null ? hits.getLength() : "N/A" %>
											</span>
										 </aui:a>
							<%
						}else if(menuItem.getLevel() == currentLevel){
							// close and open </li><li>
							%>
								</li>
								<li class='<%="menu-item level-" + menuItem.getLevel()%>'>
									<i class="fa fa-caret-right" aria-hidden="true"></i>
									<aui:a href="<%=tempURL.toString() %>">
										<span class="item-name">
											<%=menuItem.getName() %>
										</span>
										<span class="item-value">
											<%=hits != null ? hits.getLength() : "N/A" %>
										</span>
									 </aui:a>
							<%
						}else {
							// close </li></ul>
							int delta = currentLevel - menuItem.getLevel();
							if(delta > 0){
								for(int d = 0; d < delta; d++){
									%>
										</li></ul>
									<%
								}
							}
							%>
								
								<li class='<%="menu-item level-" + menuItem.getLevel()%>'>
									<i class="fa fa-caret-right" aria-hidden="true"></i>
									<aui:a href="<%=tempURL.toString() %>">
										<span class="item-name">
											<%=menuItem.getName() %>
										</span>
										<span class="item-value">
											<%=hits != null ? hits.getLength() : "N/A" %>
										</span>
									 </aui:a>
							<%
						}
						
						currentLevel = menuItem.getLevel();
					}
					 
					if(currentLevel > 0){
						for(int c = 0; c < currentLevel; c++){
					 		%>
					 			</li></ul>
					 		<%
					 	}
					}
				}
			}
		%>
		</li>
	</ul>
</c:if>