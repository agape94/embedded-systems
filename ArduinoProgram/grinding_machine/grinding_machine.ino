#include <SoftwareSerial.h>
#include <PID_v1.h>

// Bluetooth connection variables
#define PIN_INPUT 4
#define PIN_OUTPUT 9

//Define Variables we'll be connecting to
double Setpoint, Input, Output;

//Specify the links and initial tuning parameters
double Kp=0.25, Ki=1.1, Kd=0;

PID myPID(&Input, &Output, &Setpoint, Kp, Ki, Kd, DIRECT);
unsigned int D2 = 2;
unsigned int D3 = 3;
unsigned int D4 = 4;
unsigned int D5 = 5;
unsigned int D6 = 6;
unsigned int D9 = 9;
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

char SPEED_CHANGE_COMMAND[] = "spd_cmd";
char SPEED_VALUE[] = "spd_val";
char BATTERY_VALUE[] = "bat_val";

unsigned long oldTime = 0;
unsigned long newTime = 0;
unsigned long oldSendingTime = 0;
unsigned long newSendingTime = 0;
unsigned long interval = 500;

float diskRPM = 0;
int unlock = 1;


//====================================================================================================================

void setup() {
    Serial.begin(9600);
    Serial.println("<Arduino is ready>");
    pinMode(D4, INPUT);  // this pin will pull the HC-05 pin 34 (key pin) HIGH to switch module to AT mode if needed
    pinMode(D5, OUTPUT);
    pinMode(D6, OUTPUT);
    pinMode(D9, OUTPUT);
    digitalWrite(D5, HIGH);
    digitalWrite(D6, LOW);
    BTSerial.begin(9600);  // HC-05 default speed in AT command more

    //initialize the variables we're linked to
 Input = 0;
 Setpoint = 0;

 //turn the PID on
 myPID.SetMode(AUTOMATIC);
}

//====================================================================================================================

void loop() {


    //analogWrite(D9,receivedSpeedValue);

    PID_algorithm();

    
    recvFromBTWithStartEndMarkers();
    showReceivedDataFromBT();
    
    recvFromSerialMonitorWithStartEndMarkers();
    sendReceivedDataFromSerialMonitor();

    //sendMessage(SPEED_VALUE, convertIntToChar(267));

    measuringSpeed(2);
    //sendSpeedToApplication();

    Serial.println(diskRPM);
    

    //Serial.println(oldTime);
    //Serial.println();
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
        retrieveInformationFromCommand((char*)receivedBytesFromBT);
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
            //BTSerial.write(receivedBytesFromSerial[n]);
            Serial.write(receivedBytesFromSerial[n]);
        }
        //BTSerial.write(receivedBytesFromSerial, numReceivedFromSerial);
        //sendMessage(SPEED_VALUE, 267);
        Serial.println();
        newDataFromSerial = false;
    }
}

//====================================================================================================================

void sendMessage(char message_type[], float message_val){
  // Add the message type and the value, separated by semi-colon
  char* constructedMessage;
  Serial.println("Before if");
  constructedMessage = constructMessage(message_type, message_val);
  if(constructedMessage != 0){
    for (byte n = 0; n < strlen(constructedMessage); n++) {
      BTSerial.write(constructedMessage[n]);
      Serial.write(constructedMessage[n]);
    }
    Serial.println();
    delete[] constructedMessage;
  }else{
    return;    
  }
  
}

//====================================================================================================================

char* constructMessage(char message_type[], float message_val){
  String string_val = String(message_val);
  char* constructedMessage = 0;
  Serial.println("Value to send: " + string_val);
  int constructedMessageLength;
  String message_start = String((char)MESSAGE_START);
  String message_separator = String((char)SEPARATOR);
  String message_end = String((char)MESSAGE_END);
  String constructedString = String("");

  constructedString.concat(message_start);
  constructedString.concat(message_type);
  constructedString.concat(message_separator);
  constructedString.concat(string_val);
  constructedString.concat(message_end);
  constructedMessageLength = constructedString.length()+1;
  if(constructedMessageLength >= numBytes){
    return 0;
  }else{
    constructedMessage = new char[constructedMessageLength];
    constructedString.toCharArray(constructedMessage, constructedMessageLength);
    Serial.print("After toCharArray, string is: ");
    Serial.println(constructedMessage);
    Serial.print("After toCharArray, with length: ");
    Serial.println(strlen(constructedMessage));
    return constructedMessage;
  }
}

//====================================================================================================================

bool retrieveInformationFromCommand(char receivedMessage[]){
  String stringMessage = String(receivedMessage);
  String stringSeparator = String((char)SEPARATOR);
  String stringChangeSpeedCommand = String(SPEED_CHANGE_COMMAND);
  
  int separatorIdx = stringMessage.indexOf(stringSeparator);
  String message_type = stringMessage.substring(0, separatorIdx);
  String message_val = stringMessage.substring(separatorIdx + 1 , stringMessage.length());
  
  if(message_type.equals(stringChangeSpeedCommand)){
    receivedSpeedValue = message_val.toInt();
    Serial.write("The speed value is: ");
    Serial.print(receivedSpeedValue);
    Serial.write("\n\n");
    return true;
  }else{
    Serial.write("\nERROR: Unknown command message!\n");
  }
}

//====================================================================================================================

void convertIntToChar(int value, char* cstr){
  cstr = new char[16];
  itoa(value, cstr, 10);
}

//====================================================================================================================

void measuringSpeed(int magnets){
if(digitalRead(D4) == LOW && unlock == 1)
    {
     oldTime = newTime;
     newTime = millis();
    
     diskRPM = (1.0/magnets)/((newTime-oldTime)/1000.0/60.0);

     unlock  = 0;
    }

    if(digitalRead(D4) == HIGH && unlock == 0)
    {
      unlock = 1;
    }  
}

//====================================================================================================================

void PID_algorithm(){
  Setpoint = receivedSpeedValue;
  Input = diskRPM;
  myPID.Compute();
  analogWrite(D9,Output);
}

//====================================================================================================================

void sendSpeedToApplication(){
  newSendingTime = millis();
  if(newSendingTime - oldSendingTime >= interval){
    sendMessage(SPEED_VALUE, diskRPM);
    oldSendingTime = newSendingTime;
  }
}

// Functions to update the speed value of the motor and the battery level (if possible).
