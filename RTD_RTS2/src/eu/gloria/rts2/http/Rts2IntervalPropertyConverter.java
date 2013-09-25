package eu.gloria.rts2.http;

import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;


/**
 * Converter class to transform an Interval property from enumerate to ordinal and vice versa.
 * @author jcabello
 *
 */
public class Rts2IntervalPropertyConverter {
	
	/**
	 * Current property value.
	 */
	private List<String> values;
	
	/**
	 * List of possible values.
	 */
	private List<String> possibleValues;
	
	/**
	 * Flag indicating if the value is an ordinal index.
	 */
	private boolean ordinalValues;
	
	
	/**
	 * Constructor.
	 * @param values current property value.
	 * @param possibleValues Enumeration.
	 * @param ordinalValues true-> the value is an ordinal number.
	 */
	public Rts2IntervalPropertyConverter(List<String> values, List<String> possibleValues, boolean ordinalValues){
		this.values = values;
		this.possibleValues = possibleValues;
		this.ordinalValues = ordinalValues;
	}
	
	/**
	 * Access method to ordinalValues property
	 * @return boolean
	 */
	public boolean containsOrdinalValues(){
		return ordinalValues;
	}
	
	/**
	 * Transformation: Enum value -> Ordinal value
	 */
	public void transformToOrdinalValues(){
		if (!ordinalValues){
			if (values != null){
				for (int x = 0; x < values.size(); x++){
					values.set(x, getPossibleValueIndex(values.get(x)));
				}
			}
			ordinalValues = true;
		}
	}
	
	/**
	 * Transformation: Ordinal value -> Enum value
	 */
	public void transformToEnumValues(){
		if (ordinalValues){
			if (values != null){
				for (int x = 0; x < values.size(); x++){
					values.set(x, getPossibleValue(Integer.parseInt(values.get(x))));
				}
			}
			ordinalValues = false;
		}
	}
	
	/**
	 * Resolves the ordinal of an enum value.
	 * @param value Enum value.
	 * @return Ordinal in the enum list.
	 */
	private String getPossibleValueIndex(String value){
		
		int result = -1;
		if (possibleValues != null){
			for (int x = 0; x < possibleValues.size(); x++ ){
				if (possibleValues.get(x).equals(value)){
					result = x;
					break;
				}
			}
		}
		
		if (result == -1){
			throw new RuntimeException("Impossible to resolve the value=" + value);
		}
		return String.valueOf(result);
	}
	
	/**
	 * Resolves the enum value of a ordinal position.
	 * @param index Ordinal value.
	 * @return Enum value.
	 */
	private String getPossibleValue(int index){
		
		String result = null;
		if (possibleValues != null && possibleValues.size() > index){
			result = possibleValues.get(index);
		}
		
		if (result == null){
			throw new RuntimeException("Impossible to resolve the index=" + index);
		}
		return result;
	}
	
	/**
	 * Returns the index of the value into the enum list.
	 * @param value enum value
	 * @return index or -1 if it's impossible to resolve the index.
	 */
	public int getIndex(String value){
	
		int result = -1;
		if (!this.ordinalValues){
			try{
				result = Integer.parseInt(this.getPossibleValueIndex(value));
			}catch(Exception ex){
				result = -1;
			}
		}
		return result;
	} 


	/**
	 * Access method.
	 * @return value.
	 */
	public List<String> getValues() {
		return values;
	}


	/**
	 * Access method.
	 * @param values List of values.
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}


	/**
	 * Access method.
	 * @return List of possible values.
	 */
	public List<String> getPossibleValues() {
		return possibleValues;
	}


	/**
	 * Acccess method
	 * @param possibleValues Possible values list.
	 */
	public void setPossibleValues(List<String> possibleValues) {
		this.possibleValues = possibleValues;
	}


		

}
