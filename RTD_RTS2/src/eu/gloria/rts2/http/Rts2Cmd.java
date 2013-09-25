package eu.gloria.rts2.http;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.log.LogUtil;


/**
 * This class allows to access to RTS2 system using HTTP protocol.
 * 
 * @author jcabello
 *
 */

public class Rts2Cmd {
	

	
	/**
	 * Static constructor. Initializes:
	 * - https cacert location
	 * - http basic security
	 */
	static{ 
		
		//Cacerts for https connections
		try{
			
			String cacertLocation = Config.getProperty("rtd_rts2", "rts2.https.cacerts.file");
			String cacertpw = Config.getProperty("rtd_rts2", "rts2.https.cacerts.pw");
			if (cacertLocation != null && cacertLocation.trim().length() > 0 ){
				System.setProperty("javax.net.ssl.trustStore", cacertLocation);
				System.setProperty("javax.net.ssl.trustStorePassword", cacertpw);
				LogUtil.info(null, "Using a cacerts for HTTPS connections: " + cacertLocation);
			}
			
		}catch(Exception ex){
			LogUtil.severe(null, "Impossible to set the HTTPS Configuration. Exception:"  + ex.getMessage());
		}
		
		//TODO
		 Authenticator.setDefault(
        		new Authenticator() {
        			protected PasswordAuthentication getPasswordAuthentication() {
        				String user = Config.getProperty("rtd_rts2", "rts2.http.user");
        				String pw = Config.getProperty("rtd_rts2", "rts2.http.pw");
        				return new PasswordAuthentication(user, pw.toCharArray());
        				//return new PasswordAuthentication("rts2test", "1234".toCharArray());
        			}});
	}
	
	/**
	 * IP or host name
	 */
	private String host;
	
	/**
	 * Port number
	 */
	private String port;
	
	/**
	 * Web application name. Web path to the resource.
	 * 
	 */
	private String appName;
	
	/**
	 * RTS2 command.
	 */
	private Rts2CmdType cmd = null;
	
	/**
	 * RTS2 Image access path
	 */
	private String imagPath = null;	
	

	/**
	 * Parameters to send.
	 */
	private Hashtable<String, String> parameters;
	
	/**
	 * Escape parameters
	 */
	private boolean escape;
	
		
	


	/**
	 * Builds a new Rts2Cmd
	 * @param cmdType Cmd type
	 * @return the new instance.
	 */
	public static Rts2Cmd getNewCmd(Rts2CmdType cmdType){
		
		Rts2Cmd result = new Rts2Cmd();
		result.setCmd(cmdType);
		result.setPort(Config.getProperty("rtd_rts2", "rts2.http.port"));
		result.setHost(Config.getProperty("rtd_rts2", "rts2.http.host"));
		//result.setHost("150.214.56.219");
		result.setAppName(Config.getProperty("rtd_rts2", "rts2.http.appname"));
		
		return result;
	}
	
	public static Rts2Cmd getNewImageCmd (Rts2ImageCmdType cmdType){
	
		Rts2Cmd result = new Rts2Cmd();
		result.setPort(Config.getProperty("rtd_rts2", "rts2.http.port"));
		result.setHost(Config.getProperty("rtd_rts2", "rts2.http.host"));
		//jcabello: BOOTES1 does not support application
		//result.setAppName(cmdType.toString());
		
		return result;
	}
	
	/**
	 * Builds a new Rts2Cmd to access to files
	 * @param appName Application web name.
	 * @param imageName Full path to image
	 * @return the new instance.
	 */
//	public static Rts2Cmd getNewImageAccess (String appName,String imageName){
//		
//		Rts2Cmd result = new Rts2Cmd();
//		result.setImagPath(imageName);
//		result.setPort("8889"); //TODO
//		//result.setHost("localhost"); //TODO
//		result.setHost("150.214.56.219");
//		result.setAppName(appName);
//		
//		return result;
//	}
	
	
	/**
	 * Constructor.
	 */
	private Rts2Cmd(){
		parameters = new Hashtable<String, String>();	
		escape = true;
	}
	
