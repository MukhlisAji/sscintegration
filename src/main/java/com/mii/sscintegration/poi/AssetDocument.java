package com.mii.sscintegration.poi;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.mii.sscintegration.controller.RemedyController;
import com.mii.sscintegration.domain.AssetATK;
import com.mii.sscintegration.domain.AssetConsumables;
import com.mii.sscintegration.domain.AssetEventSupport;
import com.mii.sscintegration.domain.AssetKendaraan;
import com.mii.sscintegration.domain.AssetPDF;
import com.mii.sscintegration.domain.TravelokaPDF;

public class AssetDocument {
	protected static Logger logger = Logger.getLogger("AssetDocument : ");
	
    public void generateAtk(AssetPDF assetPDF, ArrayList<AssetATK> assetATKTable) throws BadElementException, IOException{
        Document document = new Document();
		
        try {
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));
//                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/ATK/"+assetPDF.noTiket+".pdf"));
                document.open();
			
			
                Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            	Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            	Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
                Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);
            	
            	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//            	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            	logo.setAlignment(logo.ALIGN_RIGHT);
            	PdfPTable header = new PdfPTable(1);
			
            	try {
                // set defaults
                header.setTotalWidth(152f);
                header.setSpacingBefore(20f);
                header.setSpacingAfter(10f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                header.addCell(logo);

                // add text
                document.add(header);
            } catch (DocumentException de) {
//                throw new ExceptionConverter(de);
            }
                
            	
            	Paragraph paragraph1 = new Paragraph("PERMINTAAN LAYANAN\n" + 
            			"OFFICE FACILITY SUPPORT - ALAT TULIS KANTOR", fontHeader);
            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);
            
            paragraph1.add("\n\n"+ assetPDF.noTiket);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(assetPDF.tanggal));
            
            paragraph1.add("\n\nRequester");
            paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
            paragraph1.add("\n"+assetPDF.fungsi);
            paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
            paragraph1.add("\n"+assetPDF.lokasi+" / "+assetPDF.gedung);
            
            paragraph1.add("\n\nDeskripsi Singkat");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.deskripsiSingkat);
            
            paragraph1.add("\nTanggal Penerimaan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.tanggalPenerimaan);
            
            paragraph1.add("\nLokasi Penerimaan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.lokasiPenerimaan);
            
            paragraph1.add("\nTanggal & Jam Pick Up Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.tanggalPickup);
            
            paragraph1.add("\nGrup Pelaksana");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.groupPelaksana);
            
            paragraph1.add("\nNama Pelaksana");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": "+assetPDF.namaPelaksana);
            
            paragraph1.add("\n\n\nDAFTAR PERMINTAAN");
            
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nama Barang", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Jumlah Permintaan", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("Satuan", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah yang Dipenuhi", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Deskripsi", fontTable));
            float[] columnPermintaan = {5, 20, 20, 15, 20, 20};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);
            
            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);


            int tempLoop = 0;
            
            while(tempLoop<assetATKTable.size()) {
            		AssetATK assetATK = assetATKTable.get(tempLoop);
            		tempLoop++;
            		cell1 = new PdfPCell(new Phrase(""+tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(assetATK.namaBarang, fontTable));
                cell3 = new PdfPCell(new Phrase(assetATK.jumlahPermintaan, fontTable));
                cell4 = new PdfPCell(new Phrase(assetATK.satuan, fontTable));
                cell5 = new PdfPCell(new Phrase(assetATK.jumlahDipenuhi, fontTable));
                cell6 = new PdfPCell(new Phrase(assetATK.deskripsi, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
//            		tempLoop++;
            }
            
            paragraph1.add(tablePermintaan);
            
            
        for(int i = 0 ; i<13 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        document.add(paragraph1); 
        
//        for(int i = 0 ; i<3 ; i++){
//            paragraph1.add("\n");
//        }   
        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
                + "\n*) Bukti permintaan harus diserahkan saat pengambilan ATK", fontFooter));
        ct.go();
        
        document.close();
            
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    public void generateKendaraan(AssetPDF assetPDF, ArrayList<AssetKendaraan> assetKendaraanTable) throws BadElementException, IOException{
	Document document = new Document();
	
	//PdfWriter.getInstance(document, new FileOutputStream("new_fixed_approval.pdf"));
    try {
        
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/Kendaraan/"+assetPDF.noTiket+".pdf"));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));
        document.open();


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);
        

        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);

        logo.setAlignment(logo.ALIGN_RIGHT);
        	
            PdfPTable header = new PdfPTable(1);
		
            try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        Paragraph paragraph1 = new Paragraph("PERMINTAAN LAYANAN\n" + 
        			"TRANSPORTATION - PENYEDIAAN KENDARAAN", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(new Chunk(assetPDF.tanggal));
        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.lokasi+" / "+assetPDF.gedung);
        
        
        paragraph1.add("\n\nDeskripsi Singkat");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.deskripsiSingkat);
        
        
        paragraph1.add("\nJumlah Penumpang");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jumlahpeserta);
                    
                    
        paragraph1.add("\nTanggal & Waktu Jemput");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktumulai);
        
        
        paragraph1.add("\nTanggal & Waktu Kembali");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktuselesai);
        
        paragraph1.add("\nLokasi Penjemputan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lokasiJemput);
        
        paragraph1.add("\nLokasi Tujuan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lokasiTujuan);
        
        paragraph1.add("\nTanggal & Jam Pick Up Tiket");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktuselesai);
        
        paragraph1.add("\nGrup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana);
        
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
        
        
        paragraph1.add("\n\n\nDAFTAR PERMINTAAN");
        
        PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
        PdfPCell cell2 = new PdfPCell(new Phrase("Jenis Kendaraan", fontTable));
        PdfPCell cell3 = new PdfPCell(new Phrase("No Kendaraan", fontTable));
        PdfPCell cell4 = new PdfPCell(new Phrase("Nama Supir", fontTable));
        PdfPCell cell5 = new PdfPCell(new Phrase("No. Handphone", fontTable));
        float[] columnPermintaan = {5, 20, 20, 15, 20};
        PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
        tablePermintaan.setWidthPercentage(100);
        
        cell1.setBorder(Rectangle.BOX);
        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBorder(Rectangle.BOX);
        cell3.setBorder(Rectangle.BOX);
        cell4.setBorder(Rectangle.BOX);
        cell5.setBorder(Rectangle.BOX);

        //cell1.setVerticalAlignment(verticalAlignment);
        tablePermintaan.addCell(cell1);
        tablePermintaan.addCell(cell2);
        tablePermintaan.addCell(cell3);
        tablePermintaan.addCell(cell4);
        tablePermintaan.addCell(cell5);
        
        int jumlahLoop = 0;
        int tempLoop = 0;
        
        while(tempLoop<assetKendaraanTable.size()) {
                AssetKendaraan assetKendaraan = assetKendaraanTable.get(tempLoop);
                tempLoop++;
                cell1 = new PdfPCell(new Phrase(""+tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(assetKendaraan.jenisKendaraan, fontTable));
                cell3 = new PdfPCell(new Phrase(assetKendaraan.noKendaraan, fontTable));
                cell4 = new PdfPCell(new Phrase(assetKendaraan.namaSupir, fontTable));
                cell5 = new PdfPCell(new Phrase(assetKendaraan.noHP, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
        }
        
        paragraph1.add(tablePermintaan);
        
        for(int i = 0 ; i<9 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        document.add(paragraph1); 
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }
        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K: "
                + "\n*) Bukti permintaan harus diserahkan pada saat konfirmasi keberangkatan"
                + "\n**) Konfirmasi keberangkatan minimal 30 menit sebelum keberangkatan"
                + "\n***) Bukti permintaan ini merupakan bukti persetujuan yang sah dan harus diserahkan pada berkas penagihan"
                + "\n****) Waktu tunggu kendaraan di lokasi Maksimal 2,5 Jam", fontFooter));
        ct.go();
        
        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
    
    public void generatKonsumsi(AssetPDF assetPDF) throws BadElementException, IOException{
	Document document = new Document();
	
    try {

//	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/Konsumsi/"+assetPDF.noTiket+".pdf"));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));
        document.open();


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);

        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);

        logo.setAlignment(logo.ALIGN_RIGHT);
        PdfPTable header = new PdfPTable(1);
		
            try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        	Paragraph paragraph1 = new Paragraph("PERMINTAAN LAYANAN\n" + 
        			"OFFICE FACILITY SUPPORT - KONSUMSI", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(new Chunk(assetPDF.tanggal));
                        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.lokasi+" / "+assetPDF.gedung);
        
        paragraph1.add("\n\nTanggal & Jam Pickup Ticket");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.tanggalPickup);
        
        paragraph1.add("\nGroup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana);
        
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
                    
        paragraph1.add("\n\nDeskripsi Kegiatan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.deskripsiSingkat);
        
        paragraph1.add("\nInstruksi Tambahan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.instruksiTambahan);
        
        paragraph1.add("\nJenis");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jenis);
        
        paragraph1.add("\nTanggal & Waktu Mulai Kegiatan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktumulai);
        
        paragraph1.add("\nTanggal & Waktu Kegiatan Berakhir");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktuselesai);
        
        paragraph1.add("\nLokasi Kegiatan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lokasikegiatan);
        
        paragraph1.add("\nPaket");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.paket);
        
        paragraph1.add("\nJumlah Paket");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jumlahpaket);
        
        paragraph1.add("\nJumlah Peserta");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jumlahpeserta);

        for(int i = 0 ; i<13 ; i++){
            paragraph1.add("\n");
        }
        
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        
        paragraph1.add("Yang Meminta / Pengguna");
    
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }        
        
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }
        
        document.add(paragraph1);
                           
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
                + "\n*) Bukti permintaan harus diserahkan pada saat pengambilan konsumsi "
                + "\n**) Bukti permintaan ini merupakan bukti persetujuan yang sah dan harus disertakan pada berkas penagihan "
                + "\n***) Pemesanan konsumsi hanya dapat dibatalkan atau di edit pada hari kerja (pkl.07:00 - 16:00), paling lambat sehari sebelum acara berlangsung", fontFooter));
        ct.go();
        
        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
        } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
    public void generateComsumables(AssetPDF assetPDF, ArrayList<AssetConsumables> assetComsumeableTable) throws BadElementException, IOException{
	Document document = new Document();

    try {
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));
//                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/Consumables/"+assetPDF.noTiket+".pdf"));
		document.open();
		
		
		Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        	Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        	Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
                Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);
        	
