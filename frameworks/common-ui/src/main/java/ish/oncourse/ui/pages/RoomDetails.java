package ish.oncourse.ui.pages;

import ish.oncourse.model.Room;
import ish.oncourse.services.room.IRoomService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class RoomDetails {

	@Inject
	private Request request;

	@Inject
	private IRoomService roomService;

	@Property
	private Room room;

	@SetupRender
	public void beforeRender() {
		String angelId = (String) request.getAttribute("roomId");
		room = roomService.getRoom(Room.ANGEL_ID_PROPERTY, Long
				.valueOf(angelId));
	}

}
