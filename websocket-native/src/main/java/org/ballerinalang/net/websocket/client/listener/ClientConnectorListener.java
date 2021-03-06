/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.websocket.client.listener;

import org.ballerinalang.net.transport.contract.websocket.WebSocketBinaryMessage;
import org.ballerinalang.net.transport.contract.websocket.WebSocketCloseMessage;
import org.ballerinalang.net.transport.contract.websocket.WebSocketConnection;
import org.ballerinalang.net.transport.contract.websocket.WebSocketControlMessage;
import org.ballerinalang.net.transport.contract.websocket.WebSocketHandshaker;
import org.ballerinalang.net.transport.contract.websocket.WebSocketTextMessage;
import org.ballerinalang.net.websocket.WebSocketResourceDispatcher;
import org.ballerinalang.net.websocket.WebSocketUtil;
import org.ballerinalang.net.websocket.observability.WebSocketObservabilityUtil;
import org.ballerinalang.net.websocket.server.WebSocketConnectionInfo;

/**
 * ClientConnectorListener implements {@link ExtendedConnectorListener} interface directly.
 *
 * @since 0.93
 */
public class ClientConnectorListener implements ExtendedConnectorListener {
    private WebSocketConnectionInfo connectionInfo = null;

    public void setConnectionInfo(WebSocketConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public void onHandshake(WebSocketHandshaker webSocketHandshaker) {
        // Cannot reach this point as onHandshake and onOpen is not supported for WebSocket client service
    }

    @Override
    public void onMessage(WebSocketTextMessage webSocketTextMessage) {
        WebSocketResourceDispatcher.dispatchOnText(connectionInfo, webSocketTextMessage, false);
    }

    @Override
    public void onMessage(WebSocketBinaryMessage webSocketBinaryMessage) {
        WebSocketResourceDispatcher.dispatchOnBinary(connectionInfo, webSocketBinaryMessage, false);
    }

    @Override
    public void onMessage(WebSocketControlMessage webSocketControlMessage) {
        WebSocketResourceDispatcher.dispatchOnPingOnPong(connectionInfo, webSocketControlMessage, false);
    }

    @Override
    public void onMessage(WebSocketCloseMessage webSocketCloseMessage) {
        WebSocketResourceDispatcher.dispatchOnClose(connectionInfo, webSocketCloseMessage, false);
    }

    @Override
    public void onError(WebSocketConnection webSocketConnection, Throwable throwable) {
        WebSocketResourceDispatcher.dispatchOnError(connectionInfo, throwable, false);
    }

    @Override
    public void onIdleTimeout(WebSocketControlMessage controlMessage) {
        WebSocketResourceDispatcher.dispatchOnIdleTimeout(connectionInfo, false);
    }

    @Override
    public void onClose(WebSocketConnection webSocketConnection) {
        WebSocketObservabilityUtil.observeClose(connectionInfo);
        try {
            WebSocketUtil.setListenerOpenField(connectionInfo);
        } catch (IllegalAccessException e) {
            // Ignore as it is not possible have an Illegal access
        }
    }
}
