#include <SoftwareSerial.h>

unsigned int D2 = 2;
unsigned int D3 = 3;
unsigned int D4 = 4;
SoftwareSerial BTSerial(D3,D2); // RX: port D2, TX port D3
byte MESSAGE_START = 0x3C;
byte MESSAGE_END = 0x3E;

//const byte numChars = 32;
//char receivedChars[numChars];

const byte numBytes = 32;
byte receivedBytes[numBytes];
byte numReceived = 0;

boolean newData = false;

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
    recvWithStartEndMarkers();
    showNewData();
}

//====================================================================================================================

void recvWithStartEndMarkers() {
    static boolean recvInProgress = false;
    static byte ndx = 0;
    byte rc;
 
    while (BTSerial.available() > 0 && newData == false) {
        rc = BTSerial.read();

        if (recvInProgress == true) {
            if (rc != MESSAGE_END) {
                receivedBytes[ndx] = rc;
                ndx++;
                if (ndx >= numBytes) {
                    ndx = numBytes - 1;
                }
            }
            else {
                receivedBytes[ndx] = '\0'; // terminate the string
                recvInProgress = false;
                numReceived = ndx;  // save the number for use when printing
                ndx = 0;
                newData = true;
            }
        }

        else if (rc == MESSAGE_START) {
            recvInProgress = true;
        }
    }
}

//====================================================================================================================

void showNewData() {
    if (newData == true) {
//        Serial.print("This just in (HEX values)... ");
        for (byte n = 0; n < numReceived; n++) {
//            Serial.print(receivedBytes[n], HEX);
            Serial.write(receivedBytes[n]);
//            Serial.print(' ');
        }
//        char theReceivedString[numReceived];
        Serial.println();
        newData = false;
    }

}

//====================================================================================================================