	/**
	 * Constructor.
	 */
	private Rts2Cmd(boolean esc){
		parameters = new Hashtable<String, String>();	
		escape = esc;
	}
	
	
	/**
	 * Builds the GET url in order to access to RTS2 system
	 * @return String url.
	 * @throws UnsupportedEncodingException In error case.
	 */
	protected String  getUrl () throws UnsupportedEncodingException{
		
		StringBuilder urlBuffer= new StringBuilder();
		
		boolean isSecure = false;
		try{
			isSecure = Boolean.parseBoolean(Config.getProperty("rtd_rts2", "rts2.http.secure"));
		}catch(Exception ex){
			
		}
		
		
		//Protocol
		if (isSecure){
			urlBuffer.append("https://");
		}else{
			urlBuffer.append("http://");
		}
		
		
		//Host & Port
		if (port == null || port.trim().isEmpty()){
			urlBuffer.append(getHost()).append("/");
		}else{
			urlBuffer.append(getHost()).append(":").append(getPort()).append("/");
		}
		
		
		//AppName
		
		if (getAppName() != null && !getAppName().trim().isEmpty()){
			urlBuffer.append(getAppName()).append("/");
		}
		
		//CMD
		if (getCmd() != null)
			urlBuffer.append(getCmd().toString());
		else{
			//urlBuffer.append(getImagPath().toString());
			
//jcabello			
			File file = new File(getImagPath().toString());
			String name = file.getName();
			urlBuffer.append(getImagPath().toString());
			
		}
		
		//GET Parameters
		Hashtable<String, String> params = getParameters();
		if (params.size() > 0){ //adds the parameters
			urlBuffer.append("?");
			
			Enumeration<String> keys = params.keys();
			String key;
			String value;
			while (keys.hasMoreElements()){
				key = keys.nextElement();
				value = params.get(key);
				if (escape)
					urlBuffer.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
				else
					urlBuffer.append(key).append("=").append(value);
				if (keys.hasMoreElements()){
					urlBuffer.append("&");
				}
			}			
		}
		
		return urlBuffer.toString();
	}
	
	
	/**
	 * Executes the cmd.
	 * @return The json response.
	 * @throws Rts2CommunicationException In error case
	 */
	public String execute() throws Rts2CommunicationException{
		
		try{
			
			String urlString = getUrl();
			
			Logger.getLogger(this.getClass().getName()).info("RTS2 URL=" + urlString);
			
			URL url = new URL(urlString);
		
			URLConnection urlConnection = url.openConnection();
			
			HttpURLConnection httpUrlConnection = (HttpURLConnection)urlConnection;
			InputStream error;
			
			if (httpUrlConnection.getResponseCode() == 400) {
				error = httpUrlConnection.getErrorStream();
				
				DataInputStream errorStream = new DataInputStream(error);
				String inputLine;	
				StringBuffer errorString = new StringBuffer();

				while ((inputLine = errorStream.readLine()) != null) {
					//System.out.println(inputLine);
					errorString.append(inputLine);
				}

				errorStream.close();
			    
			    if (errorString.toString().contains("not authorized to write to the device"))			    
			    	throw new Rts2CommunicationException(errorString.toString());
			    else
			    	throw new Rts2CommunicationException("Server returned HTTP response code: 400");
			}else{			
				StringBuffer sb = new StringBuffer();

				DataInputStream dis = new DataInputStream(urlConnection.getInputStream());
				String inputLine;			


				while ((inputLine = dis.readLine()) != null) {
					//System.out.println(inputLine);
					sb.append(inputLine);
				}

				dis.close();

				//Logger.getLogger(this.getClass().getName()).info("RTS2 JSON RESPONSE=" + sb.toString());

				return sb.toString();
			}
			
		} catch (MalformedURLException me) {
			
			Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON MalformedURLException: " + me);
            Rts2CommunicationException newEx = new Rts2CommunicationException("MalformedURLException. " + me.getMessage());
            throw newEx;
            
        } catch (IOException ioe) {
        	
            Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON IOException: " + ioe);
            Rts2CommunicationException newEx = new Rts2CommunicationException("IOException. " + ioe.getMessage());
            throw newEx;
            
        }
		
	}
	
