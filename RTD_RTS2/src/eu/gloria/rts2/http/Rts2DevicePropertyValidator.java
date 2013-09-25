package eu.gloria.rts2.http;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DevicePropertyBasicType;
import eu.gloria.rt.entity.device.DevicePropertyComplexType;


/**
 * Class to validate a device property value.
 * 
 * @author jcabello
 *
 */
public class Rts2DevicePropertyValidator {
	
	private String name;
	private DevicePropertyBasicType basicType;
	private DevicePropertyComplexType complexType;
	private List<String> values;
	private List<String> possibleValues;
	private boolean nullable;
	private List<String> minmax;
	
	public Rts2DevicePropertyValidator(String name, DevicePropertyBasicType basicType, DevicePropertyComplexType complexType, List<String> possibleValues, List<String> minmax, List<String> values){
		this.name = name;
		this.basicType = basicType;
		this.complexType = complexType;
		this.nullable = false;
		this.values = values;
		this.possibleValues = possibleValues;
		this.minmax = minmax;
		
	}
	
	public Rts2DevicePropertyValidator(String name, DevicePropertyBasicType basicType, DevicePropertyComplexType complexType, List<String> possibleValues, List<String> minmax, String value){
		this.name = name;
		this.basicType = basicType;
		this.complexType = complexType;
		this.nullable = false;
		String[] tmp = new String[1];
		tmp[0] = value;
		this.values = new ArrayList<String>();
		this.possibleValues = possibleValues;
		this.minmax = minmax;
		
	} 
	
	public boolean isValid() throws Rts2Exception{
		
		boolean result = false;
		
		if (complexType == DevicePropertyComplexType.NONE){ //is a basic type
			
			if (values == null ||values.size() != 1) {
				
				result = false;
				
			}else{
				
				result = isValidBasicType(values.get(0));
				
			}
			
		}else{ //Complex type
			
			if (complexType == DevicePropertyComplexType.RADEC){
				result = isValidRadec();
			} else if (complexType == DevicePropertyComplexType.ALTAZ){
				result = isValidAltaz();
			} else if (complexType == DevicePropertyComplexType.STAT){
				result = isValidStat();
			} else if (complexType == DevicePropertyComplexType.RECTANGLE){
				result = isValidRectangle();
			} else if (complexType == DevicePropertyComplexType.ARRAY){
				result = isValidArray();
			} else if (complexType == DevicePropertyComplexType.MMAX){
				result = isValidMmax();
			}
		}
		
		return result;
		
	}
	
	public void validate() throws Rts2Exception{
		
		boolean result = false;
		
		if (complexType == DevicePropertyComplexType.NONE){ //is a basic type
			
			if (values == null ||values.size() != 1) {
				
				throw new Rts2Exception("A value is mandatory.");
				
			}else{
				
				validateBasicType(values.get(0));
				
			}
			
		}else{ //Complex type
			
			if (complexType == DevicePropertyComplexType.RADEC){
				validateRadec();
			} else if (complexType == DevicePropertyComplexType.ALTAZ){
				validateAltaz();
			} else if (complexType == DevicePropertyComplexType.STAT){
				//validateState();
			} else if (complexType == DevicePropertyComplexType.RECTANGLE){
				validateRectangle();
			} else if (complexType == DevicePropertyComplexType.ARRAY){
				validateArray();
			} else if (complexType == DevicePropertyComplexType.MMAX){
				validateMmax();
			}
		}
		
		
	}
	
	
	
	/**
	 * Validates the basic Type.
	 * @param value Value.
	 * @throws Rts2Exception In error case.
	 */
	private void validateBasicType(String value)  throws Rts2Exception{
		
		boolean result = false;
		String basicTypeString = "";
		String pattern = "";
		
		if (basicType == DevicePropertyBasicType.STRING){
			result = isValidString(value);
			basicTypeString = "[type=STRING]";
		} else if (basicType == DevicePropertyBasicType.INTEGER){
			result = isValidInteger(value);
			basicTypeString = "[type=INTEGER]";
		} else if (basicType == DevicePropertyBasicType.TIME){
			result = isValidTime(value);
			basicTypeString = "[type=TIME]";
		} else if (basicType == DevicePropertyBasicType.DOUBLE){
			result = isValidDouble(value);
			basicTypeString = "[type=DOUBLE]";
		} else if (basicType == DevicePropertyBasicType.FLOAT){
			result = isValidFloat(value);
			basicTypeString = "[type=FLOAT]";
		} else if (basicType == DevicePropertyBasicType.BOOL){
			result = isValidBoolean(value);
			basicTypeString = "[type=BOOL]";
		} else if (basicType == DevicePropertyBasicType.SELECTION){
			result = isValidSelection(value);
			basicTypeString = "[type=SELECTION]";
			pattern = getSelectionValuesInString();
		} else if (basicType == DevicePropertyBasicType.LONGINT){
			result = isValidLong(value);
			basicTypeString = "[type=LONGINT]";
		}
		
		if (!result){
			String valueString = "[value=" + value + "]";
			throw new Rts2Exception(basicTypeString + valueString + pattern + " Invalid basic value.");
		}
	
	}
	
