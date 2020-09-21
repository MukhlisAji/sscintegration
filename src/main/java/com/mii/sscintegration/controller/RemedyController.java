package com.mii.sscintegration.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.itextpdf.text.Document;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.PJSDomain;
import com.mii.sscintegration.domain.RemedyAttachment;
import com.mii.sscintegration.domain.RemedyEmailMetadata;
import com.mii.sscintegration.domain.TravelAdvanceMetadata;
import com.mii.sscintegration.domain.TravelSettlementMetadata;
import com.mii.sscintegration.domain.TravelokaPDF;
import com.mii.sscintegration.poi.BMCDocument;

public class RemedyController {
	protected static Logger logger = Logger.getLogger("RemedyController: ");
	private String instanceId;
	
	public void generatePrintTravelSettlement() {
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//get remedy connection
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
		RemedyAPI remedyAPI = new RemedyAPI();
		BMCDocument bmcDocument = new BMCDocument();
		
		logger.info("[[[[[[[[[[[[[[[[[[[[[[ Print Travel Settlement ]]]]]]]]]]]]]]]]]]]]]]");
		
		//temporary variable
		Integer statusEntryId=0;
		TravelSettlementMetadata travelSettlementMetadata = new TravelSettlementMetadata();
		
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, configValue.getRemedyTravelSettlement(), "'Status__c'=\"0\"");
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedySession.getEntry(configValue.getRemedyTravelSettlement(),eListInfo.getEntryID(), null); 
				Value shortDesc = record.get(8);
				
				for (Integer i : record.keySet()) {
					Field field = remedySession.getField(configValue.getRemedyTravelSettlement(), i);
            			Value val = record.get(i);
            			if(val.getValue()!=null) {
            				switch (field.getName()) {
            					case "Nomer Tiket__c":
            						travelSettlementMetadata.setNomerTiket(val.getValue().toString());
		            				break;
            					case "Tanggal__c":
            						travelSettlementMetadata.setTanggal(val.getValue().toString());
            						break;
            					case "Nomer Pekerja__c":
            						travelSettlementMetadata.setNomerPekerja(val.getValue().toString());
            						break;
            					case "Nama Pekerja__c":
            						travelSettlementMetadata.setNamaPekerja(val.getValue().toString());
            						break;
            					case "No Trip__c":
            						travelSettlementMetadata.setNoTrip(val.getValue().toString());
            						break;
            					case "Company Code__c":
            						travelSettlementMetadata.setCompanyCode(val.getValue().toString());
            						break;
            					case "Cost Center__c":
            						travelSettlementMetadata.setCostCenter(val.getValue().toString());
            						break;
            					case "Local Scanning Team__c":
            						travelSettlementMetadata.setLocalScanningTeam(val.getValue().toString());
            						break;
            					case "Status__c":
            						statusEntryId = i;
            						break;
            					case "Email From__c":
            						travelSettlementMetadata.setEmailFrom(val.getValue().toString());
            						break;
            					case "Email To__c":
            						travelSettlementMetadata.setEmailTo(val.getValue().toString());
            						break;
            				}
            				//logger.info(field.getName()+":"+val.getValue());
            			}
				}
				
				DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date dateNow = new Date();
				
				travelSettlementMetadata.setFooterEmail(record.get(536870928).getValue().toString().replace("[datenow]", formatDate.format(dateNow)));
				travelSettlementMetadata.setFooterEmail(travelSettlementMetadata.getFooterEmail().replace("[enter]", "\n"));
				travelSettlementMetadata.setNamaFile("TravelSettlement_"+travelSettlementMetadata.getNomerTiket()+".pdf");
				//travelSettlementMetadata.setNamaFile(shortDesc.toString());
				bmcDocument.generateTravelExpense(travelSettlementMetadata);
				
