package eu.gloria.rts2.error;

import java.io.File;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.JsonMessage;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.log.LogUtil;

public class Rts2ErrorHandler {
	
	static{
		
		try{
			
			String xsdFile = Config.getProperty("rtd_rts2", "rts2.error.contexts.xsd");
			String xmlFile = Config.getProperty("rtd_rts2", "rts2.error.contexts.xml");
		
			LogUtil.info(null, "Rts2ErrorHandler::rts2.error.management.xsd=" + xsdFile);
			LogUtil.info(null, "Rts2ErrorHandler::rts2.error.management.xml=" + xmlFile);
		
			File schemaFile = new File(xsdFile);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);

			JAXBContext context = JAXBContext.newInstance(Rts2ErrorManagement.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			File file = new File(xmlFile);
			
			rts2ErrorManagement = (Rts2ErrorManagement) unmarshaller.unmarshal(file);
			
		}catch(Exception ex){
			LogUtil.severe(null, "Rts2ErrorHandler: Error static constructor:" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	private static Rts2ErrorManagement rts2ErrorManagement;
	
	
	
	public void handle(String contextName, String message) throws RTException{
		
		if (rts2ErrorManagement == null){
			throw new RTException(message);
		}
		
		Context context = findContext(contextName);
		
		if (context != null){
			
			LogUtil.info(this, "[MatchedError->ContextFound]::" + contextName);
			
			Rts2Message msg = findMatchedMessage(context, message);
			if (msg != null){
				
				LogUtil.info(this, "[MatchedError->PatterFound]::" + msg.getPattern());
				
				if (msg.getAction() == ActionType.EXCEPTION){
					
					String errorMsg = message;
					if (msg.getErrorMsg() != null){
						errorMsg = msg.getErrorMsg();
					}
					
					LogUtil.severe(this, "[MatchedError->RAISE]::RTS2Error: " + msg.getErrorCod() + ", " + message);
					
					throw new RTException(errorMsg, msg.getErrorCod());
					
				} else {
					//IGNORE.
					LogUtil.info(this, "[MatchedError->IGNORE]::Ignoring RTS2Error: " + message);
				}
			}
			
		}else{
			
			if (rts2ErrorManagement.getDefaultBehaviour() == Behaviour.IGNORE_ERRORS){
				
				//Nothing
				LogUtil.info(this, "[Behaviour->IGNORE_ERRORS]::Ignoring RTS2Error: " + message);
				
			}else{
				
				throw new RTException(message);
				
			}
			
		}
		
		
	}
	
	private Rts2Message findMatchedMessage(Context context, String message) throws RTException{
		
		Rts2Message result = null;
		
		if (context.getMessage() != null){
			
			for (Rts2Message msg : context.getMessage()) {
				if (matches(msg, message)){
					result = msg;
					break;
				}
			}
		}
		
		return result;
		
	}
	
	
	private boolean matches (Rts2Message msg, String message) throws RTException{
		
		if (msg.getMatchType() == MatchType.SUBSTRING){
			
			int index = message.indexOf(msg.getPattern());
			if (index > -1){ //exists
				return true;
			}
			
		} else {
			
			throw new RTException ("Rts2ErrorHander. Unsupported REGEX Message.");
		}
		
		return false;
	}
	
	private Context findContext(String name){
		
		Context result = null;
		if (rts2ErrorManagement != null && rts2ErrorManagement.getContext() != null){
			for (Context context : rts2ErrorManagement.getContext()) {
				if (context.getName().equals(name)){
					result = context;
					break;
				}
			}
		}
		
		return result;
	}

}
