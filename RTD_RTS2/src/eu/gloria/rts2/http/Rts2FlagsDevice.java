package eu.gloria.rts2.http;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Error;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.CommunicationState;

/**
 * Wraps all business logic related to device flag.
 * 
 * @author jcabello
 *
 */
public abstract class Rts2FlagsDevice {
	
	
	/**
	 * Flags info.
	 */
	protected Long flags;
	
	/**
	 * Error value to compare (mask applied)
	 */
	protected long errorValueToCompare;
	
	/**
	 * Miscellaneous value to compare (mask applied)
	 */
	protected long miscValueToCompare;
	
	/**
	 * Block value to compare (mask applied)
	 */
	protected long blockValueToCompare;
	
	/**
	 * Alarm state.
	 */
	protected AlarmState alarmState;
	
	/**
	 * Additional state description.	
	 */
	protected List<String> statusDesc;
	
	/**
	 * Communication state
	 */
	protected CommunicationState communicationState;
	
	/**
	 * Block state.
	 */
	protected BlockState blockState;
	
	protected Error error;
	
	/**
	 * Constructor
	 * @param flags Flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDevice(Long flags) throws Rts2Exception{
		
		this.flags = flags;
		this.statusDesc = new ArrayList();
		this.error = new Error();
		this.error.setCod(0); //By default
		this.error.setMsg("NONE");
		
		this.errorValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_FLAG_ERROR_MASK);
		this.miscValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_MASK);
		this.blockValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_FLAG_BLOCK_OPERATION_MASK);
		
		
		resolveAlarmState();
		resolveCommunicationState();
		resolveBlockState();
	}
	
	/**
	 * Resolves the alarm state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveAlarmState() throws Rts2Exception{
		
		//By default
		alarmState = AlarmState.MALFUNCTION;
		
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			
			this.error.setCod(0); //By default
			this.error.setMsg("NONE");
			
			alarmState = AlarmState.NONE;
			statusDesc.add("Not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			
			this.error.setCod(1); //By default
			this.error.setMsg("Error. Kill. (CodError=Unknown)");
			
			alarmState = AlarmState.DRIVER_SW;
			statusDesc.add("A KILL cmd executed.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			
			this.error.setCod(1); //By default
			this.error.setMsg("Error. HW. (CodError=Unknown)");
			
			alarmState = AlarmState.DRIVER_HW;
			statusDesc.add("Error HW. ");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			
			this.error.setCod(1); //By default
			this.error.setMsg("Error. Not ready. (CodError=Unknown)");
			
			alarmState = AlarmState.DRIVER_SW;
			statusDesc.add("Device is not ready.");
		}
		
		//Adds miscellaneous description ONLY
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			statusDesc.add("State is changing..");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			statusDesc.add("The device must be reloaded!!!.");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			statusDesc.add("The device is starting up!!!.");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			statusDesc.add("The device is shutdown.");
		}
		
	}
	
	/**
	 * Resolves the communication state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveCommunicationState() throws Rts2Exception{
		
		//By default -> BUSY
		communicationState = CommunicationState.BUSY;
		
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			communicationState = CommunicationState.READY;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			communicationState = CommunicationState.BUSY;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			communicationState = CommunicationState.BUSY;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			communicationState = CommunicationState.BUSY;
		}
		
		
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			communicationState = CommunicationState.IDLE;
		}
	}
	
	/**
	 * Resolves the device block state
	 * @throws Rts2Exception In error case.
	 */
	private void resolveBlockState() throws Rts2Exception{
		
		//By default -> BUSY
		blockState = BlockState.UNBLOCK;
		
		if (blockValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_BLOCK_OPERATION_EXPOSURE){
			blockState = BlockState.BLOCKED_EXPOSURE;
		}else if (blockValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_BLOCK_OPERATION_READOUT){
			blockState = BlockState.BLOCKED_READOUT;
		}else if (blockValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_BLOCK_OPERATION_TEL_MOVE){
			blockState = BlockState.BLOCKED_TEL_MOVE;
		}else if (blockValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_BLOCK_OPERATION_WILL_EXPOSURE){
			blockState = BlockState.BLOCKED_WILL_EXPOSURE;
		}
		
	}
	
	/**
	 * Access method.
	 * @return value
	 */
	public CommunicationState getCommunicationState(){
		return this.communicationState;
	}

	/**
	 * Access method.
	 * @return value
	 */
	public Long getFlags() {
		return flags;
	}
  
	/**
	 * Access method
	 * @return value.
	 */
	public AlarmState getAlarmState() {
		return alarmState;
	}
	
	/**
	 * Access method
	 * @return value.
	 */
	public BlockState getBlockState() {
		return blockState;
	}
	
	/**
	 * Access method
	 * @return Vaue
	 */
	public Error getError(){
		return error;
	}

	/**
	 * Access method.
	 * @return value.
	 */
	public String getStateDesc() {
		
		StringBuilder sb = new StringBuilder();
		for (String msg : this.statusDesc) {
			sb.append("[").append(msg).append("]");
		}
					
		return sb.toString();
	}

	
}
