
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

<%@page import="org.opencps.processmgt.model.ProcessOrder"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>

<%@ include file="../../init.jsp"%>

<%
	ProcessOrder processOrder = (ProcessOrder)request.getAttribute(WebKeys.PROCESS_ORDER_ENTRY);
	Dossier dossier = (Dossier)request.getAttribute(WebKeys.DOSSIER_ENTRY);
	
	String backURL = ParamUtil.getString(request, "backURL");
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title="process-order"
/>

	<liferay-util:buffer var="htmlTop">
		<c:if test="<%= processOrder != null %>">
			<div class="form-navigator-topper dossier-info">
				<div class="form-navigator-container">
					<i aria-hidden="true" class="fa fa-suitcase"></i>
					<span class="form-navigator-topper-name"><%= Validator.isNotNull(dossier.getReceptionNo()) ? dossier.getReceptionNo() : StringPool.BLANK %></span>
				</div>
			</div>
		</c:if> 
	</liferay-util:buffer>
	
	<liferay-util:buffer var="htmlBottom">
	
	</liferay-util:buffer>
	
	<aui:row>
		<aui:col width="25">
			<div style="margin-bottom: 25px;" class="opencps-searchcontainer-wrapper default-box-shadow radius8" data-spy="affix" data-offset-top="200" id='<portlet:namespace/>containerP'>
				<p id='<portlet:namespace/>p-dossier-content'>
					<aui:a href='<%="#" + renderResponse.getNamespace() + "dossier_content"%>'>
						<i class="fa dossier_content"></i>
						<%=LanguageUtil.get(pageContext, "dossier_content") %>
					</aui:a>
				</p>
				<p id='<portlet:namespace/>p-dossier-info'>
					<aui:a href='<%="#" + renderResponse.getNamespace() + "dossier_info_line"%>'>
						<i class="fa dossier_info"></i>
						<%=LanguageUtil.get(pageContext, "dossier-info") %>
					</aui:a>
				</p>
				<p id='<portlet:namespace/>p-process'>
					<aui:a href='<%="#" + renderResponse.getNamespace() + "process_line"%>'>
						<i class="fa process"></i>
						<%=LanguageUtil.get(pageContext, "process-qt") %>
					</aui:a>
				</p>
				<p id='<portlet:namespace/>p-history'>
					<aui:a href='<%="#" + renderResponse.getNamespace() + "history_line"%>'>
						<i class="fa history"></i>
						<%=LanguageUtil.get(pageContext, "history") %>
					</aui:a>
				</p>
			</div>
		</aui:col>
		
		<aui:col width="75">
			<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
				<aui:form name="pofm" action="" method="post">
				
					<aui:model-context bean="<%= processOrder %>" model="<%= ProcessOrder.class %>" />
					
					<aui:input 
						name="redirectURL" 
						type="hidden" 
						value="<%= backURL%>"
					/>
					<aui:input 
						name="returnURL" 
						type="hidden" 
						value="<%= currentURL%>"
					/>
					
					<div class="process-order-detail-container">
						<div id="<portlet:namespace/>dossier_content">
							<h4 style="text-align: center;"><%=LanguageUtil.get(pageContext, "dossier_content") %></h4>
							<liferay-util:include page='<%=templatePath + "/dossier/dossier_content_view_part_file.jsp" %>' servletContext="<%=application %>" />
						</div>
						
						<div id="<portlet:namespace/>dossier_info_line" class="separate-line"></div>
						
						<div id="<portlet:namespace/>dossier_info">
							<h4 style="text-align: center;"><%=LanguageUtil.get(pageContext, "dossier-info") %></h4>
							<liferay-util:include page='<%=templatePath + "/dossier/dossier_info.jsp" %>' servletContext="<%=application %>" />
						</div>
						
						<div id="<portlet:namespace/>process_line" class="separate-line"></div>
						
						<div id="<portlet:namespace/>process">
							<h4 style="text-align: center;"><%=LanguageUtil.get(pageContext, "process") %></h4>
							<liferay-util:include page='<%=templatePath + "/dossier/process.jsp" %>' servletContext="<%=application %>" />
						</div>
						
						<div id="<portlet:namespace/>history_line" class="separate-line"></div>
						
						<div id="<portlet:namespace/>history">
							<h4 style="text-align: center;"><%=LanguageUtil.get(pageContext, "history") %></h4>
							<liferay-util:include page='<%=templatePath + "/dossier/history.jsp" %>' servletContext="<%=application %>" />
						</div>
					</div>
					
				</aui:form>
			</div>
		</aui:col>
	</aui:row>

<aui:script>
	AUI().ready(function(A){
		
		var dossierContentDiv = A.one('#<portlet:namespace/>dossier_content');
		var dossierInfoDiv = A.one('#<portlet:namespace/>dossier_info');
		var processDiv = A.one('#<portlet:namespace/>process');
		var historyDiv = A.one('#<portlet:namespace/>history');
		
		var dossierContentP = A.one('#<portlet:namespace/>p-dossier-content');
		var dossierInfoP = A.one('#<portlet:namespace/>p-dossier-info');
		var processP = A.one('#<portlet:namespace/>p-process');
		var historyP = A.one('#<portlet:namespace/>p-history');
		
		var containerP = A.one('#<portlet:namespace/>containerP');
		
		if ($(document).scrollTop() < dossierInfoDiv.getY() - 75){
	        dossierContentP.addClass('changeDefErr');
	    } else 
	    if ($(document).scrollTop() < processDiv.getY() - 75){
	    	dossierInfoP.addClass('changeDefErr');
	    } else
	    if ($(document).scrollTop() < historyDiv.getY() - 75){
	    	processP.addClass('changeDefErr');
	    } else {
	    	historyP.addClass('changeDefErr');
	    }
		
		A.on('scroll', function(){
			
			if ($(document).scrollTop() < dossierInfoDiv.getY() - 75){
		        
		        dossierInfoP.removeClass('changeDefErr');
		        processP.removeClass('changeDefErr');
		        historyP.removeClass('changeDefErr');
		        
		        dossierContentP.addClass('changeDefErr');
		    } else 
		    if ($(document).scrollTop() < processDiv.getY() - 75){
		    	
		    	dossierContentP.removeClass('changeDefErr');
		    	processP.removeClass('changeDefErr');
		    	historyP.removeClass('changeDefErr');
		    	
		    	dossierInfoP.addClass('changeDefErr');
		    } else
		    if ($(document).scrollTop() < historyDiv.getY() - 75){
		    	
		    	dossierContentP.removeClass('changeDefErr');
		    	dossierInfoP.removeClass('changeDefErr');
		    	historyP.removeClass('changeDefErr');
		    	
		    	processP.addClass('changeDefErr');
		    } else {
		    	
		    	dossierContentP.removeClass('changeDefErr');
		    	processP.removeClass('changeDefErr');
		    	historyP.removeClass('changeDefErr');
		    	
		    	historyP.addClass('changeDefErr');
		    }

		});
	});
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.processmgt.processorder.process_order_detail.jsp");
%>