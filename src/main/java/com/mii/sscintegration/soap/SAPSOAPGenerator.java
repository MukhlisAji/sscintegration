package com.mii.sscintegration.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.bmc.arsys.api.EntryListInfo;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.SAPIntegrationCostMetadata;
import com.mii.sscintegration.domain.SAPIntegrationMetadata;
import com.mii.sscintegration.domain.SAPIntegrationReceiptsMetadata;
import com.mii.sscintegration.domain.SAPIntegrationTransitMetadata;
import com.mii.sscintegration.domain.SoapResponse;

public class SAPSOAPGenerator {

    protected static Logger logger = Logger.getLogger("SOAPGenerator: ");

    public SoapResponse createSOAPMessage(
            SAPIntegrationMetadata sapIntegrationMetadata,
            List<SAPIntegrationReceiptsMetadata> listSapReceipts,
            List<SAPIntegrationCostMetadata> listSAPCost,
            List<SAPIntegrationTransitMetadata> listSapTransit,
            ConfigurationValue configValue) {
        try {
            MessageFactory messageFactory;
            messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            String sapCredential = configValue.getSapCredential();//"TASUSER:pertamina";

            //Create SOAP Message body based on work order information
            soapMessage = createSOAPEnvelopeBody(soapMessage, sapIntegrationMetadata, listSapReceipts, listSAPCost, listSapTransit);

            //Setting credential user and action
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
            headers.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(sapCredential.getBytes())));

            soapMessage.saveChanges();

            //Change the SOAPMessage in order to log the real soap message in log file
            ByteArrayOutputStream outXml = new ByteArrayOutputStream();
            soapMessage.writeTo(outXml);
            logger.info("Generated SOAPMessage:\n" + new String(outXml.toByteArray()));

            return callWebService(soapMessage, configValue);

