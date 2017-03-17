package org.opencps.paymentmgt.keypay.wssoap;

import java.rmi.RemoteException;

public class KpWebservicesProxy implements org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType {
  private String _endpoint = null;
  private org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType kpWebservices_PortType = null;
  
  public KpWebservicesProxy() {
    _initKpWebservicesProxy();
  }
  
  public KpWebservicesProxy(String endpoint) {
    _endpoint = endpoint;
    _initKpWebservicesProxy();
  }
  
  private void _initKpWebservicesProxy() {
    try {
      kpWebservices_PortType = (new org.opencps.paymentmgt.keypay.wssoap.KpWebservices_ServiceLocator()).getkpWebservicesPort();
      if (kpWebservices_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)kpWebservices_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)kpWebservices_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (kpWebservices_PortType != null)
      ((javax.xml.rpc.Stub)kpWebservices_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.opencps.paymentmgt.keypay.wssoap.KpWebservices_PortType getKpWebservices_PortType() {
    if (kpWebservices_PortType == null)
      _initKpWebservicesProxy();
    return kpWebservices_PortType;
  }

@Override
public String querryBillStatus(String merchant_trans_id, String good_code,
		String trans_id, String merchant_code, String secure_hash)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String sendOrder(String return_url, String version,
		String current_locale, String currency_code, String command,
		String merchant_trans_id, String country_code, String good_code,
		String xml_description, String net_cost, String ship_fee, String tax,
		String merchant_code, String service_code, String secure_hash,
		String desc_1, String desc_2, String desc_3, String desc_4,
		String desc_5, String internal_bank) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String confirmTransResult(String merchant_code, String good_code,
		String merchant_trans_id, String trans_id, String trans_result,
		String secure_hash) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}
  
  
}