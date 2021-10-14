/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.utils


import org.apache.commons.net.util.SubnetUtils

import static ish.oncourse.configuration.Configuration.AdminProperty.IPV4_RANGE
import static ish.oncourse.configuration.Configuration.AdminProperty.IPV6_RANGE
import static ish.oncourse.configuration.Configuration.getValue

class DomainUtils {
    static List<SubnetUtils> ipV4Ranges = getIp4Ranges()
    static String ipV6Address = getIp6Address()


    private static List<SubnetUtils> getIp4Ranges() {
        def diapasonsStr = getValue(IPV4_RANGE)
        if (diapasonsStr == null)
            throw new RuntimeException("Incorrect property: diapasons for ipv4 not set")
        return diapasonsStr.split(",")
                .collect { it -> new SubnetUtils(it) }
    }

    private static String getIp6Address() {
        def ipV6 = getValue(IPV6_RANGE)
        if (ipV6 == null)
            throw new RuntimeException("Incorrect property: ipv6 address not set")
        return ipV6
    }

    /**
     * @return error if any of this domain ip addresses are not in range or null in other cases
     */
    static String findNotInRangeIp(String domain) throws UnknownHostException {
        String host = domain.startsWith("http") ? new URL(domain).getHost() : domain
        def notInRangesAddress = InetAddress.getAllByName(host)
                .find { it -> !ipInRange(it) }
        return notInRangesAddress == null ? null : "${notInRangesAddress} is not in allowed range"
    }

    private static boolean ipInRange(InetAddress inetAddress) {

        if (inetAddress instanceof Inet4Address)
            return ipV4InOneOfRanges(inetAddress.getHostAddress())
        if (inetAddress instanceof Inet6Address)
            return inetAddress.getHostAddress() == ipV6Address

        throw new RuntimeException("Incorrect inet address format")
    }

    private static boolean ipV4InOneOfRanges(String ipV4) {
        return ipV4Ranges.find { range -> range.getInfo().isInRange(ipV4) } != null
    }
}
