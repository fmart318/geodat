package app.geodat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import app.geodat.Util.Constantes;
import app.geodat.Util.Formulario;

public class ManipulacionJson {

	Formulario fjson = new Formulario();
	MainActivity main = new MainActivity();

	public ManipulacionJson(MainActivity main, Formulario formulario) {

		this.fjson = formulario;
		this.main = main;
	}

	public ManipulacionJson(MainActivity main) {
		this.main = main;
	}
	
	public ManipulacionJson() {

	}

	public void procesarGuardarArchivo() {

		StringBuilder listadoCsv = cargarCsv();

		if (listadoCsv.length() > 0) {

			String[] lines = listadoCsv.toString().split("__");
			for (String s : lines) {
				File archivoObsoleto = new File(Constantes.directorio + s);
				//archivoObsoleto.delete();
			}
		}

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formatoFecha = f.format(new Date());

		String nombre = main.ge_timei() + "_" + formatoFecha;

		// Objeto JSON

		if (fjson.getIdcuenta() != "NULL") {

			JSONObject json = new JSONObject();
			try {

				json.put("Imei", Double.parseDouble(main.ge_timei()));
				// json.put("Cliente", s1);
				json.put("Proyecto", fjson.getProyectos());
				// json.put("Certificador", E2a);
				json.put("Timestamp", fjson.getFecha());
				json.put("Referencia", fjson.getIdPunto());
				json.put("Referencia2", fjson.getReferencia());

				JSONArray localizacion = new JSONArray();
				String[] loc = fjson.getLocacion().split(";");
				localizacion.put(Double.parseDouble(loc[0]));
				localizacion.put(Double.parseDouble(loc[1]));
				json.put("Localizacion", localizacion);

				JSONArray adjuntos = new JSONArray();
				StringBuilder listadoCompleto = cargarListado();
				if (listadoCompleto.length() > 0) {
					String[] lines = listadoCompleto.toString().split("__");
					int i = 1;
					for (String s : lines) {
						String newName = nombre + "_" + i + ".jpg" ;
						File file = new File(Constantes.directorio + s);
					    File to = new File(Constantes.directorio, newName);
					    if (file.exists()) { file.renameTo(to);}
						adjuntos.put(newName);
						i++;
					}
				}

				String newName = nombre + ".wav" ;
				File file = new File(Constantes.fichero);
			    File to = new File(Constantes.directorio, newName);
			    if (file.exists()) { 
			    	file.renameTo(to);
			    	adjuntos.put(newName);
			    }
				
				json.put("Adjuntos", adjuntos);

				JSONObject f1 = new JSONObject();
				f1.put("Pregunta_1", fjson.isPregunta1());
				JSONObject f2 = new JSONObject();
				f2.put("Pregunta_2", fjson.isPregunta2());
				JSONObject f3 = new JSONObject();
				f3.put("Pregunta_3", fjson.isPregunta3());
				JSONObject f4 = new JSONObject();
				f4.put("Pregunta_4", fjson.getPregunta4());
				JSONObject f5 = new JSONObject();
				f5.put("Pregunta_5", fjson.getPregunta5());
				JSONObject f6 = new JSONObject();
				f6.put("Comentarios", fjson.getComentario());
				JSONObject f7 = new JSONObject();
				f7.put("Codigo", fjson.getCodigo());

				JSONArray formulario = new JSONArray();
				formulario.put(f1);
				formulario.put(f2);
				formulario.put(f3);
				formulario.put(f4);
				formulario.put(f5);
				formulario.put(f6);
				formulario.put(f7);
				json.put("Formulario", formulario);

				json.put("Multi", fjson.isMulti());
				json.put("Cuenta", Integer.parseInt(fjson.getIdcuenta()));
				

			} catch (JSONException e) {

				e.printStackTrace();
			}

			guardarArchivo(json, fjson.getLocacion(), nombre);

		} else {
			main.notificar("No ingresó el Id de la cuenta actual");
		}
	}

