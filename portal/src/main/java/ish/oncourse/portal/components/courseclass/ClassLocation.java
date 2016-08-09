package ish.oncourse.portal.components.courseclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.GetCourseClassLocation;
import ish.oncourse.services.courseclass.Location;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;


public class ClassLocation {


	@Parameter
	@Property
	private CourseClass courseClass;

	@Property
	private Boolean hasLocation;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	private ObjectMapper mapper = new ObjectMapper();

	@SetupRender
	public void beforeRender() {
		Location location = new GetCourseClassLocation(courseClass).get();
		hasLocation = location != null;
	}

	@OnEvent(value = "getLocation")
	public StreamResponse getLocation(Long courseClassId) throws IOException {

		if (!request.isXHR())
			return null;
		Location location = new GetCourseClassLocation(courseClass).get();

		String json = String.format("{\"message\": \"%s\"}", messages.get("message.withoutLocation"));
		if (location != null) {
			json = mapper.writeValueAsString(location);
		}
		return new TextStreamResponse("text/json", json);
	}
}


