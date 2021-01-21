package id.co.sweetmusroom.panicbuttonapps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.co.sweetmusroom.panicbuttonapps.app.AppController;
import id.co.sweetmusroom.panicbuttonapps.model.User;
import id.co.sweetmusroom.panicbuttonapps.util.Server;

public class LoginActivity extends AppCompatActivity {

    //simpan data login
    SharedPreferences sharedpreferences;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    ProgressDialog pDialog;
    Handler handler = new Handler();
    public final static String TAG_ID = "idpenduduk";
    public final static String TAG_IDP = "uniqid";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_PASSWORD = "password";
    public final static String TAG_NAMA = "nama";
    public final static String TAG_ALAMAT = "alamat";
    public final static String TAG_TELPON = "telepon";
    public final static String TAG_EMAIL = "email";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_object = "json_obj_req";
    private static final String TAG = LoginActivity.class.getSimpleName();
    int success;
    boolean session;

    //Kebutuhan signup dan signin
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    Boolean inetStat = false;

    //variabel
    boolean statusLogin = true;

    //widget
    Button btnMasuk,btnDaftar,btnMauDaftar;
    ScrollView layoutLogin,layoutDaftar;
    ImageView backLogin;

    EditText et_unameMasuk,et_passMasuk,et_nama,et_unameDaftar
            ,et_passDaftar,et_emailDaftar,et_tlp,et_alamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);

        //Kebutuhan menghubungkan dengan userinterface)
        btnDaftar       = findViewById(R.id.buttonDaftar);
        btnMasuk        = findViewById(R.id.buttonLogin);
        btnMauDaftar    = findViewById(R.id.buttonMauDaftar);

        backLogin       = findViewById(R.id.backlogin);

        //layout
        layoutLogin     = findViewById(R.id.layoutLogin);
        layoutDaftar    = findViewById(R.id.layoutDaftar);

        //EditText Layout Login dan Daftar
        et_unameMasuk   = findViewById(R.id.usernameSignIn);
        et_passMasuk    = findViewById(R.id.passwordSignIn);

        et_nama         = findViewById(R.id.nmLengkap);
        et_unameDaftar  = findViewById(R.id.usernameSignUp);
        et_passDaftar   = findViewById(R.id.passwordSignUp);
        et_tlp          = findViewById(R.id.tlpSignUp);
        et_emailDaftar  = findViewById(R.id.emailSignUp);
        et_alamat       = findViewById(R.id.alamatSignUp);


        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_unameMasuk.getText().toString();
                String password = et_passMasuk.getText().toString();
                inetStat = checkInternet();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (inetStat) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "Jaringan Bermasalah, Pastikan Internet Tersedia", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inetStat = checkInternet();
                if (inetStat){
                    changeLayout();
                } else {
                    Toast.makeText(getApplicationContext(), "Jaringan Bermasalah, Pastikan Internet Tersedia", Toast.LENGTH_LONG).show();
                }

            }
        });

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout();
            }
        });

        btnMauDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama     = et_nama.getText().toString();
                String username = et_unameDaftar.getText().toString();
                String password = et_passDaftar.getText().toString();
                String email    = et_emailDaftar.getText().toString();
                String telepon  = et_tlp.getText().toString();
                String alamat   = et_alamat.getText().toString();
                inetStat = checkInternet();

                // mengecek kolom yang kosong
                if (nama.trim().length() > 0 && username.trim().length() > 0
                        && password.trim().length() > 0 && email.trim().length() > 0
                        && telepon.trim().length() > 0 && alamat.trim().length() > 0) {
                    if (inetStat) {
                        checkDaftar(nama, username, password, email, telepon, alamat);
                    } else {
                        Toast.makeText(getApplicationContext(), "Jaringan Bermasalah, Pastikan Internet Tersedia", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (statusLogin){
            finish();
        } else {
            changeLayout();
        }
    }

    private void changeLayout(){
        if (statusLogin){
            layoutDaftar.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
            clearText();
            statusLogin = false;

        } else {
            layoutDaftar.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
            clearText();
            statusLogin = true;

        }
    }

    private void clearText(){
        if (statusLogin){
            et_unameMasuk.setText("");
            et_passMasuk.setText("");
        } else {
            et_unameDaftar.setText("");
            et_passDaftar.setText("");
            et_nama.setText("");
            et_emailDaftar.setText("");
            et_tlp.setText("");
            et_alamat.setText("");
        }

    }

    public boolean checkInternet() {
        boolean statusInternet;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        statusInternet = networkInfo != null && networkInfo.isConnected();

        return statusInternet;
    }

    // Kebutuan Signup dan Login
    public void checkLogin(final String username, final String password) {
        User akun = new User();

        akun.setUsername("");
        akun.setPassword("");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mohon Tunggu ...");
        showDialog();

        StringRequest stringReq = new StringRequest(Request.Method.POST, Server.URL+"login.php", new Response.Listener<String>() {

            boolean status = false;

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);

                try {
                    final JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        final String idp = jObj.getString(TAG_ID);
                        final String idunik = jObj.getString(TAG_IDP);
                        final String username = jObj.getString(TAG_USERNAME);
                        final String nama = jObj.getString(TAG_NAMA);
                        final String email = jObj.getString(TAG_EMAIL);
                        final String alamat = jObj.getString(TAG_ALAMAT);
                        final String telepon = jObj.getString(TAG_TELPON);

                        Log.e("Successfully Login!", jObj.toString());
                        Log.e("Nama!", nama);

                        status = true;

                        Runnable runnableSuccess = new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();

                                // menyimpan login ke session
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean(LoginActivity.session_status, true);
                                editor.putString(TAG_ID, idp);
                                editor.putString(TAG_IDP, idunik);
                                editor.putString(TAG_USERNAME, username);
                                editor.putString(TAG_EMAIL, email);
                                editor.putString(TAG_NAMA, nama);
                                editor.putString(TAG_ALAMAT, alamat);
                                editor.putString(TAG_TELPON, telepon);
                                Toast.makeText(getApplicationContext(), nama, Toast.LENGTH_LONG).show();
                                editor.apply();

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);

                                try {
                                    Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                finish();

                                clearText();
                            }
                        };

                        Runnable runnableFailed = new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                Toast.makeText(getApplicationContext(), "Maaf Terjadi Kesalahan Saat Masuk", Toast.LENGTH_LONG).show();
                                clearText();
                            }
                        };

                        if (status) {
                            handler.postDelayed(runnableSuccess,1500);
                        } else {
                            handler.postDelayed(runnableFailed, 1500);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        hideDialog();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringReq, tag_json_object);
    }

    public void checkDaftar(final String nama, final String username, final String password,
                            final String email, final String telepon, final String alamat) {

        final String uniq_id = UUID.randomUUID().toString();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mohon Tunggu ...");
        showDialog();

        StringRequest stringReq = new StringRequest(Request.Method.POST, Server.URL+"signup.php", new Response.Listener<String>() {

            boolean status = false;

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);

                try {
                    final JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        Log.e("Berhasil Daftar!", jObj.toString());

                        status = true;

                        Runnable runnableSuccess = new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();

                                try {
                                    Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                clearText();
                                changeLayout();
                            }
                        };

                        Runnable runnableFailed = new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                Toast.makeText(getApplicationContext(), "Maaf Terjadi Kesalahan Saat Masuk", Toast.LENGTH_LONG).show();
                                clearText();
                            }
                        };

                        if (status) {
                            handler.postDelayed(runnableSuccess,1500);
                        } else {
                            handler.postDelayed(runnableFailed, 1500);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        hideDialog();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("unik_ud", uniq_id);
                params.put("username", username);
                params.put("password", password);
                params.put("unik_id",uniq_id);
                params.put("nama", nama);
                params.put("email", email);
                params.put("telepon", telepon);
                params.put("alamat", alamat);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringReq, tag_json_object);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
