package ish.oncourse.portal.usi;

import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class DoneHandler extends AbstractStepHandler {

    @Override
    public Step getNextStep() {
        return Step.avetmissInfo;
    }

    public DoneHandler handle(Map<String, Value> input) {
        this.inputValues = input;
        return this;
    }
}
