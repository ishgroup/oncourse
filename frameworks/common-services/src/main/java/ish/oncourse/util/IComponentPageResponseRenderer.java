package ish.oncourse.util;

import java.io.IOException;

import org.apache.tapestry5.internal.structure.Page;

public interface IComponentPageResponseRenderer{
	void renderPageResponse(Page page) throws IOException;
}
