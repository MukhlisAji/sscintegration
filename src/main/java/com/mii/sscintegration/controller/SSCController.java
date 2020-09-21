package com.mii.sscintegration.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.PlatformUtil.Platform;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.PJSDomain;
import com.mii.sscintegration.domain.TravelCost;
import com.mii.sscintegration.domain.TravelExpense;
import com.mii.sscintegration.domain.TravelRequestTiket;
import com.mii.sscintegration.domain.TravelokaIssuanceTicket;
import com.mii.sscintegration.domain.TravelokaPDF;
import com.mii.sscintegration.domain.TravelokaResponse;
import com.mii.sscintegration.domain.traveloka.BookingTraveloka;
import com.mii.sscintegration.domain.traveloka.SKPDPrintPdf;
import com.mii.sscintegration.domain.traveloka.TravelFlightPrint;
import com.mii.sscintegration.domain.traveloka.TravelHotelPrint;
import com.mii.sscintegration.domain.traveloka.TravelokaOKPayment;
import com.mii.sscintegration.poi.TravelDocument;
import com.mii.sscintegration.soap.SAPSOAPGenerator;
import com.mii.sscintegration.soap.SOAPGenerator;

import ch.qos.logback.classic.pattern.Util;
import com.bmc.thirdparty.org.springframework.core.io.Resource;
import com.bmc.thirdparty.org.springframework.core.io.ResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BadElementException;
import static com.mii.sscintegration.controller.AssetController.logger;
import com.mii.sscintegration.domain.AssetATK;
import com.mii.sscintegration.domain.AssetPDF;
import com.mii.sscintegration.domain.mitra.MitraBooking;
import com.mii.sscintegration.domain.mitra.MitraTravelDetails;
import com.mii.sscintegration.poi.AssetDocument;
import java.io.File;
import java.util.Calendar;
import net.minidev.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Controller
public class SSCController {

    protected static Logger logger = Logger.getLogger("SSCController: ");

    @RequestMapping(value = "printsettlementpdfln", method = RequestMethod.GET)
    public String printSettlementDNLN() throws BadElementException, IOException {
        String formSettlement = "PTM:SSC:HR:TravelSettlement";

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        SimpleDateFormat mmddyyyyformat = new SimpleDateFormat("MM/dd/yyyy");
        List<EntryListInfo> listSettlementtobePrinted = remedyAPI.getRemedyRecordByQuery(remedySession, formSettlement, "'Status Print'=\"0\" ");
        try {
            for (EntryListInfo settlementtobePrinted : listSettlementtobePrinted) {
                TravelokaPDF travelokaPdf = new TravelokaPDF();
                TravelDocument travelDocument = new TravelDocument();
                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                Entry settlementRecord = remedySession.getEntry(formSettlement, settlementtobePrinted.getEntryID(), null);

                travelokaPdf.companyCode = settlementRecord.get(536871101).getValue().toString();
                travelokaPdf.pekerja = settlementRecord.get(536871102).getValue().toString() + " (" + settlementRecord.get(536871103).getValue().toString() + ")";
                travelokaPdf.noTrip = settlementRecord.get(536871110).getValue().toString();
                travelokaPdf.alokasiCostCenter = settlementRecord.get(536871105).getValue().toString();
                travelokaPdf.jenisPerjalanan = settlementRecord.get(536870925).getValue().toString();
                travelokaPdf.namafile = "TravelSettlement_" + travelokaPdf.noTrip + ".pdf";
                travelokaPdf.srInstanceId = settlementRecord.get(8).getValue().toString();
                try {
                    java.util.Date utilDate = mmddyyyyformat.parse(settlementRecord.get(536870921).getValue().toString());
                    Date tanggalFormat = new Date(utilDate.getTime());
                    travelokaPdf.tanggalDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                    utilDate = mmddyyyyformat.parse(settlementRecord.get(536870926).getValue().toString());
                    tanggalFormat = new Date(utilDate.getTime());
                    travelokaPdf.tanggalDinas += " s/d " + new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                } catch (ParseException e) {
                    logger.info("error converting date" + e.toString());
                }
                travelokaPdf.kotaAsal = settlementRecord.get(536870927).getValue().toString();
                travelokaPdf.kotatujuan1 = settlementRecord.get(536870922).getValue().toString();

                //mencari tiket OTA
                String CBT = settlementRecord.get(536871112).getValue().toString();
                List<BookingTraveloka> listBookingTiket = new ArrayList<BookingTraveloka>();
                if (CBT.equals("Ya, CBT Mitra")) {
                     listBookingTiket = getMitraBooking(null, travelokaPdf.noTrip, remedySession);

                } else {
                    listBookingTiket = getTravelokaBooking(null, travelokaPdf.noTrip, remedySession);

                }

                //mencatat semua expense
                List<TravelExpense> listTravelExpense = new ArrayList<TravelExpense>();
                TravelExpense travelExpense = new TravelExpense();

                //kompensasi 
                if (settlementRecord.get(536870916).getValue() != null) {
                    travelExpense.rincian = "Kompensasi";
                    travelExpense.keterangan = "Kompensasi Harian\nKompensasi Harian Acara\nKompensasi Laundry";
                    if (settlementRecord.get(536871113).getValue().toString().equalsIgnoreCase("Luar Negeri")) {
                        travelExpense.pengali = getUSDFormat(settlementRecord.get(536870917).getValue().toString()) + "\n"
                                + getUSDFormat(settlementRecord.get(536870918).getValue().toString()) + "\n"
                                + getUSDFormat(settlementRecord.get(536870920).getValue().toString());
                        travelExpense.jumlahUSD = getUSDFormat(settlementRecord.get(536870916).getValue().toString());
                    } else {
                        travelExpense.pengali = getIDRFormat(settlementRecord.get(536870917).getValue().toString()) + "\n"
                                + getIDRFormat(settlementRecord.get(536870918).getValue().toString()) + "\n"
                                + getIDRFormat(settlementRecord.get(536870920).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536870916).getValue().toString());
                    }
                    listTravelExpense.add(travelExpense);
                }

                List<String> listRincianCode1 = new ArrayList<String>();
                List<String> listRincianCode2 = new ArrayList<String>();
                List<String> listPengaliCode1 = new ArrayList<String>();
                List<String> listPengaliCode2 = new ArrayList<String>();
                List<String> listJumlahCode = new ArrayList<String>();
                List<String> listBSTPLN = new ArrayList<String>();

                listRincianCode1.add("536871124");
                listRincianCode1.add("536871127");
                listRincianCode1.add("536871130");
                listRincianCode1.add("536871133");

                listRincianCode2.add("536871125");
                listRincianCode2.add("536871128");
                listRincianCode2.add("536871131");
                listRincianCode2.add("536871134");

                listPengaliCode1.add("536871186");
                listPengaliCode1.add("536871188");
                listPengaliCode1.add("536871190");
                listPengaliCode1.add("536871191");

                listPengaliCode2.add("536871202");
                listPengaliCode2.add("536871203");
                listPengaliCode2.add("536871204");
                listPengaliCode2.add("536871205");

                listJumlahCode.add("536871046");
                listJumlahCode.add("536871047");
                listJumlahCode.add("536871045");
                listJumlahCode.add("536871043");

                listBSTPLN.add("536871126");
                listBSTPLN.add("536871132");
                listBSTPLN.add("536871135");
                listBSTPLN.add("536871137");

                //looping for BSTP Expense
                try {
                    int loopingBSTP = 0;
                    while (listRincianCode1.size() > loopingBSTP) {
                        if (settlementRecord.get(Integer.parseInt(listPengaliCode2.get(loopingBSTP))).getValue() != null) {
                            travelExpense = new TravelExpense();
                            travelExpense.rincian = settlementRecord.get(Integer.parseInt(listRincianCode1.get(loopingBSTP))).getValue().toString()
                                    + "(" + settlementRecord.get(Integer.parseInt(listRincianCode2.get(loopingBSTP))).getValue().toString() + ")";
                            travelExpense.pengali = (settlementRecord.get(Integer.parseInt(listPengaliCode1.get(loopingBSTP))).getValue().toString().equalsIgnoreCase("Pulang Pergi (PP)"))
                                    ? "2 x " : "1 x ";
                            if (settlementRecord.get(Integer.parseInt(listBSTPLN.get(loopingBSTP))).getValue().toString().equalsIgnoreCase("usd")) {
                                travelExpense.pengali += getUSDFormat(settlementRecord.get(Integer.parseInt(listPengaliCode2.get(loopingBSTP))).getValue().toString());
                                travelExpense.jumlahUSD = getUSDFormat(settlementRecord.get(Integer.parseInt(listJumlahCode.get(loopingBSTP))).getValue().toString());
                            } else {
                                travelExpense.pengali += getIDRFormat(settlementRecord.get(Integer.parseInt(listPengaliCode2.get(loopingBSTP))).getValue().toString());
                                travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(Integer.parseInt(listJumlahCode.get(loopingBSTP))).getValue().toString());
                            }
                            listTravelExpense.add(travelExpense);
                        }

                        loopingBSTP++;
                    }
                } catch (Exception e) {
                    logger.info("error BSTP : " + e);
                }

                //expense antar kota
                List<String> listKotaId = new ArrayList<String>();
                List<String> listKotaAmountId = new ArrayList<String>();
                List<String> listKotaTypeId = new ArrayList<String>();
                List<String> listKotaKmId = new ArrayList<String>();
                List<String> listTotalKm = new ArrayList<String>();
                List<String> listCurrencyAntarKota = new ArrayList<String>();

                listKotaId.add("536871136");
                listKotaId.add("536871140");
                listKotaId.add("536871144");
                listKotaId.add("536871148");

                listKotaAmountId.add("536871206");
                listKotaAmountId.add("536871209");
                listKotaAmountId.add("536871212");
                listKotaAmountId.add("536871213");

                listKotaTypeId.add("536871138");
                listKotaTypeId.add("536871142");
                listKotaTypeId.add("536871146");
                listKotaTypeId.add("536871150");

                listKotaKmId.add("536871208");
                listKotaKmId.add("536871210");
                listKotaKmId.add("536871211");
                listKotaKmId.add("536871214");

                listTotalKm.add("536871062");
                listTotalKm.add("536871063");
                listTotalKm.add("536871061");
                listTotalKm.add("536871059");

                listCurrencyAntarKota.add("536871139");
                listCurrencyAntarKota.add("536871141");
                listCurrencyAntarKota.add("536871143");
                listCurrencyAntarKota.add("536871149");

                int loopingAntarKota = 0;
                while (listKotaId.size() > loopingAntarKota) {
                    if (settlementRecord.get(Integer.parseInt(listKotaAmountId.get(loopingAntarKota))).getValue() != null) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = settlementRecord.get(Integer.parseInt(listKotaId.get(loopingAntarKota))).getValue().toString();
                        travelExpense.pengali = "";

                        if (listCurrencyAntarKota.get(loopingAntarKota).equalsIgnoreCase("usd")) {
                            travelExpense.jumlahUSD = getUSDFormat(settlementRecord.get(Integer.parseInt(listTotalKm.get(loopingAntarKota))).getValue().toString());
                        } else {
                            travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(Integer.parseInt(listTotalKm.get(loopingAntarKota))).getValue().toString());
                        }

                        listTravelExpense.add(travelExpense);
                    }

