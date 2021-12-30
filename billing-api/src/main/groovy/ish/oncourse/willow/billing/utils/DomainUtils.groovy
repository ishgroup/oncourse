/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.utils

import groovy.transform.CompileStatic
import org.apache.commons.net.util.SubnetUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.configuration.Configuration.AdminProperty.IPV4_RANGE
import static ish.oncourse.configuration.Configuration.AdminProperty.IPV6_RANGE
import static ish.oncourse.configuration.Configuration.getValue

@CompileStatic
class DomainUtils {
    private static final String INVALID_RANGES_ERROR = "Error of ip validation: valid ranges on server not set"
    private static final String UNKNOWN_HOST_ERROR = "domain setup is not correct. Go to your DNS hosting provider and set the following:\n" +
            "sttrinians.ish.com.au A 202.167.247.94\n" +
            "sttrinians.ish.com.au AAAA 2404:4f00:1010:1::6"
    private static final Logger logger = LogManager.getLogger()

    static List<SubnetUtils> ipV4Ranges = getIp4Ranges()
    static String ipV6Address = getIp6Address()


    private static List<SubnetUtils> getIp4Ranges() {
        def diapasonsStr = getValue(IPV4_RANGE)
        if (diapasonsStr == null){
            logger.error("Incorrect property: diapasons for ipv4 not set")
            return null
        }

        return diapasonsStr.split(",")
                .collect { it -> new SubnetUtils(it) }
    }

    private static String getIp6Address() {
        def ipV6 = getValue(IPV6_RANGE)
        if (ipV6 == null)
            logger.error("Incorrect property: diapasons for ipv4 not set")
        return ipV6
    }

    /**
     * @return error if any of this domain ip addresses are not in range or null in other cases
     */
    static String findNotInRangeIp(String domain) {
        if(!rangesValid()){
            logger.error(INVALID_RANGES_ERROR)
            return INVALID_RANGES_ERROR
        }

        String host = domain.startsWith("http") ? new URL(domain).getHost() : domain
        InetAddress notInRangesAddress
        try {
            notInRangesAddress = InetAddress.getAllByName(host)
                    .find { it -> !ipInRange(it) }
        } catch (UnknownHostException e) {
            return UNKNOWN_HOST_ERROR
        }
        
        return notInRangesAddress == null ? null : "${notInRangesAddress} is not in allowed range"
    }

    private static boolean rangesValid(){
        return ipV4Ranges != null && ipV6Address != null
    }

    private static boolean ipInRange(InetAddress inetAddress) {

        if (inetAddress instanceof Inet4Address)
            return ipV4InOneOfRanges(inetAddress.getHostAddress())
        if (inetAddress instanceof Inet6Address)
            return inetAddress.getHostAddress() == ipV6Address

        return false
    }

    private static boolean ipV4InOneOfRanges(String ipV4) {
        return ipV4Ranges.find { range -> range.getInfo().isInRange(ipV4) } != null
    }
}
