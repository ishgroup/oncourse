package ish.oncourse.portal.usi;

import java.io.Serializable;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public enum Step implements Serializable {
    usi(USIHandler.class),
    usiFailed(USIFailedHandler.class),
    avetmissInfo(AvetmissInfoHandler.class),
    contactInfo(ContactInfoHandler.class),
    contactInfoDone(ContactInfoDoneHandler.class),
    done(DoneHandler.class),
    waitVerify(WaitVerifyHandler.class),
    waitLocate(WaitLocateHandler.class);

    private Class<? extends AbstractStepHandler> handlerClass;

    Step(Class<? extends AbstractStepHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class<? extends AbstractStepHandler> getHandlerClass() {
        return handlerClass;
    }
}