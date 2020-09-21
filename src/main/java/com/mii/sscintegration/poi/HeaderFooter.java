/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mii.sscintegration.poi;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author MukhlisAj
 */
public class HeaderFooter extends PdfPageEventHelper {

    public void onStartPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
          
//        Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.ITALIC);

            PdfContentByte cb = writer.getDirectContent();

            Font ffont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
            Phrase footer1 = new Phrase("(SP3 ini sudah melalui online approval dan sah tanpa tanda tangan pejabat ybs, dicetak dari sistem BMC)", ffont);
 
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
    }
}