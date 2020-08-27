/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.externalconnector;

import com.orange.lo.sample.exceptions.ExternalConnectorParametersException;

public final class ExternalConnectorParameters {

    public static final String DEFAULT_USER = "connector";
    public static final int DEFAULT_MESSAGE_QOS = 1;
    public static final String DEFAULT_COMMAND_RESPONSE_TOPIC = "connector/v1/responses/command";
    public static final String DEFAULT_COMMAND_REQUEST_TOPIC = "connector/v1/requests/command";
    public static final String DEFAULT_DATA_TOPIC_TEMPLATE = "connector/v1/nodes/%s/data";
    public static final String DEFAULT_STATUS_TOPIC_TEMPLATE = "connector/v1/nodes/%s/status";

    private final int messageQos;
    private final String user;
    private final String apiKey;
    private final String hostname;
    private final String dataTopicTemplate;
    private final String statusTopicTemplate;
    private final String commandResponseTopic;
    private final String commandRequestTopic;
    private final MessageCallback messageCallback;
    private final boolean automaticReconnect;

    private ExternalConnectorParameters(ExternalConnectorParametersBuilder builder) {
        this.messageQos = builder.messageQos;
        this.user = builder.user;
        this.apiKey = builder.apiKey;
        this.hostname = builder.hostname;
        this.dataTopicTemplate = builder.dataTopicTemplate;
        this.statusTopicTemplate = builder.statusTopicTemplate;
        this.commandResponseTopic = builder.commandResponseTopic;
        this.commandRequestTopic = builder.commandRequestTopic;
        this.messageCallback = builder.messageCallback;
        this.automaticReconnect = builder.automaticReconnect;
    }

    public static ExternalConnectorParametersBuilder builder() {
        return new ExternalConnectorParametersBuilder();
    }

    public int getMessageQos() {
        return messageQos;
    }

    public String getUser() {
        return user;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDataTopicTemplate() {
        return dataTopicTemplate;
    }

    public String getStatusTopicTemplate() {
        return statusTopicTemplate;
    }

    public String getCommandResponseTopic() {
        return commandResponseTopic;
    }

    public String getCommandRequestTopic() {
        return commandRequestTopic;
    }

    public MessageCallback getMessageCallback() {
        return messageCallback;
    }

    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

    public static final class ExternalConnectorParametersBuilder {
        private int messageQos = DEFAULT_MESSAGE_QOS;
        private String user = DEFAULT_USER;
        private String apiKey;
        private String hostname;
        private String dataTopicTemplate = DEFAULT_DATA_TOPIC_TEMPLATE;
        private String statusTopicTemplate = DEFAULT_STATUS_TOPIC_TEMPLATE;
        private String commandResponseTopic = DEFAULT_COMMAND_RESPONSE_TOPIC;
        private String commandRequestTopic = DEFAULT_COMMAND_REQUEST_TOPIC;
        private boolean automaticReconnect;
        private MessageCallback messageCallback;

        public ExternalConnectorParametersBuilder messageQos(int messageQos) {
            this.messageQos = messageQos;
            return this;
        }

        public ExternalConnectorParametersBuilder user(String user) {
            this.user = user;
            return this;
        }

        public ExternalConnectorParametersBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public ExternalConnectorParametersBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        /**
         * Sets the value of the data topic template.
         * The value should contain the %s  parameter, which will be replaced by nodeId.
         * Example value: connector/v1/nodes/%s/data
         *
         * @param dataTopicTemplate New data topic template
         * @return External connector parameters builder
         */
        public ExternalConnectorParametersBuilder dataTopicTemplate(String dataTopicTemplate) {
            this.dataTopicTemplate = dataTopicTemplate;
            return this;
        }

        /**
         * Sets the value of the status topic template.
         * The value should contain the %s  parameter, which will be replaced by nodeId.
         * Example value: connector/v1/nodes/%s/status
         *
         * @param statusTopicTemplate New status topic template
         * @return External connector parameters builder
         */
        public ExternalConnectorParametersBuilder statusTopicTemplate(String statusTopicTemplate) {
            this.statusTopicTemplate = statusTopicTemplate;
            return this;
        }

        public ExternalConnectorParametersBuilder commandResponseTopic(String commandResponseTopic) {
            this.commandResponseTopic = commandResponseTopic;
            return this;
        }

        public ExternalConnectorParametersBuilder commandRequestTopic(String commandRequestTopic) {
            this.commandRequestTopic = commandRequestTopic;
            return this;
        }

        public ExternalConnectorParametersBuilder automaticReconnect(boolean automaticReconnect) {
            this.automaticReconnect = automaticReconnect;
            return this;
        }

        public ExternalConnectorParametersBuilder messageCallback(MessageCallback messageCallback) {
            this.messageCallback = messageCallback;
            return this;
        }

        public ExternalConnectorParameters build() {
            validate();
            return new ExternalConnectorParameters(this);
        }

        private void validate() {
            if (this.apiKey == null || this.apiKey.trim().length() == 0 || this.hostname == null || this.hostname.trim().length() == 0) {
                throw new ExternalConnectorParametersException("Api key and hostname are required");
            }
        }
    }
}
