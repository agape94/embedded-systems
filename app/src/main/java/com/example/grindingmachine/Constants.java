package com.example.grindingmachine;

import java.util.UUID;

public class Constants {
    public static final String GRINDING_MACHINE_BT_NAME = "GRINDING";
    public static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String APP_NAME = "Grinding app";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public static final String MESSAGE_START = "<";
    public static final String MESSAGE_END = ">";

    // Constants for the settings activity
    public static String SET_SPEED_AUTOMATICALLY_KEY = "set_speed_automatically_preference";
    public static String MINIMUM_RPM_KEY = "minimum_speed_preference";
    public static String MAXIMUM_RPM_KEY = "maximum_speed_preference";
    public static boolean SET_SPEED_AUTOMATICALLY_VALUE;
    public static int MINIMUM_RPM_VALUE;
    public static int MAXIMUM_RPM_VALUE;
}
