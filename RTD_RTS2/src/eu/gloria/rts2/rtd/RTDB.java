package eu.gloria.rts2.rtd;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import eu.gloria.rt.entity.device.DbFileInfo;
import eu.gloria.rt.entity.device.DbFileMetadata;
import eu.gloria.rt.entity.device.DbFileSystemInfo;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtd.RTDDBInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CommunicationException;
import eu.gloria.rts2.rtd.DeviceRTD;

/**
 * RTS2 RTDDBInterface implementation.
 * 
 * @author mclopez
 *
 */
public class RTDB extends DeviceRTD implements RTDDBInterface {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean dbIsAvailableDirectAccess() throws RTException {
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean dbIsAvailableChunkAccess() throws RTException {
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean dbIsAvailableURIAccess() throws RTException {
		
		return false;
	}

	@Override
	public DbFileInfo dbGetFileInfo(String folder, String fileName) throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 API for images: <i>/fits</i>
	 */
	@Override
	public List<Byte> dbGetFileContent(String folder,String fileName) throws RTException {
		return null;
			
		//It hasn't been test
//		DeviceProperty property = this.devGetDeviceProperty("path");
//		
//		Rts2Cmd cmd = Rts2Cmd.getNewImageAccess("fits", property.getValue().get(0)+folder+"/"+fileName);
//		
//		try {
//			byte[] response = cmd.executeBinary();	
//			return Arrays.asList(ArrayUtils.toObject(response));
//			
//		} catch (Rts2CommunicationException e) {
//			throw new RTException ("Cannot get file content. "+e.getMessage());
//		}		
	}

	@Override
	public List<Byte> dbGetFileChunk(String fileName, long offset, long size)
			throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String dbGetFileURI(String fileName) throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dbWriteFile(String fileName, List<Byte> content, List<DbFileMetadata> metadata) throws RTException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DbFileMetadata>  dbGetMetadata(String fileName) throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbFileSystemInfo dbGetFileSystemInfo() throws RTException {
		
		DbFileSystemInfo info = new DbFileSystemInfo();
		
		DeviceProperty property = this.devGetDeviceProperty("free");
		
		info.setFree(Long.valueOf(property.getValue().get(0)));
		
		return info;		
	}

	@Override
	public void dbRenameFile(String currentFileName, String newFileName)
			throws RTException {
		// TODO Auto-generated method stub

	}

	@Override
	public void dbDeleteFile(String fileName) throws RTException {
		// TODO Auto-generated method stub

	}

}
