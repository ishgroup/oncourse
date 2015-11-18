package ish.oncourse.portal.usi;

import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step3DoneHandler extends AbstractStepHandler {

    @Override
    public Result getValue() {
        return result;
    }

    @Override
    public Step getNextStep() {
        return Step.done;
    }

    public Step3DoneHandler handle(Map<String, Value> input) {
        this.inputValues = input;
        return this;
    }
}