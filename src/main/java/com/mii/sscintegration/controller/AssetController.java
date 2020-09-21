package com.mii.sscintegration.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.itextpdf.text.BadElementException;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.AssetATK;
import com.mii.sscintegration.domain.AssetConsumables;
import com.mii.sscintegration.domain.AssetEventSupport;
import com.mii.sscintegration.domain.AssetKendaraan;
import com.mii.sscintegration.domain.AssetPDF;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.poi.AssetDocument;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
public class AssetController {
	protected static Logger logger = Logger.getLogger("AssetController: ");
	
	@RequestMapping(value = "/printAtk")
	public String printAtk() {
		//Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:ATK3", "'SRInstanceID'=\"SRGAA5V0FMUS3AQIWWEHQHYVPB6NDR\" ");     
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:ATK3", "'Status Print__c' = \"0\" ");
        String atkId;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:ATK3", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("no req: " +assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
	        		assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536870945);
                                System.out.println(assetPdf.lokasi);
	        		assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                
	        		assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                                
                                assetPdf.tanggalPenerimaan = getValueFromRemedy(assetPdfRecord, 536871032);
                                assetPdf.tanggalPenerimaan = assetPdf.tanggalPenerimaan.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPenerimaan = assetPdf.tanggalPenerimaan.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.tanggalPenerimaan);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.tanggalPenerimaan = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPenerimaan);     
                                                                
	        		assetPdf.lokasiPenerimaan = getValueFromRemedy(assetPdfRecord, 536870945);
                                System.out.println(assetPdf.lokasiPenerimaan);
                                
	        		assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong2 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate2 = new Date(dateLong2*1000L);
                                String cekSubmitDate5 = new SimpleDateFormat("dd MMM yyyy").format(cekDate2);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate2);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                
                                
                                //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870950);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);

                                
	        		assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println(assetPdf.groupPelaksana);
	        		assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870942);
                                System.out.println(assetPdf.namaPelaksana);
	        		
                                atkId = getValueFromRemedy(assetPdfRecord, 536870937);
	        		
                                //get atk table
	        		ArrayList<AssetATK> assetATKTable = new ArrayList <AssetATK>();
	        		List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:ATKTable", "'WorkOrderID'=\""+atkId+"\" ");
	        		for(EntryListInfo eListAtk : eListAtks) {
	        			Entry atkEntry = remedySession.getEntry("PTM:SSC:AST:ATKTable", eListAtk.getEntryID(), null);
	        			AssetATK assetATK = new AssetATK();
	        			assetATK.satuan = getValueFromRemedy(atkEntry, 536870917);
                                        System.out.println(assetATK.satuan);
	        			assetATK.deskripsi = getValueFromRemedy(atkEntry, 536870915);
                                        System.out.println(assetATK.deskripsi);
	        			assetATK.jumlahDipenuhi = getValueFromRemedy(atkEntry, 536870916);
                                        System.out.println(assetATK.jumlahDipenuhi);
	        			assetATK.jumlahPermintaan = getValueFromRemedy(atkEntry, 536870914);
                                        System.out.println(assetATK.jumlahPermintaan);
	        			assetATK.namaBarang = getValueFromRemedy(atkEntry, 536870913);
                                        System.out.println(assetATK.namaBarang);
	        			assetATKTable.add(assetATK);
	        		}
	        		
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generateAtk(assetPdf, assetATKTable);
                                
