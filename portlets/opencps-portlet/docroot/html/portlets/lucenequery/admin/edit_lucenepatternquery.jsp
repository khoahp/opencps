
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.liferay.portal.model.Layout"%>
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
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="org.opencps.lucenequery.model.LuceneQueryPattern"%>
<%@page import="org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../init.jsp"%>

<%
	LuceneQueryPattern luceneQueryPattern = null;

	long patternId = ParamUtil.getLong(request, "patternId");
	
	String backURL = ParamUtil.getString(request, "backURL");
	if(patternId > 0){
		try{
			luceneQueryPattern = LuceneQueryPatternLocalServiceUtil.getLuceneQueryPattern(patternId);
		}catch(Exception e){
			
		}
	}
	
	//List<Layout> privateLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, true);
	
%>

<portlet:actionURL var="editPatternURL" name="editPattern"/>

<liferay-ui:header 
	backURL="<%= backURL %>"
	title='<%=luceneQueryPattern != null ? "update-pattern" : "add-pattern" %>' 
/>



<aui:form name="fm" method="post" action="<%=editPatternURL.toString() %>">

	<aui:model-context bean="<%= luceneQueryPattern %>" model="<%= LuceneQueryPattern.class %>" />
	<div class="span6">
		<aui:input name="patternId" type="hidden"/>
		<aui:input name="name" cssClass="input100">
			<aui:validator name="required"/>
		</aui:input>
		<aui:input name="pattern" type="textarea" cssClass="input100">
			<aui:validator name="required"/>
		</aui:input>
		<label><i>
		ex: (receptionNo = ?) [and] (serviceName LIKE ? ) [not] (submitDatetime BETWEEN ??) [or] (serviceDomainCode = ?)
		</i></label>
		
		<%-- <c:if test="<%=privateLayouts != null %>">
			<aui:select name="layoutUUID" label="linkToPage">
				<%
					for(Layout privateLayout : privateLayouts){
						%>
							<aui:option value="<%=privateLayout.getUuid() %>"><%=privateLayout.getName(locale) %></aui:option>
						<%
					}
				%>
			</aui:select>
		</c:if> --%>
		<aui:input name="url" cssClass="input100"/>
		<aui:button type="submit" name="save" value="save"/>
	</div>
	
	<div class="span5">
		<label>Các khóa truy vấn cố định</label>
		<table border="1" borderColor="#ccc" width="100%" class="keydescription">
		  <tr>
		    <th>Khóa</th>
		    <th>Mô tả</th>
		  </tr>
		  <tr>
		    <td>receptionNo</td>
		    <td>Mã số tiếp nhận</td>
		  </tr>
		  
		  <tr>
		    <td>modifiedDate</td>
		    <td>Ngày cập nhật</td>
		  </tr>
		  <tr>
		    <td>cityName</td>
		    <td>Tên tỉnh/thành phố</td>
		  </tr>
		  <tr>
		    <td>govAgencyName</td>
		    <td>Tên cơ quan xử lý</td>
		  </tr>
		  <tr>
		    <td>govAgencyCode</td>
		    <td>Mã cơ quan xử ý</td>
		  </tr>
		  <tr>
		    <td>serviceDomainCode</td>
		    <td>Mã lĩnh vực</td>
		  </tr>
		  <tr>
		    <td>dossierStatus</td>
		    <td>Trạng thái hồ sơ</td>
		  </tr>
		  <tr>
		    <td>serviceName</td>
		    <td>Tên thủ tục</td>
		  </tr>
		  <tr>
		    <td>submitDatetime</td>
		    <td>Ngày gửi hồ sơ</td>
		  </tr>
		  <tr>
		    <td>receiveDatetime</td>
		    <td>Ngày tiếp nhận</td>
		  </tr>
		  <tr>
		    <td>estimateDatetime</td>
		    <td>Ngày dự kiến hoàn thành</td>
		  </tr>
		  <tr>
		    <td>finishDatetime</td>
		    <td>Ngày hoàn thành</td>
		  </tr>
		</table>
	</div>
	
</aui:form>