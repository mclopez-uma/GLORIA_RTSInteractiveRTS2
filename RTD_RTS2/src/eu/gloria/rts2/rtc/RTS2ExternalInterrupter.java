package eu.gloria.rts2.rtc;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.rts2.rtd.CameraRTD;
import eu.gloria.tools.configuration.Config;

public class RTS2ExternalInterrupter implements IRtsExternalInterrupter {

	
	private String executor = null;
	
	private Timer waitingTimer = null;
	
	private ExtRtsInterruptionInfo info;
	
	private String idleTarget = null;
	
	private String idTarget = null;
	
	
	public static void main(String[] args) throws Exception {
		
		RTS2ExternalInterrupter interrup = new RTS2ExternalInterrupter();
		
		interrup.getInterruptionInfo().setState(ExtExecInterruptionState.INTERRUPTED);
		
//		interrup.getInterruptionInfo().setState(ExtExecInterruptionState.WAIT_TURN);
		
		System.out.print(interrup.info);
//		interrup.interrupt();
//		
//		Thread.sleep(10000);
		
		interrup.resume();
		
		
	}
	
	
	public RTS2ExternalInterrupter(){		
		
		this.executor = Config.getProperty("rtd_rts2", "executor");
		this.idleTarget = Config.getProperty("rtd_rts2", "idTarget");
		
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
		
		
		if (getCurrentTarget().equals(idTarget)){
			info.setInterruptable(false);
			info.setUnInterruptableReason("Alert target");
			
			throw new ExtRtsInterruptionException("Alert target running");
		}
		
		idTarget = null;
		try {

			
			long time = Rts2Date.now();						
			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			gatewayDevManager.executeCmd (this.executor,  "now_single+"+String.valueOf(idleTarget), false);
			
			Rts2Messages.handleErrorMessages("now_single",Rts2MessageType.error, this.executor, time);
			
			
			info.setInterruptable(false);
			info.setState(ExtExecInterruptionState.WAIT_TURN);
			info.setUnInterruptableReason("Waiting...turn");

			waitingTimer = new Timer(true);
			waitingTimer.schedule(new Task(), 0, 1000);

			
		} catch (Rts2CommunicationException e) {
			throw new ExtRtsInterruptionException("Error interrupting the system");
		} catch (Rts2Exception e) {
			throw new ExtRtsInterruptionException("Error interrupting the system");
		} catch (RTException e) {
			
			if (e.getMessage().contains("unknow command now_single")){	//B3 no tiene now_single de momento
				try {
					long time = Rts2Date.now();
					Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();

					gatewayDevManager.executeCmd (this.executor,  "now+"+String.valueOf(idleTarget), false);

					Rts2Messages.handleErrorMessages("now",Rts2MessageType.error, this.executor, time);


					info.setInterruptable(false);
					info.setState(ExtExecInterruptionState.WAIT_TURN);
					info.setUnInterruptableReason("Waiting...turn");

					waitingTimer = new Timer(true);
					waitingTimer.schedule(new Task(), 0, 1000);
				} catch (Rts2CommunicationException e1) {
					throw new ExtRtsInterruptionException("Error interrupting the system");
				} catch (RTException e2) {
					throw new ExtRtsInterruptionException("Error interrupting the system");
				}
			}
			
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
			
			if (waitingTimer != null)
				waitingTimer.cancel();
			
		case INTERRUPTED:
			
			String target = getNextTarget();

			long time = Rts2Date.now();						

			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			try {
				
				if (!target.equals("-1")){			
				
					gatewayDevManager.executeCmd (this.executor,  "now_single+"+String.valueOf(target), false);

					Rts2Messages.handleErrorMessages("now_single",Rts2MessageType.error, this.executor, time);
				}else{
					
					gatewayDevManager.executeCmd (this.executor,  "stop", false);
					
				}

			} catch (Rts2CommunicationException e) {
				throw new ExtRtsInterruptionException("Error resuming the system");
			} catch (RTException e) {
				throw new ExtRtsInterruptionException("Error resuming the system");
			}
			

			info.setInterruptable(true);
			info.setState(ExtExecInterruptionState.RESUMED);
			info.setUnInterruptableReason("");
		
			break;	
		}
		
		String target = getCurrentTarget();
		if (target.equals(idTarget)){
			info.setInterruptable(false);
			info.setUnInterruptableReason("Alert target");
		}else{
			idTarget = null;
		}
		
		
		
	}

	@Override
	public ExtRtsInterruptionInfo getInterruptionInfo() {
		
		if (!info.isInterruptable()){
			try {
				String target = getCurrentTarget();
				if (!target.equals(idTarget)){
					idTarget = null;
					info.setInterruptable(true);
					info.setUnInterruptableReason("");
				}				
			} catch (ExtRtsInterruptionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return info;
	}

	@Override
	public ExtEventResume getEventResume() throws ExtRtsInterruptionException {
		
				
		if (info.getState() == ExtExecInterruptionState.INTERRUPTED){
			String target = getCurrentTarget ();

			if (target.equals(idleTarget)){
				return null;
			}else{
				idTarget = target;
				ExtEventResume event = new ExtEventResume();
				event.setDescription("RTS2 GLORIA interactive target not running");
				return event;
			}
		}else{
			return null;
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
	
	public class Task extends TimerTask {		
		
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
