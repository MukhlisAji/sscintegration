package com.mii.sscintegration.bmc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.AttachmentField;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.Value;
import com.mii.sscintegration.domain.RemedyAttachment;



/**
 * @author MII-ferry.hendrayana
 * @version 1.0
 * @since 2018-04-29
 * 
 * The class is used to connect BMC Remedy JAVA API
 *
 */

public class RemedyAPI {
	Logger logger = LoggerFactory.getLogger(RemedyAPI.class);
	/**
	 * This method is used to get attachment filename and content filter by schemaName and query
	 * 
	 * @param remedyConnection
	 * @param schemaName
	 * @param queryString
	 * @return Array of List RemedyAttachment
	 * @throws ARException
	 */
	public ArrayList<RemedyAttachment> getRemedyAttachmentbySchemaQuery(ARServerUser remedyConnection,
			String schemaName, 
			String queryString){
		
		ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
		try {
			QualifierInfo qual = remedyConnection.parseQualification(schemaName, queryString);
			List<EntryListInfo> eListInfos = remedyConnection.getListEntry(schemaName, qual, 0, 0, null, null, false, null);
			
			for (EntryListInfo eListInfo : eListInfos) {
				Entry record = remedyConnection.getEntry(schemaName,eListInfo.getEntryID(), null);  
	            for (Integer i : record.keySet()) {
	            		Field field = remedyConnection.getField(schemaName, i);
	            		
	            		Value val = record.get(i); 
	            		if (field instanceof AttachmentField) {  
	            			AttachmentValue aVal = (AttachmentValue) val.getValue(); 
	            			//aVal.get
	            			if (aVal != null) {
	            				logger.info("name file attachment:"+aVal.getName());
	            				String aName;  
                            String[] aDetails = aVal.getValueFileName().split("\\.(?=[^\\.]+$)");  
                            if (aDetails.length == 2) {  
                                aName = aDetails[0] + "." + aDetails[1];  
                            } else {  
                                aName = aDetails[0];  
                            }  
                            int lastPos = aName.lastIndexOf('\\');  
                            String aNameShort = (lastPos < 0) ? aName : aName.substring(lastPos + 1);
  
                            byte[] attach = remedyConnection.getEntryBlob(schemaName, eListInfo.getEntryID(), i);
                            byte[] base64Attachment = Base64.encodeBase64(attach);
                            if(base64Attachment!=null)
                            		arrayofAttachment.add(new RemedyAttachment(aNameShort, new String(base64Attachment), attach));
                            else
                            	logger.info("file size is null");
	            			}
	            		}
	            }
			}
		}catch (ARException e) {
			logger.info("Error on getRemedyAttachmentbySchemaQuery: "+e.toString());
		}
		
		
		return arrayofAttachment;
	}
	
	
	/**
	 * This method is used to get BMC Remedy record by schemaName & query
	 * @param remedyConnection
	 * @param schemaName
	 * @param queryString
	 * @return List of records
	 * @throws ARException
	 */
	public List<EntryListInfo> getRemedyRecordByQuery(ARServerUser remedyConnection,
			String schemaName, 
			String queryString){
		
		try {
			logger.info("SchemaName:"+schemaName);
			QualifierInfo qual = remedyConnection.parseQualification(schemaName, queryString);
			List<EntryListInfo> eListInfos = remedyConnection.getListEntry(schemaName, qual, 0, 0, null, null, false, null);
			
			return eListInfos;
		}catch (ARException e) {
			logger.info("Error on getRemedyRecordByQuery: "+e.toString());
			return null;
		}
		
	}
	
	public void insertNewRecord(
			ARServerUser remedyConnection,
			String schemaName,
			Entry newLineEntry) {
		try {
			remedyConnection.createEntry(schemaName, newLineEntry);
		}catch (ARException e) {
			logger.info("Error on insertNewRecord: "+e.toString());
		}
		
	}
}
