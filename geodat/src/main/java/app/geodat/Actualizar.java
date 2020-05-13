package app.geodat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class Actualizar extends AsyncTask<String, Object, String> {

	private static final String OK = "OK";
	private static final String NO_PROYECTO = "NO_PROYECTO";
	private static final String NO_AGENTE = "NO_AGENTE";
	private static final String NO_IMEI = "NO_IMEI";

	private static final String MSG_OK = "Los proyectos fueron actualizados correctamente";
	private static final String MSG_EMPTY_JSON = "No se recibió información del servidor";
	private static final String MSG_NO_PROYECTO = "El agente no se encuentra vinculado a ningún proyecto";
	private static final String MSG_NO_AGENTE = "El agente no se encontró en el sistema";
	private static final String MSG_NO_IMEI = "No se informó el Imei del agente";
	private static final String MSG_NO_CONECTION = "No se pudo conectar al servidor";
	private static final String MSG_PROCESS_ERROR = "Se produjo un error al intentar actualizar proyectos";

	private static final String TAG = "Actualizar";
	ActualizarDelegate delegate;
	Context context;
	private ProgressDialog pDialog;
	String ip = "";
	String userId = "";
	String url = "";
	String msg = "Error";

	public Actualizar(ActualizarDelegate delegate, Context context) {
		this.delegate = delegate;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context); // <<-- Couldnt Recognise
		pDialog.setMessage("Actualizando proyectos ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == MSG_OK) {
			delegate.actualizarExitoso();
		} else if (result == MSG_NO_CONECTION) {
			delegate.sinConexion();
		}else {
			delegate.actualizarFallo();
		}
		pDialog.dismiss();
		delegate.notificar(msg);
	}

	@Override
	protected String doInBackground(String... params) {
		System.out.println("doInBackground");
		this.ip = params[0];
		this.userId = params[1];
		this.url = "https://geodatlog.com/" + "programas/actualizar/actualizar.php?IMEI=" + userId;

		Log.d(TAG, "Url: " + url);
		String result = readJSONFeed(url);
		if (!result.isEmpty()){
			saveJSONResult(result);
		}
		return msg;
	}

	public String readJSONFeed(Object URL) {
		System.out.println("readJSONFeed");
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet(URL.toString());
		try {
			int hardTimeout = 5; // seconds
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					httpGet.abort();
					msg = MSG_NO_CONECTION;
				}
			};
			new Timer(true).schedule(task, hardTimeout * 1000);

			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				System.out.println(stringBuilder.toString());
				inputStream.close();

			} else {
				msg = MSG_NO_CONECTION;
			}
		} catch (Exception e) {
			msg = MSG_NO_CONECTION;
		}
		return stringBuilder.toString();
	}

	private void saveJSONResult(String resultj) {
		System.out.println("onPostExecute");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this.context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.apply();

		try {
			JSONObject jsonObject = new JSONObject(resultj);

			String result = (String) ((jsonObject.isNull("result")) ? ""
					: jsonObject.getString("result"));
			String multi = (String) ((jsonObject.isNull("multi")) ? ""
					: jsonObject.getString("multi"));
			boolean multib = multi.equalsIgnoreCase("TRUE") ? true : false;

			if (result.isEmpty()) {
				msg = MSG_EMPTY_JSON;
				
			} else if (result.equalsIgnoreCase(NO_PROYECTO)) {
				msg = MSG_NO_PROYECTO;

			} else if (result.equalsIgnoreCase(NO_AGENTE)) {
				msg = MSG_NO_AGENTE;
				
			} else if (result.equalsIgnoreCase(NO_IMEI)) {
				msg = MSG_NO_IMEI;
				
			} else if (result.equalsIgnoreCase(OK)) {
				int i = 0;
				int cuenta = 0;
				Map<String, String> cuentas = new HashMap();

				while (!jsonObject.isNull("record" + i)) {

					JSONObject record = jsonObject
							.getJSONObject("record" + i++);
					String id_cuenta = (String) ((record.isNull("id_cuenta")) ? ""
							: record.getString("id_cuenta"));
					String nombre_compania = (String) ((record
							.isNull("nombre_compania")) ? "" : record
							.getString("nombre_compania"));
					String sub_id = (String) ((record.isNull("sub_id")) ? ""
							: record.getString("sub_id"));
					String nombre = (String) ((record.isNull("nombre")) ? ""
							: record.getString("nombre"));
					String pregunta1 = (String) ((record.isNull("pregunta1")) ? ""
							: record.getString("pregunta1"));
					String pregunta2 = (String) ((record.isNull("pregunta2")) ? ""
							: record.getString("pregunta2"));
					String pregunta3 = (String) ((record.isNull("pregunta3")) ? ""
							: record.getString("pregunta3"));
					String pregunta4 = (String) ((record.isNull("pregunta4")) ? ""
							: record.getString("pregunta4"));
					String pregunta5 = (String) ((record.isNull("pregunta5")) ? ""
							: record.getString("pregunta5"));

					if (!cuentas.containsKey(id_cuenta)) {
						cuentas.put(id_cuenta, nombre_compania);
						cuenta++;
						editor.putString("c" + cuenta, nombre_compania);
						editor.putString("id" + cuenta, id_cuenta);
					}

					editor.putString("e" + cuenta + sub_id, nombre);
					editor.putString("p" + cuenta + sub_id + 1, pregunta1);
					editor.putString("p" + cuenta + sub_id + 2, pregunta2);
					editor.putString("p" + cuenta + sub_id + 3, pregunta3);
					editor.putString("p" + cuenta + sub_id + 4, pregunta4);
					editor.putString("p" + cuenta + sub_id + 5, pregunta5);

				}
				editor.putBoolean("activar", multib);
				editor.putString("cantidad", String.valueOf(cuenta));
				editor.commit();
				msg = MSG_OK;
			}

		} catch (Exception e) {
			msg = MSG_PROCESS_ERROR + " " + e.toString();
		}

	}

}
