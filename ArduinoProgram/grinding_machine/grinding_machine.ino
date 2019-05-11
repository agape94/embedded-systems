#include <SoftwareSerial.h>

unsigned int D2 = 2;
unsigned int D3 = 3;
unsigned int D4 = 4;
SoftwareSerial BTSerial(D3,D2); // RX: port D2, TX port D3
byte MESSAGE_START = 0x3C;
byte MESSAGE_END = 0x3E;

const byte numBytes = 64;
byte receivedBytesFromBT[numBytes];
byte receivedBytesFromSerial[numBytes];
byte numReceivedFromBT = 0;
byte numReceivedFromSerial = 0;

boolean newDataFromBT = false;
boolean newDataFromSerial = false;

//====================================================================================================================

void setup() {
    Serial.begin(9600);
    Serial.println("<Arduino is ready>");
    pinMode(D4, OUTPUT);  // this pin will pull the HC-05 pin 34 (key pin) HIGH to switch module to AT mode if needed
    digitalWrite(D4, HIGH);
    BTSerial.begin(9600);  // HC-05 default speed in AT command more
}

//====================================================================================================================

void loop() {
    recvFromBTWithStartEndMarkers();
    showReceivedDataFromBT();
    
    recvFromSerialMonitorWithStartEndMarkers();
    sendReceivedDataFromSerialMonitor();

    //sendBatteryInformation();
}

//====================================================================================================================

void recvFromBTWithStartEndMarkers() {
    static boolean recvFromBTInProgress = false;
    static byte ndx = 0;
    byte rc;
 
    while (BTSerial.available() > 0 && newDataFromBT == false) {
        rc = BTSerial.read();

        if (recvFromBTInProgress == true) {
            if (rc != MESSAGE_END) {
                receivedBytesFromBT[ndx] = rc;
                ndx++;
                if (ndx >= numBytes) {
                    ndx = numBytes - 1;
                }
            }
            else {
                receivedBytesFromBT[ndx] = '\0'; // terminate the string
                recvFromBTInProgress = false;
                numReceivedFromBT = ndx;  // save the number for use when printing
                ndx = 0;
                newDataFromBT = true;
            }
        }

        else if (rc == MESSAGE_START) {
            recvFromBTInProgress = true;
        }
    }
}

//====================================================================================================================

void showReceivedDataFromBT() {
    if (newDataFromBT == true) {
        for (byte n = 0; n < numReceivedFromBT; n++) {
            Serial.write(receivedBytesFromBT[n]);
        }
        Serial.println();
        newDataFromBT = false;
    }

}

//====================================================================================================================

void recvFromSerialMonitorWithStartEndMarkers(){
    static boolean recvFromSerialInProgress = false;
    static byte ndxSerial = 0;
    byte rc;
 
    while (Serial.available() > 0 && newDataFromSerial == false) {
        rc = Serial.read();

        if (recvFromSerialInProgress == true) {
            if (rc != MESSAGE_END) {
                receivedBytesFromSerial[ndxSerial] = rc;
                ndxSerial++;
                if (ndxSerial >= numBytes) {
                    ndxSerial = numBytes - 1;
                }
            }
            else {
                receivedBytesFromSerial[ndxSerial] = rc;
                ndxSerial++;
                receivedBytesFromSerial[ndxSerial] = '\0'; // terminate the string
                recvFromSerialInProgress = false;
                numReceivedFromSerial = ndxSerial;  // save the number for use when printing
                ndxSerial = 0;
                newDataFromSerial = true;
            }
        }
        else if (rc == MESSAGE_START) {
            recvFromSerialInProgress = true;
            receivedBytesFromSerial[ndxSerial] = rc;
            ndxSerial++;
        }
    }
  
}

//====================================================================================================================

void sendReceivedDataFromSerialMonitor(){
    if (newDataFromSerial == true) {
        for (byte n = 0; n < numReceivedFromSerial; n++) {
            BTSerial.write(receivedBytesFromSerial[n]);
            Serial.write(receivedBytesFromSerial[n]);
        }
        //BTSerial.write(receivedBytesFromSerial, numReceivedFromSerial);
        Serial.println();
        newDataFromSerial = false;
    }
}

//====================================================================================================================
//
//void sendMessage(byte[] message, int messageType){
//  switch(messageType){
//    case 1:
//      break;
//    default:
//      break;l
//  }
//}

//====================================================================================================================
//
//bool addStartEndMarkers(char[] message){
//  char* messageWithMarkers;
//  if(strlen(message) + 3 >= numBytes){
//    return false;
//  }else{
//    messageWithMarkers = new char[strlen(message) + 3];
//    strcpy(messageWithMarkers, (char)MESSAGE_START);
//    strcat(messageWithMarkers, message);
//    strcpy(messageWithMarkers, (char)MESSAGE_END);
//  }
//}