//                                //update status print
	        		assetPdfRecord.put(536870925, new Value("1")); //field status di update jadi printed
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:ATK3", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        @RequestMapping(value = "/printKendaraan")
	public String printKendaraan() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Kendaraan2", "'SRInstanceID__c'=\"SRGAA5V0FMUS3AQJJZ0NQI1YJOXHSO\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Kendaraan2", "'Status Print'=\"0\" ");
        String kendaraanId;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:Kendaraan2", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println(assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870935);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
	        		assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536871142);
                                System.out.println(assetPdf.lokasi);
                                assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                
	        		//add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                
                                
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870929);
                                System.out.println(assetPdf.srInstanceId);
                                               
                                //tanggal dibuat
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                
                                assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                                assetPdf.jumlahpeserta = getValueFromRemedy(assetPdfRecord, 536871140);
                                System.out.println(assetPdf.jumlahpeserta);
                                
                                
                                assetPdf.waktumulai = getValueFromRemedy(assetPdfRecord, 536871143);
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.waktumulai);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.waktumulai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.waktumulai);
                                                                
                                
                                assetPdf.waktuselesai = getValueFromRemedy(assetPdfRecord, 536871144);
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("Timestamp", "");
                                Long dateLong2 = Long.parseLong(assetPdf.waktuselesai);
                                        Date cekDate2 = new Date(dateLong2*1000L);
                                String cekSubmitDate5 = new SimpleDateFormat("dd MMM yyyy").format(cekDate2);
                                assetPdf.waktuselesai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate2);
                                System.out.println("tanggalPickup:"+assetPdf.waktumulai);
                                
	        		assetPdf.lokasiJemput = getValueFromRemedy(assetPdfRecord, 536871141);
                                System.out.println(assetPdf.lokasiJemput);
                                assetPdf.lokasiTujuan = getValueFromRemedy(assetPdfRecord, 536871142);
                                System.out.println(assetPdf.lokasiTujuan);
                                
                                assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong3 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate3 = new Date(dateLong3*1000L);
                                String cekSubmitDate3 = new SimpleDateFormat("dd MMM yyyy").format(cekDate3);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate3);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                
                                System.out.println(assetPdf.tanggalPickup);
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println(assetPdf.groupPelaksana);
	        		assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870942);
                                System.out.println(assetPdf.namaPelaksana);       		
                                
	        		kendaraanId = getValueFromRemedy(assetPdfRecord, 536870937);
                                
	        		//get atk table
	        		ArrayList<AssetKendaraan> assetKendaraanTable = new ArrayList <AssetKendaraan>();
	        		List<EntryListInfo> eListkendaraans = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:KendaraanTable", "'WorkOrderID'=\""+kendaraanId+"\" ");
	        		for(EntryListInfo eListKendaraan : eListkendaraans) {
	        			Entry kendaraanEntry = remedySession.getEntry("PTM:SSC:AST:KendaraanTable", eListKendaraan.getEntryID(), null);
	        			AssetKendaraan assetKendaraan = new AssetKendaraan();
	        			assetKendaraan.jenisKendaraan = getValueFromRemedy(kendaraanEntry, 536870913);
	        			assetKendaraan.noKendaraan = getValueFromRemedy(kendaraanEntry, 536870924);
	        			assetKendaraan.namaSupir = getValueFromRemedy(kendaraanEntry, 536870922);
	        			assetKendaraan.noHP = getValueFromRemedy(kendaraanEntry, 536870923);
	        			assetKendaraanTable.add(assetKendaraan);
	        		}
	        		
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generateKendaraan(assetPdf, assetKendaraanTable);
	        		
	        		//update status print
	        		assetPdfRecord.put(536870927, new Value("1")); //field status di update jadi printed
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:Kendaraan2", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        @RequestMapping(value = "/printKonsumsi")
	public String printKonsumsi() {
		//Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Konsumsi", "'SRIntanceID__c'=\"SRGAA5V0FMUS3AQJW9SFQIY80UEYTZ\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Konsumsi", "'Status Print__c'=\"0\" ");
        String konsumsiId;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:Konsumsi", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println(assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
                                assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536870968);
                                System.out.println(assetPdf.lokasi);
	        		assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                                                
                                //tanggal
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a" );
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                                                
                                assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                                                
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println(assetPdf.groupPelaksana);
                               
                                assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870915);
                                System.out.println(assetPdf.namaPelaksana);

                                assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                                assetPdf.instruksiTambahan = getValueFromRemedy(assetPdfRecord, 536870935);
                                System.out.println(assetPdf.instruksiTambahan);
                                assetPdf.jenis = getValueFromRemedy(assetPdfRecord,536870972);
                                System.out.println(assetPdf.jenis);
                                
                                assetPdf.waktumulai = getValueFromRemedy(assetPdfRecord,536870939).toString();
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("Timestamp", "");
                                Long dateLong2 = Long.parseLong(assetPdf.waktumulai);
                                        Date cekDate2 = new Date(dateLong2*1000L);
                                String cekSubmitDate2 = new SimpleDateFormat("dd MMM yyyy").format(cekDate2);
                                assetPdf.waktumulai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate2);
                                System.out.println("waktumulai:"+assetPdf.waktumulai);                           
                                
                                assetPdf.waktuselesai = getValueFromRemedy(assetPdfRecord,536870943);
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("Timestamp", "");
                                Long dateLong3 = Long.parseLong(assetPdf.waktuselesai);
                                        Date cekDate3 = new Date(dateLong3*1000L);
                                String cekSubmitDate3 = new SimpleDateFormat("dd MMM yyyy").format(cekDate3);
                                assetPdf.waktuselesai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate3);
                                System.out.println("waktuselesai:"+assetPdf.waktuselesai);
                                
	        		assetPdf.lokasikegiatan = getValueFromRemedy(assetPdfRecord, 536870968);
                                System.out.println(assetPdf.lokasikegiatan);
                                assetPdf.paket = getValueFromRemedy(assetPdfRecord, 536870930);
                                System.out.println(assetPdf.paket);
                                assetPdf.jumlahpaket = getValueFromRemedy(assetPdfRecord, 536870933);
                                System.out.println(assetPdf.jumlahpaket);
                                assetPdf.jumlahpeserta = getValueFromRemedy(assetPdfRecord, 536870967);
                                System.out.println(assetPdf.jumlahpeserta);
                                
                                assetPdf.SK = getValueFromRemedy(assetPdfRecord, 536870959);
                                System.out.println(assetPdf.SK);

                                //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870938);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);
	        			        			        		
	        		konsumsiId = getValueFromRemedy(assetPdfRecord, 536870937);
                                System.out.println(konsumsiId);
                                	
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generatKonsumsi(assetPdf);
                                                                
                                //update status print
	        		assetPdfRecord.put(536870927, new Value("1")); //field status di update jadi printed
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:Konsumsi", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        
        @RequestMapping(value = "/printConsumables")
	public String printConsumables() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Consumables2", "'SRIntanceID__c'=\"SRGAA5V0FMUS3AQIWWFQQHYVQ96N1L\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:Consumables2", "'Status Print'=\"0\" ");
        String ConsumablesId;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:Consumables2", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println(assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
                                assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536871122);
                                System.out.println(assetPdf.lokasi);
                                assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                
                                assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                             
                                //tanggal dibuat
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                
                                assetPdf.lokasiPenerimaan = getValueFromRemedy(assetPdfRecord, 536871122);
                                System.out.println(assetPdf.lokasiPenerimaan);
                                
                                assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                	        		
                                //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870936);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);
                                                                               
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println("Group Pelaksana: "+assetPdf.groupPelaksana);
	        		assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870942);
                                System.out.println(assetPdf.namaPelaksana);
                                                                
	        		ConsumablesId = getValueFromRemedy(assetPdfRecord, 536870937);
                                System.out.println("Consumables ID: "+ConsumablesId);
                                
	        		//get consumables table
	        		ArrayList<AssetConsumables> assetConsumablesTable = new ArrayList <AssetConsumables>();
	        		List<EntryListInfo> eListComsumables = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:ConsumablesTable", "'WorkOrderID'=\""+ConsumablesId+"\" ");
	        		for(EntryListInfo eListComsumable : eListComsumables) {
	        			Entry consumablesEntry = remedySession.getEntry("PTM:SSC:AST:ConsumablesTable", eListComsumable.getEntryID(), null);
	        			AssetConsumables assetConsumables = new AssetConsumables();
	        			assetConsumables.NamaProduk = getValueFromRemedy(consumablesEntry, 536870913);
	        			assetConsumables.JumlahPermintaan = getValueFromRemedy(consumablesEntry, 536870914);
	        			assetConsumables.Satuan = getValueFromRemedy(consumablesEntry, 536870917);
	        			assetConsumables.JumlahDipenuhi = getValueFromRemedy(consumablesEntry, 536870916);
                                        assetConsumables.Deskripsi = getValueFromRemedy(consumablesEntry, 536870915);
	        			assetConsumablesTable.add(assetConsumables);
	        		}
	        		
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generateComsumables(assetPdf, assetConsumablesTable);
	        		
	        		//update status print
	        		assetPdfRecord.put(536870927, new Value("1"));
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:Consumables2", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
                                
                                System.out.println("id: "+assetPdfRecord.getEntryId());
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        @RequestMapping(value = "/printEventSupport")
	public String printEventSupport() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:EventSupport", "'SRIntanceID__c'=\"SRGAA5V0FMUS3AQ02S8OQJ4Q7QPWL5\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:EventSupport", "'Status Print'=\"0\" ");
        String EventSupportID;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:EventSupport", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);   
                                System.out.println(assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
                                assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536870927);
                                System.out.println(assetPdf.lokasi);
                                
                                assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                                
                                //tanggal dibuat
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                
                                
                                assetPdf.waktumulai = getValueFromRemedy(assetPdfRecord, 536871127);
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktumulai = assetPdf.waktumulai.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.waktumulai);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.waktumulai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.waktumulai);
                                                                
                                
                                assetPdf.waktuselesai = getValueFromRemedy(assetPdfRecord, 536871128);
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.waktuselesai = assetPdf.waktuselesai.replaceAll("Timestamp", "");
                                Long dateLong2 = Long.parseLong(assetPdf.waktuselesai);
                                        Date cekDate2 = new Date(dateLong2*1000L);
                                String cekSubmitDate5 = new SimpleDateFormat("dd MMM yyyy").format(cekDate2);
                                assetPdf.waktuselesai = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate2);
                                System.out.println("tanggalPickup:"+assetPdf.waktuselesai);
                                
                                assetPdf.lokasikegiatan = getValueFromRemedy(assetPdfRecord, 536870927);
                                System.out.println(assetPdf.lokasikegiatan);
                                assetPdf.jumlahpeserta = getValueFromRemedy(assetPdfRecord, 536871124);
                                System.out.println(assetPdf.jumlahpeserta);
                                
                                //Tanggal & Jam Pickup
                                assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong3 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate3 = new Date(dateLong3*1000L);
                                String cekSubmitDate3 = new SimpleDateFormat("dd MMM yyyy").format(cekDate3);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate3);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println("Group Pelaksana: "+assetPdf.groupPelaksana);
	        		assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870945); //field ID QA dan Prod beda
                                System.out.println(assetPdf.namaPelaksana);
	        		
                                //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870938);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);
                                               
                                //Work Order ID
	        		EventSupportID = getValueFromRemedy(assetPdfRecord, 536870937);
                                System.out.println("ID: "+EventSupportID);
                                
	        		//get consumables table
	        		ArrayList<AssetEventSupport> assetEventSupportTable = new ArrayList <AssetEventSupport>();
	        		List<EntryListInfo> eListEventSupports = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:EventSupportTable", "'WorkOrderID'=\""+EventSupportID+"\" ");
	        		for(EntryListInfo eListEventSupport : eListEventSupports) {
	        			Entry eventsupportEntry = remedySession.getEntry("PTM:SSC:AST:EventSupportTable", eListEventSupport.getEntryID(), null);
	        			AssetEventSupport assetEventSupport = new AssetEventSupport();
                                        
	        			assetEventSupport.NamaProduk = getValueFromRemedy(eventsupportEntry, 536870913);
                                        System.out.println(assetEventSupport.NamaProduk );
	        			assetEventSupport.JumlahPermintaan = getValueFromRemedy(eventsupportEntry, 536870914);
                                        System.out.println(assetEventSupport.JumlahPermintaan );
	        			assetEventSupport.Satuan = getValueFromRemedy(eventsupportEntry, 536870917);
                                        System.out.println(assetEventSupport.Satuan );
	        			assetEventSupport.JumlahDipenuhi = getValueFromRemedy(eventsupportEntry, 536870916);
                                        System.out.println(assetEventSupport.JumlahDipenuhi );
                                        assetEventSupport.Deskripsi = getValueFromRemedy(eventsupportEntry, 536870915);
                                        System.out.println(assetEventSupport.Deskripsi );
	        			
                                        assetEventSupportTable.add(assetEventSupport);
	        		}
	        		
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generateEventSupport(assetPdf, assetEventSupportTable);
	        		
	        		//update status print
	        		assetPdfRecord.put(536870933, new Value("1"));
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:EventSupport", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        
        @RequestMapping(value = "/printBahanbakar")
	public String printBahanbakar() {
		//Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:BahanBakar2", "'SRIntanceID__c'=\"SRGAA5V0FMUS3AQ00VI2QJ2TH5LVWH\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:BahanBakar2", "'Status Print'=\"0\" ");
        String BahanbakarId;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:BahanBakar2", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println(assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
	        		assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                                                
                                assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println(assetPdf.deskripsiSingkat);
                                assetPdf.namaitem = getValueFromRemedy(assetPdfRecord,536870975);
                                System.out.println(assetPdf.namaitem);
                                assetPdf.jenis = getValueFromRemedy(assetPdfRecord,536870974);
                                System.out.println(assetPdf.jenis);
                                assetPdf.nokendaraan = getValueFromRemedy(assetPdfRecord,536870976);
                                System.out.println(assetPdf.nokendaraan);
                                assetPdf.jumlahpeserta = getValueFromRemedy(assetPdfRecord, 536870977);
                                System.out.println(assetPdf.jumlahpeserta);
                                assetPdf.namasupir = getValueFromRemedy(assetPdfRecord, 536870979);
                                System.out.println(assetPdf.namasupir);
                                assetPdf.nohp = getValueFromRemedy(assetPdfRecord, 536870982);
                                System.out.println(assetPdf.nohp);
                                                                
                                //tanggal
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                                                
                                assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.tanggalPickup);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                                                
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println(assetPdf.groupPelaksana);
                                
                                //blm ada fieldnya di form
                                assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870942);
                                System.out.println(assetPdf.namaPelaksana);     
                                                                
                                //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870936);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);
	        			        			        		
	        		BahanbakarId = getValueFromRemedy(assetPdfRecord, 536870916);
                                System.out.println(BahanbakarId);
                                	
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generatBahanBakar(assetPdf);
                                                                
                                //update status print
	        		assetPdfRecord.put(536870962, new Value("1")); //field status di update jadi printed
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:BahanBakar2", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
        
        @RequestMapping(value = "/printFacilityManagement")
	public String printFacilityManagement() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
        //List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:FacilityManagement", "'SRIntanceID__c'=\"SRGAA5V0FMUS3AQ0YPB4Q0ANA6IUWD\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:AST:Fulfiller:FacilityManagement", "'Status Print'=\"0\" ");
        String facilityManagementID;
        
        try {
	        	for (EntryListInfo eListInfo : eListInfos) {
	        		AssetPDF assetPdf = new AssetPDF();
	        		
	        		Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:AST:Fulfiller:FacilityManagement", eListInfo.getEntryID(), null);
	        		assetPdf.noTiket = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("no req: " +assetPdf.noTiket);
	        		assetPdf.namaRequestor = getValueFromRemedy(assetPdfRecord, 536870918);
                                System.out.println(assetPdf.namaRequestor);
	        		assetPdf.nomorPekerja = getValueFromRemedy(assetPdfRecord, 536870922);
                                System.out.println(assetPdf.nomorPekerja);
	        		assetPdf.fungsi = getValueFromRemedy(assetPdfRecord, 536870913);
                                System.out.println(assetPdf.fungsi);
	        		assetPdf.noKontak = getValueFromRemedy(assetPdfRecord, 536870919);
                                System.out.println(assetPdf.noKontak);
	        		assetPdf.email = getValueFromRemedy(assetPdfRecord, 536870920);
                                System.out.println(assetPdf.email);
	        		assetPdf.lokasi = getValueFromRemedy(assetPdfRecord, 536870975);
                                System.out.println(assetPdf.lokasi);
	        		assetPdf.gedung = getValueFromRemedy(assetPdfRecord, 536870921);
                                System.out.println(assetPdf.gedung);
                                
                                assetPdf.permintaan = getValueFromRemedy(assetPdfRecord, 536870976);
                                System.out.println(assetPdf.permintaan);
                                assetPdf.ruangan = getValueFromRemedy(assetPdfRecord, 536870974);
                                System.out.println(assetPdf.ruangan);
                                assetPdf.lantai = getValueFromRemedy(assetPdfRecord, 536870977);
                                System.out.println(assetPdf.lantai);                                
                                
                                //deskripsi
	        		assetPdf.deskripsiSingkat = getValueFromRemedy(assetPdfRecord, 536870926);
                                System.out.println("Deskripsi: " + assetPdf.deskripsiSingkat);
                                
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                assetPdf.tanggal = formatter.format(today);
                                System.out.println("today: "+assetPdf.tanggal);
                                                                
                                //tgl permintaan
                                assetPdf.tanggalPenerimaan = getValueFromRemedy(assetPdfRecord, 536870963);
                                assetPdf.tanggalPenerimaan = assetPdf.tanggalPenerimaan.replaceAll("[^a-zA-Z0-9]", "");
                                assetPdf.tanggalPenerimaan = assetPdf.tanggalPenerimaan.replaceAll("Timestamp", "");
                                Long dateLong1 = Long.parseLong(assetPdf.tanggalPenerimaan);
                                        Date cekDate1 = new Date(dateLong1*1000L);
                                String cekSubmitDate1 = new SimpleDateFormat("dd MMM yyyy").format(cekDate1);
                                assetPdf.tanggalPenerimaan = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(cekDate1);
                                System.out.println("tanggal permintaan:"+assetPdf.tanggalPenerimaan);     
                                                                                              
//	        		assetPdf.tanggalPickup = getValueFromRemedy(assetPdfRecord, 3);
//                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("[^a-zA-Z0-9]", "");
//                                assetPdf.tanggalPickup = assetPdf.tanggalPickup.replaceAll("Timestamp", "");
//                                Long dateLong2 = Long.parseLong(assetPdf.tanggalPickup);
//                                        Date cekDate2 = new Date(dateLong2*1000L);
//                                String cekSubmitDate5 = new SimpleDateFormat("dd MMM yyyy").format(cekDate2);
//                                assetPdf.tanggalPickup = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cekDate2);
//                                System.out.println("tanggalPickup:"+assetPdf.tanggalPickup);
                                
                                assetPdf.groupPelaksana = getValueFromRemedy(assetPdfRecord, 536870941);
                                System.out.println("Group Pelaksana: " +assetPdf.groupPelaksana);
	        		assetPdf.namaPelaksana = getValueFromRemedy(assetPdfRecord, 536870942);
                                System.out.println("Nama Pelaksana: " +assetPdf.namaPelaksana);  
                                
                                
                                 //add workinfo
	        		assetPdf.requstNumber = getValueFromRemedy(assetPdfRecord, 536870923);
                                System.out.println("Request Number: "+assetPdf.requstNumber);
	        		assetPdf.filename = getValueFromRemedy(assetPdfRecord, 536870923)+".pdf";
                                System.out.println(assetPdf.filename);
                                assetPdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 536870936);
                                System.out.println("srInstanceID: "+assetPdf.srInstanceId);
	        			        			        		
	        		facilityManagementID = getValueFromRemedy(assetPdfRecord, 536870916);
                                System.out.println(facilityManagementID);
                                	
	        		AssetDocument assetDocument = new AssetDocument();
	        		assetDocument.generateFacilityManagement(assetPdf);
                                                                
                                //update status print
	        		assetPdfRecord.put(536870962, new Value("1")); //field status di update jadi printed
	        		remedySession.setEntry("PTM:SSC:AST:Fulfiller:FacilityManagement", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
	        		
	        		
	        	}
        }catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }
		
		
		return "testing";
	}
     
	
	public String getValueFromRemedy(Entry assetRecord, Object fieldID) {
		if(assetRecord.get(fieldID).getValue()==null)
			return "";
		
		return assetRecord.get(fieldID).getValue().toString();
	}
               
        
}
