package ish.oncourse.services.persistence;

import org.apache.cayenne.cache.OSQueryCache;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;

public class ISHModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ObjectContextFactory.class).to(ISHObjectContextFactory.class);
		binder.bind(QueryCache.class).to(OSQueryCache.class);
		binder.bind(Key.get(QueryCache.class, "local")).to(OSQueryCache.class);
	}
}
