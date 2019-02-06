/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.docs.guestbook.wrappers;

import javax.faces.context.FacesContext;

import com.liferay.docs.guestbook.model.EntryWrapper;
import com.liferay.faces.portal.context.LiferayPortletHelperUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;

/**
 * @author Cody Hoag
 */
public class Entry extends EntryWrapper {

	private static final String MODEL = "com.liferay.docs.guestbook.model.Entry";

	public Entry(com.liferay.docs.guestbook.model.Entry entry) {
		super(entry);
	}

}
