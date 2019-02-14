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

/**
 * "Data Transfer Object" (DTO) interface for an {@link com.liferay.docs.guestbook.model.Entry}. This interface
 * encapsulates the minimum necessary elements of an Entry in order to simplify the creation and transfer of Entries
 * between with JSF MVC classes such as beans. For example: in
 * {@link EntryBacking#saveNewEntry(javax.faces.context.FacesContext, com.liferay.docs.guestbook.bean.GuestbookManager)},
 * instead of creating a new full implementation of an Entry, a simpler implementation of EntryDTO can be created.
 * 
 * @author Kyle Stiemann
 */
public interface EntryDTO {
	public String getMessage();
	public String getName();
}
