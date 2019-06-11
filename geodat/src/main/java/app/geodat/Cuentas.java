package app.geodat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Cuentas extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	ListPreference m_updateList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.AppBaseTheme);
		addPreferencesFromResource(R.xml.cuentamulti);

		modificarSummary();

	}

	private void modificarSummary() {

		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

		for (int i = 1; i < 6; i++) {
			String is = String.valueOf(i).toString();
			EditTextPreference et = (EditTextPreference) findPreference("c"+ is);
			et.setSummary(sp.getString("c"+is, "Ingrese nombre de la cuenta"));
			EditTextPreference id = (EditTextPreference) findPreference("id"+ is);
			id.setSummary(sp.getString("id"+is, "Si trabaja para más de una cuenta"
					+ "es necesario ingresar el ID"));
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
