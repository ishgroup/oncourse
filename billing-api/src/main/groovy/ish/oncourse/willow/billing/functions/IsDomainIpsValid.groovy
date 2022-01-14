/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.functions

import ish.oncourse.model.WebHostName

class IsDomainIpsValid {

    protected WebHostName webHostName
    protected List<String> errors = null

    private IsDomainIpsValid(){}

    static IsDomainIpsValid valueof(WebHostName webHostName) {
        IsDomainIpsValid isDomainIpsValid = new IsDomainIpsValid()
        isDomainIpsValid.webHostName = webHostName
        isDomainIpsValid.errors = new ArrayList<>()
        isDomainIpsValid
    }

    List<String> getErrors() {
        return errors
    }

    IsDomainIpsValid validate() {

        if (webHostName.ipv4 == null || webHostName.ipv6 == null) {
            errors += "Domain ${webHostName.name} must have IpV4 and IpV6".toString()
        }
        this
    }

}
