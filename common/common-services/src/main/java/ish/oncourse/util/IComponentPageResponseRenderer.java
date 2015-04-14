package ish.oncourse.util;

import org.apache.tapestry5.internal.structure.Page;

import java.io.IOException;

public interface IComponentPageResponseRenderer{
	void renderPageResponse(Page page) throws IOException;
}
