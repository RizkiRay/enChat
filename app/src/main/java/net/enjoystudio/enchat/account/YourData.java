package net.enjoystudio.enchat.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.BaseAppActivity;
import net.enjoystudio.enchat.BuildConfig;
import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class YourData extends AppCompatActivity {

    private CircleImageView proPict;
    private EditText name;
    private SharedPreferences sp;
    private ProgressDialog pd;

    static String pict ="";
    private static final int SELECT_PHOTO = 100;
    private String encodedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_data);

        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        proPict = (CircleImageView) findViewById(R.id.profile_pict);
        name = (EditText) findViewById(R.id.name);
        Button fin = (Button) findViewById(R.id.finish);
        pd = new ProgressDialog(this);

        proPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        assert fin != null;
        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equals(""))
                    Toast.makeText(YourData.this, "Please fill Your Name", Toast.LENGTH_SHORT).show();
                else {
                    pd.setMessage("updating your profile...");
                    pd.show();
                    StringRequest sr = new StringRequest(Request.Method.POST, C.API_UPDATE_PROFILE + BuildConfig.KEY
                            , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            if (response.trim().equals("g")) {
                                Toast.makeText(YourData.this, "Please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jobj = new JSONObject(response);
                                    sp.edit().putString(C.NAME, jobj.getString(C.NAME)).apply();
                                    sp.edit().putString(C.STATUS, jobj.getString(C.STATUS)).apply();
                                    sp.edit().putString(C.PROFILE_PICTURE, jobj.getString(C.PROFILE_PICTURE)).apply();
                                    finish();
                                    startActivity(new Intent(YourData.this, BaseAppActivity.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("CEK", error.toString());
                            pd.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(C.USER_ID, sp.getString(C.USER_ID, "0"));
                            params.put(C.NAME, name.getText().toString().trim());
                            params.put(C.STATUS, "");
                            params.put(C.PROFILE_PICTURE, sp.getString(C.USER_ID, "0") + ".jpg");
                            return params;
                        }
                    };
                    Volley.newRequestQueue(YourData.this).add(sr);
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedFoto = data.getData();
                pict = getRealPathFromURI(selectedFoto);
                uploadImage(pict, selectedFoto);
            }

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onBackPressed() {

    }

    public void uploadImage(String sourceFileUri, final Uri selectedFoto) {
        Bitmap myImg = BitmapFactory.decodeFile(sourceFileUri);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myImg.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        encodedString = Base64.encodeToString(byte_arr, 0);
        Log.d("cek", C.API_UPLOAD_IMAGE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                C.API_UPLOAD_IMAGE + BuildConfig.KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(YourData.this, response, Toast.LENGTH_SHORT).show();
                Picasso.with(YourData.this).load(selectedFoto).fit().centerInside().into(proPict);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(YourData.this, "Gagal, pastikan koneksi Anda berjalan lancar",
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", encodedString);
                params.put("name", "/profile_picture/"+sp.getString(C.USER_ID,"0")+".jpg");
                return params;

            }

        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
