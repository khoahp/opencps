<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ include file="/html/portlet/login/init.jsp" %>

<%
	String linkToRegister = themeDisplay.getPortalURL() + "/register";
	String linkDescriptToUse = themeDisplay.getPortalURL() + "/huong-dan-su-dung";
%>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">

		<%
		String signedInAs = HtmlUtil.escape(user.getFullName());

		if (themeDisplay.isShowMyAccountIcon() && (themeDisplay.getURLMyAccount() != null)) {
			String myAccountURL = String.valueOf(themeDisplay.getURLMyAccount());

			if (PropsValues.DOCKBAR_ADMINISTRATIVE_LINKS_SHOW_IN_POP_UP) {
				signedInAs = "<a class=\"signed-in\" href=\"javascript:Liferay.Util.openWindow({dialog: {destroyOnHide: true}, title: '" + HtmlUtil.escapeJS(LanguageUtil.get(pageContext, "my-account")) + "', uri: '" + HtmlUtil.escapeJS(myAccountURL) + "'});\">" + signedInAs + "</a>";
			}
			else {
				myAccountURL = HttpUtil.setParameter(myAccountURL, "controlPanelCategory", PortletCategoryKeys.MY);

				signedInAs = "<a class=\"signed-in\" href=\"" + HtmlUtil.escape(myAccountURL) + "\">" + signedInAs + "</a>";
			}
		}
		%>

		<%= LanguageUtil.format(pageContext, "you-are-signed-in-as-x", signedInAs, false) %>
	</c:when>
	<c:otherwise>

		<%
		String redirect = ParamUtil.getString(request, "redirect");

		String login = LoginUtil.getLogin(request, "login", company);
		String password = StringPool.BLANK;
		boolean rememberMe = ParamUtil.getBoolean(request, "rememberMe");

		if (Validator.isNull(authType)) {
			authType = company.getAuthType();
		}
		if(login.startsWith("@")){
			login = StringPool.BLANK;
		}
		%>

		<portlet:actionURL secure="<%= PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS || request.isSecure() %>" var="loginURL">
			<portlet:param name="struts_action" value="/login/login" />
		</portlet:actionURL>

		<aui:form action="<%= loginURL %>" autocomplete='<%= PropsValues.COMPANY_SECURITY_LOGIN_FORM_AUTOCOMPLETE ? "on" : "off" %>' cssClass="sign-in-form" method="post" name="fm" onSubmit="event.preventDefault();">
			<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="doActionAfterLogin" type="hidden" value="<%= portletName.equals(PortletKeys.FAST_LOGIN) ? true : false %>" />

			<c:choose>
				<c:when test='<%= SessionMessages.contains(request, "userAdded") %>'>

					<%
					String userEmailAddress = (String)SessionMessages.get(request, "userAdded");
					String userPassword = (String)SessionMessages.get(request, "userAddedPassword");
					%>

					<div class="alert alert-success">
						<c:choose>
							<c:when test="<%= company.isStrangersVerify() || Validator.isNull(userPassword) %>">
								<%= LanguageUtil.get(pageContext, "thank-you-for-creating-an-account") %>

								<c:if test="<%= company.isStrangersVerify() %>">
									<%= LanguageUtil.format(pageContext, "your-email-verification-code-has-been-sent-to-x", userEmailAddress) %>
								</c:if>
							</c:when>
							<c:otherwise>
								<%= LanguageUtil.format(pageContext, "thank-you-for-creating-an-account.-your-password-is-x", userPassword, false) %>
							</c:otherwise>
						</c:choose>

						<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_ENABLED) %>">
							<%= LanguageUtil.format(pageContext, "your-password-has-been-sent-to-x", userEmailAddress) %>
						</c:if>
					</div>
				</c:when>
				<c:when test='<%= SessionMessages.contains(request, "userPending") %>'>

					<%
					String userEmailAddress = (String)SessionMessages.get(request, "userPending");
					%>

					<div class="alert alert-success">
						<%= LanguageUtil.format(pageContext, "thank-you-for-creating-an-account.-you-will-be-notified-via-email-at-x-when-your-account-has-been-approved", userEmailAddress) %>
					</div>
				</c:when>
			</c:choose>

			<liferay-ui:error exception="<%= AuthException.class %>" message="authentication-failed" />
			<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="unable-to-login-because-the-maximum-number-of-users-has-been-reached" />
			<liferay-ui:error exception="<%= CookieNotSupportedException.class %>" message="authentication-failed-please-enable-browser-cookies" />
			<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="authentication-failed" />
			<liferay-ui:error exception="<%= PasswordExpiredException.class %>" message="your-password-has-expired" />
			<liferay-ui:error exception="<%= UserEmailAddressException.class %>" message="authentication-failed" />
			<liferay-ui:error exception="<%= UserLockoutException.class %>" message="this-account-has-been-locked" />
			<liferay-ui:error exception="<%= UserPasswordException.class %>" message="authentication-failed" />
			<liferay-ui:error exception="<%= UserScreenNameException.class %>" message="authentication-failed" />
			
			<c:choose>
				<c:when test='<%=displayStyle.equals("default") %>'>
					<div class="default-sign-in-wrapper">
						<aui:fieldset>
			
							<%
							String loginLabel = null;
			
							if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
								loginLabel = "email-address";
							}
							else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
								loginLabel = "screen-name";
							}
							else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
								loginLabel = "id";
							}
							%>
							
							<aui:input autoFocus="<%= windowState.equals(LiferayWindowState.EXCLUSIVE) || windowState.equals(WindowState.MAXIMIZED) %>" cssClass="clearable" label="<%= loginLabel %>" name="login" showRequiredLabel="<%= false %>" type="text" value="<%= login %>">
								<aui:validator name="required" />
							</aui:input>
			
							<aui:input name="password" showRequiredLabel="<%= false %>" type="password" value="<%= password %>">
								<aui:validator name="required" />
							</aui:input>
			
							<span id="<portlet:namespace />passwordCapsLockSpan" style="display: none;"><liferay-ui:message key="caps-lock-is-on" /></span>
							
							<c:if test="<%= company.isAutoLogin() && !PropsValues.SESSION_DISABLED %>">
								<aui:input checked="<%= rememberMe %>" name="rememberMe" type="checkbox" />
							</c:if>
						</aui:fieldset>
		
						<aui:button-row>
							<aui:button type="submit" value="sign-in" />
						</aui:button-row>
					</div>
				</c:when>
				<c:when test='<%=displayStyle.equals("compact") %>'>
					
					<div class="compact-sign-in-wrapper">
						<div class="default-sign-in-compact-top">
							
							<!-- login input -->
							<aui:fieldset>
								<%
								String loginLabel = null;
				
								if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
									loginLabel = "email-address";
								}
								else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
									loginLabel = "screen-name";
								}
								else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
									loginLabel = "id";
								}
								%>
								<div class="input-prepend">
									<!-- <span class="add-on"><i class="fa fa-envelope-o"></i></span> -->
									<aui:input autoFocus="<%= windowState.equals(LiferayWindowState.EXCLUSIVE) || windowState.equals(WindowState.MAXIMIZED) %>" 
										cssClass="clearable user-name" 
										label="<%= StringPool.BLANK %>" 
										name="login" showRequiredLabel="<%= false %>" 
										type="text" value="<%= login %>"
										placeholder="<%= LanguageUtil.get(pageContext, \"login-placehoder\") %>" 
									>
										<aui:validator name="required" />
									</aui:input>
								</div>
								<div class="input-prepend">
									<!-- <span class="add-on"><i class="fa fa-key"></i></span> -->
									<aui:input name="password" showRequiredLabel="<%= false %>" 
										cssClass="password" 
										type="password" value="<%= password %>" 
										label="<%=StringPool.BLANK %>"
										placeholder="<%= LanguageUtil.get(pageContext, \"password-placehoder\") %>" 
									>
										<aui:validator name="required" />
									</aui:input>
								</div>
								
								<span id="<portlet:namespace />passwordCapsLockSpan" style="display: none;"><liferay-ui:message key="caps-lock-is-on" /></span>
								
							</aui:fieldset>
							<aui:row>
								<aui:col width = "50">
									<aui:a href="#">
										<liferay-ui:message key="forgot-password"/>
									</aui:a>
								</aui:col>
									
								<aui:col width = "50">
									<aui:button type="submit" value="sign-in-cus" />
								</aui:col>
							</aui:row>
							
						</div>
						
						<div class="advance-signin-compact-bottom">
							<div class="button-wrapper signature">
								<aui:button name="sign-in-with-digital-signature" value="sign-in-with-digital-signature" cssClass="btn-signature-signin"/>
							</div>
							
							<aui:row>
								<aui:col width = "50">
									<span class="create-account">
										<liferay-portlet:renderURL var="linkToPage"></liferay-portlet:renderURL>
										<aui:a href="<%=linkToRegister %>">
											<liferay-ui:message key="create-account"/>
										</aui:a>
									</span>
								</aui:col>
								<aui:col width = "50">
									<span class="create-account">
										<aui:a href="<%=linkDescriptToUse %>">
											<liferay-ui:message key="descript-to-use"/>
										</aui:a>
									</span>
								</aui:col>
							</aui:row>
							
						</div>
						
					</div>
					
				</c:when>
				<c:otherwise>
					<div class="ux-sign-in-wrapper">
						<div class="default-sign-in">
							<div class="sign-in-header">
								<span class="title">
									<liferay-ui:message key="sign-in"/>
								</span>
								<span class="create-account">
									<liferay-portlet:renderURL var="linkToPage"></liferay-portlet:renderURL>
									<aui:a href="<%=linkToRegister %>">
										<liferay-ui:message key="create-account"/>
									</aui:a>
								</span>
							</div>
							<aui:fieldset>
								<%
								String loginLabel = null;
				
								if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
									loginLabel = "email-address";
								}
								else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
									loginLabel = "screen-name";
								}
								else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
									loginLabel = "id";
								}
								%>
								<div class="input-prepend">
									<span class="add-on"><i class="fa fa-envelope-o"></i></span>
									<aui:input autoFocus="<%= windowState.equals(LiferayWindowState.EXCLUSIVE) || windowState.equals(WindowState.MAXIMIZED) %>" 
										cssClass="clearable user-name" 
										label="<%= StringPool.BLANK %>" 
										name="login" showRequiredLabel="<%= false %>" 
										type="text" value="<%= login %>"
										placeholder="<%= LanguageUtil.get(pageContext, \"login-placehoder\") %>" 
									>
										<aui:validator name="required" />
									</aui:input>
								</div>
								<div class="input-prepend">
									<span class="add-on"><i class="fa fa-key"></i></span>
									<aui:input name="password" showRequiredLabel="<%= false %>" 
										cssClass="password" 
										type="password" value="<%= password %>" 
										label="<%=StringPool.BLANK %>"
										placeholder="<%= LanguageUtil.get(pageContext, \"password-placehoder\") %>" 
									>
										<aui:validator name="required" />
									</aui:input>
								</div>
								
								<span id="<portlet:namespace />passwordCapsLockSpan" style="display: none;"><liferay-ui:message key="caps-lock-is-on" /></span>
								
							</aui:fieldset>
			
							<aui:button-row>
								<aui:button type="submit" value="sign-in-cus" />
							</aui:button-row>
							
							<div class="forgot-password">
								<aui:a href="#">
									<liferay-ui:message key="forgot-password"/>
								</aui:a>
							</div>
						</div>
						<div class="advance-signin">
							<div class="sign-in-label">
								<liferay-ui:message key="sign-in-with-facebook"/>
							</div>
							<div class="button-wrapper fb">
								<aui:button name="sign-in-with-facebook" value="sign-in-with-facebook" cssClass="btn-fb-signin">
									<i class="fa fa-facebook MR5"></i>
								</aui:button>
							</div>
							<div class="split">
								<span class="text-label">
									<liferay-ui:message key="or"/>
								</span>
							</div>
							
							<div class="sign-in-label">
								<liferay-ui:message key="sign-in-with-digital-signature"/>
							</div>
							<div class="input-prepend">
		                        <span class="add-on"><i class="fa fa-key"></i></span>
		                        <aui:input autoFocus="<%= windowState.equals(LiferayWindowState.EXCLUSIVE) || windowState.equals(WindowState.MAXIMIZED) %>" 
									cssClass="digital-signature" 
									label="<%= StringPool.BLANK %>" 
									name="ignature" showRequiredLabel="<%= false %>" 
									type="text" value=""
									placeholder="digital-signature"
								>
								</aui:input>
		                    </div>
		                    
		                    <div class="button-wrapper signature">
								<aui:button name="sign-in-with-digital-signature" value="sign-in-with-digital-signature" cssClass="btn-signature-signin"/>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</aui:form>

		<c:if test='<%=displayStyle.equals("default") %>'>
			<liferay-util:include page="/html/portlet/login/navigation.jsp" />
		</c:if>
		
		<aui:script use="aui-base">
			var form = A.one(document.<portlet:namespace />fm);

			form.on(
				'submit',
				function(event) {
					var redirect = form.one('#<portlet:namespace />redirect');

					if (redirect) {
						var redirectVal = redirect.val();

						redirect.val(redirectVal + window.location.hash);
					}

					submitForm(form);
				}
			);

			var password = form.one('#<portlet:namespace />password');

			if (password) {
				password.on(
					'keypress',
					function(event) {
						Liferay.Util.showCapsLock(event, '<portlet:namespace />passwordCapsLockSpan');
					}
				);
			}
		</aui:script>
	</c:otherwise>
</c:choose>