package eu.gloria.rts2.rtd;

/**
 * List of association type supported among devices.
 * 
 *  Composition: The new devices replace the parent one. The parent has to be remove from the device list.
 *  Independent: The new devices don't replace the parent, they have to be added to the device list.
 * 
 * @author mclopez
 *
 */
public enum DeviceAssociationType {
	
	Composition,
	Independent;

}
