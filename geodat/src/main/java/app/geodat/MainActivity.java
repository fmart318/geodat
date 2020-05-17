package app.geodat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import app.geodat.R.drawable;
import app.geodat.Util.Constantes;
import app.geodat.Util.Formulario;

public class MainActivity extends AppCompatActivity implements LocationListener, ActualizarDelegate {

    private static final String LOG_TAG = "Grabadora";
    public ImageButton bGrabar, bDetener, bReproducir, bBorrar;

    private String foto;
    File directory;
    GoogleMap googleMap;

    AudioRecorder au = new AudioRecorder(Constantes.fichero);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (android.os.Build.VERSION.SDK_INT > 23) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(drawable.ic_header);
        }

        setContentView(R.layout.fragment_main);

        TabHost tabs = findViewById(R.id.tabhost);

        tabs.setup();
        TabHost.TabSpec spec1 = tabs.newTabSpec("Pestaña1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getTabIndicator(android.R.drawable.ic_menu_info_details));
        tabs.addTab(spec1);

        tabs.setup();
        TabHost.TabSpec spec3 = tabs.newTabSpec("Pestaña3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator(getTabIndicator(android.R.drawable.ic_menu_mylocation));
        tabs.addTab(spec3);

        googlePlayActivado();

        EditText editcalendar = findViewById(R.id.editText6a);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        editcalendar.setText(f.format(new Date()));

        SimpleDateFormat f2 = new SimpleDateFormat("dd-MM-yyyy");
        String formatoFecha = f2.format(new Date());
        EditText et5a = findViewById(R.id.EditText5a);
        et5a.setText(formatoFecha);

        cargarFotosSlidingDrawer();

        bGrabar = findViewById(R.id.Grabar);
        bDetener = findViewById(R.id.Detener);
        bReproducir = findViewById(R.id.Reproducir);
        bBorrar = findViewById(R.id.Borrar);
        bDetener.setEnabled(false);
        bReproducir.setEnabled(false);
        bBorrar.setEnabled(false);

        Spinner sp2 = findViewById(R.id.spinner2);
        sp2.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                Spinner sp1 = findViewById(R.id.Spinner1);
                int cuenta = sp1.getSelectedItemPosition() + 1;
                actualizarPreguntas(position + 1, cuenta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Spinner sp1 = findViewById(R.id.Spinner1);
        sp1.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                Spinner sp2 = findViewById(R.id.spinner2);
                int proyecto = sp2.getSelectedItemPosition() + 1;
                actualizarProyectos(position + 1);
                actualizarPreguntas(proyecto, position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        StringBuilder listadoCsv = cargarCsv();
        if (listadoCsv.length() > 0) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MainActivity.this);
            alertDialog
                    .setMessage("Se encontraron archivos en el dispositivo,"
                            + "¿desea enviarlo? De lo contrario se eliminar?");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Enviar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            try {
                                onClickEnviar();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
            alertDialog.setNegativeButton("Eliminar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            ManipulacionJson mj = new ManipulacionJson();
                            int cont = mj.borrarArchivosObsoletos();
                            notificar("El han eliminado " + cont + " archivos");
                        }
                    });
            alertDialog.show();

        }

//		//Boton seg
//		SharedPreferences prefere = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//		Boolean seg = prefere.getBoolean("seguimiento", false);
//		ImageButton btnSeg = (ImageButton) findViewById(R.id.imageButton2);
//		if (seg) {
//			btnSeg.setBackgroundResource(drawable.boton);
//		} else {
//			btnSeg.setBackgroundResource(drawable.botoninv);
//
//		}

        Toolbar toolbar = findViewById(R.id.action_bar);
        TextView headerTitle = (TextView) toolbar.getChildAt(0);
        headerTitle.setTextColor(getResources().getColor(R.color.white));
        Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.header);
        headerTitle.setTypeface(face);
    }

    @Override
    public void actualizarExitoso() {
        actualizarPantalla();
    }

    @Override
    public void actualizarFallo() {
        actualizarPantalla();
    }

    @Override
    public void sinConexion() {
        actualizarPantalla();
    }

    public void notificar(String cadena) {
        Toast.makeText(getApplicationContext(), cadena, Toast.LENGTH_LONG).show();
    }

    public void mostrarMensaje(String msg) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        RelativeLayout layout_msg = findViewById(R.id.layout_msg);
        TextView text_msg = findViewById(R.id.text_msg);
        layout_msg.setVisibility(View.VISIBLE);
        text_msg.setText(msg);
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void quitarMensaje() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        RelativeLayout layout_msg = findViewById(R.id.layout_msg);
        layout_msg.setVisibility(View.GONE);
        if (actionBar != null) {
            actionBar.show();
        }
    }

    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.btnguardar) {
            onClickGuardar();
            return true;
        } else if (itemId == R.id.btnenviar) {
            onClickEnviar();
            return true;
        } else if (itemId == R.id.btnqr) {
            onClickLecturaCod(new View(getApplicationContext()));
            return true;
        } else if (itemId == R.id.btnlogout) {
            onClickLogout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void actualizar() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        Boolean multi = pref.getBoolean("activar", false);
        int cant = Integer.parseInt(pref.getString("cantidad", "5"));

        TextView txtCuenta = findViewById(R.id.TextView02);
        TableRow tableRowCuenta = findViewById(R.id.TableRow4a);

        Spinner sp1 = findViewById(R.id.Spinner1);
        Spinner sp2 = findViewById(R.id.spinner2);
        if (multi) {
            txtCuenta.setVisibility(TextView.VISIBLE);
            tableRowCuenta.setVisibility(View.VISIBLE);
            sp1.setVisibility(TextView.VISIBLE);
            int cuentaselect = sp1.getSelectedItemPosition() + 1;

            ArrayAdapter<String> aa_cuenta = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item);

            for (int x = 1; x <= cant; x++) {
                aa_cuenta.add(x + "  " + pref.getString("c" + x, ""));
            }
            aa_cuenta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(aa_cuenta);
            sp1.setSelection(cuentaselect - 1);
            actualizarProyectos(cuentaselect);
            actualizarPreguntas(1, cuentaselect);

        } else {
            int proyectoselect = sp2.getSelectedItemPosition() + 1;
            txtCuenta.setVisibility(TextView.GONE);
            sp1.setVisibility(TextView.GONE);
            tableRowCuenta.setVisibility(View.GONE);
            actualizarProyectos(1);
            actualizarPreguntas(proyectoselect, 1);
        }
    }

    private void onClickMapa() {
        String userId = getUserId();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://geodatlog.com/" + "/programas/agente/mapa.php?IMEI=" + userId));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setMessage("¿Desea salir de GEODAT?")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, salir()).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private DialogInterface.OnClickListener salir() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarPantalla();
        startService(new Intent(this, ServiceSeg.class));
    }

    public void actualizarPantalla() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        Boolean multi = pref.getBoolean("activar", false);
        int cant = Integer.parseInt(pref.getString("cantidad", "5"));

        TextView txtCuenta = findViewById(R.id.TextView02);
        TableRow tableRowCuenta = findViewById(R.id.TableRow4a);

        Spinner sp1 = findViewById(R.id.Spinner1);
        Spinner sp2 = findViewById(R.id.spinner2);
        if (multi) {
            txtCuenta.setVisibility(TextView.VISIBLE);
            tableRowCuenta.setVisibility(View.VISIBLE);
            sp1.setVisibility(TextView.VISIBLE);
            int cuentaselect = sp1.getSelectedItemPosition() + 1;

            ArrayAdapter<String> aa_cuenta = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item);

            for (int x = 1; x <= cant; x++) {
                aa_cuenta.add(x + "  " + pref.getString("c" + x, ""));
            }
            aa_cuenta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(aa_cuenta);
            sp1.setSelection(cuentaselect - 1);
            actualizarProyectos(cuentaselect);
            actualizarPreguntas(1, cuentaselect);
        } else {
            int proyectoselect = sp2.getSelectedItemPosition() + 1;
            txtCuenta.setVisibility(TextView.GONE);
            sp1.setVisibility(TextView.GONE);
            tableRowCuenta.setVisibility(View.GONE);
            actualizarProyectos(1);
            actualizarPreguntas(proyectoselect, 1);
        }
    }

    public void actualizarProyectos(int cuenta) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String i = Integer.toString(cuenta);
        Spinner sp2 = (Spinner) findViewById(R.id.spinner2);
        int proyectoselect = sp2.getSelectedItemPosition() + 1;

        ArrayAdapter<String> aa_proyecto = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);

        for (int x = 1; x < 21; x++) {
            aa_proyecto.add(x + "  " + pref.getString("e" + i + x, ""));
        }
        aa_proyecto
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(aa_proyecto);
        sp2.setSelection(proyectoselect - 1);
    }

    public void actualizarPreguntas(int proyectoselect, int cuenta) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String i = Integer.toString(cuenta);

        TextView pre1 = findViewById(R.id.textView1b);
        TextView pre2 = findViewById(R.id.textView2b);
        TextView pre3 = findViewById(R.id.textView3b);
        TextView pre4 = findViewById(R.id.textView4b);
        TextView pre5 = findViewById(R.id.textView5b);

        switch (proyectoselect) {
            case 1:
                pre1.setText(pref.getString("p" + i + "11", ""));
                pre2.setText(pref.getString("p" + i + "12", ""));
                pre3.setText(pref.getString("p" + i + "13", ""));
                pre4.setText(pref.getString("p" + i + "14", ""));
                pre5.setText(pref.getString("p" + i + "15", ""));
                break;
            case 2:
                pre1.setText(pref.getString("p" + i + "21", ""));
                pre2.setText(pref.getString("p" + i + "22", ""));
                pre3.setText(pref.getString("p" + i + "23", ""));
                pre4.setText(pref.getString("p" + i + "24", ""));
                pre5.setText(pref.getString("p" + i + "25", ""));
                break;
            case 3:
                pre1.setText(pref.getString("p" + i + "31", ""));
                pre2.setText(pref.getString("p" + i + "32", ""));
                pre3.setText(pref.getString("p" + i + "33", ""));
                pre4.setText(pref.getString("p" + i + "34", ""));
                pre5.setText(pref.getString("p" + i + "35", ""));
                break;
            case 4:
                pre1.setText(pref.getString("p" + i + "41", ""));
                pre2.setText(pref.getString("p" + i + "42", ""));
                pre3.setText(pref.getString("p" + i + "43", ""));
                pre4.setText(pref.getString("p" + i + "44", ""));
                pre5.setText(pref.getString("p" + i + "45", ""));
                break;
            case 5:
                pre1.setText(pref.getString("p" + i + "51", ""));
                pre2.setText(pref.getString("p" + i + "52", ""));
                pre3.setText(pref.getString("p" + i + "53", ""));
                pre4.setText(pref.getString("p" + i + "54", ""));
                pre5.setText(pref.getString("p" + i + "55", ""));
                break;
            case 6:
                pre1.setText(pref.getString("p" + i + "61", ""));
                pre2.setText(pref.getString("p" + i + "62", ""));
                pre3.setText(pref.getString("p" + i + "63", ""));
                pre4.setText(pref.getString("p" + i + "64", ""));
                pre5.setText(pref.getString("p" + i + "65", ""));
                break;
            case 7:
                pre1.setText(pref.getString("p" + i + "71", ""));
                pre2.setText(pref.getString("p" + i + "72", ""));
                pre3.setText(pref.getString("p" + i + "73", ""));
                pre4.setText(pref.getString("p" + i + "74", ""));
                pre5.setText(pref.getString("p" + i + "75", ""));
                break;
            case 8:
                pre1.setText(pref.getString("p" + i + "81", ""));
                pre2.setText(pref.getString("p" + i + "82", ""));
                pre3.setText(pref.getString("p" + i + "83", ""));
                pre4.setText(pref.getString("p" + i + "84", ""));
                pre5.setText(pref.getString("p" + i + "85", ""));
                break;
            case 9:
                pre1.setText(pref.getString("p" + i + "91", ""));
                pre2.setText(pref.getString("p" + i + "92", ""));
                pre3.setText(pref.getString("p" + i + "93", ""));
                pre4.setText(pref.getString("p" + i + "94", ""));
                pre5.setText(pref.getString("p" + i + "95", ""));
                break;
            case 10:
                pre1.setText(pref.getString("p" + i + "101", ""));
                pre2.setText(pref.getString("p" + i + "102", ""));
                pre3.setText(pref.getString("p" + i + "103", ""));
                pre4.setText(pref.getString("p" + i + "104", ""));
                pre5.setText(pref.getString("p" + i + "105", ""));
                break;
            case 11:
                pre1.setText(pref.getString("p" + i + "111", ""));
                pre2.setText(pref.getString("p" + i + "112", ""));
                pre3.setText(pref.getString("p" + i + "113", ""));
                pre4.setText(pref.getString("p" + i + "114", ""));
                pre5.setText(pref.getString("p" + i + "115", ""));
                break;
            case 12:
                pre1.setText(pref.getString("p" + i + "121", ""));
                pre2.setText(pref.getString("p" + i + "122", ""));
                pre3.setText(pref.getString("p" + i + "123", ""));
                pre4.setText(pref.getString("p" + i + "124", ""));
                pre5.setText(pref.getString("p" + i + "125", ""));
                break;
            case 13:
                pre1.setText(pref.getString("p" + i + "131", ""));
                pre2.setText(pref.getString("p" + i + "132", ""));
                pre3.setText(pref.getString("p" + i + "133", ""));
                pre4.setText(pref.getString("p" + i + "134", ""));
                pre5.setText(pref.getString("p" + i + "135", ""));
                break;
            case 14:
                pre1.setText(pref.getString("p" + i + "141", ""));
                pre2.setText(pref.getString("p" + i + "142", ""));
                pre3.setText(pref.getString("p" + i + "143", ""));
                pre4.setText(pref.getString("p" + i + "144", ""));
                pre5.setText(pref.getString("p" + i + "145", ""));
                break;
            case 15:
                pre1.setText(pref.getString("p" + i + "151", ""));
                pre2.setText(pref.getString("p" + i + "152", ""));
                pre3.setText(pref.getString("p" + i + "153", ""));
                pre4.setText(pref.getString("p" + i + "154", ""));
                pre5.setText(pref.getString("p" + i + "155", ""));
                break;
            case 16:
                pre1.setText(pref.getString("p" + i + "161", ""));
                pre2.setText(pref.getString("p" + i + "162", ""));
                pre3.setText(pref.getString("p" + i + "163", ""));
                pre4.setText(pref.getString("p" + i + "164", ""));
                pre5.setText(pref.getString("p" + i + "165", ""));
                break;
            case 17:
                pre1.setText(pref.getString("p" + i + "171", ""));
                pre2.setText(pref.getString("p" + i + "172", ""));
                pre3.setText(pref.getString("p" + i + "173", ""));
                pre4.setText(pref.getString("p" + i + "174", ""));
                pre5.setText(pref.getString("p" + i + "175", ""));
                break;
            case 18:
                pre1.setText(pref.getString("p" + i + "181", ""));
                pre2.setText(pref.getString("p" + i + "182", ""));
                pre3.setText(pref.getString("p" + i + "183", ""));
                pre4.setText(pref.getString("p" + i + "184", ""));
                pre5.setText(pref.getString("p" + i + "185", ""));
                break;
            case 19:
                pre1.setText(pref.getString("p" + i + "191", ""));
                pre2.setText(pref.getString("p" + i + "192", ""));
                pre3.setText(pref.getString("p" + i + "193", ""));
                pre4.setText(pref.getString("p" + i + "194", ""));
                pre5.setText(pref.getString("p" + i + "195", ""));
                break;
            case 20:
                pre1.setText(pref.getString("p" + i + "201", ""));
                pre2.setText(pref.getString("p" + i + "202", ""));
                pre3.setText(pref.getString("p" + i + "203", ""));
                pre4.setText(pref.getString("p" + i + "204", ""));
                pre5.setText(pref.getString("p" + i + "205", ""));
                break;
            default:
                break;
        }
    }

    public void onClickGuardar() {
        actualizarFecha();

        Spinner spinner1 = findViewById(R.id.Spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);
        TextView ed6a = findViewById(R.id.editText6a);
        TextView ed7a = findViewById(R.id.editText7a);
        TextView ed9a = findViewById(R.id.editText9a);
        TextView ed10a = findViewById(R.id.editText10a);

        RadioButton rb1b = findViewById(R.id.radio1b);
        RadioButton rb2b = findViewById(R.id.radio2b);
        RadioButton rb3b = findViewById(R.id.radio3b);
        TextView et4b = findViewById(R.id.editText4b);
        TextView et5b = findViewById(R.id.editText5b);

        TextView et6b = findViewById(R.id.editText6b);
        TextView tvCL = findViewById(R.id.tvCodigoLeido);

        int s1 = spinner1.getSelectedItemPosition() + 1;
        int s2 = spinner2.getSelectedItemPosition() + 1;
        String e6a = ed6a.getText().toString();
        String e7a = ed7a.getText().toString();
        String e9a = ed9a.getText().toString();
        String e10a = ed10a.getText().toString();

        Boolean r1b = rb1b.isChecked();
        Boolean r2b = rb2b.isChecked();
        Boolean r3b = rb3b.isChecked();

        String e4b = (et4b.getText().toString().equals("")) ? "0" : et4b.getText().toString();
        String e5b = (et5b.getText().toString().equals("")) ? "0" : et5b.getText().toString();
        String e6b = et6b.getText().toString();

        String tcl = tvCL.getText().toString();

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Boolean multi = pref.getBoolean("activar", false);
        String cuenta = (multi) ? pref.getString("id" + Integer.toString(s1), "NULL") : "0";

        Formulario formulario = new Formulario(s1, s2, e6a, e7a, e9a, e10a, r1b, r2b, r3b, e4b, e5b, e6b, tcl, multi, cuenta);
        ManipulacionJson manipulacionJson = new ManipulacionJson(this, formulario);
        manipulacionJson.procesarGuardarArchivo();

        cleanFields();
    }

    public void cleanFields() {

        TextView ed9a = findViewById(R.id.editText9a);
        TextView ed10a = findViewById(R.id.editText10a);

        RadioButton rb1b = findViewById(R.id.radio1b);
        RadioButton rb2b = findViewById(R.id.radio2b);
        RadioButton rb3b = findViewById(R.id.radio3b);
        TextView et4b = findViewById(R.id.editText4b);
        TextView et5b = findViewById(R.id.editText5b);

        TextView et6b = findViewById(R.id.editText6b);
        TextView codLeido = findViewById(R.id.tvCodigoLeido);

        ed9a.setText("");
        ed10a.setText("");
        rb1b.setChecked(true);
        rb2b.setChecked(true);
        rb3b.setChecked(true);
        et4b.setText("");
        et5b.setText("");
        et6b.setText("");
        codLeido.setText("");

        bGrabar = findViewById(R.id.Grabar);
        bDetener = findViewById(R.id.Detener);
        bReproducir = findViewById(R.id.Reproducir);
        bBorrar = findViewById(R.id.Borrar);

        bGrabar.setEnabled(true);
        bDetener.setEnabled(false);
        bReproducir.setEnabled(false);
        bBorrar.setEnabled(false);
    }

