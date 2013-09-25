package eu.gloria.rts2.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.exception.RTException;
import eu.gloria.tools.cache.ICacheRetriever;
import eu.gloria.tools.log.LogUtil;

/**
 * Recovers the valid values for a selval RTS2 property.
 * @author jcabello
 *
 */
public class Rts2GatewaySelValCacheRetriever implements ICacheRetriever {

	/**
	 * {@inheritDoc}
	 * <p>
	 * RETRIEVER PARAMS:
	 *  -DEV_ID (String): Device identifier.
	 *  -DEV_PROPERTY_NAME (String): Device property name
	 *  
	 * RETURNS: List<String>
	 */
	@Override
	public Object retrieve(Map<String, Object> params) throws RTException {
		
		String devId = (String)params.get("DEV_ID");
		String propName = (String)params.get("DEV_PROPERTY_NAME");
		
		LogUtil.info(this, ">>>>>>>>>>>>>>Recovering SELVAL. BEGIN");
		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.selval);
		cmd.getParameters().put("d", devId);
		cmd.getParameters().put("n", propName); //No extended format
	
		String jsonContent = cmd.execute();
		LogUtil.info(this, ">>>>>>>>>>>>>>Recovering SELVAL. END");

		
		try{
			ObjectMapper mapper = new ObjectMapper();
			List<String> values = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
			return values;
		}catch(Exception ex){
			throw new Rts2Exception("Error recovering the possible values for a property. " + ex.getMessage());
		}
		
	}

}
