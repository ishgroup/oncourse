package ish.oncourse.services.room;

import ish.oncourse.model.Room;

public interface IRoomService {
	Room getRoom(String searchProperty, Object value);
}
