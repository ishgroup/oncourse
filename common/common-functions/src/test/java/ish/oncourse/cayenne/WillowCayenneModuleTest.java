/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import org.apache.cayenne.access.types.ExtendedType;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.di.*;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * User: akoiro
 * Date: 22/3/18
 */
public class WillowCayenneModuleTest {

	@Test
	public void test_CI_PROPERTY() {
		QueryCache queryCache = Mockito.mock(QueryCache.class);
		Binder binder = Mockito.mock(Binder.class);
		MapBuilder mapBuilder = Mockito.mock(MapBuilder.class);
		ListBuilder listBuilder = Mockito.mock(ListBuilder.class);
		BindingBuilder bindingBuilder = Mockito.mock(BindingBuilder.class);


		Mockito.when(binder.bindList(ExtendedType.class, Constants.SERVER_USER_TYPES_LIST)).thenReturn(listBuilder);
		Mockito.when(binder.bindMap(String.class, Constants.PROPERTIES_MAP)).thenReturn(mapBuilder);
		Mockito.when(binder.bind(Mockito.any(Class.class))).thenReturn(bindingBuilder);
		Mockito.when(binder.bind(Mockito.any(Key.class))).thenReturn(bindingBuilder);


		WillowCayenneModule module = new WillowCayenneModule(queryCache);
		module.configure(binder);

		Mockito.verify(binder, Mockito.times(1)).bindMap(String.class, Constants.PROPERTIES_MAP);
		Mockito.verify(mapBuilder, Mockito.times(2)).put(Constants.CI_PROPERTY, "true");
	}
}
