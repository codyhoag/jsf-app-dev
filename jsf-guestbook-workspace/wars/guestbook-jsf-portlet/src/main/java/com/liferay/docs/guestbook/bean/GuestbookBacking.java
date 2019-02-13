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
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


/**
 * @author Cody Hoag
 * @author Kyle Stiemann
 */
@ManagedBean
@ViewScoped
public final class GuestbookBacking {

	private static final Logger LOGGER = LoggerFactory.getLogger(GuestbookBacking.class);

	private EntryBean newEntry;

	public void addNewEntry() {
		setNewEntry(new EntryBean());
	}

	public void clearNewEntry() {
		setNewEntry(null);
	}

	public boolean isAddingNewEntry() {
		return newEntry != null;
	}

	public EntryBean getNewEntry() {
		return newEntry;
	}

	public void setNewEntry(EntryBean entry) {
		this.newEntry = entry;
	}

	public void saveNewEntry(FacesContext facesContext) {
		GuestbookManager guestbookManager = GuestbookManager.getGuestbookManager(facesContext);

		try {
			guestbookManager.addEntry(facesContext, newEntry);
			clearNewEntry();
		}
		catch (GuestbookManager.UnableToAddEntryException e) {
			FacesContextHelperUtil.addGlobalErrorMessage("failed-to-add-x", "Entry");
			LOGGER.error(e);
		}
	}

	public List<EntryBean> getEntries(FacesContext facesContext) {

		List<EntryBean> entries = Collections.emptyList();

		try {
			GuestbookManager guestbookManager = GuestbookManager.getGuestbookManager(facesContext);
			entries = guestbookManager.getEntries(facesContext);
		}
		catch (GuestbookManager.UnableToObtainEntriesException e) {
			FacesContextHelperUtil.addGlobalErrorMessage("failed-to-obtain-x", "Entries");
			LOGGER.error(e);
		}

		return entries;
	}
}
