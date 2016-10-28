package android.btm.demoandroid;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    String DATABASE_NAME="dbContact.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    EditText etUser, etPass;
    Button btnLogin, btnCancel;

    HashMap<String,String> listUser = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        copyDBFromAssetsToMobile();
        getInfoFromDB();
        processLogin();
    }

    private void processLogin() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etUser = (EditText) findViewById(R.id.editTextUser);
        etPass = (EditText) findViewById(R.id.editTextPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void login() {
        Set set = listUser.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if (etUser.getText().toString().equals(entry.getKey())
                    && etPass.getText().toString().equals(entry.getValue())){
                Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show();
                savePreferences();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }else{
                Toast.makeText(this, "Wrong user or password", Toast.LENGTH_LONG).show();
                etUser.setVisibility(View.VISIBLE);
                etUser.setBackgroundColor(Color.RED);
            }

        }
    }

    private void savePreferences() {
        SharedPreferences pre = getSharedPreferences("my_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();

        editor.putString("user", etUser.getText().toString());
        editor.putString("pass", etPass.getText().toString());

        editor.commit();
    }

    private void getInfoFromDB() {
        database=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor=database.query("Login",null,null,null,null,null,null);
        //Cursor cursor2=database.rawQuery("select * from Contact",null);
        while (cursor.moveToNext())
        {
            listUser.put(cursor.getString(0),cursor.getString(1));
        }
        cursor.close();
    }
    private void copyDBFromAssetsToMobile() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try
            {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Save complete", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getPath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            // File f = new File(getApplicationInfo().dataDir);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getPath() {
            return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
        }
    }
