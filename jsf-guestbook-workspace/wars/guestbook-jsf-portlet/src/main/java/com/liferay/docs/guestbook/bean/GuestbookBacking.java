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
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.log4j.Logger;


/**
 * Backing bean for the <code>master.xhtml</code> view.
 * 
 * @author Cody Hoag
 * @author Kyle Stiemann
 */
@Named
@RequestScoped
public class GuestbookBacking {

	private static final Logger LOGGER = Logger.getLogger(GuestbookBacking.class);

	private List<EntryDTO> entries;

	public List<EntryDTO> getEntries(FacesContext facesContext, GuestbookManager guestbookManager) {

		if (entries == null) {
			entries = Collections.emptyList();

			try {
				entries = guestbookManager.getEntries(facesContext);
			}
			catch (GuestbookManager.UnableToObtainEntriesException e) {
				FacesContextHelperUtil.addGlobalErrorMessage(facesContext, "failed-to-obtain-x", "Entries");
				LOGGER.error(e);
			}
		}

		return entries;
	}
}
