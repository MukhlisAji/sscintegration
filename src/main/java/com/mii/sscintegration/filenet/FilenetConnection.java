package com.mii.sscintegration.filenet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.Subject;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.Properties;
import com.filenet.api.util.UserContext;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.FileMetadata;
import com.mii.sscintegration.domain.RemedyAttachment;

public class FilenetConnection {
	
	Logger logger = LoggerFactory.getLogger(FilenetConnection.class);
	//public void sendAttachmenttoFilenet(FileMetadata fileMetadata, ArrayList<RemedyAttachment> arrayofAttachment) {
	public void sendAttachmenttoFilenet(FileMetadata fileMetadata, RemedyAttachment remedyAttachment, ConfigurationValue configValue) {
		//setting connection
		String filenetUrl = configValue.getFilenetUrl();
		String filenetUser = configValue.getFilenetUser();
		String filenetPassword = configValue.getFilenetPassword();
		String notes="";
		
		
		Connection conn = Factory.Connection.getConnection(filenetUrl);
		Subject subject = UserContext.createSubject(conn, filenetUser, filenetPassword, null);
		UserContext.get().pushSubject(subject);
		try {
			//get default domain
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			
			//ObjectStore setting
			ObjectStore ptmObjectStore = Factory.ObjectStore.fetchInstance(domain, fileMetadata.getObjectStore(), null);
			
			//check folder
			Folder ptmFolder = null;
			try {
				ptmFolder = Factory.Folder.fetchInstance(ptmObjectStore,fileMetadata.getFolder(), null);
				logger.info("folder "+ fileMetadata.getFolder() +" is available");
			}catch(Exception e) {
				logger.info("folder "+ fileMetadata.getFolder() +" is not available");
				
				//ptmFolder = createFolder("201804", "/Finance/P2P/1010/Invoice/NonPO", ptmObjectStore, "PTM_NonPOFolder");
		   		//returnValue += "<br> FolderName:"+ptmFolder.get_FolderName()+" created ";
			}
			
			Document docPTM = Factory.Document.createInstance(ptmObjectStore, fileMetadata.getDocCLass());
			
			//send file to filenet
			InputStream fileUpload = new ByteArrayInputStream(remedyAttachment.getAttachedFile());
			//InputStream fileUpload = new ByteArrayInputStream("test txt from BMC Remedy".getBytes());
			ContentTransfer contentTransfer = Factory.ContentTransfer.createInstance();
			ContentElementList contentElementList = Factory.ContentElement.createList();
			contentTransfer.setCaptureSource(fileUpload);
			//file extension
			String fileExtension = FilenameUtils.getExtension(remedyAttachment.getFilename());
			
	        
	        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss a");
	    		Date date = new Date();
	        //document metadata
	        Properties metadataDocument = docPTM.getProperties();
	        String filenameOCR = "";
	        logger.info("sending filenumber:"+remedyAttachment.getUrutan()+"="+remedyAttachment.getFilename());
	        
	        metadataDocument.putValue("BMCTicketNumber", fileMetadata.getBmcTicketNumber());
	        switch (fileMetadata.getModul()) {
	        		case "O2C Manual Billing":
	        			filenameOCR = fileMetadata.getBmcTicketNumber()+"_"+remedyAttachment.getUrutan();
	        			break;
	        		case "P2P Travel Expense":
	        			filenameOCR = fileMetadata.getCompanyCode()+"_"+fileMetadata.getEmployeeNumber()+"_"+fileMetadata.getTripNumber()+"_"+remedyAttachment.getUrutan();
	        			metadataDocument.putValue("EmployeeNumber", fileMetadata.getEmployeeNumber());
	        			metadataDocument.putValue("TripNumber", fileMetadata.getTripNumber());
	        			metadataDocument.putValue("StartDate", date);
	        			metadataDocument.putValue("CostCenter", fileMetadata.getCostCenter());
	        			metadataDocument.putValue("CompanyCode", fileMetadata.getCompanyCode());
	        			metadataDocument.putValue("PersonelArea_SubArea", fileMetadata.getPersonalArea());
	        			metadataDocument.putValue("DocumentSource", fileMetadata.getDocumentSource());
	        			break;
	        		case "P2P Invoice":
	        			filenameOCR = fileMetadata.getCompanyCode()+"_"+fileMetadata.getSp3Number()+"_"+fileMetadata.getBmcTicketNumber()+"_"+remedyAttachment.getUrutan();
	        			metadataDocument.putValue("VendorNumber", fileMetadata.getVendorNumber());
	        			metadataDocument.putValue("SP3Number", fileMetadata.getSp3Number());
	        			metadataDocument.putValue("InvoiceNumber", fileMetadata.getInvoiceNumber());
	        			metadataDocument.putValue("InvoiceDate", date);
	        			metadataDocument.putValue("CompanyCode", fileMetadata.getCompanyCode());
	        			break;
	        }
	        metadataDocument.putValue("DocumentTitle", filenameOCR);
	        contentTransfer.set_RetrievalName(filenameOCR+"."+fileExtension); 
			contentElementList.add(contentTransfer);
	        docPTM.set_ContentElements(contentElementList);                       
	        docPTM.set_MimeType(fileMetadata.getBmcTicketNumber()+"_"+remedyAttachment.getUrutan()+"."+fileExtension);
	        
	        docPTM.checkin(null, CheckinType.MAJOR_VERSION);
	        docPTM.save(RefreshMode.REFRESH);
	        
	        ReferentialContainmentRelationship rc = ptmFolder.file(docPTM,
	                AutoUniqueName.AUTO_UNIQUE,
	                filenameOCR+"."+fileExtension,
	                DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
	        rc.save(RefreshMode.REFRESH);
	        
	        logger.info("Success upload for :"+remedyAttachment.getUrutan()+"="+remedyAttachment.getFilename());
		        
			
			
		}finally {
			UserContext.get().popSubject();
		}
	}
}
