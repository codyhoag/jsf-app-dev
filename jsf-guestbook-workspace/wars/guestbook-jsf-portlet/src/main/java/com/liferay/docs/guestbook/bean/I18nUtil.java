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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * @author Kyle Stiemann
 */
public final class I18nUtil {

	static void addGlobalErrorMessage(FacesContext facesContext, String pattern, Object... arguments) {

		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();
		ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n", locale);
		String message;

		try {
			message = resourceBundle.getString(pattern);

			if (arguments.length > 0) {
				message = MessageFormat.format(message, arguments);
			}
		}
		catch (MissingResourceException e) {
			message = pattern;
		}

		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}
}
