package ish.oncourse.ui.components;

import ish.oncourse.model.Document;
import ish.oncourse.services.binary.IBinaryDataService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Image {
    @Inject
    private IBinaryDataService binaryDataService;

    @Property
    @Parameter
    private String name;

    @Property
    @Parameter
    private String align;

    @Property
    @Parameter
    private String caption;

    @Property
    @Parameter
    private String alt;

    @Property
    @Parameter
    private String title;

    @Property
    @Parameter
    private String width;

    @Property
    @Parameter
    private String height;

    @Property
    @Parameter
    private String cssClass;

    @Property
    private Document image;

    @Property
    private String path;


    @SetupRender
    public void setupRender() {
        image = binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, name);
        if (image != null) {
            path = binaryDataService.getUrl(image);
        }
    }
}
