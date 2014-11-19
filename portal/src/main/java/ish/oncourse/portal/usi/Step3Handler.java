package ish.oncourse.portal.usi;

import java.util.Collections;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step.step3Done;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step3Handler extends AbstractStepHandler {

    @Override
    public Map<String, Value> getValue() {
        return Collections.emptyMap();
    }

    @Override
    public UsiController.Step getNextStep() {
        return step3Done;
    }

    public Step3Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        return this;
    }
}