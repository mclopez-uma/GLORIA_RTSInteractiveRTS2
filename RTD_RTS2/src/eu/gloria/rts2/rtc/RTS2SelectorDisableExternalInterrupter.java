package eu.gloria.rts2.rtc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtc.environment.config.device.ConfigDeviceManager;
import eu.gloria.rtc.op.ExtEventResume;
import eu.gloria.rtc.op.ExtExecInterruptionState;
import eu.gloria.rtc.op.ExtRtsInterruptionException;
import eu.gloria.rtc.op.ExtRtsInterruptionInfo;
import eu.gloria.rtc.op.IRtsExternalInterrupter;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdGetResponse;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2CommunicationException;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2Exception;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;
import eu.gloria.rts2.rtd.CameraRTD;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.TimeOut;

public class RTS2SelectorDisableExternalInterrupter implements IRtsExternalInterrupter {
	
	private String selector = null;
	private String executor = null;
	private String idleTarget = null;
	private Integer alertTargetID = null;
	
	private Timer waitingTimer = null;
	
	private ExtRtsInterruptionInfo info;	
	
	
	public static void main(String[] args) throws Exception {
		
		RTS2SelectorDisableExternalInterrupter interrup = new RTS2SelectorDisableExternalInterrupter();
		
//		interrup.getInterruptionInfo().setState(ExtExecInterruptionState.INTERRUPTED);
		
		System.out.print(interrup.info);
		interrup.interrupt();
		
		Thread.sleep(10000);
		
		interrup.resume();
		
		
	}
	
	
	public RTS2SelectorDisableExternalInterrupter(){
		
		this.selector = Config.getProperty("rtd_rts2", "selector");
		this.executor = Config.getProperty("rtd_rts2", "executor");
		this.idleTarget = Config.getProperty("rtd_rts2", "idTarget");
		this.alertTargetID = Config.getPropertyInt("rtd_rts2", "alertID", 50000);
		
		info = new ExtRtsInterruptionInfo();
		info.setInterruptable(true);
		info.setState(ExtExecInterruptionState.RESUMED);
	}
	
	@Override
	public void interrupt() throws ExtRtsInterruptionException {

		switch(info.getState()){
		case INTERRUPTED:
			throw new ExtRtsInterruptionException("Already INTERRUPTED.");
		case WAIT_TURN:
			throw new ExtRtsInterruptionException("Waiting for turn");
		}
		
		String currentTarget = getCurrentTarget();
		
		if (Integer.valueOf(currentTarget) >= alertTargetID){
			info.setInterruptable(false);
			info.setUnInterruptableReason("Alert target");
			
			throw new ExtRtsInterruptionException("Alert target running: " + currentTarget);
		}
		
		try {

			long time = Rts2Date.now();
			
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
			cmd.getParameters().put("d", this.selector);
			cmd.getParameters().put("n", "selector_enabled");
			cmd.getParameters().put("v", "0");
				
			String jsonContent = cmd.execute();	
			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			try {
				if (!gatewayDevManager.retControl(jsonContent))
					throw new ExtRtsInterruptionException("Error interrupting the system");
			} catch (Exception e) {
				throw new ExtRtsInterruptionException("Error interrupting the system");
			} 
			
			//Message recovering
//			String message = Rts2Messages.getMessageText (Rts2MessageType.error, this.selector, time);
//			if (message != null)
//				throw new ExtRtsInterruptionException("Error interrupting the system");	
				
			//AUTOLOOP TB
			time = Rts2Date.now();				
			
//			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			gatewayDevManager.executeCmd (this.executor,  "now_single+"+String.valueOf(idleTarget), false);
			
			Rts2Messages.handleErrorMessages("now_single",Rts2MessageType.error, this.executor, time);

//			cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
//			cmd.getParameters().put("d", this.executor);
//			cmd.getParameters().put("n", "next");
//			cmd.getParameters().put("v", "-1");
//
//			jsonContent = cmd.execute();			
//
//			Rts2Messages.handleErrorMessages("executor_next",Rts2MessageType.error, this.executor, time);
			
//			//Message recovering
//			message = Rts2Messages.getMessageText (Rts2MessageType.error, this.executor, time);
//			if (message != null)
//				throw new ExtRtsInterruptionException("Error interrupting the system");	

			time = Rts2Date.now();

			cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
			cmd.getParameters().put("d", this.executor);
			cmd.getParameters().put("n", "auto_loop");
			cmd.getParameters().put("v", "0");

			jsonContent = cmd.execute();	
			
			try {
				if (!gatewayDevManager.retControl(jsonContent))
					throw new ExtRtsInterruptionException("Error interrupting the system");
			} catch (Exception e) {
				throw new ExtRtsInterruptionException("Error interrupting the system");
			} 

//			Rts2Messages.handleErrorMessages("executor_next",Rts2MessageType.error, this.executor, time);
			
			//Message recovering
//			message = Rts2Messages.getMessageText (Rts2MessageType.error, this.executor, time);
//			if (message != null)
//				throw new ExtRtsInterruptionException("Error interrupting the system");	

			time = Rts2Date.now();

			cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
			cmd.getParameters().put("d", this.executor);
			cmd.getParameters().put("n", "default_auto_loop");
			cmd.getParameters().put("v", "0");

			jsonContent = cmd.execute();
			
			try {
				if (!gatewayDevManager.retControl(jsonContent))
					throw new ExtRtsInterruptionException("Error interrupting the system");
			} catch (Exception e) {
				throw new ExtRtsInterruptionException("Error interrupting the system");
			} 

//			Rts2Messages.handleErrorMessages("executor_next",Rts2MessageType.error, this.executor, time);
			
			//Message recovering
//			message = Rts2Messages.getMessageText (Rts2MessageType.error, this.executor, time);
//			if (message != null)
//				throw new ExtRtsInterruptionException("Error interrupting the system");

			
			info.setInterruptable(false);
			info.setState(ExtExecInterruptionState.WAIT_TURN);
			info.setUnInterruptableReason("Waiting...turn");

			waitingTimer = new Timer(true);
			waitingTimer.schedule(new Task(), 0, 5000);

			
		} catch (Rts2CommunicationException e) {
			throw new ExtRtsInterruptionException("Error interrupting the system");
		} catch (Rts2Exception e) {
			throw new ExtRtsInterruptionException("Error interrupting the system");
		} catch (RTException e) {
			throw new ExtRtsInterruptionException("Error interrupting the system");
		}

	}