            //return soapMessage;
        } catch (SOAPException e) {
            logger.info("SOAPException Error on createSOAPMessage: " + e.toString());
            return null;
        } catch (IOException e) {
            logger.info("IOException Error on createSOAPMessage: " + e.toString());
            return null;
        }
    }

    private SOAPMessage createSOAPEnvelopeBody(
            SOAPMessage soapMessage,
            SAPIntegrationMetadata sapIntegrationMetadata,
            List<SAPIntegrationReceiptsMetadata> listSapReceipts,
            List<SAPIntegrationCostMetadata> listSAPCost,
            List<SAPIntegrationTransitMetadata> listSapTransit) {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "urn";
        String myNamespaceURI = "urn:sap-com:document:sap:rfc:functions";

        try {
            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement soapBodyElem = soapBody.addChildElement("ZFMHR_POST_TRV", myNamespace);

            String fcode = sapIntegrationMetadata.getFcode();
            if (!fcode.equals("INS")) {
                logger.info("ini bukan insert");
                SOAPElement soapBodyElemCcode = soapBodyElem.addChildElement("TRIPNO");
                soapBodyElemCcode.addTextNode(sapIntegrationMetadata.getTripNo());
            }

            SOAPElement soapBodyElemCcode = soapBodyElem.addChildElement("CCODE");
            soapBodyElemCcode.addTextNode(sapIntegrationMetadata.getCcode());

            SOAPElement soapBodyElemCostTrip = soapBodyElem.addChildElement("COSTDIST_TRIP");

            for (SAPIntegrationCostMetadata eListInfoCost : listSAPCost) {
                SOAPElement soapBodyElemItem = soapBodyElemCostTrip.addChildElement("item");
                SOAPElement soapBodyElemPrecShare = soapBodyElemItem.addChildElement("PERC_SHARE");
                soapBodyElemPrecShare.addTextNode(eListInfoCost.getPrecShare());
                SOAPElement soapBodyElemCompCode = soapBodyElemItem.addChildElement("COSTCENTER");
                soapBodyElemCompCode.addTextNode(eListInfoCost.getCostCenter());
                //SOAPElement soapBodyElemWBSElement = soapBodyElemItem.addChildElement("WBS_ELEMT");
                //soapBodyElemWBSElement.addTextNode("D14/15/B6/RT-C");
            }

            SOAPElement soapBodyElemEmpNumber = soapBodyElem.addChildElement("EMPLOYEENUMBER");
            soapBodyElemEmpNumber.addTextNode(sapIntegrationMetadata.getEmpNumber());

            SOAPElement soapBodyElemFcode = soapBodyElem.addChildElement("FCODE");
            soapBodyElemFcode.addTextNode(sapIntegrationMetadata.getFcode());

            SOAPElement soapBodyElemFrameData = soapBodyElem.addChildElement("FRAMEDATA");
            SOAPElement soapBodyElemDepDate = soapBodyElemFrameData.addChildElement("DEP_DATE");
            soapBodyElemDepDate.addTextNode(sapIntegrationMetadata.getDepDate());

            SOAPElement soapBodyElemDepTime = soapBodyElemFrameData.addChildElement("DEP_TIME");
            soapBodyElemDepTime.addTextNode(sapIntegrationMetadata.getDepTime());

            SOAPElement soapBodyElemArrDate = soapBodyElemFrameData.addChildElement("ARR_DATE");
            soapBodyElemArrDate.addTextNode(sapIntegrationMetadata.getArrDate());

            SOAPElement soapBodyElemArrTime = soapBodyElemFrameData.addChildElement("ARR_TIME");
            soapBodyElemArrTime.addTextNode(sapIntegrationMetadata.getArrTime());

            SOAPElement soapBodyElemCustomer = soapBodyElemFrameData.addChildElement("CUSTOMER");
            soapBodyElemCustomer.addTextNode(sapIntegrationMetadata.getCustomer());

            SOAPElement soapBodyElemLocation = soapBodyElemFrameData.addChildElement("LOCATION");
            soapBodyElemLocation.addTextNode(sapIntegrationMetadata.getLocation());

            SOAPElement soapBodyElemCountry = soapBodyElemFrameData.addChildElement("COUNTRY");
            soapBodyElemCountry.addTextNode(sapIntegrationMetadata.getCountry());

            SOAPElement soapBodyElemSchema = soapBodyElemFrameData.addChildElement("T_SCHEMA");
            soapBodyElemSchema.addTextNode(sapIntegrationMetadata.gettSchema());

            SOAPElement soapBodyElemActType = soapBodyElemFrameData.addChildElement("T_ACTYPE");
            soapBodyElemActType.addTextNode(sapIntegrationMetadata.gettActype());

            SOAPElement soapBodyElemComsp = soapBodyElemFrameData.addChildElement("TT_COMSP");
            soapBodyElemComsp.addTextNode(sapIntegrationMetadata.getTtComsp());

            SOAPElement soapBodyElemStatus = soapBodyElem.addChildElement("STATUS");
            SOAPElement soapBodyElemApproved = soapBodyElemStatus.addChildElement("APPROVED");
            soapBodyElemApproved.addTextNode(sapIntegrationMetadata.getApproved());

            SOAPElement soapBodyElemAccounts = soapBodyElemStatus.addChildElement("ACCOUNT");
            soapBodyElemAccounts.addTextNode("2");

            int totalTransit = 0;
            SOAPElement soapBodyElemStopOver = soapBodyElem.addChildElement("STOPOVER");
            while (totalTransit < listSapTransit.size()) {
                SAPIntegrationTransitMetadata sapTransit = listSapTransit.get(totalTransit);
                SOAPElement soapBodyElemStopOverItem = soapBodyElemStopOver.addChildElement("item");

                SOAPElement soapBodyElemStopOverDepDate = soapBodyElemStopOverItem.addChildElement("DEP_DATE");
                soapBodyElemStopOverDepDate.addTextNode(sapTransit.depDate);

                SOAPElement soapBodyElemStopOverDepTime = soapBodyElemStopOverItem.addChildElement("DEP_TIME");
                soapBodyElemStopOverDepTime.addTextNode("00:01:00");

                SOAPElement soapBodyElemStopOverArrDate = soapBodyElemStopOverItem.addChildElement("ARR_DATE");
                soapBodyElemStopOverArrDate.addTextNode(sapTransit.arrDate);

                SOAPElement soapBodyElemStopOverArrTime = soapBodyElemStopOverItem.addChildElement("ARR_TIME");
                soapBodyElemStopOverArrTime.addTextNode(sapIntegrationMetadata.getArrTime());

                //SOAPElement soapBodyElemStopOverCustomer = soapBodyElemStopOverItem.addChildElement("CUSTOMER");
                //soapBodyElemStopOverCustomer.addTextNode(sapTransit.customer);
                SOAPElement soapBodyElemStopOverLocation = soapBodyElemStopOverItem.addChildElement("LOCATION");
                soapBodyElemStopOverLocation.addTextNode(sapTransit.location);

                SOAPElement soapBodyElemStopOverComsp = soapBodyElemStopOverItem.addChildElement("TT_COMSP");
                soapBodyElemStopOverComsp.addTextNode(sapIntegrationMetadata.getTtComsp());

                SOAPElement soapBodyElemStopOverActype = soapBodyElemStopOverItem.addChildElement("T_ACTYPE");
                soapBodyElemStopOverActype.addTextNode(sapIntegrationMetadata.gettActype());

                totalTransit++;
            }

            SOAPElement soapBodyElemReceipts = soapBodyElem.addChildElement("RECEIPTS");
            //for (SAPIntegrationReceiptsMetadata eListInfoReceipts : listSapReceipts) {
            int totalReceipt = 0;
            while (totalReceipt < listSapReceipts.size()) {
                SAPIntegrationReceiptsMetadata sapReceipt = listSapReceipts.get(totalReceipt);
                //logger.info("urutan"+totalReceipt);
                SOAPElement soapBodyElemReceiptsItem = soapBodyElemReceipts.addChildElement("item");

                SOAPElement soapBodyElemReceipno = soapBodyElemReceiptsItem.addChildElement("RECEIPTNO");
                soapBodyElemReceipno.addTextNode(sapReceipt.getReceiptNumber());

                logger.info(totalReceipt + "Receipt Number:" + sapReceipt.getReceiptNumber());

                SOAPElement soapBodyElemExpType = soapBodyElemReceiptsItem.addChildElement("EXP_TYPE");
                soapBodyElemExpType.addTextNode(sapReceipt.getExpType());

                SOAPElement soapBodyElemRecAmount = soapBodyElemReceiptsItem.addChildElement("REC_AMOUNT");
                soapBodyElemRecAmount.addTextNode(sapReceipt.getRecAmount());

                SOAPElement soapBodyElemRecCurr = soapBodyElemReceiptsItem.addChildElement("REC_CURR");
                soapBodyElemRecCurr.addTextNode(sapReceipt.getRecCurr());

                SOAPElement soapBodyElemRecShort = soapBodyElemReceiptsItem.addChildElement("SHORTTXT");
                soapBodyElemRecShort.addTextNode(sapReceipt.getExpType());

                if (sapReceipt.getExpType().equalsIgnoreCase("OTIH")
                        || sapReceipt.getExpType().equalsIgnoreCase("OTIP")
                        || sapReceipt.getExpType().equalsIgnoreCase("OTPH")
                        || sapReceipt.getExpType().equalsIgnoreCase("OTPP")) {

                    SOAPElement soapBodyElemRecDate = soapBodyElemReceiptsItem.addChildElement("REC_DATE");
                    soapBodyElemRecDate.addTextNode(sapReceipt.getRecDate());
                }

                totalReceipt++;
            }

        } catch (SOAPException e) {
            logger.info("SOAPException Error on createSOAPEnvelopeBody: " + e.toString());
        }

        return soapMessage;
    }

    public SoapResponse callWebService(SOAPMessage soapMessage, ConfigurationValue configValue) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory;
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            //checking connection before call
            //URL url = new URL("http://dev02xi.pertamina.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=HRIS_D&receiverParty=&receiverService=&interface=mi_osPost_TRV&interfaceNamespace=urn:pertamina:hris");
            URL url = new URL(configValue.getSapTripWs());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int status = con.getResponseCode();
            logger.info("SAP URL:" + configValue.getSapTripWs());
            if (status == 200) {
                logger.info("Connection is OK: ");
            } else {
                logger.info("Http responde code: " + status);
            }

            // Send SOAP Message to Opentext / SAP webservice
            //SOAPMessage soapResponse = soapConnection.call(soapMessage, "http://dev02xi.pertamina.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=HRIS_D&receiverParty=&receiverService=&interface=mi_osPost_TRV&interfaceNamespace=urn:pertamina:hris");
            SOAPMessage soapResponse = soapConnection.call(soapMessage, configValue.getSapTripWs());
            //Log the SOAP Response
            ByteArrayOutputStream outXml = new ByteArrayOutputStream();
            soapResponse.writeTo(outXml);
            logger.info("SOAP Response: " + new String(outXml.toByteArray()));

            //Close SOAP Connection
            soapConnection.close();

            return new SoapResponse(soapResponse, "Success");
        } catch (UnsupportedOperationException e) {
            logger.info("UnsupportedOperationException Error on callOpentextSOAP: " + e.toString());
            return new SoapResponse(null, "UnsupportedOperationException Error on callOpentextSOAP: " + e.toString());
        } catch (SOAPException e) {
            logger.info("SOAPException Error on callOpentextSOAP: " + e.getMessage());
            return new SoapResponse(null, "SOAPException Error on callOpentextSOAP: " + e.getMessage());
        } catch (IOException e) {
            logger.info("IOException Error on callOpentextSOAP: " + e.toString());
            return new SoapResponse(null, "IOException Error on callOpentextSOAP: " + e.toString());
        }
    }
}
