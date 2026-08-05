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
import ish.oncourse.server.api.v1.function.MembershipFunctions;
import ish.oncourse.server.cayenne.Membership;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MembershipLifecycleListener {

    /**
     * ONC-A4: this listener is registered once, process-wide (see CayenneListenersService), so the
     * hand-off set between the @Pre* and @Post* callbacks was a plain HashSet mutated concurrently by
     * every thread touching a Membership. Two consequences:
     * <ul>
     *   <li>concurrent add/remove on HashMap's table can lose entries or corrupt a bucket chain, so
     *       updateRenweval() was skipped and Membership.expiryDate never set — the CREATE stub then
     *       replicated to willow with the wrong expiry and no follow-up UPDATE record was ever
     *       queued, leaving a silent permanent angel/willow divergence;</li>
     *   <li>an entry whose @Post* callback never fired (flush aborted after applyPreCommit()) was
     *       never removed, and Membership holds a strong reference to its ObjectContext — pinning
     *       that request's entire ObjectStore forever.</li>
     * </ul>
     * The hand-off only ever needs to live within a single thread's commit, so a ThreadLocal is both
     * correct and leak-free.
     */
    private static final ThreadLocal<Set<Membership>> TO_PROCESS = ThreadLocal.withInitial(HashSet::new);

    public MembershipLifecycleListener() {
    }


    @PrePersist(value = Membership.class)
    @PreUpdate(value = Membership.class)
    public void preUpdate(Membership membership) {
        if (PaymentSource.SOURCE_ONCOURSE.equals(membership.getInvoiceLine().getInvoice().getSource())
                && ProductStatus.ACTIVE.equals(membership.getStatus())
                && !ExpiryType.LIFETIME.equals(membership.getProduct().getExpiryType())) {

            if (membership.isNewRecord()) {
                TO_PROCESS.get().add(membership);
            } else {
                var change = ChangeFilter.getAtrAttributeChange(membership.getObjectContext(), membership.getObjectId(), Membership.STATUS.getName());
                if (change != null && (change.getOldValue() == null || ProductStatus.NEW.equals(change.getOldValue()))) {
                    TO_PROCESS.get().add(membership);
                }
            }
        }
    }

    @PostPersist(value = Membership.class)
    @PostUpdate(value = Membership.class)
    public void postUpdate(Membership membership) {
        Set<Membership> toProcess = TO_PROCESS.get();
        if (toProcess.contains(membership)) {
            updateRenweval(membership);
        }
        // ONC-A4: release the ThreadLocal as soon as the hand-off is drained so a callback that
        // never fires (aborted flush) cannot pin this request's ObjectStore on a pooled thread.
        if (toProcess.isEmpty()) {
            TO_PROCESS.remove();
        }
    }


    private void updateRenweval(final Membership membership) {
        TO_PROCESS.get().remove(membership);
        Date renewalDate = MembershipFunctions.getRenwevalExpiryDate(membership.getContact(), membership);
        if (renewalDate != null) {
            membership.setExpiryDate(renewalDate);
            membership.getObjectContext().commitChanges();
        }
    }
}
