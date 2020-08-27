package com.orange.lo.sample.externalconnector;

import com.orange.lo.sample.exceptions.ExternalConnectorParametersException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalConnectorParametersTest {

    public static final String API_KEY = "abcDEfgH123I";
    public static final String HOSTNAME = "ssl://liveobjects.orange-business.com:8883";

    @Test
    void shouldSetDefaultMessageQosWhenMessageQosWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_MESSAGE_QOS, externalConnectorParameters.getMessageQos());
    }

    @Test
    void shouldChangeMessageQosWhenMessageQosWasSet() {
        int messageQos = 2;
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .messageQos(messageQos)
                .build();

        assertEquals(messageQos, externalConnectorParameters.getMessageQos());
    }

    @Test
    void shouldSetDefaultUserWhenUserWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_USER, externalConnectorParameters.getUser());
    }

    @Test
    void shouldChangeDefaultUserWhenUserWasSet() {
        String user = "newUserName";
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .user(user)
                .build();

        assertEquals(user, externalConnectorParameters.getUser());
    }

    @Test
    void shouldThrowExternalConnectorParametersExceptionWhenApiKeyIsNotSet() {
        ExternalConnectorParameters.ExternalConnectorParametersBuilder parametersBuilder = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME);
        Assertions.assertThrows(ExternalConnectorParametersException.class, parametersBuilder::build);
    }

    @Test
    void shouldSetApiKeyWhenCreatingParameters() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(API_KEY, externalConnectorParameters.getApiKey());
    }

    @Test
    void shouldSetHostnameWhenCreatingParameters() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(HOSTNAME, externalConnectorParameters.getHostname());
    }

    @Test
    void shouldThrowExternalConnectorParametersExceptionWhenHostnameIsNotSet() {
        ExternalConnectorParameters.ExternalConnectorParametersBuilder parametersBuilder = ExternalConnectorParameters.builder()
                .apiKey(API_KEY);
        Assertions.assertThrows(ExternalConnectorParametersException.class, parametersBuilder::build);
    }

    @Test
    void shouldSetMessageCallbackWhenCreatingParameters() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .messageCallback((commandRequest) -> null)
                .build();

        assertNotNull(externalConnectorParameters.getMessageCallback());
    }

    @Test
    void shouldSetDefaultAutomaticReconnectValueWhenAutomaticReconnectValueNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertFalse(externalConnectorParameters.isAutomaticReconnect());
    }

    @Test
    void shouldChangeDefaultAutomaticReconnectValueWhenAutomaticReconnectValueWasSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .automaticReconnect(true)
                .build();

        assertTrue(externalConnectorParameters.isAutomaticReconnect());
    }

    @Test
    void shouldSetDefaultCommandRequestTopicWhenCommandRequestTopicWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_COMMAND_REQUEST_TOPIC, externalConnectorParameters.getCommandRequestTopic());
    }

    @Test
    void shouldChangeDefaultCommandRequestTopicWhenCommandRequestTopicWasSet() {
        String commandRequestTopic = "new/command/request/topic";
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .commandRequestTopic(commandRequestTopic)
                .build();

        assertEquals(commandRequestTopic, externalConnectorParameters.getCommandRequestTopic());
    }

    @Test
    void shouldSetDefaultCommandResponseTopicWhenCommandResponseTopicWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_COMMAND_RESPONSE_TOPIC, externalConnectorParameters.getCommandResponseTopic());
    }

    @Test
    void shouldChangeDefaultCommandResponseTopicWhenCommandResponseTopicWasSet() {
        String commandResponseTopic = "new/command/response/topic";
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .commandResponseTopic(commandResponseTopic)
                .build();

        assertEquals(commandResponseTopic, externalConnectorParameters.getCommandResponseTopic());
    }

    @Test
    void shouldSetDefaultDataTopicTemplateWhenDataTopicTemplateWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_DATA_TOPIC_TEMPLATE, externalConnectorParameters.getDataTopicTemplate());
    }

    @Test
    void shouldChangeDefaultDataTopicTemplateWhenDataTopicTemplateWasSet() {
        String dataTopicTemplate = "/%s/new/data/topic/template";
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .dataTopicTemplate(dataTopicTemplate)
                .build();

        assertEquals(dataTopicTemplate, externalConnectorParameters.getDataTopicTemplate());
    }

    @Test
    void shouldSetDefaultStatusTopicTemplateWhenStatusTopicTemplateWasNotSet() {
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .build();

        assertEquals(ExternalConnectorParameters.DEFAULT_STATUS_TOPIC_TEMPLATE, externalConnectorParameters.getStatusTopicTemplate());
    }

    @Test
    void shouldChangeDefaultStatusTopicTemplateWhenStatusTopicTemplateWasSet() {
        String statusTopicTemplate = "/%s/new/status/topic/template";
        ExternalConnectorParameters externalConnectorParameters = ExternalConnectorParameters.builder()
                .hostname(HOSTNAME)
                .apiKey(API_KEY)
                .statusTopicTemplate(statusTopicTemplate)
                .build();

        assertEquals(statusTopicTemplate, externalConnectorParameters.getStatusTopicTemplate());
    }

}