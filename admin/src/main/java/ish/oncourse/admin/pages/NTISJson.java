package ish.oncourse.admin.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

public class NTISJson {
	
	private static final String NTIS_DATA_ATTR = "NTISData";
	
	@Inject
	private Request request;

	public StreamResponse onActivate() {
		
		Session session = request.getSession(false);
		
		List<?> ntisData = (List<?>) session.getAttribute(NTIS_DATA_ATTR);
		
		if (ntisData == null) {
			return new TextStreamResponse("text/json", "");
		}
		
		JSONArray jsonSession = new JSONArray();
		
		for (Object s : ntisData) {
			jsonSession.put(s);
		}

		return new TextStreamResponse("text/json", jsonSession.toString());
	}
}
