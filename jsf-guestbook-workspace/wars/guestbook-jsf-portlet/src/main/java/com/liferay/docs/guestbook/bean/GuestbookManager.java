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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * <p>This {@link ApplicationScoped} bean encapsulates the database or service that manages
 * {@link com.liferay.docs.guestbook.model.Guestbook}s providing a simple interface that backing beans can utilize to
 * add or obtain entries from the default Guestbook. This class ensures that:</p>
 * 
 * <p>1. JSF backing beans can create and obtain entries without full, direct knowledge of the underlying service API or
 *    database that is required to manage a Guestbook. Backing beans and views are more portable this way.</p>
 * <p>2. {@link org.osgi.util.tracker.ServiceTracker}s for services are only created once in the {@link PostConstruct}.
 * </p>
 * <p>3. The default Guestbook is only obtained or created once in the PostConstruct.</p>
 * 
 * <p>This class must be thread-safe. {@link PostConstruct} and {@link PreDestroy} are executed in a single-threaded
 * environment.</p>
 * 
 * @author Kyle Stiemann
 */
@Named
@ApplicationScoped
public class GuestbookManager {

	List<EntryDTO> guestbookEntriesMockDatabaseService;

	/**
	 * Adds an {@link com.liferay.docs.guestbook.model.Entry} (created from {@link EntryDTO}) to the current
	 * {@link com.liferay.docs.guestbook.model.Guestbook}.
	 * @param facesContext the current {@link FacesContext}.
	 * @param entryDTO the data-transfer-object of the Entry to add to the current Guestbook.
	 * @throws com.liferay.docs.guestbook.bean.GuestbookManager.UnableToAddEntryException when an entry cannot be added
	 *         to the current Guestbook.
	 */
	void addEntry(FacesContext facesContext, EntryDTO entryDTO) throws UnableToAddEntryException {

		try {
			guestbookEntriesMockDatabaseService.add(entryDTO);
		}
		catch (Exception e) {
			throw new UnableToAddEntryException(e);
		}
	}

	/**
	 * @return a {@link Collections#unmodifiableList(java.util.List)} of {@link EntryDTO} items from the current
	 *         {@link com.liferay.docs.guestbook.model.Guestbook}.
	 * @throws com.liferay.docs.guestbook.bean.GuestbookManager.UnableToObtainEntriesException when the entries in the
	 *         current Guestbook cannot be obtained.
	 */
	List<EntryDTO> getEntries(FacesContext facesContext) throws UnableToObtainEntriesException {

		try {
			return Collections.unmodifiableList(guestbookEntriesMockDatabaseService.stream().map((entry) -> {
				return new UnmodifiableEntryDTO(entry.getMessage(), entry.getName());
			}).collect(Collectors.toList()));
		}
		catch (Exception e) {
			throw new UnableToObtainEntriesException(e);
		}
	}

	@PostConstruct
	public void postConstruct() {
		guestbookEntriesMockDatabaseService = Collections.synchronizedList(new ArrayList<>());
	}

	@PreDestroy
	public void preDestroy() {
		// no-op
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

	private static final class UnmodifiableEntryDTO implements EntryDTO {

		private final String message;
		private final String name;

		public UnmodifiableEntryDTO(String message, String name) {
			this.message = message;
			this.name = name;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
