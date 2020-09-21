package com.mii.sscintegration.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.itextpdf.text.BadElementException;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.FinancePDF;
import com.mii.sscintegration.domain.SP3PO;
import com.mii.sscintegration.domain.SP3SAGR;
import com.mii.sscintegration.domain.SP3VendorTable;
import com.mii.sscintegration.domain.VIMDocument;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FinanceController {

    protected static Logger logger = Logger.getLogger("Finance PDF Controller: ");

    public String getValueFromRemedy(Entry assetRecord, Object fieldID) {
        if (assetRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return assetRecord.get(fieldID).getValue().toString();
    }

    @RequestMapping(value = "/printPOHydroCrude")
    public String POHydroCrude() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'GUID'=\"IDGAA5V0FMUS3AQJGN6CQIIMTMBBKM\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'E-SP3 Type*'=\"0\" AND 'status print' = \"0\" ");
        String GUID;
        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(assetPdfRecord, 303410700);
                financePdf.jenistransaksi = getValueFromRemedy(assetPdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }
                System.out.println(financePdf.jenistransaksi);

                financePdf.untukpembayaran = getValueFromRemedy(assetPdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(assetPdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(assetPdfRecord, 536870938);
                financePdf.nomorfakturpajak = getValueFromRemedy(assetPdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(assetPdfRecord, 536870977);
                financePdf.tanggalfakturpajak = getValueFromRemedy(assetPdfRecord, 536870936);
                financePdf.jeniscrude = getValueFromRemedy(assetPdfRecord, 536870920);
                financePdf.pelabuhantujuan = getValueFromRemedy(assetPdfRecord, 536870926);
                financePdf.periodelifting = getValueFromRemedy(assetPdfRecord, 536870925);
                financePdf.jumlahmuatan = getValueFromRemedy(assetPdfRecord, 536870924);
                financePdf.hargasatuan = getValueFromRemedy(assetPdfRecord, 536871132);

                financePdf.duedate = getValueFromRemedy(assetPdfRecord, 536871002);
                System.out.println("due date :" + financePdf.duedate);
                financePdf.denda = getValueFromRemedy(assetPdfRecord, 536871162);
                if (financePdf.denda == "") {
                    financePdf.denda = "";
                    System.out.println(financePdf.denda);
                } else {
                    String Value = financePdf.denda.substring(financePdf.denda.indexOf("=") + 1, financePdf.denda.indexOf(",Cur"));
                    String currencyCode = financePdf.denda.substring(financePdf.denda.indexOf("Code=") + 5, financePdf.denda.indexOf(",Con"));
                    financePdf.denda = Value + " " + currencyCode;
                    System.out.println(financePdf.denda);
                }

                financePdf.creditnote = getValueFromRemedy(assetPdfRecord, 536870934);
                if (financePdf.creditnote == "") {
                    financePdf.creditnote = "";
                    System.out.println(financePdf.creditnote);
                } else {
                    String Value = financePdf.creditnote.substring(financePdf.creditnote.indexOf("=") + 1, financePdf.creditnote.indexOf(",Cur"));
                    String currencyCode = financePdf.creditnote.substring(financePdf.creditnote.indexOf("Code=") + 5, financePdf.creditnote.indexOf(",Con"));
                    financePdf.creditnote = Value + " " + currencyCode;
                    System.out.println(financePdf.creditnote);
                }

                financePdf.debitnote = getValueFromRemedy(assetPdfRecord, 536871161);
                if (financePdf.debitnote == "") {
                    financePdf.debitnote = "";
                    System.out.println(financePdf.debitnote);
                } else {
                    String Value = financePdf.debitnote.substring(financePdf.debitnote.indexOf("=") + 1, financePdf.debitnote.indexOf(",Cur"));
                    String currencyCode = financePdf.debitnote.substring(financePdf.debitnote.indexOf("Code=") + 5, financePdf.debitnote.indexOf(",Con"));
                    financePdf.debitnote = Value + " " + currencyCode;
                    System.out.println(financePdf.debitnote);
                }

                GUID = getValueFromRemedy(assetPdfRecord, 536870950);
                System.out.println("GUID: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\"");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);
                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            System.out.println("C : Check");
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            System.out.println("D : Auto Debit Payment");
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            System.out.println("E : Cash payment from House Bank");
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            System.out.println("L : Letter of Credit (L/C)");
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            System.out.println("N : Non Cash Settlement (Offset)");
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            System.out.println("O : Other Manual Payment");
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            System.out.println("T : Bank Transfer");
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            System.out.println("V : Travel Advance Manual Payment");
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            System.out.println("W : Working Advance Manual Payment");
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            System.out.println("X : Transfer-Autom.Pmt");
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            System.out.println("Payment Method Not found");
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);

                    sp3vendor.wbs = getValueFromRemedy(atkEntry, 536870968);
                    System.out.println("nomor wbs " + sp3vendor.wbs);

                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870915);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870917);

                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);

                    SP3SAGRTable.add(sagr);
                }

                financePdf.SPB = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.nomorPO = getValueFromRemedy(assetPdfRecord, 536870921);
                financePdf.nomorSAGR = getValueFromRemedy(assetPdfRecord, 536871077);
                System.out.println("nomorSARG: " + financePdf.nomorSAGR);

                financePdf.nilaiSAGRIDR = getValueFromRemedy(assetPdfRecord, 536870931);
                if (financePdf.nilaiSAGRIDR == "") {
                    financePdf.nilaiSAGRIDR = "";
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRIDR);
                } else {
                    String nilaiIDR = financePdf.nilaiSAGRIDR.substring(financePdf.nilaiSAGRIDR.indexOf("=") + 1, financePdf.nilaiSAGRIDR.indexOf(",Cur"));
                    String currencyCode = getValueFromRemedy(assetPdfRecord, 536870931);
                    currencyCode = financePdf.nilaiSAGRIDR.substring(financePdf.nilaiSAGRIDR.indexOf("Code=") + 5, financePdf.nilaiSAGRIDR.indexOf(",Con"));
                    financePdf.nilaiSAGRIDR = nilaiIDR + " " + currencyCode;
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRIDR);
                }

                financePdf.nilaiSAGRUSD = getValueFromRemedy(assetPdfRecord, 536870932);
                if (financePdf.nilaiSAGRUSD == "") {
                    financePdf.nilaiSAGRUSD = "";
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRUSD);
                } else {
                    String nilaiUSD = financePdf.nilaiSAGRUSD.substring(financePdf.nilaiSAGRUSD.indexOf("=") + 1, financePdf.nilaiSAGRUSD.indexOf(",Curr"));
                    String currencyCode = getValueFromRemedy(assetPdfRecord, 536870932);
                    currencyCode = financePdf.nilaiSAGRUSD.substring(financePdf.nilaiSAGRUSD.indexOf("Code=") + 5, financePdf.nilaiSAGRUSD.indexOf(",Con"));
                    financePdf.nilaiSAGRUSD = nilaiUSD + " " + currencyCode;
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRUSD);
                }

                financePdf.companycode = getValueFromRemedy(assetPdfRecord, 536870949);
                System.out.println("companycode: " + financePdf.companycode);
                financePdf.plant = getValueFromRemedy(assetPdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(assetPdfRecord, 536871157);
                financePdf.notiket = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.disetujuioleh = getValueFromRemedy(assetPdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(assetPdfRecord, 536871152);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(assetPdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(assetPdfRecord, 303410700) + ".pdf";
                financePdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 301368700);

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generatePOHydroCrude(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

                //update status print
                assetPdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);

            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/printPOHydroNonCrude")
    public String POHydroNonCrude() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'GUID'=\"IDGAA5V0FMUS3AQJGOOBQIIOBLBIAF\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'E-SP3 Type*'=\"3\" AND 'status print' = \"0\" ");
        String GUID, jenispembayaran, totalbepaid, denda, creditnote, debitnote;

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(assetPdfRecord, 303410700);

                financePdf.jenistransaksi = getValueFromRemedy(assetPdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }

                financePdf.untukpembayaran = getValueFromRemedy(assetPdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(assetPdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(assetPdfRecord, 536870938);

                financePdf.nomorfakturpajak = getValueFromRemedy(assetPdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(assetPdfRecord, 536870977);

                financePdf.tanggalfakturpajak = getValueFromRemedy(assetPdfRecord, 536870936);

                financePdf.duedate = getValueFromRemedy(assetPdfRecord, 536871002);
                financePdf.denda = getValueFromRemedy(assetPdfRecord, 536871162);
                if (financePdf.denda == "") {
                    financePdf.denda = "";
                    System.out.println(financePdf.denda);
                } else {
                    String Value = financePdf.denda.substring(financePdf.denda.indexOf("=") + 1, financePdf.denda.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.denda.substring(financePdf.denda.indexOf("Code=") + 5, financePdf.denda.indexOf(",Con"));
                    financePdf.denda = Value + " " + currencyCode;
                    System.out.println(financePdf.denda);
                }

                financePdf.creditnote = getValueFromRemedy(assetPdfRecord, 536870934);
                if (financePdf.creditnote == "") {
                    financePdf.creditnote = "";
                    System.out.println(financePdf.creditnote);
                } else {
                    String Value = financePdf.creditnote.substring(financePdf.creditnote.indexOf("=") + 1, financePdf.creditnote.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.creditnote.substring(financePdf.creditnote.indexOf("Code=") + 5, financePdf.creditnote.indexOf(",Con"));
                    financePdf.creditnote = Value + " " + currencyCode;
                    System.out.println(financePdf.creditnote);
                }

                financePdf.debitnote = getValueFromRemedy(assetPdfRecord, 536871161);
                if (financePdf.debitnote == "") {
                    financePdf.debitnote = "";
                    System.out.println(financePdf.debitnote);
                } else {
                    String Value = financePdf.debitnote.substring(financePdf.debitnote.indexOf("=") + 1, financePdf.debitnote.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.debitnote.substring(financePdf.debitnote.indexOf("Code=") + 5, financePdf.debitnote.indexOf(",Con"));
                    financePdf.debitnote = Value + " " + currencyCode;
                    System.out.println(financePdf.debitnote);
                }

                GUID = getValueFromRemedy(assetPdfRecord, 536870950);
                System.out.println("vendor id: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\" ");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Curr"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);
                    System.out.println("rek bank: " + sp3vendor.rekeningbank);

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            System.out.println("C : Check");
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            System.out.println("D : Auto Debit Payment");
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            System.out.println("E : Cash payment from House Bank");
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            System.out.println("L : Letter of Credit (L/C)");
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            System.out.println("N : Non Cash Settlement (Offset)");
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            System.out.println("O : Other Manual Payment");
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            System.out.println("T : Bank Transfer");
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            System.out.println("V : Travel Advance Manual Payment");
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            System.out.println("W : Working Advance Manual Payment");
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            System.out.println("X : Transfer-Autom.Pmt");
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            System.out.println("Payment Method Not found");
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);

                    sp3vendor.wbs = getValueFromRemedy(atkEntry, 536870968);
                    System.out.println("nomor wbs " + sp3vendor.wbs);

                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870914);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870926);

                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);

                    SP3SAGRTable.add(sagr);
                }

                financePdf.SPB = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.nomorPO = getValueFromRemedy(assetPdfRecord, 536870921);
                financePdf.nomorSAGR = getValueFromRemedy(assetPdfRecord, 536871077);

                financePdf.nilaiSAGRIDR = getValueFromRemedy(assetPdfRecord, 536870931);
                if (financePdf.nilaiSAGRIDR == "") {
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRIDR);
                } else {
                    String nilaiIDR = financePdf.nilaiSAGRIDR.substring(financePdf.nilaiSAGRIDR.indexOf("=") + 1, financePdf.nilaiSAGRIDR.indexOf(","));
                    String currencyCode = getValueFromRemedy(assetPdfRecord, 536870931);
                    currencyCode = financePdf.nilaiSAGRIDR.substring(financePdf.nilaiSAGRIDR.indexOf("Code=") + 5, financePdf.nilaiSAGRIDR.indexOf(",Con"));
                    financePdf.nilaiSAGRIDR = nilaiIDR + " " + currencyCode;
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRIDR);
                }

                financePdf.nilaiSAGRUSD = getValueFromRemedy(assetPdfRecord, 536870932);
                if (financePdf.nilaiSAGRUSD == "") {
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRUSD);
                } else {
                    String nilaiUSD = financePdf.nilaiSAGRUSD.substring(financePdf.nilaiSAGRUSD.indexOf("=") + 1, financePdf.nilaiSAGRUSD.indexOf(","));
                    String currencyCode = financePdf.nilaiSAGRUSD.substring(financePdf.nilaiSAGRUSD.indexOf("Code=") + 5, financePdf.nilaiSAGRUSD.indexOf(",Con"));
                    financePdf.nilaiSAGRUSD = nilaiUSD + " " + currencyCode;
                    System.out.println("nilai SAGR: " + financePdf.nilaiSAGRUSD);
                }

                financePdf.companycode = getValueFromRemedy(assetPdfRecord, 536870949);
                financePdf.plant = getValueFromRemedy(assetPdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(assetPdfRecord, 536871157);

                financePdf.notiket = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.disetujuioleh = getValueFromRemedy(assetPdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(assetPdfRecord, 536871152);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(assetPdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(assetPdfRecord, 303410700) + ".pdf";
                financePdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 301368700);

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generatePOHydroNonCrude(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

                //update status print
                assetPdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);
            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/printWithPOHydroPassthrough")
    public String WithPOHydroPassthrough() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'GUID'=\"IDGAA5V0FMUS3AQJUGCDQIWEKTA1N6\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'E-SP3 Type*'=\"6\" AND 'status print' = \"0\" ");
        String GUID;

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(assetPdfRecord, 303410700);
                System.out.println("no tiket: " + financePdf.no);

                financePdf.jenistransaksi = getValueFromRemedy(assetPdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }
                System.out.println("jenis transaksi: " + financePdf.jenistransaksi);

                financePdf.untukpembayaran = getValueFromRemedy(assetPdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(assetPdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(assetPdfRecord, 536870938);
                financePdf.nomorfakturpajak = getValueFromRemedy(assetPdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(assetPdfRecord, 536870977);

                financePdf.tanggalfakturpajak = getValueFromRemedy(assetPdfRecord, 536870936);
                System.out.println(financePdf.tanggalfakturpajak);

//                financePdf.jumlahpembayaran = getValueFromRemedy(assetPdfRecord, 536871003);
//                financePdf.jumlahpembayaran = financePdf.jumlahpembayaran.substring(financePdf.jumlahpembayaran.indexOf("=")+1, financePdf.jumlahpembayaran.indexOf(","));
                financePdf.duedate = getValueFromRemedy(assetPdfRecord, 536871002);
                financePdf.denda = getValueFromRemedy(assetPdfRecord, 536871162);
                if (financePdf.denda == "") {
                    financePdf.denda = "";
                    System.out.println(financePdf.denda);
                } else {
                    String Value = financePdf.denda.substring(financePdf.denda.indexOf("=") + 1, financePdf.denda.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.denda.substring(financePdf.denda.indexOf("Code=") + 5, financePdf.denda.indexOf(",Con"));
                    financePdf.denda = Value + " " + currencyCode;
                    System.out.println(financePdf.denda);
                }

                financePdf.creditnote = getValueFromRemedy(assetPdfRecord, 536870934);
                if (financePdf.creditnote == "") {
                    financePdf.creditnote = "";
                    System.out.println(financePdf.creditnote);
                } else {
                    String Value = financePdf.creditnote.substring(financePdf.creditnote.indexOf("=") + 1, financePdf.creditnote.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.creditnote.substring(financePdf.creditnote.indexOf("Code=") + 5, financePdf.creditnote.indexOf(",Con"));
                    financePdf.creditnote = Value + " " + currencyCode;
                    System.out.println(financePdf.creditnote);
                }

                financePdf.debitnote = getValueFromRemedy(assetPdfRecord, 536871161);
                if (financePdf.debitnote == "") {
                    financePdf.debitnote = "";
                    System.out.println(financePdf.debitnote);
                } else {
                    String Value = financePdf.debitnote.substring(financePdf.debitnote.indexOf("=") + 1, financePdf.debitnote.indexOf(",Cur"));
//                    double conv = Double.parseDouble(Value);
//                    String denda1 = String.format("%,.2f", conv);
                    String currencyCode = financePdf.debitnote.substring(financePdf.debitnote.indexOf("Code=") + 5, financePdf.debitnote.indexOf(",Con"));
                    financePdf.debitnote = Value + " " + currencyCode;
                    System.out.println(financePdf.debitnote);
                }

                GUID = getValueFromRemedy(assetPdfRecord, 536870950);
                System.out.println("vendor id: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\"");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Curr"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            sp3vendor.payment = "Payment Not found";
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);

                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870914);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870926);
                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);
                    SP3SAGRTable.add(sagr);
                }

                financePdf.SPB = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.nomorPO = getValueFromRemedy(assetPdfRecord, 536870921);
                financePdf.companycode = getValueFromRemedy(assetPdfRecord, 536870949);
                financePdf.plant = getValueFromRemedy(assetPdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(assetPdfRecord, 536871157);
                financePdf.notiket = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.disetujuioleh = getValueFromRemedy(assetPdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(assetPdfRecord, 536871152);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(assetPdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(assetPdfRecord, 303410700) + ".pdf";
                System.out.println(financePdf.filename);
                financePdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 301368700);

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generateWithPOHydroPassthrough(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

                //update status print
                assetPdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);

            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/printWithoutPONonHydro")
    public String WithoutPONonHydro() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "'GUID'=\"IDGAA5V0FM4EOAQ1WG8AQ0YCCLMO8J\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "('E-SP3 Type*'=\"2\" OR 'E-SP3 Type*'=\"1\") AND 'status print' = \"0\" ");
        String GUID;

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry assetPdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(assetPdfRecord, 303410700);
                System.out.println("no tiket: " + financePdf.no);

                financePdf.jenistransaksi = getValueFromRemedy(assetPdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }
                System.out.println(financePdf.jenistransaksi);
                financePdf.untukpembayaran = getValueFromRemedy(assetPdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(assetPdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(assetPdfRecord, 536870938);
                financePdf.nomorfakturpajak = getValueFromRemedy(assetPdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(assetPdfRecord, 536870977);

                financePdf.tanggalfakturpajak = getValueFromRemedy(assetPdfRecord, 536870936);
                System.out.println(financePdf.tanggalfakturpajak);

//                financePdf.jumlahpembayaran = getValueFromRemedy(assetPdfRecord, 536871003);
//                financePdf.jumlahpembayaran = financePdf.jumlahpembayaran.substring(financePdf.jumlahpembayaran.indexOf("=")+1, financePdf.jumlahpembayaran.indexOf(","));
                financePdf.duedate = getValueFromRemedy(assetPdfRecord, 536871002);

                GUID = getValueFromRemedy(assetPdfRecord, 536870950);
                System.out.println("vendor id: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\"");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Curr"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);
                    System.out.println("rek bank: " + sp3vendor.rekeningbank);

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            System.out.println("O : Other Manual Payment");
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            sp3vendor.payment = "Payment Method Not found";
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);

                    sp3vendor.wbs = getValueFromRemedy(atkEntry, 536870968);
                    sp3vendor.costcenter = getValueFromRemedy(atkEntry, 536870966);
                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870915);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870917);
                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);
                    SP3SAGRTable.add(sagr);
                }

                financePdf.SPB = getValueFromRemedy(assetPdfRecord, 536870923);
                financePdf.GLAccount = getValueFromRemedy(assetPdfRecord, 536870941);
                financePdf.companycode = getValueFromRemedy(assetPdfRecord, 536870949);
                financePdf.plant = getValueFromRemedy(assetPdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(assetPdfRecord, 536871157);
                financePdf.notiket = getValueFromRemedy(assetPdfRecord, 536870923);

                financePdf.disetujuioleh = getValueFromRemedy(assetPdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(assetPdfRecord, 536871152);
                System.out.println(financePdf.disetujuioleh);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(assetPdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(assetPdfRecord, 303410700) + ".pdf";
                financePdf.srInstanceId = getValueFromRemedy(assetPdfRecord, 301368700);

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generateWithoutPONonHydro(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

                //update status print
                assetPdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", assetPdfRecord.getEntryId(), assetPdfRecord, null, 0);

            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/printDownPayment")
    public String DownPayment() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", " 'GUID' = \"IDGAA5V0FMUS3AQJ9H4JQILG30OART\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", "('E-SP3 Type*'=\"4\" OR 'E-SP3 Type*'=\"5\") AND 'status print' = \"0\" ");
        String GUID;

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry financePdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(financePdfRecord, 303410700);

                financePdf.jenistransaksi = getValueFromRemedy(financePdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }
                System.out.println(financePdf.jenistransaksi);

                financePdf.untukpembayaran = getValueFromRemedy(financePdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(financePdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(financePdfRecord, 536870938);
                financePdf.nomorfakturpajak = getValueFromRemedy(financePdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(financePdfRecord, 536870977);
                financePdf.tanggalfakturpajak = getValueFromRemedy(financePdfRecord, 536870936);

//                financePdf.jumlahpembayaran = getValueFromRemedy(financePdfRecord, 536871003);
//                financePdf.jumlahpembayaran = financePdf.jumlahpembayaran.substring(financePdf.jumlahpembayaran.indexOf("=")+1, financePdf.jumlahpembayaran.indexOf(","));
//                System.out.println("jumlah pembayaran: "+financePdf.jumlahpembayaran);
                financePdf.duedate = getValueFromRemedy(financePdfRecord, 536871002);
                financePdf.denda = getValueFromRemedy(financePdfRecord, 536871162);
                if (financePdf.denda == "") {
                    financePdf.denda = "";
                    System.out.println(financePdf.denda);
                } else {
                    String Value = financePdf.denda.substring(financePdf.denda.indexOf("=") + 1, financePdf.denda.indexOf(",Cur"));
                    String currencyCode = financePdf.denda.substring(financePdf.denda.indexOf("Code=") + 5, financePdf.denda.indexOf(",Con"));
                    financePdf.denda = Value + " " + currencyCode;
                    System.out.println(financePdf.denda);
                }

                financePdf.creditnote = getValueFromRemedy(financePdfRecord, 536870934);
                if (financePdf.creditnote == "") {
                    financePdf.creditnote = "";
                    System.out.println(financePdf.creditnote);
                } else {
                    String Value = financePdf.creditnote.substring(financePdf.creditnote.indexOf("=") + 1, financePdf.creditnote.indexOf(",Cur"));
                    String currencyCode = financePdf.creditnote.substring(financePdf.creditnote.indexOf("Code=") + 5, financePdf.creditnote.indexOf(",Con"));
                    financePdf.creditnote = Value + " " + currencyCode;
                    System.out.println(financePdf.creditnote);
                }

                financePdf.debitnote = getValueFromRemedy(financePdfRecord, 536871161);
                if (financePdf.debitnote == "") {
                    financePdf.debitnote = "";
                    System.out.println(financePdf.debitnote);
                } else {
                    String Value = financePdf.debitnote.substring(financePdf.debitnote.indexOf("=") + 1, financePdf.debitnote.indexOf(",Cur"));
                    String currencyCode = financePdf.debitnote.substring(financePdf.debitnote.indexOf("Code=") + 5, financePdf.debitnote.indexOf(",Con"));
                    financePdf.debitnote = Value + " " + currencyCode;
                    System.out.println(financePdf.debitnote);
                }

                financePdf.SPB = getValueFromRemedy(financePdfRecord, 536870923);
                financePdf.nomorPO = getValueFromRemedy(financePdfRecord, 536870921);
                financePdf.companycode = getValueFromRemedy(financePdfRecord, 536870949);
                financePdf.plant = getValueFromRemedy(financePdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(financePdfRecord, 536871157);
                financePdf.notiket = getValueFromRemedy(financePdfRecord, 536870923);
                financePdf.disetujuioleh = getValueFromRemedy(financePdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(financePdfRecord, 536871152);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(financePdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(financePdfRecord, 303410700) + ".pdf";
                financePdf.srInstanceId = getValueFromRemedy(financePdfRecord, 301368700);

                GUID = getValueFromRemedy(financePdfRecord, 536870950);
                System.out.println("vendor id: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\"");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);
                    System.out.println("quantity: " + sp3vendor.quantity);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Curr"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            sp3vendor.payment = "Payment Not found";
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);
                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870914);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870926);
                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);

                    SP3SAGRTable.add(sagr);
                }

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generateDownPayment(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

//                System.out.println("Test");
//                System.out.println("id: "+financePdfRecord.getEntryId());
                financePdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", financePdfRecord.getEntryId(), financePdfRecord, null, 0);

//                System.out.println("Test 2");
            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/printSubsequent")
    public String Subsequent() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

//        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", " 'GUID' = \"IDGAA5V0FMUS3AQ396UAQ2L023E37V\" ");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SP3Manual", " 'E-SP3 Type*' = \"7\" AND 'status print' = \"0\" ");
        String GUID;

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                FinancePDF financePdf = new FinancePDF();

                Entry financePdfRecord = remedySession.getEntry("PTM:SSC:FIN:AIF:SP3Manual", eListInfo.getEntryID(), null);
                financePdf.no = getValueFromRemedy(financePdfRecord, 303410700);
                System.out.println(financePdf.no);

                financePdf.jenistransaksi = getValueFromRemedy(financePdfRecord, 536870947);
                switch (financePdf.jenistransaksi) {
                    case "0":
                        financePdf.jenistransaksi = "With PO Hydro - Crude";
                        break;
                    case "1":
                        financePdf.jenistransaksi = "Without PO Hydro";
                        break;
                    case "2":
                        financePdf.jenistransaksi = "Without PO Non Hydro";
                        break;
                    case "3":
                        financePdf.jenistransaksi = "With PO Hydro - Non Crude";
                        break;
                    case "4":
                        financePdf.jenistransaksi = "Hydro - Down Payment / LC";
                        break;
                    case "5":
                        financePdf.jenistransaksi = "Non Hydro - Down Payment / LC";
                        break;
                    case "6":
                        financePdf.jenistransaksi = "With PO Hydro - Passthrough";
                        break;
                    case "7":
                        financePdf.jenistransaksi = "Hydro - Subsequent";
                        break;
                    case "8":
                        financePdf.jenistransaksi = "Pembayaran Retensi";
                        break;
                    default:
                        financePdf.jenistransaksi = "E-SP3 Type Not found";
                }
                System.out.println(financePdf.jenistransaksi);

                financePdf.untukpembayaran = getValueFromRemedy(financePdfRecord, 536870942);
                financePdf.nomorinvoice = getValueFromRemedy(financePdfRecord, 536870937);
                financePdf.tanggalinvoice = getValueFromRemedy(financePdfRecord, 536870938);
                financePdf.nomorfakturpajak = getValueFromRemedy(financePdfRecord, 536871027);
                financePdf.notes = getValueFromRemedy(financePdfRecord, 536870977);
                financePdf.tanggalfakturpajak = getValueFromRemedy(financePdfRecord, 536870936);

//                financePdf.jumlahpembayaran = getValueFromRemedy(financePdfRecord, 536871003);
//                financePdf.jumlahpembayaran = financePdf.jumlahpembayaran.substring(financePdf.jumlahpembayaran.indexOf("=")+1, financePdf.jumlahpembayaran.indexOf(","));
//                System.out.println("jumlah pembayaran: "+financePdf.jumlahpembayaran);
                financePdf.duedate = getValueFromRemedy(financePdfRecord, 536871002);
                financePdf.denda = getValueFromRemedy(financePdfRecord, 536871162);
                if (financePdf.denda == "") {
                    financePdf.denda = "";
                    System.out.println(financePdf.denda);
                } else {
                    String Value = financePdf.denda.substring(financePdf.denda.indexOf("=") + 1, financePdf.denda.indexOf(",Cur"));
                    String currencyCode = financePdf.denda.substring(financePdf.denda.indexOf("Code=") + 5, financePdf.denda.indexOf(",Con"));
                    financePdf.denda = Value + " " + currencyCode;
                    System.out.println(financePdf.denda);
                }

                financePdf.creditnote = getValueFromRemedy(financePdfRecord, 536870934);
//                System.out.println("creditnote:" +financePdf.creditnote);
                if (financePdf.creditnote == "") {
                    financePdf.creditnote = "";
                    System.out.println("creaditnote: " + financePdf.creditnote);
                } else {
                    String Value = financePdf.creditnote.substring(financePdf.creditnote.indexOf("=") + 1, financePdf.creditnote.indexOf(",Cur"));
                    String currencyCode = financePdf.creditnote.substring(financePdf.creditnote.indexOf("Code=") + 5, financePdf.creditnote.indexOf(",Con"));
                    financePdf.creditnote = Value + " " + currencyCode;
                    System.out.println("creditnote:" + financePdf.creditnote);
                }

                financePdf.debitnote = getValueFromRemedy(financePdfRecord, 536871161);
//                System.out.println("debitnote:" +financePdf.debitnote);
                if (financePdf.debitnote == "") {
                    financePdf.debitnote = "";
                    System.out.println(financePdf.debitnote);
                } else {
                    String Value = financePdf.debitnote.substring(financePdf.debitnote.indexOf("=") + 1, financePdf.debitnote.indexOf(",Cur"));
                    String currencyCode = financePdf.debitnote.substring(financePdf.debitnote.indexOf("Code=") + 5, financePdf.debitnote.indexOf(",Con"));
                    financePdf.debitnote = Value + " " + currencyCode;
                    System.out.println(financePdf.debitnote);
                }

                financePdf.SPB = getValueFromRemedy(financePdfRecord, 536870923);
                financePdf.nomorPO = getValueFromRemedy(financePdfRecord, 536870921);
                financePdf.companycode = getValueFromRemedy(financePdfRecord, 536870949);
                financePdf.plant = getValueFromRemedy(financePdfRecord, 536870946);
                financePdf.PICTagihan = getValueFromRemedy(financePdfRecord, 536871157);
                financePdf.notiket = getValueFromRemedy(financePdfRecord, 536870923);
                financePdf.disetujuioleh = getValueFromRemedy(financePdfRecord, 536871126);
                financePdf.approvedDate = getValueFromRemedy(financePdfRecord, 536871152);
                financePdf.nomorSAGR = getValueFromRemedy(financePdfRecord, 536871163);

                //add workinfo
                financePdf.requstNumber = getValueFromRemedy(financePdfRecord, 303410700);
                financePdf.filename = "SP3Manual_" + getValueFromRemedy(financePdfRecord, 303410700) + ".pdf";
                financePdf.srInstanceId = getValueFromRemedy(financePdfRecord, 301368700);

                GUID = getValueFromRemedy(financePdfRecord, 536870950);
                System.out.println("vendor id: " + GUID);

                ArrayList<SP3VendorTable> sp3vendorTable = new ArrayList<SP3VendorTable>();
                List<EntryListInfo> eListAtks = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:Vendor", "'GUID'=\"" + GUID + "\" AND 'Status' = \"Enabled\"");
                for (EntryListInfo eListAtk : eListAtks) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:Vendor", eListAtk.getEntryID(), null);
                    SP3VendorTable sp3vendor = new SP3VendorTable();

                    sp3vendor.nomorvendor = getValueFromRemedy(atkEntry, 536870915);
                    sp3vendor.namavendor = getValueFromRemedy(atkEntry, 536870917);
                    sp3vendor.npwp = getValueFromRemedy(atkEntry, 536870918);
                    sp3vendor.quantity = getValueFromRemedy(atkEntry, 536871010);
                    System.out.println("quantity: " + sp3vendor.quantity);

                    String IDR, USD, Others;
                    sp3vendor.nilaiIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.nilaiIDR != "") {
                        sp3vendor.nilaiIDR = sp3vendor.nilaiIDR.substring(sp3vendor.nilaiIDR.indexOf("=") + 1, sp3vendor.nilaiIDR.indexOf(",Curr"));
//                            double conv = Double.parseDouble(nomIDR);
//                            IDR = String.format("%,.2f", conv) + " IDR";
//                            sp3vendor.nilaiIDR = nomIDR + " IDR";
                        System.out.println(sp3vendor.nilaiIDR);
                    } else {
                        System.out.println(sp3vendor.nilaiIDR);
                    }

                    sp3vendor.currencyIDR = getValueFromRemedy(atkEntry, 536870937);
                    if (sp3vendor.currencyIDR != "") {
                        sp3vendor.currencyIDR = sp3vendor.currencyIDR.substring(sp3vendor.currencyIDR.indexOf("Code=") + 5, sp3vendor.currencyIDR.indexOf(",Con"));
                        System.out.println("currency idr: " + sp3vendor.currencyIDR);
                    } else {
                        System.out.println(sp3vendor.currencyIDR);
                    }

                    sp3vendor.nilaiUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.nilaiUSD != "") {
                        sp3vendor.nilaiUSD = sp3vendor.nilaiUSD.substring(sp3vendor.nilaiUSD.indexOf("=") + 1, sp3vendor.nilaiUSD.indexOf(",Cur"));
//                            double conv = Double.parseDouble(nomUSD);
//                            USD = String.format("%,.2f", conv) + " USD";
//                            sp3vendor.nilaiUSD = nomUSD + " USD";
                        System.out.println("Nilai USA: " + sp3vendor.nilaiUSD);
                    } else {
                        System.out.println(sp3vendor.nilaiUSD);
                    }

                    sp3vendor.currencyUSD = getValueFromRemedy(atkEntry, 536870965);
                    if (sp3vendor.currencyUSD != "") {
                        sp3vendor.currencyUSD = sp3vendor.currencyUSD.substring(sp3vendor.currencyUSD.indexOf("Code=") + 5, sp3vendor.currencyUSD.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("Nilai Others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.nilaiOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.nilaiOthers != "") {
                        sp3vendor.nilaiOthers = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("=") + 1, sp3vendor.nilaiOthers.indexOf(",Cur"));
//                            double conv = Double.parseDouble(Value);
//                            Others = String.format("%,.2f", conv);
//                            String currencyCode = getValueFromRemedy(atkEntry, 536870972);
//                            currencyCode = sp3vendor.nilaiOthers.substring(sp3vendor.nilaiOthers.indexOf("Code=")+5, sp3vendor.nilaiOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("nilai others: " + sp3vendor.nilaiOthers);
                    } else {
                        System.out.println(sp3vendor.nilaiOthers);
                    }

                    sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                    if (sp3vendor.currencyOthers != "") {
//                            sp3vendor.currencyOthers = getValueFromRemedy(atkEntry, 536870972);
                        sp3vendor.currencyOthers = sp3vendor.currencyOthers.substring(sp3vendor.currencyOthers.indexOf("Code=") + 5, sp3vendor.currencyOthers.indexOf(",Con"));
//                            sp3vendor.nilaiOthers = Value + " " + currencyCode;
                        System.out.println("curr Others: " + sp3vendor.currencyOthers);
                    } else {
                        System.out.println(sp3vendor.currencyOthers);
                    }

                    sp3vendor.rekeningbank = getValueFromRemedy(atkEntry, 536870925);

                    sp3vendor.payment = getValueFromRemedy(atkEntry, 536870919);
                    switch (sp3vendor.payment) {
                        case "C":
                            sp3vendor.payment = "C : Check";
                            break;
                        case "D":
                            sp3vendor.payment = "D : Auto Debit Payment";
                            break;
                        case "E":
                            sp3vendor.payment = "E : Cash payment from House Bank";
                            break;
                        case "L":
                            sp3vendor.payment = "L : Letter of Credit (L/C)";
                            break;
                        case "N":
                            sp3vendor.payment = "N : Non Cash Settlement (Offset)";
                            break;
                        case "O":
                            sp3vendor.payment = "O : Other Manual Payment";
                            break;
                        case "T":
                            sp3vendor.payment = "T : Bank Transfer";
                            break;
                        case "V":
                            sp3vendor.payment = "V : Travel Advance Manual Payment";
                            break;
                        case "W":
                            sp3vendor.payment = "W : Working Advance Manual Payment";
                            break;
                        case "X":
                            sp3vendor.payment = "X : Transfer-Autom.Pmt";
                            break;
                        default:
                            sp3vendor.payment = "Payment Not found";
                    }
                    System.out.println("payment: " + sp3vendor.payment);

                    sp3vendor.jenispembayaran = getValueFromRemedy(atkEntry, 536870974);
                    switch (sp3vendor.jenispembayaran) {
                        case "0":
                            System.out.println("Payment");
                            sp3vendor.jenispembayaran = "Payment";
                            break;
                        case "1":
                            System.out.println("Autodebet");
                            sp3vendor.jenispembayaran = "Autodebet";
                            break;
                        default:
                            System.out.println("Payment Type Not found");
                    }
                    System.out.println("jenis pembayaran: " + sp3vendor.jenispembayaran);
                    sp3vendorTable.add(sp3vendor);
                }

                ArrayList<SP3PO> sp3POTable = new ArrayList<SP3PO>();
                List<EntryListInfo> eListSP3POs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:PO", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSP3PO : eListSP3POs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:PO", eListSP3PO.getEntryID(), null);
                    SP3PO sp3po = new SP3PO();
                    sp3po.nomorPO = getValueFromRemedy(atkEntry, 536870914);
                    sp3po.currency = getValueFromRemedy(atkEntry, 536870926);
                    sp3POTable.add(sp3po);
                }

                ArrayList<SP3SAGR> SP3SAGRTable = new ArrayList<SP3SAGR>();
                List<EntryListInfo> eListSAGRs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:FIN:AIF:SAorGR", "'GUID'=\"" + GUID + "\" ");
                for (EntryListInfo eListSAGR : eListSAGRs) {
                    Entry atkEntry = remedySession.getEntry("PTM:SSC:FIN:AIF:SAorGR", eListSAGR.getEntryID(), null);
                    SP3SAGR sagr = new SP3SAGR();
                    sagr.noSAGR = getValueFromRemedy(atkEntry, 536870914);
                    sagr.noSAGRItem = getValueFromRemedy(atkEntry, 536870938);
                    sagr.noPOItem = getValueFromRemedy(atkEntry, 536870939);
                    sagr.TotalAmountSAGR = getValueFromRemedy(atkEntry, 536870920);
                    sagr.Year = getValueFromRemedy(atkEntry, 536870943);

                    SP3SAGRTable.add(sagr);
                }

                VIMDocument vimDocument = new VIMDocument();
                vimDocument.generateSubsequent(financePdf, sp3vendorTable, sp3POTable, SP3SAGRTable);

//                System.out.println("Test");
//                System.out.println("id: "+financePdfRecord.getEntryId());
                financePdfRecord.put(536871130, new Value("1"));
                remedySession.setEntry("PTM:SSC:FIN:AIF:SP3Manual", financePdfRecord.getEntryId(), financePdfRecord, null, 0);

//                System.out.println("Test 2");
            }
        } catch (ARException | BadElementException | IOException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

}
