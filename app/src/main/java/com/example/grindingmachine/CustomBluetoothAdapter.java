package com.example.grindingmachine;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Iterator;
import java.util.Set;

public class CustomBluetoothAdapter {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRemoteDevice;

    public CustomBluetoothAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public int getBluetoothState(){
        if(mBluetoothAdapter == null){
            return -1;
        }else if(mBluetoothAdapter.isEnabled()){
            return BluetoothAdapter.STATE_ON;
        }else if(!mBluetoothAdapter.isEnabled()){
            return BluetoothAdapter.STATE_OFF;
        }

        //TODO get other states, like connected or disconnected

        return -1; // Means unknown bt state
    }

    public boolean isConnectedTo(String address){
        boolean isConnected = false;
        Set<BluetoothDevice> currentDevices = mBluetoothAdapter.getBondedDevices();
        for(Iterator<BluetoothDevice> it = currentDevices.iterator() ; it.hasNext(); ){
            BluetoothDevice device = it.next();
            if(device.getAddress().compareTo(address) == 0){
                isConnected = true;
                mRemoteDevice = device;
            }
        }

        return isConnected;
    }
    public boolean disconnect(){
        //TODO disconnect from the remote (paired) device
        return false;
    }

    public boolean connect(){
        //TODO connect to the remote (paired) device
        return false;
    }
}
