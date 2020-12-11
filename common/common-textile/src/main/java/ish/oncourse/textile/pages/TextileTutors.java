package ish.oncourse.textile.pages;

import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileTutors {
    
    @Inject
    private Request request;

    @Property
    private Long tutorId;
    @Property
    private String tagName;
    @Property
    private Integer count;

    @SetupRender
    void beginRender() {
        tutorId = (Long) request.getAttribute(TextileUtil.TEXTILE_TUTORS_TUTOR_ID_PARAM);
        tagName = (String) request.getAttribute(TextileUtil.TEXTILE_TUTORS_TAG_NAME_PARAM);
        count = (Integer) request.getAttribute(TextileUtil.TEXTILE_TUTORS_COUNT_PARAM);
    }
}
