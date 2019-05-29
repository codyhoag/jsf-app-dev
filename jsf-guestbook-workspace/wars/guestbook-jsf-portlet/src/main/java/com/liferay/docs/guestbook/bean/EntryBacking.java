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

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.log4j.Logger;


/**
 * Backing bean for the <code>entry.xhtml</code> view.
 * 
 * @author Cody Hoag
 * @author Kyle Stiemann
 */
@Named
@RequestScoped
public class EntryBacking {

	static final String NOT_BLANK_REGEX = "^[\\S\\s]*[\\S]+[\\S\\s]*$";

	private static final Logger LOGGER = Logger.getLogger(EntryBacking.class);

	@Valid
	private EntryBean entry;

	public EntryBean getEntry() {

		if (entry == null) {
			entry = new EntryBean();
		}

		return entry;
	}

	public String saveNewEntry(FacesContext facesContext, GuestbookManager guestbookManager) {
		String navigationOutcome = null;

		try {
			guestbookManager.addEntry(facesContext, entry);
			navigationOutcome = "master";
		}
		catch (GuestbookManager.UnableToAddEntryException e) {
			I18nUtil.addGlobalErrorMessage(facesContext, "failed-to-add-x", "Entry");
			LOGGER.error(e);
		}

		return navigationOutcome;
	}

	public static class EntryBean implements EntryDTO {

		private static final String VALUE_IS_REQUIRED_MESSAGE_KEY = "{value-is-required}";
		private static final String VALUE_MUST_NOT_BE_BLANK_MESSAGE_KEY = "{value-must-not-be-blank}";

		@Pattern(message = VALUE_MUST_NOT_BE_BLANK_MESSAGE_KEY, regexp = NOT_BLANK_REGEX)
		@NotNull(message = VALUE_IS_REQUIRED_MESSAGE_KEY)
		private String message;

		@Pattern(message = VALUE_MUST_NOT_BE_BLANK_MESSAGE_KEY, regexp = NOT_BLANK_REGEX)
		@NotNull(message = VALUE_IS_REQUIRED_MESSAGE_KEY)
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
