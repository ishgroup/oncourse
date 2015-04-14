package ish.oncourse.ui.components;

import ish.oncourse.model.Room;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class RoomLocation {
	@Parameter(required = true)
	@Property
	private Room room;

	@Parameter
	@Property
	private boolean withRoomName = true;

	@Parameter
	@Property
	private boolean withSiteAddress;

	@Parameter
	@Property
	private boolean disabledLink;

	@Parameter
	@Property
	private boolean isList;

	@SetupRender
	boolean beforeRender() {
		// prevent displaying null room
		return room != null && room.getSite() != null && room.getSite().getIsWebVisible();
	}

	public boolean isRedirect() {
		return !isList || !room.getSite().isHasCoordinates();
	}

	public String getMapLink() {
		return withRoomName ? "/room/" + room.getAngelId() : "/site/" + room.getSite().getAngelId();
	}
}