	/**
	 * Checks the basic type of a value.
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidBasicType(String value) throws Rts2Exception{
		
		boolean result = false;
		
		if (basicType == DevicePropertyBasicType.STRING){
			result = isValidString(value);
		} else if (basicType == DevicePropertyBasicType.INTEGER){
			result = isValidInteger(value);
		} else if (basicType == DevicePropertyBasicType.TIME){
			result = isValidTime(value);
		} else if (basicType == DevicePropertyBasicType.DOUBLE){
			result = isValidDouble(value);
		} else if (basicType == DevicePropertyBasicType.FLOAT){
			result = isValidFloat(value);
		} else if (basicType == DevicePropertyBasicType.BOOL){
			result = isValidBoolean(value);
		} else if (basicType == DevicePropertyBasicType.SELECTION){
			result = isValidSelection(value);
		} else if (basicType == DevicePropertyBasicType.LONGINT){
			result = isValidLong(value);
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid String
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidString(String value){
		boolean result = true;
		if (!nullable && value == null) result = false;
		return result;
	}
	
	/**
	 * Checks if the value is a valid Integer
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidInteger(String value){
		
		boolean result = true;
		
		try{
			
			if (!nullable && value == null) {
				result = false;
			} else if (value != null){
				Integer.parseInt(value);
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid Double
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidDouble(String value){
		
		boolean result = true;
		
		try{
			
			if (!nullable && value == null) {
				result = false;
			} else if (value != null){
				Double.parseDouble(value);
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid Time (double precision floating point, seconds from 1.1.1970)
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidTime(String value){
		
		return isValidDouble(value);
	}
	
	/**
	 * Checks if the value is a valid Float
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidFloat(String value){
		
		boolean result = true;
		
		try{
			
			if (!nullable && value == null) {
				result = false;
			} else if (value != null){
				Float.parseFloat(value);
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid Boolean
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidBoolean(String value){
		
		boolean result = true;
		
		try{
			
			if (!nullable && value == null) {
				result = false;
			} else if (value != null){
				if (!"true".equals(value) && !"false".equals(value)){
				 return false;
				}
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid LongInt
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidLong(String value){
		
		boolean result = true;
		
		try{
			
			if (!nullable && value == null) {
				result = false;
			} else if (value != null){
				Long.parseLong(value);
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the value is a valid LongInt
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidSelection(String value){
		
		boolean result = false;
		
		try{
			
			if (value == null || possibleValues == null || possibleValues.size() == 0) {
				
				result = false;
				
			} else {
				
				for (int x = 0; x < possibleValues.size(); x++){
					if (value.equals(possibleValues.get(x))) {
						result = true;
						break;
					}
				}
				
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Returns the possible values in a string
	 * @return String [value1,value2, ..., valueN]
	 */
	private String getSelectionValuesInString(){
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("[Possible values={");
		if (possibleValues == null || possibleValues.size() == 0) {
				
			sb.append("[Possible Values = No possible values available]");
				
		} else {
				
			for (int x = 0; x < possibleValues.size(); x++){
				if (x>0){
					sb.append(", ");
				}
				sb.append(possibleValues.get(x));
			}
				
		}
		sb.append("}]");
			

		return sb.toString();
	}
	
	/**
	 * Checks if the value is a valid RADEC
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidRadec(){
		
		boolean result = true;
		
		try{
			
			if (values == null ||values.size() != 2) {
				
				result = false;
				
			} else {
				
				for (int x = 0; x < values.size(); x++){
					if (!isValidBasicType(values.get(x))) {
						result = false;
						break;
					}
				}
				
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Validates a RADEC value.
	 * @throws Rts2Exception In error case
	 */
	private void validateRadec() throws Rts2Exception{
	
			if (values == null ||values.size() != 2) {
				
				throw new Rts2Exception("RADEC value must be composed by two values.");
				
			} else {
				
				for (int x = 0; x < values.size(); x++){
					if (!isValidBasicType(values.get(x))) {
						throw new Rts2Exception("At least one value is not a right basic type.");
					}
				}
				
			}
			

	}
	
	/**
	 * Checks if the value is a valid ALTAZ
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidAltaz(){
		
		boolean result = true;
		
		try{
			
			if (values == null ||values.size() != 2) {
				
				result = false;
				
			} else {
				
				for (int x = 0; x < values.size(); x++){
					if (!isValidBasicType(values.get(x))) {
						result = false;
						break;
					}
				}
				
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Validates an ALTAZ value.
	 * @throws Rts2Exception In error case
	 */
	private void validateAltaz() throws Rts2Exception{
	
		if (values == null ||values.size() != 2) {
			
			throw new Rts2Exception("ALTAZ value must be composed by two values.");
			
		} else {
			
			for (int x = 0; x < values.size(); x++){
				if (!isValidBasicType(values.get(x))) {
					throw new Rts2Exception("At least one value is not a right basic type.");
				}
			}
			
		}
		
	}
	
