package app.geodat.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Ftp {
	// Campos-----------------------------------------------------------
	private String ip; // Almacena la dirección del servidor
	private String usuario; // Almacena el nombre de usuario
	private String contrasena; // Almacena la contraseña del usuario
	FTPClient ftpClient; // Crea la conexión con el servidor
	BufferedInputStream buffer; // Crea una buffer de lectura
	File rutaSd; // Almacena la ruta sd
	File rutaCompleta; // Almacena la ruta completa del archivo
	Context context; // Almacena el contexto de la aplicacion
						// util a la hora de mostrar mensajes

	// ------------------------------------------------------------------

	// Constructores-----------------------------------------------------

	/**
	 * Crea una instancia de FTP sin credenciales
	 */
	public Ftp(String ip, Context context) {

		// Inicializaci�n de campos
		this.ip = ip;
		usuario = null;
		contrasena = null;

		ftpClient = null;
		buffer = null;
		rutaSd = null;
		rutaCompleta = null;

		this.context = context;
	}

	/**
	 * Crea una instancia de FTP con credenciales
	 * 
	 * @param usuario
	 *            El nombre de usuario
	 * @param contrasena
	 *            La contrase�a de usuario
	 */
	public Ftp(String ip, String usuario, String contrasena, Context context) {

		// Inicializaci�n de campos
		this.ip = ip;
		this.usuario = usuario;
		this.contrasena = contrasena;

		ftpClient = null;
		buffer = null;
		rutaSd = null;
		rutaCompleta = null;

		this.context = context;

	}

	// ------------------------------------------------------------------

	// Propiedades-------------------------------------------------------

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	// ------------------------------------------------------------------

	/**
	 * Realiza el login en el servidor
	 * 
	 * @param usuario El nombre de usuario
	 * @param contrasena La contraseña de usuario
	 * @return Verdad en caso de haber realizado login correctamente
	 * @throws SocketException
	 * @throws IOException
	 */
	public boolean Login(String usuario, String contrasena)
			throws SocketException, IOException {

		// Almacena los valores en la clase
		this.usuario = usuario;
		this.contrasena = contrasena;

		// Establece conexión con el servidor
		Toast.makeText(context, "Conectando . . .", Toast.LENGTH_SHORT).show();
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ip);
		} catch (Exception e) {

			// Informa al usuario
			Toast.makeText(context, e.toString(),
					Toast.LENGTH_SHORT).show();
			return false; // En caso de que no sea posible la conexion
		}

		// Hace login en el servidor
		if (ftpClient.login(usuario, contrasena)) {

			// Informa al usuario
			Toast.makeText(context, "Login correcto . . .", Toast.LENGTH_SHORT)
					.show();
			return true; // En caso de login correcto
		} else {

			// Informa al usuario
			Toast.makeText(context, "Login incorrecto . . .",
					Toast.LENGTH_SHORT).show();
			return false; // En caso de login incorrecto
		}

	}

	/**
	 * Sube un archivo al servidor FTP si previamente se ha hecho login
	 * correctamente
	 * 
	 * @param nombreArchivo
	 *            Nombre del archivo que se quiere subir
	 * @return Verdad en caso de que se haya subido con éxito
	 * @throws IOException
	 */
	public boolean SubirArchivo(String nombreArchivo) throws IOException {

		ftpClient.enterLocalActiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		// Cambia la carpeta Ftp
		if (ftpClient.changeWorkingDirectory("ftp")) {

			// Informa al usuario
			Toast.makeText(context, "Carpeta ftp cambiada . . .",
					Toast.LENGTH_SHORT).show();

			// Obtiene la dirección de la ruta sd
			Toast.makeText(context, "Ruta SD obtenida . . .",
					Toast.LENGTH_SHORT).show();
			rutaSd = Environment.getExternalStorageDirectory();

			// Obtiene la ruta completa donde se encuentra el archivo
			Toast.makeText(context, "Ruta completa archivo obtenida . . .",
					Toast.LENGTH_SHORT).show();
			rutaCompleta = new File(rutaSd.getAbsolutePath(), nombreArchivo);

			// Crea un buffer hacia el servidor de subida
			buffer = new BufferedInputStream(new FileInputStream(rutaCompleta));

			if (ftpClient.storeFile(nombreArchivo, buffer)) {

				// Informa al usuario
				Toast.makeText(context, "Archivo subido . . .",
						Toast.LENGTH_SHORT).show();

				buffer.close(); // Cierra el bufer
				return true; // Se ha subido con éxito
			} else {

				// Informa al usuario
				Toast.makeText(context, "Imposible subir archivo . . .",
						Toast.LENGTH_SHORT).show();

				buffer.close(); // Cierra el bufer
				return false; // No se ha subido
			}
		} else {

			// Informa al usuario
			Toast.makeText(context, "Carpeta ftp imposible cambiar . . .",
					Toast.LENGTH_SHORT).show();

			return false; // Imposible cambiar de directo en servidor ftp
		}
	}
}
