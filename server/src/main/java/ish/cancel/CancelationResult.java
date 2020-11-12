/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.cancel;

import org.apache.cayenne.validation.ValidationFailure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CancelationResult implements Serializable {

    private List<ValidationFailure> failures;
    private boolean failed = false;

    public CancelationResult() {
        this.failures = new ArrayList<>();
    }

    public boolean hasFailures() { return !failures.isEmpty(); }

    public List<ValidationFailure> getFailures() {
        return failures;
    }

    public void addFailure(ValidationFailure failure) {
        this.failures.add(failure);
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

}
