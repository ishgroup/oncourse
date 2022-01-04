/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.utils

import groovy.transform.CompileStatic
import ish.oncourse.model.WebHostName
import org.apache.commons.net.util.SubnetUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.configuration.Configuration.AdminProperty.IPV4_RANGE
import static ish.oncourse.configuration.Configuration.AdminProperty.IPV6_RANGE
import static ish.oncourse.configuration.Configuration.getValue

@CompileStatic
class DomainUtils {
    private static final String INVALID_RANGES_ERROR = "Error of ip validation: valid ranges on server not set"
    private static final String IPV4_ERROR_FORMAT = "Hostname has been disabled. Go to your domain host and set the following:\n" +
            "%s. A    202.167.247.94\n" +
            "%s. AAAA 2404:4f00:1010:1::6"
    private static final String IPV6_ERROR_FORMAT = "Some users will not be able to see your site. Go to your domain host and set the following:\n" +
            "%s. AAAA 2404:4f00:1010:1::6"
    private static final Logger logger = LogManager.getLogger()

    static List<SubnetUtils> ipV4Ranges = getIp4Ranges()
    static String ipV6Address = getIp6Address()


    private static List<SubnetUtils> getIp4Ranges() {
        def diapasonsStr = getValue(IPV4_RANGE)
        if (diapasonsStr == null) {
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
     * @return error if any of this domain ip addresses are null or null in other case
     */
    static String checkForIpErrors(WebHostName webHostName) {
        if (webHostName.getIpv4() == null)
            return String.format(IPV4_ERROR_FORMAT, webHostName.name)
        if (webHostName.getIpv6() == null)
            return String.format(IPV6_ERROR_FORMAT, webHostName.name)
        return null
    }

    /**
     * set ipv4 and ipv6 fields of WebHostName if there are no errors and they are in ranges
     */
    static void buildDomainIps(WebHostName webHostName) {
        if (!rangesValid()) {
            logger.error(INVALID_RANGES_ERROR)
            return
        }

        String domain = webHostName.name
        String host = domain.startsWith("http") ? new URL(domain).getHost() : domain
        InetAddress[] addresses
        try {
            addresses = InetAddress.getAllByName(host)
        } catch (UnknownHostException e) {
            logger.error(e.getMessage())
            return
        }

        webHostName.setIpv4(addressOf(addresses, Inet4Address.class))
        webHostName.setIpv6(addressOf(addresses, Inet6Address.class))
    }

    private static boolean rangesValid() {
        return ipV4Ranges != null && ipV6Address != null
    }

    private static byte[] addressOf(InetAddress[] allAddresses, Class<? extends InetAddress> ipvClass) {
        List<? extends InetAddress> addresses = allAddresses.findAll { ipvClass.isInstance(it) }
                .collect { ipvClass.cast(it) }
        if (addresses.size() != 1)
            return null
        return ipInRange(addresses.first()) ? addresses.first().address : null
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
