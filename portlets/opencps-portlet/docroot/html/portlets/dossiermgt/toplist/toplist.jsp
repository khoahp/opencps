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
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="java.util.List"%>

<%@ include file="init.jsp" %>

<%
	List<Dossier> dossiers = new ArrayList<Dossier>();
	int totalCount = 0;

	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", "/html/portlets/dossiermgt/toplist/toplist.jsp");
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("boundcol1");
	headerNames.add("boundcol2");
	
	String headers = StringUtil.merge(headerNames, StringPool.COMMA);

%>
<div class="opencps-searchcontainer-wrapper">

	<div class="opcs-serviceinfo-list-label">
		<div class="title_box">
			<p class="file_manage_title"><liferay-ui:message key='<%= "list-dossier-" + status %>' /></p>
			<p class="count"></p>
		</div>
	</div>

	<liferay-ui:search-container 
			emptyResultsMessage="no-dossier-were-found"
			iteratorURL="<%=iteratorURL %>"
			delta="<%=20 %>"
			deltaConfigurable="true"
			headerNames="<%=headers %>"
			>
			<liferay-ui:search-container-results>
				<%
					dossiers = DossierLocalServiceUtil
						.getDossierByG_DS_U
							(scopeGroupId, status, user.getUserId()
								, searchContainer.getStart(), searchContainer.getEnd());
					
					totalCount = DossierLocalServiceUtil
									.countDossierByG_DS_U
									(scopeGroupId, status, user.getUserId());		
					
					results = dossiers;
					total = totalCount;
					
					pageContext.setAttribute("results", results);
					pageContext.setAttribute("total", total);
				%>
			</liferay-ui:search-container-results>
			
			<liferay-ui:search-container-row 
				className="org.opencps.dossiermgt.model.Dossier" 
				modelVar="dossier" 
				keyProperty="dossierId"
			>
				<%
					String serviceName = StringPool.BLANK;
					
					String dossierName = StringPool.BLANK;
					
					User bossOfDossier = null;
					ServiceInfo serviceInfo = null;
					
					try {
						bossOfDossier = UserLocalServiceUtil.getUser(dossier.getUserId());
						dossierName = bossOfDossier.getFullName();
					} catch(Exception e) {
						//nothing to do
					}
					
					try {
						serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(dossier.getServiceInfoId());
						serviceName = serviceInfo.getFullName();
					} catch (Exception e) {
						//nothing to do
					}
					
				%>
				<liferay-util:buffer var="boundcol1">
					<div class="row-fluid">
						<div class="row-fluid">
							<div class="span2 bold-label">
								<liferay-ui:message key="dossier-numb-top"/>
							</div>
							<div class="span10">
								<%=dossier.getReceptionNo() %>
							</div>
						</div>
					</div>
					
					<div class="row-fluid">
						
						<div class="span2 bold-label">
							 <liferay-ui:message key="name-of-service"/>
						</div>
						
						<div class="span10">
							<%=serviceName %>
						</div>
					</div>
				</liferay-util:buffer>
				
				
				<liferay-util:buffer var="boundcol2">
				<div class="row-fluid">
					
					<div class="span5 bold-label"><liferay-ui:message key="boss-of-dossier"/></div>
					<div class="span7">
						<%=dossierName %>
					</div>
				</div>
				
				<div class="row-fluid">
					
					<div class="span5 bold-label">
						 <liferay-ui:message key="date-for-receiving"/>
					</div>
					
					<div class="span7">
						<%=DateTimeUtil.convertDateToString(dossier.getCreateDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) %>
					</div>
					
				</div>
				<c:if test="<%= Validator.isNotNull(dossier.getFinishDatetime()) %>">
					<div class="row-fluid">
						
						<div class="span5 bold-label">
							 <liferay-ui:message key="date-for-completed"/>
						</div>
						
						<div class="span7">
							<%=DateTimeUtil.convertDateToString(dossier.getFinishDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT) %>
						</div>
						
					</div>
				</c:if>
				
				
				</liferay-util:buffer>
				<%
					row.setClassName("opencps-searchcontainer-row");
					row.addText(boundcol1);
					row.addText(boundcol2);
				%>

			</liferay-ui:search-container-row>
			<liferay-ui:search-iterator type="opencs_page_iterator"/>
	</liferay-ui:search-container>
</div>