	@Override
	public void resume() throws ExtRtsInterruptionException {

		switch(info.getState()){
		
		case RESUMED:
			
			info.setInterruptable(true);
			info.setState(ExtExecInterruptionState.RESUMED);
			info.setUnInterruptableReason("");
			
			break;
			
		case WAIT_TURN:
			
			waitingTimer.cancel();
			
		case INTERRUPTED:
			try {
				
				long time = Rts2Date.now();
				
				Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
				cmd.getParameters().put("d", this.selector);
				cmd.getParameters().put("n", "selector_enabled");
				cmd.getParameters().put("v", "1");

				String jsonContent = cmd.execute();
				
				Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
				try {
					if (!gatewayDevManager.retControl(jsonContent))
						throw new ExtRtsInterruptionException("Error interrupting the system");
				} catch (Exception e) {
					throw new ExtRtsInterruptionException("Error interrupting the system");
				} 
				
				//Message recovering
//				String message = Rts2Messages.getMessageText (Rts2MessageType.error, this.selector, time);
//				if (message != null)
//					throw new ExtRtsInterruptionException("Error interrupting the system");
				

				time = Rts2Date.now();
				cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
				cmd.getParameters().put("d", this.executor);
				cmd.getParameters().put("n", "auto_loop");
				cmd.getParameters().put("v", "1");

				jsonContent = cmd.execute();
				
				try {
					if (!gatewayDevManager.retControl(jsonContent))
						throw new ExtRtsInterruptionException("Error interrupting the system");
				} catch (Exception e) {
					throw new ExtRtsInterruptionException("Error interrupting the system");
				} 

				//Message recovering
//				message = Rts2Messages.getMessageText (Rts2MessageType.error, this.executor, time);
//				if (message != null)
//					throw new ExtRtsInterruptionException("Error interrupting the system");	

				time = Rts2Date.now();

				cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
				cmd.getParameters().put("d", this.executor);
				cmd.getParameters().put("n", "default_auto_loop");
				cmd.getParameters().put("v", "1");

				jsonContent = cmd.execute();
				
				try {
					if (!gatewayDevManager.retControl(jsonContent))
						throw new ExtRtsInterruptionException("Error interrupting the system");
				} catch (Exception e) {
					throw new ExtRtsInterruptionException("Error interrupting the system");
				} 

				//Message recovering
//				message = Rts2Messages.getMessageText (Rts2MessageType.error, this.executor, time);
//				if (message != null)
//					throw new ExtRtsInterruptionException("Error interrupting the system");
				
				
				String target = getNextTarget();

				time = Rts2Date.now();						

//				Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
				try {
					
					if ((!target.equals("-1")) && (!(target.equals("18")))){				
					
						gatewayDevManager.executeCmd (this.executor,  "now_single+"+String.valueOf(target), false);

						Rts2Messages.handleErrorMessages("now_single",Rts2MessageType.error, this.executor, time);
					}else{
						
						gatewayDevManager.executeCmd (this.executor,  "stop", false);
						
						cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
						cmd.getParameters().put("d", this.executor);
						cmd.getParameters().put("n", "selector_next");
						cmd.getParameters().put("v", "1");

						jsonContent = cmd.execute();	
						
						try {
							if (!gatewayDevManager.retControl(jsonContent))
								throw new ExtRtsInterruptionException("Error resuming the system");
						} catch (Exception e) {
							throw new ExtRtsInterruptionException("Error resuming the system");
						} 
						
					}

				} catch (Rts2CommunicationException e) {
					throw new ExtRtsInterruptionException("Error resuming the system");
				} catch (RTException e) {
					throw new ExtRtsInterruptionException("Error resuming the system");
				}
				
				

				info.setInterruptable(true);
				info.setState(ExtExecInterruptionState.RESUMED);
				info.setUnInterruptableReason("");
					
				
			} catch (Rts2CommunicationException e) {
				throw new ExtRtsInterruptionException("Error resuming the system");
//			} catch (Rts2Exception e) {
//				throw new ExtRtsInterruptionException("Error resuming the system");
			} catch (RTException e) {
				throw new ExtRtsInterruptionException("Error resuming the system");
			}			
			break;	
		}
	}

