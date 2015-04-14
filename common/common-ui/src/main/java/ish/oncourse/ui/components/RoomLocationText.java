package ish.oncourse.ui.components;

import ish.oncourse.model.Room;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;


public class RoomLocationText {

	@Parameter(required = true)
	@Property
	private Room room;
	@Parameter
	@Property
	private boolean withRoomName;
	@Parameter
	@Property
	private boolean withSiteAddress;

	public boolean isHasRoomName() {
		return (room != null) 
				&& (room.getName() != null)
				&& ! "".equals(room.getName());
	}

	public boolean isHasSiteName() {
		return (room != null) 
				&& (room.getSite() != null)
				&& (room.getSite().getName() != null)
				&& ! "".equals(room.getSite().getName());
	}
}
