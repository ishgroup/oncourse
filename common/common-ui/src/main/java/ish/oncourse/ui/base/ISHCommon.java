package ish.oncourse.ui.base;

import ish.math.Money;
import ish.oncourse.model.Document;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.tapestry.TapestryFormatUtils;
import ish.oncourse.utils.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

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
    protected Request request;


    @Inject
    private ITagService tagService;

    @Inject
    private IBinaryDataService binaryDataService;

    @Property
    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";

    @Property
    private Document entityAttachment;

    @Inject
    private IRichtextConverter textileConverter;

    @Inject
    private IPlainTextExtractor extractor;

    @Property
    private TapestryFormatUtils formatUtils = new TapestryFormatUtils();
    
    private static final int SNIPPET_LENGTH = 490;

    public String convertCustomText(String source) {
        return convertCustomText(source, true);
    }
    
    public String convertCustomText(String source, Boolean snippet) {
        String detail = textileConverter.convertCustomText(source, new ValidationErrors());
        if (detail == null) {
            return StringUtils.EMPTY;
        }

        if (snippet) {
            return StringUtilities.abbreviate(extractor.extractFromHtml(detail), SNIPPET_LENGTH);
        } else {
            return detail;
        }    
    }
    
    public String formatMoney(Money money, String pattern) {
       return formatUtils.formatMoney(money, pattern);
    }

    public String formatDate(Date date, String pattern) {
        return formatUtils.formatDate(date, pattern);
    }

	public Date getCurrentDate() {
		return new Date();
	}

    public <E extends Queueable> boolean hasTag(E entity, String tagPath) {
        return tagService.hasTag(entity, tagPath);
    }

    public <E extends Queueable> List<Document> getEntityAttachments(E entity) {
        return binaryDataService.getAttachments(entity);
    }

    public <E extends Queueable> Document getEntityAttachmentByTag(E entity, final String tagPath) {
        return binaryDataService.getAttachmentByTag(entity, tagPath);
    }

    public <E extends Queueable> List<Document> getEntityImages(E entity) {
        return binaryDataService.getImages(entity);
    }

    public static int getIntParam(String s, int def) {
        return (s != null && s.matches("\\d+")) ? Integer.parseInt(s) : def;
    }
    
}
