package app.geodat.Util;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class Constantes {

	public static String directorio = Environment.getExternalStorageDirectory() + "/NubeGIS/";
	//public static String imei;
	public static String fichero = directorio+"audio.wav";
//	public static final String IP = "54.162.149.226";
	public static final String USER = "nubegis";
	public static final String PASS = "nubegis";
	public static final int I_CAM_FOTO = 1;
	public static final int I_CAM_COD = 2;
}
