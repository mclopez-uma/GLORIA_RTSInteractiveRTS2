package eu.gloria.rts2.http;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.Image;
import eu.gloria.rt.entity.device.ImageContentType;
import eu.gloria.rts2.rtd.CameraRTD;
import eu.gloria.rts2.rtd.FocuserRTD;


/**
 * Provides all methods to access to RTS2.
 * 
 * @author jcabello
 *
 */
public class Rts2Gateway {
	
	/*public static void main(String[] args) {
		
		try {
			//System.out.println("LOGS=" + System.getProperty("java.util.logging.config.file"));
			//DeviceManager manager = new DeviceManager();
			
			//manager.devGetDevices();
			/*boolean connected = manager.devIsConnected("C0");
			manager.devConnect("C0");
			connected = manager.devIsConnected("C0");
			manager.devDisconnect("C0");
			connected = manager.devIsConnected("C0");
			
			Device dev = manager.devGetDevice("C0");
			
			
			ArrayList<String> values = new ArrayList<String>();
			values.add("0");
			values.add("0");
			values.add("300");
			values.add("500");
			
			manager.devUpdateDeviceProperty("C0", "WINDOW", values);*/
			
			//CameraRTD camera = new CameraRTD();
			//camera.camSetExposureTime("C0", 60);
			//camera.camStartExposure("C0",true);
			//Image img = camera.camGetImage("C0");
			//MountControl mount = new MountControl();
			//mount.mntGetPointingModel("T0");
			//DomeControl dome = new DomeControl();
			//dome.domMoveAzimuth("CUP",75);
			//FocuserRTD focus = new FocuserRTD();
			//System.out.println("LOGS="+ focus.focGetPosition());
			
			//System.out.println("CMD ends....");
						
			
			/*ObjectMapper mapper = new ObjectMapper();
			String jsoncontent = "{\"d\":{\"infotime\":[16778243,1328095340.41172504,0,0,\"time of last update\"],\"focuser\":[16778241,\"F0\",0,0,\"\"],\"wheel\":[16778241,\"W0\",0,0,\"\"],\"wheelA\":[16778241,\"W0\",0,0,\"\"],\"CCD_TYPE\":[16778497,\"Dummy\",0,0,\"camera type\"],\"CCD_SER\":[16778497,\"1\",0,0,\"camera serial number\"],\"chips\":[16778242,1,0,0,\"\"],\"SCRIPREP\":[33554690,0,0,0,\"script loop count\"],\"SCRIPT\":[34603265,\"\",0,0,\"script used to take this images\"],\"SCR_COMM\":[33554689,\"\",0,0,\"comment recorded for this script\"],\"COMM_NUM\":[33554690,0,0,0,\"comment order within current script\"],\"script_status\":[33554439,0,0,0,\"script status\"],\"scriptPosition\":[33554434,0,0,0,\"position within script\"],\"scriptLen\":[33554434,0,0,0,\"length of the current script element\"],\"elementPosition\":[50332802,[],0,0,\"position of element within the script\"],\"calculate_stat\":[33554439,0,0,0,\"if statistics values should be calculated\"],\"average\":[16778244,null,0,0,\"image average\"],\"max\":[16778244,null,0,0,\"maximum pixel value\"],\"min\":[16778244,null,0,0,\"minimal pixel value\"],\"sum\":[16778244,null,0,0,\"sum of pixels readed out\"],\"computed\":[8,0,0,0,\"number of pixels so far computed\"],\"que_exp_num\":[33554434,0,0,0,\"number of exposures in que\"],\"exposure_num\":[8,0,0,0,\"number of exposures camera takes\"],\"script_exp_num\":[8,0,0,0,\"number of images taken in script\"],\"exposure_end\":[16778243,null,0,0,\"expected end of exposure\"],\"wait_for_que\":[16778246,0,0,0,\"if camera is waiting for empty que\"],\"wait_for_notbop\":[16778246,0,0,0,\"if camera is waiting for not bop state\"],\"SIZE\":[16778290,[0,0,400,500],0,0,\"chip size\"],\"WINDOW\":[50332722,[0,0,400,500],0,0,\"used chip subframe\"],\"binning\":[33554695,0,0,0,\"[pixelX x pixelY] chip binning\"],\"BINX\":[16778498,1,0,0,\"[pixels] binning along X axis\"],\"BINY\":[16778498,1,0,0,\"[pixels] binning along X axis\"],\"data_type\":[33554439,0,0,0,\"used data type\"],\"exposure\":[50332708,1.00000000,0,0,\"current exposure time\"],\"FLIP\":[16778498,1,0,0,\"camera flip (since most astrometry devices works as mirrors\"],\"pixels_second\":[17368068,null,0,0,\"[pixels/second] average readout speed\"],\"focpos\":[33554434,0,0,0,\"position of focuser\"],\"CCD_ROTA\":[50726148,0.00000000,0,0,\"CCD rotang\"],\"fw_moving\":[16778246,0,0,0,\"if filter wheel is moving\"],\"foc_moving\":[16778246,0,0,0,\"if focuser is moving\"],\"CCD_TEMP\":[16778501,31.69428444,0,0,\"CCD temperature\"],\"readout\":[50332932,0.00000000,0,0,\"readout sleep in sec\"],\"readout_size\":[50332680,4096,0,0,\"[pixels] number of pixels send on a single read\"],\"exp_min\":[50332676,0.00000000,0,0,\"[s] minimal exposure time\"],\"exp_max\":[50332676,3600.00000000,0,0,\"[s] maximal exposure time\"],\"gen_type\":[33554695,0,0,0,\"data generation algorithm\"],\"noise_range\":[50332676,300.00000000,0,0,\"readout noise range\"],\"has_error\":[50332678,0,0,0,\"if true, info will report error\"],\"SHUTTER\":[33554695,0,0,0,\"shutter state\"],\"CCD_AIR\":[16778501,32.87946320,0,0,\"detector air temperature\"],\"COOLING\":[51053830,2,0,0,\"camera cooling start/stop\"],\"CCD_SET\":[50332964,null,0,0,\"CCD set temperature\"],\"nightcool\":[50332677,null,0,0,\"night cooling temperature\"],\"temp_min\":[50332676,-257.00000000,0,0,\"[C] minimal set temperature\"],\"temp_max\":[50332676,50.00000000,0,0,\"[C] maximal set temperature\"],\"filter\":[33554439,0,0,0,\"used filter number\"],\"filter_offsets\":[50332804,[],0,0,\"filter offsets\"],\"FILTA\":[33554439,0,0,0,\"used filter number\"],\"filter_offsets_A\":[50332804,[],0,0,\"filter offsets\"]},\"minmax\":{\"exposure\":[0.00000000,3600.00000000],\"CCD_SET\":[-257.00000000,50.00000000]},\"idle\":1,\"state\":0,\"sstart\":null,\"send\":null,\"f\":null}";
			Object kk = mapper.readValue(jsoncontent, Object.class);*/
			
			//Rts2GatewayDeviceManager gate = new Rts2GatewayDeviceManager();
			//Device[] devList = gate.getDevices();
			
			/*Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.expose);
			cmd.getParameters().put("ccd", "C0");
			cmd.getParameters().put("fe", "jcabello");
			  
			String result = cmd.execute();*/
			
			/*Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
			cmd.getParameters().put("d", "SEL");
			cmd.getParameters().put("e", "0");
			cmd.getParameters().put("from", "10000000");
			
			
			String result = cmd.execute();
			
			HashMap obj = (HashMap) JsonObj.fromJson(result);*/
			
			
			
            
      /*  } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
		
	}*/

}
