package eu.gloria.rts2.rtd.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.rtd.DomeRTD;

public class DomeRTDTester {

	DomeRTD dome = null;
	
	public DomeRTDTester (){
		
		dome = new DomeRTD ();
		dome.setDeviceId("CUP");
	}
	
	@Test
	public void testDomOpen() throws RTException {
		
		if (((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityStateOpening()== ActivityStateDomeOpening.CLOSE){
			try{
				dome.domOpen(1);
				assertEquals("OPENING",((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityStateOpening().value());
			}catch (UnsupportedOpException e){
				fail("This method has to be supported");
			}
		}else{
			fail("Try again. This method could not be tested.");
		}
		
	}
	
	@Test
	public void testDomCanSetAltitude() throws RTException {

		try{
			if (!dome.domCanSetAltitude()){
				try{
					dome.domMoveAltitude(10);
					fail("Inconsistency: domCanSetAltitude() vs domMoveAltitude()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
				try{
					dome.domGetAltitude();
					fail("Inconsistency: domCanSetAltitude() vs domGetAltitude()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					dome.domMoveAltitude(10);					
				}catch (UnsupportedOpException e){
					fail("Inconsistency: domCanSetAltitude() vs domMoveAltitude()");
				}
				try{
					dome.domGetAltitude();					
				}catch (UnsupportedOpException e){
					fail("Inconsistency: domCanSetAltitude() vs domGetAltitude()");
				}				
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testDomCanSetAzimuth() throws RTException {
		try{
			if (!dome.domCanSetAzimuth()){
				try{
					dome.domMoveAzimuth(10);
					fail("Inconsistency: domCanSetAzimuth() vs domMoveAzimuth()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
				try{
					dome.domGetAzimuth();
					fail("Inconsistency: domCanSetAzimuth() vs domGetAzimuth()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					dome.domMoveAzimuth(10);					
				}catch (UnsupportedOpException e){
					fail("Inconsistency: domCanSetAzimuth() vs domMoveAzimuth()");
				}
				try{
					dome.domGetAzimuth();					
				}catch (UnsupportedOpException e){
					fail("Inconsistency: domCanSetAzimuth() vs domGetAzimuth()");
				}catch (RTException ex){
					
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testDomCanSetPark() throws RTException {
		try{
			if (!dome.domCanSetPark()){
				try{
					dome.domSetPark(80, 70);
					fail("Inconsistency: domCanSetPark() vs domSetPark()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}				
			}else{
				try{
					dome.domSetPark(80,70);					
				}catch (UnsupportedOpException e){
					fail("Inconsistency: domCanSetPark() vs domSetPark()");
				}			
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testDomMoveAzimuth() throws RTException {

		try{
			dome.domMoveAzimuth(80);
			assertEquals("MOVING",((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testDomMoveAltitude() throws RTException {
		try{
			dome.domMoveAltitude(80);
			assertEquals("MOVING",((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}
	
	@Test
	public void testDomClose() throws RTException {
		
		if (((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityStateOpening()== ActivityStateDomeOpening.OPEN){
			try{
				dome.domClose(1);
				assertEquals("CLOSING",((DeviceDome) DeviceDiscoverer.devGetDevice(dome.getDeviceId(), false)).getActivityStateOpening().value());
			}catch (UnsupportedOpException e){
				fail("This method has to be supported");
			}
		}else{
			fail("Try again. This method could not be tested.");
		}
	}

}
