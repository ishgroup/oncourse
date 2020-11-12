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

package ish.util;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.PersistentObject;

public class MessageUtils {
    private final static String SEPARATOR = "_";

    /**
     * generates creatorKey with mask: prefix_objectClassName1_id_objectClassName2_id...
     *
     * NOTE: only already committed objects used in creatorKey generation
     *
     * Example:
     *      input - "someprefix", Enrolment, Student
     *      output - someprefix_Enrolment_123_Student_456
     *
     *      input - "someprefix", Enrolment1, Enrolment2, Student
     *      output - someprefix_Enrolment_123_Enrolment_789_Student_456
     *
     *      input - "someprefix", Enrolment1, Student, Enrolment2
     *      output - someprefix_Enrolment_123_Student_456_Enrolment_789
     *
     * NOTE 2: <code>prefix</code> must NOT be null.
     *
     * @param prefix - user generated string to identify script, that calls message sending
     * @param objects - Persistent objects to encode in creatorKey, needed to identify message
     * @IllegalArgumentException - will be thrown if objects list contain at least one not committed/deleted object
     * @NullPointerException - will be thrown if prefix will be null
     * @return generated "creatorKey" string
     */
    public static String generateCreatorKey(String prefix, PersistentObject... objects){
        if (objects == null)
            return prefix;

        var creatorKey = new StringBuilder(prefix);
        appendCreatorKeyWithObjects(creatorKey, objects);

        return creatorKey.toString();
    }

    /**
     * appends entity names and ids to creatorKey string
     *
     * @param creatorKey - string with already setted prefix
     * @param objectList - list of Persistent objects
     */
    private static void appendCreatorKeyWithObjects(StringBuilder creatorKey, PersistentObject... objectList){
        for (var object : objectList){
            validatePersistentObject(object);

            creatorKey.append(SEPARATOR);
            creatorKey.append(object.getClass().getSimpleName());
            creatorKey.append(SEPARATOR);
            creatorKey.append(object.getObjectId().getIdSnapshot().get("id"));
        }
    }

    /**
     * checks whether this object is already in DB by it's Persistent state
     * throws IllegalArgumentException if object's state shows that it is not in a DB
     *
     * @param object
     */
    private static void validatePersistentObject(PersistentObject object){
        switch (object.getPersistenceState()){
            case PersistenceState.TRANSIENT :
            case PersistenceState.NEW :
            case PersistenceState.DELETED :
                throw new IllegalArgumentException(String.format("Can't generate for object in persistence state: %s", object.getPersistenceState()));
        }
    }
}
