package com.mii.sscintegration.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

import com.mii.sscintegration.domain.SoapResponse;


public class SOAPGenerator {
	protected static Logger logger = Logger.getLogger("SOAPGenerator: ");
	
	public SOAPMessage createSOAPMessage() {
		try {
			MessageFactory messageFactory;
			messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			
			//Create SOAP Message body based on work order information
			soapMessage = createSOAPEnvelopeBody(soapMessage);
			
			//Setting credential user and action
			MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", "http://tempuri.org/IAdInterimService/SelectAdInterimAttributes");
	        //headers.addHeader("Authorization", "Basic " + new  String(Base64.encodeBase64(configValue.getOpentextLogin().getBytes())));
	        
	        soapMessage.saveChanges();
	        
	        //Change the SOAPMessage in order to log the real soap message in log file
	        ByteArrayOutputStream outXml = new ByteArrayOutputStream();
	        soapMessage.writeTo(outXml);
	        logger.info("Generated SOAPMessage:\n"+new String (outXml.toByteArray()));
	        
	        callWebService(soapMessage);
			
			return soapMessage;
		}catch (SOAPException e) {
			logger.info("SOAPException Error on createSOAPMessage: "+e.toString());
			return null;
		}catch (IOException e) {
			logger.info("IOException Error on createSOAPMessage: "+e.toString());
			return null;
		}
	}
	
	private SOAPMessage createSOAPEnvelopeBody(SOAPMessage soapMessage) {
		SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "tem";
        String myNamespaceURI = "http://tempuri.org/";
        
        try {
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
	        
	        // SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("SelectAdInterimAttributes", myNamespace);
	        
	        SOAPElement soapBodyElemDate = soapBodyElem.addChildElement("date", myNamespace);
	        soapBodyElemDate.addTextNode("2018-10-10");
	        
	        SOAPElement soapBodyElemUsername = soapBodyElem.addChildElement("SvcUserName", myNamespace);
	        soapBodyElemUsername.addTextNode("pjs_admin");
	        
	        SOAPElement soapBodyElemPassword = soapBodyElem.addChildElement("SvcPassword", myNamespace);
	        soapBodyElemPassword.addTextNode("pertamina@1");
        }catch (SOAPException e) {
			logger.info("SOAPException Error on createSOAPEnvelopeBody: "+e.toString());
		}
		
		return soapMessage;
	}
	
	public SoapResponse callWebService(SOAPMessage soapMessage) {
		try {
			// Create SOAP Connection
	        SOAPConnectionFactory soapConnectionFactory;
			soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			//checking connection before call
			URL url = new URL("http://ptmkpwebpipsdev.pertamina.com/eCorr_Service/AdInterim/AdInterimService.svc");
		    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		    int status = con.getResponseCode();
		    if (status == 200){
		    		logger.info("Connection is OK: ");
		    } else {
		    		logger.info("Http responde code: "+status);
		    }
			
			// Send SOAP Message to Opentext / SAP webservice
		    SOAPMessage soapResponse = soapConnection.call(soapMessage, "http://ptmkpwebpipsdev.pertamina.com/eCorr_Service/AdInterim/AdInterimService.svc");
		    
		    //Log the SOAP Response
		    ByteArrayOutputStream outXml = new ByteArrayOutputStream();
		    soapResponse.writeTo(outXml);
		    logger.info("SOAP Response: "+new String (outXml.toByteArray()));
		    
		    //Close SOAP Connection
		    soapConnection.close();
		    
		    return new SoapResponse(soapResponse, "Success");
		}catch (UnsupportedOperationException e) {
			logger.info("UnsupportedOperationException Error on callOpentextSOAP: "+e.toString());
			return new SoapResponse(null, "UnsupportedOperationException Error on callOpentextSOAP: "+e.toString());
		}catch (SOAPException e) {
			logger.info("SOAPException Error on callOpentextSOAP: "+e.getMessage());
			return new SoapResponse(null, "SOAPException Error on callOpentextSOAP: "+e.getMessage());
		}catch (IOException e) {
			logger.info("IOException Error on callOpentextSOAP: "+e.toString());
			return new SoapResponse(null, "IOException Error on callOpentextSOAP: "+e.toString());
		}
	}
}
