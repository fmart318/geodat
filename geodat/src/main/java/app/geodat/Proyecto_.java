package app.geodat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Proyecto_ extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.AppBaseTheme);
		addPreferencesFromResource(R.xml.proyecto);
		
		//setSummary();
		modificarSummary();
	}

	private void modificarSummary() {
		
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		
		for (int i = 1; i < 21 ; i++) {
			String is = String.valueOf(i).toString();
			PreferenceScreen ps = (PreferenceScreen) findPreference("s1"+is);
			ps.setSummary(sp.getString("e1"+is, ""));
			EditTextPreference et = (EditTextPreference) findPreference("e1"+is);
			et.setSummary(sp.getString("e1"+is, ""));
			for (int j = 1; j < 6 ; j++){
				String js = String.valueOf(j).toString();
				EditTextPreference pet = (EditTextPreference) findPreference("p1"+is+js);
				pet.setSummary(sp.getString("p1"+is+js, ""));
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
	
//	private void setSummary(){
//		
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//		SharedPreferences.Editor editor = preferences.edit();
//		
//		String pro1 = preferences.getString("e11", "");
//		String pro2 = preferences.getString("e12", "");
//		String pro3 = preferences.getString("e13", "");
//		String pro4 = preferences.getString("e14", "");
//		String pro5 = preferences.getString("e15", "");
//		String pro6 = preferences.getString("e16", "");
//		String pre11 = preferences.getString("p111", "");
//		String pre12 = preferences.getString("p112", "");
//		String pre13 = preferences.getString("p113", "");
//		String pre14 = preferences.getString("p114", "");
//		String pre15 = preferences.getString("p115", "");
//		String pre21 = preferences.getString("p121", "");
//		String pre22 = preferences.getString("p122", "");
//		String pre23 = preferences.getString("p123", "");
//		String pre24 = preferences.getString("p124", "");
//		String pre25 = preferences.getString("p125", "");
//		String pre31 = preferences.getString("p131", "");
//		String pre32 = preferences.getString("p132", "");
//		String pre33 = preferences.getString("p133", "");
//		String pre34 = preferences.getString("p134", "");
//		String pre35 = preferences.getString("p135", "");
//		String pre41 = preferences.getString("p141", "");
//		String pre42 = preferences.getString("p142", "");
//		String pre43 = preferences.getString("p143", "");
//		String pre44 = preferences.getString("p144", "");
//		String pre45 = preferences.getString("p145", "");
//		String pre51 = preferences.getString("p151", "");
//		String pre52 = preferences.getString("p152", "");
//		String pre53 = preferences.getString("p153", "");
//		String pre54 = preferences.getString("p154", "");
//		String pre55 = preferences.getString("p155", "");
//		String pre61 = preferences.getString("p161", "");
//		String pre62 = preferences.getString("p162", "");
//		String pre63 = preferences.getString("p163", "");
//		String pre64 = preferences.getString("p164", "");
//		String pre65 = preferences.getString("p165", "");
//		
//		if(pro1 == ""){editor.putString("e11", "Local de venta");}
//		if(pro2 == ""){editor.putString("e12", "ULTRA TECH");}
//		if(pro3 == ""){editor.putString("e13", "VITATECH");}
//		if(pro4 == ""){editor.putString("e14", "XING YU");}
//		if(pro5 == ""){editor.putString("e15", "BARRAS PROTEICAS");}
//		if(pro6 == ""){editor.putString("e16", "Problemas");}
//		
//		if(pre11 == ""){editor.putString("p111", "Vende Vitaminas ?");}
//		if(pre12 == ""){editor.putString("p112", "Local Grande ?");}
//		if(pre13 == ""){editor.putString("p113", "Exhibe la Competencia ?");}
//		if(pre14 == ""){editor.putString("p114", "Interesa el negocio? (de 1 a 5)");}
//		if(pre15 == ""){editor.putString("p115", "Imagen del Local? (de 1 a 10)");}
//		
//		if(pre21 == ""){editor.putString("p121", "La competencia Exhibe ?");}
//		if(pre22 == ""){editor.putString("p122", "% mayor a 20 % ?");}
//		if(pre23 == ""){editor.putString("p123", "ULTRA TECH m�s caro ?");}
//		if(pre24 == ""){editor.putString("p124", "Precio Competencia 1");}
//		if(pre25 == ""){editor.putString("p125", "Precio Competencia 2");}
//		
//		if(pre31 == ""){editor.putString("p131", "Bien exhibido ?");}
//		if(pre32 == ""){editor.putString("p132", "Promo y Ofertas de Competencia ?");}
//		if(pre33 == ""){editor.putString("p133", "Más caro que Competencia ?");}
//		if(pre34 == ""){editor.putString("p134", "Precio Competencia 3");}
//		if(pre35 == ""){editor.putString("p135", "Precio competencia 4");}
//		
//		if(pre41 == ""){editor.putString("p141", "Falta stock ?");}
//		if(pre42 == ""){editor.putString("p142", "Precio XING YU competitivo?");}
//		if(pre43 == ""){editor.putString("p143", "Imagen de XiING YU aceptable ?");}
//		if(pre44 == ""){editor.putString("p144", "Precio 5");}
//		if(pre45 == ""){editor.putString("p145", "Precio 6");}
//		
//		if(pre51 == ""){editor.putString("p151", "Bien exhibido ?");}
//		if(pre52 == ""){editor.putString("p152", "Precio pactado ?");}
//		if(pre53 == ""){editor.putString("p153", "Todas las variedades?");}
//		if(pre54 == ""){editor.putString("p154", "Precio Marca 7");}
//		if(pre55 == ""){editor.putString("p155", "Precio 8");}
//		
//		if(pre61 == ""){editor.putString("p161", "Enojado con Lab W Supp. (aclare en observaciones)");}
//		if(pre62 == ""){editor.putString("p162", "No respeta acuerdo pactado ?");}
//		if(pre63 == ""){editor.putString("p163", "No recibi� pedido ?");}
//		if(pre64 == ""){editor.putString("p164", "Limpieza mala 1 a 10");}
//		if(pre65 == ""){editor.putString("p165", "Trato malo 1 a 10");}
//		
//		editor.commit();
//		
//		
//	}
	

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
