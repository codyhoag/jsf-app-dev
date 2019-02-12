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
package com.liferay.docs.guestbook.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.liferay.docs.guestbook.service.EntryLocalService;
import com.liferay.docs.guestbook.services.EntryLocalServiceTracker;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.docs.guestbook.services.GuestbookLocalServiceTracker;
import com.liferay.docs.guestbook.wrappers.Entry;
import com.liferay.docs.guestbook.wrappers.Guestbook;

import com.liferay.faces.portal.context.LiferayPortletHelperUtil;
import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;


/**
 * @author Cody Hoag
 */
@ManagedBean
@ViewScoped
public class GuestbookBacking extends AbstractBacking {

	public static final String DEFAULT_GUESTBOOK_NAME = "Main";
	public static final String MODEL = "com.liferay.docs.guestbook";

	private GuestbookLocalServiceTracker guestbookLocalServiceTracker;
	private EntryLocalServiceTracker entryLocalServiceTracker;

	private Guestbook selectedGuestbook;
	
	private Entry selectedEntry;
	private List<Entry> entries;

	private boolean editingEntry;

	public void editEntry() {
		editingEntry = true;
	}

	@PostConstruct
	public void postConstruct() {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		guestbookLocalServiceTracker = new GuestbookLocalServiceTracker(bundleContext);
		guestbookLocalServiceTracker.open();
		entryLocalServiceTracker = new EntryLocalServiceTracker(bundleContext);
		entryLocalServiceTracker.open();
		
		createMainGuestbook();
	}

	@PreDestroy
	public void preDestroy() {
		guestbookLocalServiceTracker.close();
		entryLocalServiceTracker.close();
	}

	public void createMainGuestbook() {

		try {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);
			GuestbookLocalService guestbookLocalService = guestbookLocalServiceTracker.getService();

			com.liferay.docs.guestbook.model.Guestbook defaultGuestbook = (com.liferay.docs.guestbook.model.Guestbook)
				guestbookLocalService.getFirstGuestbookByName(scopeGroupId, DEFAULT_GUESTBOOK_NAME);

			// Create the default guestbook if it does not exist in the database
			if (defaultGuestbook == null) {
				logger.info("postConstruct: creating a default guestbook named " + DEFAULT_GUESTBOOK_NAME + " ...");

				Guestbook guestbook = new Guestbook(guestbookLocalService.createGuestbook(0L));
				guestbook.setName(DEFAULT_GUESTBOOK_NAME);
				guestbook.setGroupId(scopeGroupId);
				guestbook.setCompanyId(LiferayPortletHelperUtil.getCompanyId(facesContext));
				guestbook.setUserId(LiferayPortletHelperUtil.getUserId(facesContext));
				guestbookLocalService.addGuestbook(guestbook);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void select(Guestbook guestbook) {

		if (guestbook == null) {
			setSelectedGuestbook(null);
		}
		else {
			setSelectedGuestbook(guestbook);
		}

		// force Guestbooks and Entries to reload
		//setGuestbooks(null);
		setEntries(null);

		editingEntry = false;
		//editingGuestbook = false;
	}
	
	public void setEditingEntry(boolean editingEntry) {
		this.editingEntry = editingEntry;
	}

	public List<Entry> getEntries() {

		if (entries == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);

			try {
				EntryLocalService entryLocalService = entryLocalServiceTracker.getService();
				entries = new ArrayList<Entry>();
				Guestbook selectedGuestbook = getSelectedGuestbook();

				if (selectedGuestbook == null) {
					logger.info("getEntries: selectedGuestbook == null ... ");
				}
				else {
					List<com.liferay.docs.guestbook.model.Entry> list = entryLocalService.getEntries(scopeGroupId,
							selectedGuestbook.getGuestbookId());

					for (com.liferay.docs.guestbook.model.Entry entry : list) {
						entries.add(new Entry(entry));
					}
				}

			}
			catch (SystemException e) {
				logger.error(e);
			}
		}

		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public Entry getSelectedEntry() {
		return selectedEntry;
	}

	public void setSelectedEntry(Entry selectedEntry) {
		this.selectedEntry = new Entry(selectedEntry);
	}

	public Guestbook getSelectedGuestbook() {

		if (selectedGuestbook == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);

			try {
				GuestbookLocalService guestbookLocalService = guestbookLocalServiceTracker.getService();

				com.liferay.docs.guestbook.model.Guestbook firstGuestbookByName =
					(com.liferay.docs.guestbook.model.Guestbook) guestbookLocalService.getFirstGuestbookByName(
						scopeGroupId, DEFAULT_GUESTBOOK_NAME);

				if (firstGuestbookByName == null) {
					logger.info("getSelectedGuestbook: No Guestbook named " + DEFAULT_GUESTBOOK_NAME);
				}
				else {
					selectedGuestbook = new Guestbook(firstGuestbookByName);
				}
			}
			catch (SystemException e) {
				logger.error(e);
			}
		}

		return selectedGuestbook;
	}
	
	public void setSelectedGuestbook(Guestbook selectedGuestbook) {
		this.selectedGuestbook = selectedGuestbook;
	}
	
	public boolean isEditingEntry() {
		return editingEntry;
	}

}
