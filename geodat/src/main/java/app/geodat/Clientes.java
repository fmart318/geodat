package app.geodat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Clientes extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	ListPreference m_updateList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.clientes);

		modificarSummary();

	}

	private void modificarSummary() {

		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

		for (int i = 1; i < 11; i++) {
			String is = String.valueOf(i).toString();
			EditTextPreference et = (EditTextPreference) findPreference("c"
					+ is);
			et.setSummary(sp.getString("c"+is, ""));
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);
		if (pref instanceof PreferenceScreen) {
			EditTextPreference etp = (EditTextPreference) pref;
			pref.setSummary(etp.getText());
			modificarSummary();
		}

	}

}
