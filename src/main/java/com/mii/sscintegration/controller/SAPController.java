package com.mii.sscintegration.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.SAPIntegrationCostMetadata;
import com.mii.sscintegration.domain.SAPIntegrationMetadata;
import com.mii.sscintegration.domain.SAPIntegrationReceiptsMetadata;
import com.mii.sscintegration.domain.SAPIntegrationTransitMetadata;
import com.mii.sscintegration.domain.SoapResponse;
import com.mii.sscintegration.soap.SAPSOAPGenerator;

public class SAPController {

    protected static Logger logger = Logger.getLogger("SAPController: ");

    public void sendToSAP() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        //Setting SAP Metadata
        SAPIntegrationMetadata sapIntegrationMetadata = new SAPIntegrationMetadata();

        //List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:INT:SAP", "'Status__c'=\"0\" AND 'REQNUMBER__c'=\"REQ000000005097\"");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:INT:SAP", "'Status__c'=\"0\" ");
        try {
            int jumlahReq = 0;
            while (jumlahReq < 1) {
                jumlahReq++;
                for (EntryListInfo eListInfo : eListInfos) {
                    Entry record = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListInfo.getEntryID(), null);
                    String reqNumber = record.get(536870928).getValue().toString();
                    String fcode = record.get(536870916).getValue().toString();
                    logger.info("nomer req=" + reqNumber);
                    sapIntegrationMetadata.setReqNumber(reqNumber);
                    sapIntegrationMetadata.setCcode(record.get(536870914).getValue().toString());
                    sapIntegrationMetadata.setEmpNumber(record.get(536870915).getValue().toString());
                    sapIntegrationMetadata.setFcode(fcode);
                    sapIntegrationMetadata.setApproved(record.get(536870917).getValue().toString());
                    sapIntegrationMetadata.setDepDate(record.get(536870919).getValue().toString());
                    sapIntegrationMetadata.setDepTime(record.get(536870920).getValue().toString());
                    sapIntegrationMetadata.setArrDate(record.get(536870921).getValue().toString());
                    sapIntegrationMetadata.setArrTime(record.get(536870922).getValue().toString());
                    sapIntegrationMetadata.setCustomer(record.get(536870923).getValue().toString());
                    sapIntegrationMetadata.setLocation(record.get(536870924).getValue().toString());
                    sapIntegrationMetadata.setCountry(record.get(536870925).getValue().toString());
                    sapIntegrationMetadata.settSchema(record.get(536870926).getValue().toString());
                    sapIntegrationMetadata.setTtComsp(record.get(536870929).getValue().toString());
                    sapIntegrationMetadata.settActype(record.get(536870930).getValue().toString());
//                    String isMitra = ;

                    switch (fcode) {
                        case "DEL":
                            sapIntegrationMetadata.setTripNo(record.get(536870927).getValue().toString());
                            break;
                        case "MOD":
                            sapIntegrationMetadata.setTripNo(record.get(536870927).getValue().toString());
                            break;
                    }

                    //if(record.get(536870927)!=null)
                    //	sapIntegrationMetadata.setTripNo(record.get(536870927).getValue().toString());
                    //sapIntegrationMetadata.setReqNumber(record.get(536870928).getValue().toString());
                    //sapStatus, 536870931
                    //List<SAPIntegrationReceiptsMetadata> listSapReceipts = getReceipts(reqNumber, remedyAPI, remedySession);
                    List<SAPIntegrationReceiptsMetadata> listSapReceipts = getNewReceipts(reqNumber, remedyAPI, remedySession);
                    List<SAPIntegrationCostMetadata> listSAPCost = getCostCenter(reqNumber, remedyAPI, remedySession);
                    List<SAPIntegrationTransitMetadata> listSapTransit = getTransit(reqNumber, remedyAPI, remedySession);

                    SAPSOAPGenerator sapSoapGenerator = new SAPSOAPGenerator();
                    SoapResponse soapResponse = readSAPResponse(sapSoapGenerator.createSOAPMessage(
                            sapIntegrationMetadata, listSapReceipts, listSAPCost, listSapTransit, configValue));

                    //updating middle form to skip this record in the next scheduler
                    record.put(7, new Value("2"));
                    remedySession.setEntry("PTM:SSC:HR:Travel:INT:SAP", record.getEntryId(), record, null, 0);

                    //update status in BMC
                    ByteArrayOutputStream outXml = new ByteArrayOutputStream();
                    soapResponse.getSoapResponse().writeTo(outXml);
                    logger.info("sap : " + outXml);
                    record.put(536870934, new Value(new String(outXml.toByteArray())));
                    record.put(536870932, new Value(soapResponse.getErrorMessage()));
                    record.put(536870931, new Value(soapResponse.getStatus()));

                    String statusSAP = soapResponse.getStatus();
                    logger.info("status sap===" + statusSAP);
                    switch (statusSAP) {
                        case "S":
                            record.put(7, new Value("1"));
                            String tripNumber = soapResponse.getTripNo();
                            record.put(536870927, new Value(tripNumber));

                            if (fcode.equals("INS") || fcode.equals("MOD")) {
                                //generate corporate ref id
                                String corporateReferenceID = "";
                                corporateReferenceID = reqNumber.substring(3);
                                corporateReferenceID = corporateReferenceID.replaceFirst("^0+(?!$)", "");
                                corporateReferenceID += sapIntegrationMetadata.getEmpNumber();
                                corporateReferenceID += tripNumber.replaceFirst("^0+(?!$)", "");
                                logger.info("corporate=" + corporateReferenceID);

                                String vendorCBT = record.get(536870937).getValue().toString();
                                if (vendorCBT.equals("Mitra")) {
                                    corporateReferenceID = "M" + corporateReferenceID;
                                    record.put(8, new Value(corporateReferenceID));
                                } else if (vendorCBT.equals("Traveloka")) {
                                    record.put(8, new Value(corporateReferenceID));
                                } else if (vendorCBT.equals("Tidak")) {
                                    record.put(8, new Value(corporateReferenceID));
                                }
                            }
                            break;
                        case "E":
                            record.put(7, new Value("3"));
                            break;
                    }
                    //updating middle form
                    remedySession.setEntry("PTM:SSC:HR:Travel:INT:SAP", record.getEntryId(), record, null, 0);
                }
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        } catch (IOException e) {
            logger.info("IOException Error on sendToSAP: " + e.toString());
        } catch (SOAPException e) {
            logger.info("SOAPException Error on sendToSAP: " + e.toString());
        }

    }

    public List<SAPIntegrationReceiptsMetadata> getReceipts(String reqNumber, RemedyAPI remedyAPI, ARServerUser remedySession) {
        SAPIntegrationReceiptsMetadata sapReceiptsMetadata = new SAPIntegrationReceiptsMetadata();
        List<SAPIntegrationReceiptsMetadata> listReceipts = new ArrayList<SAPIntegrationReceiptsMetadata>();
        SAPIntegrationReceiptsMetadata sapReceiptsMetadataOtip = new SAPIntegrationReceiptsMetadata();
        SAPIntegrationReceiptsMetadata sapReceiptsMetadataOtih = new SAPIntegrationReceiptsMetadata();

        //searching for OTIP
        List<EntryListInfo> eListInfosOTIPReceipts = remedyAPI.getRemedyRecordByQuery(
                remedySession,
                "PTM:SSC:HR:Travel:INT:SAP:Receipts",
                "'REQNUMBER__c'=\"" + reqNumber + "\" AND 'EXP_TYPE__c'=\"OTIP\" ");
        int totalOtip = 0;
        try {
            for (EntryListInfo eListInfosOTIPReceipt : eListInfosOTIPReceipts) {
                sapReceiptsMetadataOtip = new SAPIntegrationReceiptsMetadata();
                Entry recordOTIP = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:Receipts", eListInfosOTIPReceipt.getEntryID(), null);
                totalOtip += Integer.parseInt(recordOTIP.get(536870917).getValue().toString());

                sapReceiptsMetadataOtip.setReqNumber(reqNumber);
                sapReceiptsMetadataOtip.setReceiptNumber(recordOTIP.get(536870915).getValue().toString());
                sapReceiptsMetadataOtip.setExpType(recordOTIP.get(536870916).getValue().toString());
                sapReceiptsMetadataOtip.setRecCurr(recordOTIP.get(536870918).getValue().toString());
                sapReceiptsMetadataOtip.setRecAmount(recordOTIP.get(536870917).getValue().toString());

                listReceipts.add(sapReceiptsMetadataOtip);
            }

            //sapReceiptsMetadataOtip.setRecAmount(String.valueOf(totalOtip));
            //listReceipts.add(2, sapReceiptsMetadata);
        } catch (ARException e) {
            logger.info("ARException Error on get OTIP Receipts: " + e.toString());
        }

        //searching for OTIH
        int totalOtih = 0;
        List<EntryListInfo> eListInfosOTIHReceipts = remedyAPI.getRemedyRecordByQuery(
                remedySession,
                "PTM:SSC:HR:Travel:INT:SAP:Receipts",
                "'REQNUMBER__c'=\"" + reqNumber + "\" AND 'EXP_TYPE__c'=\"OTIH\" ");
        try {
            for (EntryListInfo eListInfosOTIHReceipt : eListInfosOTIHReceipts) {
                sapReceiptsMetadataOtih = new SAPIntegrationReceiptsMetadata();
                Entry recordOTIH = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:Receipts", eListInfosOTIHReceipt.getEntryID(), null);
                totalOtih += Integer.parseInt(recordOTIH.get(536870917).getValue().toString());

                sapReceiptsMetadataOtih.setReqNumber(reqNumber);
                sapReceiptsMetadataOtih.setReceiptNumber(recordOTIH.get(536870915).getValue().toString());
                sapReceiptsMetadataOtih.setExpType(recordOTIH.get(536870916).getValue().toString());
                sapReceiptsMetadataOtih.setRecCurr(recordOTIH.get(536870918).getValue().toString());
                sapReceiptsMetadataOtih.setRecAmount(recordOTIH.get(536870917).getValue().toString());

                listReceipts.add(sapReceiptsMetadataOtih);
            }

            //sapReceiptsMetadataOtih.setRecAmount(String.valueOf(totalOtih));
            //listReceipts.add(1, sapReceiptsMetadata);
        } catch (ARException e) {
            logger.info("ARException Error on get OTIH Receipts: " + e.toString());
        }

        //searching ecost
        List<EntryListInfo> eListInfosReceipts = remedyAPI.getRemedyRecordByQuery(
                remedySession, "PTM:SSC:HR:Travel:INT:SAP:Receipts", "'REQNUMBER__c'=\"" + reqNumber + "\" AND 'EXP_TYPE__c'=\"ECOS\" ");
        try {
            int indexArray = 0;
            int totalEcost = 0;
            for (EntryListInfo eListInfoRec : eListInfosReceipts) {
                sapReceiptsMetadata = new SAPIntegrationReceiptsMetadata();
                Entry recordRec = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:Receipts", eListInfoRec.getEntryID(), null);
                String receiptNo = recordRec.get(536870915).getValue().toString();
                logger.info(indexArray + "-receipts=" + receiptNo);

                sapReceiptsMetadata.setReqNumber(reqNumber);
                sapReceiptsMetadata.setReceiptNumber(receiptNo);
                sapReceiptsMetadata.setExpType(recordRec.get(536870916).getValue().toString());
                totalEcost += Integer.parseInt(recordRec.get(536870917).getValue().toString());
                totalEcost = totalEcost - (totalOtih + totalOtip);
                sapReceiptsMetadata.setRecAmount(String.valueOf(totalEcost));
                //sapReceiptsMetadata.setRecAmount(totalEcost.toString());
                sapReceiptsMetadata.setRecCurr(recordRec.get(536870918).getValue().toString());
                listReceipts.add(sapReceiptsMetadata);
                //listReceipts.add(0, sapReceiptsMetadata);
                logger.info(indexArray + "++++++++++=" + sapReceiptsMetadata.getReceiptNumber());
                indexArray++;
            }

            /* For testing only
			int tempArray = 0;
			SAPIntegrationReceiptsMetadata sapReceipts = new SAPIntegrationReceiptsMetadata();
			while(tempArray<listReceipts.size()) {
				sapReceipts = listReceipts.get(tempArray);
				logger.info(tempArray+"-expType="+sapReceipts.getReceiptNumber());
				tempArray++;
			}*/
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata getReceipts: " + e.toString());
        }

        /*
		int jumlahReceipt = 1;
		if(sapReceiptsMetadataOtih.getReqNumber()!=null) {
			listReceipts.add(jumlahReceipt, sapReceiptsMetadataOtih);
			jumlahReceipt += 1;
		}
		
		if(sapReceiptsMetadataOtip.getReqNumber()!=null) {
			listReceipts.add(jumlahReceipt, sapReceiptsMetadataOtip);
			jumlahReceipt += 1;
		}*/
        //push ke PTM:SSC:HR:SKPD
        return listReceipts;
    }

    public List<SAPIntegrationReceiptsMetadata> getNewReceipts(String reqNumber,
            RemedyAPI remedyAPI,
            ARServerUser remedySession) {
        SAPIntegrationReceiptsMetadata sapReceiptsMetadata = new SAPIntegrationReceiptsMetadata();
        List<SAPIntegrationReceiptsMetadata> listReceipts = new ArrayList<SAPIntegrationReceiptsMetadata>();

        //searching for Receipts
        List<EntryListInfo> eListInfosOTIPReceipts = remedyAPI.getRemedyRecordByQuery(
                remedySession,
                "PTM:SSC:HR:Travel:INT:SAP:Receipts",
                "'REQNUMBER__c'=\"" + reqNumber + "\" ");

        boolean ecostSent = false;

        try {
            for (EntryListInfo eListInfosOTIPReceipt : eListInfosOTIPReceipts) {
                sapReceiptsMetadata = new SAPIntegrationReceiptsMetadata();
                Entry recordReceipt = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:Receipts", eListInfosOTIPReceipt.getEntryID(), null);

                sapReceiptsMetadata.setReqNumber(reqNumber);
                sapReceiptsMetadata.setReceiptNumber(recordReceipt.get(536870915).getValue().toString());
                sapReceiptsMetadata.setExpType(recordReceipt.get(536870916).getValue().toString());
                sapReceiptsMetadata.setRecCurr(recordReceipt.get(536870918).getValue().toString());
                sapReceiptsMetadata.setRecAmount(recordReceipt.get(536870917).getValue().toString());

                if (sapReceiptsMetadata.getExpType().equalsIgnoreCase("OTIH")
                        || sapReceiptsMetadata.getExpType().equalsIgnoreCase("OTIP")
                        || sapReceiptsMetadata.getExpType().equalsIgnoreCase("OTPH")
                        || sapReceiptsMetadata.getExpType().equalsIgnoreCase("OTPP")) {
                    sapReceiptsMetadata.setRecDate(recordReceipt.get(536870919).getValue().toString());
                }

                    listReceipts.add(sapReceiptsMetadata);
            }

        } catch (ARException e) {
            logger.info("ARException Error on get OTIP Receipts: " + e.toString());
        }
        return listReceipts;
    }

    public List<SAPIntegrationCostMetadata> getCostCenter(String reqNumber, RemedyAPI remedyAPI, ARServerUser remedySession) {
        SAPIntegrationCostMetadata sapCostCenter = new SAPIntegrationCostMetadata();
        List<SAPIntegrationCostMetadata> listSapCostCenter = new ArrayList<SAPIntegrationCostMetadata>();
        List<EntryListInfo> eListInfosCost = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:INT:SAP:CostCenter", "'REQNUMBER__c'=\"" + reqNumber + "\"");
        try {
            int indexArray = 0;
            for (EntryListInfo eListInfoCost : eListInfosCost) {
                Entry recordCost = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:CostCenter", eListInfoCost.getEntryID(), null);
                sapCostCenter.setReqNumber(reqNumber);
                sapCostCenter.setCostCenter(recordCost.get(536870916).getValue().toString());
                sapCostCenter.setPrecShare(recordCost.get(536870915).getValue().toString());
                //listSapCostCenter.add(sapCostCenter);
                listSapCostCenter.add(indexArray, sapCostCenter);
                indexArray++;
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata getReceipts: " + e.toString());
        }

        return listSapCostCenter;
    }

    public List<SAPIntegrationTransitMetadata> getTransit(
            String reqNumber,
            RemedyAPI remedyAPI,
            ARServerUser remedySession) {
        List<SAPIntegrationTransitMetadata> listSapTransit = new ArrayList<SAPIntegrationTransitMetadata>();

        List<EntryListInfo> eListInfosTransits = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:INT:SAP:StopOver", "'REQNUMBER__c'=\"" + reqNumber + "\"");
        try {
            int indexArray = 0;
            for (EntryListInfo eListInfosTransit : eListInfosTransits) {
                Entry recordTransit = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP:StopOver", eListInfosTransit.getEntryID(), null);
                SAPIntegrationTransitMetadata sapTransit = new SAPIntegrationTransitMetadata();
                sapTransit.depDate = recordTransit.get(536870915).getValue().toString();
                sapTransit.arrDate = recordTransit.get(536870916).getValue().toString();
                sapTransit.country = recordTransit.get(536870919).getValue().toString();
                sapTransit.customer = recordTransit.get(536870917).getValue().toString();
                sapTransit.location = recordTransit.get(536870918).getValue().toString();
                sapTransit.reqNumber = reqNumber;
                sapTransit.tactype = recordTransit.get(536870921).getValue().toString();
                sapTransit.ttComsp = recordTransit.get(536870920).getValue().toString();
                listSapTransit.add(indexArray, sapTransit);
                indexArray++;
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata getReceipts: " + e.toString());
        }

        return listSapTransit;

    }

    /**
     * This method is used to mapping SAP response
     *
     * @param soapResponse
     * @return WOResponseElement
     */
    public SoapResponse readSAPResponse(SoapResponse soapResponse) {
        try {
            SOAPBody soapBody = soapResponse.getSoapResponse().getSOAPBody();

            java.util.Iterator soapBodyIterator = soapBody.getChildElements();
            while (soapBodyIterator.hasNext()) {
                // The listing and its ID
                SOAPElement soapElementupdate = (SOAPElement) soapBodyIterator.next();
                String status = soapElementupdate.getAttribute("TYPE");
                java.util.Iterator i = soapElementupdate.getChildElements();
                while (i.hasNext()) {

                    SOAPElement e = (SOAPElement) i.next();
                    String soapName = e.getLocalName();
                    String soapValue = e.getValue();
                    if (soapName == "TRIPNUMBER") {
                        soapResponse.setTripNo(soapValue);
                    }
                    logger.info("++++++" + soapName + "=" + soapValue);

                    if (soapName == "RETURN") {
                        SOAPElement soapElement1 = (SOAPElement) e.getChildElements().next();
                        java.util.Iterator element2 = soapElement1.getChildElements();
                        while (element2.hasNext()) {
                            SOAPElement e2 = (SOAPElement) element2.next();
                            String e2LocalName = e2.getLocalName();
                            String e2Value = e2.getValue();
                            switch (e2LocalName) {
                                case "TYPE":
                                    soapResponse.setStatus(e2Value);
                                    logger.info("status nya=========" + e2Value);
                                    break;
                                case "MESSAGE":
                                    soapResponse.setErrorMessage(e2Value);
                                    break;
                            }
                            logger.info(e2.getLocalName() + "=" + e2.getValue());
                        }
                    }
                }
            }

            //ByteArrayOutputStream outXml = new ByteArrayOutputStream();
            //soapResponse.getSoapResponse().writeTo(outXml);
        } catch (SOAPException e) {
            logger.info("SAPException Read SOAP Response error: " + e.toString());
        } catch (ClassCastException e) {
            logger.info("ClassCastException Read SOAP Response error: " + e.toString());
        }

        return soapResponse;
    }
}