//	public void onClickSeg(View view) {
//		// Obtener Preferencias
//		SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//		Boolean seg = prefer.getBoolean("seguimiento", false);
//		SharedPreferences.Editor editor = prefer.edit();
//		ImageButton btnSeg = (ImageButton) findViewById(R.id.imageButton2);
//		if (seg) {
//
//			startService(new Intent(this, ServiceSeg.class));
//			notificar("Seguimiento Activado");
//			editor.putBoolean("seguimiento", false);
////			btnSeg.setBackgroundResource(drawable.botoninv);
//		} else {
//
//			stopService(new Intent(this, ServiceSeg.class));
//			notificar("Seguimiento Desactivado");
//			editor.putBoolean("seguimiento", true);
//			btnSeg.setBackgroundResource(drawable.boton);
//
//		}
//		editor.commit();
//	}

    private void onClickLogout() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.logout_confirmacion)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, logout()).show();
    }

    private DialogInterface.OnClickListener logout() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService(new Intent(MainActivity.this, ServiceSeg.class));
                MainActivity.this.finish();
            }
        };
    }

    public void onClickEnviar() {
        if (exiteConexionInternet()) {

            ManipulacionJson manipulacionJson = new ManipulacionJson(this);
            manipulacionJson.procesarEnviarArchivo();

        } else {
            notificar("No hay conexión disponible");
        }
    }

    public void onClickGrabar(final View view) {
        au.startRecording();

        bGrabar = findViewById(R.id.Grabar);
        bDetener = findViewById(R.id.Detener);

        bGrabar.setEnabled(false);
        bDetener.setEnabled(true);

        notificar("Presione el boton pause para dete"
                + "ner la grabación, se detendrá a los 10 seg.");

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                onClickDetener(view);
            }

        };
        handler.postDelayed(r, 10000);

    }

    public void onClickDetener(View view) {
        au.stopRecording();

        bDetener = findViewById(R.id.Detener);
        bReproducir = findViewById(R.id.Reproducir);
        bBorrar = findViewById(R.id.Borrar);

        bDetener.setEnabled(false);
        bReproducir.setEnabled(true);
        bBorrar.setEnabled(true);

        notificar("Audio Guardado");
    }

    public void onClickBorrar(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setMessage("¿Desea eliminar el archivo?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        bGrabar = findViewById(R.id.Grabar);
                        bDetener = findViewById(R.id.Detener);
                        bReproducir = findViewById(R.id.Reproducir);
                        bBorrar = findViewById(R.id.Borrar);

                        bGrabar.setEnabled(true);
                        bDetener.setEnabled(false);
                        bReproducir.setEnabled(false);
                        bBorrar.setEnabled(false);

                        File Fichero = new File(Constantes.fichero);
                        Fichero.delete();
                    }
                });
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // c�digo java si se ha pulsado no
                    }
                });
        alertDialog.show();

    }

    public void onClickReproducir(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(Constantes.fichero);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en reproducción");
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            ExifInterface exif;
            int orientation = 0;
            try {
                exif = new ExifInterface(foto);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            Bitmap original = CargarArchivo.reducirImagen(foto);
            Bitmap bm = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(String.format(foto));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cargarFotosSlidingDrawer();
            notificar("Imagen guardada");
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String contenido = data.getStringExtra("SCAN_RESULT");
            String formato = data.getStringExtra("SCAN_RESULT_FORMAT");
            notificar("Código: " + contenido);
            TextView codLeido = (TextView) findViewById(R.id.tvCodigoLeido);
            codLeido.setText(contenido);
        }
    }

    public void onClickLecturaCod(View view) {
        Intent intent = new Intent("app.geodat.SCAN");
        intent.putExtra("SCAN_MODE", "SCAN_MODE");
        startActivityForResult(intent, 2);
    }

    public void onClickCamara(View view) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        String formatoFecha = df.format(cal.getTime());

        TextView lat = (TextView) findViewById(R.id.textView12l);
        TextView lon = (TextView) findViewById(R.id.textView22l);
        String lats = (String) lat.getText();
        String lons = (String) lon.getText();
        String userID = getUserId();

        foto = Constantes.directorio + userID + "_" + formatoFecha + "_" + lats + ";" + lons
                + "_TEMP.jpg";

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri output = Uri.fromFile(new File(foto));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);

        startActivityForResult(intent, 1);
    }

    public void onClickMarcadores(View view) {

        ToggleButton marcadores = (ToggleButton) findViewById(R.id.marcadores);
        if (marcadores.isChecked()) {
            StringBuilder listadoCompleto = cargarListado();
            String[] lines = listadoCompleto.toString().split("__");

            for (String s : lines) {
                agregarMarcador(s);
            }
        } else
            notificar("Apagado");
    }

    public void actualizarFecha() {
        EditText editcalendar = (EditText) findViewById(R.id.editText6a);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        editcalendar.setText(f.format(new Date()));
    }

    public boolean exiteConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @SuppressLint("ResourceAsColor")
    public void cargarFotosSlidingDrawer() {

        LinearLayout rl = (LinearLayout) findViewById(R.id.imagen_scroll);

        StringBuilder listadoCompleto = cargarListado();

        if (listadoCompleto.length() > 0) {

            String[] lines = listadoCompleto.toString().split("__");

            rl.removeAllViews();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params2.gravity = Gravity.BOTTOM;
            params2.rightMargin = 2;
            params2.leftMargin = 2;

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params3.leftMargin = 5;
            params3.rightMargin = 5;

            for (String s : lines) {
                FrameLayout fl = new FrameLayout(this);
                ImageView iv = new ImageView(this);
                LinearLayout ll = new LinearLayout(this);
                ImageButton botborrar = new ImageButton(this);
                ImageButton botcentrar = new ImageButton(this);
                String direccion = directory.toString() + "/" + s;
                iv.setImageBitmap(CargarArchivo
                        .decodeSampledBitmapFromResource(direccion, 110, 119));

                botborrar.setTag(s);
                botborrar
                        .setBackgroundResource(android.R.drawable.ic_menu_delete);
                botborrar.setLayoutParams(params3);
                botborrar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String nombre = view.getTag().toString();
                        // Mostrar un mensaje de confirmaci�n antes de realizar
                        // el test
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                MainActivity.this);
                        alertDialog.setMessage("¿Desea eliminar el archivo?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        File fichero = new File(directory
                                                .toString() + "/" + nombre);
                                        fichero.delete();
                                        cargarFotosSlidingDrawer();
                                    }
                                });
                        alertDialog.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // c�digo java si se ha pulsado no
                                    }
                                });
                        alertDialog.show();

                    }

                });

                botcentrar.setTag(s);
                botcentrar.setBackgroundResource(android.R.drawable.ic_menu_compass);
                botcentrar.setLayoutParams(params3);
                botcentrar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombre = view.getTag().toString();
                        agregarMarcador(nombre);
                    }

                });

                ll.setLayoutParams(params);
                ll.setGravity(Gravity.CENTER);
                ll.setBackgroundResource(R.color.fondoSemiTransparente);
                fl.setLayoutParams(params2);

                ll.addView(botborrar);
                ll.addView(botcentrar);
                fl.addView(iv);
                fl.addView(ll);
                rl.addView(fl);
            }

        } else {
            rl.removeAllViews();
            TextView nohayarchivos = new TextView(this);
            nohayarchivos.setText("No se encontraron archivos");
            nohayarchivos.setTextColor(Color.parseColor("#FFFFFF"));
            nohayarchivos.setGravity(Gravity.CENTER);
            rl.addView(nohayarchivos);

        }
    }

    public void googlePlayActivado() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else {

            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            googleMap = fm.getMap();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            Location location;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                location = locationManager.getLastKnownLocation(provider);
            }

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "GPS desactivado", Toast.LENGTH_LONG).show();
            }

            if (location != null) {
                quitarMensaje();
                onLocationChanged(location);

                LatLng inicio = new LatLng(location.getLatitude(),
                        location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 15));
            } else {
                mostrarMensaje("Cargando posición actual, si tarda demasiado "
                        + "por favor active Wi-Fi o desplácese a una zona con mejor cobertura 3G");
                onLocationChanged(location);
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestLocationUpdates(provider, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            quitarMensaje();

            TextView lat = findViewById(R.id.textView12l);
            TextView lon = findViewById(R.id.textView22l);
            TextView locacion = findViewById(R.id.editText7a);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            String slat = String.valueOf(latitude);
            String slon = String.valueOf(longitude);
            lat.setText(slat);
            lon.setText(slon);
            locacion.setText((latitude) + ";" + (longitude));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }

    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
    }

    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    private StringBuilder cargarCsv() {
        String[] listado = ListadoArchivos.getCsvFiles(Constantes.directorio);
        StringBuilder listadoCompleto = new StringBuilder();
        if (listado.length > 0) {
            for (int i = 0; i < listado.length; i++) {
                listadoCompleto.append(listado[i]);
                listadoCompleto.append("__");
            }
        }
        return listadoCompleto;
    }

    private StringBuilder cargarListado() {
        directory = new File(Constantes.directorio);
        directory.mkdirs();

        String[] listado = ListadoArchivos.getImages(directory.toString());

        StringBuilder listadoCompleto = new StringBuilder();
        if (listado.length > 0) {
            for (int i = 0; i < listado.length; i++) {
                listadoCompleto.append(listado[i]);
                listadoCompleto.append("__");
            }
        }
        return listadoCompleto;
    }

    private void agregarMarcador(String nombre) {
        nombre = nombre.substring(0, nombre.length() - 4);
        String[] lines = nombre.split("_");
        String fecha = lines[1];
        String hora = lines[2];
        String latlons = lines[3];
        String[] latlo = latlons.split(";");

        LatLng latlon = new LatLng(Double.parseDouble(latlo[0]),
                Double.parseDouble(latlo[1]));
        String fichero = directory.toString() + "/" + nombre + ".jpg";
        Bitmap bm = CargarArchivo.decodeSampledBitmapFromResource(fichero, 70,
                70);

        Bitmap mascara = BitmapFactory.decodeResource(getResources(),
                R.drawable.mask_drawable);
        bm = Bitmap.createScaledBitmap(bm, mascara.getWidth(),
                mascara.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(mascara.getWidth(),
                mascara.getHeight(), Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(bm, 0, 0, null);
        mCanvas.drawBitmap(mascara, 0, 0, paint);
        paint.setXfermode(null);

        // Agregar marcador
        googleMap.addMarker(new MarkerOptions().position(latlon).title(fecha)
                .snippet(hora)
                .icon(BitmapDescriptorFactory.fromBitmap(result))
                .anchor(0.5f, 1));

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlon));
    }

    public String getUserId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = prefs.getString(getString(R.string.user_id), "-");
        if (userId != null && !userId.isEmpty() && !"-".equals(userId)) {
            return userId;
        } else {
            String imei = getIMEI();
            if (imei != null) {
                return imei;
            }
        }
        return "";
    }

    @SuppressLint("HardwareIds")
    public String getIMEI() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return null;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
//		return "860046038880938";
    }

    private View getTabIndicator(int icon) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);
        return view;
    }
}
