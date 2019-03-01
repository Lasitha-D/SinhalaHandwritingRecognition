package research_project.seng_44696.sinhalahandwritingrecognition;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import research_project.seng_44696.sinhalahandwritingrecognition.Views.MyDrawView;

public class MainActivity extends Activity {
    MyDrawView myDrawView;
    RelativeLayout parent;
    Button sendButton, clearButton;
    private OutputStream outputStream;
    //String fname;
    BluetoothDevice myB;
    ParcelUuid[] uuids;
    BluetoothAdapter mybluetoothAdapter;
    Intent enableBluetoothIntent;
    int requestCodeForEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RelativeLayout parent = findViewById(R.id.signImageParent);
        sendButton = findViewById(R.id.sendBtn);
        clearButton = findViewById(R.id.clearBtn);
        myDrawView = new MyDrawView(this);
        parent.addView(myDrawView);
        requestCodeForEnable = 1;

        sendImageByBluetooth();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==requestCodeForEnable){
            if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling cancelled. You will not be able to send data to your computer", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //send the saved image by bluetooth to device
    public void sendImageByBluetooth(){

        //saveImage();

        mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mybluetoothAdapter.isEnabled()){
            enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, requestCodeForEnable);
        }

    }

    //save image as png
    public void saveImage(){

        verifyStoragePermissions(MainActivity.this);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_"+ timeStamp +".png";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            parent.setDrawingCacheEnabled(true);
            Bitmap bmp = parent.getDrawingCache();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        MediaScannerConnection.scanFile(MainActivity.this, new String[]{
                file.toString()}, new String[]{file.getName()}, null);

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}