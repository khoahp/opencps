
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

<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletMode"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page
	import="org.opencps.lucenequery.service.LuceneMenuRoleLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.util.LuceneMenuUtil"%>
<%@page
	import="org.opencps.lucenequery.service.LuceneMenuLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.model.LuceneMenu"%>
<%@page
	import="org.opencps.lucenequery.service.LuceneMenuGroupLocalServiceUtil"%>
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

<!-- 
<style>

	.menu-group.closed {
		display: none;
	}
	
	ul.group-level-1{
		padding-left: 20px;
	}
	
	ul.menu-group.group-level-2{
		padding-left: 40px;
	}
	
	ul.menu-group.group-level-3{
		padding-left: 60px;
	}
	
	ul.menu-group.group-level-4{
		padding-left: 80px;
	}
	
	ul.menu-group.group-level-5{
		padding-left: 100px;
	}

</style> -->

<c:if test="<%=menuGroupIds != null &&  menuGroupIds.length > 0%>">
	<ul class="lucene-menu-wrapper">
		<li>
			<%
				//int[] expandAt = new int[]{-1, -1};
					
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
			%> <span class="lucene-menu-header"><%=luceneMenuGroup.getName()%></span>
			<%
				SearchContext searchContext = SearchContextFactory.getInstance(request);
						 
						for(LuceneMenu menuItem : treeMenu){
							
							if(!LuceneMenuRoleLocalServiceUtil.hasPermission(menuItem.getMenuItemId(), 
									user.getUserId())){
								continue;
							}
							
							Layout linkToPageLayout = null;

							if (Validator.isNotNull(menuItem.getLayoutUUID())) {

								try {
									linkToPageLayout = LayoutLocalServiceUtil
											.getLayoutByUuidAndCompanyId(menuItem.getLayoutUUID(),
													company.getCompanyId());
								} catch (Exception e) {
								}
							}

							PortletURL renderURL = null;
							
							if(Validator.isNotNull(menuItem.getTargetPortletName()) && 
									Validator.isNotNull(menuItem.getLayoutUUID())){
								renderURL=  PortletURLFactoryUtil
									.create(request, menuItem.getTargetPortletName(),
											linkToPageLayout != null ? linkToPageLayout.getPlid() : 
												themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
								renderURL.setWindowState(LiferayWindowState.NORMAL);
								renderURL.setPortletMode(LiferayPortletMode.VIEW);
							}
							
							
							
							String cssCloseGroup = "expanded";
							
							if(menuItem.getLevel() == startLevel){
								cssCloseGroup = StringPool.BLANK;
							}else{
								cssCloseGroup = "closed";
							}
							
							String treeIndex = menuItem.getTreeIndex();
							
							long root = menuItem.getParentId() == 0 ? menuItem.getMenuItemId() : 0;
							
							Hits hits = null;
							
							List<String> paramNames = new ArrayList<String>();
						
							//String tempURL = linkToPageURL.toString();
							
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
									
									if(renderURL != null){
										renderURL.setParameter(paramName, paramValue);
									}
								}
								/* String tempParam = StringPool.AMPERSAND + StringPool.UNDERLINE + targetPortletName + 
											StringPool.UNDERLINE + paramName + StringPool.EQUAL + paramValue;
								tempURL += tempParam; */
								count ++;
							}
						
							if(menuItem.getLevel() > currentLevel){
								//open <ul><li>
			%>
			<ul
				class='<%="menu-group group-level-" + menuItem.getLevel() + StringPool.SPACE + cssCloseGroup%>'>
				<li class='<%="menu-item level-" + menuItem.getLevel()%>'
					treeIndex="<%=treeIndex%>" root="<%=String.valueOf(root)%>"><i
					class="fa fa-caret-right" aria-hidden="true"></i> <aui:a
						href='<%=renderURL != null ? renderURL.toString() : "javascript:void(0);"%>'
						cssClass="menu-item-link">
						<span class="item-name"> <%=menuItem.getName()%>
						</span>
						<c:if test="<%=Validator.isNotNull(menuItem.getPattern())%>">
							<span class="item-value"> <%=hits != null ? hits.getLength() : "N/A"%>
							</span>
						</c:if>
					</aui:a> <%
 	}else if(menuItem.getLevel() == currentLevel){
 					// close and open </li><li>
 %></li>
				<li class='<%="menu-item level-" + menuItem.getLevel()%>'
					treeIndex="<%=treeIndex%>" root="<%=String.valueOf(root)%>"><i
					class="fa fa-caret-right" aria-hidden="true"></i> <aui:a
						href='<%=renderURL != null ? renderURL.toString() : "javascript:void(0);"%>'
						cssClass="menu-item-link">
						<span class="item-name"> <%=menuItem.getName()%>
						</span>
						<c:if test="<%=Validator.isNotNull(menuItem.getPattern())%>">
							<span class="item-value"> <%=hits != null ? hits.getLength() : "N/A"%>
							</span>
						</c:if>
					</aui:a> <%
 	}else {
 					// close </li></ul>
 					int delta = currentLevel - menuItem.getLevel();
 					if(delta > 0){
 						for(int d = 0; d < delta; d++){
 %></li>
			</ul> <%
 	}
 					}
 %>
		
		<li class='<%="menu-item level-" + menuItem.getLevel()%>'
			treeIndex="<%=treeIndex%>" root="<%=String.valueOf(root)%>"><i
			class="fa fa-caret-right" aria-hidden="true"></i> <aui:a
				href='<%=renderURL != null ? renderURL.toString() : "javascript:void(0);"%>'
				cssClass="menu-item-link">
				<span class="item-name"> <%=menuItem.getName()%>
				</span>
				<c:if test="<%=Validator.isNotNull(menuItem.getPattern())%>">
					<span class="item-value"> <%=hits != null ? hits.getLength() : "N/A"%>
					</span>
				</c:if>
			</aui:a> <%
 	}
 				
 				currentLevel = menuItem.getLevel();
 			}
 			 
 			if(currentLevel > 0){
 				for(int c = 0; c < currentLevel; c++){
 %></li>
	</ul>
	<%
		}
				}
			}
		}
	%>
	</li>
	</ul>
	<aui:script>
		AUI().ready(
				function(A) {
					var itemLinks = A.all('.menu-item-link');
					//var items = A.all('.menu-item');

					if (itemLinks) {
						itemLinks.each(function(itemLink) {
							itemLink.on('click', function() {
								var li = itemLink.get('parentNode');
								var i = li.one('.fa');
								var submenu = li.one('ul');
								var root = li.attr('root');
								var treeIndex = li.attr('treeIndex');
								var hash = [];
								var treeIndexs = [];

								hash = treeIndex.split(".");

								if (hash != null && hash.length > 0) {
									for (var h = 0; h < hash.length; h++) {
										if (h > 0) {
											treeIndexs[h] = treeIndexs[h - 1]
													+ "." + hash[h];
										} else {
											treeIndexs[h] = hash[h];
										}
									}
								}

								if (i) {
									//var css = i.get('className');
									if (i.hasClass('fa fa-caret-right')) {

										i.replaceClass('fa fa-caret-right',
												'fa fa-caret-down');

										if (submenu) {
											if (submenu.hasClass('closed')) {
												submenu.replaceClass('closed',
														'expanded');
											}
										}

										/* 
										items.each(function(item){
											
											var tempTreeIndex = item.attr('treeindex');
											
											var tempRoot = item.attr('root');
										
											var hideItem = false;
											
											//console.log(tempTreeIndex + '|' + treeIndexs + "|" + root + "|" + treeIndexs.indexOf(tempTreeIndex));
											
											if(tempRoot != root){
												hideItem = true;
											}else{
												if(treeIndexs!= null && treeIndexs.length > 0 && treeIndexs.indexOf(tempTreeIndex) < 0){
													hideItem = true;
												}
											}
											
											 if(hideItem == true){
												var expaneds = item.all('ul.expanded');
												var carets = item.all('fa fa-caret-down');
												if(carets){
													carets.each(function(caret){
														caret.replaceClass('fa fa-caret-down', 'fa fa-caret-right');
													});
												}
												if(expaneds){
													expaneds.each(function(expaned){
														if(expaned.hasClass('expanded')){
															expaned.replaceClass('expanded', 'closed');
														}
													});
												}
											} 
										}); 
										 */

									} else {
										i.replaceClass('fa fa-caret-down',
												'fa fa-caret-right');

										if (submenu) {
											if (submenu.hasClass('expanded')) {
												submenu.replaceClass(
														'expanded', 'closed');
											}
										}
									}
								}
							});
						});
					}
				});
	</aui:script>
		

	</c:if>