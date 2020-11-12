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

package ish.oncourse.server.scripting.api;

import ish.oncourse.API;

/**
 * Mechanism of automatic skipping sending emails for contacts have already received email with the same key.
 *
 * If key is not set sending email is going as usual.
 * If key is set creator key property is added to Message.
 * Also Message is created for emails which are sent by SMTP. This Message has no MessagePerson records but contains a creator key.
 */
@API
public enum KeyCollision {

    /**
     *
     * Always send a email. Default value.
     */
    @API
    accept,

    /**
     * Skip sending email if email with specified key has already been sent.
     */
    @API
    drop,

    /**
     * Skip sending email if email with specified key has already been sent and add audit log.
     */
    @API
    error
}
