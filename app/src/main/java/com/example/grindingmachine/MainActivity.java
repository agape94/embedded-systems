package com.example.grindingmachine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SeekBar mMotorSpeedSeekbar;
    private EditText mSetSpeedValue;
    private int mCurrentRPM;
    private Button mBluetoothToggle;
    private Button mSetSpeedButton;
    SharedPreferences mBluetoothSettings;
    private Menu mMenu;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mBluetoothService = null;
    // Name of the connected device
    private String mConnectedDeviceName = null;

    //======================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothSettings = getSharedPreferences(Constants.APP_NAME,MODE_PRIVATE);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences settingsSharedPref = android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);

        Constants.MAXIMUM_RPM_VALUE = Integer.parseInt(settingsSharedPref.getString(Constants.MAXIMUM_RPM_KEY, "999"));
        Constants.MINIMUM_RPM_VALUE = Integer.parseInt(settingsSharedPref.getString(Constants.MINIMUM_RPM_KEY, "0"));
        Constants.SET_SPEED_AUTOMATICALLY_VALUE = settingsSharedPref.getBoolean(Constants.SET_SPEED_AUTOMATICALLY_KEY, true);

        initUIElements();
        toastIfBTNotAvailable();

    }

    //======================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        mMenu = menu;
        return true;
    }

    //======================================================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:
                // User chose the "Settings" item, show the app settings UI...
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.bluetooth_item:
                // User chose the "Bluetooth" item, show the app settings UI...
                connect();
                return true;

            case R.id.help_item:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //======================================================================================

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        } else {
            // Initialize the BluetoothService to perform bluetooth connections
            if(mBluetoothService == null) {
                mBluetoothService = new BluetoothService(this, mHandler);
                if(mBluetoothSettings.contains(Constants.DEVICE_ADDRESS))
                {
                    String deviceAddress = mBluetoothSettings.getString(Constants.DEVICE_ADDRESS, "");
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    // Attempt to connect to the device
                    mBluetoothService.connect(device);
                    Log.d("onStart", "Connected automatically to " + deviceAddress);
                }
            }
        }
    }

    //======================================================================================

    @Override
    public synchronized void onPause() {
        super.onPause();

    }

    //======================================================================================

    @Override
    public void onStop() {
        super.onStop();

    }

    //======================================================================================

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the BluetoothService
        if (mBluetoothService != null) mBluetoothService.stop();
    }

    //======================================================================================

    void initUIElements(){
        mCurrentRPM = Constants.MINIMUM_RPM_VALUE;

        mSetSpeedValue = findViewById(R.id.rpm_edit_text);
        mSetSpeedValue.setText(String.valueOf(mCurrentRPM));

        mMotorSpeedSeekbar = findViewById(R.id.motorSpeedSeekBar);
        mMotorSpeedSeekbar.setProgress(mCurrentRPM);

        mMotorSpeedSeekbar.setMin(Constants.MINIMUM_RPM_VALUE);
        mMotorSpeedSeekbar.setMax(Constants.MAXIMUM_RPM_VALUE);

        mSetSpeedValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeSeekBarValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mMotorSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrentRPM = seekBar.getProgress();
                if(Constants.SET_SPEED_AUTOMATICALLY_VALUE)
                {
                    sendNewRpmToGrindingMachine();
                }
                if(!mSetSpeedValue.getText().toString().isEmpty()) {
                    if (mCurrentRPM != Integer.parseInt(mSetSpeedValue.getText().toString())) {
                        mSetSpeedValue.setText(String.valueOf(mCurrentRPM));
                        mSetSpeedValue.setSelection(mSetSpeedValue.getText().toString().length());
                    }else {
                        return;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSetSpeedButton = findViewById(R.id.set_speed_btn);
        mSetSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewRpmToGrindingMachine();
            }
        });
    }

    //======================================================================================

    void toastIfBTNotAvailable(){
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
    }

    //======================================================================================

    private void changeSeekBarValue(){

        if(!mSetSpeedValue.getText().toString().isEmpty()) {
            mCurrentRPM = Integer.parseInt(mSetSpeedValue.getText().toString());
            mMotorSpeedSeekbar.setProgress(mCurrentRPM, true);
        }else{
            mMotorSpeedSeekbar.setProgress(0, true);
            mCurrentRPM = 0;
        }
    }

    //======================================================================================

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //======================================================================================

    // The Handler that gets information back from the BluetoothService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_STATE_CHANGE:
                    int bluetoothState = msg.arg1;
                    if(mMenu != null) {
                        switch (bluetoothState) {
                            case BluetoothService.STATE_NONE:
                                mMenu.findItem(R.id.bluetooth_item).setIcon(R.drawable.ic_bluetooth_disabled_red_24dp);
                                break;
                            case BluetoothService.STATE_CONNECTED:
                                mMenu.findItem(R.id.bluetooth_item).setIcon(R.drawable.ic_bluetooth_connected_white_24dp);
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                mMenu.findItem(R.id.bluetooth_item).setIcon(R.drawable.ic_bluetooth_yellow_24dp);
                                break;
                        }
                    }
                    break;

            }
        }
    };

    //======================================================================================

    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mBluetoothService.write(send);
        }
    }

    //======================================================================================

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBluetoothService.connect(device);

                    // Writing the device address to shared prefs, so next time it will connect automatically
                    SharedPreferences.Editor editor = mBluetoothSettings.edit();
                    editor.putString(Constants.DEVICE_ADDRESS, address);
                    editor.commit();
                }
                break;
            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //======================================================================================

    public void connect() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE);
    }

    //======================================================================================

    public void discoverable(View v) {
        ensureDiscoverable();
    }

    //======================================================================================

    public void sendNewRpmToGrindingMachine(){
        String message = String.valueOf(mCurrentRPM);
        sendMessage(Constants.MESSAGE_START + message + Constants.MESSAGE_END);
    }
}
