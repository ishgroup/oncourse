/**
 * PaymentExpressWSLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class PaymentExpressWSLocator extends org.apache.axis.client.Service implements com.paymentexpress.stubs.PaymentExpressWS {

    public PaymentExpressWSLocator() {
    }


    public PaymentExpressWSLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PaymentExpressWSLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PaymentExpressWSSoap
    private java.lang.String PaymentExpressWSSoap_address = "https://sec.paymentexpress.com/WS/PXWS.asmx";

    public java.lang.String getPaymentExpressWSSoapAddress() {
        return PaymentExpressWSSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PaymentExpressWSSoapWSDDServiceName = "PaymentExpressWSSoap";

    public java.lang.String getPaymentExpressWSSoapWSDDServiceName() {
        return PaymentExpressWSSoapWSDDServiceName;
    }

    public void setPaymentExpressWSSoapWSDDServiceName(java.lang.String name) {
        PaymentExpressWSSoapWSDDServiceName = name;
    }

    public com.paymentexpress.stubs.PaymentExpressWSSoap_PortType getPaymentExpressWSSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentExpressWSSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentExpressWSSoap(endpoint);
    }

    public com.paymentexpress.stubs.PaymentExpressWSSoap_PortType getPaymentExpressWSSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub _stub = new com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub(portAddress, this);
            _stub.setPortName(getPaymentExpressWSSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentExpressWSSoapEndpointAddress(java.lang.String address) {
        PaymentExpressWSSoap_address = address;
    }


    // Use to get a proxy class for PaymentExpressWSSoap12
    private java.lang.String PaymentExpressWSSoap12_address = "https://sec.paymentexpress.com/WS/PXWS.asmx";

    public java.lang.String getPaymentExpressWSSoap12Address() {
        return PaymentExpressWSSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PaymentExpressWSSoap12WSDDServiceName = "PaymentExpressWSSoap12";

    public java.lang.String getPaymentExpressWSSoap12WSDDServiceName() {
        return PaymentExpressWSSoap12WSDDServiceName;
    }

    public void setPaymentExpressWSSoap12WSDDServiceName(java.lang.String name) {
        PaymentExpressWSSoap12WSDDServiceName = name;
    }

    public com.paymentexpress.stubs.PaymentExpressWSSoap_PortType getPaymentExpressWSSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentExpressWSSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentExpressWSSoap12(endpoint);
    }

    public com.paymentexpress.stubs.PaymentExpressWSSoap_PortType getPaymentExpressWSSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.paymentexpress.stubs.PaymentExpressWSSoap12Stub _stub = new com.paymentexpress.stubs.PaymentExpressWSSoap12Stub(portAddress, this);
            _stub.setPortName(getPaymentExpressWSSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentExpressWSSoap12EndpointAddress(java.lang.String address) {
        PaymentExpressWSSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.paymentexpress.stubs.PaymentExpressWSSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub _stub = new com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub(new java.net.URL(PaymentExpressWSSoap_address), this);
                _stub.setPortName(getPaymentExpressWSSoapWSDDServiceName());
                return _stub;
            }
            if (com.paymentexpress.stubs.PaymentExpressWSSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.paymentexpress.stubs.PaymentExpressWSSoap12Stub _stub = new com.paymentexpress.stubs.PaymentExpressWSSoap12Stub(new java.net.URL(PaymentExpressWSSoap12_address), this);
                _stub.setPortName(getPaymentExpressWSSoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
	    switch (inputPortName) {
		    case "PaymentExpressWSSoap":
			    return getPaymentExpressWSSoap();
		    case "PaymentExpressWSSoap12":
			    return getPaymentExpressWSSoap12();
		    default:
			    java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			    ((org.apache.axis.client.Stub) _stub).setPortName(portName);
			    return _stub;
	    }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://PaymentExpress.com", "PaymentExpressWS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://PaymentExpress.com", "PaymentExpressWSSoap"));
            ports.add(new javax.xml.namespace.QName("http://PaymentExpress.com", "PaymentExpressWSSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

	    switch (portName) {
		    case "PaymentExpressWSSoap":
			    setPaymentExpressWSSoapEndpointAddress(address);
			    break;
		    case "PaymentExpressWSSoap12":
			    setPaymentExpressWSSoap12EndpointAddress(address);
			    break;
		    default:  // Unknown Port Name
			    throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
	    }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