//        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
                Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
        	logo.setAlignment(logo.ALIGN_RIGHT);
        	PdfPTable header = new PdfPTable(1);
		
        	try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        	Paragraph paragraph1 = new Paragraph("PERMINTAAN LAYANAN\n" + 
        			"OFFICE FACILITY SUPPORT - CONSUMABLES", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        
        paragraph1.add(new Chunk(assetPDF.tanggal));
        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.lokasi+" / "+assetPDF.gedung);
        
        
        paragraph1.add("\n\nDeskripsi Singkat");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.deskripsiSingkat);
        
        paragraph1.add("\nLokasi Penerimaan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lokasiPenerimaan);
        
        paragraph1.add("\nTanggal & Jam Pick Up Tiket");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.tanggalPickup);
        
        paragraph1.add("\nGrup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana);
              
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
                
        
        paragraph1.add("\n\n\nDAFTAR PERMINTAAN");
        
        PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
        PdfPCell cell2 = new PdfPCell(new Phrase("Nama Produk", fontTable));
        PdfPCell cell3 = new PdfPCell(new Phrase("Jumlah Permintaan", fontTable));
        PdfPCell cell4 = new PdfPCell(new Phrase("Satuan", fontTable));
        PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Yang Dipenuhi", fontTable));
        PdfPCell cell6 = new PdfPCell(new Phrase("Deskripsi", fontTable));
        float[] columnPermintaan = {5, 20, 15, 15, 15, 20};
        PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
        tablePermintaan.setWidthPercentage(100);
        
        cell1.setBorder(Rectangle.BOX);
        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
        cell6.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBorder(Rectangle.BOX);
        cell3.setBorder(Rectangle.BOX);
        cell4.setBorder(Rectangle.BOX);
        cell5.setBorder(Rectangle.BOX);
        cell6.setBorder(Rectangle.BOX);

        //cell1.setVerticalAlignment(verticalAlignment);
        tablePermintaan.addCell(cell1);
        tablePermintaan.addCell(cell2);
        tablePermintaan.addCell(cell3);
        tablePermintaan.addCell(cell4);
        tablePermintaan.addCell(cell5);
        tablePermintaan.addCell(cell6);
        
//        int jumlahLoop = 0;
        int tempLoop = 0;
        
        while(tempLoop<assetComsumeableTable.size()) {
                AssetConsumables assetConsumeables = assetComsumeableTable.get(tempLoop);
                tempLoop++;
                cell1 = new PdfPCell(new Phrase(""+tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(assetConsumeables.NamaProduk, fontTable));
                cell3 = new PdfPCell(new Phrase(assetConsumeables.JumlahPermintaan, fontTable));
                cell4 = new PdfPCell(new Phrase(assetConsumeables.Satuan, fontTable));
                cell5 = new PdfPCell(new Phrase(assetConsumeables.JumlahDipenuhi, fontTable));
                cell6 = new PdfPCell(new Phrase(assetConsumeables.Deskripsi, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
//        		tempLoop++;
        }
        
        paragraph1.add(tablePermintaan);
        
        for(int i = 0 ; i<12 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        document.add(paragraph1);
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }
        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
                + "\n*) Bukti permintaan harus diserahkan saat pengambilan/penerimaan barang", fontFooter));
        ct.go();

        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
    
    public void generateEventSupport(AssetPDF assetPDF, ArrayList<AssetEventSupport> assetEventSupportTable) throws BadElementException, IOException{
	Document document = new Document();

    try {
	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/EventSupport/"+assetPDF.noTiket+".pdf"));
        document.open();


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);

      Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
        logo.setAlignment(logo.ALIGN_RIGHT);
        PdfPTable header = new PdfPTable(1);

        try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        	Paragraph paragraph1 = new Paragraph("PERMINTAAN LAYANAN\n" + 
        			"OFFICE FACILITY SUPPORT - EVENT SUPPORT", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        
        paragraph1.add(new Chunk(assetPDF.tanggal));
        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.lokasi+" / "+assetPDF.gedung);
        
        
        paragraph1.add("\n\nDeskripsi Singkat");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.deskripsiSingkat);
        
        paragraph1.add("\nTanggal dan Waktu Mulai");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktumulai);
        
        
        paragraph1.add("\nTanggal & Waktu Berakhir");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.waktuselesai);
        
        paragraph1.add("\nLokasi Kegiatan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lokasikegiatan);
        
        paragraph1.add("\nJumlah Peserta");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jumlahpeserta);
                           
        paragraph1.add("\nGrup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana); 
      
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
        
        
        paragraph1.add("\n\n\nDAFTAR PERMINTAAN");
        
        PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
        PdfPCell cell2 = new PdfPCell(new Phrase("Nama Produk", fontTable));
        PdfPCell cell3 = new PdfPCell(new Phrase("Jumlah Permintaan", fontTable));
        PdfPCell cell4 = new PdfPCell(new Phrase("Satuan", fontTable));
        PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Yang Dipenuhi", fontTable));
        PdfPCell cell6 = new PdfPCell(new Phrase("Deskripsi", fontTable));
        float[] columnPermintaan = {5, 20, 20, 15, 20, 15};
        PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
        tablePermintaan.setWidthPercentage(100);
        
        cell1.setBorder(Rectangle.BOX);
        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
        cell6.setBackgroundColor(new BaseColor(179, 214, 253));
        cell2.setBorder(Rectangle.BOX);
        cell3.setBorder(Rectangle.BOX);
        cell4.setBorder(Rectangle.BOX);
        cell5.setBorder(Rectangle.BOX);
        cell6.setBorder(Rectangle.BOX);

        //cell1.setVerticalAlignment(verticalAlignment);
        tablePermintaan.addCell(cell1);
        tablePermintaan.addCell(cell2);
        tablePermintaan.addCell(cell3);
        tablePermintaan.addCell(cell4);
        tablePermintaan.addCell(cell5);
        tablePermintaan.addCell(cell6);
        
//        int jumlahLoop = 0;
        int tempLoop = 0;
        
        while(tempLoop<assetEventSupportTable.size()) {
            AssetEventSupport assetEventSupport = assetEventSupportTable.get(tempLoop);
            tempLoop++;
            cell1 = new PdfPCell(new Phrase(""+tempLoop, fontTable));
            cell2 = new PdfPCell(new Phrase(assetEventSupport.NamaProduk, fontTable));
            cell3 = new PdfPCell(new Phrase(assetEventSupport.JumlahPermintaan, fontTable));
            cell4 = new PdfPCell(new Phrase(assetEventSupport.Satuan, fontTable));
            cell5 = new PdfPCell(new Phrase(assetEventSupport.JumlahDipenuhi, fontTable));
            cell6 = new PdfPCell(new Phrase(assetEventSupport.Deskripsi, fontTable));
            cell1.setBorder(Rectangle.BOX);
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
//            tempLoop++;
        }
        paragraph1.add(tablePermintaan);
        
        for(int i = 0 ; i<12 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        document.add(paragraph1); 
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }

        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
                + "\n*) Bukti permintaan harus diserahkan saat penggunaan ruangan", fontFooter));
        ct.go();

        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
    
    public void generatBahanBakar(AssetPDF assetPDF) throws BadElementException, IOException{
	Document document = new Document();
	
	//PdfWriter.getInstance(document, new FileOutputStream("new_fixed_approval.pdf"));
    try {

//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/Bahan Bakar/"+assetPDF.noTiket+".pdf"));
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));

        document.open();


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);

        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
        logo.setAlignment(logo.ALIGN_RIGHT);
        PdfPTable header = new PdfPTable(1);

        try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        	Paragraph paragraph1 = new Paragraph("PENYEDIAAN BAHAN BAKAR", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(new Chunk(assetPDF.tanggal));
        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.gedung);
        
        paragraph1.add("\n\nDeskripsi Singkat");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.deskripsiSingkat);
        
        paragraph1.add("\nNama Item");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaitem);
        
        paragraph1.add("\nJenis Kendaraan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jenis);
        
        paragraph1.add("\nNomor Kendaraan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.nokendaraan);
        
        paragraph1.add("\nJumlah");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.jumlahpeserta+" Ltr");
        
        paragraph1.add("\nNama Supir");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namasupir);
        
        paragraph1.add("\nNomor Handphone");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.nohp);
        
        paragraph1.add("\n\nTanggal & Jam Pickup Ticket");
        paragraph1.add(Chunk.TABBING);            
        paragraph1.add(": "+assetPDF.tanggalPickup);
        
        paragraph1.add("\nGroup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana);
        
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
        
        for(int i = 0 ; i<13 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
                
        for(int i = 0 ; i<10 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("(                                         )");
        
        document.add(paragraph1); 
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }

        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
                + "\n*) Bukti permintaan harus diserahkan saat pengisian bahan bakar "
                + "\n**) Permintaan penambahan bahan bakar adalah untuk kepentingan perusahaan dan dapat dipertanggungjawabkan"
                + "\n***) Bukti permintaan ini adalah dokumen yang sah dan telah disetujui oleh pejabat yang meminta", fontFooter));
        ct.go();
        
        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
        } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
    public void generateFacilityManagement(AssetPDF assetPDF) throws BadElementException, IOException{
	Document document = new Document();
	
	//PdfWriter.getInstance(document, new FileOutputStream("new_fixed_approval.pdf"));
    try {

//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/Facility Management/"+assetPDF.noTiket+".pdf"));
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(assetPDF.noTiket+".pdf"));

        document.open();


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.BLACK);

        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
        logo.setAlignment(logo.ALIGN_RIGHT);
        PdfPTable header = new PdfPTable(1);

        try {
            // set defaults
            header.setTotalWidth(152f);
            header.setSpacingBefore(20f);
            header.setSpacingAfter(10f);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(60);
            header.getDefaultCell().setBorderColor(BaseColor.WHITE);
            header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            header.addCell(logo);

            // add text
            document.add(header);
        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
        }
        	
        	Paragraph paragraph1 = new Paragraph("FACILITY MANAGEMENT SERVICES", fontHeader);
        paragraph1.setFont(fontUntukIsi);
        paragraph1.setSpacingAfter(30f);
        
        paragraph1.add("\n\n"+ assetPDF.noTiket);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(new Chunk(assetPDF.tanggal));
        
        paragraph1.add("\n\nRequester");
        paragraph1.add("\n"+assetPDF.namaRequestor+" / "+assetPDF.nomorPekerja);
        paragraph1.add("\n"+assetPDF.fungsi);
        paragraph1.add("\n"+assetPDF.noKontak+" / "+assetPDF.email);
        paragraph1.add("\n"+assetPDF.lokasi);
        
        paragraph1.add("\n\nGedung");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.gedung);
        
        paragraph1.add("\nPermintaan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.permintaan);
        
        paragraph1.add("\nRuangan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.ruangan);
        
        paragraph1.add("\nLantai");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.lantai);
        
        paragraph1.add("\nDeskripsi");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+ assetPDF.deskripsiSingkat);
               
        paragraph1.add("\n\nTanggal Penerimaan");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.tanggalPenerimaan);
        
        paragraph1.add("\nGroup Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.groupPelaksana);
        
        paragraph1.add("\nNama Pelaksana");
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(Chunk.TABBING);
        paragraph1.add(": "+assetPDF.namaPelaksana);
        
        for(int i = 0 ; i<13 ; i++){
            paragraph1.add("\n");
        }
        
        paragraph1.add(Chunk.TABBING);
        paragraph1.add("Pelaksana");
        
        for(int i = 0 ; i<8 ; i++){
            paragraph1.add(Chunk.TABBING);
        }
        paragraph1.add("Yang Meminta / Pengguna");
        
        for(int i = 0 ; i<5 ; i++){
            paragraph1.add("\n");
        }
        paragraph1.add("(                                         )");
        
        for(int i = 0 ; i<7 ; i++){
            paragraph1.add(Chunk.TABBING);
        }    
        
        paragraph1.add("(                                         )");
        
        document.add(paragraph1); 
        
        for(int i = 0 ; i<3 ; i++){
            paragraph1.add("\n");
        }

        
        Rectangle rect = new Rectangle(800, 80, 10, 10);
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph("S & K:"
               + "\n*) Bukti permintaan harus diserahkan pada saat tim teknisi engineering menyerahkan kabel extension "
               + "\n**) Bukti permintaan ini merupakan bukti persetujuan yang sah dan harus diserahkan kepada tim pelaksana pada saat penyerahan" , fontFooter));
        ct.go();
        
        document.close();
        
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        travelokaPdf.namafile = assetPDF.filename;
        travelokaPdf.srInstanceId = assetPDF.srInstanceId;
        travelokaPdf.requstNumber = assetPDF.requstNumber;
        
        RemedyController remedyController = new RemedyController();
        remedyController.sendPdftoWorkInfo(travelokaPdf);
        
        } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }

    
    
}