	public void procesarEnviarArchivo() {

		String dirNombre ="";
		StringBuilder lista = cargarCsv();
		
		if (lista.length() > 0) {
			String[] listaJson = lista.toString().split("__");
			int archivosJson = listaJson.length;
			
			//Imagenes
			int archivos = 0;
			StringBuilder listadoCompleto = cargarListadoGuardado();
			if (!listadoCompleto.toString().equals(""))
			{
				String[] images = listadoCompleto.toString().split("__");
				archivos = images.length;
			}
			
			//Audios
			int audioCant = 0;
			StringBuilder listadoAudio = cargarListadoAudios();
			if(!listadoAudio.toString().equals(""))
			{
				String[] audios = listadoAudio.toString().split("__");
				audioCant = audios.length;
			}
	
			String msg = "";

			if (archivosJson == 1 && audioCant == 0 && archivos == 0){
				try {
					subirArchivo();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}else if (archivosJson > 0) {
				msg = "Se enviaran " + archivosJson + " formulario/s";
				
				if (archivos > 0 && audioCant > 0){
					msg += ", " + archivos + " imagen/es, y " + audioCant + " audio/s";
				}else if(archivos > 0){
					msg += " y " + archivos + " imagen/es ";
				}else if(audioCant > 0){
					msg += " y " + audioCant + " audio/s ";
				}
				
				mostrarMsgConfirmacion(msg);
			} else {
				main.notificar("Debe guardar el formulario antes de enviarlo");
			}
		} else {
			main.notificar("Debe guardar el formulario antes de enviarlo");
		}
	}

	public StringBuilder cargarCsv() {

		String[] listado = ListadoArchivos
				.getCsvFiles(Constantes.directorio);

		StringBuilder listadoCompleto = new StringBuilder();

		if (listado.length > 0) {
			for (int i = 0; i < listado.length; i++) {

				listadoCompleto.append(listado[i].toString());
				listadoCompleto.append("__");
			}
		}
		return listadoCompleto;

	}

	public StringBuilder cargarListado() {
		
		File directory = new File(Constantes.directorio);
		directory.mkdirs();

		String[] listado = ListadoArchivos
				.getImages(directory.toString());
		StringBuilder listadoCompleto = new StringBuilder();

		if (listado.length > 0) {

			for (int i = 0; i < listado.length; i++) {
				listadoCompleto.append(listado[i].toString());
				listadoCompleto.append("__");
			}
		}
		return listadoCompleto;
	}
	
	public StringBuilder cargarListadoGuardado() {
		
		File directory = new File(Constantes.directorio);
		directory.mkdirs();

		String[] listado = ListadoArchivos
				.getSavedImages(directory.toString());
		StringBuilder listadoCompleto = new StringBuilder();

		if (listado.length > 0) {

			for (int i = 0; i < listado.length; i++) {
				listadoCompleto.append(listado[i].toString());
				listadoCompleto.append("__");
			}
		}
		return listadoCompleto;
	}
	
	public StringBuilder cargarListadoAudios() {
		
		File directory = new File(Constantes.directorio);
		directory.mkdirs();

		String[] listado = ListadoArchivos
				.getSavedAudios(directory.toString());
		StringBuilder listadoCompleto = new StringBuilder();

		if (listado.length > 0) {

			for (int i = 0; i < listado.length; i++) {
				listadoCompleto.append(listado[i].toString());
				listadoCompleto.append("__");
			}
		}
		return listadoCompleto;
	}

	public void guardarArchivo(JSONObject json, String coor, String nombre) {

		nombre = nombre + ".json";
		File sdCard, directory, file;

		try {

			// validamos si se encuentra montada nuestra memoria externa
			if (Environment.getExternalStorageState().equals("mounted")) {

				// Obtenemos el directorio de la memoria externa
				sdCard = Environment.getExternalStorageDirectory();

				// Clase que permite grabar texto en un archivo
				FileOutputStream fout = null;

				try {

					directory = new File(Constantes.directorio);
					directory.mkdirs();

					// creamos el archivo en el nuevo directorio creado
					file = new File(directory, nombre);

					fout = new FileOutputStream(file);

					// Convierte un stream de caracteres en un stream de
					// bytes
					OutputStreamWriter ows = new OutputStreamWriter(fout);

					ows.write(json.toString());

					ows.flush(); // Volca lo que hay en el buffer al archivo
					ows.close(); // Cierra el archivo de texto

					main.notificar("El archivo se ha almacenado");

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception ex) {
			Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}

	}
	
	private void subirArchivo() throws SocketException,
	UnknownHostException, IOException {
		UploaderFoto uf;
		uf = new UploaderFoto(main);
		uf.execute();
	
	}
	
	private void mostrarMsgConfirmacion(String msg){
		// Mostrar un mensaje de confirmaci�n
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				main);
		alertDialog.setMessage(msg);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Aceptar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						try {
							
							subirArchivo();
							//borrarArchivosObsoletos();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});
		alertDialog.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						main.notificar("Elimine los archivos que no desee enviar"
								+ " y vuelva a intentarlo");
					}
				});
		alertDialog.show();
	}
	
	public int borrarArchivosObsoletos() {
		
		File directory = new File(Constantes.directorio);
		directory.mkdirs();

		String[] listado = ListadoArchivos
				.getFiles(directory.toString());
		int cantFiles = 0;
		if (listado.length > 0) {

			for (int i = 0; i < listado.length; i++) {
				
				File file = new File(Constantes.directorio + listado[i]);
				file.delete();
				cantFiles++;
			}
		}
		return cantFiles;
	}

}
