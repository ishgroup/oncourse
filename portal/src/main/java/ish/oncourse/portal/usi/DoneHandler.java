package ish.oncourse.portal.usi;

import ish.common.types.UsiStatus;
import ish.oncourse.model.Student;

import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class DoneHandler extends AbstractStepHandler {

    @Override
    public Result getValue() {
        if (result.isEmpty()) {
            Student student = getUsiController().getContact().getStudent();
            addValue(Value.valueOf(Student.USI.getName(),
                    student.getUsi()));
            addValue(Value.valueOf(Student.USI_STATUS.getName(), student.getUsiStatus() != null ? student.getUsiStatus().name() : UsiStatus.DEFAULT_NOT_SUPPLIED));
        }
        return result;
    }

    @Override
    public Step getNextStep() {
        return Step.avetmissInfo;
    }

    public DoneHandler handle(Map<String, Value> input) {
        this.inputValues = input;
        return this;
    }
}
