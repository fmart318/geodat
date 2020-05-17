package app.geodat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import app.geodat.Util.Constantes;

class UploaderFoto extends AsyncTask<String, Void, Integer> {
	
	public ImageButton bGrabar, bDetener, bReproducir, bBorrar;
	ActionBar actionBar;

	File Fichero = new File(Constantes.fichero);
	String nombre;
	int resultCode;
	MainActivity main = new MainActivity();
	ManipulacionJson mjson;
	String[] archivosJson;
	FileInputStream in = null;

	public UploaderFoto(MainActivity mainActivity) {
		this.main = mainActivity;
		this.mjson = new ManipulacionJson(main);
		this.actionBar = main.getActionBar();
	}

	@Override
	protected Integer doInBackground(String... params) {
		StringBuilder lista = mjson.cargarCsv();
		nombre = lista.toString();
		archivosJson = lista.toString().split("__");
		try {
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect("www.geodatlog.com", 21);
			ftpClient.enterLocalPassiveMode();
			if (!ftpClient.login(Constantes.USER, Constantes.PASS)) {
				cancel(true);
				Log.d("Cancelado", "No se pudo conectar con el servidor");
			} else {
				if (ftpClient.changeWorkingDirectory("json")) {
					Log.d("Cambiado", "Se cambio a la carpeta json");
				} else {
					Log.d("Error", "No Se cambio a la carpeta json");
					cancel(true);
				}
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

				for (String s : archivosJson) {
					nombre = s;
					in = new FileInputStream(Constantes.directorio + nombre);
					if (ftpClient.storeFile(nombre, in)) {
						Log.d("Ok", "Se subio el formulario");
						File file = new File(Constantes.directorio + nombre);
						file.delete();
						if (Fichero.exists()) {
							FileInputStream ina = new FileInputStream(Constantes.fichero);
							String nAudio = nombre.substring(0, nombre.length() - 5);
							if (ftpClient.storeFile(nAudio+".wav", ina)) {
								Log.d("Ok", "Se subio el audio");
								File filea = new File(Constantes.fichero);
								filea.delete();
							}
						}
					} else {
						cancel(true);
						Log.d("Cancelado", "Ya existe el archivo");
					}
				}

				StringBuilder listadoAudio = mjson.cargarListadoAudios();
				if (listadoAudio.length() > 0) {
					String[] lines = listadoAudio.toString().split("__");
					int cont = 1;
					nombre = nombre.substring(0, nombre.length() - 5);
					for (String s : lines) {
						Log.d("Leyendo", "audio " + cont);
						BufferedInputStream ini = null;
						ini = new BufferedInputStream(new FileInputStream(
								Constantes.directorio + s));
						if (ftpClient.storeFile(s, ini)) {
							Log.d("OK", "Se subio el audio " + cont);
							File file = new File(Constantes.directorio + s);
							file.delete();
							cont++;
						} else {
							Log.d("Error", "Error al subir el audio");
							cancel(true);
						}

					}
					Log.d("Termindo", "Audio ");
				}
				StringBuilder listadoCompleto = mjson.cargarListadoGuardado();
				if (listadoCompleto.length() > 0) {
					String[] lines = listadoCompleto.toString().split("__");
					int cont = 1;
					nombre = nombre.substring(0, nombre.length() - 5);
					for (String s : lines) {
						Log.d("Leyendo", "archivo " + cont);
						BufferedInputStream ini = null;
						ini = new BufferedInputStream(new FileInputStream(
								Constantes.directorio + s));
						if (ftpClient.storeFile(s, ini)) {
							Log.d("OK", "Se subio la imagen " + cont);
							File file = new File(Constantes.directorio + s);
							file.delete();
							cont++;
						} else {
							Log.d("Error", "Error al subir la imagen");
							cancel(true);
						}

					}
					Log.d("Termindo", "archivo ");

				}

				ftpClient.logout();
				ftpClient.disconnect();
				resultCode = Activity.RESULT_OK;
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			Log.e("LOGTAG", "Error", e);
			resultCode = Activity.RESULT_CANCELED;
		}
		return resultCode;
	}

	protected void onPreExecute() {
		super.onPreExecute();
		main.mostrarMensaje("Subiendo el archivo, espere por favor...");
	}

	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if (result == Activity.RESULT_OK) {
			main.cargarFotosSlidingDrawer();
			main.notificar("Archivo subido");
			main.quitarMensaje();

			bGrabar = main.findViewById(R.id.Grabar);
			bDetener = main.findViewById(R.id.Detener);
			bReproducir = main.findViewById(R.id.Reproducir);
			bBorrar = main.findViewById(R.id.Borrar);

			bGrabar.setEnabled(true);
			bDetener.setEnabled(false);
			bReproducir.setEnabled(false);
			bBorrar.setEnabled(false);

			main.notificar( "Archivo subido");
		} else {
			main.notificar("Error al subir el archivo, inténtelo más tarde");
			main.quitarMensaje();
			main.notificar("Error en la conexión");
		}
	}

	@Override
	protected void onCancelled() {
		main.notificar("No se pudo conectar al servidor");
		resultCode = Activity.RESULT_CANCELED;
		main.quitarMensaje();
		main.notificar("Error en la conexión");
	}
}