package eu.gloria.rts2.rtd;

import java.util.List;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;

/**
 * This interface defines the methods that inform about the associated devices of an
 * specific device.
 * 
 * @author mclopez
 *
 */
public interface RTDAssociatedDevInterface {
	
	/**
	 * Returns the device identifier list of all the associated devices.
	 * 
	 * @return Device identifier list.
	 */
	List<String> getAssociatedDevices () throws RTException;
	
	/**
	 * Return the relation between the parent device and its associated devices.
	 * 
	 * @return {@link DeviceAssociationType}
	 */
	DeviceAssociationType getRelation ();
	
	/**
	 * Return the device type of the specified device identifier
	 * 
	 * @param deviceId Device identifier 
	 * @return {@link DeviceType}
	 */
	DeviceType getDeviceType (String deviceId);
	
	/**
	 * Return the device subtype of the specified device identifier
	 * @param deviceId Device identifier 
	 * @return Device subtype
	 */
	String getDeviceSubType (String deviceId);
	

}
