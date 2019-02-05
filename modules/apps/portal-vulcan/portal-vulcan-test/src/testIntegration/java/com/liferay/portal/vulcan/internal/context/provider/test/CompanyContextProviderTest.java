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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.context.provider.test.util.MockMessage;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class CompanyContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCreateContextWithCustomCompany() throws Exception {
		Company actualCompany = CompanyTestUtil.addCompany();

		try {
			User user = UserTestUtil.addCompanyAdminUser(actualCompany);

			Group group = GroupTestUtil.addGroup(
				actualCompany.getCompanyId(), user.getUserId(), 0L);

			CompanyMockHttpServletRequest companyMockHttpServletRequest =
				new CompanyMockHttpServletRequest(actualCompany, group);

			Company finalCompany = _companyContextProvider.createContext(
				new MockMessage(companyMockHttpServletRequest));

			Assert.assertEquals(actualCompany, finalCompany);
		}
		finally {
			CompanyLocalServiceUtil.deleteCompany(actualCompany.getCompanyId());
		}
	}

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.context.provider.CompanyContextProvider"
	)
	private ContextProvider<Company> _companyContextProvider;

	private class CompanyMockHttpServletRequest extends MockHttpServletRequest {

		public CompanyMockHttpServletRequest(Company company, Group group)
			throws PortalException {

			addHeader("Host", company.getVirtualHostname());

			setRemoteHost(company.getPortalURL(group.getGroupId()));
		}

	}

}