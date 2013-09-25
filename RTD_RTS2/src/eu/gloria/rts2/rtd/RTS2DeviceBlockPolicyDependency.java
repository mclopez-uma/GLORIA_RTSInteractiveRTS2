package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.DeviceType;

/**
 * This class set the block policy based on device dependencies.
 * 
 * @author jcabello
 *
 */
public class RTS2DeviceBlockPolicyDependency {

	public boolean baseOnDevType;
	public String depDevId;
	public DeviceType depDevType;
	public List<BlockState> depBlockStates;
	public BlockState targetBlockState; 
	
	/**
	 * Constructor
	 * @param baseOnDevType true if the dependence is base on the device type.
	 * @param input [dev:blockstate1, blockstate2....:targetBlockstate].
	 * @param componentSeparator  : in the previous example.
	 * @param blockStateSeparator , in the previous example.
	 */
	public RTS2DeviceBlockPolicyDependency(boolean baseOnDevType, String input, String componentSeparator, String blockStateSeparator){
		
		this.depBlockStates = new ArrayList<BlockState>();
		this.baseOnDevType = baseOnDevType;
		
		StringTokenizer compST = new StringTokenizer(input, componentSeparator);
		
		if (baseOnDevType){
			depDevType = DeviceType.fromValue(compST.nextToken());
		}else{
			depDevId = compST.nextToken();
		}
		
		//depBlockStates.
		StringTokenizer stateST = new StringTokenizer(compST.nextToken(), blockStateSeparator);
		while (stateST.hasMoreTokens()){
			this.depBlockStates.add(BlockState.fromValue(stateST.nextToken()));
		}
		
		//targetBlockState.
		this.targetBlockState = BlockState.fromValue(compST.nextToken());
	}
	
	public boolean containsDepBlockState(BlockState state){
		return depBlockStates.contains(state);
	}

	public String getDepDevId() {
		return depDevId;
	}

	public void setDepDevId(String depDevId) {
		this.depDevId = depDevId;
	}

	public DeviceType getDepDevType() {
		return depDevType;
	}

	public void setDepDevType(DeviceType depDevType) {
		this.depDevType = depDevType;
	}

	public List<BlockState> getDepBlockStates() {
		return depBlockStates;
	}

	public void setDepBlockStates(List<BlockState> depBlockStates) {
		this.depBlockStates = depBlockStates;
	}

	public BlockState getTargetBlockState() {
		return targetBlockState;
	}

	public void setTargetBlockState(BlockState targetBlockState) {
		this.targetBlockState = targetBlockState;
	}
	
	
}
