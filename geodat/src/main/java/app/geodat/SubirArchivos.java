package app.geodat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import app.geodat.Util.Constantes;

public class SubirArchivos extends AsyncTask<String, Void, Integer>{
	
	//BS AS
	///*
	/*final String IP = "190.2.47.186";
	final String USER = "alfagis-server";
	final String PASS = "Alfagis0101";*/
	//*/
	//AMAZON
	
	//final String IP = "54.175.120.6";
	final String USER = "nubegis";
	final String PASS = "nubegis";
	
	String DIRECTORIO = Environment.getExternalStorageDirectory() + "/NubeGIS/Seguimiento/";
	
	int resultCode;
	File Fichero = new File(DIRECTORIO);
	String[]lista;
	
	public void setLista(String[] lista) {
		   this.lista = lista;
		}

	@Override
	protected Integer doInBackground(String... params) {

		try {

//			FTPClient ftpClient = new FTPClient();
//			ftpClient.connect(Constantes.IP);
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect("www.geodatlog.com", 21);
            ftpClient.enterLocalPassiveMode();

		if (!ftpClient.login(Constantes.USER, Constantes.PASS)) {
				cancel(true);
				Log.d("ServiceDemo", "Cancelado. No se pudo conectar con el servidor");

			} else {

				if (ftpClient.changeWorkingDirectory("seguimiento")) {
					Log.d("ServiceDemo", "Cambio. Se cambio a la carpeta seguimiento");
				} else {
					Log.d("ServiceDemo", "Error. No Se cambio a la carpeta seguimiento");
					cancel(true);
				}
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

				if (lista.length > 0) {

					int cont = 1;
					for (String s : lista) {
						Log.d("ServiceDemo", "Leyendo archivo " + cont);
						BufferedInputStream ini = null;
						ini = new BufferedInputStream(new FileInputStream(DIRECTORIO + s));

						if (ftpClient.storeFile(s, ini)) {
							Log.d("OK", "Se subio la imagen " + cont);
							File file = new File(DIRECTORIO + s);
							file.delete();
							cont++;
						} else {
							Log.d("ServiceDemo", "Error al subir el archivo");
							cancel(true);
						}

					}
					Log.d("ServiceDemo", "Terminado");

				}

				ftpClient.logout();
				ftpClient.disconnect();
				resultCode = Activity.RESULT_OK;
			}

		} catch (Exception e) {
			Log.e("ServiceDemo", "Error", e);
			resultCode = Activity.RESULT_CANCELED;
		}

		return resultCode;

	}

	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected void onCancelled() {
	}
}