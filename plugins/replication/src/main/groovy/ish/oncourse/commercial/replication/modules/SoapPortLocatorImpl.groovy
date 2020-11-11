/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.modules

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.Replication
import ish.oncourse.server.AngelModule
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.license.LicenseService
import ish.oncourse.webservices.soap.v22.PaymentPortType
import ish.oncourse.webservices.soap.v22.ReplicationPortType
import ish.oncourse.webservices.soap.v22.ReplicationService
import ish.oncourse.webservices.soap.v7.ReferencePortType
import ish.oncourse.webservices.soap.v7.ReferenceService
import org.apache.cxf.headers.Header
import org.apache.cxf.jaxb.JAXBDataBinding

import javax.xml.bind.JAXBException
import javax.xml.namespace.QName
import javax.xml.ws.BindingProvider
import java.util.ArrayList
import java.util.List

@Singleton
@CompileStatic
class SoapPortLocatorImpl implements ISoapPortLocator {

    private static final String COMMUNICATION_KEY_PARAMETER = "CommunicationKey"
    private static final String REPLICATION_WSDL_PATH = String.format("wsdl/%s_replication.wsdl", Replication.ACTUAL_REPLICATION_VERSION_STRING)
    private static final String ANGEL_VERSION_PARAMETER = "AngelVersion"
    private static final String SECURITY_CODE_PARAMETER = "SecurityCode"
    private static final String REFERENCE_WSDL_PATH = String.format("wsdl/%s_reference.wsdl", Replication.ACTUAL_REFERENCE_VERSION_STRING)
    private static final long DEFAULT_TIMEOUT = 1000L * 60 * 5
    private static final long PAYMENT_SERVICE_TIMEOUT = 1000L * 60 * 25
    private static final String REFERENCE_URL_PARAM = "reference.url"
    private static final String PAYMENT_URL_PARAM = "payment.url"
    private static final String REPLICATION_URL_PARAM = "replication.url"

    private final String angelVersion
    private ReplicationPortType replicationPortType
    private final PreferenceController pref
    private final String referenceUrl
    private final String replicationUrl
    private final String paymentUrl
    private final LicenseService licenseService


    @Inject
    public SoapPortLocatorImpl(@Named(AngelModule.ANGEL_VERSION) String angelVersion, PreferenceController pref, LicenseService licenseService) {
        super()
        this.angelVersion = angelVersion
        this.pref = pref
        String servicesUrl = String.format("%s/services/%s", licenseService.getServices_host(), Replication.ACTUAL_REPLICATION_VERSION_STRING)
        this.replicationUrl = String.format("%s/replication", servicesUrl)
        this.paymentUrl =  String.format("%s/payment", servicesUrl)
        this.referenceUrl =  String.format("%s/services/%s/reference", licenseService.getServices_host(), Replication.ACTUAL_REFERENCE_VERSION_STRING)
        this.licenseService = licenseService
    }

    /**
     * @see com.google.inject.Provider#get()
     */
    @Override
    public ReferencePortType referencePort() {

        ReferenceService referenceService = new ReferenceService(ReferencePortType.class.getClassLoader().getResource(REFERENCE_WSDL_PATH))
        ReferencePortType referencePort = referenceService.getReferencePort()

        return PortFiller.valueOf(referencePort,
                getServiceUrl(REFERENCE_URL_PARAM, referenceUrl),
                geteDefaultPortHeaders(),
                DEFAULT_TIMEOUT).fill()
    }

    /**
     * @see com.google.inject.Provider#get()
     */
    @Override
    public PaymentPortType paymentPort() {

        ReplicationService replicationService = new ReplicationService(ReplicationPortType.class.getClassLoader().getResource(REPLICATION_WSDL_PATH))
        PaymentPortType paymentPortType = replicationService.getPaymentPortType()

        PortFiller filler = PortFiller.valueOf(paymentPortType,
                getServiceUrl(PAYMENT_URL_PARAM, paymentUrl),
                geteDefaultPortHeaders(),
                PAYMENT_SERVICE_TIMEOUT)
        return filler.fill()
    }

    /**
     * @see com.google.inject.Provider#get()
     */
    @Override
    ReplicationPortType replicationPort() {

        if (this.replicationPortType == null) {
            ReplicationService replicationService = new ReplicationService(ReplicationPortType.class.getClassLoader().getResource(REPLICATION_WSDL_PATH))
            this.replicationPortType = replicationService.getReplicationPort()

            BindingProvider provider = (BindingProvider) this.replicationPortType
            provider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true)
        }


        return PortFiller.valueOf(replicationPortType,
                getServiceUrl(REPLICATION_URL_PARAM, this.replicationUrl),
                getReplicationPortHeaders(),
                PAYMENT_SERVICE_TIMEOUT).fill()
    }

    private String getServiceUrl(String propertyKey, String defaultValue)
    {
        return System.getProperty(propertyKey) != null ? System.getProperty(propertyKey) : defaultValue
    }

    private List<Header> geteDefaultPortHeaders() {
        List<Header> headers = new ArrayList<>()
        try {
            headers.add(new Header(new QName(SECURITY_CODE_PARAMETER), this.licenseService.getSecurity_key(), new JAXBDataBinding(String.class)))
            headers.add(new Header(new QName(ANGEL_VERSION_PARAMETER), this.angelVersion, new JAXBDataBinding(String.class)))
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e)
        }
        return headers
    }

    private List<Header> getReplicationPortHeaders() {
        try {
            List<Header> headers = geteDefaultPortHeaders()
            headers.add(new Header(new QName(COMMUNICATION_KEY_PARAMETER), this.pref.getCommunicationKey(), new JAXBDataBinding(String.class)))
            return headers
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e)
        }
    }

    /**
     * Resets soap replication port to force creation of new session on next invoication.
     *
     * @see ISoapPortLocator#resetReplicationPort()
     */
    @Override
    public void resetReplicationPort() {
        this.replicationPortType = null
    }
}
