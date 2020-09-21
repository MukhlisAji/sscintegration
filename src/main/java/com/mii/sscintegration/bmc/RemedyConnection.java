package com.mii.sscintegration.bmc;

import org.apache.log4j.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.mii.sscintegration.domain.ConfigurationValue;



public class RemedyConnection {
	protected static Logger logger = Logger.getLogger("RemedyConnection: ");
	
	public ARServerUser connectToRemedy(ConfigurationValue configValue) {
		ARServerUser remedyConnection = new ARServerUser();
		
		//Setting remedy connection properties
		remedyConnection.setServer(configValue.getRemedyServer());
		remedyConnection.setUser(configValue.getRemedyUsername());
		remedyConnection.setPassword(configValue.getRemedyPassword());
		remedyConnection.setPort(Integer.parseInt(configValue.getRemedyPort()));
		
		try {
			remedyConnection.verifyUser();
			logger.info("************Connected to BMC Remedy successfully, server address:"+remedyConnection.getServer()+"****************");
		}catch (ARException e) {
			logger.info("!!!!!!!!!!!!!!!!!!!!! Error on connection to Remedy: "+e.toString()+"!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		return remedyConnection;
	}
}