	/**
	 * Executes the cmd.
	 * @return The json response.
	 * @throws Rts2CommunicationException In error case
	 */
	//public List<Byte> executeBinary () throws Rts2CommunicationException{
	public byte[] executeBinary () throws Rts2CommunicationException{
		
		try{
			
			String urlString = getUrl();
			
			Logger.getLogger(this.getClass().getName()).info("RTS2 URL=" + urlString);
			
			URL url = new URL(urlString);
		
			URLConnection urlConnection = url.openConnection();    
			
			return IOUtils.toByteArray(urlConnection.getInputStream());
			
		} catch (MalformedURLException me) {
			
			Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON MalformedURLException: " + me);
            Rts2CommunicationException newEx = new Rts2CommunicationException("MalformedURLException. " + me.getMessage());
            throw newEx;
            
        } catch (IOException ioe) {
        	
            Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON IOException: " + ioe);
            Rts2CommunicationException newEx = new Rts2CommunicationException("IOException. " + ioe.getMessage());
            throw newEx;
            
        }
		
	}
	
	/**
	 * Executes the cmd.
	 * @return The json response.
	 * @throws Rts2CommunicationException In error case
	 */
	public URL executeBinaryURL () throws Rts2CommunicationException{
		
		try{
			
			String urlString = getUrl();
			
			Logger.getLogger(this.getClass().getName()).info("RTS2 URL=" + urlString);
			
			URL url = new URL(urlString);
			
			return url;
			
		} catch (MalformedURLException me) {
			
			Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON MalformedURLException: " + me);
            Rts2CommunicationException newEx = new Rts2CommunicationException("MalformedURLException. " + me.getMessage());
            throw newEx;
            
        } catch (IOException ioe) {
        	
            Logger.getLogger(this.getClass().getName()).severe("RTS2 JSON IOException: " + ioe);
            Rts2CommunicationException newEx = new Rts2CommunicationException("IOException. " + ioe.getMessage());
            throw newEx;
            
        }
		
	}


	/**
	 * Returns the IP or host name.
	 * @return String
	 */
	public String getHost() {
		return host;
	}


	/**
	 * Sets the IP or HOST name.
	 * @param host New value.
	 */
	public void setHost(String host) {
		this.host = host;
	}


	/**
	 * Returns the Port
	 * @return String.
	 */
	public String getPort() {
		return port;
	}


	/**
	 * Sets the Port
	 * @param port New value.
	 */
	public void setPort(String port) {
		this.port = port;
	}


	/**
	 * Returns the Application web name.
	 * @return String.
	 */
	public String getAppName() {
		return appName;
	}


	/**
	 * Sets the application web name.
	 * @param appName New value.
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}


	/**
	 * Returns the RTS2 command.
	 * @return String.
	 */
	public Rts2CmdType getCmd() {
		return cmd;
	}


	/**
	 * Sets the RTS2 command.
	 * @param cmd New command.
	 */
	public void setCmd(Rts2CmdType cmd) {
		this.cmd = cmd;
	}


	/**
	 * Returns the parameters to send.
	 * @return Parameters list.
	 */
	public Hashtable<String, String> getParameters() {
		return parameters;
	}


	/**
	 * Sets the parameters to send.
	 * @param parameters New parameters list.
	 */
	public void setParameters(Hashtable<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public boolean isEscape() {
		return escape;
	}


	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	/**
	 * Returns the image path
	 * @return Image command
	 */
	public String getImagPath() {
		return imagPath;
	}

	/**
	 * Sets the RTS2 image path
	 * @param imagCmd
	 */
	public void setImagPath(String imagPath) {
		this.imagPath = imagPath;
	}
}
