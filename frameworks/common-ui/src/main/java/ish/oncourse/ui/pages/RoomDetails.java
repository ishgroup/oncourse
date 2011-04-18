package ish.oncourse.ui.pages;

import ish.oncourse.model.Room;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class RoomDetails {

	@Inject
	private Request request;

	@Property
	private Room room;

	@SetupRender
	public void beforeRender() {
		room = (Room) request.getAttribute(Room.class.getSimpleName());
	}

}