	@Override
	public ExtRtsInterruptionInfo getInterruptionInfo() {
		
		return info;
	}

	@Override
	public ExtEventResume getEventResume() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getNextTarget () throws ExtRtsInterruptionException{
		
		try {
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
			cmd.getParameters().put("d", executor);	
			cmd.getParameters().put("e", "1");

			String jsonContent = cmd.execute();

			List<String> valueProp = new ArrayList<String>();
			valueProp.add("next");
			eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest propReq = new eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest(eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType.CUSTOM, valueProp);
			Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);
			
			return resp.getVars().get(0).getValue().get(0);
			
		}catch (Rts2CommunicationException e) {
			throw new ExtRtsInterruptionException("Error getting current target");
		} catch (Rts2Exception e) {
			throw new ExtRtsInterruptionException("Error getting current target");
		}
	}
	
	private String getCurrentTarget () throws ExtRtsInterruptionException{
		
		try {
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
			cmd.getParameters().put("d", executor);	
			cmd.getParameters().put("e", "1");

			String jsonContent = cmd.execute();

			List<String> valueProp = new ArrayList<String>();
			valueProp.add("current");
			eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest propReq = new eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest(eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType.CUSTOM, valueProp);
			Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);
			
			return resp.getVars().get(0).getValue().get(0);
			
		}catch (Rts2CommunicationException e) {
			throw new ExtRtsInterruptionException("Error getting current target");
		} catch (Rts2Exception e) {
			throw new ExtRtsInterruptionException("Error getting current target");
		}
	}

	public class Task extends TimerTask {		
		
		private TimeOut timeOut;
		
		public Task() {
			this.timeOut = new TimeOut(60000);
		}
		
		@Override
		public void run() {
			
			try {
				Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
				cmd.getParameters().put("d", executor);	
				cmd.getParameters().put("e", "1");

				String jsonContent = cmd.execute();
				
				List<String> valueProp = new ArrayList<String>();
				valueProp.add("current");
				eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest propReq = new eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest(eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType.CUSTOM, valueProp);
				Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);
				
				
				if (resp.getVars().get(0).getValue().get(0).equals(idleTarget)){
					
					//Test shutter property in ccds
					shutterReset();
					
					info.setInterruptable(false);
					info.setState(ExtExecInterruptionState.INTERRUPTED);
					info.setUnInterruptableReason("Already INTERRUPTED.");
					
					waitingTimer.cancel(); 
					
				}else if (timeOut.timeOut()){
					LogUtil.severe(this,"Timeout waiting for interruption");
					
					resume();
					
					waitingTimer.cancel();					
					
				}
			} catch (Rts2CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Rts2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}

		}
		
		private void shutterReset (){
			
			ConfigDeviceManager configDeviceManager = null;
			try {
				configDeviceManager = new ConfigDeviceManager();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
			List <String> removeDevices = new ArrayList<String> ();
			
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.devbytype);
			
			cmd.getParameters().put("t", "3");
			
			try {
				String jsonContent = cmd.execute();
				
				ObjectMapper mapper = new ObjectMapper();
				ArrayList<String> values = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
				
				//Removing of devices not in xml
				for (String deviceId: values){
					eu.gloria.rt.entity.environment.config.device.Device dev = configDeviceManager.getDevice(deviceId);
					if (dev == null){
						removeDevices.add(deviceId);
					}
				}			
				values.removeAll(removeDevices);
				
				List<String> valueProp = new ArrayList<String>();
				valueProp.add("LIGHT");
				
				for (String dev: values){	
					try{
						DeviceProperty prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty("SHUTTER");
						if (prop.getValue().get(0).equals("DARK")){
							((CameraRTD) DeviceDiscoverer.getRTD(dev)).devUpdateDeviceProperty("SHUTTER", valueProp);
						}
					} catch (Exception e) {
						if (!e.getMessage().contains("The property does not exist")){
							throw new RTException(e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
