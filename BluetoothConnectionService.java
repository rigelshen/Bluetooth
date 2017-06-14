package com.example.rshen17.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rshen17 on 6/13/2017.
 */
public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID= UUID.randomUUID();
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //connecting as a server
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            //Use a temp object that is later assigned to mmServerSocket since
            //mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                //MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID);
            }catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            //Keep listening until exception occurs or a socket is returned.
            while (true){
                try{
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if(socket != null) {
                    //A connection was accepted. Perform work associated with
                    //the connection in a separate thread.
                    //manageMyConnectedSocket(socket);
                    //mmServerSocket.close();
                    //connected(sockeet, mmDevice);
                    break;
                }
            }
        }
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch(IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }


    //connecting as a client
    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            //use a temp obj that is later assigned to mmSocket since mmSocket is final
            //BluetoothSocket tmp = null;
            mmDevice = device;
            deviceUUID = uuid;
            /*
            try {
                //Get a BluetoothSocket to connect with the given BluetoothDevice.
                //My_UUID is the app's UUID string, also used in the server <code>
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;*/
        }

        public void run(){
            //Cancel discovery because it otherwise slows down the connection
            mBluetoothAdapter.cancelDiscovery();
            BluetoothSocket tmp = null;

            //take tmp
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException closeException) {
                //unable to connect; close the socket and return
                Log.e(TAG, "Could not create InsecureRfcommSocket", closeException);
            }
            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();

            try {
                //connect to the remote device through the socket. This call blocks
                //until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException closeException) {
                //unable to connect; close the socket and return
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }
        //The connection attempt succeeded. Perform work associated with the
        //connection in a separate thread
        //manageMyConnectedSocket(mmSocket);

        //Closes the client socket and causes the thread to finish
        public void cancel() {
            try {
                mmSocket.close();
            } catch(IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}
