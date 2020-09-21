package com.mii.sscintegration.domain;

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
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mii.sscintegration.controller.RemedyController;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class VIMDocument {

    protected static Logger logger = Logger.getLogger("VIM Document : ");

    public void generatePOHydroCrude(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+financePDF.filename));
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("E:\\Users\\Mukhlish.Aji\\sscintegration\\pertamina-logo.png"), Color.WHITE);
//            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(2);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setFont(null);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);

                // add text
                document.add(header);
            } catch (DocumentException de) {
//              throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\n\n--Crude Information");

            paragraph1.add("\nJenis Crude");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jeniscrude);

            paragraph1.add("\nPelabuhan Tujuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.pelabuhantujuan);

            paragraph1.add("\nPeriode Lifting");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.periodelifting);

            paragraph1.add("\nJumlah Muatan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jumlahmuatan);

            paragraph1.add("\nHarga Satuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.hargasatuan);

            paragraph1.add("\n\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\nDenda");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.denda);

            paragraph1.add("\nCredit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.creditnote);

            paragraph1.add("\nDebit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.debitnote);

            paragraph1.add("\n\nAgar dibayarkan kepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            //Table
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Quantity", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell9 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 10, 20, 10, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell9.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);
            cell9.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);
            tablePermintaan.addCell(cell9);

            int tempLoop = 0;

            while (tempLoop < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(tempLoop);
                tempLoop++;

                cell1 = new PdfPCell(new Phrase("" + tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.quantity, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell9 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                cell9.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
                tablePermintaan.addCell(cell9);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

//        SP3PO SP3PO = sp3POTable.get(0);
//        paragraph1.add("\nNomor PO");
//        paragraph1.add(Chunk.TABBING);
//        paragraph1.add(Chunk.TABBING);
//        paragraph1.add(Chunk.TABBING);
//        paragraph1.add(": "+SP3PO.nomorPO);
            paragraph1.add("\nNomor PO");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorPO);

            paragraph1.add("\nNomor SA / GR");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorSAGR);

            if (financePDF.nilaiSAGRIDR == "") {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRUSD);
            } else if (financePDF.nilaiSAGRIDR == "" && financePDF.nilaiSAGRUSD == "") {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRUSD);
            } else {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRIDR);
            }

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");
            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

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

    public void generatePOHydroNonCrude(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+"SP3Manual_"+financePDF.no+".pdf"));

            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(1);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);

                // add text
                document.add(header);
            } catch (DocumentException de) {
                //            throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\n\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\nDenda");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.denda);

            paragraph1.add("\nCredit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.creditnote);

            paragraph1.add("\nDebit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.debitnote);

            paragraph1.add("\n\nAgar dibayarkan kepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            //Table
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 20, 10, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);

            int tempLoop = 0;

            while (tempLoop < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(tempLoop);
                tempLoop++;

                cell1 = new PdfPCell(new Phrase("" + tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

            paragraph1.add("\nNomor PO");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorPO);

            paragraph1.add("\nNomor SA / GR");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorSAGR);

            if (financePDF.nilaiSAGRIDR == "") {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRUSD);
            } else if (financePDF.nilaiSAGRIDR == "" && financePDF.nilaiSAGRUSD == "") {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRUSD);
            } else {
                paragraph1.add("\nNilai SA / GR");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(": " + financePDF.nilaiSAGRIDR);
            }

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");
            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

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

    public void generateWithPOHydroPassthrough(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+"SP3Manual_"+financePDF.no+".pdf"));

            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(1);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);
                // add text
                document.add(header);
            } catch (DocumentException de) {
                //            throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\n\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\nDenda");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.denda);

            paragraph1.add("\nCredit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.creditnote);

            paragraph1.add("\nDebit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.debitnote);
            paragraph1.add("\n\nAgar dibayarkan kepada :");

            //Table
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 20, 10, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);

            int tempLoop = 0;

            while (tempLoop < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(tempLoop);
                tempLoop++;

                cell1 = new PdfPCell(new Phrase("" + tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

            paragraph1.add("\nNomor PO");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorPO);

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");

            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

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

    public void generateWithoutPONonHydro(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+"SP3Manual_"+financePDF.no+".pdf"));

            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//                Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(1);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);

                // add text
                document.add(header);
            } catch (DocumentException de) {
                //            throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            SP3VendorTable a = sp3vendorTable.get(0);
            paragraph1.add("\nJenis Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + a.jenispembayaran);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\n\nAgar dibayarkan kepada :");

            //Table            
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 20, 10, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);

            int i = 0;
            while (i < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(i);
                i++;

                cell1 = new PdfPCell(new Phrase("" + i, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

            paragraph1.add("\nGL Account");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.GLAccount);

            paragraph1.add("\nCost Center");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + a.costcenter);

            paragraph1.add("\nWBS Element");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + a.wbs);

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");

            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

            RemedyController remedyController = new RemedyController();
            remedyController.sendPdftoWorkInfo(travelokaPdf);
//        
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void generateDownPayment(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+"SP3Manual_"+financePDF.no+".pdf"));

            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(1);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);

                // add text
                document.add(header);
            } catch (DocumentException de) {
                //            throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\n\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\nDenda");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.denda);

            paragraph1.add("\nCredit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.creditnote);

            paragraph1.add("\nDebit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.debitnote);

            paragraph1.add("\n\nAgar dibayarkan kepada :");

            //Table Vendor
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 20, 8, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);

            int tempLoop = 0;

            while (tempLoop < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(tempLoop);
                tempLoop++;

                cell1 = new PdfPCell(new Phrase("" + tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

            paragraph1.add("\nNomor PO");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorPO);

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");
            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

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

    public void generateSubsequent(FinancePDF financePDF, ArrayList<SP3VendorTable> sp3vendorTable, ArrayList<SP3PO> sp3POTable, ArrayList<SP3SAGR> sp3SAGRTable) throws BadElementException, IOException {
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("SP3Manual_" + financePDF.no + ".pdf"));
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+"SP3Manual_"+financePDF.no+".pdf"));

            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.BLACK);

            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
//        	Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
            logo.setAlignment(logo.ALIGN_RIGHT);
            PdfPTable header = new PdfPTable(1);

            try {
                // set defaults
                header.setTotalWidth(520f);
                header.setPaddingTop(-15);
                header.setSpacingAfter(15f);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(60);
                header.getDefaultCell().setBorderColor(BaseColor.WHITE);
                header.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                Barcode128 code128 = new Barcode128();
                code128.setGenerateChecksum(true);
                code128.setCode(financePDF.requstNumber);
                PdfPCell cell1 = new PdfPCell(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
                cell1.setPaddingTop(20);
                cell1.setBorderColor(BaseColor.WHITE);
                header.addCell(cell1);

                PdfPCell cell = new PdfPCell(logo);
                cell.setPaddingLeft(150f);
                cell.setFixedHeight(55);
                cell.setBorderColor(BaseColor.WHITE);
                header.addCell(cell);

                // add text
                document.add(header);
            } catch (DocumentException de) {
                //            throw new ExceptionConverter(de);
            }

            Paragraph paragraph1 = new Paragraph("SURAT PERMINTAAN PROSES PEMBAYARAN", fontHeader);

            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nNo");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nTanggal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nKepada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");
            paragraph1.add("\nDari");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": ");

            paragraph1.add("\n\nTerlampir kami sampaikan dokumen pendukung pembayaran yang terdiri dari ");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.notes);

            paragraph1.add("\n\nJenis Transaksi");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.jenistransaksi);

            paragraph1.add("\nUntuk Pembayaran");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.untukpembayaran);

            paragraph1.add("\nNomor Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorinvoice);

            paragraph1.add("\nTanggal Invoice");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalinvoice);

            paragraph1.add("\nNomor Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorfakturpajak);

            paragraph1.add("\nTanggal Faktur Pajak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.tanggalfakturpajak);

            paragraph1.add("\n\nDue Date");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.duedate);

            paragraph1.add("\nDenda");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.denda);

            paragraph1.add("\nCredit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.creditnote);

            paragraph1.add("\nDebit Note");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.debitnote);

            paragraph1.add("\n\nAgar dibayarkan kepada :");

            //Table Vendor
            PdfPCell cell1 = new PdfPCell(new Phrase("No", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Nomor Vendor", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Nama Vendor", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("NPWP", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Jumlah Pembayaran", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Currency", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Rekening Bank", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Payment", fontTable));
            float[] columnPermintaan = {5, 15, 15, 20, 20, 8, 18, 13};
            PdfPTable tablePermintaan = new PdfPTable(columnPermintaan);
            tablePermintaan.setWidthPercentage(100);

            cell1.setBorder(Rectangle.BOX);
            cell1.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBackgroundColor(new BaseColor(179, 214, 253));
            cell3.setBackgroundColor(new BaseColor(179, 214, 253));
            cell4.setBackgroundColor(new BaseColor(179, 214, 253));
            cell5.setBackgroundColor(new BaseColor(179, 214, 253));
            cell6.setBackgroundColor(new BaseColor(179, 214, 253));
            cell7.setBackgroundColor(new BaseColor(179, 214, 253));
            cell8.setBackgroundColor(new BaseColor(179, 214, 253));
            cell2.setBorder(Rectangle.BOX);
            cell3.setBorder(Rectangle.BOX);
            cell4.setBorder(Rectangle.BOX);
            cell5.setBorder(Rectangle.BOX);
            cell6.setBorder(Rectangle.BOX);
            cell7.setBorder(Rectangle.BOX);
            cell8.setBorder(Rectangle.BOX);

            //cell1.setVerticalAlignment(verticalAlignment);
            tablePermintaan.addCell(cell1);
            tablePermintaan.addCell(cell2);
            tablePermintaan.addCell(cell3);
            tablePermintaan.addCell(cell4);
            tablePermintaan.addCell(cell5);
            tablePermintaan.addCell(cell6);
            tablePermintaan.addCell(cell7);
            tablePermintaan.addCell(cell8);

            int tempLoop = 0;

            while (tempLoop < sp3vendorTable.size()) {
                SP3VendorTable SP3Vendor = sp3vendorTable.get(tempLoop);
                tempLoop++;

                cell1 = new PdfPCell(new Phrase("" + tempLoop, fontTable));
                cell2 = new PdfPCell(new Phrase(SP3Vendor.nomorvendor, fontTable));
                cell3 = new PdfPCell(new Phrase(SP3Vendor.namavendor, fontTable));
                cell4 = new PdfPCell(new Phrase(SP3Vendor.npwp, fontTable));
                cell5 = new PdfPCell(new Phrase(SP3Vendor.nilaiIDR + "\n" + SP3Vendor.nilaiUSD + "\n" + SP3Vendor.nilaiOthers, fontTable));
                cell6 = new PdfPCell(new Phrase(SP3Vendor.currencyIDR + "\n" + SP3Vendor.currencyUSD + "\n" + SP3Vendor.currencyOthers, fontTable));
                cell7 = new PdfPCell(new Phrase(SP3Vendor.rekeningbank, fontTable));
                cell8 = new PdfPCell(new Phrase(SP3Vendor.payment, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                tablePermintaan.addCell(cell1);
                tablePermintaan.addCell(cell2);
                tablePermintaan.addCell(cell3);
                tablePermintaan.addCell(cell4);
                tablePermintaan.addCell(cell5);
                tablePermintaan.addCell(cell6);
                tablePermintaan.addCell(cell7);
                tablePermintaan.addCell(cell8);
            }

            paragraph1.add(tablePermintaan);

            paragraph1.add("\n\nSPB / No Kontrak");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.SPB);

            paragraph1.add("\nNomor PO");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorPO);

            paragraph1.add("\nNomor SA/GR");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.nomorSAGR);

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.companycode);

            paragraph1.add("\nPlant");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.plant);

            paragraph1.add("\nPIC Tagihan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.PICTagihan);

            paragraph1.add("\n\nNomor Tiket");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.no);

            paragraph1.add("\nDisetujui Oleh");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.disetujuioleh);

            paragraph1.add("\nDisetujui Pada");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + financePDF.approvedDate);

            paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

            paragraph1.add("\n\nBersama ini kami menyatakan bahwa transaksi yang ditagihkan ini benar dan absah untuk dibayar dan telah sesuai"
                    + " dengan sistem Tata Kerja dan Prinsip - Prinsip Dasar Integritas Perusahaan serta Prinsip GCG yang dimuat dalam Code of Conduct PT Pertamina (Persero)."
                    + " Dokumen - dokumen terkait yang tidak dilampirkan pada Surat Permintaan Proses Pembayaran ini disimpan ditempat "
                    + " kami dan dapat diperlihatkan kepada Fungsi Keuangan apabila diperlukan.");

            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontFooter);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.add("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)");
            document.add(paragraph2);
            document.close();

            TravelokaPDF travelokaPdf = new TravelokaPDF();
            travelokaPdf.namafile = financePDF.filename;
            travelokaPdf.srInstanceId = financePDF.srInstanceId;
            travelokaPdf.requstNumber = financePDF.requstNumber;

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
