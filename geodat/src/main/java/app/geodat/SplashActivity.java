package app.geodat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends FragmentActivity{

	//private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
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
		
		TextView btnsuscribir = (TextView) findViewById(R.id.textView2);

		btnsuscribir.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://www.geodat.app/"));
				startActivity(intent);
		    }
		});

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
			startMainActivity();
		}
	}

	public void login(View view) {

		//viewProgressBar.setVisibility(View.VISIBLE);
		checkAllLogin();
		//viewProgressBar.setVisibility(View.GONE);
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
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_ALL: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						checkAllLogin();
				}
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
		
}
