package com.mii.sscintegration.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mii.sscintegration.domain.TravelAdvanceMetadata;
import com.mii.sscintegration.domain.TravelSettlementMetadata;

public class BMCDocument {
	public void generateTravelExpense(TravelSettlementMetadata travelSettlementMetadata) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(travelSettlementMetadata.getNamaFile()));
			 
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
			Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK); 
			//Chunk chunk = new Chunk("Formulir Pertanggungjawaban Cash Card", font);
			
			Paragraph paragraph1 = new Paragraph("Formulir Pertanggungjawaban Travel Expense");
			paragraph1.setFont(font);
			paragraph1.setSpacingAfter(50f);
			paragraph1.setSpacingBefore(72f);
			
			Paragraph paragraph3 = new Paragraph();
			paragraph3.setAlignment(Paragraph.ALIGN_LEFT);
			paragraph3.setFont(fontUntukIsi);
			
			
			
			
			float[] columnWidths = {5, 1, 10};
			PdfPTable table = new PdfPTable(columnWidths);
			
			
			PdfPCell cellOne = new PdfPCell(new Phrase("Nomer Tiket"));
			PdfPCell cellTwo = new PdfPCell(new Phrase(":"));
			PdfPCell cell3 = new PdfPCell(new Phrase(travelSettlementMetadata.getNomerTiket()));
			
			cellOne.setMinimumHeight(30);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellTwo.setBorder(Rectangle.NO_BORDER);
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3. setColspan(3);
			
			//cell3.setFixedHeight(fixedHeight);
			
			table.addCell(cellOne);
			table.addCell(cellTwo);
			table.addCell(cell3);
			
			
			
			PdfPCell cell4 = new PdfPCell(new Phrase("Tanggal"));
			PdfPCell cell5 = new PdfPCell(new Phrase(":"));
			PdfPCell cell6 = new PdfPCell(new Phrase(travelSettlementMetadata.getTanggal()));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell5.setBorder(Rectangle.NO_BORDER);
			cell6.setBorder(Rectangle.NO_BORDER);
			cell4.setMinimumHeight(30);
			table.addCell(cell4);
			table.addCell(cell5);
			table.addCell(cell6);
			
			
			PdfPCell cell7 = new PdfPCell(new Phrase("Nomor Pekerja"));
			PdfPCell cell8 = new PdfPCell(new Phrase(":"));
			PdfPCell cell9 = new PdfPCell(new Phrase(travelSettlementMetadata.getNomerPekerja()));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell8.setBorder(Rectangle.NO_BORDER);
			cell9.setBorder(Rectangle.NO_BORDER);
			cell7.setMinimumHeight(30);
			table.addCell(cell7);
			table.addCell(cell8);
			table.addCell(cell9);
			
			
			
			PdfPCell cell10 = new PdfPCell(new Phrase("Nama  Pekerja"));
			PdfPCell cell11 = new PdfPCell(new Phrase(":"));
			PdfPCell cell12 = new PdfPCell(new Phrase(travelSettlementMetadata.getNamaPekerja()));
			cell10.setBorder(Rectangle.NO_BORDER);
			cell11.setBorder(Rectangle.NO_BORDER);
			cell12.setBorder(Rectangle.NO_BORDER);
			cell10.setMinimumHeight(30);
			table.addCell(cell10);
			table.addCell(cell11);
			table.addCell(cell12);
			
			
			PdfPCell cell33 = new PdfPCell(new Phrase("No Trip"));
			PdfPCell cell34 = new PdfPCell(new Phrase(":"));
			PdfPCell cell35 = new PdfPCell(new Phrase(travelSettlementMetadata.getNoTrip()));
			cell33.setBorder(Rectangle.NO_BORDER);
			cell34.setBorder(Rectangle.NO_BORDER);
			cell35.setBorder(Rectangle.NO_BORDER);
			cell33.setMinimumHeight(30);
			table.addCell(cell33);
			table.addCell(cell34);
			table.addCell(cell35);
			
			PdfPCell cell13 = new PdfPCell(new Phrase("Company Code"));
			PdfPCell cell14 = new PdfPCell(new Phrase(":"));
			PdfPCell cell15 = new PdfPCell(new Phrase(travelSettlementMetadata.getCompanyCode()));
			cell13.setBorder(Rectangle.NO_BORDER);
			cell14.setBorder(Rectangle.NO_BORDER);
			cell15.setBorder(Rectangle.NO_BORDER);
			cell13.setMinimumHeight(30);
			table.addCell(cell13);
			table.addCell(cell14);
			table.addCell(cell15);
			
			
			
			
			PdfPCell cell19 = new PdfPCell(new Phrase("Local Scanning Team"));
			PdfPCell cell20 = new PdfPCell(new Phrase(":"));
			PdfPCell cell21 = new PdfPCell(new Phrase(travelSettlementMetadata.getLocalScanningTeam()));
			cell19.setBorder(Rectangle.NO_BORDER);
			cell20.setBorder(Rectangle.NO_BORDER);
			cell21.setBorder(Rectangle.NO_BORDER);
			cell19.setMinimumHeight(30);
			table.addCell(cell19);
			table.addCell(cell20);
			table.addCell(cell21);
			
			PdfPCell cell16 = new PdfPCell(new Phrase("Cost Center"));
			PdfPCell cell17 = new PdfPCell(new Phrase(":"));
			PdfPCell cell18 = new PdfPCell(new Phrase(travelSettlementMetadata.getCostCenter()));
			cell16.setBorder(Rectangle.NO_BORDER);
			cell17.setBorder(Rectangle.NO_BORDER);
			cell18.setBorder(Rectangle.NO_BORDER);
			cell16.setMinimumHeight(30);
			table.addCell(cell16);
			table.addCell(cell17);
			table.addCell(cell18);
			
			Paragraph footerParagraph = new Paragraph (travelSettlementMetadata.getFooterEmail(), fontUntukIsi);
			
			paragraph3.add(table);

			
		 
		
			document.add(paragraph1);
			document.add(paragraph3);
			//document.add(paragraph3);
			document.add(footerParagraph);
			document.close();
			//return document;
		} catch (DocumentException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}catch(IOException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}
		//return null;
	}
	
	public void generateTravelAdvance(TravelAdvanceMetadata travelAdvanceMetadata) {
		try {
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(travelAdvanceMetadata.getNamaFile()));
			 
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
			Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK); 
			Font fontUntukFooter = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
			//Chunk chunk = new Chunk("Formulir Pertanggungjawaban Cash Card", font);
			
			Paragraph paragraph1 = new Paragraph("Form Pengambilan Panjar Dinas", font);
			paragraph1.setFont(font);
			paragraph1.setSpacingAfter(50f);
			paragraph1.setSpacingBefore(72f);
			
			//Paragraph paragraph2 = new Paragraph("Nama : Aryo Pambudi");
			//paragraph2.setFont(fontUntukIsi);
			//paragraph2.setSpacingAfter(72F);
			
			Paragraph paragraph3 = new Paragraph("",fontUntukIsi);
			paragraph3.setAlignment(Paragraph.ALIGN_LEFT);
			paragraph3.setFont(fontUntukIsi);
			
			float[] columnWidths = {5, 1, 10};
			PdfPTable table = new PdfPTable(columnWidths);
			
			
			PdfPCell cellOne = new PdfPCell(new Phrase("Nama Pekerja"));
			PdfPCell cellTwo = new PdfPCell(new Phrase(":"));
			PdfPCell cell3 = new PdfPCell(new Phrase(travelAdvanceMetadata.getNamaPekerja()));
			cellOne.setMinimumHeight(30);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellTwo.setBorder(Rectangle.NO_BORDER);
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3. setColspan(3);
			table.addCell(cellOne);
			table.addCell(cellTwo);
			table.addCell(cell3);
			
			cellOne.setPhrase(new Phrase("Nomer Pekerja"));
			cellTwo.setPhrase(new Phrase(":"));
			cell3.setPhrase(new Phrase(travelAdvanceMetadata.getNomerPekerja()));
			table.addCell(cellOne);
			table.addCell(cellTwo);
			table.addCell(cell3);
			
			PdfPCell cell4 = new PdfPCell(new Phrase("No TRIP"));
			PdfPCell cell5 = new PdfPCell(new Phrase(":"));
			PdfPCell cell6 = new PdfPCell(new Phrase(travelAdvanceMetadata.getNomerTrip()));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell5.setBorder(Rectangle.NO_BORDER);
			cell6.setBorder(Rectangle.NO_BORDER);
			cell4.setMinimumHeight(30);
			table.addCell(cell4);
			table.addCell(cell5);
			table.addCell(cell6);
			
			
			PdfPCell cell7 = new PdfPCell(new Phrase("No. Tiket Request"));
			PdfPCell cell8 = new PdfPCell(new Phrase(":"));
			PdfPCell cell9 = new PdfPCell(new Phrase(travelAdvanceMetadata.getNomerTiket()));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell8.setBorder(Rectangle.NO_BORDER);
			cell9.setBorder(Rectangle.NO_BORDER);
			cell7.setMinimumHeight(30);
			table.addCell(cell7);
			table.addCell(cell8);
			table.addCell(cell9);
			
			PdfPCell cell10 = new PdfPCell(new Phrase("Tujuan"));
			PdfPCell cell11 = new PdfPCell(new Phrase(":"));
			PdfPCell cell12 = new PdfPCell(new Phrase(travelAdvanceMetadata.getTujuan()));
			cell10.setBorder(Rectangle.NO_BORDER);
			cell11.setBorder(Rectangle.NO_BORDER);
			cell12.setBorder(Rectangle.NO_BORDER);
			cell10.setMinimumHeight(30);
			table.addCell(cell10);
			table.addCell(cell11);
			table.addCell(cell12);
			
			PdfPCell cell13 = new PdfPCell(new Phrase("Bank Pengambilan"));
			PdfPCell cell14 = new PdfPCell(new Phrase(":"));
			PdfPCell cell15 = new PdfPCell(new Phrase(travelAdvanceMetadata.getBankPengambilan()));
			cell13.setBorder(Rectangle.NO_BORDER);
			cell14.setBorder(Rectangle.NO_BORDER);
			cell15.setBorder(Rectangle.NO_BORDER);
			cell13.setMinimumHeight(30);
			table.addCell(cell13);
			table.addCell(cell14);
			table.addCell(cell15);
			
			PdfPCell cell16 = new PdfPCell(new Phrase("Jumlah (dalam USD)"));
			PdfPCell cell17 = new PdfPCell(new Phrase(":"));
			PdfPCell cell18 = new PdfPCell(new Phrase(travelAdvanceMetadata.getJumlah()));
			cell16.setBorder(Rectangle.NO_BORDER);
			cell17.setBorder(Rectangle.NO_BORDER);
			cell18.setBorder(Rectangle.NO_BORDER);
			cell16.setMinimumHeight(30);
			table.addCell(cell16);
			table.addCell(cell17);
			table.addCell(cell18);
			
			PdfPCell cell19 = new PdfPCell(new Phrase("Masa Berlaku Form"));
			PdfPCell cell20 = new PdfPCell(new Phrase(":"));
			PdfPCell cell21 = new PdfPCell(new Phrase(travelAdvanceMetadata.getMasaBerlaku()));
			cell19.setBorder(Rectangle.NO_BORDER);
			cell20.setBorder(Rectangle.NO_BORDER);
			cell21.setBorder(Rectangle.NO_BORDER);
			cell19.setMinimumHeight(30);
			table.addCell(cell19);
			table.addCell(cell20);
			table.addCell(cell21);
			
			paragraph3.add(table);

			document.add(paragraph1);
			document.add(paragraph3);
			
			Paragraph paragraph4 = new Paragraph("\n Lampiran dokumen pengambilan Panjar Dinas oleh Pekerja (wajib menunjukkan keseluruhan) : \n", fontUntukFooter);
			paragraph4.add("1. Asli Surat Perjalanan Dinas (SPD) yang ditandatangani pekerja dan salah satu atasan\n");
			paragraph4.add("2. Asli ID Card Pertamina (Kartu Pekerja) yang masih berlaku\n");
			paragraph4.add("3. Asli e-KTP / SIM / Paspor yang masih berlaku\n");
			paragraph4.add("\nLampiran dokumen pengambilan Panjar Dinas oleh Penerima Kuasa (wajib menunjukkan keseluruhan)\n");
			paragraph4.add("4. Dokumen 1 s.d 3 di atas ditambah Surat Kuasa asli bermaterai dan asli e-KTP / SIM / Paspor Penerima Kuasa yang masih berlaku\n");
			paragraph4.setAlignment(Paragraph.ALIGN_LEFT);
			//paragraph4.setFont(fontUntukFooter);
			
			document.add(paragraph4);
			document.close();
			
		}catch (DocumentException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}catch(IOException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}
	}
	
	public void generateTextPdf() {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream("PTM_CashCard_Settlement_new.pdf"));
			 
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
			Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK); 
			//Chunk chunk = new Chunk("Formulir Pertanggungjawaban Cash Card", font);
			
			Paragraph paragraph1 = new Paragraph("Formulir Pertanggungjawaban Cash Card");
			paragraph1.setFont(font);
			paragraph1.setSpacingAfter(50f);
			paragraph1.setSpacingBefore(72f);
			
			Paragraph paragraph2 = new Paragraph("Nomer Tiket : REQ00000111");
			paragraph2.setFont(fontUntukIsi);
			paragraph2.setSpacingAfter(72F);
			
			Paragraph paragraph3 = new Paragraph();
			paragraph3.setAlignment(Paragraph.ALIGN_LEFT);
			paragraph3.setFont(fontUntukIsi);
			
			
			float[] columnWidths = {5, 1, 10};
			PdfPTable table = new PdfPTable(columnWidths);
			
			
			PdfPCell cellOne = new PdfPCell(new Phrase("Nomer Tiket"));
			PdfPCell cellTwo = new PdfPCell(new Phrase(":"));
			PdfPCell cell3 = new PdfPCell(new Phrase("REQ00000111"));
			
			cellOne.setMinimumHeight(30);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellTwo.setBorder(Rectangle.NO_BORDER);
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3. setColspan(3);
			
			//cell3.setFixedHeight(fixedHeight);
			
			table.addCell(cellOne);
			table.addCell(cellTwo);
			table.addCell(cell3);
			
			
			
			PdfPCell cell4 = new PdfPCell(new Phrase("Tanggal"));
			PdfPCell cell5 = new PdfPCell(new Phrase(":"));
			PdfPCell cell6 = new PdfPCell(new Phrase("2 March 2018"));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell5.setBorder(Rectangle.NO_BORDER);
			cell6.setBorder(Rectangle.NO_BORDER);
			cell4.setMinimumHeight(30);
			table.addCell(cell4);
			table.addCell(cell5);
			table.addCell(cell6);
			
			
			PdfPCell cell7 = new PdfPCell(new Phrase("Nomer Pekerja"));
			PdfPCell cell8 = new PdfPCell(new Phrase(":"));
			PdfPCell cell9 = new PdfPCell(new Phrase("749565"));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell8.setBorder(Rectangle.NO_BORDER);
			cell9.setBorder(Rectangle.NO_BORDER);
			cell7.setMinimumHeight(30);
			table.addCell(cell7);
			table.addCell(cell8);
			table.addCell(cell9);
			
			
			
			PdfPCell cell10 = new PdfPCell(new Phrase("Nama  Pekerja"));
			PdfPCell cell11 = new PdfPCell(new Phrase(":"));
			PdfPCell cell12 = new PdfPCell(new Phrase("Aryo Pambudi"));
			cell10.setBorder(Rectangle.NO_BORDER);
			cell11.setBorder(Rectangle.NO_BORDER);
			cell12.setBorder(Rectangle.NO_BORDER);
			cell10.setMinimumHeight(30);
			table.addCell(cell10);
			table.addCell(cell11);
			table.addCell(cell12);
			
			PdfPCell cell22 = new PdfPCell(new Phrase("Nomor PO"));
			PdfPCell cell23 = new PdfPCell(new Phrase(":"));
			PdfPCell cell24 = new PdfPCell(new Phrase("PO12345"));
			cell22.setBorder(Rectangle.NO_BORDER);
			cell23.setBorder(Rectangle.NO_BORDER);
			cell24.setBorder(Rectangle.NO_BORDER);
			cell22.setMinimumHeight(30);
			table.addCell(cell22);
			table.addCell(cell23);
			table.addCell(cell24);
			
			
			PdfPCell cell25 = new PdfPCell(new Phrase("Tanggal PO"));
			PdfPCell cell26 = new PdfPCell(new Phrase(":"));
			PdfPCell cell27 = new PdfPCell(new Phrase("2 March 2018"));
			cell25.setBorder(Rectangle.NO_BORDER);
			cell26.setBorder(Rectangle.NO_BORDER);
			cell27.setBorder(Rectangle.NO_BORDER);
			cell25.setMinimumHeight(30);
			table.addCell(cell25);
			table.addCell(cell26);
			table.addCell(cell27);
			
			
			PdfPCell cell13 = new PdfPCell(new Phrase("Company Code"));
			PdfPCell cell14 = new PdfPCell(new Phrase(":"));
			PdfPCell cell15 = new PdfPCell(new Phrase("1010"));
			cell13.setBorder(Rectangle.NO_BORDER);
			cell14.setBorder(Rectangle.NO_BORDER);
			cell15.setBorder(Rectangle.NO_BORDER);
			cell13.setMinimumHeight(30);
			table.addCell(cell13);
			table.addCell(cell14);
			table.addCell(cell15);
			
			
			PdfPCell cell16 = new PdfPCell(new Phrase("Cost Center"));
			PdfPCell cell17 = new PdfPCell(new Phrase(":"));
			PdfPCell cell18 = new PdfPCell(new Phrase("A1503035"));
			cell16.setBorder(Rectangle.NO_BORDER);
			cell17.setBorder(Rectangle.NO_BORDER);
			cell18.setBorder(Rectangle.NO_BORDER);
			cell16.setMinimumHeight(30);
			table.addCell(cell16);
			table.addCell(cell17);
			table.addCell(cell18);
			
			
			/*
			PdfPCell cell19 = new PdfPCell(new Phrase("Nama Unit"));
			PdfPCell cell20 = new PdfPCell(new Phrase(":"));
			PdfPCell cell21 = new PdfPCell(new Phrase("IT Operation - Customer Service"));
			cell19.setBorder(Rectangle.NO_BORDER);
			cell20.setBorder(Rectangle.NO_BORDER);
			cell21.setBorder(Rectangle.NO_BORDER);
			cell19.setMinimumHeight(30);
			table.addCell(cell19);
			table.addCell(cell20);
			table.addCell(cell21);
			*/
			
			PdfPCell cell19 = new PdfPCell(new Phrase("Local Scanning Team"));
			PdfPCell cell20 = new PdfPCell(new Phrase(":"));
			PdfPCell cell21 = new PdfPCell(new Phrase("Scanning Team Member"));
			cell19.setBorder(Rectangle.NO_BORDER);
			cell20.setBorder(Rectangle.NO_BORDER);
			cell21.setBorder(Rectangle.NO_BORDER);
			cell19.setMinimumHeight(30);
			table.addCell(cell19);
			table.addCell(cell20);
			table.addCell(cell21);
			
			
			
			paragraph3.add(table);

			
		 
		
			document.add(paragraph1);
			document.add(paragraph3);
			//document.add(paragraph3);
			document.close();
		} catch (DocumentException | IOException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}
		
	}
	
	
	public void generateDocument() {
		//Blank Document
		String documentOutput = "ptm_document1.docx";
		XWPFDocument bmcDocument = new XWPFDocument();
		
		XWPFParagraph title = bmcDocument.createParagraph();
		title.setAlignment(ParagraphAlignment.LEFT);
		
		XWPFRun titleRun = title.createRun();
		titleRun.setText("Formulir Pertanggungjawaban Cash Card");
		//titleRun.setColor("009933");
		titleRun.setBold(true);
		titleRun.setFontFamily("Cambria");
		titleRun.setFontSize(14);
		
		//create table
		XWPFTable bmcTable = bmcDocument.createTable();
		
		//create first row
		XWPFTableRow tableRowOne = bmcTable.getRow(0);
		tableRowOne.getCell(0).setText("Nomer Tiket");
		tableRowOne.addNewTableCell().setText(":");
		tableRowOne.addNewTableCell().setText("REQ00000111");
		
		//second row
		XWPFTableRow tableRowTwo = bmcTable.createRow();
		tableRowTwo.getCell(0).setText("Tanggal");
	    tableRowTwo.getCell(1).setText(":");
	    tableRowTwo.getCell(2).setText("2 Februari 2018");
	    
	    //third row
	    XWPFTableRow tableRow3 = bmcTable.createRow();
	    tableRow3.getCell(0).setText("Nomer Pekerja");
	    tableRow3.getCell(1).setText(":");
	    tableRow3.getCell(2).setText("749565");
	    
	    
	  //4 row
	    XWPFTableRow tableRow4 = bmcTable.createRow();
	    tableRow4.getCell(0).setText("Nama Pekerja");
	    tableRow4.getCell(1).setText(":");
	    tableRow4.getCell(2).setText("Aryo Pambudi");
	    
	  //5 row
	    XWPFTableRow tableRow5 = bmcTable.createRow();
	    tableRow5.getCell(0).setText("Company Code");
	    tableRow5.getCell(1).setText(":");
	    tableRow5.getCell(2).setText("1010");
		
	    
	  //5 row
	    XWPFTableRow tableRow6 = bmcTable.createRow();
	    tableRow6.getCell(0).setText("Cost Center");
	    tableRow6.getCell(1).setText(":");
	    tableRow6.getCell(2).setText("A1503035");
	    
	  //5 row
	    XWPFTableRow tableRow7 = bmcTable.createRow();
	    tableRow7.getCell(0).setText("Nama Unit");
	    tableRow7.getCell(1).setText(":");
	    tableRow7.getCell(2).setText("IT Operation - Customer Service");
	    
	  //5 row
	    XWPFTableRow tableRow8 = bmcTable.createRow();
	    tableRow8.getCell(0).setText("Nomor PO");
	    tableRow8.getCell(1).setText(":");
	    tableRow8.getCell(2).setText("PO12345");
	    
	  //5 row
	    XWPFTableRow tableRow9 = bmcTable.createRow();
	    tableRow9.getCell(0).setText("Tanggal PO");
	    tableRow9.getCell(1).setText(":");
	    tableRow9.getCell(2).setText("2 Februari 2018");
		
		try {
			FileOutputStream out = new FileOutputStream(documentOutput);
			bmcDocument.write(out);
			out.close();
			bmcDocument.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/* testing only */
	public void generateTravelAdvanceNew(TravelAdvanceMetadata travelAdvanceMetadata) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(travelAdvanceMetadata.getNamaFile()));
			 
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
			Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK); 
			Font fontUntukIsiBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 11, BaseColor.BLACK); 
			//Font fontUntukFooter = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
			
			Paragraph paragraph1 = new Paragraph(travelAdvanceMetadata.getSubjectEmail(), font);
			paragraph1.setFont(fontUntukIsi);
			paragraph1.setSpacingAfter(30f);
			paragraph1.setSpacingBefore(50f);
			
			paragraph1.add("\n\n\nNama Pekerja");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getNamaPekerja()));
			
			paragraph1.add("\nNomer Pekerja");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getNomerPekerja()));
			
			paragraph1.add("\nNo TRIP");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getNomerTrip()));
			
			paragraph1.add("\nNo. Tiket Request");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getNomerTiket()));
			
			paragraph1.add("\nTujuan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getTujuan()));
			
			paragraph1.add("\nBank Pengambilan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getBankPengambilan()));

			paragraph1.add("\nJumlah (dalam USD)");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": "+travelAdvanceMetadata.getJumlah()));
			
			paragraph1.add("\nMasa Berlaku Form");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": s.d "+travelAdvanceMetadata.getMasaBerlaku()));
			
			paragraph1.add(new Chunk(travelAdvanceMetadata.getNotesEmail()));
			
			Paragraph paragraph2 = new Paragraph("*) Syarat dan Ketentuan:\n" + 
					"Diberlakukan rounding ke nilai terkecil untuk nominal di bawah 1 USD.", fontUntukIsiBold);
			
			
			Paragraph footerParagraph = new Paragraph (travelAdvanceMetadata.getFooterEmail(), fontUntukIsi);
			//paragraph1.add(travelAdvanceMetadata.getFooterEmail());
			/*DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date dateNow = new Date();
			paragraph1.add("\n\n\n\nGenerated automatically from SSC Pertamina workflow system on "+formatDate.format(dateNow));
			paragraph1.add("\nBila ada pertanyaan silakan menghubungi servicedesk.ssc@pertamina.com atau telepon ke (021) 1500234 ext. 3");*/
			
			document.add(paragraph1);
			document.add(paragraph2);
			document.add(footerParagraph);
			document.close();
			
		}catch (DocumentException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}catch(IOException e) {
			System.out.println("FileNotFoundException or DocumentException:"+e);
		}
	}
}
