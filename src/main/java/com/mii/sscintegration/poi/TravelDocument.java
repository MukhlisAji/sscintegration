package com.mii.sscintegration.poi;

import com.itextpdf.text.BadElementException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mii.sscintegration.controller.RemedyController;
import com.mii.sscintegration.domain.TravelExpense;
import com.mii.sscintegration.domain.TravelRequestTiket;
import com.mii.sscintegration.domain.TravelokaPDF;
import com.mii.sscintegration.domain.traveloka.BookingTraveloka;
import com.mii.sscintegration.domain.traveloka.SKPDPrintPdf;
import com.mii.sscintegration.domain.traveloka.TravelFlightPrint;
import com.mii.sscintegration.domain.traveloka.TravelHotelPrint;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

public class TravelDocument {

    protected static Logger logger = Logger.getLogger("TravelDocument : ");

    public void generateApprovalTravelPdf(
            TravelokaPDF travelokaPdf,
            SKPDPrintPdf skpdPrintPdf,
            List<BookingTraveloka> listBookingTraveloka) throws BadElementException, IOException {

        Document document = new Document();
        try {

            //format uang rupiah
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("IDR ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            //PdfWriter.getInstance(document, new FileOutputStream("new_fixed_approval.pdf"));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(travelokaPdf.namafile));
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

//          String path = "C:\\\\Users\\\\MukhlisAj\\\\Pictures\\\\pertamina-logo.png";
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

            Paragraph paragraph1 = new Paragraph("APPROVAL REQUEST : TRAVEL SUMMARY REPORT", fontHeader);
            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            if (travelokaPdf.approvalTimeLimit != null) {
                paragraph1.add("\n\nTIME LIMIT");
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(Chunk.TABBING);
                paragraph1.add(new Chunk(": " + travelokaPdf.approvalTimeLimit));
            }

            paragraph1.add("\n\n\nTravel Details");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            //paragraph1.add(new Chunk(": 2 Nop 2018 20:30"));

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.companyCode));

            /*
			paragraph1.add("\nSkema Perjalanan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": Residential"));
             */
            paragraph1.add("\nNama Pekerja");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.pekerja));

            paragraph1.add("\nNo Trip");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.noTrip));

            paragraph1.add("\nCost Center");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + travelokaPdf.alokasiCostCenter);

            paragraph1.add("\nJenis Perjalanan Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.jenisPerjalanan));

            paragraph1.add("\nAlasan Perjalanan Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.alasanPerjalanan));

            paragraph1.add("\nTanggal Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.tanggalDinas + " s/d " + travelokaPdf.pulangDinas));

            paragraph1.add("\nKota Asal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.kotaAsal));

            paragraph1.add("\nKota Tujuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            String kotaTujuan = travelokaPdf.kotatujuan1;
            kotaTujuan += (travelokaPdf.tgltujuan1 == null) ? "" : " (" + travelokaPdf.tgltujuan1 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan2 == null) ? " " : ", " + travelokaPdf.kotatujuan2 + "(" + travelokaPdf.tgltujuan2 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan3 == null) ? " " : ", " + travelokaPdf.kotatujuan3 + "(" + travelokaPdf.tgltujuan3 + ")";
            paragraph1.add(new Chunk(": " + kotaTujuan));
            document.add(paragraph1);
            float[] columnHotel = {15, 15, 10, 10, 10, 10, 15, 15};
            float[] columnFlight = {15, 20, 40, 15};

            PdfPCell cell1 = new PdfPCell(new Phrase("Passanger name", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Airlines", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Source Airport", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("Dest. Airport", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Departure date", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Return Date", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Class", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontUntukIsi);
            paragraph2.add("\nCBT - Flight Information");
            PdfPTable tableFlight = new PdfPTable(columnFlight);
            tableFlight.setWidthPercentage(100);

            Paragraph paragraph3 = new Paragraph();
            paragraph3.setFont(fontUntukIsi);
            paragraph3.add("\n\nCBT - Hotel Information");
            PdfPTable tableHotel = new PdfPTable(columnHotel);
            tableHotel.setWidthPercentage(100);

            //looping booking detail
            int loopingBooking = 0;
            boolean adaBookingPesawat = false;
            boolean adaBookingHotel = false;
            int hotelKe = 0;
            while (listBookingTraveloka.size() > loopingBooking) {
                BookingTraveloka bookingTraveloka = listBookingTraveloka.get(loopingBooking);
                if (bookingTraveloka.productType.equalsIgnoreCase("flight")) {
                    if (!adaBookingPesawat) {
                        cell1 = new PdfPCell(new Phrase("Booking Code", fontTable));
                        cell2 = new PdfPCell(new Phrase("Passenger", fontTable));
                        cell3 = new PdfPCell(new Phrase("Flight Information", fontTable));
                        cell4 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);

                        //cell1.setVerticalAlignment(verticalAlignment);
                        tableFlight.addCell(cell1);
                        tableFlight.addCell(cell2);
                        tableFlight.addCell(cell3);
                        tableFlight.addCell(cell4);
                    }
                    adaBookingPesawat = true;

                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.bookingId, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.travelFlightPrint.get(0).passenger, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.bookingDetail, fontTable));
                    cell4 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    tableFlight.addCell(cell1);
                    tableFlight.addCell(cell2);
                    tableFlight.addCell(cell3);
                    tableFlight.addCell(cell4);
                }

                if (bookingTraveloka.productType.equalsIgnoreCase("hotel")) {
                    //booking hotel
                    if (!adaBookingHotel) {
                        cell1 = new PdfPCell(new Phrase("Guest name", fontTable));
                        cell2 = new PdfPCell(new Phrase("Hotel Name", fontTable));
                        cell3 = new PdfPCell(new Phrase("Hotel city", fontTable));
                        cell4 = new PdfPCell(new Phrase("Country", fontTable));
                        cell5 = new PdfPCell(new Phrase("Check-in date", fontTable));
                        cell6 = new PdfPCell(new Phrase("Night", fontTable));
                        cell7 = new PdfPCell(new Phrase("Price/night", fontTable));
                        cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);
                        cell5.setBorder(Rectangle.BOX);
                        cell6.setBorder(Rectangle.BOX);
                        cell7.setBorder(Rectangle.BOX);
                        cell8.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell6.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell7.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell8.setBackgroundColor(new BaseColor(179, 214, 253));

                        tableHotel.addCell(cell1);
                        tableHotel.addCell(cell2);
                        tableHotel.addCell(cell3);
                        tableHotel.addCell(cell4);
                        tableHotel.addCell(cell5);
                        tableHotel.addCell(cell6);
                        tableHotel.addCell(cell7);
                        tableHotel.addCell(cell8);
                    }
                    adaBookingHotel = true;
                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).guestName, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelName, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCity, fontTable));
                    cell4 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCountry, fontTable));
                    cell5 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).checkInDate, fontTable));
                    cell6 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).nights, fontTable));
                    //cell7 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.travelHotelPrint.get(0).pricePerNight)), fontTable));
                    cell7 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).pricePerNight, fontTable));
                    cell8 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell7.setBorder(Rectangle.BOX);
                    cell8.setBorder(Rectangle.BOX);

                    tableHotel.addCell(cell1);
                    tableHotel.addCell(cell2);
                    tableHotel.addCell(cell3);
                    tableHotel.addCell(cell4);
                    tableHotel.addCell(cell5);
                    tableHotel.addCell(cell6);
                    tableHotel.addCell(cell7);
                    tableHotel.addCell(cell8);
                    hotelKe++;
                }

                loopingBooking++;

            }

            if (adaBookingPesawat) {
                paragraph2.add(tableFlight);
                document.add(paragraph2);
            }

            if (adaBookingHotel) {
                paragraph3.add(tableHotel);
                document.add(paragraph3);
            }

            //detail cost 
            Paragraph paragraph4 = new Paragraph();
            paragraph4.setFont(fontUntukIsi);
            paragraph4.add("\n\n");
            float[] column4 = {30, 30, 30, 10};
            PdfPTable tableCost = new PdfPTable(column4);
            tableCost.setWidthPercentage(100);

            cell1 = new PdfPCell(new Phrase("Total Estimated Cost", fontUntukIsi));
            cell2 = new PdfPCell(new Phrase("", fontUntukIsi));
            cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.totalEcost)), fontUntukIsi));
            //cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.kompensasiHarian)), fontUntukIsi));
            cell4 = new PdfPCell(new Phrase("", fontUntukIsi));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            cell1 = new PdfPCell(new Phrase(" ", fontUntukIsi));
            cell2 = new PdfPCell(new Phrase(" ", fontUntukIsi));
            cell3 = new PdfPCell(new Phrase(" ", fontUntukIsi));
            cell4 = new PdfPCell(new Phrase(" ", fontUntukIsi));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            boolean adaOTA = false;
            int totalBiayaHotel = 0;
            int totalBiayaPesawat = 0;

            if (!skpdPrintPdf.ecostHotel.equalsIgnoreCase("0")) {
                adaOTA = true;
                totalBiayaHotel = Integer.parseInt(skpdPrintPdf.ecostHotel);
                cell1 = new PdfPCell(new Phrase("Hotel (actual)", fontUntukIsi));
                cell2 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.ecostHotel)), fontUntukIsi));
                cell4 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tableCost.addCell(cell1);
                tableCost.addCell(cell2);
                tableCost.addCell(cell3);
                tableCost.addCell(cell4);
            }

            if (!skpdPrintPdf.ecostPesawat.equalsIgnoreCase("0")) {
                totalBiayaPesawat = Integer.parseInt(skpdPrintPdf.ecostPesawat);
                adaOTA = true;
                cell1 = new PdfPCell(new Phrase("Flight (actual)", fontUntukIsi));
                cell2 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.ecostPesawat)), fontUntukIsi));
                cell4 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setBorder(Rectangle.NO_BORDER);

                tableCost.addCell(cell1);
                tableCost.addCell(cell2);
                tableCost.addCell(cell3);
                tableCost.addCell(cell4);
            }

            if (adaOTA) {
                int totalBiayaOTA = totalBiayaHotel + totalBiayaPesawat;
                cell1 = new PdfPCell(new Phrase("Total Actual Cost", fontUntukIsi));
                cell2 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(String.valueOf(totalBiayaOTA))), fontUntukIsi));
                cell4 = new PdfPCell(new Phrase("", fontUntukIsi));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tableCost.addCell(cell1);
                tableCost.addCell(cell2);
                tableCost.addCell(cell3);
                tableCost.addCell(cell4);
            }

            paragraph4.add(tableCost);
            document.add(paragraph4);

            HeaderFooter footer = new HeaderFooter();
            writer.setPageEvent(footer);
            document.close();

            //sending pdf to srm:workinfo
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

    public void generateTravelSettlementPdf(
            TravelokaPDF travelokaPdf,
            List<BookingTraveloka> listBookingTraveloka,
            List<TravelExpense> listTravelExpense,
            List<TravelRequestTiket> listTravelRequestTiket) throws BadElementException, IOException {

        Document document = new Document();
        try {

            //format uang rupiah
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("IDR ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            //PdfWriter.getInstance(document, new FileOutputStream("settlement_detail.pdf"));
            PdfWriter.getInstance(document, new FileOutputStream(travelokaPdf.namafile));
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

            String path = "C:\\Users\\MukhlisAj\\Pictures\\pertamina-logo.png";
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

            Paragraph paragraph1 = new Paragraph("TRAVEL SETTLEMENT SUMMARY REPORT", fontHeader);
            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);
            paragraph1.setSpacingBefore(50f);

            paragraph1.add("\n\n\nTravel Details");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            //paragraph1.add(new Chunk(": 2 Nop 2018 20:30"));

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.companyCode));

            /*
			paragraph1.add("\nSkema Perjalanan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": Residential"));
             */
            paragraph1.add("\nNama Pekerja");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.pekerja));

            paragraph1.add("\nNo Trip");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.noTrip));

            paragraph1.add("\nCost Center");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + travelokaPdf.alokasiCostCenter);

            paragraph1.add("\nJenis Perjalanan Dinas");
            paragraph1.add(new Chunk(": " + travelokaPdf.jenisPerjalanan));

            paragraph1.add("\nTanggal Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.tanggalDinas));

            paragraph1.add("\nKota Asal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.kotaAsal));

            paragraph1.add("\nKota Tujuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            String kotaTujuan = travelokaPdf.kotatujuan1;
            kotaTujuan += (travelokaPdf.tgltujuan1 == null) ? "" : " (" + travelokaPdf.tgltujuan1 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan2 == null) ? " " : ", " + travelokaPdf.kotatujuan2 + "(" + travelokaPdf.tgltujuan2 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan3 == null) ? " " : ", " + travelokaPdf.kotatujuan3 + "(" + travelokaPdf.tgltujuan3 + ")";
            paragraph1.add(new Chunk(": " + kotaTujuan));
            document.add(paragraph1);
            float[] columnHotel = {15, 15, 10, 10, 10, 10, 15, 15};
            float[] columnFlight = {15, 20, 40, 15};

            PdfPCell cell1 = new PdfPCell(new Phrase("Passanger name", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Airlines", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Source Airport", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("Dest. Airport", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Departure date", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Return Date", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Class", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontUntukIsi);
            paragraph2.add("\nCBT - Flight Information");
            PdfPTable tableFlight = new PdfPTable(columnFlight);
            tableFlight.setWidthPercentage(100);

            Paragraph paragraph3 = new Paragraph();
            paragraph3.setFont(fontUntukIsi);
            paragraph3.add("\nCBT - Hotel Information");
            PdfPTable tableHotel = new PdfPTable(columnHotel);
            tableHotel.setWidthPercentage(100);

            //looping booking detail
            int loopingBooking = 0;
            int hotelKe = 0;
            boolean adaBookingPesawat = false;
            boolean adaBookingHotel = false;
            while (listBookingTraveloka.size() > loopingBooking) {
                BookingTraveloka bookingTraveloka = listBookingTraveloka.get(loopingBooking);
                if (bookingTraveloka.productType.equalsIgnoreCase("flight")) {
                    logger.info("booking flight=" + bookingTraveloka.bookingDetail);
                    if (!adaBookingPesawat) {
                        cell1 = new PdfPCell(new Phrase("Booking Code", fontTable));
                        cell2 = new PdfPCell(new Phrase("Passenger", fontTable));
                        cell3 = new PdfPCell(new Phrase("Flight Information", fontTable));
                        cell4 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);

                        //cell1.setVerticalAlignment(verticalAlignment);
                        tableFlight.addCell(cell1);
                        tableFlight.addCell(cell2);
                        tableFlight.addCell(cell3);
                        tableFlight.addCell(cell4);
                    }
                    adaBookingPesawat = true;

                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.bookingId, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.penumpang, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.bookingDetail, fontTable));
                    cell4 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    tableFlight.addCell(cell1);
                    tableFlight.addCell(cell2);
                    tableFlight.addCell(cell3);
                    tableFlight.addCell(cell4);
                }

                if (bookingTraveloka.productType.equalsIgnoreCase("hotel") && hotelKe < bookingTraveloka.travelHotelPrint.size()) {
                    logger.info("booking hotel=" + hotelKe + "=" + bookingTraveloka.bookingDetail);
                    logger.info("total hotel=" + bookingTraveloka.travelHotelPrint.size());
                    //booking hotel
                    if (!adaBookingHotel) {
                        cell1 = new PdfPCell(new Phrase("Guest name", fontTable));
                        cell2 = new PdfPCell(new Phrase("Hotel Name", fontTable));
                        cell3 = new PdfPCell(new Phrase("Hotel city", fontTable));
                        cell4 = new PdfPCell(new Phrase("Country", fontTable));
                        cell5 = new PdfPCell(new Phrase("Check-in date", fontTable));
                        cell6 = new PdfPCell(new Phrase("Night", fontTable));
                        cell7 = new PdfPCell(new Phrase("Price/night", fontTable));
                        cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);
                        cell5.setBorder(Rectangle.BOX);
                        cell6.setBorder(Rectangle.BOX);
                        cell7.setBorder(Rectangle.BOX);
                        cell8.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell6.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell7.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell8.setBackgroundColor(new BaseColor(179, 214, 253));

                        tableHotel.addCell(cell1);
                        tableHotel.addCell(cell2);
                        tableHotel.addCell(cell3);
                        tableHotel.addCell(cell4);
                        tableHotel.addCell(cell5);
                        tableHotel.addCell(cell6);
                        tableHotel.addCell(cell7);
                        tableHotel.addCell(cell8);
                    }
                    adaBookingHotel = true;
                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).guestName, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelName, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCity, fontTable));
                    cell4 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCountry, fontTable));
                    cell5 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).checkInDate, fontTable));
                    cell6 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).nights, fontTable));
                    cell7 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.travelHotelPrint.get(hotelKe).pricePerNight)), fontTable));
                    cell8 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell7.setBorder(Rectangle.BOX);
                    cell8.setBorder(Rectangle.BOX);

                    logger.info("hotelname=" + hotelKe + "=" + bookingTraveloka.travelHotelPrint.get(hotelKe).hotelName);

                    tableHotel.addCell(cell1);
                    tableHotel.addCell(cell2);
                    tableHotel.addCell(cell3);
                    tableHotel.addCell(cell4);
                    tableHotel.addCell(cell5);
                    tableHotel.addCell(cell6);
                    tableHotel.addCell(cell7);
                    tableHotel.addCell(cell8);
                    hotelKe++;
                }

                loopingBooking++;

            }

            if (adaBookingPesawat) {
                paragraph2.add(tableFlight);
                document.add(paragraph2);
            }

            if (adaBookingHotel) {
                paragraph3.add(tableHotel);
                document.add(paragraph3);
            }

            //detail cost 
            Paragraph paragraph4 = new Paragraph();
            paragraph4.setFont(fontUntukIsi);
            paragraph4.add("\nExpense Detail");
            float[] column4 = {15, 30, 30, 25};
            PdfPTable tableCost = new PdfPTable(column4);
            tableCost.setWidthPercentage(100);

            int loopingExpense = 0;
            while (listTravelExpense.size() > loopingExpense) {
                if (loopingExpense == 0) {
                    cell1 = new PdfPCell(new Phrase("Expense", fontTable));
                    cell2 = new PdfPCell(new Phrase("Keterangan", fontTable));
                    cell3 = new PdfPCell(new Phrase("Perhitungan", fontTable));
                    cell4 = new PdfPCell(new Phrase("Total", fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                    tableCost.addCell(cell1);
                    tableCost.addCell(cell2);
                    tableCost.addCell(cell3);
                    tableCost.addCell(cell4);
                }
                TravelExpense travelExpense = listTravelExpense.get(loopingExpense);
                cell1 = new PdfPCell(new Phrase(travelExpense.rincian, fontTable));
                cell2 = new PdfPCell(new Phrase(travelExpense.keterangan, fontTable));
                cell3 = new PdfPCell(new Phrase(travelExpense.pengali, fontTable));
                cell4 = new PdfPCell(new Phrase(travelExpense.jumlahTotal, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tableCost.addCell(cell1);
                tableCost.addCell(cell2);
                tableCost.addCell(cell3);
                tableCost.addCell(cell4);

                loopingExpense++;
            }

            if (loopingExpense > 0) {
                paragraph4.add(tableCost);
                document.add(paragraph4);
            }

            //print history tiket
            Paragraph paragraph5 = new Paragraph();
            paragraph5.setFont(fontUntukIsi);
            paragraph5.add("\n\nRequest History Ticket");
            float[] column6 = {20, 15, 15, 20, 20, 10};
            PdfPTable tableRequset = new PdfPTable(column6);
            tableRequset.setWidthPercentage(100);

            int loopingRequest = 0;
            while (listTravelRequestTiket.size() > loopingRequest) {
                TravelRequestTiket travelRequestTiket = listTravelRequestTiket.get(loopingRequest);
                if (loopingRequest == 0) {
                    cell1 = new PdfPCell(new Phrase("REQ Number", fontTable));
                    cell2 = new PdfPCell(new Phrase("Tanggal Dinas", fontTable));
                    cell3 = new PdfPCell(new Phrase("Pulang Dinas", fontTable));
                    cell4 = new PdfPCell(new Phrase("Tujuan", fontTable));
                    cell5 = new PdfPCell(new Phrase("Jenis Perjalanan", fontTable));
                    cell6 = new PdfPCell(new Phrase("Status", fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell6.setBackgroundColor(new BaseColor(179, 214, 253));

                    tableRequset.addCell(cell1);
                    tableRequset.addCell(cell2);
                    tableRequset.addCell(cell3);
                    tableRequset.addCell(cell4);
                    tableRequset.addCell(cell5);
                    tableRequset.addCell(cell6);
                }

                cell1 = new PdfPCell(new Phrase(travelRequestTiket.requestNumber, fontTable));
                cell2 = new PdfPCell(new Phrase(travelRequestTiket.tanggalDinas, fontTable));
                cell3 = new PdfPCell(new Phrase(travelRequestTiket.pulangDinas, fontTable));
                cell4 = new PdfPCell(new Phrase(travelRequestTiket.kotaAsal + " - " + travelRequestTiket.kotaTujuan, fontTable));
                cell5 = new PdfPCell(new Phrase(travelRequestTiket.jenisPerjalanan, fontTable));
                cell6 = new PdfPCell(new Phrase(travelRequestTiket.statusRequest, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                tableRequset.addCell(cell1);
                tableRequset.addCell(cell2);
                tableRequset.addCell(cell3);
                tableRequset.addCell(cell4);
                tableRequset.addCell(cell5);
                tableRequset.addCell(cell6);

                loopingRequest++;
            }

            paragraph5.add(tableRequset);
            document.add(paragraph5);
            document.close();

            //sending pdf to srm:workinfo
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

    public void generateTravelSettlementPdfLN(
            TravelokaPDF travelokaPdf,
            List<BookingTraveloka> listBookingTraveloka,
            List<TravelExpense> listTravelExpense,
            List<TravelRequestTiket> listTravelRequestTiket) throws BadElementException, IOException {

        Document document = new Document();
        try {

            //format uang rupiah
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("IDR ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            //PdfWriter.getInstance(document, new FileOutputStream("settlement_detail.pdf"));
//            PdfWriter.getInstance(document, new FileOutputStream(travelokaPdf.namafile));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/apache-tomcat-8.5.38/bin/pdf/SP3/"+travelokaPdf.namafile));
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

//          String path = "C:\\\\Users\\\\MukhlisAj\\\\Pictures\\\\pertamina-logo.png";
//            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/ferry.hendrayana/Personal/Workspace/pertamina-logo.png"), Color.WHITE);
            Image logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage("/Users/dewiyu/Desktop/pertamina-logo.png"), Color.WHITE);
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

            Paragraph paragraph1 = new Paragraph("TRAVEL SETTLEMENT SUMMARY REPORT", fontHeader);
            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);

            paragraph1.add("\n\n\nTravel Details");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            //paragraph1.add(new Chunk(": 2 Nop 2018 20:30"));

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.companyCode));

            /*
			paragraph1.add("\nSkema Perjalanan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": Residential"));
             */
            paragraph1.add("\nNama Pekerja");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.pekerja));

            paragraph1.add("\nNo Trip");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.noTrip));

            paragraph1.add("\nCost Center");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + travelokaPdf.alokasiCostCenter);

            paragraph1.add("\nJenis Perjalanan Dinas");
            paragraph1.add(new Chunk(": " + travelokaPdf.jenisPerjalanan));

            paragraph1.add("\nTanggal Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.tanggalDinas));

            paragraph1.add("\nKota Asal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.kotaAsal));

            paragraph1.add("\nKota Tujuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            String kotaTujuan = travelokaPdf.kotatujuan1;
            kotaTujuan += (travelokaPdf.tgltujuan1 == null) ? "" : " (" + travelokaPdf.tgltujuan1 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan2 == null) ? " " : ", " + travelokaPdf.kotatujuan2 + "(" + travelokaPdf.tgltujuan2 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan3 == null) ? " " : ", " + travelokaPdf.kotatujuan3 + "(" + travelokaPdf.tgltujuan3 + ")";
            paragraph1.add(new Chunk(": " + kotaTujuan));
            document.add(paragraph1);
            float[] columnHotel = {15, 15, 10, 10, 10, 10, 15, 15};
            float[] columnFlight = {15, 20, 40, 15};

            PdfPCell cell1 = new PdfPCell(new Phrase("Passanger name", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Airlines", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Source Airport", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("Dest. Airport", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Departure date", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Return Date", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Class", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setFont(fontUntukIsi);
            paragraph2.add("\nCBT - Flight Information");
            PdfPTable tableFlight = new PdfPTable(columnFlight);
            tableFlight.setWidthPercentage(100);

            Paragraph paragraph3 = new Paragraph();
            paragraph3.setFont(fontUntukIsi);
            paragraph3.add("\nCBT - Hotel Information");
            PdfPTable tableHotel = new PdfPTable(columnHotel);
            tableHotel.setWidthPercentage(100);

            //looping booking detail
            int loopingBooking = 0;
            int hotelKe = 0;
            boolean adaBookingPesawat = false;
            boolean adaBookingHotel = false;
            while (listBookingTraveloka.size() > loopingBooking) {
                BookingTraveloka bookingTraveloka = listBookingTraveloka.get(loopingBooking);
                if (bookingTraveloka.productType.equalsIgnoreCase("flight")) {
                    logger.info("booking flight=" + bookingTraveloka.bookingDetail);
                    if (!adaBookingPesawat) {
                        cell1 = new PdfPCell(new Phrase("Booking Code", fontTable));
                        cell2 = new PdfPCell(new Phrase("Passenger", fontTable));
                        cell3 = new PdfPCell(new Phrase("Flight Information", fontTable));
                        cell4 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);

                        //cell1.setVerticalAlignment(verticalAlignment);
                        tableFlight.addCell(cell1);
                        tableFlight.addCell(cell2);
                        tableFlight.addCell(cell3);
                        tableFlight.addCell(cell4);
                    }
                    adaBookingPesawat = true;

                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.bookingId, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.penumpang, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.bookingDetail, fontTable));
                    cell4 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    tableFlight.addCell(cell1);
                    tableFlight.addCell(cell2);
                    tableFlight.addCell(cell3);
                    tableFlight.addCell(cell4);
                }

                if (bookingTraveloka.productType.equalsIgnoreCase("hotel") && hotelKe < bookingTraveloka.travelHotelPrint.size()) {
                    logger.info("booking hotel=" + hotelKe + "=" + bookingTraveloka.bookingDetail);
                    logger.info("total hotel=" + bookingTraveloka.travelHotelPrint.size());
                    //booking hotel
                    if (!adaBookingHotel) {
                        cell1 = new PdfPCell(new Phrase("Guest name", fontTable));
                        cell2 = new PdfPCell(new Phrase("Hotel Name", fontTable));
                        cell3 = new PdfPCell(new Phrase("Hotel city", fontTable));
                        cell4 = new PdfPCell(new Phrase("Country", fontTable));
                        cell5 = new PdfPCell(new Phrase("Check-in date", fontTable));
                        cell6 = new PdfPCell(new Phrase("Night", fontTable));
                        cell7 = new PdfPCell(new Phrase("Price/night", fontTable));
                        cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));
                        cell1.setBorder(Rectangle.BOX);
                        cell2.setBorder(Rectangle.BOX);
                        cell3.setBorder(Rectangle.BOX);
                        cell4.setBorder(Rectangle.BOX);
                        cell5.setBorder(Rectangle.BOX);
                        cell6.setBorder(Rectangle.BOX);
                        cell7.setBorder(Rectangle.BOX);
                        cell8.setBorder(Rectangle.BOX);
                        cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell6.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell7.setBackgroundColor(new BaseColor(179, 214, 253));
                        cell8.setBackgroundColor(new BaseColor(179, 214, 253));

                        tableHotel.addCell(cell1);
                        tableHotel.addCell(cell2);
                        tableHotel.addCell(cell3);
                        tableHotel.addCell(cell4);
                        tableHotel.addCell(cell5);
                        tableHotel.addCell(cell6);
                        tableHotel.addCell(cell7);
                        tableHotel.addCell(cell8);
                    }
                    adaBookingHotel = true;
                    cell1 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).guestName, fontTable));
                    cell2 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelName, fontTable));
                    cell3 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCity, fontTable));
                    cell4 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).hotelCountry, fontTable));
                    cell5 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).checkInDate, fontTable));
                    cell6 = new PdfPCell(new Phrase(bookingTraveloka.travelHotelPrint.get(hotelKe).nights, fontTable));
                    cell7 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.travelHotelPrint.get(hotelKe).pricePerNight)), fontTable));
                    cell8 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell7.setBorder(Rectangle.BOX);
                    cell8.setBorder(Rectangle.BOX);

                    logger.info("hotelname=" + hotelKe + "=" + bookingTraveloka.travelHotelPrint.get(hotelKe).hotelName);

                    tableHotel.addCell(cell1);
                    tableHotel.addCell(cell2);
                    tableHotel.addCell(cell3);
                    tableHotel.addCell(cell4);
                    tableHotel.addCell(cell5);
                    tableHotel.addCell(cell6);
                    tableHotel.addCell(cell7);
                    tableHotel.addCell(cell8);
                    hotelKe++;
                }

                loopingBooking++;

            }

            if (adaBookingPesawat) {
                paragraph2.add(tableFlight);
                document.add(paragraph2);
            }

            if (adaBookingHotel) {
                paragraph3.add(tableHotel);
                document.add(paragraph3);
            }

            //detail cost 
            Paragraph paragraph4 = new Paragraph();
            paragraph4.setFont(fontUntukIsi);
            paragraph4.add("\nExpense Detail");
            float[] column4 = {15, 30, 30, 25};
            float[] column5 = {20, 30, 20, 20, 10};
            PdfPTable tableCost = new PdfPTable(column5);
            tableCost.setWidthPercentage(100);

            int loopingExpense = 0;
            while (listTravelExpense.size() > loopingExpense) {
                if (loopingExpense == 0) {
                    cell1 = new PdfPCell(new Phrase("Expense", fontTable));
                    cell2 = new PdfPCell(new Phrase("Keterangan", fontTable));
                    cell3 = new PdfPCell(new Phrase("Perhitungan", fontTable));
                    cell4 = new PdfPCell(new Phrase("Total IDR", fontTable));
                    cell5 = new PdfPCell(new Phrase("Total USD", fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                    tableCost.addCell(cell1);
                    tableCost.addCell(cell2);
                    tableCost.addCell(cell3);
                    tableCost.addCell(cell4);
                    tableCost.addCell(cell5);
                }
                TravelExpense travelExpense = listTravelExpense.get(loopingExpense);
                cell1 = new PdfPCell(new Phrase(travelExpense.rincian, fontTable));
                cell2 = new PdfPCell(new Phrase(travelExpense.keterangan, fontTable));
                cell3 = new PdfPCell(new Phrase(travelExpense.pengali, fontTable));
                cell4 = new PdfPCell(new Phrase(travelExpense.jumlahTotal, fontTable));
                cell5 = new PdfPCell(new Phrase(travelExpense.jumlahUSD, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tableCost.addCell(cell1);
                tableCost.addCell(cell2);
                tableCost.addCell(cell3);
                tableCost.addCell(cell4);
                tableCost.addCell(cell5);

                loopingExpense++;
            }

            if (loopingExpense > 0) {
                paragraph4.add(tableCost);
                document.add(paragraph4);
            }

            //print history tiket
            Paragraph paragraph5 = new Paragraph();
            paragraph5.setFont(fontUntukIsi);
            paragraph5.add("\n\nRequest History Ticket");
            float[] column6 = {20, 15, 15, 20, 20, 10};
            PdfPTable tableRequset = new PdfPTable(column6);
            tableRequset.setWidthPercentage(100);

            int loopingRequest = 0;
            while (listTravelRequestTiket.size() > loopingRequest) {
                TravelRequestTiket travelRequestTiket = listTravelRequestTiket.get(loopingRequest);
                if (loopingRequest == 0) {
                    cell1 = new PdfPCell(new Phrase("REQ Number", fontTable));
                    cell2 = new PdfPCell(new Phrase("Tanggal Dinas", fontTable));
                    cell3 = new PdfPCell(new Phrase("Pulang Dinas", fontTable));
                    cell4 = new PdfPCell(new Phrase("Tujuan", fontTable));
                    cell5 = new PdfPCell(new Phrase("Jenis Perjalanan", fontTable));
                    cell6 = new PdfPCell(new Phrase("Status", fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                    cell6.setBackgroundColor(new BaseColor(179, 214, 253));

                    tableRequset.addCell(cell1);
                    tableRequset.addCell(cell2);
                    tableRequset.addCell(cell3);
                    tableRequset.addCell(cell4);
                    tableRequset.addCell(cell5);
                    tableRequset.addCell(cell6);
                }

                cell1 = new PdfPCell(new Phrase(travelRequestTiket.requestNumber, fontTable));
                cell2 = new PdfPCell(new Phrase(travelRequestTiket.tanggalDinas, fontTable));
                cell3 = new PdfPCell(new Phrase(travelRequestTiket.pulangDinas, fontTable));
                cell4 = new PdfPCell(new Phrase(travelRequestTiket.kotaAsal + " - " + travelRequestTiket.kotaTujuan, fontTable));
                cell5 = new PdfPCell(new Phrase(travelRequestTiket.jenisPerjalanan, fontTable));
                cell6 = new PdfPCell(new Phrase(travelRequestTiket.statusRequest, fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                tableRequset.addCell(cell1);
                tableRequset.addCell(cell2);
                tableRequset.addCell(cell3);
                tableRequset.addCell(cell4);
                tableRequset.addCell(cell5);
                tableRequset.addCell(cell6);

                loopingRequest++;
            }

            paragraph5.add(tableRequset);
            document.add(paragraph5);
            document.close();

            //sending pdf to srm:workinfo
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

    public void generateApprovalNew(
            TravelokaPDF travelokaPdf,
            TravelHotelPrint travelHotelPrint,
            TravelFlightPrint travelFlightPrint,
            SKPDPrintPdf skpdPrintPdf,
            BookingTraveloka bookingTraveloka) {

        Document document = new Document();
        try {

            //format uang rupiah
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("IDR ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            //PdfWriter.getInstance(document, new FileOutputStream("testing_approval.pdf"));
            PdfWriter.getInstance(document, new FileOutputStream(travelokaPdf.noTrip + ".pdf"));
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK);
            Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

            Paragraph paragraph1 = new Paragraph("APPROVAL REQUEST : TRAVEL SUMMARY REPORT", fontHeader);
            paragraph1.setFont(fontUntukIsi);
            paragraph1.setSpacingAfter(30f);
            paragraph1.setSpacingBefore(50f);

            paragraph1.add("\n\nTIME LIMIT");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.approvalTimeLimit));

            paragraph1.add("\n\n\nTravel Details");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            //paragraph1.add(new Chunk(": 2 Nop 2018 20:30"));

            paragraph1.add("\nCompany Code");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.companyCode));

            /*
			paragraph1.add("\nSkema Perjalanan");
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(Chunk.TABBING);
			paragraph1.add(new Chunk(": Residential"));
             */
            paragraph1.add("\nNama Pekerja");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.pekerja));

            paragraph1.add("\nNo Trip");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.noTrip));

            paragraph1.add("\nCost Center");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(": " + travelokaPdf.alokasiCostCenter);

            paragraph1.add("\nJenis Perjalanan Dinas");
            paragraph1.add(new Chunk(": " + travelokaPdf.jenisPerjalanan));

            paragraph1.add("\nTanggal Dinas");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.tanggalDinas + " s/d " + travelokaPdf.pulangDinas));

            paragraph1.add("\nKota Asal");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(new Chunk(": " + travelokaPdf.kotaAsal));

            paragraph1.add("\nKota Tujuan");
            paragraph1.add(Chunk.TABBING);
            paragraph1.add(Chunk.TABBING);
            String kotaTujuan = travelokaPdf.kotatujuan1 + " (" + travelokaPdf.tgltujuan1 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan2 == null) ? " " : ", " + travelokaPdf.kotatujuan2 + "(" + travelokaPdf.tgltujuan2 + ")";
            kotaTujuan += (travelokaPdf.kotatujuan3 == null) ? " " : ", " + travelokaPdf.kotatujuan3 + "(" + travelokaPdf.tgltujuan3 + ")";
            paragraph1.add(new Chunk(": " + kotaTujuan));
            document.add(paragraph1);
            float[] columnHotel = {15, 15, 10, 10, 10, 10, 15, 15};
            float[] columnFlight = {15, 20, 40, 15};

            PdfPCell cell1 = new PdfPCell(new Phrase("Passanger name", fontTable));
            PdfPCell cell2 = new PdfPCell(new Phrase("Airlines", fontTable));
            PdfPCell cell3 = new PdfPCell(new Phrase("Source Airport", fontTable));
            PdfPCell cell4 = new PdfPCell(new Phrase("Dest. Airport", fontTable));
            PdfPCell cell5 = new PdfPCell(new Phrase("Departure date", fontTable));
            PdfPCell cell6 = new PdfPCell(new Phrase("Return Date", fontTable));
            PdfPCell cell7 = new PdfPCell(new Phrase("Class", fontTable));
            PdfPCell cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));

            //jika ada flight 
            if (travelokaPdf.listFlight != null) {
                Paragraph paragraph2 = new Paragraph();
                paragraph2.setFont(fontUntukIsi);
                paragraph2.add("\nFlight Information");

                PdfPTable table = new PdfPTable(columnFlight);
                table.setWidthPercentage(100);

                cell1 = new PdfPCell(new Phrase("Booking Code", fontTable));
                cell2 = new PdfPCell(new Phrase("Passenger", fontTable));
                cell3 = new PdfPCell(new Phrase("Flight Information", fontTable));
                cell4 = new PdfPCell(new Phrase("Total Fare", fontTable));

                cell1.setBorder(Rectangle.BOX);
                cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);

                //cell1.setVerticalAlignment(verticalAlignment);
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);

                int loopingFlight = 0;
                //while(travelokaPdf.listFlight.size()>loopingFlight) {
                TravelFlightPrint travelFlight = new TravelFlightPrint();
                travelFlight = travelokaPdf.listFlight.get(loopingFlight);

                cell1 = new PdfPCell(new Phrase(bookingTraveloka.bookingId, fontTable));
                cell2 = new PdfPCell(new Phrase(travelFlight.passenger, fontTable));

                cell3 = new PdfPCell(new Phrase(bookingTraveloka.bookingDetail, fontTable));
                cell4 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);

                loopingFlight++;
                //}
                paragraph2.add(table);
                document.add(paragraph2);
            }

            //jika ada hotel
            if (travelokaPdf.listHotel != null) {
                Paragraph paragraph3 = new Paragraph();
                paragraph3.setFont(fontUntukIsi);
                paragraph3.add("\n\nHotel Information");
                //float[] columnWidths2 = { 8, 8, 8, 8, 8, 8, 8, 8 };
                PdfPTable tableHotel = new PdfPTable(columnHotel);
                tableHotel.setWidthPercentage(100);
                cell1 = new PdfPCell(new Phrase("Guest name", fontTable));
                cell2 = new PdfPCell(new Phrase("Hotel Name", fontTable));
                cell3 = new PdfPCell(new Phrase("Hotel city", fontTable));
                cell4 = new PdfPCell(new Phrase("Country", fontTable));
                cell5 = new PdfPCell(new Phrase("Check-in date", fontTable));
                cell6 = new PdfPCell(new Phrase("Night", fontTable));
                cell7 = new PdfPCell(new Phrase("Price/night", fontTable));
                cell8 = new PdfPCell(new Phrase("Total Fare", fontTable));
                cell1.setBorder(Rectangle.BOX);
                cell2.setBorder(Rectangle.BOX);
                cell3.setBorder(Rectangle.BOX);
                cell4.setBorder(Rectangle.BOX);
                cell5.setBorder(Rectangle.BOX);
                cell6.setBorder(Rectangle.BOX);
                cell7.setBorder(Rectangle.BOX);
                cell8.setBorder(Rectangle.BOX);
                cell1.setBackgroundColor(new BaseColor(179, 214, 253));
                cell2.setBackgroundColor(new BaseColor(179, 214, 253));
                cell3.setBackgroundColor(new BaseColor(179, 214, 253));
                cell4.setBackgroundColor(new BaseColor(179, 214, 253));
                cell5.setBackgroundColor(new BaseColor(179, 214, 253));
                cell6.setBackgroundColor(new BaseColor(179, 214, 253));
                cell7.setBackgroundColor(new BaseColor(179, 214, 253));
                cell8.setBackgroundColor(new BaseColor(179, 214, 253));

                tableHotel.addCell(cell1);
                tableHotel.addCell(cell2);
                tableHotel.addCell(cell3);
                tableHotel.addCell(cell4);
                tableHotel.addCell(cell5);
                tableHotel.addCell(cell6);
                tableHotel.addCell(cell7);
                tableHotel.addCell(cell8);

                int loopingHotel = 0;
                while (travelokaPdf.listHotel.size() > loopingHotel) {
                    TravelHotelPrint travelHotel = new TravelHotelPrint();
                    travelHotel = travelokaPdf.listHotel.get(loopingHotel);

                    cell1 = new PdfPCell(new Phrase(travelHotel.guestName, fontTable));
                    cell2 = new PdfPCell(new Phrase(travelHotel.hotelName, fontTable));
                    cell3 = new PdfPCell(new Phrase(travelHotel.hotelCity, fontTable));
                    cell4 = new PdfPCell(new Phrase(travelHotel.hotelCountry, fontTable));
                    cell5 = new PdfPCell(new Phrase(travelHotel.checkInDate, fontTable));
                    cell6 = new PdfPCell(new Phrase(travelHotel.nights, fontTable));
                    cell7 = new PdfPCell(new Phrase(travelHotel.pricePerNight, fontTable));
                    cell8 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)), fontTable));
                    cell1.setBorder(Rectangle.BOX);
                    cell2.setBorder(Rectangle.BOX);
                    cell3.setBorder(Rectangle.BOX);
                    cell4.setBorder(Rectangle.BOX);
                    cell5.setBorder(Rectangle.BOX);
                    cell6.setBorder(Rectangle.BOX);
                    cell7.setBorder(Rectangle.BOX);
                    cell8.setBorder(Rectangle.BOX);

                    tableHotel.addCell(cell1);
                    tableHotel.addCell(cell2);
                    tableHotel.addCell(cell3);
                    tableHotel.addCell(cell4);
                    tableHotel.addCell(cell5);
                    tableHotel.addCell(cell6);
                    tableHotel.addCell(cell7);
                    tableHotel.addCell(cell8);

                    loopingHotel++;
                }
                paragraph3.add(tableHotel);
                document.add(paragraph3);
            }

            //detail cost 
            Paragraph paragraph4 = new Paragraph();
            paragraph4.setFont(fontUntukIsi);
            paragraph4.add("\n\nTotal Cost");
            float[] column4 = {30, 20, 20, 30};
            PdfPTable tableCost = new PdfPTable(column4);
            tableCost.setWidthPercentage(100);

            cell1 = new PdfPCell(new Phrase("Lumpsum Harian (est.)", fontTable));
            cell2 = new PdfPCell(new Phrase(skpdPrintPdf.pengaliKompensasi, fontTable));
            cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.kompensasiHarian)), fontTable));
            cell4 = new PdfPCell(new Phrase("", fontTable));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Hotel (actual)", fontTable));
            cell2 = new PdfPCell(new Phrase("", fontTable));
            cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.ecostHotel)), fontTable));
            cell4 = new PdfPCell(new Phrase("", fontTable));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Flight (actual)", fontTable));
            cell2 = new PdfPCell(new Phrase("", fontTable));
            cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.ecostPesawat)), fontTable));
            cell4 = new PdfPCell(new Phrase("", fontTable));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorder(Rectangle.NO_BORDER);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Total Biaya", fontTable));
            cell2 = new PdfPCell(new Phrase("", fontTable));
            cell3 = new PdfPCell(new Phrase(kursIndonesia.format(Double.parseDouble(skpdPrintPdf.totalEcost)), fontTable));
            cell4 = new PdfPCell(new Phrase("", fontTable));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell3.setVerticalAlignment(Element.ALIGN_RIGHT);

            tableCost.addCell(cell1);
            tableCost.addCell(cell2);
            tableCost.addCell(cell3);
            tableCost.addCell(cell4);

            paragraph4.add(tableCost);
            document.add(paragraph4);

            document.close();

            //sending pdf to srm:workinfo
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

    public void generateApprovalPdf(
            TravelokaPDF travelokaPdf,
            TravelHotelPrint travelHotelPrint,
            TravelFlightPrint travelFlightPrint,
            SKPDPrintPdf skpdPrintPdf) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(travelokaPdf.noTrip + ".pdf"));
            document.open();
            //document.setO
            Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);
            Font fontUntukIsi = FontFactory.getFont(FontFactory.COURIER, 6, BaseColor.BLACK);
            Font fontTableHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 8, BaseColor.BLACK);

            Paragraph paragraph1 = new Paragraph("Approval or Payment Time Limit : " + travelokaPdf.approvalTimeLimit, fontHeader);
            //paragraph1.setFont(fontHeader);
            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph2.setFont(fontUntukIsi);

            float[] columnWidths = {10, 10, 10, 10};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            //PdfPCell cell0 = new PdfPCell(new Phrase(" "));
            PdfPCell cell1 = new PdfPCell(new Phrase("No Trip"));
            PdfPCell cell2 = new PdfPCell(new Phrase(travelokaPdf.noTrip));
            PdfPCell cell3 = new PdfPCell(new Phrase("Jenis Perjalanan Dinas"));
            PdfPCell cell4 = new PdfPCell(new Phrase(travelokaPdf.jenisPerjalanan));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Pekerja"));
            cell2 = new PdfPCell(new Phrase(travelokaPdf.pekerja));
            cell3 = new PdfPCell(new Phrase("Sifat Perjalanan Dinas"));
            cell4 = new PdfPCell(new Phrase(travelokaPdf.sifatPerjalanan));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Tanggal Dinas"));
            cell2 = new PdfPCell(new Phrase(travelokaPdf.tanggalDinas));
            cell3 = new PdfPCell(new Phrase("Alokasi Cost Centre"));
            cell4 = new PdfPCell(new Phrase(travelokaPdf.alokasiCostCenter));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Pulang Dinas"));
            cell2 = new PdfPCell(new Phrase(travelokaPdf.pulangDinas));
            cell3 = new PdfPCell(new Phrase("Total Estimated Cost (Budget)"));
            cell4 = new PdfPCell(new Phrase(skpdPrintPdf.totalEcost));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Alasan Perjalanan Dinas"));
            cell2 = new PdfPCell(new Phrase(skpdPrintPdf.alasanPerjalanan));
            cell3 = new PdfPCell(new Phrase("Total Biaya Online Travel Agent (OTA)"));
            cell4 = new PdfPCell(new Phrase(travelokaPdf.totalBiayaOTA));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            paragraph2.add(table);
            document.add(paragraph2);

            Paragraph paragraph3 = new Paragraph("\n\nRincian Perjalanan");
            paragraph3.setFont(fontUntukIsi);
            document.add(paragraph3);

            //float[] columnWidths = { 10, 10, 10, 10 };
            PdfPTable table2 = new PdfPTable(columnWidths);
            table2.setWidthPercentage(100);

            Paragraph paragraph4 = new Paragraph();
            paragraph4.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph4.setFont(fontUntukIsi);

            cell1 = new PdfPCell(new Phrase("Kota Asal"));
            cell2 = new PdfPCell(new Phrase(travelokaPdf.kotaAsal));
            cell3 = new PdfPCell(new Phrase(" "));
            cell4 = new PdfPCell(new Phrase(" "));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table2.addCell(cell1);
            table2.addCell(cell2);
            table2.addCell(cell3);
            table2.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Kota Tujuan 1"));
            cell2 = new PdfPCell(new Phrase(travelokaPdf.kotatujuan1));
            cell3 = new PdfPCell(new Phrase("Tanggal Keberangkatan"));
            cell4 = new PdfPCell(new Phrase(travelokaPdf.tanggalDinas));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table2.addCell(cell1);
            table2.addCell(cell2);
            table2.addCell(cell3);
            table2.addCell(cell4);

            if (travelokaPdf.kotatujuan2 != null) {
                cell1 = new PdfPCell(new Phrase("Kota Tujuan 2"));
                cell2 = new PdfPCell(new Phrase(travelokaPdf.kotatujuan2));
                cell3 = new PdfPCell(new Phrase("Tanggal Keberangkatan"));
                cell4 = new PdfPCell(new Phrase(travelokaPdf.tgltujuan2));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell4.setBorder(Rectangle.NO_BORDER);
                table2.addCell(cell1);
                table2.addCell(cell2);
                table2.addCell(cell3);
                table2.addCell(cell4);
            }

            if (travelokaPdf.kotatujuan3 != null) {
                cell1 = new PdfPCell(new Phrase("Kota Tujuan 3"));
                cell2 = new PdfPCell(new Phrase(travelokaPdf.kotatujuan3));
                cell3 = new PdfPCell(new Phrase("Tanggal Keberangkatan"));
                cell4 = new PdfPCell(new Phrase(travelokaPdf.tgltujuan3));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell4.setBorder(Rectangle.NO_BORDER);
                table2.addCell(cell1);
                table2.addCell(cell2);
                table2.addCell(cell3);
                table2.addCell(cell4);
            }

            paragraph4.add(table2);
            document.add(paragraph4);

            Paragraph paragraph5 = new Paragraph("\n\nInformasi Booking OTA");
            paragraph5.setFont(fontUntukIsi);
            document.add(paragraph5);

            PdfPTable table3 = new PdfPTable(columnWidths);
            table3.setWidthPercentage(100);

            Paragraph paragraph6 = new Paragraph();
            paragraph6.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph6.setFont(fontUntukIsi);

            cell1 = new PdfPCell(new Phrase("Type"));
            cell2 = new PdfPCell(new Phrase("Hotel"));
            cell3 = new PdfPCell(new Phrase("Type"));
            cell4 = new PdfPCell(new Phrase("Flight"));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Check-in date"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.checkInDate));
            cell3 = new PdfPCell(new Phrase("Source Airport"));
            cell4 = new PdfPCell(new Phrase(travelFlightPrint.sourceAirport));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Nights"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.nights));
            cell3 = new PdfPCell(new Phrase("Destination Airport"));
            cell4 = new PdfPCell(new Phrase(travelFlightPrint.destinationAirport));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Hotel name"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.hotelName));
            cell3 = new PdfPCell(new Phrase("Departure Date"));
            cell4 = new PdfPCell(new Phrase(travelFlightPrint.departureDate));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Hotel City"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.hotelCity));
            cell3 = new PdfPCell(new Phrase("Return Date"));
            cell4 = new PdfPCell(new Phrase(travelFlightPrint.returnDate));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Hotel Country"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.hotelCountry));
            cell3 = new PdfPCell(new Phrase("Seat Class"));
            cell4 = new PdfPCell(new Phrase(travelFlightPrint.seatClass));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("#rooms"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.rooms));
            cell3 = new PdfPCell(new Phrase("Details of Adult Passengers"));
            cell4 = new PdfPCell(new Phrase(" "));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Guest names "));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.guestName));
            cell3 = new PdfPCell(new Phrase(" "));
            cell4 = new PdfPCell(new Phrase(" "));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            cell1 = new PdfPCell(new Phrase("Price Per Night"));
            cell2 = new PdfPCell(new Phrase(travelHotelPrint.pricePerNight));
            cell3 = new PdfPCell(new Phrase("Total Fare"));
            cell4 = new PdfPCell(new Phrase(" "));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);
            table3.addCell(cell4);

            paragraph6.add(table3);
            document.add(paragraph6);

            Paragraph paragraph7 = new Paragraph("\n\nRincian Biaya Estimated Cost (Budget)");
            paragraph7.setFont(fontUntukIsi);
            document.add(paragraph7);

            float[] column3Widths = {10, 10, 10};
            PdfPTable table4 = new PdfPTable(column3Widths);
            table4.setWidthPercentage(100);

            Paragraph paragraph8 = new Paragraph();
            paragraph8.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph8.setFont(fontUntukIsi);

            cell1 = new PdfPCell(new Phrase("Estimasi Lumpsum Harian "));
            cell2 = new PdfPCell(new Phrase(skpdPrintPdf.pengaliKompensasi));
            cell3 = new PdfPCell(new Phrase(skpdPrintPdf.kompensasiHarian));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell1);
            table4.addCell(cell2);
            table4.addCell(cell3);

            cell1 = new PdfPCell(new Phrase("Estimasi Hotel"));
            cell2 = new PdfPCell(new Phrase(skpdPrintPdf.pengaliHotel));
            cell3 = new PdfPCell(new Phrase(skpdPrintPdf.ecostHotel));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell1);
            table4.addCell(cell2);
            table4.addCell(cell3);

            cell1 = new PdfPCell(new Phrase("Estimasi Tiket Pesawat "));
            cell2 = new PdfPCell(new Phrase(skpdPrintPdf.pengaliPesawat));
            cell3 = new PdfPCell(new Phrase(skpdPrintPdf.ecostPesawat));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell1);
            table4.addCell(cell2);
            table4.addCell(cell3);

            cell1 = new PdfPCell(new Phrase("Total Estimated Cost"));
            cell2 = new PdfPCell(new Phrase(""));
            cell3 = new PdfPCell(new Phrase(skpdPrintPdf.totalEcost));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell3.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell1);
            table4.addCell(cell2);
            table4.addCell(cell3);

            paragraph8.add(table4);
            document.add(paragraph8);

            document.close();

            //sending pdf to srm:workinfo
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
