package ish.oncourse.ui.pages;

import ish.oncourse.model.Room;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class RoomDetails {

    @Inject
    private ITextileConverter textileConverter;

    @Inject
	private Request request;

	@Property
	private Room room;

	@SetupRender
	public void beforeRender() {
		room = (Room) request.getAttribute(Room.class.getSimpleName());
	}

    public String getDirections() {
        if (room != null && StringUtils.trimToNull(room.getDirections()) != null)
            return textileConverter.convertCustomTextile(room.getDirections(), new ValidationErrors());
        else
            return null;
    }


}
