package ish.oncourse.cms.pages;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.content.IWebContentService;

import java.util.Iterator;
import java.util.SortedSet;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class PT {

	@Inject
	private Request request;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICayenneService cayenneService;

	StreamResponse onActionFromSort() {

		Long id = Long.parseLong(request.getParameter("id"));
		WebContent block = (WebContent) cayenneService.newContext().localObject(webContentService.findById(id).getObjectId(), null);
		
		int weight = Integer.parseInt(request.getParameter("w"));

		String rg = request.getParameter("rg");
		if (rg != null) {
			RegionKey regionKey = RegionKey.valueOf(rg);
			block.getWebContentVisibility().setRegionKey(regionKey);
		}

		SortedSet<WebContentVisibility> vSet = webContentService
				.getBlockVisibilityForRegionKey(block.getWebContentVisibility()
						.getRegionKey());

		int w = 0;
		Iterator<WebContentVisibility> it = vSet.iterator();

		while (it.hasNext()) {
			WebContentVisibility v = it.next();
			if (w < weight) {
				v.setWeight(w);
			} else {
				v.setWeight(w + 1);
			}
			w++;
		}

		block.getWebContentVisibility().setWeight(weight);

		block.getObjectContext().commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
}
