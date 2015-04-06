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

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.settings.SettingsLocatorHelper;

/**
 * @author Ivan Zaera
 * @author Jorge Ferrer
 */
public class PortletInstanceSettingsLocator implements SettingsLocator {

	public PortletInstanceSettingsLocator(Layout layout, String portletId) {
		_layout = layout;
		_portletId = portletId;
	}

	public String getSettingsId() {
		return _portletId;
	}

	public Settings retrieveSettings() throws SettingsException {
		long companyId = getCompanyId(_layout.getGroupId());

		Settings portalPropertiesSettings =
			_settingsLocatorHelper.getPortalPropertiesSettings();

		Settings configurationBeanSettings =
			_settingsLocatorHelper.getConfigurationBeanSettings(
				_portletId, portalPropertiesSettings);

		Settings portalPreferencesSettings =
			_settingsLocatorHelper.getPortalPreferencesSettings(
				companyId, configurationBeanSettings);

		Settings companyPortletPreferencesSettings =
			_settingsLocatorHelper.getCompanyPortletPreferencesSettings(
				companyId, _portletId, portalPreferencesSettings);

		Settings groupPortletPreferencesSettings =
			_settingsLocatorHelper.getGroupPortletPreferencesSettings(
				_layout.getGroupId(), _portletId,
				companyPortletPreferencesSettings);

		return
			_settingsLocatorHelper.getPortletInstancePortletPreferencesSettings(
				_layout, _portletId, groupPortletPreferencesSettings);
	}

	protected long getCompanyId(long groupId) throws SettingsException {
		try {
			Group group = GroupLocalServiceUtil.getGroup(groupId);

			return group.getCompanyId();
		}
		catch (PortalException pe) {
			throw new SettingsException(pe);
		}
	}

	private final Layout _layout;
	private final String _portletId;
	private final SettingsLocatorHelper _settingsLocatorHelper =
		new SettingsLocatorHelper();

}