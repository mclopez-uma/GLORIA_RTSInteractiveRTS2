package eu.gloria.rts2.rtd.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.gloria.rt.entity.device.ActivityStateCamera;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.rtd.CameraRTD;
import eu.gloria.rt.entity.device.DeviceCamera;

public class CameraRTDTest {

	CameraRTD camera = null;
	
	public CameraRTDTest (){
		
		camera = new CameraRTD ();
		camera.setDeviceId("C0");
	}
	
	@Test
	public void testCamStartExposure() throws RTException, InterruptedException {
		
		while ((((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState())!= ActivityStateCamera.READY);
		
		try{			
			camera.camSetExposureTime(10);
			try{
				camera.camStartExposure(true);
			}catch (UnsupportedOpException e){			
				fail("This method has to be supported");
			}
			Thread.sleep(500);
			assertEquals("EXPOSING",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());			
		}catch (UnsupportedOpException e){			
			try{
				camera.camStartExposure(true);
			}catch (UnsupportedOpException ex){			
				fail("This method has to be supported");
			}
			Thread.sleep(100);
			assertEquals("EXPOSING",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());
		}
	}
	
	
	
	@Test
	public void testCamCanAbortExposure() throws RTException {
		try{
			if (!camera.camCanAbortExposure()){
				try{
					camera.camAbortExposure();
					fail("Inconsistency: camCanAbortExposure() vs camAbortExposure()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camAbortExposure();
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanAbortExposure() vs camAbortExposure()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}
	

	@Test
	public void testCamCanGetCoolerPower() throws RTException {
		try{
			if (!camera.camCanGetCoolerPower()){
				try{
					camera.camGetCoolerPower();
					fail("Inconsistency: camCanGetCoolerPower() vs camGetCoolerPower()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetCoolerPower();
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanGetCoolerPower() vs camGetCoolerPower()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamCanSetCCDTemperature() throws RTException {
		try{
			if (!camera.camCanSetCCDTemperature()){
				try{
					camera.camSetCCDTemperature(200);
					fail("Inconsistency: camCanSetCCDTemperature() vs camSetCCDTemperature()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camSetCCDTemperature(200);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanSetCCDTemperature() vs camSetCCDTemperature()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamCanControlTemperature() throws RTException {
		try{
			if (!camera.camCanControlTemperature()){
				try{
					camera.camGetCCDCurrentTemperature();
					fail("Inconsistency: camCanControlTemperature() vs camGetCCDCurrentTemperature()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetCCDCurrentTemperature();
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanControlTemperature() vs camGetCCDCurrentTemperature()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamCanStopExposure() throws RTException {
		try{
			if (!camera.camCanStopExposure()){
				try{
					camera.camStopExposure();
					fail("Inconsistency: camCanStopExposure() vs camStopExposure()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camStopExposure();
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanStopExposure() vs camStopExposure()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	
	@Test
	public void testCamHasBrightness() throws RTException {
		try{
			if (!camera.camHasBrightness()){
				try{
					camera.camGetBrightness();
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetBrightness();
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamHasConstrast() throws RTException {
		try{
			if (!camera.camHasConstrast()){
				try{
					camera.camGetContrast();
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetContrast();
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamHasGain() throws RTException {
		try{
			if (!camera.camHasGain()){
				try{
					camera.camGetGain();
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetGain();
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamHasGamma() throws RTException {
		try{
			if (!camera.camHasGamma()){
				try{
					camera.camGetGamma();
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camGetGamma();
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamHasSubframe() throws RTException {
		try{
			if (!camera.camHasSubframe()){
				try{
					camera.camSetROINumX(2);
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
				try{
					camera.camSetROINumY(2);
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camSetROINumX(2);
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamHasExposureTime() throws RTException {
		try{
			if (!camera.camHasExposureTime()){
				try{
					camera.camSetExposureTime(5);
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camSetExposureTime(5);
				}catch (RTException e){
					assertFalse(e.getMessage().equals("Operation not supported"));
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamSetBinY() throws RTException {
		try{
			camera.camSetBinY(1);
			assertEquals("Failed to assert ",1, camera.camGetBinY());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}
	
	@Test
	public void testCamSetBinX() throws RTException {
		
		try{
			camera.camSetBinX(1);
			assertEquals("Failed to assert ",1, camera.camGetBinX());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
		
	}
	

	@Test
	public void testCamSetCoolerOn() throws RTException {
		try{
			boolean valor = true;
			camera.camSetCoolerOn(valor);			
			assertEquals("Failed to assert ",valor, camera.camIsCoolerOn());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetROINumX() throws RTException {
		try{			
			camera.camSetROINumX(1);
			assertEquals("Failed to assert ",1, camera.camGetROINumX());
		}catch (UnsupportedOpException e){
			if (camera.camHasSubframe())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetROINumY() throws RTException {
		try{			
			camera.camSetROINumY(1);
			assertEquals("Failed to assert ",1, camera.camGetROINumY());
		}catch (UnsupportedOpException e){
			if (camera.camHasSubframe())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetROIStartX() throws RTException {
		try{			
			camera.camSetROIStartX(1);
			assertEquals("Failed to assert ",1, camera.camGetROIStartX());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetROIStartY() throws RTException {
		try{			
			camera.camSetROIStartY(1);
			assertEquals("Failed to assert ",1, camera.camGetROIStartY());
		}catch (UnsupportedOpException e){
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetBrightness() throws RTException {
		try{			
			camera.camSetBrightness(5);
			assertEquals("Failed to assert ",5, camera.camGetBrightness());
		}catch (UnsupportedOpException e){
			if (camera.camHasBrightness())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetContrast() throws RTException {
		try{			
			camera.camSetContrast(5);
			assertEquals("Failed to assert ",5, camera.camGetContrast());
		}catch (UnsupportedOpException e){
			if (camera.camHasConstrast())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetGain() throws RTException {
		try{			
			camera.camSetGain(5);
			assertEquals("Failed to assert ",5, camera.camGetGain());
		}catch (UnsupportedOpException e){
			if (camera.camHasGain())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetGamma() throws RTException {
		try{			
			camera.camSetGamma(5);
			assertEquals("Failed to assert ",5, camera.camGetGamma());
		}catch (UnsupportedOpException e){
			if (camera.camHasGamma())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetExposureTime() throws RTException {
		try{			
			camera.camSetExposureTime(10);
			assertEquals(10, camera.camGetExposureTime(), 0.01);
		}catch (UnsupportedOpException e){
			if (camera.camHasExposureTime())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	@Test
	public void testCamSetCCDTemperature() throws RTException {
		try{			
			camera.camSetCCDTemperature(283);
			assertEquals(283, camera.camGetCCDTemperature(), 0.1);
		}catch (UnsupportedOpException e){
			if (camera.camCanSetCCDTemperature())
				fail("This method has to be supported");
			else
				assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}

	

	@Test
	public void testCamStopExposure() throws RTException, InterruptedException  {
		
		try{
			boolean value = camera.camCanStopExposure();
			
			if (value){
				try{
					camera.camSetExposureTime(20);
					try{
						camera.camStartExposure(true);
					}catch (UnsupportedOpException ex){			
						fail("This method has to be supported");
					}
					Thread.sleep(100);
					camera.camStopExposure();
					assertEquals("READY",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());
				}catch (UnsupportedOpException e){			
					try{
						camera.camStartExposure(true);
					}catch (UnsupportedOpException ex){			
						fail("This method has to be supported");
					}
					Thread.sleep(100);
					camera.camStopExposure();
					assertEquals("READY",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());
				}				

			}
		}catch (UnsupportedOpException e){	
			fail("This method has to be supported");
		}		
		
	}

	
	@Test
	public void testCamCanSetCooler() throws RTException {
		try{
			if (!camera.camCanSetCooler()){
				try{
					camera.camSetCoolerOn(false);
					fail("Inconsistency: camCanSetCooler() vs camSetCoolerOn()");
				}catch (UnsupportedOpException e){
					assertEquals("Failed to assert ","Operation not supported",e.getMessage());
				}
			}else{
				try{
					camera.camSetCoolerOn(false);
				}catch (UnsupportedOpException e){
					fail("Inconsistency: camCanSetCooler() vs camSetCoolerOn()");
				}
			}
		}catch (UnsupportedOpException e){
			fail("This method has to be supported");
		}
	}

	@Test
	public void testCamSetBitDepth() throws RTException {
		try{			
			camera.camSetBitDepth(16);
			assertEquals(10, camera.camGetExposureTime(), 0.01);
		}catch (UnsupportedOpException e){			
			assertEquals("Failed to assert ","Operation not supported",e.getMessage());
		}
	}
	
	@Test
	public void testCamAbortExposure() throws RTException, InterruptedException  {
		
		while ((((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState())!= ActivityStateCamera.READY);
		
		try{
			boolean value = camera.camCanAbortExposure();
			
			if (value){
				try{
					camera.camSetExposureTime(20);
					try{
						camera.camStartExposure(true);						
					}catch (UnsupportedOpException ex){			
						fail("camStartExposure() has to be supported");
					}
					Thread.sleep(100);					
					camera.camAbortExposure();
					assertEquals("READY",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());

				}catch (UnsupportedOpException e){			
					try{
						camera.camStartExposure(true);
					}catch (UnsupportedOpException ex){			
						fail("This method has to be supported");
					}
					Thread.sleep(100);
					camera.camAbortExposure();
					assertEquals("READY",((DeviceCamera) DeviceDiscoverer.devGetDevice(camera.getDeviceId(), false)).getActivityState().value());
				}				

			}
		}catch (UnsupportedOpException e){	
			fail("This method has to be supported");
		}		
		
	}

}
