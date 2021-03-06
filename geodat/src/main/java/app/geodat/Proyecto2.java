package app.geodat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Proyecto2 extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.proyecto2);
		
		modificarSummary();
	}

	private void modificarSummary() {
		
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		
		for (int i = 1; i < 21 ; i++) {
			String is = String.valueOf(i).toString();
			PreferenceScreen ps = (PreferenceScreen) findPreference("s2"+is);
			ps.setSummary(sp.getString("e2"+is, ""));
			EditTextPreference et = (EditTextPreference) findPreference("e2"+is);
			et.setSummary(sp.getString("e2"+is, ""));
			for (int j = 1; j < 6 ; j++){
				String js = String.valueOf(j).toString();
				EditTextPreference pet = (EditTextPreference) findPreference("p2"+is+js);
				pet.setSummary(sp.getString("p2"+is+js, ""));
			}
		}
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		modificarSummary();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
	

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		Preference pref = findPreference(key);
		if (pref instanceof PreferenceScreen) {
			EditTextPreference etp = (EditTextPreference) pref;
			pref.setSummary(etp.getText());
			modificarSummary();
		}

	}
}
