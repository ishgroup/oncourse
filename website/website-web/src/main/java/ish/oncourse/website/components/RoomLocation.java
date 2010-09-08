package ish.oncourse.website.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import ish.oncourse.model.Room;

public class RoomLocation {
	@Parameter
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

	public boolean isHasRoomName() {
		return room.getName() != null && !"".equals(room.getName());
	}

	public boolean isHasSiteName() {
		return room.getSite() != null && room.getSite().getName() != null
				&& !"".equals(room.getSite().getName());
	}

	public String getLink() {
		return disabledLink ? "" : "#";
	}
}