                    loopingAntarKota++;
                }

                //Laundry expense
                if (settlementRecord.get(536871032).getValue() != null) {
                    travelExpense = new TravelExpense();
                    travelExpense.rincian = "Laundry";
                    travelExpense.pengali = settlementRecord.get(536871168).getValue().toString()
                            + " x " + getIDRFormat(settlementRecord.get(536871167).getValue().toString());
                    travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871032).getValue().toString());
                    listTravelExpense.add(travelExpense);
                }

                //Daily Allowance
                if (settlementRecord.get(536871034).getValue() != null) {
                    if (settlementRecord.get(536871034).getIntValue() > 0) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = "Daily Allowance";
                        travelExpense.pengali = settlementRecord.get(536871174).getValue().toString()
                                + " x " + getIDRFormat(settlementRecord.get(536871173).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871034).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                }

                //kompensasi area tertentu
                if (settlementRecord.get(536871037).getValue() != null) {
                    if (settlementRecord.get(536871037).getIntValue() > 0) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = "Kompensasi Area Tertentu";
                        travelExpense.pengali = settlementRecord.get(536871177).getValue().toString()
                                + " x " + getIDRFormat(settlementRecord.get(536871176).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871037).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                }

                //other expense
                List<String> otherExpenseTypeId = new ArrayList<String>();
                List<String> otherExpenseAmountId = new ArrayList<String>();
                List<String> otherExpenseKeteranganId = new ArrayList<String>();

                otherExpenseTypeId.add("536871152");
                otherExpenseTypeId.add("536871155");
                otherExpenseTypeId.add("536871158");
                otherExpenseTypeId.add("536871161");
                otherExpenseTypeId.add("536871178");

                otherExpenseAmountId.add("536871215");
                otherExpenseAmountId.add("536871216");
                otherExpenseAmountId.add("536871217");
                otherExpenseAmountId.add("536871218");
                otherExpenseAmountId.add("536871219");

                otherExpenseKeteranganId.add("536871154");
                otherExpenseKeteranganId.add("536871157");
                otherExpenseKeteranganId.add("536871160");
                otherExpenseKeteranganId.add("536871164");
                otherExpenseKeteranganId.add("536871180");

                // other expense
                int loopingOtherExpense = 0;
                while (otherExpenseTypeId.size() > loopingOtherExpense) {
                    if (settlementRecord.get(Integer.parseInt(otherExpenseAmountId.get(loopingOtherExpense))).getValue() != null) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = settlementRecord.get(Integer.parseInt(otherExpenseTypeId.get(loopingOtherExpense))).getValue().toString();
                        travelExpense.keterangan = (settlementRecord.get(Integer.parseInt(otherExpenseKeteranganId.get(loopingOtherExpense))).getValue() == null)
                                ? " " : settlementRecord.get(Integer.parseInt(otherExpenseKeteranganId.get(loopingOtherExpense))).toString();
                        travelExpense.pengali = "";
                        travelExpense.jumlahTotal = getIDRFormat(
                                settlementRecord.get(Integer.parseInt(otherExpenseAmountId.get(loopingOtherExpense))).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                    loopingOtherExpense++;
                }

                int totalTobePaid = 0;
                if (settlementRecord.get(536871056).getValue() == null) {
                    totalTobePaid = settlementRecord.get(536870966).getIntValue();
                } else {
                    totalTobePaid = settlementRecord.get(536871056).getIntValue();
                }

                //get advance payment
                if (settlementRecord.get(536870928).getValue() != null) {
                    travelExpense = new TravelExpense();
                    travelExpense.rincian = "Panjar Dinas";
                    travelExpense.pengali = "";
                    if (settlementRecord.get(536871113).getValue().toString().equalsIgnoreCase("Luar Negeri")) {
                        travelExpense.jumlahUSD = "-" + getUSDFormat(settlementRecord.get(536870928).getValue().toString());
                    } else {
                        travelExpense.jumlahTotal = "-" + getIDRFormat(settlementRecord.get(536870928).getValue().toString());
                    }
                    listTravelExpense.add(travelExpense);
                    totalTobePaid -= settlementRecord.get(536870928).getIntValue();

                }

                //total expense
                travelExpense = new TravelExpense();
                travelExpense.rincian = "Total to be paid";
                travelExpense.pengali = "";
                if (settlementRecord.get(536871113).getValue().toString().equalsIgnoreCase("Luar Negeri")) {
                    travelExpense.jumlahUSD = getUSDFormat(String.valueOf(settlementRecord.get(536870966)));
                } else {
                    travelExpense.jumlahTotal = getIDRFormat(String.valueOf(totalTobePaid));
                }
                listTravelExpense.add(travelExpense);

                //get request ticket
                List<TravelRequestTiket> listTravelRequestTiket = getTravelRequestTiket(travelokaPdf.noTrip, remedySession);

                //travelDocument.generateTravelSettlementPdf(travelokaPdf, listBookingTiket, listTravelExpense, listTravelRequestTiket);
                travelDocument.generateTravelSettlementPdfLN(travelokaPdf, listBookingTiket, listTravelExpense, listTravelRequestTiket);

                //update status print
//                settlementRecord.put(536870913, new Value("1"));
//                remedySession.setEntry(formSettlement, settlementRecord.getEntryId(), settlementRecord, null, 0);
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "printsettlementpdf", method = RequestMethod.GET)
    public String printSettlementPdf() throws BadElementException, IOException {
        String formSettlement = "PTM:SSC:HR:TravelSettlement";

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        SimpleDateFormat mmddyyyyformat = new SimpleDateFormat("MM/dd/yyyy");
        List<EntryListInfo> listSettlementtobePrinted = remedyAPI.getRemedyRecordByQuery(remedySession, formSettlement, "'Status Print'=\"0\" ");
        try {
            for (EntryListInfo settlementtobePrinted : listSettlementtobePrinted) {
                TravelokaPDF travelokaPdf = new TravelokaPDF();
                TravelDocument travelDocument = new TravelDocument();
                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                Entry settlementRecord = remedySession.getEntry(formSettlement, settlementtobePrinted.getEntryID(), null);

                travelokaPdf.companyCode = settlementRecord.get(536871101).getValue().toString();
                travelokaPdf.pekerja = settlementRecord.get(536871102).getValue().toString() + " (" + settlementRecord.get(536871103).getValue().toString() + ")";
                travelokaPdf.noTrip = settlementRecord.get(536871110).getValue().toString();
                travelokaPdf.alokasiCostCenter = settlementRecord.get(536871105).getValue().toString();
                travelokaPdf.jenisPerjalanan = settlementRecord.get(536870925).getValue().toString();
                travelokaPdf.namafile = "TravelSettlement_" + travelokaPdf.noTrip + ".pdf";
                travelokaPdf.srInstanceId = settlementRecord.get(8).getValue().toString();
                try {
                    java.util.Date utilDate = mmddyyyyformat.parse(settlementRecord.get(536870921).getValue().toString());
                    Date tanggalFormat = new Date(utilDate.getTime());
                    travelokaPdf.tanggalDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                    utilDate = mmddyyyyformat.parse(settlementRecord.get(536870926).getValue().toString());
                    tanggalFormat = new Date(utilDate.getTime());
                    travelokaPdf.tanggalDinas += " s/d " + new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                } catch (ParseException e) {
                    logger.info("error converting date" + e.toString());
                }
                travelokaPdf.kotaAsal = settlementRecord.get(536870927).getValue().toString();
                travelokaPdf.kotatujuan1 = settlementRecord.get(536870922).getValue().toString();
                /* cuman ada satu
				travelokaPdf.kotatujuan2 = ;
				travelokaPdf.kotatujuan3 = ;*/

                //mencari tiket OTA
                List<BookingTraveloka> listBookingTiket = getTravelokaBooking(null, travelokaPdf.noTrip, remedySession);

                //mencatat semua expense
                List<TravelExpense> listTravelExpense = new ArrayList<TravelExpense>();
                TravelExpense travelExpense = new TravelExpense();

                //kompensasi 
                if (settlementRecord.get(536870916).getValue() != null) {
                    travelExpense.rincian = "Kompensasi";
                    travelExpense.keterangan = "Kompensasi Harian\nKompensasi Harian Acara\nKompensasi Laundry";
                    travelExpense.pengali = getIDRFormat(settlementRecord.get(536870917).getValue().toString()) + "\n"
                            + getIDRFormat(settlementRecord.get(536870918).getValue().toString()) + "\n"
                            + getIDRFormat(settlementRecord.get(536870920).getValue().toString());
                    travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536870916).getValue().toString());
                    listTravelExpense.add(travelExpense);
                }

                /*
				//kompensasi makan
				if(settlementRecord.get(536870917).getValue()!=null) {
					travelExpense.rincian = "Kompensasi Harian Perjalanan";
					travelExpense.pengali = "";
					travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536870917).getValue().toString());
					listTravelExpense.add(travelExpense);
				}
				
				
				//kompensasi makan
				if(settlementRecord.get(536870918).getValue()!=null) {
					travelExpense.rincian = "Kompensasi Harian Acara";
					travelExpense.pengali = "";
					travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536870918).getValue().toString());
					listTravelExpense.add(travelExpense);
				}
				//kompensasi makan
				if(settlementRecord.get(536870920).getValue()!=null) {
					travelExpense.rincian = "Kompensasi Laundry";
					travelExpense.pengali = "";
					travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536870920).getValue().toString());
					listTravelExpense.add(travelExpense);
				}*/
                List<String> listRincianCode1 = new ArrayList<String>();
                List<String> listRincianCode2 = new ArrayList<String>();
                List<String> listPengaliCode1 = new ArrayList<String>();
                List<String> listPengaliCode2 = new ArrayList<String>();
                List<String> listJumlahCode = new ArrayList<String>();

                listRincianCode1.add("536871124");
                listRincianCode1.add("536871127");
                listRincianCode1.add("536871130");
                listRincianCode1.add("536871133");

                listRincianCode2.add("536871125");
                listRincianCode2.add("536871128");
                listRincianCode2.add("536871131");
                listRincianCode2.add("536871134");

                listPengaliCode1.add("536871186");
                listPengaliCode1.add("536871188");
                listPengaliCode1.add("536871190");
                listPengaliCode1.add("536871191");

                listPengaliCode2.add("536871202");
                listPengaliCode2.add("536871203");
                listPengaliCode2.add("536871204");
                listPengaliCode2.add("536871205");

                listJumlahCode.add("536871046");
                listJumlahCode.add("536871047");
                listJumlahCode.add("536871045");
                listJumlahCode.add("536871043");

                //looping for BSTP Expense
                int loopingBSTP = 0;
                while (listRincianCode1.size() > loopingBSTP) {
                    if (settlementRecord.get(Integer.parseInt(listPengaliCode2.get(loopingBSTP))).getValue() != null) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = settlementRecord.get(Integer.parseInt(listRincianCode1.get(loopingBSTP))).getValue().toString()
                                + "(" + settlementRecord.get(Integer.parseInt(listRincianCode2.get(loopingBSTP))).getValue().toString() + ")";
                        travelExpense.pengali = (settlementRecord.get(Integer.parseInt(listPengaliCode1.get(loopingBSTP))).getValue().toString().equalsIgnoreCase("Pulang Pergi (PP)"))
                                ? "2 x " : "1 x ";
                        travelExpense.pengali += getIDRFormat(settlementRecord.get(Integer.parseInt(listPengaliCode2.get(loopingBSTP))).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(Integer.parseInt(listJumlahCode.get(loopingBSTP))).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }

                    loopingBSTP++;
                }

                //expense antar kota
                List<String> listKotaId = new ArrayList<String>();
                List<String> listKotaAmountId = new ArrayList<String>();
                List<String> listKotaTypeId = new ArrayList<String>();
                List<String> listKotaKmId = new ArrayList<String>();
                List<String> listTotalKm = new ArrayList<String>();

                listKotaId.add("536871136");
                listKotaId.add("536871140");
                listKotaId.add("536871144");
                listKotaId.add("536871148");

                listKotaAmountId.add("536871206");
                listKotaAmountId.add("536871209");
                listKotaAmountId.add("536871212");
                listKotaAmountId.add("536871213");

                listKotaTypeId.add("536871138");
                listKotaTypeId.add("536871142");
                listKotaTypeId.add("536871146");
                listKotaTypeId.add("536871150");

                listKotaKmId.add("536871208");
                listKotaKmId.add("536871210");
                listKotaKmId.add("536871211");
                listKotaKmId.add("536871214");

                listTotalKm.add("536871062");
                listTotalKm.add("536871063");
                listTotalKm.add("536871061");
                listTotalKm.add("536871059");

                int loopingAntarKota = 0;
                while (listKotaId.size() > loopingAntarKota) {
                    if (settlementRecord.get(Integer.parseInt(listKotaAmountId.get(loopingAntarKota))).getValue() != null) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = settlementRecord.get(Integer.parseInt(listKotaId.get(loopingAntarKota))).getValue().toString();
                        travelExpense.pengali = "";

                        //if(settlementRecord.get(Integer.parseInt(listKotaAmountId.get(loopingAntarKota))).getValue().toString().equalsIgnoreCase("0")) {
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(Integer.parseInt(listTotalKm.get(loopingAntarKota))).getValue().toString());
                        //} else {
                        //	travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(Integer.parseInt(listKotaAmountId.get(loopingAntarKota))).getValue().toString());
                        //}

                        listTravelExpense.add(travelExpense);
                    }

                    loopingAntarKota++;
                }

                //Laundry expense
                if (settlementRecord.get(536871032).getValue() != null) {
                    travelExpense = new TravelExpense();
                    travelExpense.rincian = "Laundry";
                    travelExpense.pengali = settlementRecord.get(536871168).getValue().toString()
                            + " x " + getIDRFormat(settlementRecord.get(536871167).getValue().toString());
                    travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871032).getValue().toString());
                    listTravelExpense.add(travelExpense);
                }

                //Daily Allowance
                if (settlementRecord.get(536871034).getValue() != null) {
                    if (settlementRecord.get(536871034).getIntValue() > 0) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = "Daily Allowance";
                        travelExpense.pengali = settlementRecord.get(536871174).getValue().toString()
                                + " x " + getIDRFormat(settlementRecord.get(536871173).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871034).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                }

                //kompensasi area tertentu
                if (settlementRecord.get(536871037).getValue() != null) {
                    if (settlementRecord.get(536871037).getIntValue() > 0) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = "Kompensasi Area Tertentu";
                        travelExpense.pengali = settlementRecord.get(536871177).getValue().toString()
                                + " x " + getIDRFormat(settlementRecord.get(536871176).getValue().toString());
                        travelExpense.jumlahTotal = getIDRFormat(settlementRecord.get(536871037).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                }

                //other expense
                List<String> otherExpenseTypeId = new ArrayList<String>();
                List<String> otherExpenseAmountId = new ArrayList<String>();
                List<String> otherExpenseKeteranganId = new ArrayList<String>();

                otherExpenseTypeId.add("536871152");
                otherExpenseTypeId.add("536871155");
                otherExpenseTypeId.add("536871158");
                otherExpenseTypeId.add("536871161");
                otherExpenseTypeId.add("536871178");

                otherExpenseAmountId.add("536871215");
                otherExpenseAmountId.add("536871216");
                otherExpenseAmountId.add("536871217");
                otherExpenseAmountId.add("536871218");
                otherExpenseAmountId.add("536871219");

                otherExpenseKeteranganId.add("536871154");
                otherExpenseKeteranganId.add("536871157");
                otherExpenseKeteranganId.add("536871160");
                otherExpenseKeteranganId.add("536871164");
                otherExpenseKeteranganId.add("536871180");

                // other expense
                int loopingOtherExpense = 0;
                while (otherExpenseTypeId.size() > loopingOtherExpense) {
                    if (settlementRecord.get(Integer.parseInt(otherExpenseAmountId.get(loopingOtherExpense))).getValue() != null) {
                        travelExpense = new TravelExpense();
                        travelExpense.rincian = settlementRecord.get(Integer.parseInt(otherExpenseTypeId.get(loopingOtherExpense))).getValue().toString();
                        travelExpense.keterangan = (settlementRecord.get(Integer.parseInt(otherExpenseKeteranganId.get(loopingOtherExpense))).getValue() == null)
                                ? " " : settlementRecord.get(Integer.parseInt(otherExpenseKeteranganId.get(loopingOtherExpense))).toString();
                        travelExpense.pengali = "";
                        travelExpense.jumlahTotal = getIDRFormat(
                                settlementRecord.get(Integer.parseInt(otherExpenseAmountId.get(loopingOtherExpense))).getValue().toString());
                        listTravelExpense.add(travelExpense);
                    }
                    loopingOtherExpense++;
                }

                int totalTobePaid = settlementRecord.get(536871056).getIntValue();
                //get advance payment
                if (settlementRecord.get(536870928).getValue() != null) {
                    travelExpense = new TravelExpense();
                    travelExpense.rincian = "Panjar Dinas";
                    travelExpense.pengali = "";
                    travelExpense.jumlahTotal = "-" + getIDRFormat(settlementRecord.get(536870928).getValue().toString());
                    listTravelExpense.add(travelExpense);
                    totalTobePaid -= settlementRecord.get(536870928).getIntValue();

                }

                //total expense
                travelExpense = new TravelExpense();
                travelExpense.rincian = "Total to be paid";
                travelExpense.pengali = "";
                travelExpense.jumlahTotal = getIDRFormat(String.valueOf(totalTobePaid));
                listTravelExpense.add(travelExpense);

                //get request ticket
                List<TravelRequestTiket> listTravelRequestTiket = getTravelRequestTiket(travelokaPdf.noTrip, remedySession);

                travelDocument.generateTravelSettlementPdf(travelokaPdf, listBookingTiket, listTravelExpense, listTravelRequestTiket);

                //update status print
                settlementRecord.put(536870913, new Value("1"));
                remedySession.setEntry(formSettlement, settlementRecord.getEntryId(), settlementRecord, null, 0);
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        return "testing";
    }

    public List<TravelRequestTiket> getTravelRequestTiket(String noTrip, ARServerUser remedySession) {
        List<TravelRequestTiket> listTravelRequestTiket = new ArrayList<TravelRequestTiket>();
        RemedyAPI remedyAPI = new RemedyAPI();
        String skpdForm = "PTM:SSC:HR:SKPD";

        List<EntryListInfo> listSKPD = remedyAPI.getRemedyRecordByQuery(
                remedySession,
                skpdForm,
                "'No Trip'=\"" + noTrip + "\" ");

        SimpleDateFormat mmddyyyyformat = new SimpleDateFormat("MM/dd/yyyy");
        java.util.Date utilDate;
        Date tanggalFormat;

        try {
            for (EntryListInfo skpdRequest : listSKPD) {
                TravelRequestTiket travelRequestTiket = new TravelRequestTiket();
                Entry skpdRecord = remedySession.getEntry(skpdForm, skpdRequest.getEntryID(), null);
                travelRequestTiket.jenisPerjalanan = skpdRecord.get(536870921).getValue().toString();
                travelRequestTiket.kotaAsal = skpdRecord.get(536871035).getValue().toString();
                travelRequestTiket.kotaTujuan = skpdRecord.get(536871132).getValue().toString();
                //travelRequestTiket.pulangDinas = skpdRecord.get(536870926).getValue().toString();
                utilDate = mmddyyyyformat.parse(skpdRecord.get(536870926).getValue().toString());
                tanggalFormat = new Date(utilDate.getTime());
                travelRequestTiket.pulangDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                //travelRequestTiket.tanggalDinas = skpdRecord.get(536870924).getValue().toString();
                utilDate = mmddyyyyformat.parse(skpdRecord.get(536870924).getValue().toString());
                tanggalFormat = new Date(utilDate.getTime());
                travelRequestTiket.tanggalDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                travelRequestTiket.requestNumber = skpdRecord.get(536870913).getValue().toString();
                travelRequestTiket.statusRequest = skpdRecord.get(536871136).getValue().toString();
                logger.info("nomer req=" + travelRequestTiket.requestNumber);

                listTravelRequestTiket.add(travelRequestTiket);
            }
        } catch (ARException | ParseException e) {
            logger.info("ARException Error on generate print settlement: " + e.toString());
        }

        return listTravelRequestTiket;
    }

    public List<BookingTraveloka> getTravelokaBooking(String requestNumber,
            String noTrip,
            ARServerUser remedySession) {
        List<BookingTraveloka> listBookingTiket = new ArrayList<BookingTraveloka>();
        RemedyAPI remedyAPI = new RemedyAPI();
        String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
        String mitraMainForm = "PTM:SSC:HR:Travel:INT:Mitra:MAIN";
        List<EntryListInfo> listTiketTraveloka = new ArrayList<EntryListInfo>();

        if (requestNumber != null) {
            //skpd request
            logger.info("cari ota untuk request=" + requestNumber);

            listTiketTraveloka = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    travelokaMainForm,
                    "'Req_Id'=\"" + requestNumber + "\" AND (Status__c=\"Assigned\" OR Status__c=\"Fixed\") ");

        } else {
            //settlement

            logger.info("cari ota untuk settlement=" + noTrip);
            listTiketTraveloka = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    travelokaMainForm,
                    "'Short Description__c'=\"" + noTrip + "\" AND (Status__c=\"Fixed\") ");

        }

        try {
            int hotelKe = 0;

            List<TravelHotelPrint> listHotel = new ArrayList<TravelHotelPrint>();
            List<TravelFlightPrint> listFlight = new ArrayList<TravelFlightPrint>();

            for (EntryListInfo tiketTraveloka : listTiketTraveloka) {

                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                bookingTraveloka.travelFlightPrint = new ArrayList<TravelFlightPrint>();
                bookingTraveloka.travelHotelPrint = new ArrayList<TravelHotelPrint>();
                Entry bookingRecord = remedySession.getEntry(travelokaMainForm, tiketTraveloka.getEntryID(), null);
                logger.info("jumlah array hotel=" + bookingTraveloka.travelHotelPrint.size());
                bookingTraveloka.bookingId = bookingRecord.get(536870915).getValue().toString();
                bookingTraveloka.productType = bookingRecord.get(536870920).getValue().toString();
                bookingTraveloka.totalFare = bookingRecord.get(536870919).getValue().toString();
                //bookingTraveloka.bookingDetail = "";
                logger.info("product type=" + hotelKe + "=" + bookingTraveloka.productType);

                if (bookingTraveloka.productType.equalsIgnoreCase("hotel")) {
                    //listHotel = new ArrayList<TravelHotelPrint>();
                    String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Traveloka:hotelProductDetail";
                    List<EntryListInfo> eListHotels = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaHotelForm, "'bookingId'=\"" + bookingTraveloka.bookingId + "\" ");
                    for (EntryListInfo eListHotel : eListHotels) {
                        TravelHotelPrint travelHotelPrint = new TravelHotelPrint();
                        Entry hotelRecord = remedySession.getEntry(travelokaHotelForm, eListHotel.getEntryID(), null);
                        SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date utilDateHotel;
                        try {
                            utilDateHotel = yyyymmddformat.parse(hotelRecord.get(536870919).getValue().toString());
                            Date tanggalHotelCheck = new Date(utilDateHotel.getTime());
                            travelHotelPrint.checkInDate = new SimpleDateFormat("dd MMM yyyy").format(tanggalHotelCheck);
                        } catch (ParseException e) {
                            logger.info("cannot convert date = " + e.toString());
                        }

                        travelHotelPrint.guestName = hotelRecord.get(536870928).getValue().toString();
                        travelHotelPrint.hotelCity = hotelRecord.get(536870917).getValue().toString();
                        travelHotelPrint.hotelCountry = hotelRecord.get(536870918).getValue().toString();
                        travelHotelPrint.hotelName = hotelRecord.get(536870916).getValue().toString();
                        travelHotelPrint.nights = hotelRecord.get(536870920).getValue().toString();
                        travelHotelPrint.rooms = hotelRecord.get(536870921).getValue().toString();
                        travelHotelPrint.pricePerNight = hotelRecord.get(536870925).getValue().toString().replace("IDR ", "");
                        //bookingTraveloka.travelHotelPrint.add(travelHotelPrint);
                        //bookingTraveloka.travelHotelPrint.add(hotelKe, travelHotelPrint);
                        listHotel.add(travelHotelPrint);

                        hotelKe++;
                    }
                    bookingTraveloka.travelHotelPrint = listHotel;
                    logger.info("jumlah hotel=" + bookingTraveloka.travelHotelPrint.size());
                } else if (bookingTraveloka.productType.equalsIgnoreCase("flight")) {
                    String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Traveloka:flightProductDetail";
                    List<EntryListInfo> eListPesawats = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            travelokaFlightForm,
                            "'bookingId__c'=\"" + bookingTraveloka.bookingId + "\" ");
                    SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                    for (EntryListInfo eListPesawat : eListPesawats) {
                        listFlight = new ArrayList<TravelFlightPrint>();
                        Entry pesawatRecord = remedySession.getEntry(travelokaFlightForm, eListPesawat.getEntryID(), null);
                        java.util.Date utilDate2;
                        TravelFlightPrint travelFlightPrint = new TravelFlightPrint();
                        try {
                            utilDate2 = yyyymmddformat.parse(pesawatRecord.get(536870918).getValue().toString());
                            logger.info("test date=" + utilDate2.toString());
                            Date tanggalFormatFlight = new Date(utilDate2.getTime());
                            travelFlightPrint.departureDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(tanggalFormatFlight);
                        } catch (ParseException e) {
                            logger.info("cannot convert date for flight" + e.toString());
                        }

                        travelFlightPrint.destinationAirport = pesawatRecord.get(536870917).getValue().toString();
                        travelFlightPrint.returnDate = pesawatRecord.get(536870919).getValue().toString();
                        travelFlightPrint.seatClass = pesawatRecord.get(536870921).getValue().toString();
                        travelFlightPrint.sourceAirport = pesawatRecord.get(536870916).getValue().toString();
                        travelFlightPrint.airlines = pesawatRecord.get(536870920).getValue().toString();

                        if (bookingTraveloka.bookingDetail == null) {
                            bookingTraveloka.bookingDetail = "";
                        }
                        bookingTraveloka.bookingDetail += travelFlightPrint.airlines + "\n"
                                + travelFlightPrint.sourceAirport + "-" + travelFlightPrint.destinationAirport + "\n"
                                + travelFlightPrint.departureDate + "\n"
                                + travelFlightPrint.seatClass + "\n\n";

                        logger.info("bookingdetail=" + bookingTraveloka.bookingDetail);
                        //travelFlightPrint.totalFare = ;
                        logger.info("travel size=" + bookingTraveloka.travelFlightPrint.size());
                        listFlight.add(travelFlightPrint);

                        bookingTraveloka.travelFlightPrint.add(travelFlightPrint);
                    }
                    bookingTraveloka.travelFlightPrint = listFlight;

                    // get passenger travelFlightPrint.passenger
                    String passengerForm = "PTM:SSC:HR:Travel:INT:Traveloka:Passenger";
                    String namaPassenger = "";
                    int jumlahPassenger = 0;
                    List<EntryListInfo> eListPassengers = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            passengerForm,
                            "'bookingId'=\"" + bookingTraveloka.bookingId + "\" ");
                    for (EntryListInfo eListPassenger : eListPassengers) {
                        Entry passengerRecord = remedySession.getEntry(passengerForm, eListPassenger.getEntryID(), null);
                        if (jumlahPassenger > 0) {
                            namaPassenger += "\n";
                        }
                        namaPassenger += (passengerRecord.get(536870917).getValue() == null)
                                ? "" : passengerRecord.get(536870917).getValue().toString();
                        namaPassenger += (passengerRecord.get(536870918).getValue() == null)
                                ? "" : " " + passengerRecord.get(536870918).getValue().toString();
                        namaPassenger += (passengerRecord.get(536870919).getValue() == null)
                                ? "" : "(" + passengerRecord.get(536870919).getValue().toString() + ")";
                        jumlahPassenger++;
                    }
                    bookingTraveloka.penumpang = namaPassenger;
                }
                listBookingTiket.add(bookingTraveloka);
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate print settlement: " + e.toString());
        }

        return listBookingTiket;
    }
    
     public List<BookingTraveloka> getMitraBooking(String requestNumber,
            String noTrip,
            ARServerUser remedySession) {
        List<BookingTraveloka> listBookingTiket = new ArrayList<BookingTraveloka>();
        RemedyAPI remedyAPI = new RemedyAPI();
        String mitraMainForm = "PTM:SSC:HR:Travel:INT:Mitra:MAIN";
        List<EntryListInfo> listTiketTraveloka = new ArrayList<EntryListInfo>();

        if (requestNumber != null) {
            //skpd request
            logger.info("cari ota untuk request=" + requestNumber);

            listTiketTraveloka = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    mitraMainForm,
                    "'Req_Id'=\"" + requestNumber + "\" AND (Status__c=\"Assigned\" OR Status__c=\"Fixed\") ");

        } else {
            //settlement

            logger.info("cari ota untuk settlement=" + noTrip);
            listTiketTraveloka = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    mitraMainForm,
                    "'Short Description'=\"" + noTrip + "\" AND (Status=\"Fixed\") ");

        }

        try {
            int hotelKe = 0;

            List<TravelHotelPrint> listHotel = new ArrayList<TravelHotelPrint>();
            List<TravelFlightPrint> listFlight = new ArrayList<TravelFlightPrint>();

            for (EntryListInfo tiketTraveloka : listTiketTraveloka) {

                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                bookingTraveloka.travelFlightPrint = new ArrayList<TravelFlightPrint>();
                bookingTraveloka.travelHotelPrint = new ArrayList<TravelHotelPrint>();
                Entry bookingRecord = remedySession.getEntry(mitraMainForm, tiketTraveloka.getEntryID(), null);
                logger.info("jumlah array hotel=" + bookingTraveloka.travelHotelPrint.size());
                bookingTraveloka.bookingId = bookingRecord.get(536870915).getValue().toString();
                bookingTraveloka.productType = bookingRecord.get(536870920).getValue().toString();
                bookingTraveloka.totalFare = bookingRecord.get(536870919).getValue().toString();
                //bookingTraveloka.bookingDetail = "";
                logger.info("product type=" + hotelKe + "=" + bookingTraveloka.productType);

                if (bookingTraveloka.productType.equalsIgnoreCase("hotel")) {
                    //listHotel = new ArrayList<TravelHotelPrint>();
                    String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Mitra:hotelProductDetail";
                    List<EntryListInfo> eListHotels = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaHotelForm, "'bookingId__c'=\"" + bookingTraveloka.bookingId + "\" ");
                    for (EntryListInfo eListHotel : eListHotels) {
                        TravelHotelPrint travelHotelPrint = new TravelHotelPrint();
                        Entry hotelRecord = remedySession.getEntry(travelokaHotelForm, eListHotel.getEntryID(), null);
                        SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date utilDateHotel;
                        try {
                            utilDateHotel = yyyymmddformat.parse(hotelRecord.get(536870919).getValue().toString());
                            Date tanggalHotelCheck = new Date(utilDateHotel.getTime());
                            travelHotelPrint.checkInDate = new SimpleDateFormat("dd MMM yyyy").format(tanggalHotelCheck);
                        } catch (ParseException e) {
                            logger.info("cannot convert date = " + e.toString());
                        }

                        travelHotelPrint.guestName = hotelRecord.get(536870928).getValue().toString();
                        travelHotelPrint.hotelCity = hotelRecord.get(536870917).getValue().toString();
                        travelHotelPrint.hotelCountry = hotelRecord.get(536870918).getValue().toString();
                        travelHotelPrint.hotelName = hotelRecord.get(536870916).getValue().toString();
                        travelHotelPrint.nights = hotelRecord.get(536870920).getValue().toString();
                        travelHotelPrint.rooms = hotelRecord.get(536870921).getValue().toString();
                        travelHotelPrint.pricePerNight = hotelRecord.get(536870925).getValue().toString().replace("IDR ", "");
                        //bookingTraveloka.travelHotelPrint.add(travelHotelPrint);
                        //bookingTraveloka.travelHotelPrint.add(hotelKe, travelHotelPrint);
                        listHotel.add(travelHotelPrint);

                        hotelKe++;
                    }
                    bookingTraveloka.travelHotelPrint = listHotel;
                    logger.info("jumlah hotel=" + bookingTraveloka.travelHotelPrint.size());
                } else if (bookingTraveloka.productType.equalsIgnoreCase("flight")) {
                    String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Mitra:flightProductDetail";
                    List<EntryListInfo> eListPesawats = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            travelokaFlightForm,
                            "'bookingId__c'=\"" + bookingTraveloka.bookingId + "\" ");
                    SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                    for (EntryListInfo eListPesawat : eListPesawats) {
                        listFlight = new ArrayList<TravelFlightPrint>();
                        Entry pesawatRecord = remedySession.getEntry(travelokaFlightForm, eListPesawat.getEntryID(), null);
                        java.util.Date utilDate2;
                        TravelFlightPrint travelFlightPrint = new TravelFlightPrint();
                        try {
                            utilDate2 = yyyymmddformat.parse(pesawatRecord.get(536870918).getValue().toString());
                            logger.info("test date=" + utilDate2.toString());
                            Date tanggalFormatFlight = new Date(utilDate2.getTime());
                            travelFlightPrint.departureDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(tanggalFormatFlight);
                        } catch (ParseException e) {
                            logger.info("cannot convert date for flight" + e.toString());
                        }

                        travelFlightPrint.destinationAirport = pesawatRecord.get(536870917).getValue().toString();
                        travelFlightPrint.returnDate = pesawatRecord.get(536870919).getValue().toString();
                        travelFlightPrint.seatClass = pesawatRecord.get(536870921).getValue().toString();
                        travelFlightPrint.sourceAirport = pesawatRecord.get(536870916).getValue().toString();
                        travelFlightPrint.airlines = pesawatRecord.get(536870920).getValue().toString();

                        if (bookingTraveloka.bookingDetail == null) {
                            bookingTraveloka.bookingDetail = "";
                        }
                        bookingTraveloka.bookingDetail += travelFlightPrint.airlines + "\n"
                                + travelFlightPrint.sourceAirport + "-" + travelFlightPrint.destinationAirport + "\n"
                                + travelFlightPrint.departureDate + "\n"
                                + travelFlightPrint.seatClass + "\n\n";

                        logger.info("bookingdetail=" + bookingTraveloka.bookingDetail);
                        //travelFlightPrint.totalFare = ;
                        logger.info("travel size=" + bookingTraveloka.travelFlightPrint.size());
                        listFlight.add(travelFlightPrint);

                        bookingTraveloka.travelFlightPrint.add(travelFlightPrint);
                    }
                    bookingTraveloka.travelFlightPrint = listFlight;

                    // get passenger travelFlightPrint.passenger
                    String passengerForm = "PTM:SSC:HR:Travel:INT:Mitra:flightPassenger";
                    String namaPassenger = "";
                    int jumlahPassenger = 0;
                    List<EntryListInfo> eListPassengers = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            passengerForm,
                            "'bookingId__c'=\"" + bookingTraveloka.bookingId + "\" ");
                    for (EntryListInfo eListPassenger : eListPassengers) {
                        Entry passengerRecord = remedySession.getEntry(passengerForm, eListPassenger.getEntryID(), null);
                        if (jumlahPassenger > 0) {
                            namaPassenger += "\n";
                        }
                        namaPassenger += (passengerRecord.get(536870917).getValue() == null)
                                ? "" : passengerRecord.get(536870917).getValue().toString();
                        namaPassenger += (passengerRecord.get(536870918).getValue() == null)
                                ? "" : " " + passengerRecord.get(536870918).getValue().toString();
                        namaPassenger += (passengerRecord.get(536870919).getValue() == null)
                                ? "" : "(" + passengerRecord.get(536870919).getValue().toString() + ")";
                        jumlahPassenger++;
                    }
                    bookingTraveloka.penumpang = namaPassenger;
                }
                listBookingTiket.add(bookingTraveloka);
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate print settlement: " + e.toString());
        }

        return listBookingTiket;
    }

    @RequestMapping(value = "/testing", method = RequestMethod.GET)
    public String testSch() {
        //SOAPGenerator soapGenerator = new SOAPGenerator();
        //soapGenerator.createSOAPMessage();
        long epoch = System.currentTimeMillis();
        long epoch2 = Long.parseLong("4165486939");
        //1541710164043
        Timestamp ts2 = new Timestamp(epoch2);
        logger.info("ts diterima=" + ts2.toString());

        epoch2 -= 600000;
        Timestamp ts3 = new Timestamp(epoch2);
        logger.info("ts 10 menit=" + ts3.toString());

        String testingResult = "hasil long=" + epoch + "<br/>";
        logger.info("hasil long=" + epoch);
        Timestamp ts = new Timestamp(epoch);
        testingResult += "hasil timestamp=" + ts.toString();
        logger.info("ts=" + ts.toString());
        logger.info("++++++++++++++++++++++ testing jee++++++++++++++++++++++++");
        Map<String, String> model = new HashMap<String, String>();
        model.put("testingResult", testingResult);

        return "testing";
    }

    @RequestMapping(value = "/testingsap", method = RequestMethod.GET)
    public String testSap() {
        //SAPSOAPGenerator sapSoapGenerator = new SAPSOAPGenerator();
        //sapSoapGenerator.createSOAPMessage();
        SAPController sapController = new SAPController();
        sapController.sendToSAP();
        logger.info("++++++++++++++++++++++ testing sap++++++++++++++++++++++++");

        return "testing";
    }

    @RequestMapping(value = "/testingreceiptsap", method = RequestMethod.GET)
    public String testReceipt() {
        //SAPSOAPGenerator sapSoapGenerator = new SAPSOAPGenerator();
        //sapSoapGenerator.createSOAPMessage();
        SAPController sapController = new SAPController();
        sapController.sendToSAP();
        logger.info("++++++++++++++++++++++ testing sap++++++++++++++++++++++++");

        return "testing";
    }

    @RequestMapping(value = "/testingOkPayment", method = RequestMethod.GET)
    public String testOkPayment() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        TravelokaOKPayment travelokaOKPayment = new TravelokaOKPayment();
        String okPaymentForm = "PTM:SSC:HR:Travel:INT:Traveloka:OKPayment";
        final String uri = configValue.getTravelokaPayment();

        //setting proxy
        ProxySearch proxySearch = new ProxySearch();
        if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
            logger.info("linux");
            //proxySearch.addStrategy(Strategy.GNOME);
            //proxySearch.addStrategy(Strategy.KDE);
            proxySearch.addStrategy(Strategy.FIREFOX);
        } else {
            logger.info("non unix");
            proxySearch.addStrategy(Strategy.OS_DEFAULT);
        }
        ProxySelector myProxySelector = proxySearch.getProxySelector();

        ProxySelector.setDefault(myProxySelector);
        logger.info("proxy nya:" + myProxySelector.toString());
        /*
		try {
			
			//URL url = new URL("https://ext-api-corporate.test.traveloka.com/paymentConfirmation");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
		    int status = con.getResponseCode();
		    
		    RestTemplate restTemplate = new RestTemplate();
		    //TravelokaResponse travelokaResponse = restTemplate.postForObject(uri, travelokaOKPayment, TravelokaResponse.class);
			//String testtrvl = restTemplate.postForObject(uri, travelokaOKPayment, String.class);
			//logger.info("rest="+restTemplate.getRequestFactory().toString());
			//logger.info("hasil traveloka="+testtrvl);
			
		    if (status == 200){
		    		logger.info("Connection is OK: ");
		    } else {
		    		logger.info("Http responde code: "+status);
		    }
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("error notes:"+e.toString());
		}
         */

        //TravelokaResponse travelokaResponse = restTemplate.postForObject(uri, travelokaOKPayment, TravelokaResponse.class);
        //travelokaResponse = restTemplate.postForObject(uri, travelokaIssuanceTicket, TravelokaResponse.class);
        //String testtrvl = restTemplate.postForObject(uri, travelokaIssuanceTicket, String.class);
        List<EntryListInfo> eListPayments = remedyAPI.getRemedyRecordByQuery(remedySession, okPaymentForm, "'Status__c'=\"0\" ");
        try {
            for (EntryListInfo eListPayment : eListPayments) {
                URL url = new URL(configValue.getTravelokaPayment());

                Entry recordPayment = remedySession.getEntry(okPaymentForm, eListPayment.getEntryID(), null);

                travelokaOKPayment = new TravelokaOKPayment();
                travelokaOKPayment.approvalRequestId = recordPayment.get(536870913).getValue().toString();
                travelokaOKPayment.approvalStatus = recordPayment.get(536870914).getValue().toString();
                travelokaOKPayment.approverEmail = recordPayment.get(536870916).getValue().toString();
                travelokaOKPayment.reason = recordPayment.get(536870915).getValue().toString();
                travelokaOKPayment.remark = recordPayment.get(536870918).getValue().toString();
                Timestamp timestampApproval = new Timestamp(System.currentTimeMillis());
                travelokaOKPayment.approvalTimestamp = timestampApproval.getTime();

                boolean limitValid = true;
                //cek time limit
                String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(
                        remedySession, travelokaMainForm, "'Status__c'=\"2\" AND 'Req_Id'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                for (EntryListInfo eListTraveloka : eListTravelokas) {
                    Entry travelokaMainRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                    Long timeLimitTraveloka = Long.parseLong(travelokaMainRecord.get(536870917).getValue().toString());
                    if (timeLimitTraveloka < travelokaOKPayment.approvalTimestamp) {
                        limitValid = false;
                    }
                }

                if (limitValid) {
                    logger.info("time limit is valid");
                    //setting proxy
                    //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.17.3.155", 8080));
                    //SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();

                    logger.info("testing proxy");
                    //clientHttpReq.setProxy(proxy);
                    //RestTemplate restTemplate = new RestTemplate(clientHttpReq);
                    RestTemplate restTemplate = new RestTemplate();
                    logger.info("Request send to traveloka");
                    logger.info("approvalRequestId=" + travelokaOKPayment.approvalRequestId);

                    String testtrvl = restTemplate.postForObject(uri, travelokaOKPayment, String.class);
                    logger.info("hasil traveloka=" + testtrvl);
                    recordPayment.put(4, new Value(testtrvl));
                    recordPayment.put(7, new Value("1"));
                    remedySession.setEntry(okPaymentForm, recordPayment.getEntryId(), recordPayment, null, 0);
                } else {
                    logger.info("time limit is invalid");
                    List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                    for (EntryListInfo eListReq : eListReqs) {
                        Entry recordReq = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                        recordReq.put(7, new Value("7000"));
                        remedySession.setEntry("SRM:Request", recordReq.getEntryId(), recordReq, null, 0);
                    }
                }

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("error notes:" + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/okPayment", method = RequestMethod.GET)
    public String okPayment() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        TravelokaOKPayment travelokaOKPayment = new TravelokaOKPayment();
        String okPaymentForm = "PTM:SSC:HR:Travel:INT:Traveloka:OKPayment";
        //final String uri = "https://ext-api-corporate.test.traveloka.com/paymentConfirmation";
        final String uri = "https://external-api.ctv.staging-traveloka.com/paymentConfirmation";
//        final String uri = configValue.getTravelokaPayment();

        List<EntryListInfo> eListPayments = remedyAPI.getRemedyRecordByQuery(remedySession, okPaymentForm, "'Status__c'=\"0\" ");
        try {
            for (EntryListInfo eListPayment : eListPayments) {
                //URL url = new URL("https://ext-api-corporate.test.traveloka.com/paymentConfirmation");

                Entry recordPayment = remedySession.getEntry(okPaymentForm, eListPayment.getEntryID(), null);

                travelokaOKPayment = new TravelokaOKPayment();
                travelokaOKPayment.approvalRequestId = recordPayment.get(536870913).getValue().toString();
                travelokaOKPayment.approvalStatus = recordPayment.get(536870914).getValue().toString();
                travelokaOKPayment.approverEmail = recordPayment.get(536870916).getValue().toString();
                travelokaOKPayment.reason = recordPayment.get(536870915).getValue().toString();
                travelokaOKPayment.remark = recordPayment.get(536870918).getValue().toString();
                Timestamp timestampApproval = new Timestamp(System.currentTimeMillis());
                travelokaOKPayment.approvalTimestamp = timestampApproval.getTime();
                logger.info("test approval req id=" + travelokaOKPayment.approvalRequestId);

                boolean limitValid = true;

                //cek time limit
                String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(
                        remedySession, travelokaMainForm, "'Req_Id'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                //remedySession, travelokaMainForm, "'Status__c'=\"2\" AND 'Req_Id'=\""+recordPayment.get(8).getValue().toString()+"\" ");
                for (EntryListInfo eListTraveloka : eListTravelokas) {
                    Entry travelokaMainRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                    Long timeLimitTraveloka = Long.parseLong(travelokaMainRecord.get(536870917).getValue().toString());
                    //Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    Instant instant = Instant.now();
                    travelokaOKPayment.approvalTimestamp = instant.toEpochMilli();
                    logger.info("epoch current time=" + instant.toEpochMilli());
                    timeLimitTraveloka -= 600000; //dikurangi 10 menit untuk menghindari perbedaan jam antara bmc dan traveloka
                    if (timeLimitTraveloka < instant.toEpochMilli()) {
                        limitValid = false;
                    }
                }

                if (limitValid) {
                    logger.info("time limit is valid");
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    String pertaminaProxy = configValue.getPertaminaProxy();
                    logger.info("Using Proxy=" + pertaminaProxy);

                    Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(pertaminaProxy, 8080));
                    requestFactory.setProxy(proxy);

                    RestTemplate restTemplate = new RestTemplate(requestFactory);
                    logger.info("Request send to traveloka");
                    logger.info("approvalRequestId=" + travelokaOKPayment.approvalRequestId);

                    String testtrvl = restTemplate.postForObject(uri, travelokaOKPayment, String.class);
                    logger.info("hasil traveloka=" + testtrvl);
                    recordPayment.put(4, new Value(testtrvl));
                    recordPayment.put(7, new Value("1"));
                    remedySession.setEntry(okPaymentForm, recordPayment.getEntryId(), recordPayment, null, 0);
                } else {
                    logger.info("time limit is invalid");
                    List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                    for (EntryListInfo eListReq : eListReqs) {
                        Entry recordReq = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                        recordReq.put(7, new Value("7000"));
                        remedySession.setEntry("SRM:Request", recordReq.getEntryId(), recordReq, null, 0);

                        //kirim notifikasi untuk time limit
                        addWorkInfo("Ticket is rejected",
                                "Booking expired, ticket is rejected",
                                recordReq.get(536871038).getValue().toString(),
                                remedySession);
                    }
                }

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }/*catch (IOException e) {
			e.printStackTrace();
			logger.info("error notes:"+e.toString());
		}*/


        return "testing";
    }

    @RequestMapping(value = "/mitraBooking", method = RequestMethod.GET)
    public String mitraBooking() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        MitraBooking mitraBooking = new MitraBooking();
        String okPaymentForm = "PTM:SSC:HR:Travel:BookingDetail";
        //final String uri = "https://ext-api-corporate.test.traveloka.com/paymentConfirmation";
        final String uri = "https://pertamina.opsicorp.com/api/skpd/insert";

        List<EntryListInfo> eListPayments = remedyAPI.getRemedyRecordByQuery(remedySession, okPaymentForm, "'Status'=\"0\" ");
        try {
            for (EntryListInfo eListPayment : eListPayments) {
                //URL url = new URL("https://ext-api-corporate.test.traveloka.com/paymentConfirmation");

                Entry recordPayment = remedySession.getEntry(okPaymentForm, eListPayment.getEntryID(), null);

                mitraBooking = new MitraBooking();
                mitraBooking.trNumber = recordPayment.get(536870914).getValue().toString();
                mitraBooking.travelStartDate = recordPayment.get(536870914).getValue().toString();
                mitraBooking.travelEndDate = recordPayment.get(536870916).getValue().toString();
                mitraBooking.employeeId = recordPayment.get(536870919).getValue().toString();
                mitraBooking.employeeId = recordPayment.get(536870919).getValue().toString();
                mitraBooking.origin = recordPayment.get(536870918).getValue().toString();
                mitraBooking.destination1 = recordPayment.get(536870918).getValue().toString();
                mitraBooking.destination2 = recordPayment.get(536870918).getValue().toString();
                mitraBooking.destination3 = recordPayment.get(536870918).getValue().toString();
//                mitraBooking.approvalTimestamp = recordPayment.get(536870918).getValue().toString();
                mitraBooking.approverEmail = recordPayment.get(536870918).getValue().toString();
                mitraBooking.tripPurpose = recordPayment.get(536870918).getValue().toString();
                mitraBooking.remark = recordPayment.get(536870918).getValue().toString();
                Timestamp timestampApproval = new Timestamp(System.currentTimeMillis());
                mitraBooking.approvalTimestamp = timestampApproval.getTime();
                logger.info("test approval req id=" + mitraBooking.approvalTimestamp);

                boolean limitValid = true;

                //cek time limit
                String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(
                        remedySession, travelokaMainForm, "'Req_Id'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                //remedySession, travelokaMainForm, "'Status__c'=\"2\" AND 'Req_Id'=\""+recordPayment.get(8).getValue().toString()+"\" ");
                for (EntryListInfo eListTraveloka : eListTravelokas) {
                    Entry travelokaMainRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                    Long timeLimitTraveloka = Long.parseLong(travelokaMainRecord.get(536870917).getValue().toString());
                    //Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    Instant instant = Instant.now();
                    mitraBooking.approvalTimestamp = instant.toEpochMilli();
                    logger.info("epoch current time=" + instant.toEpochMilli());
                    timeLimitTraveloka -= 600000; //dikurangi 10 menit untuk menghindari perbedaan jam antara bmc dan traveloka
                    if (timeLimitTraveloka < instant.toEpochMilli()) {
                        limitValid = false;
                    }
                }

                if (limitValid) {
                    logger.info("time limit is valid");
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    String pertaminaProxy = configValue.getPertaminaProxy();
                    logger.info("Using Proxy=" + pertaminaProxy);

                    Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(pertaminaProxy, 8080));
                    requestFactory.setProxy(proxy);

                    RestTemplate restTemplate = new RestTemplate(requestFactory);
                    logger.info("Request send to traveloka");
                    logger.info("approvalRequestId=" + mitraBooking.trNumber);

                    String testtrvl = restTemplate.postForObject(uri, mitraBooking, String.class);
                    logger.info("hasil traveloka=" + testtrvl);
                    recordPayment.put(4, new Value(testtrvl));
                    recordPayment.put(7, new Value("1"));
                    remedySession.setEntry(okPaymentForm, recordPayment.getEntryId(), recordPayment, null, 0);
                } else {
                    logger.info("time limit is invalid");
                    List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + recordPayment.get(8).getValue().toString() + "\" ");
                    for (EntryListInfo eListReq : eListReqs) {
                        Entry recordReq = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                        recordReq.put(7, new Value("7000"));
                        remedySession.setEntry("SRM:Request", recordReq.getEntryId(), recordReq, null, 0);

                        //kirim notifikasi untuk time limit
                        addWorkInfo("Ticket is rejected",
                                "Booking expired, ticket is rejected",
                                recordReq.get(536871038).getValue().toString(),
                                remedySession);
                    }
                }

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }/*catch (IOException e) {
			e.printStackTrace();
			logger.info("error notes:"+e.toString());
		}*/


        return "testing";
    }

    @RequestMapping(value = "/printtravel", method = RequestMethod.GET)
    public String testPrintTravel() {
        logger.info("++++++++++++++++++++++ testing jee++++++++++++++++++++++++");
        RemedyController remedyController = new RemedyController();
        remedyController.generatePrintTravelAdvance();

        return "testing";
    }

    @RequestMapping(value = "/testPing")
    public String testPing() {
        try {
            //checking connection before call
            URL url = new URL("https://www.google.co.id/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int status = con.getResponseCode();
            if (status == 200) {
                logger.info("Connection is OK: ");
            } else {
                logger.info("Http responde code: " + status);
            }
        } catch (UnsupportedOperationException e) {
            logger.info("error:" + e.toString());
        } catch (MalformedURLException e) {
            logger.info("error:" + e.toString());
        } catch (IOException e) {
            logger.info("error:" + e.toString());
        }

        return "testing";
    }

    @RequestMapping(value = "/testpdf")
    public String testpdf() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        TravelokaPDF travelokaPdf = new TravelokaPDF();
        TravelDocument travelDocument = new TravelDocument();
        long approvalTimeLimit = 1;

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:Printedpdf", "'Status__c'=\"0\" ");
        try {
            for (EntryListInfo eListInfo : eListInfos) {
                Entry pdfRecord = remedySession.getEntry("PTM:SSC:HR:Travel:Printedpdf", eListInfo.getEntryID(), null);
                String reqNumber = pdfRecord.get(536870913).getValue().toString();
                approvalTimeLimit = 1;

                //query to get req detail
                List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + reqNumber + "\" ");
                for (EntryListInfo eListReq : eListReqs) {
                    Entry reqRecord = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                    travelokaPdf.requstNumber = reqNumber;
                    travelokaPdf.srInstanceId = reqRecord.get(179).getValue().toString();
                    travelokaPdf.noTrip = reqRecord.get(536870973).getValue().toString();
                    travelokaPdf.jenisPerjalanan = reqRecord.get(536870967).getValue().toString();
                    travelokaPdf.pekerja = reqRecord.get(1000003297).getValue().toString()
                            //+ " " + reqRecord.get(300890310).getValue().toString() 
                            + " " + reqRecord.get(1000003298).getValue().toString();
                    travelokaPdf.sifatPerjalanan = reqRecord.get(536870968).getValue().toString();
                    travelokaPdf.tanggalDinas = reqRecord.get(536871014).getValue().toString();
                    travelokaPdf.alokasiCostCenter = reqRecord.get(536871024).getValue().toString();
                    travelokaPdf.pulangDinas = reqRecord.get(536871015).getValue().toString();
                    //travelokaPdf.estimatedCost = "123";
                    //travelokaPdf.alasanPerjalanan = reqRecord.get(
                    //travelokaPdf.totalBiayaOTA = reqRecord.get(

                    travelokaPdf.kotaAsal = reqRecord.get(536870970).getValue().toString();
                    travelokaPdf.kotatujuan1 = reqRecord.get(536870981).getValue().toString();
                    //travelokaPdf.tgltujuan1 = reqRecord.get(536871048).getValue().toString();
                    //travelokaPdf.kotatujuan2 = reqRecord.get(536870982).getValue().toString();
                    //travelokaPdf.tgltujuan2 = reqRecord.get(536871049).getValue().toString();
                    //travelokaPdf.kotatujuan3 = reqRecord.get(536870983).getValue().toString();
                    //travelokaPdf.tgltujuan3 = reqRecord.get(536871050).getValue().toString();

                    String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                    int bookingLoop = 0;
                    Double totalHargaOTA = (double) 0;
                    TravelHotelPrint travelHotelPrint = new TravelHotelPrint();
                    TravelFlightPrint travelFlightPrint = new TravelFlightPrint();

                    List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaMainForm, "'Req_Id'=\"" + reqNumber + "\" AND Status__c=\"Assigned\"");
                    for (EntryListInfo eListTraveloka : eListTravelokas) {
                        Entry bookingRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                        bookingLoop++;
                        String bookingType = bookingRecord.get(536870920).getValue().toString();
                        String bookingId = bookingRecord.get(536870915).getValue().toString();

                        if (bookingRecord.get(536870919).getValue() != null) {
                            totalHargaOTA += Double.valueOf(bookingRecord.get(536870919).getValue().toString());
                        }

                        if (bookingRecord.get(536870919).getValue() != null) {
                            long timeLimitBooking = Long.parseLong(bookingRecord.get(536870917).getValue().toString());
                            if (approvalTimeLimit == 1) {
                                approvalTimeLimit = timeLimitBooking;
                            } else {
                                if (approvalTimeLimit > timeLimitBooking) {
                                    approvalTimeLimit = timeLimitBooking;
                                }
                            }
                        }

                        if (bookingType.equalsIgnoreCase("hotel")) {
                            String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Traveloka:hotelProductDetail";
                            List<EntryListInfo> eListHotels = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaHotelForm, "'bookingId'=\"" + bookingId + "\" ");
                            for (EntryListInfo eListHotel : eListHotels) {
                                Entry hotelRecord = remedySession.getEntry(travelokaHotelForm, eListHotel.getEntryID(), null);
                                travelHotelPrint.checkInDate = hotelRecord.get(536870919).getValue().toString();
                                travelHotelPrint.guestName = hotelRecord.get(536870928).getValue().toString();
                                travelHotelPrint.hotelCity = hotelRecord.get(536870917).getValue().toString();
                                travelHotelPrint.hotelCountry = hotelRecord.get(536870918).getValue().toString();
                                travelHotelPrint.hotelName = hotelRecord.get(536870916).getValue().toString();
                                travelHotelPrint.nights = hotelRecord.get(536870920).getValue().toString();
                                travelHotelPrint.rooms = hotelRecord.get(536870921).getValue().toString();
                                travelHotelPrint.pricePerNight = hotelRecord.get(536870925).getValue().toString().replace("IDR ", "");
                            }

                        } else {
                            String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Traveloka:flightProductDetail";
                            List<EntryListInfo> eListPesawats = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaFlightForm, "'bookingId__c'=\"" + bookingId + "\" ");
                            for (EntryListInfo eListPesawat : eListPesawats) {
                                Entry pesawatRecord = remedySession.getEntry(travelokaFlightForm, eListPesawat.getEntryID(), null);
                                travelFlightPrint.departureDate = pesawatRecord.get(536870918).getValue().toString();
                                travelFlightPrint.destinationAirport = pesawatRecord.get(536870917).getValue().toString();
                                //travelFlightPrint.passenger
                                travelFlightPrint.returnDate = pesawatRecord.get(536870919).getValue().toString();
                                travelFlightPrint.seatClass = pesawatRecord.get(536870921).getValue().toString();
                                travelFlightPrint.sourceAirport = pesawatRecord.get(536870916).getValue().toString();
                                //travelFlightPrint.totalFare = ;
                            }
                        }

                    }

                    String skpdTravelForm = "PTM:SSC:HR:SKPD";
                    List<EntryListInfo> eListEcosts = remedyAPI.getRemedyRecordByQuery(remedySession, skpdTravelForm, "'SR-Request ID'=\"" + reqNumber + "\" ");
                    SKPDPrintPdf skpdPrintPdf = new SKPDPrintPdf();
                    for (EntryListInfo eListEcost : eListEcosts) {
                        Entry ecostRecord = remedySession.getEntry(skpdTravelForm, eListEcost.getEntryID(), null);
                        int totalHari = Integer.parseInt(ecostRecord.get(536870988).getValue().toString());
                        int totalHariMenginap = totalHari - 1;

                        skpdPrintPdf.pengaliKompensasi = (ecostRecord.get(536870988).getValue() == null) ? " "
                                : ecostRecord.get(536870988).getValue().toString() + " * ";
                        skpdPrintPdf.pengaliKompensasi += (ecostRecord.get(536870993).getValue() == null) ? " "
                                : ecostRecord.get(536870993).getValue().toString();

                        //skpdPrintPdf.pengaliKompensasi = ecostRecord.get(536870988).getValue().toString()+" * " + ecostRecord.get(536870993).getValue().toString();
                        skpdPrintPdf.kompensasiHarian = (ecostRecord.get(536871135).getValue() == null)
                                ? " " : ecostRecord.get(536871135).getValue().toString();
                        skpdPrintPdf.pengaliHotel = totalHariMenginap + " * " + ecostRecord.get(536871078).getValue().toString();
                        skpdPrintPdf.pengaliPesawat = "";
                        skpdPrintPdf.ecostPesawat = ecostRecord.get(536871019).getValue().toString();
                        skpdPrintPdf.ecostHotel = ecostRecord.get(536871078).getValue().toString();
                        skpdPrintPdf.totalEcost = ecostRecord.get(536871105).getValue().toString();
                        skpdPrintPdf.alasanPerjalanan = ecostRecord.get(536870922).getValue().toString();
                    }

                    if (approvalTimeLimit > 1) {
                        approvalTimeLimit -= 600000;
                        Timestamp tsLimit = new Timestamp(approvalTimeLimit);
                        travelokaPdf.approvalTimeLimit = tsLimit.toString() + " WIB";
                    }
                    travelokaPdf.totalBiayaOTA = totalHargaOTA.toString();

                    travelDocument.generateApprovalPdf(
                            travelokaPdf,
                            travelHotelPrint,
                            travelFlightPrint,
                            skpdPrintPdf);
                }

                //update status pdf middle form
                pdfRecord.put(7, new Value("1"));
                remedySession.setEntry("PTM:SSC:HR:Travel:Printedpdf", pdfRecord.getEntryId(), pdfRecord, null, 0);

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        //catch(NullPointerException e) {
        //	logger.info("NullPointer exception:" +e.toString());
        //}
        return "testing";
    }

    @RequestMapping(value = "/testnewpdf")
    public String testnewpdf() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        long approvalTimeLimit = 1;

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:Printedpdf", "'Status__c'=\"0\" ");
        //List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:Printedpdf", "'Request Number__c'=\"REQ000000005612\" ");
        SimpleDateFormat mmddyyyyformat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                TravelokaPDF travelokaPdf = new TravelokaPDF();
                TravelDocument travelDocument = new TravelDocument();
                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                Entry pdfRecord = remedySession.getEntry("PTM:SSC:HR:Travel:Printedpdf", eListInfo.getEntryID(), null);
                String reqNumber = pdfRecord.get(536870913).getValue().toString();
                approvalTimeLimit = 1;

                //query to get req detail
                List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + reqNumber + "\" ");
                for (EntryListInfo eListReq : eListReqs) {
                    Entry reqRecord = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                    travelokaPdf.requstNumber = reqNumber;
                    travelokaPdf.srInstanceId = reqRecord.get(179).getValue().toString();
                    travelokaPdf.noTrip = reqRecord.get(536870973).getValue().toString();
                    travelokaPdf.jenisPerjalanan = reqRecord.get(536870967).getValue().toString();
                    travelokaPdf.pekerja = reqRecord.get(1000003297).getValue().toString()
                            //+ " " + reqRecord.get(300890310).getValue().toString() 
                            + " " + reqRecord.get(1000003298).getValue().toString();
                    travelokaPdf.sifatPerjalanan = reqRecord.get(536870968).getValue().toString();
                    try {
                        java.util.Date utilDate = mmddyyyyformat.parse(reqRecord.get(536871014).getValue().toString());
                        Date tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.tanggalDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        logger.info("test1=" + travelokaPdf.tanggalDinas);

                        utilDate = mmddyyyyformat.parse(reqRecord.get(536871015).getValue().toString());
                        tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.pulangDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                        utilDate = mmddyyyyformat.parse(reqRecord.get(536871014).getValue().toString());
                        tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.tgltujuan1 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                        //tanggal tujuan 2 kosong
                        if (reqRecord.get(536871049).getValue() != null) {
                            utilDate = mmddyyyyformat.parse(reqRecord.get(536871049).getValue().toString());
                            tanggalFormat = new Date(utilDate.getTime());
                            travelokaPdf.tgltujuan2 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        }

                        //tanggal tujuan 3 kosong
                        if (reqRecord.get(536871050).getValue() != null) {
                            utilDate = mmddyyyyformat.parse(reqRecord.get(536871050).getValue().toString());
                            tanggalFormat = new Date(utilDate.getTime());
                            travelokaPdf.tgltujuan3 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        }
                    } catch (ParseException e) {
                        logger.info("error converting date" + e.toString());
                    }
                    travelokaPdf.alokasiCostCenter = reqRecord.get(536871024).getValue().toString();

                    travelokaPdf.kotaAsal = reqRecord.get(536870970).getValue().toString();
                    travelokaPdf.kotatujuan1 = reqRecord.get(536870981).getValue().toString();

                    travelokaPdf.kotatujuan2 = (reqRecord.get(536870982).getValue() == null) ? null : reqRecord.get(536870982).getValue().toString();

                    travelokaPdf.kotatujuan3 = (reqRecord.get(536870983).getValue() == null) ? null : reqRecord.get(536870983).getValue().toString();

                    String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                    int bookingLoop = 0;
                    Double totalHargaOTA = (double) 0;
                    List<TravelHotelPrint> listHotel = new ArrayList<TravelHotelPrint>();
                    TravelHotelPrint travelHotelPrint = new TravelHotelPrint();
                    List<TravelFlightPrint> listFlight = new ArrayList<TravelFlightPrint>();
                    TravelFlightPrint travelFlightPrint = new TravelFlightPrint();

                    List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            travelokaMainForm,
                            "'Req_Id'=\"" + reqNumber + "\" AND (Status__c=\"Assigned\" OR Status__c=\"Fixed\") ");
                    for (EntryListInfo eListTraveloka : eListTravelokas) {
                        Entry bookingRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                        bookingLoop++;
                        String bookingType = bookingRecord.get(536870920).getValue().toString();
                        String bookingId = bookingRecord.get(536870915).getValue().toString();

                        bookingTraveloka.bookingId = bookingId;
                        bookingTraveloka.productType = bookingType;
                        //bookingTraveloka.bookingDetail = "";
                        bookingTraveloka.totalFare = bookingRecord.get(536870919).getValue().toString();

                        if (bookingRecord.get(536870919).getValue() != null) {
                            totalHargaOTA += Double.valueOf(bookingRecord.get(536870919).getValue().toString());
                        }

                        if (bookingRecord.get(536870919).getValue() != null) {
                            long timeLimitBooking = Long.parseLong(bookingRecord.get(536870917).getValue().toString());
                            if (approvalTimeLimit == 1) {
                                approvalTimeLimit = timeLimitBooking;
                            } else {
                                if (approvalTimeLimit > timeLimitBooking) {
                                    approvalTimeLimit = timeLimitBooking;
                                }
                            }
                        }

                        if (bookingType.equalsIgnoreCase("hotel")) {
                            String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Traveloka:hotelProductDetail";
                            List<EntryListInfo> eListHotels = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaHotelForm, "'bookingId'=\"" + bookingId + "\" ");
                            for (EntryListInfo eListHotel : eListHotels) {
                                travelHotelPrint = new TravelHotelPrint();
                                Entry hotelRecord = remedySession.getEntry(travelokaHotelForm, eListHotel.getEntryID(), null);
                                SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date utilDateHotel;
                                try {
                                    utilDateHotel = yyyymmddformat.parse(hotelRecord.get(536870919).getValue().toString());
                                    Date tanggalHotelCheck = new Date(utilDateHotel.getTime());
                                    travelHotelPrint.checkInDate = new SimpleDateFormat("dd MMM yyyy").format(tanggalHotelCheck);
                                } catch (ParseException e) {
                                    logger.info("cannot conver date = " + e.toString());
                                }

                                travelHotelPrint.guestName = hotelRecord.get(536870928).getValue().toString();
                                travelHotelPrint.hotelCity = hotelRecord.get(536870917).getValue().toString();
                                travelHotelPrint.hotelCountry = hotelRecord.get(536870918).getValue().toString();
                                travelHotelPrint.hotelName = hotelRecord.get(536870916).getValue().toString();
                                travelHotelPrint.nights = hotelRecord.get(536870920).getValue().toString();
                                travelHotelPrint.rooms = hotelRecord.get(536870921).getValue().toString();
                                travelHotelPrint.pricePerNight = hotelRecord.get(536870925).getValue().toString();
                                listHotel.add(travelHotelPrint);
                            }

                            travelokaPdf.listHotel = listHotel;

                        } else {
                            String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Traveloka:flightProductDetail";
                            List<EntryListInfo> eListPesawats = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaFlightForm, "'bookingId__c'=\"" + bookingId + "\" ");
                            SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            for (EntryListInfo eListPesawat : eListPesawats) {
                                Entry pesawatRecord = remedySession.getEntry(travelokaFlightForm, eListPesawat.getEntryID(), null);
                                java.util.Date utilDate2;
                                travelFlightPrint = new TravelFlightPrint();
                                try {
                                    utilDate2 = yyyymmddformat.parse(pesawatRecord.get(536870918).getValue().toString());
                                    logger.info("test date=" + utilDate2.toString());
                                    Date tanggalFormatFlight = new Date(utilDate2.getTime());
                                    travelFlightPrint.departureDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(tanggalFormatFlight);
                                } catch (ParseException e) {
                                    logger.info("cannot convert date for flight" + e.toString());
                                }

                                //travelFlightPrint.departureDate = pesawatRecord.get(536870918).getValue().toString();
                                travelFlightPrint.destinationAirport = pesawatRecord.get(536870917).getValue().toString();
                                // get passenger travelFlightPrint.passenger
                                String passengerForm = "PTM:SSC:HR:Travel:INT:Traveloka:Passenger";
                                String namaPassenger = "";
                                int jumlahPassenger = 0;
                                List<EntryListInfo> eListPassengers = remedyAPI.getRemedyRecordByQuery(remedySession, passengerForm, "'bookingId'=\"" + bookingId + "\" ");
                                for (EntryListInfo eListPassenger : eListPassengers) {
                                    Entry passengerRecord = remedySession.getEntry(passengerForm, eListPassenger.getEntryID(), null);
                                    if (jumlahPassenger > 0) {
                                        namaPassenger += "\n";
                                    }
                                    namaPassenger += (passengerRecord.get(536870917).getValue() == null) ? "" : " " + passengerRecord.get(536870917).getValue().toString();
                                    namaPassenger += (passengerRecord.get(536870918).getValue() == null) ? "" : " " + passengerRecord.get(536870918).getValue().toString();
                                    namaPassenger += (passengerRecord.get(536870919).getValue() == null) ? "" : "(" + passengerRecord.get(536870919).getValue().toString() + ")";
                                    jumlahPassenger++;
                                }
                                travelFlightPrint.passenger = namaPassenger;
                                travelFlightPrint.returnDate = pesawatRecord.get(536870919).getValue().toString();
                                travelFlightPrint.seatClass = pesawatRecord.get(536870921).getValue().toString();
                                travelFlightPrint.sourceAirport = pesawatRecord.get(536870916).getValue().toString();
                                travelFlightPrint.airlines = pesawatRecord.get(536870920).getValue().toString();

                                if (bookingTraveloka.bookingDetail == null) {
                                    bookingTraveloka.bookingDetail = "";
                                }
                                bookingTraveloka.bookingDetail += travelFlightPrint.airlines + "\n"
                                        + travelFlightPrint.sourceAirport + "-" + travelFlightPrint.destinationAirport + "\n"
                                        + travelFlightPrint.departureDate + "\n"
                                        + travelFlightPrint.seatClass + "\n\n";

                                logger.info("bookingdetail=" + bookingTraveloka.bookingDetail);
                                //travelFlightPrint.totalFare = ;

                                listFlight.add(travelFlightPrint);
                            }

                            travelokaPdf.listFlight = listFlight;
                        }

                    }

                    String skpdTravelForm = "PTM:SSC:HR:SKPD";
                    List<EntryListInfo> eListEcosts = remedyAPI.getRemedyRecordByQuery(remedySession, skpdTravelForm, "'SR-Request ID'=\"" + reqNumber + "\" ");
                    SKPDPrintPdf skpdPrintPdf = new SKPDPrintPdf();

                    for (EntryListInfo eListEcost : eListEcosts) {
                        Entry ecostRecord = remedySession.getEntry(skpdTravelForm, eListEcost.getEntryID(), null);
                        int totalHari = Integer.parseInt(ecostRecord.get(536870988).getValue().toString());
                        int totalHariMenginap = totalHari - 1;

                        travelokaPdf.companyCode = ecostRecord.get(536870940).getValue().toString();

                        skpdPrintPdf.pengaliKompensasi = (ecostRecord.get(536870988).getValue() == null) ? " "
                                : ecostRecord.get(536870988).getValue().toString() + " * ";
                        skpdPrintPdf.pengaliKompensasi += (ecostRecord.get(536870993).getValue() == null) ? " "
                                : ecostRecord.get(536870993).getValue().toString();

                        //skpdPrintPdf.pengaliKompensasi = ecostRecord.get(536870988).getValue().toString()+" * " + ecostRecord.get(536870993).getValue().toString();
                        skpdPrintPdf.kompensasiHarian = (ecostRecord.get(536871133).getValue() == null)
                                ? " " : ecostRecord.get(536871133).getValue().toString();
                        skpdPrintPdf.pengaliHotel = totalHariMenginap + " * " + ecostRecord.get(536871078).getValue().toString();
                        skpdPrintPdf.pengaliPesawat = "";
                        skpdPrintPdf.ecostPesawat = ecostRecord.get(536871140).getValue().toString();
                        skpdPrintPdf.ecostHotel = ecostRecord.get(536871141).getValue().toString();
                        skpdPrintPdf.totalEcost = ecostRecord.get(536871105).getValue().toString();
                        skpdPrintPdf.alasanPerjalanan = ecostRecord.get(536870922).getValue().toString();
                    }

                    if (approvalTimeLimit > 1) {
                        approvalTimeLimit -= 600000;
                        Timestamp tsLimit = new Timestamp(approvalTimeLimit);
                        Date tanggalLimit = new Date(tsLimit.getTime());
                        travelokaPdf.approvalTimeLimit = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(tanggalLimit) + " WIB";
                    }
                    travelokaPdf.totalBiayaOTA = totalHargaOTA.toString();

                    logger.info("testing pdf new");
                    travelDocument.generateApprovalNew(
                            travelokaPdf,
                            travelHotelPrint,
                            travelFlightPrint,
                            skpdPrintPdf,
                            bookingTraveloka);
                }

                //update status pdf middle form
                pdfRecord.put(7, new Value("1"));
                remedySession.setEntry("PTM:SSC:HR:Travel:Printedpdf", pdfRecord.getEntryId(), pdfRecord, null, 0);

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        //catch(NullPointerException e) {
        //	logger.info("NullPointer exception:" +e.toString());
        //}
        return "testing";
    }

    @RequestMapping(value = "/testProxy")
    private String testProxy() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        /*
		ProxySearch proxySearch = new ProxySearch();
		if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
			logger.info("linux");
			//proxySearch.addStrategy(Strategy.GNOME);
			//proxySearch.addStrategy(Strategy.KDE);
			proxySearch.addStrategy(Strategy.FIREFOX);
		} else {
			logger.info("non unix");
			proxySearch.addStrategy(Strategy.OS_DEFAULT);
		}
		//proxySearch.get
		ProxySelector myProxySelector = proxySearch.getProxySelector();
		
		ProxySelector.setDefault(myProxySelector);
		logger.info("proxy nya:"+myProxySelector.toString());
		
		htttps.proxyHost
         */
 /*
		System.setProperty("htttps.proxyUser", "tamuptm.2136");
		System.setProperty("htttps.proxyPassword", "pertamina");
		System.setProperty("htttps.proxyHost", "http://172.17.3.155");
		System.setProperty("htttps.proxyPort", "8080");
		System.setProperty("htttps.nonProxyHosts", "localhost|127.0.0.1");*/
        String pertaminaProxy = configValue.getPertaminaProxy();
        logger.info("using proxy=" + pertaminaProxy);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(pertaminaProxy, 8080));

        try {

            URL url = new URL(configValue.getTravelokaPayment());
            HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
            int status = con.getResponseCode();
            logger.info("traveloka server:" + configValue.getTravelokaPayment());
            if (status == 200) {
                logger.info("Connection is OK: ");
            } else {
                logger.info("Http responde code: " + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("error notes:" + e.toString());
        }

        //long longVar = 1541664280374;
        //Timestamp ts = new Timestamp(longVar);
        return "testing";
    }

    @RequestMapping(value = "/testNewPdf")
    private String testNewPdf() {
        //TravelDocument travelDocument = new TravelDocument();
        //travelDocument.generateApprovalNew();

        return "testing";
    }

    @RequestMapping(value = "/generateAdvance")
    private String generateAdvance() {
        RemedyController remedyController = new RemedyController();
        remedyController.generatePrintTravelAdvance();

        return "testing";
    }

    @RequestMapping(value = "/generateSettlement")
    private String generateSettlement() {
        RemedyController remedyController = new RemedyController();
        remedyController.generatePrintTravelSettlement();

        return "testing";
    }

    @RequestMapping(value = "/filenet")
    private String sendtoFilenet() {
        FilenetController filenetController = new FilenetController();
        filenetController.getRemedyTicket();

        return "testing";
    }

    @RequestMapping(value = "/printapprovalpdf")
    public String printapprovalpdf() throws BadElementException, IOException {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        long approvalTimeLimit = 1;

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:Printedpdf", "'Status__c'=\"0\" ");
        //List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Travel:Printedpdf", "'Request Number__c'=\"REQ000000005612\" ");
        SimpleDateFormat mmddyyyyformat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                TravelokaPDF travelokaPdf = new TravelokaPDF();
                TravelDocument travelDocument = new TravelDocument();
                BookingTraveloka bookingTraveloka = new BookingTraveloka();
                List<BookingTraveloka> listBookingTiket = new ArrayList<BookingTraveloka>();
                Entry pdfRecord = remedySession.getEntry("PTM:SSC:HR:Travel:Printedpdf", eListInfo.getEntryID(), null);
                String reqNumber = pdfRecord.get(536870913).getValue().toString();
                approvalTimeLimit = 1;

                //query to get req detail
                List<EntryListInfo> eListReqs = remedyAPI.getRemedyRecordByQuery(remedySession, "SRM:Request", "'Request Number'=\"" + reqNumber + "\" ");
                for (EntryListInfo eListReq : eListReqs) {
                    Entry reqRecord = remedySession.getEntry("SRM:Request", eListReq.getEntryID(), null);
                    travelokaPdf.requstNumber = reqNumber;
                    travelokaPdf.srInstanceId = reqRecord.get(179).getValue().toString();
                    if (configValue.getRemedyServer().equalsIgnoreCase("172.20.1.94")) {
                        travelokaPdf.noTrip = reqRecord.get(536870966).getValue().toString();
                    } else {
                        travelokaPdf.noTrip = reqRecord.get(536870973).getValue().toString();
                    }
                    travelokaPdf.jenisPerjalanan = reqRecord.get(536870967).getValue().toString();
                    travelokaPdf.pekerja = reqRecord.get(1000003297).getValue().toString()
                            //+ " " + reqRecord.get(300890310).getValue().toString() 
                            + " " + reqRecord.get(1000003298).getValue().toString()
                            + " (" + reqRecord.get(536871035).getValue().toString() + ")";
                    travelokaPdf.sifatPerjalanan = reqRecord.get(536870968).getValue().toString();
                    travelokaPdf.alasanPerjalanan = reqRecord.get(536870969).getValue().toString();
                    travelokaPdf.namafile = "TravelRequest_" + travelokaPdf.noTrip + ".pdf";
                    try {
                        java.util.Date utilDate = mmddyyyyformat.parse(reqRecord.get(536871014).getValue().toString());
                        Date tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.tanggalDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        logger.info("test1=" + travelokaPdf.tanggalDinas);

                        utilDate = mmddyyyyformat.parse(reqRecord.get(536871015).getValue().toString());
                        tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.pulangDinas = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                        utilDate = mmddyyyyformat.parse(reqRecord.get(536871014).getValue().toString());
                        tanggalFormat = new Date(utilDate.getTime());
                        travelokaPdf.tgltujuan1 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);

                        //tanggal tujuan 2 kosong
                        if (reqRecord.get(536871049).getValue() != null) {
                            utilDate = mmddyyyyformat.parse(reqRecord.get(536871049).getValue().toString());
                            tanggalFormat = new Date(utilDate.getTime());
                            travelokaPdf.tgltujuan2 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        }

                        //tanggal tujuan 3 kosong
                        if (reqRecord.get(536871050).getValue() != null) {
                            utilDate = mmddyyyyformat.parse(reqRecord.get(536871050).getValue().toString());
                            tanggalFormat = new Date(utilDate.getTime());
                            travelokaPdf.tgltujuan3 = new SimpleDateFormat("dd MMM yyyy").format(tanggalFormat);
                        }
                    } catch (ParseException e) {
                        logger.info("error converting date" + e.toString());
                    }
                    travelokaPdf.alokasiCostCenter = reqRecord.get(536871024).getValue().toString();

                    travelokaPdf.kotaAsal = reqRecord.get(536870970).getValue().toString();
                    travelokaPdf.kotatujuan1 = reqRecord.get(536870981).getValue().toString();

                    travelokaPdf.kotatujuan2 = (reqRecord.get(536870982).getValue() == null) ? null : reqRecord.get(536870982).getValue().toString();

                    travelokaPdf.kotatujuan3 = (reqRecord.get(536870983).getValue() == null) ? null : reqRecord.get(536870983).getValue().toString();

                    String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
                    int bookingLoop = 0;
                    Double totalHargaOTA = (double) 0;
                    List<TravelHotelPrint> listHotel = new ArrayList<TravelHotelPrint>();
                    TravelHotelPrint travelHotelPrint = new TravelHotelPrint();
                    List<TravelFlightPrint> listFlight = new ArrayList<TravelFlightPrint>();
                    TravelFlightPrint travelFlightPrint = new TravelFlightPrint();

                    List<EntryListInfo> eListTravelokas = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            travelokaMainForm,
                            "'Req_Id'=\"" + reqNumber + "\" ");
                    //"'Req_Id'=\""+reqNumber+"\" AND (Status__c=\"Assigned\" OR Status__c=\"Fixed\") ");
                    for (EntryListInfo eListTraveloka : eListTravelokas) {
                        bookingTraveloka = new BookingTraveloka();
                        Entry bookingRecord = remedySession.getEntry(travelokaMainForm, eListTraveloka.getEntryID(), null);
                        bookingLoop++;
                        String bookingType = bookingRecord.get(536870920).getValue().toString();
                        String bookingId = bookingRecord.get(536870915).getValue().toString();

                        bookingTraveloka.bookingId = bookingId;
                        bookingTraveloka.productType = bookingType;
                        //bookingTraveloka.bookingDetail = "";
                        bookingTraveloka.totalFare = bookingRecord.get(536870919).getValue().toString();

                        if (bookingRecord.get(536870919).getValue() != null) {
                            totalHargaOTA += Double.valueOf(bookingRecord.get(536870919).getValue().toString());
                        }

                        if (bookingRecord.get(536870919).getValue() != null) {
                            long timeLimitBooking = Long.parseLong(bookingRecord.get(536870917).getValue().toString());
                            if (approvalTimeLimit == 1) {
                                approvalTimeLimit = timeLimitBooking;
                            } else {
                                if (approvalTimeLimit > timeLimitBooking) {
                                    approvalTimeLimit = timeLimitBooking;
                                }
                            }
                        }

                        if (bookingType.equalsIgnoreCase("hotel")) {
                            String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Traveloka:hotelProductDetail";
                            List<EntryListInfo> eListHotels = remedyAPI.getRemedyRecordByQuery(remedySession, travelokaHotelForm, "'bookingId'=\"" + bookingId + "\" ");
                            for (EntryListInfo eListHotel : eListHotels) {
                                travelHotelPrint = new TravelHotelPrint();
                                Entry hotelRecord = remedySession.getEntry(travelokaHotelForm, eListHotel.getEntryID(), null);
                                SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date utilDateHotel;
                                try {
                                    utilDateHotel = yyyymmddformat.parse(hotelRecord.get(536870919).getValue().toString());
                                    Date tanggalHotelCheck = new Date(utilDateHotel.getTime());
                                    travelHotelPrint.checkInDate = new SimpleDateFormat("dd MMM yyyy").format(tanggalHotelCheck);
                                } catch (ParseException e) {
                                    logger.info("cannot conver date = " + e.toString());
                                }

                                travelHotelPrint.guestName = hotelRecord.get(536870928).getValue().toString();
                                travelHotelPrint.hotelCity = hotelRecord.get(536870917).getValue().toString();
                                travelHotelPrint.hotelCountry = hotelRecord.get(536870918).getValue().toString();
                                travelHotelPrint.hotelName = hotelRecord.get(536870916).getValue().toString();
                                travelHotelPrint.nights = hotelRecord.get(536870920).getValue().toString();
                                travelHotelPrint.rooms = hotelRecord.get(536870921).getValue().toString();
                                travelHotelPrint.pricePerNight = getIDRFormat(hotelRecord.get(536870925).getValue().toString().replace("IDR ", ""));
                                listHotel.add(travelHotelPrint);
                            }
                            bookingTraveloka.travelHotelPrint = listHotel;
                            travelokaPdf.listHotel = listHotel;

                        } else {
                            String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Traveloka:flightProductDetail";
                            List<EntryListInfo> eListPesawats = remedyAPI.getRemedyRecordByQuery(
                                    remedySession,
                                    travelokaFlightForm,
                                    "'bookingId__c'=\"" + bookingId + "\" ");
                            SimpleDateFormat yyyymmddformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                            for (EntryListInfo eListPesawat : eListPesawats) {
                                Entry pesawatRecord = remedySession.getEntry(travelokaFlightForm, eListPesawat.getEntryID(), null);
                                java.util.Date utilDate2;
                                travelFlightPrint = new TravelFlightPrint();
                                try {
                                    utilDate2 = yyyymmddformat.parse(pesawatRecord.get(536870918).getValue().toString());
                                    logger.info("test date=" + utilDate2.toString());
                                    Date tanggalFormatFlight = new Date(utilDate2.getTime());
                                    travelFlightPrint.departureDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa").format(tanggalFormatFlight);
                                } catch (ParseException e) {
                                    logger.info("cannot convert date for flight" + e.toString());
                                }

                                //travelFlightPrint.departureDate = pesawatRecord.get(536870918).getValue().toString();
                                travelFlightPrint.destinationAirport = pesawatRecord.get(536870917).getValue().toString();
                                // get passenger travelFlightPrint.passenger
                                String passengerForm = "PTM:SSC:HR:Travel:INT:Traveloka:Passenger";
                                String namaPassenger = "";
                                int jumlahPassenger = 0;
                                List<EntryListInfo> eListPassengers = remedyAPI.getRemedyRecordByQuery(
                                        remedySession,
                                        passengerForm,
                                        "'bookingId'=\"" + bookingId + "\" ");
                                for (EntryListInfo eListPassenger : eListPassengers) {
                                    Entry passengerRecord = remedySession.getEntry(passengerForm, eListPassenger.getEntryID(), null);
                                    if (jumlahPassenger > 0) {
                                        namaPassenger += "\n";
                                    }
                                    namaPassenger += (passengerRecord.get(536870917).getValue() == null)
                                            ? "" : " " + passengerRecord.get(536870917).getValue().toString();
                                    namaPassenger += (passengerRecord.get(536870918).getValue() == null)
                                            ? "" : " " + passengerRecord.get(536870918).getValue().toString();
                                    namaPassenger += (passengerRecord.get(536870919).getValue() == null)
                                            ? "" : "(" + passengerRecord.get(536870919).getValue().toString() + ")";
                                    jumlahPassenger++;
                                }
                                travelFlightPrint.passenger = namaPassenger;
                                travelFlightPrint.returnDate = pesawatRecord.get(536870919).getValue().toString();
                                travelFlightPrint.seatClass = pesawatRecord.get(536870921).getValue().toString();
                                travelFlightPrint.sourceAirport = pesawatRecord.get(536870916).getValue().toString();
                                travelFlightPrint.airlines = pesawatRecord.get(536870920).getValue().toString();

                                if (bookingTraveloka.bookingDetail == null) {
                                    bookingTraveloka.bookingDetail = "";
                                }
                                bookingTraveloka.bookingDetail += travelFlightPrint.airlines + "\n"
                                        + travelFlightPrint.sourceAirport + "-" + travelFlightPrint.destinationAirport + "\n"
                                        + travelFlightPrint.departureDate + "\n"
                                        + travelFlightPrint.seatClass + "\n\n";

                                logger.info("bookingdetail=" + bookingTraveloka.bookingDetail);
                                //travelFlightPrint.totalFare = ;

                                listFlight.add(travelFlightPrint);
                            }
                            bookingTraveloka.travelFlightPrint = listFlight;

                            travelokaPdf.listFlight = listFlight;
                        }
                        listBookingTiket.add(bookingTraveloka);
                    }

                    String skpdTravelForm = "PTM:SSC:HR:SKPD";
                    List<EntryListInfo> eListEcosts = remedyAPI.getRemedyRecordByQuery(remedySession, skpdTravelForm, "'SR-Request ID'=\"" + reqNumber + "\" ");
                    SKPDPrintPdf skpdPrintPdf = new SKPDPrintPdf();

                    //format uang rupiah
                    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
                    formatRp.setCurrencySymbol("IDR ");
                    formatRp.setMonetaryDecimalSeparator(',');
                    formatRp.setGroupingSeparator('.');
                    kursIndonesia.setDecimalFormatSymbols(formatRp);

                    for (EntryListInfo eListEcost : eListEcosts) {
                        Entry ecostRecord = remedySession.getEntry(skpdTravelForm, eListEcost.getEntryID(), null);
                        int totalHari = Integer.parseInt(ecostRecord.get(536870988).getValue().toString());
                        int totalHariMenginap = totalHari - 1;

                        travelokaPdf.companyCode = ecostRecord.get(536870940).getValue().toString();
                        // TODO Mengambil nomer dan atau nama pekerja travelokaPdf.pekerja += eListEcost.

                        //non res
                        skpdPrintPdf.pengaliKompensasi = (ecostRecord.get(536870988).getValue() == null) ? " "
                                : ecostRecord.get(536870988).getValue().toString() + " hari x ";
                        skpdPrintPdf.pengaliKompensasi += (ecostRecord.get(536870993).getValue() == null) ? " "
                                : kursIndonesia.format(Double.parseDouble(ecostRecord.get(536870993).getValue().toString()));

                        //res diluar kedudukan
                        // TODO total hari * uang acara, total acara * uang makan
                        //skpdPrintPdf.pengaliKompensasi = ecostRecord.get(536870988).getValue().toString()+" * " + ecostRecord.get(536870993).getValue().toString();
                        skpdPrintPdf.kompensasiHarian = (ecostRecord.get(536871133).getValue() == null)
                                ? " " : ecostRecord.get(536871133).getValue().toString();
                        skpdPrintPdf.pengaliHotel = totalHariMenginap + " * " + ecostRecord.get(536871078).getValue().toString();
                        skpdPrintPdf.pengaliPesawat = "";
                        skpdPrintPdf.ecostPesawat = ecostRecord.get(536871140).getValue().toString();
                        skpdPrintPdf.ecostHotel = ecostRecord.get(536871141).getValue().toString();
                        skpdPrintPdf.totalEcost = ecostRecord.get(536871105).getValue().toString();
                        skpdPrintPdf.alasanPerjalanan = ecostRecord.get(536870922).getValue().toString();
                    }

                    if (approvalTimeLimit > 1) {
                        approvalTimeLimit -= 600000;
                        Timestamp tsLimit = new Timestamp(approvalTimeLimit);
                        Date tanggalLimit = new Date(tsLimit.getTime());
                        travelokaPdf.approvalTimeLimit = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(tanggalLimit) + " WIB";
                    }
                    travelokaPdf.totalBiayaOTA = totalHargaOTA.toString();

                    //logger.info("testing newwwwww new");
                    travelDocument.generateApprovalTravelPdf(travelokaPdf, skpdPrintPdf, listBookingTiket);
                }

                //update status pdf middle form
                pdfRecord.put(7, new Value("1"));
                remedySession.setEntry("PTM:SSC:HR:Travel:Printedpdf", pdfRecord.getEntryId(), pdfRecord, null, 0);

            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        }

        //catch(NullPointerException e) {
        //	logger.info("NullPointer exception:" +e.toString());
        //}
        return "testing";
    }

    public String getIDRFormat(String nominalUang) {
        //format uang rupiah
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("IDR ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(Double.parseDouble(nominalUang));
    }

    public String getUSDFormat(String nominalUang) {
        //format uang rupiah
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatUsd = new DecimalFormatSymbols();
        formatUsd.setCurrencySymbol("USD ");
        formatUsd.setMonetaryDecimalSeparator('.');
        formatUsd.setGroupingSeparator(',');
        kursIndonesia.setDecimalFormatSymbols(formatUsd);

        return kursIndonesia.format(Double.parseDouble(nominalUang));
    }

    public void addWorkInfo(
            String summary,
            String notes,
            String corpRefId,
            ARServerUser remedySession) {

        RemedyAPI remedyAPI = new RemedyAPI();
        Entry entryTravelokaNotif = new Entry();
        entryTravelokaNotif.put(536870916, new Value(notes));
        entryTravelokaNotif.put(536870915, new Value(summary));
        entryTravelokaNotif.put(8, new Value(corpRefId));
        String formTravelokaNotification = "PTM:SSC:HR:Travel:SendTravelokaBookingtoWorkInfo";

        logger.info("input in bmc:" + corpRefId);

        remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, entryTravelokaNotif);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("172.17.3.155", 8080));
        requestFactory.setProxy(proxy);

        return new RestTemplate(requestFactory);
    }

    @RequestMapping(value = "pullData", method = RequestMethod.GET)
    public String pullData() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //SSC connection setting up
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        logger.info("+++++++ Pull data is starting +++++++++++++++ ");
        logger.info("jumlah pull data per menit=" + configValue.getRemedyPullData());
        int jumlahLooping = Integer.parseInt(configValue.getRemedyPullData());
        RemedyController remedyController = new RemedyController();

        //Date date = new Date();
        //String strDateFormat = "hh:mm:ss a";
        //DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        //String formattedDate= dateFormat.format(date);
        for (int looping = 0; looping < jumlahLooping; looping++) {
            if (looping == 900) {
                looping = 0;
            }
            remedyController.getPOPullData(remedyServer);
            logger.info(looping + "---------- next item ------------");
        }

        logger.info("+++++++ Pull data is done +++++++++++++++");

        //close remedy connection
        remedyServer.logout();
        return "testing";
    }

    @RequestMapping(value = "/getPJSEcor")
    public String getPJSEcor() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        try {
            MessageFactory messageFactory;
            messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            SOAPPart soapPart = soapMessage.getSOAPPart();
            String myNamespace = "tem";
            String myNamespaceURI = "http://tempuri.org/";

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement soapBodyElem = soapBody.addChildElement("SelectAdInterimAttributes", myNamespace);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            SOAPElement soapBodyElemDate = soapBodyElem.addChildElement("date", myNamespace);
            soapBodyElemDate.addTextNode(dtf.format(now));

            SOAPElement soapBodyElemPjsUser = soapBodyElem.addChildElement("SvcUserName", myNamespace);
            soapBodyElemPjsUser.addTextNode("pjs_admin");

            SOAPElement soapBodyElemPjsPassword = soapBodyElem.addChildElement("SvcPassword", myNamespace);
            soapBodyElemPjsPassword.addTextNode("pertamina@1");

            //Setting action
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", "http://tempuri.org/IAdInterimService/SelectAdInterimAttributes");
            soapMessage.saveChanges();

            //Change the SOAPMessage in order to log the real soap message in log file
            ByteArrayOutputStream outXml = new ByteArrayOutputStream();
            soapMessage.writeTo(outXml);
            logger.info("Generated PJS SOAPMessage:\n" + new String(outXml.toByteArray()));

            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory;
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapResponse = soapConnection.call(soapMessage, "http://ptmkpwebpipsdev.pertamina.com/eCorr_Service/AdInterim/AdInterimService.svc");
            readEcorResult(soapResponse, remedySession);

            outXml = new ByteArrayOutputStream();
            soapResponse.writeTo(outXml);
            //logger.info("SOAP Response: "+new String (outXml.toByteArray()));

            //Close SOAP Connection
            soapConnection.close();

        } catch (SOAPException e) {
            logger.info("SOAPException Error on createSOAPMessage: " + e.toString());
            return null;
        } catch (IOException e) {
            logger.info("IOException Error on createSOAPMessage: " + e.toString());
            return null;
        }

        //close remedy connection
        remedySession.logout();
        return "testing";
    }

    public void readEcorResult(SOAPMessage soapResponse, ARServerUser remedySession) {
        try {
            String pejabatNIK = "1", positionId = "1", startDatePJS = "1", endDatePJS = "1";
            SOAPBody soapBody = soapResponse.getSOAPBody();
            java.util.Iterator bodyIterator1 = soapBody.getChildElements();
            while (bodyIterator1.hasNext()) {
                SOAPElement soapElement1 = (SOAPElement) bodyIterator1.next();
                java.util.Iterator bodyIterator2 = soapElement1.getChildElements();
                while (bodyIterator2.hasNext()) {
                    SOAPElement soapElement2 = (SOAPElement) bodyIterator2.next();
                    java.util.Iterator bodyIterator3 = soapElement2.getChildElements();
                    while (bodyIterator3.hasNext()) {
                        SOAPElement soapElement3 = (SOAPElement) bodyIterator3.next();
                        java.util.Iterator bodyIterator4 = soapElement3.getChildElements();
                        while (bodyIterator4.hasNext()) {
                            SOAPElement soapElement4 = (SOAPElement) bodyIterator4.next();
                            String elementName = soapElement4.getLocalName();
                            String elementValue = soapElement4.getValue();
                            switch (elementName) {
                                case "PejabatNIK":
                                    pejabatNIK = elementValue;
                                    break;
                                case "EndDate":
                                    endDatePJS = elementValue;
                                    break;
                                case "PosID":
                                    positionId = elementValue;
                                    break;
                                case "StartDate":
                                    startDatePJS = elementValue;
                                    break;
                            }

                            //logger.info(elementName+":"+elementValue);
                        }
                        logger.info("++++++++++");
                        setPJS(remedySession, positionId, pejabatNIK, startDatePJS, endDatePJS);

                    }
                }
            }
        } catch (SOAPException e) {
            logger.info("error" + e.toString());
            //e.printStackTrace();
        }
    }

    public void setPJS(ARServerUser remedySession,
            String positionId,
            String pejabatNIK,
            String startDatePJS,
            String endDatePJS) {

        //insert record to 
        PJSDomain pjsDomain = new PJSDomain();
        pjsDomain.setPjsNik(pejabatNIK);
        pjsDomain.setPositionId(positionId);

        RemedyController remedyController = new RemedyController();
        remedyController.insertPJSRecord(remedySession, pjsDomain);

        //TODO cek vacant position apa tidak
        RemedyAPI remedyAPI = new RemedyAPI();
        List<EntryListInfo> listPeopleinPosition = remedyAPI.getRemedyRecordByQuery(remedySession, "CTM:People", "'Job Title ID'=\"" + positionId + "\"");
        if (listPeopleinPosition.size() > 0) {
            //jika tidak vacant maka tambahkan di table alternate approval
            logger.info(positionId + " is fulfilled=" + pejabatNIK);
        } else {
            //membaca form PTM:IntegrasiHRIS:People:FinalDataSubmit

            //jika vacant, maka ganti table approval hris
            String approvalMasterData = "PTM:SSC:HR:Travel:ApprovalMasterData";
            List<EntryListInfo> listApprovalMasterData = remedyAPI.getRemedyRecordByQuery(remedySession, approvalMasterData, "'Job Title ID'=\"" + positionId + "\"");
            /*v
			try {
				for (EntryListInfo  listApproval: listApprovalMasterData) {
					Entry approvalRecord = remedySession.getEntry(approvalMasterData,listApproval.getEntryID(), null);
					approvalRecord.get(536871101).getValue().toString();
				}
			}*/
            logger.info(positionId + " is vacant");
        }

    }

    @RequestMapping(value = "/testTime")
    public String getTestTime() {
        Long dateLong = Long.parseLong("1533631779");
        long dateLongKecil = Long.parseLong("1533631779");

        Timestamp besar = new Timestamp(dateLong);
        Timestamp kecil = new Timestamp(dateLongKecil);

        Date cekDate = new Date(dateLong * 1000L);
        String cekSubmitDate = new SimpleDateFormat("dd MMM yyyy").format(cekDate);
        System.out.println("besar=" + cekSubmitDate);

        cekDate = new Date(kecil.getTime());
        cekSubmitDate = new SimpleDateFormat("dd MMM yyyy").format(cekDate);
        System.out.println("kecil=" + cekSubmitDate);

        return "testing";
    }

    @RequestMapping(value = "/getWorkInfo")
    public String getWorkEntry() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        System.out.print("Remedy Server:" + configValue.getRemedyServer());

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        String Form = "SRM:WorkInfo";

        List<EntryListInfo> listForm = remedyAPI.getRemedyRecordByQuery(remedySession, Form, "'Request Number'=\"REQ000000000018\" "); //kalau di sql ini query nya utk kualifikasi 

        try {
            for (EntryListInfo getEntryForm : listForm) {
                //	SRMWorkInfoMetadata getEntry = new SRMWorkInfoMetadata(); //class yg deklar field2 di form SRM:WorkInfo
                Entry formRecord = remedySession.getEntry(Form, getEntryForm.getEntryID(), null); //untuk nampung fieldnya

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                //testing push to travel
                //untuk narik tanggal             
                //try {
                String tanggalTest = formRecord.get(10001962).getValue().toString();
                System.out.println("testing:" + tanggalTest);
                tanggalTest = tanggalTest.replaceAll("[^a-zA-Z0-9]", "");
                tanggalTest = tanggalTest.replaceAll("Timestamp", "");
                System.out.println("testing tanggal:" + tanggalTest);
                Long dateLong = Long.parseLong(tanggalTest);
                //dateLong -= 600000;
                Timestamp tsLimit = new Timestamp(dateLong);

                //java.util.Date utilDate = dateFormat.parse(tanggalTest);
                Date date = new Date(tsLimit.getTime());
                String cekSubmitDate = new SimpleDateFormat("dd MMM yyyy").format(date);

                //Date tsDate = (Date) formRecord.get(10001962).getValue();
                //travelokaPdf.approvalTimeLimit = tsLimit.toString() +" WIB";
                //utilDate = dateFormat.parse(formRecord.get(10001962).getValue().toString());
                //date = new Date(utilDate.getTime());
                System.out.println("ini tanggalnya " + cekSubmitDate);
                //getEntry.submitdate = new SimpleDateFormat("dd MMM yyyy").format(submitdate);

            }
        } catch (Exception e) {
            System.out.println("error karena:" + e.toString());
            logger.info("error function getWorkInfo :" + e.toString()); //buat munculin error

            String testingResult = e.toString(); //ngarahin ke testing yg testingResult
            Map<String, String> model = new HashMap<String, String>();
            model.put("testingResult", testingResult);
        }
        return "testing";
    }

    @RequestMapping(value = "/MitraTravel", method = RequestMethod.GET)
    public String MitraTravel() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        System.out.print("Remedy Server:" + configValue.getRemedyServer());

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);

        //queri
        List<MitraTravelDetails> MitraTravelDetails = new ArrayList<MitraTravelDetails>();
        RemedyAPI remedyAPI = new RemedyAPI();
        MitraTravelDetails mitratraveldetails = new MitraTravelDetails();

        //String MitraTra = "PTM:SSC:HR:Travel:INT:Traveloka:OKPayment";
        String MiddleForm = "PTM:SSC:HR:Travel:MitraBookingDetail";
        //final String uri = "https://ext-api-corporate.test.traveloka.com/paymentConfirmation";
        final String uri = "https://pertamina.opsicorp.com/api/skpd/insert";

        List<EntryListInfo> MitraBookingDetails = remedyAPI.getRemedyRecordByQuery(remedySession,
                MiddleForm, "'Status'=\"New\"");
        try {
            for (EntryListInfo MitraTravel : MitraBookingDetails) {
                //URL url = new URL("https://ext-api-corporate.test.traveloka.com/paymentConfirmation");

                Entry recordBooking = remedySession.getEntry(MiddleForm, MitraTravel.getEntryID(), null);

                mitratraveldetails = new MitraTravelDetails();

                //Request Number
                mitratraveldetails.trNumber = recordBooking.get(536870928).getValue().toString();
                String RequestNumber = mitratraveldetails.trNumber;
                if (RequestNumber == null) {
                    System.out.println("Request Number is not found");
                } else {
                    System.out.println(RequestNumber);
                }

                //No Trip
                mitratraveldetails.noTrip = recordBooking.get(536870933).getValue().toString();
                String noTrip = mitratraveldetails.noTrip;
                if (RequestNumber == null) {
                    System.out.println("Trip Number is not found");
                } else {
                    System.out.println(noTrip);
                }

                //TravelStarDate
//                mitratraveldetails.travelStartDate = recordBooking.get(536870917).getValue().toString();
                String REQ_Date = recordBooking.get(536870917).getValue().toString();;
                REQ_Date = REQ_Date.replaceAll("[^a-zA-Z0-9]", "");
                REQ_Date = REQ_Date.replaceAll("Timestamp", "");
                Long dateLong1 = Long.parseLong(REQ_Date);
                Date cekDate1 = new Date(dateLong1 * 1000L);
                String cekSubmitDate1 = new SimpleDateFormat("ddMMyyyy").format(cekDate1);
//                String cekSubmitDate3 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(cekDate1);
                mitratraveldetails.travelStartDate = cekSubmitDate1;
                System.out.println("Departure Date:" + cekSubmitDate1);

                //TravelEndDate
//                mitratraveldetails.travelEndDate = ;
                String TravelEndDate = recordBooking.get(536870918).getValue().toString();
                TravelEndDate = TravelEndDate.replaceAll("[^a-zA-Z0-9]", "");
                TravelEndDate = TravelEndDate.replaceAll("Timestamp", "");
                Long dateLongEnd = Long.parseLong(TravelEndDate);
                Date cekDateEnd = new Date(dateLongEnd * 1000L);
                String cekSubmitDateEnd = new SimpleDateFormat("ddMMyyyy").format(cekDateEnd);
//                String cekSubmitDate3 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(cekDate1);
                mitratraveldetails.travelEndDate = cekSubmitDateEnd;
                System.out.println("Departure Date:" + cekSubmitDateEnd);
                if (TravelEndDate == null) {
                    System.out.println("TravelEndDate is not found");
                } else {
//                    System.out.println(TravelEndDate);
                }

                //EmployeeID
                mitratraveldetails.employeeId = recordBooking.get(536870919).getValue().toString();
                String EmployeeID = mitratraveldetails.employeeId;
                if (TravelEndDate == null) {
                    System.out.println("EmployeeID is not found");
                } else {
                    System.out.println(EmployeeID);
                }

                mitratraveldetails.golper = recordBooking.get(536870926).getValue().toString();
                String golper = mitratraveldetails.golper;
                if (golper == null) {
                    System.out.println("Origin is not found");
                } else {
                    System.out.println(golper);
                }

                //Remark
                mitratraveldetails.remark = recordBooking.get(536870925).getValue().toString();
                String remark = mitratraveldetails.remark;
                if (golper == null) {
                    System.out.println("Origin is not found");
                } else {
                    System.out.println(remark);
                }

                //tripPurpose
                mitratraveldetails.tripPurpose = recordBooking.get(536870927).getValue().toString();
                String tripPurpose = mitratraveldetails.tripPurpose;
                if (tripPurpose == null) {
                    System.out.println("Origin is not found");
                } else {
                    System.out.println(tripPurpose);
                }

                //Origin
                mitratraveldetails.origin = recordBooking.get(536870920).getValue().toString();
                String Origin = mitratraveldetails.origin;
                if (Origin == null) {
                    System.out.println("Origin is not found");
                } else {
                    System.out.println("origin = " + Origin);
                }

                //Detination 1
                mitratraveldetails.destination1 = recordBooking.get(536870921).getValue().toString();
                String Destination1 = mitratraveldetails.destination1;
                if (Destination1 == null) {
                    System.out.println("Destination1 is not found");
                } else {
                    System.out.println("Destination 1 = " + Destination1);
                }

                //Destination 2
                if (recordBooking.get(536870922).getValue() == null) {
                    System.out.println("Destination2 is not found");
                } else {
                    mitratraveldetails.destination2 = recordBooking.get(536870922).getValue().toString();
                    String Destination2 = mitratraveldetails.destination2;
                    System.out.println(Destination2);
                }
                //Destination 3
                if (recordBooking.get(536870923).getValue() == null) {
                    System.out.println("Destination3 is not found");
                } else {
                    mitratraveldetails.destination3 = recordBooking.get(536870923).getValue().toString();
                    String Destination3 = mitratraveldetails.destination3;
                    System.out.println(Destination3);
                }

                //ApproverEmail
                mitratraveldetails.approverEmail = recordBooking.get(536870924).getValue().toString();
                String ApproverEmail = mitratraveldetails.approverEmail;
                if (ApproverEmail == null) {
                    System.out.println("ApproverEmail is not found");
                } else {
                    System.out.println(ApproverEmail);
                }

                ///Approvaltimestime
                Timestamp timestampApproval = new Timestamp(System.currentTimeMillis());
                mitratraveldetails.approvalTimestamp = timestampApproval.getTime();

                //transportationType
                mitratraveldetails.transportationType = recordBooking.get(536870932).getValue().toString();
                String transportationType = mitratraveldetails.transportationType;
                if (ApproverEmail == null) {
                    System.out.println("transportationType is not found");
                } else {
                    System.out.println(transportationType);
                }

                //Estimated Cost
                Float estimatedCost, totalFlight = null;
                Float totalHotel = Float.valueOf(recordBooking.get(536870931).getValue().toString());
                if (recordBooking.get(536870930).getValue() != null) {
                    totalFlight = Float.valueOf(recordBooking.get(536870930).getValue().toString());
                    estimatedCost = totalFlight + totalHotel;
                    mitratraveldetails.flightEstimatedCost = totalFlight;
                } else {
                    estimatedCost = 2000000 + totalHotel;
                }
                System.out.println(totalHotel + " >> " + totalFlight + " = " + estimatedCost);

                mitratraveldetails.estimatedCost = estimatedCost;
                mitratraveldetails.hotelEstimatedCost = totalHotel;
//                
                //Send to rest
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                logger.info("trNumber=" + RequestNumber);
                logger.info("noTrip=" + noTrip);
                logger.info("origin=" + Origin);
                logger.info("Destination1=" + Destination1);
                logger.info("trip purpose=" + tripPurpose);
                logger.info("estimatedCost=" + estimatedCost);
                logger.info("estimatedCostHotel=" + totalHotel);
                logger.info("estimatedCostFlight=" + totalFlight);

                System.out.println(mitratraveldetails.getClass());
                System.out.println(mitratraveldetails.hashCode());

                ObjectMapper mapper = new ObjectMapper();

                try {

                    String testtrvl = restTemplate.postForObject(uri, mitratraveldetails, String.class);

                    if (testtrvl.contains("TRN Number' already exists")) {
                        recordBooking.put(4, new Value("TRN Number' already exists"));
                        recordBooking.put(7, new Value("2"));
                    } else if (testtrvl.contains("SUCCESSFUL")) {
                        recordBooking.put(4, new Value("SUCCESSFUL"));
                        recordBooking.put(7, new Value("1"));
                    }

                    String json = mapper.writeValueAsString(mitratraveldetails);
                    logger.info(json);
                    remedySession.setEntry(MiddleForm, recordBooking.getEntryId(), recordBooking, null, 0);

//                    restTemplate.delete("https://pertamina.opsicorp.com/api/skpd/delete?trnNumber=10002131");
                } catch (RestClientResponseException e) {
                    logger.info("Couldn't stream resource " + uri + "parce que :" + e.getResponseBodyAsString());
                    logger.info("Couldn't stream resource " + uri + "parce que :" + e.getMessage());
                    //String json = mapper.writeValueAsString(mitratraveldetails);
                    //logger.info(json);
                    recordBooking.put(4, new Value(e.getLocalizedMessage().toString()));
                    recordBooking.put(7, new Value("2"));
                } catch(JsonProcessingException ej) {
                	
                }

            }
        } catch (ARException e) {
            logger.info("Error while push data to mitra : " + e);
        }

        return "testing";
    }

    @RequestMapping(value = "/getCancelledTicket")
    public String getCancelledTicket() {
        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        System.out.print("Remedy Server:" + configValue.getRemedyServer());

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);

        //query
        List<MitraTravelDetails> MitraTravelDetails = new ArrayList<MitraTravelDetails>();
        RemedyAPI remedyAPI = new RemedyAPI();
        MitraTravelDetails mitratraveldetails = new MitraTravelDetails();

        String MiddleForm = "PTM:SSC:HR:Travel:Mitra:Cancelled";
        final String uri = "https://pertamina.opsicorp.com/api/skpd/delete?trNumber=";

        List<EntryListInfo> MitraBookingDetails = remedyAPI.getRemedyRecordByQuery(remedySession, MiddleForm, "'Status'=\"New\" ");
        System.out.println("jumlah array of list: " + MitraBookingDetails.size());
        int looping = 0;
        try {
            for (EntryListInfo MitraTravel : MitraBookingDetails) {
                looping++;
                Entry recordBooking = remedySession.getEntry(MiddleForm, MitraTravel.getEntryID(), null);

                System.out.println("Looping ke-" + looping);

                //TRN Number
                String trNumber = recordBooking.get(536870914).getValue().toString();
                mitratraveldetails.trNumber = trNumber;
                String FinalUri = uri + trNumber;
                System.out.println("url final = " + FinalUri);

                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                //logger.info("Request send to traveloka");
                logger.info("trNumber=" + trNumber);

                try {
                    restTemplate.delete("https://pertamina.opsicorp.com/api/skpd/delete?trnNumber={trNumber}", trNumber);
                    recordBooking.put(4, new Value("success"));
                    recordBooking.put(7, new Value("1"));
                    remedySession.setEntry(MiddleForm, recordBooking.getEntryId(), recordBooking, null, 0);

//                    restTemplate.delete("https://pertamina.opsicorp.com/api/skpd/delete?trnNumber=10002131");
                } catch (RestClientResponseException e) {
                    logger.info("Couldn't delete stream resource " + uri + "parce que :" + e.getLocalizedMessage());
                    logger.info("Couldn't delete stream resource " + uri + "parce que :" + e.getMessage());
                    recordBooking.put(4, new Value(e.getLocalizedMessage().toString()));
                }

            }
        } catch (Exception e) {
            System.out.println("Error getCancelledTicket");
        }

        return "testing";
    }

    @RequestMapping(value = "/Approval")
    public String Approval() {

        String date = "2/7/2020 4:24:34 PM";
        String date1 = "2020-02-07 17:23:15.017";
        date = date.replaceAll("[^a-zA-Z0-9]", "");
        date = date.replaceAll("Timestamp", "");
        Long dateLong1 = Long.parseLong(date);
        Date cekDate1 = new Date(dateLong1 * 1000L);
        String cekSubmitDate1 = new SimpleDateFormat("yyyy/MM/dd H:mm:ss.SSS").format(cekDate1);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd H:mm:ss.SSS");
//        System.out.println("time : " + time);
        System.out.println("waktu : " + cekSubmitDate1);
        return "testing";
    }

    //untuk AO
    class Nama{
    public String nama1, nama2, nama3, nama4, nama5, nama6, nama7, nama8, nama9, nama10;

        Nama(String nama1){
            this.nama1 = nama1;
        }
    }
    
    class ADType{
        public String adType;
        
        ADType(String adType){
            this.adType = adType;
        }
    }
}

