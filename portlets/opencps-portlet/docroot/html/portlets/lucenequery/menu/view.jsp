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

<c:if test="<%=luceneMenuSchemas != null %>">
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

	<ul class="lucene-menu-wrapper">
	<%
		SearchContext searchContext = SearchContextFactory.getInstance(request);
		
		for(LuceneMenuSchema luceneMenuSchema : luceneMenuSchemas){
			String pattern = luceneMenuSchema.getPattern();
			Hits hits = null;
			List<String> paramNames = new ArrayList<String>();
			try{
				//BooleanQuery booleanQuery = LuceneQueryUtil.buildQuerySearch(pattern, luceneMenuSchema.getParams(), searchContext);
				LuceneQuery luceneQuery = new LuceneQuery(pattern, luceneMenuSchema.getParams(), searchContext);
				if(luceneQuery.getQuery() != null){
					hits = SearchEngineUtil.search(searchContext, luceneQuery.getQuery());
				}
				
				if(luceneQuery.getParamNames() != null){
					paramNames = luceneQuery.getParamNames();
				}
				
			}catch(Exception e){
				
			}
			
			String tempURL = linkToPageURL.toString();
			int count = 0;
			if(paramNames != null && paramNames.size() == luceneMenuSchema.getParams().size()){
				for(String paramName : paramNames){
					Object object = luceneMenuSchema.getParams().get(count);
					String paramValue = StringPool.BLANK;
					if(object != null){
						Class<?> clazz = luceneMenuSchema.getParamTypes().get(count);
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
				}
			}
			
			%>
				<li class='<%="menu-item level-" + luceneMenuSchema.getLevel()%>'>
					<aui:a href="<%=tempURL.toString() %>">
						<span class="item-name">
							<%=luceneMenuSchema.getName() %>
						</span>
						<span class="item-value">
							<%=hits != null ? hits.getLength() : "N/A" %>
						</span>
					</aui:a>
				</li>
			<%
		}
	%>
	</ul>
</c:if>