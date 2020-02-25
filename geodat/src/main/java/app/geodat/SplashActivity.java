package app.geodat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import static android.widget.Toast.*;

public class SplashActivity extends FragmentActivity implements ActualizarDelegate {

	private static final int PERMISSION_ALL = 100;
	String[] PERMISSIONS = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.CAMERA
	};
	ProgressBar viewProgressBar;

	@Override
	protected void onCreate(@javax.annotation.Nullable Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_splash);
		
		final ImageView arrows = (ImageView) findViewById(R.id.arrows);
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
		arrows.startAnimation(myFadeInAnimation);

		viewProgressBar = (ProgressBar)findViewById(R.id.progressBar);
	}

	private void startMainActivity() {
		Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(mainIntent);
		SplashActivity.this.finish();
	}

	public void checkAllLogin(){

		if(!hasPermissions(this, PERMISSIONS)){
			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
		}
		else {
			try {
				new Actualizar(this, SplashActivity.this).execute("", ge_timei());
			} catch (Exception ex) {

			}
		}
	}

	public String ge_timei() {

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			makeText( getApplicationContext(), "Imei necesario", LENGTH_SHORT ).show();
		}
		String imei = tm.getDeviceId();

//		return (imei);
		return "860046038880938";
	}

	public void login(View view) {
		checkAllLogin();
	}

	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_ALL: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						checkAllLogin();
				}
			}
		}
	}

	@Override
	public void actualizarExitoso() {
		startMainActivity();
	}

	@Override
	public void actualizarFallo() {
		AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert).create();
		alertDialog.setTitle(R.string.new_user_title);
		alertDialog.setMessage(getString(R.string.new_user_text));
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.new_user_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.register_url)));
						startActivity(intent);
					}
				});
		alertDialog.show();
	}

	@Override
	public void sinConexion() {
		AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert).create();
		alertDialog.setTitle(R.string.no_network_title);
		alertDialog.setMessage(getString(R.string.no_network_text));
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.no_network_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startMainActivity();
					}
				});
		alertDialog.show();
	}

	@Override
	public void notificar(String mensaje) {
		makeText(getApplicationContext(), mensaje, LENGTH_LONG).show();
	}
}
