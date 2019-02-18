/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.docs.guestbook.bean;

import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;


/**
 * Backing bean for the <code>entry.xhtml</code> view.
 * 
 * @author Cody Hoag
 * @author Kyle Stiemann
 */
@Named
@RequestScoped
public class EntryBacking {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntryBacking.class);

	private EntryBean entry;

	public EntryBean getEntry() {

		if (entry == null) {
			entry = new EntryBean();
		}

		return entry;
	}

	public void setEntry(EntryBean entry) {
		this.entry = entry;
	}

	public String saveNewEntry(FacesContext facesContext, GuestbookManager guestbookManager) {
		String navigationOutcome = null;

		try {
			guestbookManager.addEntry(facesContext, entry);
			navigationOutcome = "master";
		}
		catch (GuestbookManager.UnableToAddEntryException e) {
			FacesContextHelperUtil.addGlobalErrorMessage(facesContext, "failed-to-add-x", "Entry");
			LOGGER.error(e);
		}

		return navigationOutcome;
	}

	public static class EntryBean implements EntryDTO {

		private String message;
		private String name;

		@Override
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
