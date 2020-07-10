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
import com.orange.lo.sample.lo.model.CommandRequest;
import com.orange.lo.sample.lo.model.CommandResponse;
import com.orange.lo.sample.lo.model.DataMessage;
import com.orange.lo.sample.lo.model.NodeStatus;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.UUID;

public class ExternalConnectorClient {

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
        }
        if (parameters.getMessageCallback() != null) {
            receiveCommands();
        }
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
    }

    public void sendStatus(String nodeId, NodeStatus nodeStatus) {
        MqttMessage msg = prepareMqttMessage(nodeStatus);
        String topic = String.format(parameters.getStatusTopicTemplate(), nodeId);
        publish(topic, msg);
    }

    public void sendMessage(String nodeId, DataMessage dataMessage) {
        MqttMessage msg = prepareMqttMessage(dataMessage);
        String topic = String.format(parameters.getDataTopicTemplate(), nodeId);
        publish(topic, msg);
    }

    private MqttMessage prepareMqttMessage(Object dataMessage) {
        try {
            String payload = objectMapper.writeValueAsString(dataMessage);
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

    public void sendCommandResponse(CommandResponse commandResponse) {
        MqttMessage msg = prepareMqttMessage(commandResponse);
        publish(parameters.getCommandResponseTopic(), msg);
    }

    private void receiveCommands() {
        try {
            mqttClient.subscribe(parameters.getCommandRequestTopic(), parameters.getMessageQos(), this::messageArrived);
        } catch (MqttException e) {
            throw new LoMqttException(e);
        }
    }

    private void messageArrived(String topic, MqttMessage mqttMessage) throws IOException {
        CommandRequest commandRequest = objectMapper.readValue(mqttMessage.getPayload(), CommandRequest.class);
        MessageCallback messageCallback = parameters.getMessageCallback();
        messageCallback.onMessage(commandRequest.getNodeId(), commandRequest);
    }

    private MqttConnectOptions getMqttConnectionOptions() {
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName(parameters.getUser());
        opts.setPassword(parameters.getApiKey().toCharArray());
        opts.setAutomaticReconnect(parameters.isAutomaticReconnect());
        return opts;
    }

}
