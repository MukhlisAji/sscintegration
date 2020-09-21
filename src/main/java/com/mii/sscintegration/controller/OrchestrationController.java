/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mii.sscintegration.controller;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.mii.sscintegration.ao.AtriumOrchestration;
import com.mii.sscintegration.ao.SecurityGroup;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author DewiYu
 */

@Controller
public class OrchestrationController {
    
    protected static Logger logger = Logger.getLogger("OrchestrationController: ");

    
    public void addEmail(
            String Email1,
            String REQNumber,
            String srintanceid,
            String emailgroup,
            String membergroup,
            String wonumber,
            String typefield40,
            ARServerUser remedySession) {

        RemedyAPI remedyAPI = new RemedyAPI();
        Entry AOEmail = new Entry();
        AOEmail.put(536870928,new Value(REQNumber));
        AOEmail.put(536870919,new Value(emailgroup));
        AOEmail.put(536870942, new Value(Email1));
        AOEmail.put(536870943, new Value(srintanceid));
        AOEmail.put(536870941, new Value(membergroup));
        AOEmail.put(536870916, new Value(wonumber));
        AOEmail.put(536870930, new Value(typefield40));
        
        String formTravelokaNotification = "PTM:SSC:IT:AO:INT";

        logger.info("input in bmc:" + REQNumber);

        remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, AOEmail);
    }
    
    
    
    public String getValueFromRemedy(Entry emailRecord, Object fieldID) {
		if(emailRecord.get(fieldID).getValue()==null)
			return "-";
		
		return emailRecord.get(fieldID).getValue().toString();
    }
    
    @RequestMapping(value = "/ManipulasiString")
    public String SplitEmail() {
        //remedy server
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        System.out.print("Remedy Server:" + configValue.getRemedyServer());

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        System.out.println(remedySession);

        // berfungsi untuk query kualifikasi
        List<AtriumOrchestration> listWorkInfo = new ArrayList<AtriumOrchestration>();
        RemedyAPI remedyAPI = new RemedyAPI();

        //Inisialisasi nama Form
        String MDF = "PTM:SSC:IT:AO:INT";

        List<EntryListInfo> list = remedyAPI.getRemedyRecordByQuery(remedySession, MDF, " ('Email' != \"0\" AND 'SR TYPE FIELD 40' = \"Create Email Group\" AND 'Short Description' != \"Sudah di Split Email\") ");
//        List<EntryListInfo> list = remedyAPI.getRemedyRecordByQuery(remedySession, MDF, " 'REQNUMBER' = \"REQ000000039584\" ");
        System.out.println("total data: " + list.size());

        try {
            int loopingNumber = 0;
            for (EntryListInfo getEntryForm : list) {
                loopingNumber++;
                System.out.println();
                System.out.println("looping ke-" + loopingNumber);

                //Narik data SRM:REQ
                AtriumOrchestration email = new AtriumOrchestration();
                ArrayList<String> arrli = new ArrayList<String>();
                
                Entry emailRecord = remedySession.getEntry(MDF, getEntryForm.getEntryID(), null);

                Entry entry = new Entry();
                
                SSCController.Nama[] arr;
                
                arr = new SSCController.Nama[10];
                
                email.reqnumber = getValueFromRemedy(emailRecord, 536870928);
                String reqnumber = email.reqnumber;
                email.nama = getValueFromRemedy(emailRecord, 536870941);
                String nama = email.nama;
                email.srinstanceid = getValueFromRemedy(emailRecord, 536870943);
                String srinstanceid = email.srinstanceid;
                email.emailgroup = getValueFromRemedy(emailRecord, 536870919);
                String emailgroup = email.emailgroup;
                email.membergroup = getValueFromRemedy(emailRecord, 536870941);
                String membergroup = email.membergroup;             
                email.wonumber = getValueFromRemedy(emailRecord, 536870916);
                String wonumber = email.wonumber;
                email.typefield40 = getValueFromRemedy(emailRecord, 536870930);
                String typefield40 = email.typefield40;
                                
                System.out.println("tampung data: " + nama);
                
                
                String parts[] = nama.split("\\s*,\\s*");
                
                for(int i = 0;i<parts.length; i++){
                    System.out.println("Nama: "+parts[i]);
                    addEmail(parts[i],reqnumber,srinstanceid,emailgroup,membergroup,wonumber,typefield40,remedySession);
                }
                
//                System.out.println(parts.length); //untuk tahu ada berapa total data
                
                emailRecord.put(8, new Value("Sudah di Split Email"));
                emailRecord.put(536870940, new Value(parts.length));
                remedySession.setEntry("PTM:SSC:IT:AO:INT", emailRecord.getEntryId(), emailRecord, null, 0);

                
//                String[] values = nama1.split("\\s*,\\s*");
//                System.out.println(Arrays.toString(values));
                

        }

    }catch (Exception e) {
        // TODO: handle exception
        logger.info("error function manipulasiString : " + e.toString()); //buat munculin error
        System.out.println("error fucntion manipulasiString " + e.toString());
        String testingResult = e.toString(); //ngarahin ke testing yg testingResult
        Map<String, String> model = new HashMap<String, String>();
        model.put("testingResult", testingResult);
    }


    return "testing";
    }
    
    public void jenisAD(
            String jenisSecurity,
            String REQNumber,
            String namaForm,
            String jenisLayanan,
            String jenisLayanan2,
            String wonumber,
            String sysrequestid,            
            String srintanceid,
            String secGroup,
            String userID,
//            String email,
            ARServerUser remedySession) {

        RemedyAPI remedyAPI = new RemedyAPI();
        Entry AOsecGrp = new Entry();
        AOsecGrp.put(536870992, new Value(jenisSecurity));
        AOsecGrp.put(536870928, new Value(REQNumber));
        AOsecGrp.put(536870914, new Value(namaForm));
        AOsecGrp.put(536870915, new Value(jenisLayanan));
        AOsecGrp.put(536870952, new Value(jenisLayanan2));
        AOsecGrp.put(536870916, new Value(wonumber));
        AOsecGrp.put(536870939, new Value(sysrequestid));
        AOsecGrp.put(536870943, new Value(srintanceid));
        AOsecGrp.put(536870967, new Value(secGroup));
        AOsecGrp.put(536870966, new Value(userID));
//        AOsecGrp.put(536870921, new Value(typefield33));
        
        String formTravelokaNotification = "PTM:SSC:IT:AO:INT";

        logger.info("input in bmc:" + REQNumber);

        remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, AOsecGrp);
    }
    
    @RequestMapping(value = "/splitSecGrp")
    public String splitSecGrp() {
        //remedy server
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        System.out.print("Remedy Server:" + configValue.getRemedyServer());

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        System.out.println(remedySession);

        // berfungsi untuk query kualifikasi
        List<AtriumOrchestration> listWorkInfo = new ArrayList<AtriumOrchestration>();
        RemedyAPI remedyAPI = new RemedyAPI();

        //Inisialisasi nama Form
        String MDF = "PTM:SSC:IT:AO:INT";

        List<EntryListInfo> list = remedyAPI.getRemedyRecordByQuery(remedySession, MDF, " 'NAMA FORM' = \"Layanan Pekerja Baru, Konsultan/Auditor dan Mitra Kerja\" AND 'JENIS LAYANAN' = \"Add AD User to Security Group\" AND ('SR TYPE FIELD 33' LIKE \"%VPN%\" OR 'SR TYPE FIELD 33' LIKE \"%Internet%\" OR 'SR TYPE FIELD 33' LIKE \"%Wifi_P6C%\") AND 'Short Description' != \"Sudah di Split SECGROUP\" AND 'Status' = 0 AND 'SecurityADGroup' = $NULL$ AND 'JenisSecurity' = $NULL$ ");
//        List<EntryListInfo> list = remedyAPI.getRemedyRecordByQuery(remedySession, MDF, " 'REQNUMBER' = \"REQ000000044394\" ");
        System.out.println("total data: " + list.size());

        try {
            int loopingNumber = 0;
            for (EntryListInfo getEntryForm : list) {
                loopingNumber++;
                System.out.println();
                System.out.println("looping ke-" + loopingNumber);

                //Narik data SRM:REQ
                SecurityGroup secGrpType = new SecurityGroup();
                ArrayList<String> arrli = new ArrayList<String>();
                
                Entry emailRecord = remedySession.getEntry(MDF, getEntryForm.getEntryID(), null);

                Entry entry = new Entry();
                
                SSCController.ADType[] arr;
                
                arr = new SSCController.ADType[5];
                
                secGrpType.jenisSecurity = getValueFromRemedy(emailRecord, 536870992);
                String jenisSecurity = secGrpType.jenisSecurity;      
                
                secGrpType.REQNumber = getValueFromRemedy(emailRecord, 536870928);
                String REQNumber = secGrpType.REQNumber;
                System.out.println(REQNumber);
                System.out.println(jenisSecurity);
                
                secGrpType.namaForm = getValueFromRemedy(emailRecord, 536870914);
                String namaForm = secGrpType.namaForm;
                System.out.println(namaForm);
                
                secGrpType.jenisLayanan = getValueFromRemedy(emailRecord, 536870915);
                String jenisLayanan = secGrpType.jenisLayanan;
                System.out.println(jenisLayanan);
                
                secGrpType.jenisLayanan2 = getValueFromRemedy(emailRecord, 536870952);
                String jenisLayanan2 = secGrpType.jenisLayanan2;
                System.out.println(jenisLayanan);
                
                secGrpType.wonumber = getValueFromRemedy(emailRecord, 536870916);
                String wonumber = secGrpType.wonumber;
                System.out.println(wonumber);
                
                secGrpType.sysrequestid = getValueFromRemedy(emailRecord, 536870939);
                String sysrequestid = secGrpType.sysrequestid;
                System.out.println(sysrequestid);
                               
                
                secGrpType.jenisSecurity = getValueFromRemedy(emailRecord, 536870992);
                  
                
                secGrpType.srintanceid = getValueFromRemedy(emailRecord, 536870943);
                String srintanceid = secGrpType.srintanceid;
                System.out.println(srintanceid);
                
                secGrpType.secGroup = getValueFromRemedy(emailRecord, 536870967);
                String secGroup = secGrpType.secGroup;
                System.out.println(secGroup);
                
                secGrpType.userID = getValueFromRemedy(emailRecord, 536870966);
                String userID = secGrpType.userID;
                System.out.println(userID);                
                
                secGrpType.typefield33 = getValueFromRemedy(emailRecord, 536870921);
                String typefield33 = secGrpType.typefield33;
                System.out.println(typefield33);
                
                System.out.println("tampung jenis AD: " + typefield33);
                
                
//                String parts[] = typefield33.split("\\s*,\\s*");
                String parts[] = typefield33.split(";");
                
                for(int i = 0;i<parts.length; i++){
                    System.out.println("Jenis AD: "+parts[i]);
                    jenisAD(parts[i],REQNumber,namaForm,jenisLayanan,jenisLayanan2,wonumber,sysrequestid,srintanceid,secGroup,userID,remedySession);
                }
                
                System.out.println(parts.length); //untuk tahu ada berapa total data
                
                emailRecord.put(8, new Value("Sudah di Split SECGROUP"));
                emailRecord.put(536870940, new Value(parts.length));
                remedySession.setEntry("PTM:SSC:IT:AO:INT", emailRecord.getEntryId(), emailRecord, null, 0);

                
//                String[] values = typefield33.split(";");
//                System.out.println(Arrays.toString(values));
                

        }

    }catch (Exception e) {
        // TODO: handle exception
        logger.info("error function jenisAD : " + e.toString()); //buat munculin error
        System.out.println("error fucntion manipulasiString " + e.toString());
        String testingResult = e.toString(); //ngarahin ke testing yg testingResult
        Map<String, String> model = new HashMap<String, String>();
        model.put("testingResult", testingResult);
    }


    return "testing";
    }
    
 
    
    
}
