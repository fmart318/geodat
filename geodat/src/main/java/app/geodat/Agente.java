package app.geodat;



import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Agente extends PreferenceActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.agente);
	}
}
