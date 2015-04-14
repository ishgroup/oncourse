package ish.oncourse.admin.pages;

import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

public class UpdatePostcodeCallback {

	@Inject
	private Request request;
	
	@Inject
	private PreferenceController preferenceController;
	
public StreamResponse onActivate() {
		Session session = request.getSession(false);
		Boolean updateInProgress = (Boolean) session.getAttribute(UpdatePostcode.IS_POSTCODE_UPDATE_STARTED_FLAG);
		final JSONArray jsonSession = new JSONArray();
		if (Boolean.TRUE.equals(updateInProgress)) {
			final String progress = ((Long) session.getAttribute(UpdatePostcode.POSTCODE_UPDATE_PROCESSED_FLAG)).toString();
			jsonSession.put("Processed " + progress + " postcodes");
			return new TextStreamResponse("text/json", jsonSession.toString());
		} else {
			String lastUpdate = preferenceController.getPostcodesLastUpdate();
			if (lastUpdate == null) {
				lastUpdate = UpdatePostcode.NEVER_UPDATED_VALUE;
			}
			jsonSession.put(String.format("Finished_%s", lastUpdate));
		}
		return new TextStreamResponse("text/json", jsonSession.toString());
	}
}
