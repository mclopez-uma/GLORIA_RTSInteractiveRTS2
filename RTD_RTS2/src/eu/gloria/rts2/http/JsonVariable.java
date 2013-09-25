package eu.gloria.rts2.http;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;



/**
 * Represents a Generic Rts2 variable.
 * 
 * @author jcabello
 *
 */
public class JsonVariable {
	
	public static String PATH_SEPARATOR = "/";

	private String path;
	private String name;
	private List<String> value;
	private String desc;
	private Long flags;
	
	public JsonVariable(){
		path = "";
		name = "";
		value = new ArrayList();
		desc = "";
		flags = null;
	}
	
	public String getId(){
		return path + PATH_SEPARATOR + name;
	}
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getValue() {
		return value;
	}
	public void setValue(List<String> value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Long getFlags() {
		return flags;
	}

	public void setFlags(Long flags) {
		this.flags = flags;
	}
	
}
