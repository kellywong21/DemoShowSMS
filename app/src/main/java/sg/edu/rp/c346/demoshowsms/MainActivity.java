package sg.edu.rp.c346.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView tvSMS;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSMS = findViewById(R.id.tvSMS);
        btnRetrieve = findViewById(R.id.btnRetrieve);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }
                //create messages URI
                Uri uri = Uri.parse("content://sms");
                //columns we want
                //date of message took place
                //address is number of the recipient
                //body is message content
                //type 1 is received, type 2 is send.

                String[] reqCols = new String[]{"date","address","body","type"};

                //Get content resolver object from which to
                //query the content provider
                ContentResolver cr = getContentResolver();
                String filter = "body LIKE ? AND body LIKE ?";
                String[] filterArgs = {"%late%,","%min%"};
                //Fetch SMS messages from Built-in Content Provider
                Cursor cursor = cr.query(uri,reqCols,null,null,null);
                String smsBody = "";
                if (cursor.moveToFirst()){
                    do{
                        long dataInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM YYYY h:mm:ss aa",dataInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")){
                            type = "Inbox";
                        }else{
                            type = "Sent";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body
                                + "\"\n\n";
                    }while (cursor.moveToNext());
                }
                tvSMS.setText(smsBody);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnRetrieve.performClick();
                }else{
                    Toast.makeText(MainActivity.this,"Permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
