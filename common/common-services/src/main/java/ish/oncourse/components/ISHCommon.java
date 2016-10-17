package ish.oncourse.components;

import ish.math.Money;
import ish.oncourse.model.Document;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * The class contains common methods and common properties which can be used in any tapestry templates.
 * Any Tapestry Component or Page class should extend the class
 */
public abstract class ISHCommon {
    @Inject
    @Property
    private Request request;


    @Inject
    private ITagService tagService;

    @Inject
    private IBinaryDataService binaryDataService;

    @Property
    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";

    @Property
    private Document attachment;

    public String formatMoney(Money money, String pattern) {
        NumberFormat format = new DecimalFormat(pattern);
        return format.format(money);
    }

    public String formatDate(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

	public Date getCurrentDate() {
		return new Date();
	}

    public <E extends Queueable> boolean hasTag(E entity, String tagPath) {
        return tagService.hasTag(entity, tagPath);
    }

    public <E extends Queueable> List<Document> getAttachments(E entity) {
        return binaryDataService.getAttachments(entity);
    }

    public <E extends Queueable> Document getAttachmentByTag(E entity, final String tagPath) {
        return binaryDataService.getAttachmentByTag(entity, tagPath);
    }
}
