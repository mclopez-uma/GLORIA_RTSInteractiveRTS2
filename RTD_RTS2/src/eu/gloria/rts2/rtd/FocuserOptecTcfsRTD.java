package eu.gloria.rts2.rtd;


import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.tools.log.LogUtil;

/**
 * Focuser implementation for OPTEC TCF_S version.
 * 
 * @author jcabello
 *
 */
public class FocuserOptecTcfsRTD extends FocuserRTD {
	
	
	/**
	 * Maximum step position permitted.
	 */
	private static long MAX_STEP = 7000;
	
	/**
	 * Constructor
	 */
	public FocuserOptecTcfsRTD () {
		
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Drawtube: 0.6 inch
	 * Steps: 7000
	 * Step size=( 0.6 inch -> 15240 microns) --> 15240/7000(steps) = 2.1771 microns
	 * Optec web: the step size= 2.184 microns (more o less  previous one).
	 */
	@Override
	public double focGetStepSize() throws RTException {
		
		return 2.184;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Return -1 (whatever increment)
	 */
	@Override
	public long focGetMaxIncrement() throws RTException {
		
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 */
	@Override
	public long focGetMaxStep() throws RTException {

		return MAX_STEP;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 */
	@Override
	public boolean focIsTempCompAvailable() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public double focGetTemperature() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public void focHalt() throws RTException {
		
		//Do nothing.
	}
	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Sets "FOC_TAR" from RTS2 focusd.
	 */
	@Override
	public void focMove(long position) throws RTException {
		
		long filterOffSet = 0;
		
		//Check the right position
		try{
			DeviceProperty property = this.devGetDeviceProperty("FOC_FOFF");
			filterOffSet = (Double.valueOf(property.getValue().get(0))).longValue();
		}catch (RTException e){
			//The property does not exist.
		}
		
		long realPos = position + filterOffSet;
		boolean rightPos = (0 <= realPos && realPos <= focGetMaxStep()); //0 <= POS_FOC + FOFF <= 7000
		
		if (!rightPos){
			
			String[] names = {
					"Focus Position",
					"Filter offset"
			};
			
			String[] values = {
					String.valueOf(position),
					String.valueOf(filterOffSet)
			};
			
			throw new RTException("Invalid position (0 <= POS_FOC + FOFF <= 7000): " + LogUtil.getLog(names, values));
		}
		
		//Moves
		super.focMove(position);
	}

}
