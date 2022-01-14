/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.functions;

class IsDomainValid {

    protected String webHostName
    protected List<String> errors = null

    private IsDomainValid(){}

    static IsDomainValid valueof(String webHostName) {
        IsDomainValid isDomainValid = new IsDomainValid()
        isDomainValid.webHostName = webHostName
        isDomainValid.errors = new ArrayList<>()
        isDomainValid
    }

    List<String> getErrors() {
        return errors
    }

    IsDomainValid validate() {

        String domain = webHostName
        String host = domain.startsWith("http") ? new URL(domain).getHost() : domain

        try {
            InetAddress.getAllByName(host)
        } catch (UnknownHostException ignored) {
            errors += "Domain ${webHostName}: Unknown host".toString()
        }
        this
    }

}
