package eu.gloria.rts2.http;

import java.util.Date;

import eu.gloria.tools.log.LogUtil;

/**
 * Represents a Json message
 * 
 * @author mclopez
 *
 */
public class JsonMessage {
	
	public static String PATH_SEPARATOR = "/";

	private String path;
	private String device;
	private Rts2MessageType type;
	private String message;
	private Long date;
	
	/**
	 * Constructor
	 */
	public JsonMessage (){
		path = "";
		device = "";
		type = null;
		message = "";
		date = null;		
	}
	
	public String toString(){
		String[] names = {
			"path",
			"device",
			"Rts2MessageType",
			"date",
			"message"
		};
		
		String[] values = {
			path,
			device,
			type.toString(),
			date.toString(),
			message
		};
		
		return "JsonMessage::" + LogUtil.getLog(names, values);
	}

	/**
	 * Access method.
	 * @return value
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Access method.
	 * @param path value
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Access method.
	 * @return value
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * Access method.
	 * @param device
	 */
	public void setDevice(String device) {
		this.device = device;
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
	public String getMessage() {
		return message;
	}

	/**
	 * Access method.
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Access method.
	 * @return
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * Access method.
	 * @param date
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	
	

}
