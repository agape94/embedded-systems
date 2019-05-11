package com.example.grindingmachine;

import java.util.UUID;

public class Constants {
    public static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String APP_NAME = "Grinding app";
    public static final String SPEED_PROFILES_SHARED_PREF_KEY = "speed_profiles";
    public static final String SPEED_PROFILES_SHARED_PREF_NAME = "speed_profiles_shared_prefs";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 3;
    public static final int MESSAGE_READ = 4;
    public static final int MESSAGE_WRITE = 5;
    public static final int MESSAGE_DEVICE_NAME = 6;
    public static final int MESSAGE_TOAST = 7;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public static final char MESSAGE_START = '<';
    public static final char MESSAGE_END = '>';

    // Constants for the settings activity
    public static String SET_SPEED_AUTOMATICALLY_KEY = "set_speed_automatically_preference";
    public static String MINIMUM_RPM_KEY = "minimum_speed_preference";
    public static String MAXIMUM_RPM_KEY = "maximum_speed_preference";
    public static boolean SET_SPEED_AUTOMATICALLY_VALUE;
    public static int MINIMUM_RPM_VALUE;
    public static int MAXIMUM_RPM_VALUE;

    public enum EditOptions{
        NEW_SPEED_PROFILE,
        EDIT_SPEED_PROFILE,
        DELETE_SPEED_PROFILE;
    }

    public static final int SELECT_SPEED_PROFILE_REQUEST_CODE = 8;
    public static final int SAVE_SPEED_PROFILE_REQUEST_CODE = 9;
    public static final int START_SETTINGS_REQUEST_CODE = 10;
    public static final int EDIT_VIEW_SPEED_PROFILES_REQUEST_CODE = 11;
    public static final String SPEED_PROFILES_ACTIVITY_FOR = "start_speed_profiles_for";
    public static final String SPEED_PROFILE_SELECT_KEY = "load_speed_profile";
    public static final String SPEED_PROFILE_SAVE_KEY = "save_speed_profile";
}
