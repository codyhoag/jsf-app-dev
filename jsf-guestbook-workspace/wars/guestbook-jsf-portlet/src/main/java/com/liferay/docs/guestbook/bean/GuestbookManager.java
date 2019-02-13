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

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.docs.guestbook.model.Entry;
import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.service.EntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.faces.portal.context.LiferayPortletHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class must be thread-safe. {@link PostConstruct} and {@link PreDestroy} are executed in a single-threaded
 * environment.
 * 
 * @author Kyle Stiemann
 */
@ManagedBean
@ApplicationScoped
public final class GuestbookManager {

	private static final String DEFAULT_GUESTBOOK_NAME = "Main";
	private static final Logger LOGGER = LoggerFactory.getLogger(GuestbookManager.class);

	private ServiceTracker<CounterLocalService, CounterLocalService> counterLocalServiceTracker;
	private ServiceTracker<EntryLocalService, EntryLocalService> entryLocalServiceTracker;
	private ServiceTracker<GuestbookLocalService, GuestbookLocalService> guestbookLocalServiceTracker;
	private Guestbook defaultGuestbook;

	static GuestbookManager getGuestbookManager(FacesContext facesContext) {

		// Use Application.evaluateExpressionGet() (instead of ApplicationMap.get()) to ensure that this bean is
		// instantiated before it is provided to the caller.
		return (GuestbookManager) facesContext.getApplication()
				.evaluateExpressionGet(facesContext, "#{guestbookManager}", GuestbookManager.class);
	}

	/**
	 * Adds an {@link Entry} (created from {@link EntryBean}) to the current {@link Guestbook}.
	 * @param facesContext the current {@link FacesContext}.
	 * @param entryBean the data-transfer-object of the Entry to add to the current Guestbook.
	 * @throws com.liferay.docs.guestbook.bean.GuestbookManager.UnableToAddEntryException 
	 */
	void addEntry(FacesContext facesContext, EntryBean entryBean) throws UnableToAddEntryException {

		try {
			CounterLocalService counterLocalService = counterLocalServiceTracker.getService();
			long entryId = counterLocalService.increment(Entry.class.getName());
			EntryLocalService entryLocalService = entryLocalServiceTracker.getService();
			Entry entry = entryLocalService.createEntry(entryId);

			long companyId = LiferayPortletHelperUtil.getCompanyId(facesContext);
			entry.setCompanyId(companyId);

			long guestbookId = defaultGuestbook.getGuestbookId();
			entry.setGuestbookId(guestbookId);

			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);
			entry.setGroupId(scopeGroupId);

			String message = entryBean.getMessage();
			entry.setMessage(message);

			String name = entryBean.getName();
			entry.setName(name);

			long userId = LiferayPortletHelperUtil.getUserId(facesContext);
			entry.setUserId(userId);
			entryLocalService.addEntry(entry);
		}
		catch (Exception e) {
			throw new UnableToAddEntryException(e);
		}
	}

	/**
	 * @return a {@link Collections#unmodifiableList(java.util.List)} of {@link EntryBean} items from the current
	 *         {@link Guestbook}.
	 * @throws com.liferay.docs.guestbook.bean.GuestbookManager.UnableToObtainEntriesException when the entries in the
	 *         current {@link Guestbook} cannot be obtained.
	 */
	List<EntryBean> getEntries(FacesContext facesContext) throws UnableToObtainEntriesException {

		try {
			EntryLocalService entryLocalService = entryLocalServiceTracker.getService();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);
			long guestbookId = defaultGuestbook.getGuestbookId();
			List<Entry> entries = entryLocalService.getEntries(scopeGroupId, guestbookId);
			return Collections.unmodifiableList(entries.stream().map((entry) -> {
				return new EntryBean(entry.getMessage(), entry.getName());
			}).collect(Collectors.toList()));
		}
		catch (Exception e) {
			throw new UnableToObtainEntriesException(e);
		}
	}

	@PostConstruct
	public void postConstruct() {

		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		guestbookLocalServiceTracker = new ServiceTracker<>(bundleContext, GuestbookLocalService.class, null);
		guestbookLocalServiceTracker.open();
		counterLocalServiceTracker = new ServiceTracker<>(bundleContext, CounterLocalService.class, null);
		counterLocalServiceTracker.open();
		entryLocalServiceTracker = new ServiceTracker<>(bundleContext, EntryLocalService.class, null);
		entryLocalServiceTracker.open();

		try {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);
			GuestbookLocalService guestbookLocalService = guestbookLocalServiceTracker.getService();
			defaultGuestbook = (Guestbook) guestbookLocalService.getFirstGuestbookByName(scopeGroupId,
				DEFAULT_GUESTBOOK_NAME);

			// Create the default guestbook if it does not exist in the database
			if (defaultGuestbook == null) {
				LOGGER.info("postConstruct: creating a default guestbook named " + DEFAULT_GUESTBOOK_NAME + " ...");

				CounterLocalService counterLocalService = counterLocalServiceTracker.getService();
				long guestbookId = counterLocalService.increment(Guestbook.class.getName());
				defaultGuestbook = guestbookLocalService.createGuestbook(guestbookId);
				defaultGuestbook.setName(DEFAULT_GUESTBOOK_NAME);
				defaultGuestbook.setGroupId(scopeGroupId);
				defaultGuestbook.setCompanyId(LiferayPortletHelperUtil.getCompanyId(facesContext));
				defaultGuestbook.setUserId(LiferayPortletHelperUtil.getUserId(facesContext));
				guestbookLocalService.addGuestbook(defaultGuestbook);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to create or obtain the default guestbook.", e);
		}
	}

	@PreDestroy
	public void preDestroy() {

		entryLocalServiceTracker.close();
		counterLocalServiceTracker.close();
		guestbookLocalServiceTracker.close();
	}

	static final class UnableToAddEntryException extends Exception {

		private static final long serialVersionUID = -5947091021866881684L;

		UnableToAddEntryException(Throwable cause) {
			super(cause);
		}
	}

	static final class UnableToObtainEntriesException extends Exception {

		private static final long serialVersionUID = -4732770829320123878L;

		UnableToObtainEntriesException(Throwable cause) {
			super(cause);
		}
	}
}
