package org.first.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
        TextView statustextView;
        Button searchbtn;
        ListView listView;
        BluetoothAdapter bluetoothAdapter;
        ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    //ArrayList<BluetoothDevice> dEVICES = new ArrayList<>();
    BluetoothDevice dEVICES[] = new BluetoothDevice[20];

    BluetoothDevice device;
        ArrayAdapter arrayAdapter;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }
//================================================================================================================================================






    private final BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Action",action);
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    statustextView.setText("Finished");
                    searchbtn.setEnabled(true);
                }else if(BluetoothDevice.ACTION_FOUND.equals(action)){


                         device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                         for (int i =0; i <dEVICES.length;i++){
                             dEVICES[i] = device;
                         }




                    String name = device.getName();
                        String address = device.getAddress();
                        String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                       // Log.i("device found","Name " +name + " address "+address+ " rssi "+rssi);
                        if(!addresses.contains(address)){
                            addresses.add(address);
                            String deviceString = "";
                            if (name==null||name.equals("")){
                                deviceString =address + " RSSI " + rssi + "dBm";
                            }else {
                                deviceString = name + " RSSI " + rssi + "dBm";
                            }

                            bluetoothDevices.add(deviceString);
                            arrayAdapter.notifyDataSetChanged();
                        }

                }
            }
        };
        public void searchClick(View view){
            statustextView.setText("Searching...");
            searchbtn.setEnabled(false);
            bluetoothDevices.clear();
            addresses.clear();
            bluetoothAdapter.startDiscovery();
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,bluetoothDevices);
        listView.setAdapter(arrayAdapter);
        statustextView = findViewById(R.id.statusTextView);
        searchbtn = findViewById(R.id.searchingButton);

         checkPermissions();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);
        bluetoothAdapter.startDiscovery();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                   createBond(dEVICES[i]);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });



    }
    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();

    }
}