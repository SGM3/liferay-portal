/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portal.vulcan.internal.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.context.AcceptLanguage;
import com.liferay.portal.vulcan.internal.context.provider.test.util.MockMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class AcceptLanguageContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_availableLocales = LanguageUtil.getAvailableLocales();
		_defaultLocale = LocaleUtil.getDefault();

		_company = CompanyTestUtil.addCompany();

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(), Collections.singletonList(Locale.TAIWAN),
			Locale.TAIWAN);

		_company = CompanyLocalServiceUtil.getCompany(_company.getCompanyId());

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _user.getUserId(), 0L);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			PortalUtil.getDefaultCompanyId(), _availableLocales,
			_defaultLocale);

		CompanyLocalServiceUtil.deleteCompany(_company.getCompanyId());
	}

	@Test
	public void testCreateContextWithAuthenticatedUser() throws Exception {
		User user = UserTestUtil.addUser(
			_group.getGroupId(), LocaleUtil.BRAZIL);

		_testCreateContext(LocaleUtil.BRAZIL, user);
	}

	@Test
	public void testCreateContextWithDefaultUser() throws Exception {
		User user = _company.getDefaultUser();

		_testCreateContext(LocaleUtil.TAIWAN, user);
	}

	private void _testCreateContext(Locale userLocale, User user)
		throws Exception {

		// One locale

		AcceptLanguage acceptLanguage =
			_acceptLanguageContextProvider.createContext(
				new MockMessage(
					new AcceptLanguageMockHttpServletRequest(
						user, Locale.JAPAN)));

		Assert.assertEquals(Locale.JAPAN, acceptLanguage.getPreferredLocale());

		// Three locales

		acceptLanguage = _acceptLanguageContextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					user, Locale.GERMAN, Locale.JAPAN, Locale.US)));

		Assert.assertEquals(Locale.GERMAN, acceptLanguage.getPreferredLocale());

		// No locales

		Assert.assertEquals(userLocale, user.getLocale());

		acceptLanguage = _acceptLanguageContextProvider.createContext(
			new MockMessage(new AcceptLanguageMockHttpServletRequest(user)));

		Assert.assertEquals(
			user.getLocale(), acceptLanguage.getPreferredLocale());
	}

	private static Set<Locale> _availableLocales;
	private static Company _company;
	private static Locale _defaultLocale;
	private static Group _group;
	private static User _user;

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.context.provider.AcceptLanguageContextProvider"
	)
	private ContextProvider<AcceptLanguage> _acceptLanguageContextProvider;

	private class AcceptLanguageMockHttpServletRequest
		extends MockHttpServletRequest {

		public AcceptLanguageMockHttpServletRequest(
				User user, Locale... locales)
			throws PortalException {

			if (ArrayUtil.isNotEmpty(locales)) {
				addHeader(
					HttpHeaders.ACCEPT_LANGUAGE,
					StringUtil.merge(
						LocaleUtil.toW3cLanguageIds(locales),
						StringPool.COMMA));
			}

			addHeader("Host", _company.getVirtualHostname());

			if (!user.isDefaultUser()) {
				setAttribute(WebKeys.USER_ID, user.getUserId());
			}

			if (ArrayUtil.isNotEmpty(locales)) {
				setPreferredLocales(Arrays.asList(locales));
			}

			setRemoteHost(_company.getPortalURL(_group.getGroupId()));
		}

	}

}