	/**
	 * Checks if the value is a valid STAT
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidStat(){
		
		return false;
	}
	
	/**
	 * Checks if the value is a valid RECTANGLE
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidRectangle(){
		
		boolean result = true;
		
		try{
			
			if (values == null ||values.size() != 4) {
				
				result = false;
				
			} else {
				
				for (int x = 0; x < values.size(); x++){
					if (!isValidBasicType(values.get(x))) {
						result = false;
						break;
					}
				}
				
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Validates an RECTANGLE value.
	 * @throws Rts2Exception In error case
	 */
	private void validateRectangle() throws Rts2Exception{
		
		if (values == null ||values.size() != 4) {
			
			throw new Rts2Exception("RECTANGLE value must be composed by four values.");
			
		} else {
			
			for (int x = 0; x < values.size(); x++){
				if (!isValidBasicType(values.get(x))) {
					throw new Rts2Exception("At least one value is not a right basic type.");
				}
			}
			
		}
	
	}
	
	/**
	 * Checks if the value is a valid ARRAY
	 * @param value Value
	 * @return true|false
	 */
	private boolean isValidArray(){
		
		boolean result = true;
		
		try{
			
			if (values == null ||values.size() != 1) {
				
				result = false;
				
			} else {
				
				if (!isValidBasicType(values.get(0))) {
						result = false;
				}
				
			}
			
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Validates an ARRAY value.
	 * @throws Rts2Exception In error case
	 */
	private void validateArray() throws Rts2Exception{
		
		if (values == null ||values.size() <1 ) {
			
			throw new Rts2Exception("ARRAY value must be composed at least for one value.");
			
		} else {
			
			if (!isValidBasicType(values.get(0))) {
				throw new Rts2Exception("At least one value is not a right basic type.");
			}
			
		}
	
	}
	
	/**
	 * Checks if the value is a valid MMAX
	 * @param value Value
	 * @return true|false
	 * @throws Rts2Exception 
	 */
	private boolean isValidMmax() throws Rts2Exception{
		
		boolean result = true;
		
		try{
			
			if (values == null ||values.size() != 1) {
				
				result = false;
				
			}else{

				if (basicType == DevicePropertyBasicType.INTEGER){
					
					int value = Integer.parseInt(values.get(0));
					int intervalPos1 = minmax.get(0)== null ? value : Integer.parseInt(minmax.get(0));
					int intervalPos2 = minmax.get(1)== null ? value : Integer.parseInt(minmax.get(1));
					result = intervalPos1 <= value && value<= intervalPos2;
					
				} else if (basicType == DevicePropertyBasicType.TIME){
					
					double value = Double.parseDouble(values.get(0));
					double intervalPos1 = minmax.get(0)== null ? value : Double.parseDouble(minmax.get(0));
					double intervalPos2 = minmax.get(1)== null ? value : Double.parseDouble(minmax.get(1));
					result = intervalPos1 <= value && value<= intervalPos2;
					
				} else if (basicType == DevicePropertyBasicType.DOUBLE){
					
					double value = Double.parseDouble(values.get(0));
					double intervalPos1 = minmax.get(0)== null ? value : Double.parseDouble(minmax.get(0));
					double intervalPos2 = minmax.get(1)== null ? value : Double.parseDouble(minmax.get(1));
					result = intervalPos1 <= value && value<= intervalPos2;
					
				} else if (basicType == DevicePropertyBasicType.FLOAT){
					
					float value = Float.parseFloat(values.get(0));
					float intervalPos1 = minmax.get(0)== null ? value : Float.parseFloat(minmax.get(0));
					float intervalPos2 = minmax.get(1)== null ? value : Float.parseFloat(minmax.get(1));
					result = intervalPos1 <= value && value<= intervalPos2;
					
				} else if (basicType == DevicePropertyBasicType.LONGINT){
					
					long value = Long.parseLong(values.get(0));
					long intervalPos1 = minmax.get(0)== null ? value :  Long.parseLong(minmax.get(0));
					long intervalPos2 = minmax.get(1)== null ? value : Long.parseLong(minmax.get(1));
					result = intervalPos1 <= value && value<= intervalPos2;
					
				}
				
			}
							
		}catch(Exception ex){
			
			throw new Rts2Exception(ex);
		}
		
		return result;
	}
	
	/**
	 * Validates an Mmax value.
	 * @throws Rts2Exception In error case
	 */
	private void validateMmax() throws Rts2Exception{
		
		try{
			if (!isValidMmax()){
				throw new Exception ("Invalid.");
			}
		}catch(Exception ex){
			String intervalString = "[possible interval={" + minmax.get(0) + ", " + minmax.get(1) + "}]";
			throw new Rts2Exception(intervalString + " Wrong interval value.");
		}
	
	}

}
