
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

<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="java.util.Date"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>

<%@ include file="../init.jsp"%>

<%
	Date defautDate = DateTimeUtil.convertStringToDate("01/01/1970");
	PortletUtil.SplitDate spd = new PortletUtil.SplitDate(defautDate);
	
	Date now = new Date();
	
	String dateNow = DateTimeUtil.convertDateToString(now, DateTimeUtil._VN_DATE_FORMAT);
	
	Date defautDateNow = DateTimeUtil.convertStringToDate(dateNow);
	
	PortletUtil.SplitDate spdNow = new PortletUtil.SplitDate(defautDateNow);
%>

<portlet:renderURL var="referentToSeachContaner" windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>">
	<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/log/ajax/search_result.jsp"/>
</portlet:renderURL>

<div class="opencps-bound-wrapper pd20 mg-b-20 default-box-shadow">
	<aui:row>
		<aui:col width="25">
		<label><liferay-ui:message key="from-date"/></label>
			
			<liferay-ui:input-date
				dayParam="fromDay"
				dayValue="<%=spd.getDayOfMoth() %>"
				monthParam="fromMonth"
				monthValue="<%=spd.getMonth() %>"
				yearParam="fromYear"
				yearValue="<%=spd.getYear() %>"
				disabled="<%= false %>"
				name="fromDate"
				formName="fm"
			/>
		</aui:col >
		
		<aui:col width="25">
			<label><liferay-ui:message key="to-date"/></label>
			<%-- <input id="<portlet:namespace />toDate" type="date"/> --%>
		
			<liferay-ui:input-date
				dayParam="toDay"
				dayValue="<%=spdNow.getDayOfMoth() %>"
				monthParam="toMonth"
				monthValue="<%=spdNow.getMonth() %>"
				yearParam="toYear"
				yearValue="<%=spdNow.getYear() %>"
				disabled="<%= false %>"
				name="toDate"
				formName="fm"
			/>
			
		</aui:col>

		<aui:col width="25">
			<aui:select name="level">
				<aui:option value="<%= -1 %>">
					
				</aui:option>
			
				<aui:option value="<%= 0 %>">
					<liferay-ui:message key="log" />
				</aui:option>
				
				<aui:option value="<%= 1 %>">
					<liferay-ui:message key="notify" />
				</aui:option>
				
				<aui:option value="<%= 2 %>">
					<liferay-ui:message key="error" />
				</aui:option>
			</aui:select>
		</aui:col>
		
		<aui:col width="25">
			<%-- <datamgt:ddr 
				depthLevel="1" 
				dictCollectionCode="DOSSIER_STATUS"
				itemNames="dossierStatus"
				itemsEmptyOption="true"	
			/>	 --%>
			<aui:select name="dossierStatus">
				<aui:option value="<%= StringPool.BLANK %>"></aui:option>
				<%
					for(DictItem stt : PortletUtil.getDossierStatus(scopeGroupId)) {
						%>
							<aui:option value="<%=stt.getItemCode() %>">
								<%=stt.getItemName(locale, true) %>
							</aui:option>
						<%
					}
				%>
			</aui:select>
			
		</aui:col>
	</aui:row>
	
	<aui:input  name="isAutoLoad" type="checkbox"/>
	
	<aui:button name="seach" value="search"/>
</div>

<div id="<portlet:namespace />loadSearchLog" ></div>

<aui:script>

	AUI().ready(function(A) {
		var fromDateInp = A.one('#<portlet:namespace />fromDate');
		var toDateInp = A.one('#<portlet:namespace />toDate');
		var levelInp = A.one('#<portlet:namespace />level');
		var dossierStatusInp = A.one('#<portlet:namespace />dossierStatus');
		var fromDateInp = A.one('#<portlet:namespace />fromDate');
		var isAutoLoadCheckboxInp = A.one('#<portlet:namespace />isAutoLoadCheckbox');
		var btnSeach = A.one('#<portlet:namespace />seach');
		var todayInp = A.one('#<portlet:namespace />toDay');
		var tomonthInp = A.one('#<portlet:namespace />toMonth');
		var toyearInp = A.one('#<portlet:namespace />toYear');
		var fromdayInp = A.one('#<portlet:namespace />fromDay');
		var frommonthInp = A.one('#<portlet:namespace />fromMonth');
		var fromyearInp = A.one('#<portlet:namespace />fromYear');	
		var currentURL = '<%=currentURL%>';
		
		<portlet:namespace />sentParamToSearch(fromDateInp.val(), toDateInp.val(), levelInp.val(), dossierStatusInp.val(),
				todayInp.val(), tomonthInp.val(), toyearInp.val(), fromdayInp.val(), frommonthInp.val(), fromyearInp.val(), currentURL);
		
		if(isAutoLoadCheckboxInp) {
			var isAutoRef = A.one('#<portlet:namespace />isAutoLoad');
			isAutoLoadCheckboxInp.on('click', function () {
				if(isAutoRef.val() == 'true') {
					setInterval((function(){
						<portlet:namespace />sentParamToSearch(fromDateInp.val(), toDateInp.val(), levelInp.val(), dossierStatusInp.val(),
								todayInp.val(), tomonthInp.val(), toyearInp.val(), fromdayInp.val(), frommonthInp.val(), fromyearInp.val(), currentURL);
					}), 60000);
				}
			});
			
		}
		
		if(btnSeach) {
			btnSeach.on('click', function() {
				<portlet:namespace />sentParamToSearch(fromDateInp.val(), toDateInp.val(), levelInp.val(), dossierStatusInp.val(),
						todayInp.val(), tomonthInp.val(), toyearInp.val(), fromdayInp.val(), frommonthInp.val(), fromyearInp.val(), currentURL);
			} );
		}
	});
	
	Liferay.provide(window, '<portlet:namespace />sentParamToSearch',
		function(fromDate, toDate, level, status, fromday,frommonth, fromyear
				, today, tomonth,toyear, currentURL ){
		
		var A = AUI();

		A.io.request(
				'<%=referentToSeachContaner.toString() %>',
				{
					dataType : 'text/html',
					method : 'GET',
				    data:{    
				    	"<portlet:namespace />fromDateReq" : fromDate,
				    	"<portlet:namespace />toDateReq" : toDate,
				    	"<portlet:namespace />levelReq" : level,
				    	"<portlet:namespace />statusReq" : status,
				    	
				    	"<portlet:namespace />fromDayReq" : fromday,
				    	"<portlet:namespace />fromMonthReq" : frommonth,
				    	"<portlet:namespace />fromYearReq" : fromyear,
				    	"<portlet:namespace />toDayReq" : today,
				    	"<portlet:namespace />toMonthReq" : tomonth,
				    	"<portlet:namespace />toYearReq" : toyear,
				    	"<portlet:namespace />currentURL" : currentURL
				    },   
				    on: {
				    	success: function(event, id, obj) {
							var instance = this;
							var res = instance.get('responseData');
							
							var loadSearchLog = A.one("#<portlet:namespace/>loadSearchLog");
							
							if(loadSearchLog){
								loadSearchLog.empty();
								loadSearchLog.html(res);
							}
								
						},
				    	error: function(){}
					}
				}
			);
	},['aui-base','aui-io']);
</aui:script>


