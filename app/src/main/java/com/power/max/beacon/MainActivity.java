package com.power.max.beacon;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Main Class that starts the BeaconService.
 */
public class MainActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Create CheckedChangeListener for Service switch */
        Switch switchService = (Switch) findViewById(R.id.switchService);
        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!isMyServiceRunning(BeaconService.class)) {
                        // Start Service
                        context.startService(new Intent(context, BeaconService.class));

                        if (!isBluetoothEnabled()) {
                            activateBluetooth();
                            Toast.makeText(context, "Bluetooth activated", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(context, "Service Started", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isMyServiceRunning(BeaconService.class)) {
                        // Stop Service
                        context.stopService(new Intent(context, BeaconService.class));
                        Toast.makeText(context, "Service Stopped", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Check if Service is running and set the checkstate for the switch widget.
        if (isMyServiceRunning(BeaconService.class)) {
            switchService.setChecked(true);
        } else {
            switchService.setChecked(false);
        }
    }

    /**
     * Kill Service on Destroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMyServiceRunning(BeaconService.class)) {
            // Stop Service
            context.stopService(new Intent(context, BeaconService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Open about dialog.
        if (id == R.id.action_settings) {
            AboutFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getFragmentManager(), "dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to check, if a specific Service is running.
     * Call it: isMyServiceRunning(MyService.class)
     * http://stackoverflow.com/a/5921190/2369122
     *
     * @param serviceClass the Service to check.
     * @return true if Service is running, false if service is not running.
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function checks, if Bluetooth is enabled.
     * @return false if not enabled, else true.
     */
    private boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * Method to activate Bluetooth.
     */
    private void activateBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }
}