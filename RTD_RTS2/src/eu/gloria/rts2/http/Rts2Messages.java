package eu.gloria.rts2.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.error.Rts2ErrorHandler;

/**
 * Recovers the RTS2 messages produced by a specific device in a time interval
 * 
 * @author mclopez
 * 
 */
public class Rts2Messages {

	/**
	 * Interval initial time
	 */
	private long iniTimeStap;

	/**
	 * Interval final time
	 */
	private long endTimeStap;

	/**
	 * Message type to be recovered
	 */
	private Rts2MessageType type;
	
	/**
	 * Message List
	 */
	private ArrayList<JsonMessage> messages;

	/**
	 * Device identifier
	 */
	private String deviceId = null;

	public Rts2Messages() {
		iniTimeStap = -1;
		endTimeStap = -1;
		type = null;

	}

	/**
	 * Access method.
	 * @return
	 */
	public long getIniTimeStap() {
		return iniTimeStap;
	}

	/**
	 * Access method.
	 * @param iniTimeStap
	 */
	public void setIniTimeStap(long iniTimeStap) {
		this.iniTimeStap = iniTimeStap;
	}

	/**
	 * Access method.
	 * @return
	 */
	public long getEndTimeStap() {
		return endTimeStap;
	}

	/**
	 * Access method.
	 * @param endTimeStap
	 */
	public void setEndTimeStap(long endTimeStap) {
		this.endTimeStap = endTimeStap;
	}

	/**
	 * Access method.
	 * @return
	 */
	public Rts2MessageType getType() {
		return type;
	}

	/**
	 * Access method.
	 * @param type
	 */
	public void setType(Rts2MessageType type) {
		this.type = type;		
	}

	/**
	 * Access method.
	 * @return
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Access method.
	 * @param deviceId
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	/**
	 * Long to Rts2MessageType conversion
	 * 
	 * @param valor long
	 * @return Rts2MessageType
	 */
	private Rts2MessageType longToType (long valor){
		
		if (valor == Rts2Constants.RTS2_MESSAGE_ALL)
			return Rts2MessageType.all;
		if (valor == Rts2Constants.RTS2_MESSAGE_DEBUG)
			return Rts2MessageType.debug;
		if (valor == Rts2Constants.RTS2_MESSAGE_ERROR)
			return Rts2MessageType.error;
		if (valor == Rts2Constants.RTS2_MESSAGE_INFO)
			return Rts2MessageType.info;
		if (valor == Rts2Constants.RTS2_MESSAGE_WARNING)
			return Rts2MessageType.warning;
		
		return null;
		
	}
	
	/**
	 * Rts2MessageType to long conversion
	 * 
	 * @param valor Rts2MessageType
	 * @return long
	 */
	private long typeToLong (Rts2MessageType valor){
		
		if (valor.compareTo(Rts2MessageType.all) == 0)
			return Rts2Constants.RTS2_MESSAGE_ALL;
		if (valor.compareTo(Rts2MessageType.debug) == 0)
			return Rts2Constants.RTS2_MESSAGE_DEBUG;
		if (valor.compareTo(Rts2MessageType.error) == 0)
			return Rts2Constants.RTS2_MESSAGE_ERROR;
		if (valor.compareTo(Rts2MessageType.info) == 0)
			return Rts2Constants.RTS2_MESSAGE_INFO;
		if (valor.compareTo(Rts2MessageType.warning) == 0)
			return Rts2Constants.RTS2_MESSAGE_WARNING;
		
		return -1;
	}
	
	
	/**
	 * Retrieve the RTS2 messages from a specific date (iniTimeStap) and for a specific type (type)
	 * 
	 * @return ArrayList<JsonMessage>
	 * @throws Rts2CommunicationException
	 * @throws Rts2Exception
	 */
	public ArrayList<JsonMessage> getMessages() throws Rts2CommunicationException, Rts2Exception {

		DecimalFormat numFormat = new DecimalFormat("##.###");
		
		
		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.messages);
		cmd.getParameters().put("type", String.valueOf(typeToLong(type)));
		cmd.getParameters().put("from", numFormat.format(iniTimeStap));
		//cmd.getParameters().put("to", numFormat.format(endTimeStap));

		String jsonContent;
		