				//setting email content
				RemedyEmailMetadata remedyEmailContent = new RemedyEmailMetadata();
				String emailBody = record.get(536870924).getValue().toString();
				emailBody = emailBody.replace("[enter]", "\n");
				/*String emailBody = "Dengan hormat,\n" + 
						"\n" + 
						"Terlampir Form Pertanggungjawaban Travel Expense dengan Request Nomor "+travelSettlementMetadata.getNomerTiket()+
						"\n\n" + 
						"1. Form dicetak dan digunakan sebagai halaman depan (cover page) dokumen hardcopy pertanggungjawaban perjalanan dinas yang dikirimkan ke Local Scanning \n\n" + 
						"2. Dokumen hardcopy yang dikirimkan harus sesuai dengan yang dilampirkan pada request di aplikasi BMC"+
						"\n\n" + 
						"3. Dokumen invoice/receipt/bukti pengeluaran selama pelaksanaan dinas harus dalam kertas format A4 (ditempelkan pada kertas A4 jika diperlukan)."+
						"\n\n" +
						"Demikian disampaikan, agar dapat digunakan sebagaimana mestinya.\n\n"+
						"Terima kasih\n\n"+
						"SSC Finance";*/
				remedyEmailContent.setEmailBody(emailBody);
				//remedyEmailContent.setEmailBody(record.get(536870924).getValue().toString());
				remedyEmailContent.setEmailFrom(travelSettlementMetadata.getEmailFrom());
				remedyEmailContent.setEmailTo(travelSettlementMetadata.getEmailTo());
				remedyEmailContent.setNamaFileAttachment(travelSettlementMetadata.getNamaFile());
				//remedyEmailContent.setSubjectEmail("Form Pertanggungjawaban Travel Expense Request Nomor "+travelSettlementMetadata.getNomerTiket());
				remedyEmailContent.setSubjectEmail(record.get(536870925).getValue().toString());
				//remedyEmailContent.setSubjectEmail("Form Pertanggungjawaban Travel Expense Request Nomor "+travelSettlementMetadata.getNomerTiket());
				
				setRemedyEmail(remedySession, remedyEmailContent);
				
