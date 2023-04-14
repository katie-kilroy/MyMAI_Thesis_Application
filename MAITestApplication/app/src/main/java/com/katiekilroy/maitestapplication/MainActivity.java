//package com.katiekilroy.maitestapplication;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothAdapter.LeScanCallback;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanResult;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.graphics.Region;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//
//public class MainActivity extends AppCompatActivity {
//
//    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//    private BluetoothLeScanner bluetoothLeScanner = adapter.getBluetoothLeScanner();
//    private boolean scanning;
//    private Handler handler = new Handler();
//
//    private static final String TAG = ".maitestapplication";
//
//    // Stops scanning after 10 seconds.
//    private static final long SCAN_PERIOD = 10000;
//
//    private void scanLeDevice(final boolean scan) {
//        if (!scan) {
//            // Stops scanning .
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            bluetoothLeScanner.stopScan(new ScanCallback() {
//                @Override
//                public void onScanResult(int callbackType, ScanResult result) {
//                    super.onScanResult(callbackType, result);
//                }
//            });
//        } else if (scan) {
////            bluetoothLeScanner.startScan(LeScanCallback);
//        } else {
////            bluetoothLeScanner.stopScan(LeScanCallback);
//        }
//    }
//    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//    void bluetoothScanning(final boolean scanning) {
//
//        if (scanning) {
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            this.registerReceiver(mReceiver, filter);
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mBluetoothAdapter.startDiscovery();
//        }
//        else if (scanning){
//            mBluetoothAdapter.cancelDiscovery();
//        }
//
//    }
//
//
//    // Create a BroadcastReceiver for ACTION_FOUND.
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
////                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//
//                Log.i("Device Name: " , "device " + deviceName);
//                Log.i("deviceHardwareAddress " , "hard"  + deviceHardwareAddress);
//            }
//        }
//    };
//
//
//    @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            Log.d(TAG, "App started up");
//
//        setContentView(R.layout.activity_main);
//
//        final Button button1 = findViewById(R.id.button_first);
//        button1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Code here executes on main thread after user presses button
//                bluetoothScanning(true);
//        }
//        });
//
//        final Button button2 = findViewById(R.id.button_second);
//        button2.setOnClickListener(new View.OnClickListener()
//
//            {
//                public void onClick (View v){
//                // Code here executes on main thread after user presses button
//
//                    bluetoothScanning(false);
//            }
//
//            });
// }
//
//
//}