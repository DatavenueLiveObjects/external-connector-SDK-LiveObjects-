/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.externalconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.exceptions.LoMqttException;
import com.orange.lo.sample.exceptions.ParseException;
import com.orange.lo.sample.lo.model.*;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class ExternalConnectorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalConnectorClient.class);

    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final ExternalConnectorParameters parameters;

    public ExternalConnectorClient(ExternalConnectorParameters parameters, IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
        this.parameters = parameters;
        this.objectMapper = new ObjectMapper();
    }

    public ExternalConnectorClient(ExternalConnectorParameters parameters) throws MqttException {
        this(parameters, new MqttClient(parameters.getHostname(), UUID.randomUUID().toString()));
    }

    public void connect() throws MqttException {
        if (!mqttClient.isConnected()) {
            MqttConnectOptions opts = getMqttConnectionOptions();
            mqttClient.connect(opts);
            LOGGER.info("Successfully connected to Live Objects.");
        }
        if (parameters.getMessageCallback() != null) {
            receiveCommands();
        }
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
        LOGGER.info("Successfully disconnected.");
    }

    public void sendStatus(String nodeId, NodeStatus nodeStatus) {
        MqttMessage msg = prepareMqttMessage(nodeStatus);
        String topic = String.format(parameters.getStatusTopicTemplate(), nodeId);
        publish(topic, msg);
        LOGGER.debug("Status for nodeId {} has been sent successfully.", nodeId);
    }

    public void sendMessage(String nodeId, DataMessage dataMessage) {
        MqttMessage msg = prepareMqttMessage(dataMessage);
        String topic = String.format(parameters.getDataTopicTemplate(), nodeId);
        publish(topic, msg);
        LOGGER.debug("Message for nodeId {} has been sent successfully.", nodeId);
    }

    private MqttMessage prepareMqttMessage(Object message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            MqttMessage msg = new MqttMessage();
            msg.setQos(parameters.getMessageQos());
            msg.setPayload(payload.getBytes());
            return msg;
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
    }

    private void publish(String topic, MqttMessage msg) {
        try {
            mqttClient.publish(topic, msg);
        } catch (MqttException e) {
            throw new LoMqttException(e);
        }
    }

    private void sendCommandResponse(CommandResponse commandResponse) {
        MqttMessage msg = prepareMqttMessage(commandResponse);
        publish(parameters.getCommandResponseTopic(), msg);
    }

    private void receiveCommands() {
        try {
            mqttClient.subscribe(parameters.getCommandRequestTopic(), parameters.getMessageQos(), this::messageArrived);
            LOGGER.info("Command request topic was subscribed successfully.");
        } catch (MqttException e) {
            throw new LoMqttException(e);
        }
    }

    private void messageArrived(String topic, MqttMessage mqttMessage) throws IOException {
        CommandRequest commandRequest = objectMapper.readValue(mqttMessage.getPayload(), CommandRequest.class);
        LOGGER.debug("Command arrived. Topic: {}, Id: {}, nodeId: {}.", topic, commandRequest.getId(), commandRequest.getNodeId());
        MessageCallback messageCallback = parameters.getMessageCallback();
        Object response = messageCallback.onMessage(commandRequest);
        if (isAckModeNone(commandRequest)) {
            LOGGER.debug("AckMode is set to None - no need to send a response.");
        } else {
            CommandResponse commandResponse = new CommandResponse(commandRequest.getId(), commandRequest.getNodeId());
            commandResponse.setResponse(response);
            sendCommandResponse(commandResponse);
            LOGGER.debug("Response was sent successfully. Command Id: {}, nodeId: {}.", commandRequest.getId(), commandRequest.getNodeId());
        }
    }

    private boolean isAckModeNone(CommandRequest commandRequest) {
        AcknowledgementMode ackMode = commandRequest.getAckMode();
        return AcknowledgementMode.NONE.equals(ackMode);
    }

    private MqttConnectOptions getMqttConnectionOptions() {
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName(parameters.getUser());
        opts.setPassword(parameters.getApiKey().toCharArray());
        opts.setAutomaticReconnect(parameters.isAutomaticReconnect());
        return opts;
    }

}
