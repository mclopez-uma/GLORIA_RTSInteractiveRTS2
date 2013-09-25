package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

public class BartFilterWheelRTD extends FilterWheelRTD {

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Sets "filter" from RTS2 filterd.
	 */
	@Override
	public void fwSelectFilterKind(String kind) throws RTException {
		
		int index = this.filtersGlobalName.indexOf(kind);
		if (index == -1){
			throw new RTException("Unknown filter value for mapping. value=" + kind);
		}
		
		String localName = this.filtersLocalName.get(index);
		

		List<String> valueProp = new ArrayList<String>();
		valueProp.add(localName);
		
//		String camera = fwGetCamera();
//
//		DeviceProperty prop;
//		String propPre = "wheel";
//		String propName = "wheelA";
//		char propSuf = 'A';
//		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
//		while (!prop.getValue().get(0).equals(this.getDeviceId())){
//			propName = propPre + propSuf;
//			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
//			propSuf++;
//		}
		
		String camera = fwGetCamera();

		DeviceProperty prop;
		String propPre = "wheel";
		String propName = "wheelA";
		char propSuf = 'A';
		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
		while (!prop.getValue().get(0).equals(this.getDeviceId())){
			propSuf++;
			propName = propPre + propSuf;
			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);			
		}
		
		long time = Rts2Date.now();
		
		((CameraRTD) DeviceDiscoverer.getRTD(camera)).devUpdateDeviceProperty("filter", valueProp);
		
//		if (!this.devUpdateDeviceProperty("filter", valueProp))
//			throw new RTException ("Cannot change filter");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Gets and sets "filter" from RTS2 filterd.
	 */
	@Override
	public void fwSelectFilterPosition(int position) throws RTException {
		
		if (this.filtersGlobalName.size() == 0){
			throw new RTException ("No filter available.");
		}
		
		if (position >= this.filtersGlobalName.size()){
			throw new RTException ("Invalid position.");
		}

		/*List<String> valueProp = new ArrayList<String>();
		valueProp.add(this.filtersLocalName.get(position));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("filter", valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
			*/
		
		String camera = fwGetCamera();

		DeviceProperty prop;
		String propPre = "wheel";
		String propName = "wheelA";
		char propSuf = 'A';
		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
		while (!prop.getValue().get(0).equals(this.getDeviceId())){
			propSuf++;
			propName = propPre + propSuf;
			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);			
		}
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(this.filtersLocalName.get(position));
		
		long time = Rts2Date.now();
		
		((CameraRTD) DeviceDiscoverer.getRTD(camera)).devUpdateDeviceProperty("filter", valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
		
	}
	
}
