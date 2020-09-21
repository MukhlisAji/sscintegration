package com.mii.sscintegration.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

@Controller
public class TravelController {

    protected static Logger logger = Logger.getLogger("SSCController: ");

    @RequestMapping(value = "/sendSAPv2")
    public String sendSAPv2() {
        logger.info("---------------------- masuk");
        ArrayList<String> reqList = new ArrayList<String>();
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        List<EntryListInfo> eListSAPs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:INT:SAP", "'Status__c'=\"0\" ");
        Entry sapRecord;
        int loopNumber = 0;
        String nomerReqID;

        //Setting SAP Metadata
        SAPIntegrationMetadata sapIntegrationMetadata = new SAPIntegrationMetadata();
        SAPController sapController = new SAPController();

        try {
            for (EntryListInfo eListSAP : eListSAPs) {
                sapRecord = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListSAP.getEntryID(), null);
                nomerReqID = getValueFromRemedy(sapRecord, 1);
                reqList.add(nomerReqID);
                logger.info(reqList.size() + "=" + nomerReqID);
                sapRecord.put(7, new Value("2"));
                remedySession.setEntry("PTM:SSC:HR:Travel:INT:SAP", sapRecord.getEntryId(), sapRecord, null, 0);
            }

            for (EntryListInfo eListSAP : eListSAPs) {
                sapRecord = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListSAP.getEntryID(), null);
                nomerReqID = getValueFromRemedy(sapRecord, 1);
                logger.info("testing" + nomerReqID);

                Entry record = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListSAP.getEntryID(), null);
                String reqNumber = record.get(536870928).getValue().toString();
                String fcode = record.get(536870916).getValue().toString();
                logger.info("nomer req=" + reqNumber);
                sapIntegrationMetadata.setReqNumber(reqNumber);
                sapIntegrationMetadata.setCcode(getValueFromRemedy(record, 536870914));
                sapIntegrationMetadata.setEmpNumber(getValueFromRemedy(record, 536870915));
                sapIntegrationMetadata.setFcode(fcode);
                sapIntegrationMetadata.setApproved(getValueFromRemedy(record, 536870917));
                sapIntegrationMetadata.setDepDate(getValueFromRemedy(record, 536870919));
                sapIntegrationMetadata.setDepTime(getValueFromRemedy(record, 536870920));
                sapIntegrationMetadata.setArrDate(getValueFromRemedy(record, 536870921));
                sapIntegrationMetadata.setArrTime(getValueFromRemedy(record, 536870922));
                sapIntegrationMetadata.setCustomer(getValueFromRemedy(record, 536870923));
                sapIntegrationMetadata.setLocation(getValueFromRemedy(record, 536870924));
                sapIntegrationMetadata.setCountry(getValueFromRemedy(record, 536870925));
                sapIntegrationMetadata.settSchema(getValueFromRemedy(record, 536870926));
                sapIntegrationMetadata.setTtComsp(getValueFromRemedy(record, 536870929));
                sapIntegrationMetadata.settActype(getValueFromRemedy(record, 536870930));
//                    String isMitra = ;

                switch (fcode) {
                    case "DEL":
                        sapIntegrationMetadata.setTripNo(getValueFromRemedy(record, 536870927));
                        break;
                    case "MOD":
                        sapIntegrationMetadata.setTripNo(getValueFromRemedy(record, 536870927));
                        break;
                }

                //if(record.get(536870927)!=null)
                //	sapIntegrationMetadata.setTripNo(record.get(536870927).getValue().toString());
                //sapIntegrationMetadata.setReqNumber(record.get(536870928).getValue().toString());
                //sapStatus, 536870931
                //List<SAPIntegrationReceiptsMetadata> listSapReceipts = getReceipts(reqNumber, remedyAPI, remedySession);
                List<SAPIntegrationReceiptsMetadata> listSapReceipts = sapController.getNewReceipts(reqNumber, remedyAPI, remedySession);
                List<SAPIntegrationCostMetadata> listSAPCost = sapController.getCostCenter(reqNumber, remedyAPI, remedySession);
                List<SAPIntegrationTransitMetadata> listSapTransit = sapController.getTransit(reqNumber, remedyAPI, remedySession);

                SAPSOAPGenerator sapSoapGenerator = new SAPSOAPGenerator();
                SoapResponse soapResponse = sapController.readSAPResponse(sapSoapGenerator.createSOAPMessage(
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

                            String vendorCBT = getValueFromRemedy(record, 536871185);
                            if (vendorCBT.equals("Mitra")) {
                                corporateReferenceID = "M" + corporateReferenceID;
                                record.put(8, new Value(corporateReferenceID));
                            } else if (vendorCBT.equals("Traveloka")) {
                                record.put(8, new Value(corporateReferenceID));
                            } else if (vendorCBT.equals("Tidak")) {
                                record.put(8, new Value(corporateReferenceID));
                            } else {
                                corporateReferenceID = "M" + corporateReferenceID;
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
            //while(loopNumber<reqList.size()) {

            //}
            logger.info("jumlah array=" + reqList.size());
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        } catch (IOException e) {
            logger.info("IOException Error on sendToSAP: " + e.toString());
        } catch (SOAPException e) {
            logger.info("SOAPException Error on sendToSAP: " + e.toString());
        }

        logger.info("---------------------- selesai");

        return "testing";
    }

    public String getValueFromRemedy(Entry assetRecord, Object fieldID) {
        if (assetRecord.get(fieldID) == null) {
            return "-";
        }

        return assetRecord.get(fieldID).getValue().toString();
    }
}
