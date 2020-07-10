## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Requirements](#requirements)
* [Build](#build)
* [Examples](#examples)

## General info
This repository contains everything you need to create the external connector mode connection with Live Objects. This project is intended for Live Objects users wishing to explore the external connector mode integration patterns. When you will connect devices that can not use a native connectivity in Live Objects, you can use this library.

Main features are:
* publish data and devices status
* manage devices commands: subscribe to command requests and publish command responses

## Technologies
* Java 8
* Eclipse Paho 1.2.4
* Jackson 2.9.0
* Jackson Databind 2.9.9.3

## Requirements
In order to use the x-connector-library you need to have:
* **Live Objects account with external connector API key** (API key generation is described in the [user guide](https://liveobjects.orange-business.com/cms/app/uploads/EN_User-guide-Live-Objects-4.pdf#%5B%7B%22num%22%3A190%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C68%2C574%2C0%5D)),
* **A device registered in Live Objects with an external connector interface**
* **Java SE Development Kit 8 installed**
* **Apache Maven installed**

## Build
If you want to build x-connector-library, please first clone this repository, then:  
```
mvn clean package
```
The jar file built in this way should be attached to the project as a library. 

## Examples

#### Creating an ExternalConnectorClient
To create an `ExternalConnectorClient` instance and create a connection to Live Objects, you must create an `ExternalConnectorParameters` instance and pass it to `ExternalConnectorClient` constructor:
```
ExternalConnectorParameters parameters = ExternalConnectorParameters.builder()
                .hostname("ssl://liveobjects.orange-business.com:8883")
                .apiKey("abcDEfgH123I")
                .build();
ExternalConnectorClient externalConnectorClient = new ExternalConnectorClient(parameters);
```
Note that an API key and host name are required.

#### Opening the connection

You can use the sample code to open the connection:
```
externalConnectorClient.connect();
```

#### NodeStatus publication
A NodeStatus publication allows to set the ONLINE/OFFLINE status of the device and its capacity to receive or not command requests. To send the NodeStatus to Live Objects, you can use the sample code:
```
NodeStatus nodeStatus = new NodeStatus();
nodeStatus.setStatus(Status.ONLINE);
nodeStatus.setCapabilities(new NodeStatus.Capabilities(true));
externalConnectorClient.sendStatus(nodeStatus);
```

#### Data message publication
A DataMessage publication allows to send a DataMessage on behalf of a specific device. In order to use the decoding capability of LiveObjects, a DataMessage must contains additional `value.payload` and `metadata.encoding` fields. To send encoded DataMessage to Live Objects, you can use the sample code:
```
Value value = new Value("15;25");
Metadata metadata = new Metadata("test_csv");
DataMessage dataMessage = new DataMessage();
dataMessage.setValue(value);
dataMessage.setMetadata(metadata);
externalConnectorClient.sendMessage(dataMessage);
```

#### Commands
A command request is a downlink message that Live Objects sends to the device, with acknowledgement mechanism.

In order to receive all command requests targeting your devices, you should implement your own message handling class implementing `MessageCallback`: 
```
public class MyMessageCallback implements MessageCallback {
    @Override
    public void onMessage(String nodeId, CommandRequest commandRequest) {

    }
}
```
And use it to create an `ExternalConnectorClient` configuration:
```
ExternalConnectorParameters parameters = ExternalConnectorParameters.builder()
                .hostname("ssl://liveobjects.orange-business.com:8883")
                .apiKey("abcDEfgH123I")
                .messageCallback(new MyMessageCallback())
                .build();
ExternalConnectorClient externalConnectorClient = new ExternalConnectorClient(parameters);
```
In order to reply to a command requests, you must create an `CommandResponse` with id of the command you want to respond to:
```
CommandResponse commandResponse = new CommandResponse(commandId, exConnectorNodeId);
externalConnectorClient.sendCommandResponse(commandResponse);
```

#### Closing the connection

You can use the sample code to close the connection:
```
externalConnectorClient.disconnect();
```