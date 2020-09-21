package com.mii.sscintegration.scheduler;


import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mii.sscintegration.controller.FilenetController;
import com.mii.sscintegration.controller.RemedyController;
import com.mii.sscintegration.controller.SAPController;
import com.mii.sscintegration.controller.SSCController;



@Configuration
@EnableScheduling
public class SSCScheduler {
	protected static Logger logger = Logger.getLogger("SSCScheduler: ");
/*
	@Scheduled(fixedRate = 300000)
	public void sendTripToSAP() {
		logger.info("++++++++++++++++++++++ Sending trip to SAP ++++++++++++++++++++++++");
		SAPController sapController = new SAPController();
		sapController.sendToSAP();
	}
	
	@Scheduled(fixedRate = 120000)
	public void testingSch2() {
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!! PDF Printing !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		SSCController sscController = new SSCController();
		sscController.testpdf();
		//SAPController sapController = new SAPController();
		//sapController.sendToSAP();
	}
	
	@Scheduled(fixedRate = 360000)
	public void testingPaymentTraveloka() {
		logger.info("------------------------- OK Payment Traveloka ------------------------------");
		
		SSCController sscController = new SSCController();
		sscController.testOkPayment();
		//SAPController sapController = new SAPController();
		//sapController.sendToSAP();
	}
	
	/*
	@Scheduled(fixedDelay = 60000)
	public void sentTriptoSAP() {
		logger.info("++++++++++++++++++++++ Sending tiket to SAP ++++++++++++++++++++++++");
		SAPController sapController = new SAPController();
		sapController.sendToSAP();
	}
	
	/*
	@Scheduled(fixedDelay = 5000)
	public void printTravelAdvance() {
		logger.info("++++++++++++++++++++++ Print Travel Advance ++++++++++++++++++++++++");
		RemedyController remedyController = new RemedyController();
		remedyController.generatePrintTravelAdvance();
	}
	
	/*
	@Scheduled(fixedDelay = 5000)
	public void printTravelSettlement() {
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!! Print Travel Settlement !!!!!!!!!!!!!!!!!!!!!!!!!!!");
		RemedyController remedyController = new RemedyController();
		remedyController.generatePrintTravelSettlement();
	}
	
	
	@Scheduled(fixedDelay = 5000)
	public void sendFilenet() {
		logger.info("============================== Sending attachment to Filenet ==============================");
		FilenetController filenetController = new FilenetController();
		filenetController.getRemedyTicket();
	}*/
	
	
}
