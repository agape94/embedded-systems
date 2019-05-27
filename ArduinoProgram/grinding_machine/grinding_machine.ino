#include <SoftwareSerial.h>

// Bluetooth connection variables
unsigned int D2 = 2;
unsigned int D3 = 3;
unsigned int D4 = 4;
SoftwareSerial BTSerial(D3,D2); // RX: port D2, TX port D3
byte MESSAGE_START = 0x3C; // <
byte MESSAGE_END = 0x3E; // >
byte SEPARATOR = 0x3B; // ;

const byte numBytes = 64;
byte receivedBytesFromBT[numBytes];
byte receivedBytesFromSerial[numBytes];
byte numReceivedFromBT = 0;
byte numReceivedFromSerial = 0;

boolean newDataFromBT = false;
boolean newDataFromSerial = false;

/* Speed variables and message types
 * Communication protocol:
 * < message_type ; value >
 * */
int receivedSpeedValue = 0;
int currentSpeedValue = 0;
int currentBatteryLevel = 0;

char[] SPEED_CHANGE_COMMAND = "spd_cmd";
char[] SPEED_VALUE = "spd_val";
char[] BATTERY_VALUE = "bat_val";

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

void sendMessage(char[] message_type, char[] message_val){
  // Add the message type and the value, separated by semi-colon
  char* constructedMessage;
  if(constructMessage(message_type, message_val, constructedMessage) == true){
    for (byte n = 0; n < numReceivedFromSerial; n++) {
      BTSerial.write(constructedMessage[n]);
    }
    delete[] constructedMessage;
  }else{
    return;    
  }
  
}

//====================================================================================================================

bool constructMessage(char[] message_type, char[] message_val, char* constructedMessage){
  int constructedMessageLength = strlen(message_type) + strlen(message_val) + strlen(MESSAGE_START) + strlen(MESSAGE_END) + strlen(SEPARATOR) + 1; // we need +1 because of \n
  if(constructedMessageLength >= numBytes){
    return false;
  }else{
    constructedMessage = new char[constructedMessageLength];
    strcpy(constructedMessage, (char)MESSAGE_START);
    strcat(constructedMessage, message_type);
    strcpy(constructedMessage, (char) SEPARATOR);
    strcat(constructedMessage, message_val);
    strcpy(constructedMessage, (char)MESSAGE_END);
    return true;
  }
}

//====================================================================================================================

bool retrieveInformationFromCommand(char[] receivedMessage){
  String stringMessage = String(receivedMessage);
  String stringStart = String((char)MESSAGE_START);
  String stringEnd = String((char)MESSAGE_END);
  String stringSeparator = String((char)SEPARATOR);
  String stringChangeSpeedCommand = String(SPEED_CHANGE_COMMAND);
  
  if(stringMessage.startsWith(stringStart) && stringMessage.endsWith(stringEnd)){
    int separatorIdx = stringMessage.indexOf(stringSeparator);
    String message_type = stringMessage.substring(1, separatorIdx - 1);
    String message_val = stringMessage.substring(separatorIdx + 1 , stringMessage.length()-1);
    
    if(message_type.equals(stringChangeSpeedCommand)){
      receivedSpeedValue = message_val.toInt();
      return true;
    }
  }else{
    return false;
  }
}

//====================================================================================================================

// Functions to update the speed value of the motor and the battery level (if possible).














