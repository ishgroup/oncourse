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

package ish.oncourse.server.imports

import org.apache.cayenne.validation.ValidationException

import java.sql.SQLException

class CreateUserFriendlyMessage {
    private Throwable throwable
    private int recordNumber = 0

    static CreateUserFriendlyMessage valueOf(Throwable th, int recordNumber){
        CreateUserFriendlyMessage createUserFriendlyMessage = new CreateUserFriendlyMessage()
        createUserFriendlyMessage.throwable = th
        createUserFriendlyMessage.recordNumber = recordNumber
        createUserFriendlyMessage
    }

    /**
     * Searches for Validation or Sql exception in throwable's exception chain
     * Creates user-friendly message, based on information from validation result failures or sql exception message
     *
     * If there are no any validation or sql exception, current exception message will be returned
     * Also adding Record number to message
     */
    String getMessage(){
        String wrongTitle = "Check title OR"
        String userFriendlyMessage = "<br>${recordNumber == 1 ? wrongTitle : ""} Record No: $recordNumber.<br>"
        Throwable dataException = throwable

        while (dataException) {
            if (dataException instanceof ValidationException)
                return userFriendlyMessage + (dataException as ValidationException).validationResult.failures.collect {it.description}.join("<br>")
            if (dataException instanceof SQLException)
                return userFriendlyMessage + dataException.message
            dataException = dataException.cause
        }

        return userFriendlyMessage + throwable
    }
}
