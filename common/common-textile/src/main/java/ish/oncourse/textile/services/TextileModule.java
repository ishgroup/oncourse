package ish.oncourse.textile.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.services.LibraryMapping;

public class TextileModule {
	
	public void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("textile", "ish.oncourse.textile"));
	}

}
