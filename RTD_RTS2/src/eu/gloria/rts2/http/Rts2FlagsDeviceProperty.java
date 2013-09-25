package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.DevicePropertyBasicType;
import eu.gloria.rt.entity.device.DevicePropertyComplexType;

/**
 * Wraps all business logic related to a Property device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceProperty {
	
	private Long flags;
	
	public Rts2FlagsDeviceProperty(Long flags){
		this.flags = flags;
	}
	
	public boolean isValueWritable() throws Rts2Exception{
		
		long valueToCompare = (flags & Rts2Constants.RTS2_VALUE_WRITABLE_MASK);
		
		return valueToCompare > 0;
	}
	
	public DevicePropertyBasicType getBasicType() throws Rts2Exception{
		
		
		long valueToCompare = (flags & Rts2Constants.RTS2_BASE_TYPE_MASK);
		
		if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_STRING){
			return DevicePropertyBasicType.STRING;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_INTEGER){
			return DevicePropertyBasicType.INTEGER;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_TIME){
			return DevicePropertyBasicType.TIME;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_DOUBLE){
			return DevicePropertyBasicType.DOUBLE;
		}else if(valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_FLOAT){
			return DevicePropertyBasicType.FLOAT;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_BOOL){
			return DevicePropertyBasicType.BOOL;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_SELECTION){
			return DevicePropertyBasicType.SELECTION;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_LONGINT){
			return DevicePropertyBasicType.LONGINT;
		}
		
		//RADEC
		
		if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_RADEC){
			return DevicePropertyBasicType.DOUBLE;
		} else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_ALTAZ){
			return DevicePropertyBasicType.DOUBLE;
		}
		
		
		
		throw new Rts2Exception("Immpossible to resolve a basic type. Flags:" + flags);
			
	
	}
	
	public DevicePropertyComplexType getComplexType() throws Rts2Exception{
		
		long valueToCompare = (flags & Rts2Constants.RTS2_EXT_TYPE_MAX);
		
		if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_STAT){
			return DevicePropertyComplexType.STAT;		
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_RECTANGLE){
			return DevicePropertyComplexType.RECTANGLE;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_ARRAY){
			return DevicePropertyComplexType.ARRAY;
		}else if (valueToCompare == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_MMAX){
			return DevicePropertyComplexType.MMAX;
		}else if ((flags & Rts2Constants.RTS2_BASE_TYPE_MASK) == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_RADEC){
			return DevicePropertyComplexType.RADEC;
		}else if ((flags & Rts2Constants.RTS2_BASE_TYPE_MASK) == Rts2Constants.RTS2_FLAG_MASK_COMP_TYPE_ALTAZ){
			return DevicePropertyComplexType.ALTAZ;
		}else{
			return DevicePropertyComplexType.NONE;
		}
				
	}

}
