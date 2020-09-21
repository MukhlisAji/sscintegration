package com.mii.sscintegration.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.FileMetadata;
import com.mii.sscintegration.domain.RemedyAttachment;
import com.mii.sscintegration.filenet.FilenetConnection;



public class FilenetController {
	Logger logger = LoggerFactory.getLogger(FilenetController.class);
	
	public void sendAttachmenttoFilenet(FileMetadata fileMetadata,
			RemedyAPI remedyAPI,
			ARServerUser remedySession,
			ConfigurationValue configValue) {
		//get attachment 
		ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
		arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedySession, 
				"SRM:WorkInfo", 
				"'SRInstanceId' LIKE \""+fileMetadata.getTicketKey()+"\"");
		/*arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedySession, 
				"SRM:WorkInfo", 
				"'SRInstanceId' LIKE \""+fileMetadata.getTicketKey()+"\"");*/
		/*arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedySession, 
				"WOI:WorkInfo", 
				"'Work Order ID'=\""+fileMetadata.getTicketKey()+"\"");*/
		
		FilenetConnection filenetConnection = new FilenetConnection();
		int urutan = 0;
		
		for(RemedyAttachment remedyAttachment : arrayofAttachment) {
			//untuk naming convension berdasarkan urutan file (example : 001, 002, 003 dst)
			urutan++;
			String urutanFilename = "";
			for(int lengthChar = String.valueOf(urutan).length(); lengthChar<3; lengthChar++) {
				urutanFilename += "0";
			}
			urutanFilename += urutan;
			remedyAttachment.setUrutan(urutanFilename);
			
			filenetConnection.sendAttachmenttoFilenet(fileMetadata, remedyAttachment, configValue);
		}
		
		
	}
	
	
	
	public void getRemedyTicket() {
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		
		//get remedy connection
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
		RemedyAPI remedyAPI = new RemedyAPI();
		logger.info("{{{{{{{{{{{{{{{{{{{{{ Sending to Filenet }}}}}}}}}}}}}}}}}}}}}");
		//temporary variable
		Integer statusEntryId=0;
		FileMetadata fileMetadata = new FileMetadata();
		String bmcTicketNumber, bmcModul="";
		
		
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, configValue.getFilenetMiddleForm(), "'Status__c'=\"0\"");
		
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedySession.getEntry(configValue.getFilenetMiddleForm(),eListInfo.getEntryID(), null);  
				for (Integer i : record.keySet()) { 
	            		Field field = remedySession.getField(configValue.getFilenetMiddleForm(), i);
	            		Value val = record.get(i);
	            		if(val.getValue()!=null) {
	            			switch (field.getName()) {
			            		case "Doc Class__c":
			            			fileMetadata.setDocCLass(val.getValue().toString());
			            			break;
			            		case "Folder__c":
			            			fileMetadata.setFolder(val.getValue().toString());
			            			break;
			            		case "Folder Class__c":
			            			fileMetadata.setFlfolderClass(val.getValue().toString());
			            			break;
			            		case "Object Store__c":
			            			fileMetadata.setObjectStore(val.getValue().toString());
			            			break;
			            		case "Modul__c":
			            			bmcModul = val.getValue().toString();
			            			fileMetadata.setModul(bmcModul);
			            			break;
			            		case "BMC Ticket Number__c":
			            			bmcTicketNumber = val.getValue().toString();
			            			fileMetadata.setBmcTicketNumber(bmcTicketNumber);
			            			break;
			            		case "Employee Number__c":
			            			fileMetadata.setEmployeeNumber(val.getValue().toString());
			            			break;
			            		case "Trip Number__c":
			            			fileMetadata.setTripNumber(val.getValue().toString());
			            			break;
			            		case "Start Date__c":
			            			fileMetadata.setStartDate(val.getValue().toString());
			            			break;
			            		case "Cost Center__c":
			            			fileMetadata.setCostCenter(val.getValue().toString());
			            			break;
			            		case "Company Code__c":
			            			fileMetadata.setCompanyCode(val.getValue().toString());
			            			break;
			            		case "Personal Area Sub Area__c":
			            			fileMetadata.setPersonalArea(val.getValue().toString());
			            			break;
			            		case "Document Source__c":
			            			fileMetadata.setDocumentSource(val.getValue().toString());
			            			break;
			            		case "Vendor Number__c":
			            			fileMetadata.setVendorNumber(val.getValue().toString());
			            			break;
			            		case "SP3 Number__c":
			            			fileMetadata.setSp3Number(val.getValue().toString());
			            			break;
			            		case "Invoice Number__c":
			            			fileMetadata.setInvoiceNumber(val.getValue().toString());
			            			break;
			            		case "Short Description__c":
			            			fileMetadata.setTicketKey(val.getValue().toString());
			            			break;
			            		case "Status__c":
			            			statusEntryId = i;
			            			break;
		            		}
		            		logger.info(field.getName()+":"+val.getValue());
	            		}
	            		
				}
				
				sendAttachmenttoFilenet(fileMetadata, remedyAPI, remedySession, configValue);
				
				record.put(statusEntryId, new Value("2"));
				//Updating SRT middle form based on response from SAP
				remedySession.setEntry("PTM:SSC:FIN:INT:FILENET", record.getEntryId(), record, null, 0);
			}
		}catch (ARException e) { 
			logger.info("ARException Error on sendWOtoOpentext: "+e.toString());
		}
		
		//return fileMetadata;
		//Closing ApplicationContext to avoid memory leak
		((AbstractApplicationContext) context).close();
	}
}
