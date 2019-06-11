package app.geodat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Inicio extends Activity{
//	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//	Boolean login = pref.getBoolean("login", false);
//	TextView etCuenta = (TextView) findViewById(R.id.etcuenta);
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inicio);
	}
	
	public void onClickIngresar(View view){
//		if(etCuenta.getText().toString() != ""){
//			login = true;
//		}
//		
//		if(login){
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		this.finish();
//		}else{
//			Toast.makeText(this, "Login incorrecto", Toast.LENGTH_LONG).show();;
////		}
	}

}
