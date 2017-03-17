/**
 * KpWebservices_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.opencps.paymentmgt.keypay.wssoap;

public interface KpWebservices_PortType extends java.rmi.Remote {
    public java.lang.String querryBillStatus(java.lang.String merchant_trans_id, java.lang.String good_code, java.lang.String trans_id, java.lang.String merchant_code, java.lang.String secure_hash) throws java.rmi.RemoteException;
    public java.lang.String sendOrder(java.lang.String return_url, java.lang.String version, java.lang.String current_locale, java.lang.String currency_code, java.lang.String command, java.lang.String merchant_trans_id, java.lang.String country_code, java.lang.String good_code, java.lang.String xml_description, java.lang.String net_cost, java.lang.String ship_fee, java.lang.String tax, java.lang.String merchant_code, java.lang.String service_code, java.lang.String secure_hash, java.lang.String desc_1, java.lang.String desc_2, java.lang.String desc_3, java.lang.String desc_4, java.lang.String desc_5, java.lang.String internal_bank) throws java.rmi.RemoteException;
    public java.lang.String confirmTransResult(java.lang.String merchant_code, java.lang.String good_code, java.lang.String merchant_trans_id, java.lang.String trans_id, java.lang.String trans_result, java.lang.String secure_hash) throws java.rmi.RemoteException;
}
