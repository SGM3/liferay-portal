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

package com.liferay.portlet.documentlibrary.store;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Manuel de la Peña
 * @author Edward C. Han
 */
public class StoreFactory {

	public static StoreFactory getInstance() {
		if (_storeFactory == null) {
			_storeFactory = new StoreFactory();
		}

		return _storeFactory;
	}

	public StoreFactory() {
		_storeServiceTrackerMap = ServiceTrackerCollections.openSingleValueMap(
			Store.class, "store.type");
	}

	public void checkProperties() {
		if (_warned) {
			return;
		}

		String dlHookImpl = PropsUtil.get("dl.hook.impl");

		if (Validator.isNull(dlHookImpl)) {
			_warned = true;

			return;
		}

		boolean found = false;

		for (String key : _storeServiceTrackerMap.keySet()) {
			Store store = getStore(key);

			Class<?> clazz = store.getClass();

			String className = clazz.getName();

			if (dlHookImpl.equals(className)) {
				PropsValues.DL_STORE_IMPL = className;

				found = true;

				break;
			}
		}

		if (!found) {
			PropsValues.DL_STORE_IMPL = dlHookImpl;
		}

		if (_log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(11);

			sb.append("Liferay is configured with the legacy property ");
			sb.append("\"dl.hook.impl=");
			sb.append(dlHookImpl);
			sb.append("\" in portal-ext.properties. Please reconfigure to ");
			sb.append("use the new property \"");
			sb.append(PropsKeys.DL_STORE_IMPL);
			sb.append("\". Liferay will attempt to temporarily set \"");
			sb.append(PropsKeys.DL_STORE_IMPL);
			sb.append("=");
			sb.append(PropsValues.DL_STORE_IMPL);
			sb.append("\".");

			_log.warn(sb.toString());
		}

		_warned = true;
	}

	public void destroy() {
		_storeServiceTrackerMap.close();
	}

	public Store getStore() {
		Store store = _storeServiceTrackerMap.getService(
			PropsValues.DL_STORE_IMPL);

		if (store == null) {
			throw new IllegalStateException("Store is not available");
		}

		return store;
	}

	public Store getStore(String key) {
		return _storeServiceTrackerMap.getService(key);
	}

	public String[] getStoreTypes() {
		Set<String> storeTypes = _storeServiceTrackerMap.keySet();

		return storeTypes.toArray(new String[0]);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public void setStore(String key) {
	}

	private static final Log _log = LogFactoryUtil.getLog(StoreFactory.class);

	private static StoreFactory _storeFactory;
	private static boolean _warned;

	private final ServiceTrackerMap<String, Store> _storeServiceTrackerMap;

}