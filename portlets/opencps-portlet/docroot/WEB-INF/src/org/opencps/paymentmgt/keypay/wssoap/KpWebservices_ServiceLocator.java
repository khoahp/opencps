/**
 * KpWebservices_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.opencps.paymentmgt.keypay.wssoap;

public class KpWebservices_ServiceLocator extends org.apache.axis.client.Service implements org.opencps.paymentmgt.keypay.wssoap.KpWebservices_Service {

    public KpWebservices_ServiceLocator() {
    }


    public KpWebservices_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public KpWebservices_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for kpWebservicesPort
    private java.lang.String kpWebservicesPort_address = "http://webservices.keypay.vn:80/kpWebservices";

    public java.lang.String getkpWebservicesPortAddress() {
        return kpWebservicesPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String kpWebservicesPortWSDDServiceName = "kpWebservicesPort";

    public java.lang.String getkpWebservicesPortWSDDServiceName() {
        return kpWebservicesPortWSDDServiceName;
    }

    public void setkpWebservicesPortWSDDServiceName(java.lang.String name) {
        kpWebservicesPortWSDDServiceName = name;
    }

    public org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType getkpWebservicesPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(kpWebservicesPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getkpWebservicesPort(endpoint);
    }

    public org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType getkpWebservicesPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	org.opencps.paymentmgt.keypay.wssoap.KpWebservicesPortBindingStub _stub = new org.opencps.paymentmgt.keypay.wssoap.KpWebservicesPortBindingStub(portAddress, this);
            _stub.setPortName(getkpWebservicesPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setkpWebservicesPortEndpointAddress(java.lang.String address) {
        kpWebservicesPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
            	org.opencps.paymentmgt.keypay.wssoap.KpWebservicesPortBindingStub _stub = new org.opencps.paymentmgt.keypay.wssoap.KpWebservicesPortBindingStub(new java.net.URL(kpWebservicesPort_address), this);
                _stub.setPortName(getkpWebservicesPortWSDDServiceName());
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
        if ("kpWebservicesPort".equals(inputPortName)) {
            return getkpWebservicesPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://kpWebservices/", "kpWebservices");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://kpWebservices/", "kpWebservicesPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("kpWebservicesPort".equals(portName)) {
            setkpWebservicesPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
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
