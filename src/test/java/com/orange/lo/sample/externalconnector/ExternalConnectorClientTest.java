package com.orange.lo.sample.externalconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.exceptions.ParseException;
import com.orange.lo.sample.lo.model.*;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static com.orange.lo.sample.externalconnector.ExternalConnectorParameters.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExternalConnectorClientTest {

    private static final String EX_CONNECTOR_NODE_ID = "x-con-library-device-node-id";

    @Mock
    private MqttClient mqttClient;
    private ExternalConnectorParameters externalConnectorParameters;
    private ExternalConnectorClient externalConnectorClient;

    @BeforeEach
    void setUp() throws MqttException {
        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .build();
        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();
    }

    @Test
    void shouldCallConnectFromMqttClientWhenConnectIsCalled() throws MqttException {
        verify(mqttClient, times(1)).connect(any(MqttConnectOptions.class));
    }

    @Test
    void shouldCallDisconnectFromMqttClientWhenDisconnectIsCalled() throws MqttException {
        externalConnectorClient.disconnect();

        verify(mqttClient, times(1)).disconnect();
    }

    @Test
    void shouldNotSubscribeCommandRequestTopicWhenMessageCallbackIsNotSet() throws MqttException {
        verify(mqttClient, times(0)).subscribe(eq(DEFAULT_COMMAND_REQUEST_TOPIC), eq(DEFAULT_MESSAGE_QOS), any(IMqttMessageListener.class));
    }

    @Test
    void shouldSubscribeCommandRequestTopicWhenMessageCallbackIsSet() throws MqttException {
        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .messageCallback((nodeId, commandRequest) -> {
                })
                .build();

        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();

        verify(mqttClient, times(1)).subscribe(eq(DEFAULT_COMMAND_REQUEST_TOPIC), eq(DEFAULT_MESSAGE_QOS), any(IMqttMessageListener.class));
    }

    @Test
    void shouldSubscribeToChangedCommandRequestTopicWhenMessageCallbackIsSetAndCommandRequestTopicWasChangedInParameters() throws MqttException {
        String commandRequestTopic = "new/command/request/topic";
        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .messageCallback((nodeId, commandRequest) -> {
                })
                .commandRequestTopic(commandRequestTopic)
                .build();

        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();

        verify(mqttClient, times(1)).subscribe(eq(commandRequestTopic), eq(DEFAULT_MESSAGE_QOS), any(IMqttMessageListener.class));
    }

    @Test
    void shouldSendNodeStatusToDefaultStatusTopicAsSelectedNodeIdWhenStatusTopicWasNotChangedInParameters() throws MqttException {
        String expectedTopic = String.format(DEFAULT_STATUS_TOPIC_TEMPLATE, EX_CONNECTOR_NODE_ID);
        NodeStatus nodeStatus = getNodeStatus();
        MqttMessage expectedMessage = toMqttMessage(nodeStatus);

        externalConnectorClient.sendStatus(EX_CONNECTOR_NODE_ID, nodeStatus);

        verify(mqttClient, times(1)).publish(eq(expectedTopic), hasSamePayload(expectedMessage));
    }

    @Test
    void shouldSendNodeStatusToChangedStatusTopicAsSelectedNodeIdWhenStatusTopicWasChangedInParameters() throws MqttException {
        String statusTopic = "/%s/new/status/topic";
        String expectedTopic = String.format(statusTopic, EX_CONNECTOR_NODE_ID);
        NodeStatus nodeStatus = getNodeStatus();
        MqttMessage expectedMessage = toMqttMessage(nodeStatus);

        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .statusTopicTemplate(statusTopic)
                .build();

        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();
        externalConnectorClient.sendStatus(EX_CONNECTOR_NODE_ID, nodeStatus);

        verify(mqttClient, times(1)).publish(eq(expectedTopic), hasSamePayload(expectedMessage));
    }

    @Test
    void shouldSendMessageToDefaultDataTopicTemplateAsSelectedNodeIdWhenDataTopicTemplateWasNotChangedInParameters() throws MqttException {
        String expectedTopic = String.format(DEFAULT_DATA_TOPIC_TEMPLATE, EX_CONNECTOR_NODE_ID);
        DataMessage dataMessage = getDataMessage();
        MqttMessage expectedMessage = toMqttMessage(dataMessage);

        externalConnectorClient.sendMessage(EX_CONNECTOR_NODE_ID, dataMessage);

        verify(mqttClient, times(1)).publish(eq(expectedTopic), hasSamePayload(expectedMessage));
    }

    @Test
    void shouldSendMessageToChangedDataTopicTemplateAsSelectedNodeIdWhenDataTopicTemplateWasChangedInParameters() throws MqttException {
        String dataTopicTemplate = "/%s/new/data/topic";
        String expectedTopic = String.format(dataTopicTemplate, EX_CONNECTOR_NODE_ID);
        DataMessage dataMessage = getDataMessage();
        MqttMessage expectedMessage = toMqttMessage(dataMessage);

        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .dataTopicTemplate(dataTopicTemplate)
                .build();

        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();
        externalConnectorClient.sendMessage(EX_CONNECTOR_NODE_ID, dataMessage);

        verify(mqttClient, times(1)).publish(eq(expectedTopic), hasSamePayload(expectedMessage));
    }

    @Test
    void shouldSendCommandResponseToDefaultCommandResponseTopicWhenCommandResponseTopicWasNotChangedInParameters() throws MqttException {
        CommandResponse commandResponse = getCommandResponse();
        MqttMessage expectedMessage = toMqttMessage(commandResponse);

        externalConnectorClient.sendCommandResponse(commandResponse);

        verify(mqttClient, times(1)).publish(eq(DEFAULT_COMMAND_RESPONSE_TOPIC), hasSamePayload(expectedMessage));
    }

    @Test
    void shouldSendCommandResponseToChangedCommandResponseTopicWhenCommandResponseTopicWasChangedInParameters() throws MqttException {
        String commandResponseTopic = "new/command/response/topic";
        CommandResponse commandResponse = getCommandResponse();
        MqttMessage expectedMessage = toMqttMessage(commandResponse);

        externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(ExternalConnectorParametersTest.HOSTNAME)
                .apiKey(ExternalConnectorParametersTest.API_KEY)
                .commandResponseTopic(commandResponseTopic)
                .build();

        externalConnectorClient = new ExternalConnectorClient(externalConnectorParameters, mqttClient);
        externalConnectorClient.connect();
        externalConnectorClient.sendCommandResponse(commandResponse);

        verify(mqttClient, times(1)).publish(eq(commandResponseTopic), hasSamePayload(expectedMessage));
    }

    private CommandResponse getCommandResponse() {
        String commandId = "command-id";
        return new CommandResponse(commandId, EX_CONNECTOR_NODE_ID);
    }

    private NodeStatus getNodeStatus() {
        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus(Status.ONLINE);
        nodeStatus.setCapabilities(new NodeStatus.Capabilities(true));
        return nodeStatus;
    }

    private DataMessage getDataMessage() {
        Value payload = new Value("15;25");
        Metadata metadata = new Metadata("test_csv");
        DataMessage dataMessage = new DataMessage();
        dataMessage.setValue(payload);
        dataMessage.setMetadata(metadata);
        return dataMessage;
    }

    private MqttMessage toMqttMessage(Object dataMessage) {
        try {
            String payload = new ObjectMapper().writeValueAsString(dataMessage);
            MqttMessage msg = new MqttMessage();
            msg.setQos(externalConnectorParameters.getMessageQos());
            msg.setPayload(payload.getBytes());
            return msg;
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
    }

    private static MqttMessage hasSamePayload(MqttMessage expectedMessage) {
        return argThat(givenMessage -> {
            try {
                byte[] expectedPayload = expectedMessage.getPayload();
                byte[] givenPayload = givenMessage.getPayload();
                return Arrays.equals(givenPayload, expectedPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }
}