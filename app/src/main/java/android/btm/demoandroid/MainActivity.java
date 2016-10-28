package android.btm.demoandroid;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String DATABASE_NAME="dbContact.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    ListView lvContact;
    ArrayList<String> lstContact;
    ArrayAdapter<String> adapterContact;

    Button btnAdd, btnEdit, btnDelete;
    TextView txtUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coppyDBFromAssetsToMobile();
        addControls();
        addEvents();
        showAllContactOnListView();
        restorePreferences();
    }

    private void restorePreferences() {
        SharedPreferences pre = getSharedPreferences("my_data", MODE_PRIVATE);

        if (pre != null){
            txtUser = (TextView) findViewById(R.id.txtUser);
            txtUser.setText("hello: " + pre.getString("user","").toString());
        }
    }

    private void addControls() {
        lvContact = (ListView) findViewById(R.id.lvContact);
        lstContact =new ArrayList<>();
        adapterContact =new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1, lstContact);
        lvContact.setAdapter(adapterContact);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDelete = (Button) findViewById(R.id.btnDelete);
    }

    private void addEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAction();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editAction();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteAction();
            }
        });
    }

    private void addAction() {
        ContentValues row=new ContentValues();
        row.put("Ma",100);
        row.put("Ten","Truong Phan");
        row.put("Phone","081090");
        long r= database.insert("Contact",null,row);
        Toast.makeText(MainActivity.this,"Save Complete", Toast.LENGTH_LONG).show();
        showAllContactOnListView();
    }
    private void editAction() {
        ContentValues row=new ContentValues();
        row.put("Ten","Edit Name");
        database.update("Contact", row, "ma=?", new String[]{"3"});
        showAllContactOnListView();
    }
    private void deleteAction() {
        database.delete("Contact","ma=?",new String[]{"2"});
        showAllContactOnListView();
    }

    private void showAllContactOnListView() {
        database=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor=database.query("Contact",null,null,null,null,null,null);
        //Cursor cursor2=database.rawQuery("select * from Contact",null);
        lstContact.clear();
        while (cursor.moveToNext())
        {
            int ma=cursor.getInt(0);
            String ten=cursor.getString(1);
            String phone=cursor.getString(2);
            lstContact.add(ma+"-"+ten+"\n"+phone);
        }
        cursor.close();
        adapterContact.notifyDataSetChanged();
    }


    private void coppyDBFromAssetsToMobile() {
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
        try
        {
            InputStream myInput=getAssets().open(DATABASE_NAME);
            String outFileName = getPath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
           // File f = new File(getApplicationInfo().dataDir);
            if(!f.exists())
            {
                f.mkdir();
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex)
        {
            Log.e("coppy error",ex.toString());
        }
    }

    private String getPath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

}
