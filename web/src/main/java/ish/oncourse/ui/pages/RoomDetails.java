package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.ui.components.Attachments;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class RoomDetails extends ISHCommon {

    @Inject
    private IRichtextConverter textileConverter;

	@Property
	private Room room;

    @InjectComponent
    private Attachments attachments;

	@SetupRender
	public void beforeRender() {
        room = (Room) request.getAttribute(Room.class.getSimpleName());
	}

    public String getDirections() {
        if (room != null && StringUtils.trimToNull(room.getDirections()) != null)
            return textileConverter.convertCustomText(room.getDirections(), new ValidationErrors());
        else
            return null;
    }

    public String[] getIdentifiers()
    {
        return new String[]{Site.class.getSimpleName(),
                Room.class.getSimpleName()};
    }

    public String[] getIds()
    {
        return new String[]{String.valueOf(room.getSite().getId()),
                String.valueOf(room.getId())};
    }
}
