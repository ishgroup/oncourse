/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.webservices.replication.util;

import ish.common.types.EntityMapping;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;

public class EntityMappingExtended {
    public static String extendedWillowIdentifier(GenericReplicationStub currentStub) {
        SupportedVersions version = PortHelper.getVersionByReplicationStub(currentStub);
        String identifier = EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier());
        if (version == SupportedVersions.V25 && identifier.equals(Message.class.getSimpleName()))
            identifier = MessagePerson.class.getSimpleName();
        return identifier;
    }
}
