package eu.gloria.rts2.http;

/**
 * Class for maintaining RTS2 constants.
 * 
 * @author jcabello
 *
 */
public class Rts2Constants {
	
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_STRING=0x00000001;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_INTEGER=0x0000002;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_TIME=0x00000003;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_DOUBLE=0x00000004;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_FLOAT=0x00000005;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_BOOL=0x00000006;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_SELECTION=0x00000007;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_LONGINT=0x00000008;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_RADEC=0x00000009;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_ALTAZ=0x0000000A;
	
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_NONE=0x00000000;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_STAT=0x00000010;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_MMAX=0x00000020;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_RECTANGLE=0x00000030;
	public static final long  RTS2_FLAG_MASK_COMP_TYPE_ARRAY=0x00000040;
	
	public static long RTS2_BASE_TYPE_MASK=0x0000000f;
	public static long RTS2_FULL_VALUE_MASK=0x000000ff;
	public static long RTS2_EXT_TYPE_MAX=0x000000f0;
	
	public static long RTS2_VALUE_WRITABLE_MASK=0x02000000;
	
	public static long RTS2_DEVICE_FLAG_ERROR_MASK = 0x00ff0000;
	public static long RTS2_DEVICE_FLAG_ERROR_NO = 0x00000000;
	public static long RTS2_DEVICE_FLAG_ERROR_KILL = 0x00010000;
	public static long RTS2_DEVICE_FLAG_ERROR_HW = 0x00020000;
	public static long RTS2_DEVICE_FLAG_ERROR_NOT_READY = 0x00040000;
	
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_MASK = 0x0000f000;
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR = 0x00001000;
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD = 0x00002000;
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP = 0x00004000;
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN = 0x00008000;
	public static long RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE = 0x00000000;
	
	public static long RTS2_DEVICE_CCD_FLAG_EXPOSING_MASK = 0x0001;
	public static long RTS2_DEVICE_CCD_FLAG_EXPOSING = 0x0001;
	public static long RTS2_DEVICE_CCD_FLAG_READING_MASK = 0x0002;
	public static long RTS2_DEVICE_CCD_FLAG_READING = 0x0002;
	public static long RTS2_DEVICE_CCD_FLAG_SHUTTER_MASK = 0x0300;
	public static long RTS2_DEVICE_CCD_FLAG_SHUTTER_CLEARED = 0x0000;
	public static long RTS2_DEVICE_CCD_FLAG_SHUTTER_SET = 0x01000;
	public static long RTS2_DEVICE_CCD_FLAG_SHUTTER_TRANS = 0x0200;
	public static long RTS2_DEVICE_CCD_FLAG_FOCUSSING_MASK = 0x0800;
	public static long RTS2_DEVICE_CCD_FLAG_FOCUSSING = 0x0800;
	public static long RTS2_DEVICE_CCD_FLAG_HAS_IMG_MASK = 0x0008;
	public static long RTS2_DEVICE_CCD_FLAG_HAS_IMG = 0x0008;
	public static long RTS2_DEVICE_FOCUSER_FLAG_FOCUSSING_MASK = 0x01;
	public static long RTS2_DEVICE_FOCUSER_FLAG_FOCUSSING = 0x01;
	public static long RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_MASK = 0x07;
	public static long RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_OBSERVING = 0x00;
	public static long RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_MOVING = 0x01;
	public static long RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_PARKED = 0x02;
	public static long RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_PARKING = 0x04;
	public static long RTS2_DEVICE_MOUNT_FLAG_TRACK_MASK = 0x0000;
	public static long RTS2_DEVICE_MOUNT_FLAG_TRACK_TRACKING = 0x0020;
	public static long RTS2_DEVICE_DOME_FLAG_MOVEMENT_MASK = 0x40;
	public static long RTS2_DEVICE_DOME_FLAG_MOVEMENT_NOT_MOVING = 0x00;
	public static long RTS2_DEVICE_DOME_FLAG_MOVEMENT_MOVING = 0x40;
	public static long RTS2_DEVICE_DOME_FLAG_SYNCH_MASK = 0x80;
	public static long RTS2_DEVICE_DOME_FLAG_SYNCH_SYNCH = 0x80;
	public static long RTS2_DEVICE_DOME_FLAG_SYNCH_NOT_SYNCH = 0x00;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_MASK = 0x0f;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_UNKNOW = 0x00;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_CLOSED = 0x01;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_OPENING = 0x02;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_OPENED = 0x04;
	public static long RTS2_DEVICE_DOME_FLAG_OPEN_CLOSING = 0x08;
	public static long RTS2_DEVICE_MIRROR_FLAG_MOVEMENT_MASK = 0x1f;
	public static long RTS2_DEVICE_MIRROR_FLAG_MOVEMENT_MOVE = 0x10;
	public static long RTS2_DEVICE_MIRROR_FLAG_MOVEMENT_NOT_MOVE = 0x00;
	public static long RTS2_DEVICE_FILTER_FLAG_MOVEMENT_MASK = 0x02;
	public static long RTS2_DEVICE_FILTER_FLAG_MOVEMENT_IDLE = 0x00;
	public static long RTS2_DEVICE_FILTER_FLAG_MOVEMENT_MOVE = 0x02;
	public static long RTS2_DEVICE_ROTATOR_FLAG_ROTATING_MASK = 0x01;
	public static long RTS2_DEVICE_ROTATOR_FLAG_ROTATING = 0x01;
	
	
	public static long RTS2_MESSAGE_ERROR = 0x0001;
	public static long RTS2_MESSAGE_WARNING = 0x0002;
	public static long RTS2_MESSAGE_INFO = 0x0004;
	public static long RTS2_MESSAGE_DEBUG = 0x0008;
	public static long RTS2_MESSAGE_ALL = 0xFFFF;
	
	// various datatypes
	public static int RTS2_DATA_BYTE = 8;
	public static int RTS2_DATA_SHORT = 16;
	public static int RTS2_DATA_LONG = 32;
	public static int RTS2_DATA_LONGLONG = 64;
	public static int RTS2_DATA_FLOAT = -32;
	public static int RTS2_DATA_DOUBLE = -64;

	// unsigned data types
	public static int RTS2_DATA_SBYTE = 10;
	public static int RTS2_DATA_USHORT = 20;
	public static int RTS2_DATA_ULONG = 40;
	
	// device block
	public static long RTS2_DEVICE_FLAG_BLOCK_OPERATION_MASK=0x3F000000;
	
	public static long RTS2_DEVICE_FLAG_BLOCK_OPERATION_EXPOSURE=0x01000000;
	public static long RTS2_DEVICE_FLAG_BLOCK_OPERATION_READOUT=0x02000000;
	public static long RTS2_DEVICE_FLAG_BLOCK_OPERATION_TEL_MOVE=0x04000000;
	public static long RTS2_DEVICE_FLAG_BLOCK_OPERATION_WILL_EXPOSURE=0x08000000;
	
	
	// Mask for weather	
	//public static long RTS2_WEATHER_MASK = 0x80000000;

	// Allow operations as weather is acceptable,
	//public static long GOOD_WEATHER =  0x00000000;

	// Block observations because there is bad weather.
	//public static long BAD_WEATHER = 0x80000000;
	
	
}
