package com.example.grindingmachine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    private Button mLoadSpeedProfileButton;
    private Button mSaveSpeedProfileButton;
    SharedPreferences mBluetoothSettings;
    private Menu mMenu;
    private boolean mSeekbarMoved = false;

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

        loadSettings();
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
                // User chose the "Settings" item
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, Constants.START_SETTINGS_REQUEST_CODE);
                return true;

            case R.id.bluetooth_item:
                // User chose the "Bluetooth" item
                connect();
                return true;

            case R.id.speed_profiles_item:
                // User chose the "Speed profiles" action
                Intent speedProfilesIntent = new Intent(this, SpeedProfilesActivity.class);
                speedProfilesIntent.putExtra(Constants.SPEED_PROFILES_ACTIVITY_FOR, Constants.EDIT_VIEW_SPEED_PROFILES_REQUEST_CODE);
                startActivityForResult(speedProfilesIntent, Constants.EDIT_VIEW_SPEED_PROFILES_REQUEST_CODE);
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
        if(mMotorSpeedSeekbar.getMax() != Constants.MAXIMUM_RPM_VALUE)
        {
            mMotorSpeedSeekbar.setMax(Constants.MAXIMUM_RPM_VALUE);
        }
        mMotorSpeedSeekbar.setProgress(mCurrentRPM);

        mSetSpeedValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rpmValueChanged();
            }
        });

        mMotorSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mSeekbarMoved) {
                    mCurrentRPM = Constants.MINIMUM_RPM_VALUE + progress;
                    mSetSpeedValue.setText(String.valueOf(mCurrentRPM));
                    mSetSpeedValue.setSelection(mSetSpeedValue.getText().toString().length());
                    if (Constants.SET_SPEED_AUTOMATICALLY_VALUE) {
                        sendNewRpmToGrindingMachine();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekbarMoved = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekbarMoved = false;
            }
        });

        mSetSpeedButton = findViewById(R.id.set_speed_btn);
        mSetSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewRpmToGrindingMachine();
            }
        });

        mLoadSpeedProfileButton = findViewById(R.id.load_speed_btn);
        mLoadSpeedProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speedProfilesIntent = new Intent(getApplicationContext(),SpeedProfilesActivity.class);
                speedProfilesIntent.putExtra(Constants.SPEED_PROFILES_ACTIVITY_FOR, Constants.SELECT_SPEED_PROFILE_REQUEST_CODE);
                startActivityForResult(speedProfilesIntent,Constants.SELECT_SPEED_PROFILE_REQUEST_CODE);
            }
        });
        mSaveSpeedProfileButton = findViewById(R.id.save_speed_btn);
        mSaveSpeedProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
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

    private void rpmValueChanged(){

        if(!mSetSpeedValue.getText().toString().isEmpty()) {
            int inputedValue = 0;
            inputedValue = Integer.parseInt(mSetSpeedValue.getText().toString());
            if(inputedValue >= Constants.MINIMUM_RPM_VALUE && inputedValue <= Constants.MAXIMUM_RPM_VALUE) {
                mCurrentRPM = inputedValue;
                if(!mSeekbarMoved) {
                    mMotorSpeedSeekbar.setProgress(mCurrentRPM, true);
                }
            }else{
                mSetSpeedValue.setError("The RPM value should be between " + Constants.MINIMUM_RPM_VALUE
                + " and " + Constants.MAXIMUM_RPM_VALUE);
            }
        }else{
            if(!mSeekbarMoved) {
                mMotorSpeedSeekbar.setProgress(Constants.MINIMUM_RPM_VALUE, true);
            }
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
                }
            case Constants.SELECT_SPEED_PROFILE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    SpeedProfile selectedSpeedProfile = (SpeedProfile) data.getSerializableExtra(Constants.SPEED_PROFILE_SELECT_KEY);
                    if(selectedSpeedProfile != null)
                    {
                        mCurrentRPM = selectedSpeedProfile.getSpeed();
                        mMotorSpeedSeekbar.setProgress(mCurrentRPM);
                        mSetSpeedValue.setText(String.valueOf(mCurrentRPM));
                        Toast.makeText(this, Html.fromHtml(getString(R.string.selected_speed_profile_toast)+" <b>"+ selectedSpeedProfile.getTitle() +"</b>"), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constants.SAVE_SPEED_PROFILE_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Saved succesfully!", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.START_SETTINGS_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    loadSettings();
                }
                break;
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

    //======================================================================================

    private void showEditDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.profile_popup_dialog, null);
        final EditText mTitleTV = (EditText) mView.findViewById(R.id.pop_up_title);
        final EditText mSpeedTV = (EditText) mView.findViewById(R.id.pop_up_speed);
        mSpeedTV.setText(String.valueOf(mCurrentRPM));
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!mTitleTV.getText().toString().isEmpty() && !mSpeedTV.getText().toString().isEmpty()) {
                    SpeedProfile profile = new SpeedProfile();
                    profile.setSpeed(Integer.parseInt(mSpeedTV.getText().toString()));
                    profile.setTitle(mTitleTV.getText().toString());
                    Intent speedProfilesIntent = new Intent(getApplicationContext(), SpeedProfilesActivity.class);
                    speedProfilesIntent.putExtra(Constants.SPEED_PROFILES_ACTIVITY_FOR, Constants.SAVE_SPEED_PROFILE_REQUEST_CODE);
                    speedProfilesIntent.putExtra(Constants.SPEED_PROFILE_SAVE_KEY, profile);
                    startActivityForResult(speedProfilesIntent, Constants.SAVE_SPEED_PROFILE_REQUEST_CODE);
                }
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    //======================================================================================

    private void loadSettings(){
        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences settingsSharedPref = android.support.v7.preference.PreferenceManager
                .getDefaultSharedPreferences(this);

        Constants.MAXIMUM_RPM_VALUE = Integer.parseInt(settingsSharedPref.getString(Constants.MAXIMUM_RPM_KEY, "999"));
        Constants.MINIMUM_RPM_VALUE = Integer.parseInt(settingsSharedPref.getString(Constants.MINIMUM_RPM_KEY, "0"));
        Constants.SET_SPEED_AUTOMATICALLY_VALUE = settingsSharedPref.getBoolean(Constants.SET_SPEED_AUTOMATICALLY_KEY, true);

        if (Constants.MINIMUM_RPM_VALUE >= Constants.MAXIMUM_RPM_VALUE)
        {
            Toast.makeText(this, "Wrong values for RPM limits. Minimum value should be less" +
                    " than Maximum value. Using default values of 0 and 999 respectively.", Toast.LENGTH_SHORT);
            Constants.MINIMUM_RPM_VALUE = 0;
            Constants.MAXIMUM_RPM_VALUE = 999;

        }
        if(mMotorSpeedSeekbar != null){
            mMotorSpeedSeekbar.setMax(Constants.MAXIMUM_RPM_VALUE);
        }
    }

}
