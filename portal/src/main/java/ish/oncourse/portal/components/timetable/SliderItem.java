package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Session;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * User: artem
 * Date: 10/11/13
 * Time: 3:23 PM
 */
public class SliderItem {

    @Parameter
    private Session session;

    @Property
    private  String stage;


}
