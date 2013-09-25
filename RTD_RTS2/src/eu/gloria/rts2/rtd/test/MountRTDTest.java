package eu.gloria.rts2.rtd.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.gloria.rt.entity.device.ActivityStateMount;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.TrackingRateType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.rtd.MountRTD;

public class MountRTDTest {
	
	MountRTD mount = null;
	
	public MountRTDTest (){		
		mount = new MountRTD();
		mount.setDeviceId("T0");		
	}
	

	@Test
	public void testMntSetTrackingRate() throws RTException {
		try{
			mount.mntSetTrackingRate(TrackingRateType.DRIVE_SIDEREAL);
			assertEquals("Failed to assert ",TrackingRateType.DRIVE_SIDEREAL, mount.mntGetTrackingRate());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntGetTracking() throws RTException {
		try{
			mount.mntSetTracking(false);
			assertEquals("Failed to assert ",false, mount.mntGetTracking());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}	

	@Test
	public void testMntCanSetTracking() throws RTException {
		try{
			if (!mount.mntCanSetTracking()){
				try{
					mount.mntSetTracking(false);
					fail("Inconsistency: mntCanSetTracking() vs mntSetTracking()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSetTracking(false);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSetTracking() vs mntSetTracking()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntCanSetTrackingRate() throws RTException {
		try{
			if (!mount.mntCanSetTrackingRate()){
				try{
					mount.mntSetTrackingRate(TrackingRateType.DRIVE_SIDEREAL);
					fail("Inconsistency: mntCanSetTracking() vs mntSetTracking()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSetTrackingRate(TrackingRateType.DRIVE_SIDEREAL);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSetTracking() vs mntSetTracking()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}
	

	@Test
	public void testMntCanSlewCoordinates() throws RTException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  &&  (state != ActivityStateMount.PARKED));
		
		try{
			if (!mount.mntCanSlewCoordinates()){
				try{
					mount.mntSlewToCoordinates(80,70);
					fail("Inconsistency: mntCanSlewCoordinates() vs mntSlewToCoordinates()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSlewToCoordinates(80,70);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSlewCoordinates() vs mntSlewToCoordinates()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntCanSlewCoordinatesAsync() throws RTException {
		
ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED));
		
		try{
			if (!mount.mntCanSlewCoordinatesAsync()){
				try{
					mount.mntSlewToCoordinatesAsync(80,70);
					fail("Inconsistency: mntCanSlewCoordinatesAsync() vs mntSlewToCoordinatesAsync()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSlewToCoordinatesAsync(80,70);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSlewCoordinatesAsync() vs mntSlewToCoordinatesAsync()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntCanSlewAltAz() throws RTException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED));
		
		try{
			if (!mount.mntCanSlewAltAz()){
				try{
					mount.mntSlewToAltAz(80,70);
					fail("Inconsistency: mntCanSlewAltAz() vs mntSlewToAltAz()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSlewToAltAz(80,70);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSlewAltAz() vs mntSlewToAltAz()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntCanSlewAzAsync() throws RTException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			if (!mount.mntCanSlewAzAsync()){
				try{
					mount.mntSlewToAltAzAsync(80,70);
					fail("Inconsistency: mntCanSlewAzAsync() vs mntSlewToAltAzAsync()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntSlewToAltAzAsync(80,70);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanSlewAzAsync() vs mntSlewToAltAzAsync()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntCanMoveAzis() throws RTException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			if (!mount.mntCanMoveAzis()){
				try{
					mount.mntMoveAxis(0, 0);
					fail("Inconsistency: mntCanMoveAzis() vs mntMoveAxis()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					mount.mntMoveAxis(0,0);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: mntCanMoveAzis() vs mntMoveAxis()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testMntSlewToAltAz() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntSlewToAltAz(80,70);
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntSlewToAltAzAsync() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntSlewToAltAzAsync(80,70);
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntSlewToCoordinates() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntSlewToCoordinates(80,70);
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntSlewToCoordinatesAsync() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntSlewToCoordinatesAsync(80,70);
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntMoveAxis() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntMoveAxis(0,70);
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntStopSlew() throws RTException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntSlewToCoordinates(85,70);
			mount.mntStopSlew();
			assertEquals("STOP",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntMoveNorth() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntMoveNorth();
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntMoveEast() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntMoveEast();
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntMoveSouth() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntMoveSouth();
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testMntMoveWest() throws RTException, InterruptedException {
		
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntMoveWest();
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}
	
	@Test
	public void testMntPARK() throws RTException, InterruptedException{
	
		ActivityStateMount state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		while ((state != ActivityStateMount.STOP)  && (state != ActivityStateMount.PARKED))
			state = (((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState());
		
		try{
			mount.mntPark();
			Thread.sleep(500);
			assertEquals("MOVING",((DeviceMount) DeviceDiscoverer.devGetDevice(mount.getDeviceId(), false)).getActivityState().value());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

}
