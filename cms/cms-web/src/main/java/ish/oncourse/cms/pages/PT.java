package ish.oncourse.cms.pages;

import java.util.Iterator;
import java.util.SortedSet;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.content.IWebContentService;

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
		
		String id = request.getParameter("id");
		String rg = request.getParameter("rg");
		int weight = Integer.parseInt(request.getParameter("w"));
		
		WebContent block = webContentService.loadByIds(id).get(0);
		RegionKey regionKey = (rg != null) ?  RegionKey.valueOf(rg) : block.getWebContentVisibility().getRegionKey();
		
		SortedSet<WebContentVisibility> vSet = webContentService.getBlockVisibilityForRegionKey(regionKey);
		
		int w = 0;
		Iterator<WebContentVisibility> it = vSet.iterator();
		
		while( it.hasNext()) {
			WebContentVisibility v = it.next();
			if (w < weight) {
				v.setWeight(w);
			}
			else {
				v.setWeight(w + 1);
			}
			w++;
		}
		
		block.getWebContentVisibility().setRegionKey(regionKey);
		block.getWebContentVisibility().setWeight(weight);
		
		cayenneService.sharedContext().commitChanges();
		
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
}
