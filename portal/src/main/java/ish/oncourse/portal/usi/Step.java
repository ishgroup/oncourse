package ish.oncourse.portal.usi;

import java.io.Serializable;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public enum Step implements Serializable {
    step1(Step1Handler.class),
    step1Done(Step1DoneHandler.class),
    step1Failed(Step1FailedHandler.class),
    step2(Step2Handler.class),
    step3(Step3Handler.class),
    step3Done(Step3DoneHandler.class),
    done(DoneHandler.class),
    wait(WaitHandler.class);

    private Class<? extends AbstractStepHandler> handlerClass;

    Step(Class<? extends AbstractStepHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class<? extends AbstractStepHandler> getHandlerClass() {
        return handlerClass;
    }
}