		jsonContent = cmd.execute();
		getResponse(jsonContent);
				
		
		return this.messages;

	}
	
	/**
	 * Given a type, a device identifier and a time stap; recovers all the messages that satisfies those restrinctions.
	 * 
	 * @param type {@link Rts2MessageType}
	 * @param deviceId Device Identifier.
	 * @param date long
	 * @return String with all messages.
	 * @throws RTException 
	 */
	public static String getMessageText (Rts2MessageType type, String deviceId, long date) throws RTException{
		
		ArrayList<JsonMessage> jsMessages = new ArrayList<JsonMessage>();
		Rts2Messages messages = new Rts2Messages();
		String message = null;
		
		//Initialization
		messages.setType(type);
		messages.setDeviceId(deviceId);
		messages.setIniTimeStap(date);
		
		//Message recovering
		try {
			jsMessages = messages.getMessages();
		} catch (Rts2CommunicationException e) {
			throw new RTException("Error recovering decive messages. " + e.getMessage());
		} catch (Rts2Exception e) {
			throw new RTException("Error recovering decive messages. " + e.getMessage());
		}

		if (jsMessages != null){			
			for (JsonMessage jsMessage: jsMessages){
				 message += jsMessage.getDevice() +": "+ jsMessage.getMessage() +'\n';
			}
		}		
		
		return message;
	}
	
	/**
	 * Given a type, a device identifier and a time stap; recovers all the messages that satisfies those restrinctions.
	 * @param type {@link Rts2MessageType}
	 * @param deviceId Device Identifier.
	 * @param date long
	 * @return JsonMessage list
	 * @throws RTException In error case
	 */
	public static List<JsonMessage> getMessageTexts (Rts2MessageType type, String deviceId, long date) throws RTException{
		
		ArrayList<JsonMessage> jsMessages = new ArrayList<JsonMessage>();
		Rts2Messages messages = new Rts2Messages();
		
		//Initialization
		messages.setType(type);
		messages.setDeviceId(deviceId);
		messages.setIniTimeStap(date);
		
		//Message recovering
		try {
			jsMessages = messages.getMessages();
		} catch (Rts2CommunicationException e) {
			throw new RTException("Error recovering decive messages. " + e.getMessage());
		} catch (Rts2Exception e) {
			throw new RTException("Error recovering decive messages. " + e.getMessage());
		}

		return jsMessages;
	}
	
	/**
	 * This method handler error messages, taking into account the rts2 error context.
	 * @param contextName Context.
	 * @param type {@link Rts2MessageType}
	 * @param deviceId  Device Identifier.
	 * @param date long
	 * @throws RTException In error case.
	 */
	public static void handleErrorMessages(String contextName, Rts2MessageType type, String deviceId, long date) throws RTException{
		
		List<JsonMessage> msgList =  getMessageTexts (type, deviceId, date);
		
		Rts2ErrorHandler errorHandler = new Rts2ErrorHandler();
		
		if (msgList != null){
			for (JsonMessage jsonMessage : msgList) {
				errorHandler.handle(contextName, jsonMessage.getMessage());
			}
		}
	}
	
	/**
	 * Private method for filling this object using the json content information
	 * 
	 * @param jsonContent String 
	 * @throws Rts2Exception
	 */
	/*private void getResponse (String jsonContent) throws Rts2Exception{
		
		try{
			DecimalFormat df = new DecimalFormat("##");			

			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);

			Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD MESSAGES]. JSON content converted to Objects.");

			if (info != null) {

				if ((info instanceof Map<?, ?>)) { 

					Map<String, Object> map = (Map<String, Object>) info;
					ArrayList<Object> messList =(ArrayList<Object>) map.get("d");
					
					if (messList != null){
						messages = new  ArrayList<JsonMessage>();
						
						for (Object mess : messList) {							
							
							
							JsonMessage jsMessage = new JsonMessage();							
							jsMessage.setPath(JsonMessage.PATH_SEPARATOR + "d");							
							jsMessage.setDate(Long.valueOf(df.format(((ArrayList<Object>) mess).get(0))));
							
							if (this.getDeviceId()!= null){
								if (this.getDeviceId().equals(((ArrayList<Object>) mess).get(1)))
									jsMessage.setDevice((String) ((ArrayList<Object>) mess).get(1));
							}else{
								jsMessage.setDevice((String) ((ArrayList<Object>) mess).get(1));
							}
							
							
							jsMessage.setType(longToType((Integer) ((ArrayList<Object>) mess).get(2)));
							jsMessage.setMessage((String) ((ArrayList<Object>) mess).get(3));
							
							Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD MESSAGES]. MESSAGE=>" + jsMessage.toString());
							
							this.messages.add(jsMessage);
						}
						
					}

				}
			}

		}catch(Exception ex){
			throw new Rts2Exception("[JSON RESP::CMD MESSAGES]. Error analyzing the jsonContent. " + ex.getMessage());
		}
	}*/
	
	/**
	 * Private method for filling this object using the json content information
	 * 
	 * @param jsonContent String 
	 * @throws Rts2Exception
	 */
	public void getResponse (String jsonContent) throws Rts2Exception{
		
		try{
			DecimalFormat df = new DecimalFormat("##");			

			ObjectMapper mapper = new ObjectMapper();
//			HashMap<String, Object> info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
			
			/***** Patch*****/
			HashMap<String, Object> info = null;
			
			int count = 1;
			while (count < 20){
				
				try {
					info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);	
					break;
				} catch (JsonParseException e) {

					Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD MESSAGES]. Error in JSON format, trying to recover..." );
					
//					e.printStackTrace();
					
					BufferedReader reader = new BufferedReader(new StringReader(jsonContent));

					String line = null;
					String oldLine = null;
					for (int i=1;i<=e.getLocation().getLineNr();i++){

						line = reader.readLine();

					}

					oldLine = line.toString();
//					System.out.print(line+"\n");
//					System.out.print(line.substring(e.getLocation().getColumnNr()-4, e.getLocation().getColumnNr()-2).equals("\"") +"\n");
//					System.out.print(line.substring(e.getLocation().getColumnNr()-4, e.getLocation().getColumnNr()+4).contains("\"") +"\n");
					if (!line.substring(e.getLocation().getColumnNr()-6, e.getLocation().getColumnNr()+6).contains("\\\"")){
						if (line.substring(e.getLocation().getColumnNr()-6, e.getLocation().getColumnNr()+6).contains("\"")){
							int index = line.substring(e.getLocation().getColumnNr()-6, e.getLocation().getColumnNr()+6).indexOf("\"");
//							System.out.print(index+"\n");
							char[] newLine = line.concat(" ").toCharArray();

							int j;
							for (j=newLine.length;j>e.getLocation().getColumnNr()-6+index+1;j--)
								newLine[j-1]=newLine[j-2];						

//							System.out.print(String.valueOf(newLine)+"\n");
//							System.out.print(j+"\n");
//							newLine[e.getLocation().getColumnNr()-3]='\\';
							newLine[j-1]='\\';

							line = String.valueOf(newLine);
						}

					}

//					System.out.print(line+"\n");
//					System.out.print(oldLine+"\n");	
					jsonContent = jsonContent.replace(oldLine, line);	
//					System.out.print(jsonContent+"\n");
					
					count++;
					
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			/***** Patch*****/

			Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD MESSAGES]. JSON content converted to Objects. DeviceId=" + this.getDeviceId() );

			if (info != null) {

				if ((info instanceof Map<?, ?>)) { 

					Map<String, Object> map = (Map<String, Object>) info;
					ArrayList<Object> messList =(ArrayList<Object>) map.get("d");
					
					if (messList != null){
						messages = new  ArrayList<JsonMessage>();
						
						for (Object mess : messList) {		
							
							JsonMessage jsMessage = null;
							
							if (this.getDeviceId()!= null){// we are looking for a deviceId
								if (this.getDeviceId().equals(((ArrayList<Object>) mess).get(1))){
									jsMessage = new JsonMessage();
								}
							}else{ //we are looking for all devices
								jsMessage = new JsonMessage();
							}
							
							if (jsMessage != null){
								jsMessage.setDevice((String) ((ArrayList<Object>) mess).get(1));
								
								jsMessage.setPath(JsonMessage.PATH_SEPARATOR + "d");							
								jsMessage.setDate(Long.valueOf(df.format(((ArrayList<Object>) mess).get(0))));
								
								jsMessage.setType(longToType((Integer) ((ArrayList<Object>) mess).get(2)));
								jsMessage.setMessage((String) ((ArrayList<Object>) mess).get(3));
								
								Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD MESSAGES]. MESSAGE=>" + jsMessage.toString());
								
								this.messages.add(jsMessage);
							}
							
							
							
						}
						
					}

				}
			}

		}catch(Exception ex){
			throw new Rts2Exception("[JSON RESP::CMD MESSAGES]. Error analyzing the jsonContent. " + ex.getMessage());
		}
	}
	
	/******************************************************************************************************************************************/
	
	public static void main(String[] args) {
		
		File file = new File("C:\\repositorio\\messages.txt");
		byte[] buffer = new byte[(int) file.length()];
		DataInputStream in;
		try {
			in = new DataInputStream(new FileInputStream(file));
			
			in.readFully(buffer);
			
			Rts2Messages message = new Rts2Messages();
			message.getResponse(new String (buffer));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Rts2Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	



}
