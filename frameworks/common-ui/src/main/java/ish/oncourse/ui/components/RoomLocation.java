package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import ish.oncourse.model.Room;

public class RoomLocation {
	@Parameter(required = true)
	@Property
	private Room room;

	@Parameter
	@Property
	private boolean withRoomName;

	@Parameter
	@Property
	private boolean withSiteAddress;

	@Parameter
	@Property
	private boolean disabledLink;

	public String getMapLink() {
		if (room == null) {
			return "";
		}
		return withRoomName ? "/room/" + room.getAngelId() : "/site/" + room.getSite().getAngelId();
	}
}
