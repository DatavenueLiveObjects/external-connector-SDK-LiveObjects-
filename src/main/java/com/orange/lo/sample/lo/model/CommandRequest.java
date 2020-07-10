/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.lo.model;

public class CommandRequest {

    private String id;

    private String nodeId;

    private String ackMode;

    private CommandRequestValue value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getAckMode() {
        return ackMode;
    }

    public void setAckMode(String ackMode) {
        this.ackMode = ackMode;
    }

    public CommandRequestValue getValue() {
        return value;
    }

    public void setValue(CommandRequestValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CommandRequest [id=" + id + ", nodeId=" + nodeId + ", ackMode=" + ackMode + ", value=" + value + "]";
    }
}