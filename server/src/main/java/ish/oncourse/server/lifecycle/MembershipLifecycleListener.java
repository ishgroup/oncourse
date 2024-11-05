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

package ish.oncourse.server.lifecycle;

import ish.common.types.ExpiryType;
import ish.common.types.PaymentSource;
import ish.common.types.ProductStatus;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.v1.function.MembershipFunctions;
import ish.oncourse.server.cayenne.Membership;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MembershipLifecycleListener {

    private ICayenneService cayenneService;
    private Set<Membership> toProcess = new HashSet<>();

    public MembershipLifecycleListener(ICayenneService cayenneService) {
        this.cayenneService = cayenneService;
    }


    @PrePersist(value = Membership.class)
    @PreUpdate(value = Membership.class)
    public void preUpdate(Membership membership) {
        if (PaymentSource.SOURCE_ONCOURSE.equals(membership.getInvoiceLine().getInvoice().getSource())
                && ProductStatus.ACTIVE.equals(membership.getStatus())
                && !ExpiryType.LIFETIME.equals(membership.getProduct().getExpiryType())) {

            if (membership.isNewRecord()) {
                toProcess.add(membership);
            } else {
                var change = ChangeFilter.getAtrAttributeChange(membership.getObjectContext(), membership.getObjectId(), Membership.STATUS.getName());
                if (change != null && (change.getOldValue() == null || ProductStatus.NEW.equals(change.getOldValue()))) {
                    toProcess.add(membership);
                }
            }
        }
    }

    @PostPersist(value = Membership.class)
    @PostUpdate(value = Membership.class)
    public void postUpdate(Membership membership) {
       if (toProcess.contains(membership)) {
           updateRenweval(membership);
       }
    }


    private void updateRenweval(final Membership membership) {
        toProcess.remove(membership);
        Date renewalDate = MembershipFunctions.getRenwevalExpiryDate(membership.getContact(), membership);
        if (renewalDate != null) {
            DataContext context = cayenneService.getNewContext();
            var localMembership = context.localObject(membership);
            localMembership.setExpiryDate(renewalDate);
            context.commitChanges();
        }
    }
}
