package app.geodat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ServiceSeg extends Service {


	private final String DIRECTORIO = Environment.getExternalStorageDirectory() + "/NubeGIS/Seguimiento";
	private static final int INVERVALO = 1000 * 60 * 5;

	public LocationManager locationManager;
	public MyLocationListener listener;
	private Timer timer = new Timer();

	int counter = 0;
	boolean seguir = true;
	double lat;
	double lon;
	String prov;
	String fecha;
	//cero es true cuando Loc = 0
	boolean cero = false;
	String[] listado = new String[10];


	@Override
	public void onCreate() {
		super.onCreate();

		seguir = true;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (seguir) {
					generarJson();
				}
			}
		}, 0, INVERVALO);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		Log.v("ServiceDemo", "DONE");
		locationManager.removeUpdates(listener);
		seguir = false;
	}

	public void generarJson() {
		if (lat != 0.0) {
			actualizarFecha();
			JSONObject json = new JSONObject();
			try {
				String imei = get_imei();
				json.put("Imei", Double.parseDouble(imei));
				json.put("Timestamp", fecha);

				JSONArray localizacion = new JSONArray();
				localizacion.put(lat);
				localizacion.put(lon);
				json.put("Localizacion", localizacion);

			} catch (JSONException e) {

				e.printStackTrace();
			}
			String fe = fecha.replace(':', '.');
			String nombre = get_imei() + "_" + fe;

			guardarArchivo(json, nombre);
			//contar();
			SubirArchivos sa = new SubirArchivos();
			listado = ListadoArchivos.getCsvFiles(DIRECTORIO);
			sa.setLista(listado);
			Log.v("ServiceDemo", "Enviando");
			sa.execute();
			cero = false;
		} else {
			cero = true;
		}

	}

	public void actualizarFecha() {

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		fecha = (f.format(new Date()));
	}

	public void guardarArchivo(JSONObject json, String nombre) {

		nombre = nombre + ".json";

		try {

			if (Environment.getExternalStorageState().equals("mounted")) {

				FileOutputStream fout = null;
				try {

					File directory = new File(DIRECTORIO);
					directory.mkdirs();

					// creamos el archivo en el nuevo directorio creado
					File file = new File(directory, nombre);

					fout = new FileOutputStream(file);

					// Convierte un stream de caracteres en un stream de bytes
					OutputStreamWriter ows = new OutputStreamWriter(fout);
					ows.write(json.toString());
					ows.flush(); // Vuelca lo que hay en el buffer al archivo
					ows.close(); // Cierra el archivo de texto
					Log.v("ServiceDemo", "Guardado");

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception ex) {
			Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}

	}

	private boolean contar() {

		listado = ListadoArchivos.getCsvFiles(DIRECTORIO);
		if (listado.length < 5) {
			return true;
		} else {
			return false;
		}
	}

	public String get_imei() {

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText( getApplicationContext(), "Imei necesario", Toast.LENGTH_SHORT ).show();
		}
		String imei = tm.getDeviceId();

		return (imei);
//		return "860046038880938";
	}
	
	public class MyLocationListener implements LocationListener
	 {
	
	     public void onLocationChanged(final Location loc)
	     {
	    	 if (loc != null) {
		         Log.i("ServiceDemo", "Location changed");
		         lat = loc.getLatitude();
		         lon = loc.getLongitude();
		         prov = loc.getProvider();
		         
		         if(cero){ generarJson(); }
	    	 }
	     }
	
	     public void onProviderDisabled(String provider)
	     {
	         Toast.makeText( getApplicationContext(), "Gps deshabilitado", Toast.LENGTH_SHORT ).show();
	     }
	
	
	     public void onProviderEnabled(String provider)
	     {
	         Toast.makeText( getApplicationContext(), "Gps habilitado", Toast.LENGTH_SHORT).show();
	     }
	
	
	     public void onStatusChanged(String provider, int status, Bundle extras)
	     {
	
	     }
	
	 }



}
