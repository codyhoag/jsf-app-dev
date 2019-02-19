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

import org.junit.Test;
import org.junit.Assert;

/**
 * @author Kyle Stiemann
 */
public class EntryBackingTest {

	@Test
	public void testNotBlankRegex() {
		Assert.assertFalse("".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertFalse(" ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertFalse("\t".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertFalse("\n".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertFalse("\r".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertFalse(" \t\n\r".matches(EntryBacking.NOT_BLANK_REGEX));

		Assert.assertTrue(" \t\n\ra".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue("a \t\n\r".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" a".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue("a ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" a ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" a a ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" .".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(". ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" . ".matches(EntryBacking.NOT_BLANK_REGEX));
		Assert.assertTrue(" . . ".matches(EntryBacking.NOT_BLANK_REGEX));
	}
}