				//update status to Sent
				record.put(statusEntryId, new Value("1"));
				remedySession.setEntry(configValue.getRemedyTravelSettlement(), record.getEntryId(), record, null, 0);
			}
		}catch (ARException e) { 
			logger.info("ARException Error on generate Travel Expense Document: "+e.toString());
		}
	}
	
	
	
	public void generatePrintTravelAdvance() {
		//String travelAdvanceFormName = "PTM:SSC:FIN:P2P:TravelAdvanceDocument";
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class, ConfigurationValue.class);
		//ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//get remedy connection
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
		RemedyAPI remedyAPI = new RemedyAPI();
		BMCDocument bmcDocument = new BMCDocument();
		logger.info("[[[[[[[[[[[[[[[[[[[[[[ Print Travel Advance ]]]]]]]]]]]]]]]]]]]]]]");
		
		//temporary variable
		Integer statusEntryId=0;
		TravelAdvanceMetadata travelAdvanceMetadata = new TravelAdvanceMetadata();
		
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, configValue.getRemedyTravelAdvance(), "'Status__c'=\"0\"");
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedySession.getEntry(configValue.getRemedyTravelAdvance(),eListInfo.getEntryID(), null);  
				for (Integer i : record.keySet()) { 
	            		Field field = remedySession.getField(configValue.getRemedyTravelAdvance(), i);
	            		Value val = record.get(i);
	            		if(val.getValue()!=null) {
	            			switch (field.getName()) {
	            			case "Short Description__c":
	            				travelAdvanceMetadata.setNamaPekerja(val.getValue().toString());
	            				break;
		            		case "Nomer Pekerja__c":
		            			travelAdvanceMetadata.setNomerPekerja(val.getValue().toString());
		            			break;
		            		case "No Trip__c":
		            			travelAdvanceMetadata.setNomerTrip(val.getValue().toString());
		            			break;
		            		case "No Tiket Request__c":
		            			travelAdvanceMetadata.setNomerTiket(val.getValue().toString());
		            			break;
		            		case "Tujuan__c":
		            			travelAdvanceMetadata.setTujuan(val.getValue().toString());
		            			break;
		            		case "Bank Pengambilan__c":
		            			travelAdvanceMetadata.setBankPengambilan(val.getValue().toString());
		            			break;
		            		case "Jumlah (dalam USD)__c":
		            			travelAdvanceMetadata.setJumlah(val.getValue().toString());
		            			break;
		            		case "Masa Berlaku Form__c":
		            			travelAdvanceMetadata.setMasaBerlaku(val.getValue().toString());
		            			break;
		            		case "Email to__c":
		            			travelAdvanceMetadata.setEmailTo(val.getValue().toString());
		            			break;
		            		case "Email From__c":
		            			travelAdvanceMetadata.setEmailFrom(val.getValue().toString());
		            			break;
		            		case "Status__c":
		            			statusEntryId = i;
		            			break;
		            		}
		            		//logger.info(field.getName()+":"+val.getValue());
	            		}
	            		
				}
				
				DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date dateNow = new Date();
				travelAdvanceMetadata.setNamaFile(record.get(536870924).getValue().toString() + " "+travelAdvanceMetadata.getNomerTrip()+".pdf");
				travelAdvanceMetadata.setSubjectEmail(record.get(536870924).getValue().toString());
				travelAdvanceMetadata.setBodyEmail(record.get(536870925).getValue().toString().replace("[enter]", "\n"));
				travelAdvanceMetadata.setNotesEmail(record.get(536870926).getValue().toString().replace("[enter]", "\n"));
				travelAdvanceMetadata.setFooterEmail(record.get(536870927).getValue().toString().replace("[datenow]", formatDate.format(dateNow)));
				travelAdvanceMetadata.setFooterEmail(travelAdvanceMetadata.getFooterEmail().replace("[enter]", "\n"));
				//travelAdvanceMetadata.setNamaFile(record.get(8).toString());
				//bmcDocument.generateTravelAdvance(travelAdvanceMetadata);
				bmcDocument.generateTravelAdvanceNew(travelAdvanceMetadata);
				
				
				//setting email content
				RemedyEmailMetadata remedyEmailContent = new RemedyEmailMetadata();
				/*String emailBody = "Dengan hormat,\n" + 
						"\n" + 
						"Terlampir Form Pengambilan Panjar Dinas dengan Request Nomor "+travelAdvanceMetadata.getNomerTiket()+
						"\n\n" + 
						"1. Form dicetak pada kertas berlogo perusahaan \n\n" + 
						"2. Form digunakan untuk pengambilan panjar dinas dalam mata uang US Dollar pada bank yang sudah dipilih"+
						"\n\n" + 
						"3. Pada saat pengambilan ke bank, membawa persyaratan yang sudah disebutkan dalam form."+
						"\n\n" +
						"Demikian disampaikan, agar dapat digunakan sebagaimana mestinya.\n\n"+
						"Terima kasih\n\n"+
						"SSC Finance";*/
				
				//remedyEmailContent.setSubjectEmail("Form Pengambilan Panjar Dinas Request Nomor "+travelAdvanceMetadata.getNomerTiket());
				remedyEmailContent.setSubjectEmail(travelAdvanceMetadata.getSubjectEmail()+" Request Nomor "+ travelAdvanceMetadata.getNomerTiket());
				remedyEmailContent.setEmailBody(travelAdvanceMetadata.getBodyEmail().replace("[enter]", "\n"));
				remedyEmailContent.setEmailFrom(travelAdvanceMetadata.getEmailFrom());
				remedyEmailContent.setEmailTo(travelAdvanceMetadata.getEmailTo());
				remedyEmailContent.setNamaFileAttachment(travelAdvanceMetadata.getNamaFile());
				
				
				setRemedyEmail(remedySession, remedyEmailContent);
				
				//update status to Sent
				record.put(statusEntryId, new Value("1"));
				remedySession.setEntry(configValue.getRemedyTravelAdvance(), record.getEntryId(), record, null, 0);
			}
		}catch (ARException e) { 
			//logger.info("ARException Error on generate Travel Advance Document: "+e.toString());
		}
	}
	

	public void getPOPullData(ARServerUser remedyServer) {
		//logger.info("++++++++++++++++++++++++ searching get Pull Data +++++++++++++++++++++++++++++++++");
		//Get configuration value from sscconfig.properties
		//ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		//ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//SSC connection setting up
		//RemedyConnection remedyConnection = new RemedyConnection();
		//ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
		
		RemedyAPI remedyAPI = new RemedyAPI();
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, "PTM:SSC:FIN:SAP_PullData", "'Status'=\"0\"");
		Integer tempKeySet;
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedyServer.getEntry("PTM:SSC:FIN:SAP_PullData",eListInfo.getEntryID(), null);
				/*
				for (Integer i : record.keySet()) {
					Field field = remedyServer.getField("PTM:SSC:FIN:SAP_PullData", i);
					Value val = record.get(i);
					if(val.getValue()!=null) {
						if(field.getName()=="z1D Action") {
							tempKeySet = i;
						}
					}
					
				}*/
				
				logger.info("+++++++++++++++++++++ GET PO +++++++++++++++++++++ :"+record.get(536870921).getValue().toString());
				
				
				//tempKeySet = Integer(Integer.parseInt("536870973"));
				record.put(536870973, new Value("NEW_DATA"));
				remedyServer.setEntry("PTM:SSC:FIN:SAP_PullData", record.getEntryId(), record, null, 0);
			}
		}catch (ARException e) { 
			//logger.info("ARException Error on get PO: "+e.toString());
		}
	}
	
	
	public void setRemedyEmail(
			ARServerUser remedySession,
			RemedyEmailMetadata remedyEmailContent) {
		
		RemedyAPI remedyAPI = new RemedyAPI();
		Entry entry = new Entry();
		entry.put(18085, new Value(remedyEmailContent.getEmailTo())); //To
		entry.put(18086, new Value(remedyEmailContent.getEmailFrom())); //from
		entry.put(18087, new Value(remedyEmailContent.getEmailFrom()));//reply to
		entry.put(18090, new Value(remedyEmailContent.getSubjectEmail())); //subject
		//entry.put(18091, new Value(remedyEmailContent.getEmailBody())); //Plain Text Body
		entry.put(18290, new Value(remedyEmailContent.getEmailBody())); //HTML Body
		entry.put(18092, new Value("1")); //Message Type
		entry.put(18099, new Value("1")); //Send message 1 --> Yes
		
		
		Entry attachmentEntry = new Entry();
		AttachmentValue attachment = new AttachmentValue();
		attachment.setName(remedyEmailContent.getNamaFileAttachment());
		
        try {
			//PdfWriter writer = PdfWriter.getInstance(bmcDocument.generateTravelExpense(), baos);
			
        		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
        		//XmlStreamUtils
            //org.apache.xml.security.utils.XMLUtils.outputDOM(bmcDocument.generateTravelExpense(), baos, true);
            //return baos.toByteArray();
			
			attachment.setValue(remedyEmailContent.getNamaFileAttachment());
		} catch (IOException io) {
			logger.info("Couldn't find the file");
		}
		
        
		
		//attachment.setValue("testing".getBytes());
		attachmentEntry.put(18004, new Value(attachment));
		attachmentEntry.put(18005, new Value(0));
		attachmentEntry.put(18133, new Value(remedyEmailContent.getNamaFileAttachment()));
		
		
		Entry emailAssociationEntry = new Entry();
		emailAssociationEntry.put(18001, new Value(0));//Source Type --> 0 = Email
		emailAssociationEntry.put(18002, new Value(0));//Destination Type --> 0 = Attachment
		
		//String formName = "AR System Email Messages";
		//String filterForm = "'To'=\"ferry.hendrayana@mii.co.id\"";
		String formName = "AR System Email Attachments";
		//String filterForm = "1=1";
		
		//List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "AR System Email Association", filterForm);
		
		
		try {
			String resultEntryEmail = remedySession.createEntry("AR System Email Messages", entry);
			
			entry = remedySession.getEntry("AR System Email Messages", resultEntryEmail, null);
			//logger.info("Entry result for email:"+resultEntryEmail+",uid="+entry.get(179));
			
			String resultEntry = remedySession.createEntry(formName, attachmentEntry);
			attachmentEntry = remedySession.getEntry(formName, resultEntry, null);
			//logger.info("Entry result:"+resultEntry+",uid="+attachmentEntry.get(179));
			
			
			emailAssociationEntry.put(18000, attachmentEntry.get(179));//Destination ID --> attachment
			emailAssociationEntry.put(18134, entry.get(179));//Source ID2--> email
			String resultAssociationEntry = remedySession.createEntry("AR System Email Association", emailAssociationEntry);
			//logger.info("Success to create email association with entryID="+resultAssociationEntry);
		}catch (ARException e) { 
			logger.info("ARException Error on get email messages: "+e.toString());
		}
	}
	
	public void sendPdftoWorkInfo(TravelokaPDF travelokaPdf) {
		//logger.info("++++++++++++++++++++++++ searching get Pull Data +++++++++++++++++++++++++++++++++");
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//SRT connection setting up
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
		
		
		String workInfoFormName = "SRM:WorkInfo";
		Entry workInfoEntry = new Entry();
		
		AttachmentValue attachment = new AttachmentValue();
		attachment.setName(travelokaPdf.namafile);
		
        try {
			attachment.setValue(travelokaPdf.namafile);
		} catch (IOException io) {
			logger.info("Couldn't find the file");
		}
		
		//attachment1 10001831
        workInfoEntry.put(10001831, new Value(attachment));
		workInfoEntry.put(10001821, new Value(travelokaPdf.srInstanceId));//SRInstanceID 
		workInfoEntry.put(1000000829, new Value(travelokaPdf.requstNumber));//Request Number 
		workInfoEntry.put(303449900, new Value("General Information"));//WorkInfoTypeSelection 
		workInfoEntry.put(10001952, new Value("Public"));//View Access  
		//Status
		workInfoEntry.put(10006800 , new Value(travelokaPdf.namafile));//Summary 
		workInfoEntry.put(10000101, new Value(travelokaPdf.namafile)); //Notes 
		//SR_RequestNumber 
		//SRID
		workInfoEntry.put(10001953, new Value("Yes"));//Secure Log 
		workInfoEntry.put(10001950, new Value("General Information"));//WorkInfoType 
		//WorkInfoSubmitDate
		
		try {
			String resultAttachment = remedySession.createEntry(workInfoFormName, workInfoEntry);
		} catch (ARException e) {
			// TODO Auto-generated catch block
			logger.info("entry failed"+e.toString());
		}
		logger.info("OK");
	}
	
	public void readAttachmentFromREQ() {
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//SRT connection setting up
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
		String woID = "";
		
		RemedyAPI remedyAPI = new RemedyAPI();
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, "WOI:WorkOrder", "'SRID'=\"REQ000000002407\"");
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedyServer.getEntry("WOI:WorkOrder",eListInfo.getEntryID(), null);  
				for (Integer i : record.keySet()) { 
	            		Field field = remedyServer.getField("WOI:WorkOrder", i);
	            		Value val = record.get(i);
	            		switch (field.getName()) {
		            		case "SRInstanceID": //"InstanceId":
		            			instanceId = val.getValue().toString();
		            			//logger.info(field.getName()+":"+val.getValue());
		            			break;
		            		case "Work Order ID":
		            			woID = val.getValue().toString();
		            			//logger.info(field.getName()+":"+val.getValue());
		            			break;
	            		}
	            		//logger.info(field.getName()+":"+val.getValue());
				}
			}
			
			//get remedy attachment
			//logger.info("++++++ getting attachment from SRM:WorkInfo REQ+++++++++++");
			ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
			arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer, 
					"SRM:WorkInfo", 
					"'SRInstanceId' LIKE \""+instanceId+"\"");
			
			//logger.info("++++++ getting attachment from WOI:WorkInfo REQ+++++++++++");
			arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer, 
					"WOI:WorkInfo", 
					"'Work Order ID'=\""+woID+"\"");
			
			
		}catch (ARException e) { 
			//logger.info("ARException Error on sendWOtoOpentext: "+e.toString());
		}
		
	}
	
	
	public void readWorkOrderInfo() {
		//Get configuration value from sscconfig.properties
		ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//SRT connection setting up
		RemedyConnection remedyConnection = new RemedyConnection();
		ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
		
		RemedyAPI remedyAPI = new RemedyAPI();
		List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, "WOI:WorkOrder", "'Work Order ID'=\"WO0000000003001\"");
		
		try {
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedyServer.getEntry("WOI:WorkOrder",eListInfo.getEntryID(), null);  
				for (Integer i : record.keySet()) { 
	            		Field field = remedyServer.getField("WOI:WorkOrder", i);
	            		Value val = record.get(i);
	            		switch (field.getName()) {
		            		case "SRInstanceID": //"InstanceId":
		            			instanceId = val.getValue().toString();
		            			break;
	            		}
	            		//logger.info(field.getName()+":"+val.getValue());
				}
			}
			
			//get remedy attachment
			//logger.info("++++++ getting attachment from SRM:WorkInfo +++++++++++");
			ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
			arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer, 
					"SRM:WorkInfo", 
					"'SRInstanceId' LIKE \""+instanceId+"\"");
			
			//logger.info("++++++ getting attachment from WOI:WorkInfo +++++++++++");
			arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer, 
					"WOI:WorkInfo", 
					"'Work Order ID'=\"WO0000000003001\"");
			
			
		}catch (ARException e) { 
			//logger.info("ARException Error on sendWOtoOpentext: "+e.toString());
		}
	}
	
	
	public void insertPJSRecord(
			ARServerUser remedySession,
			PJSDomain pjsDomain) {
		
		String pjsFormName = "PTM:SSC:HR:PJS";
		Entry pjsEntry = new Entry();
		
		pjsEntry.put(536870914, new Value(pjsDomain.getPositionId()));
		pjsEntry.put(536870915, new Value(pjsDomain.getPjsNik()));
		
		try {
			String resultInsertPjs = remedySession.createEntry(pjsFormName, pjsEntry);
		} catch (ARException e) {
			// TODO Auto-generated catch block
			logger.info("entry failed"+e.toString());
		}
	}
}
