/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package io.ballerina.stdlib.websocket.testutils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple WebSocket frame handler for testing.
 */
public class WebSocketRemoteServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketRemoteServerFrameHandler.class);
    private WebSocketHttpRequestHandler httpRequestHandler;

    public WebSocketRemoteServerFrameHandler(WebSocketHttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channel is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("channel is inactive");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ChannelFuture f = ctx.channel().writeAndFlush(new TextWebSocketFrame("Connected"));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            // Echos the same text
            String text = ((TextWebSocketFrame) frame).text();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(frame.isFinalFragment(), 0, text));
        } else if (frame instanceof BinaryWebSocketFrame) {
            ByteBuffer bufferCopy = cloneBuffer(frame.content().nioBuffer());
            ctx.writeAndFlush(new BinaryWebSocketFrame(frame.isFinalFragment(), 0,
                                                       Unpooled.wrappedBuffer(bufferCopy)));
        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
        } else if (frame instanceof ContinuationWebSocketFrame) {
            ByteBuffer clonedBuffer = cloneBuffer(frame.content().nioBuffer());
            ctx.writeAndFlush(new ContinuationWebSocketFrame(frame.isFinalFragment(), 0,
                                                             Unpooled.wrappedBuffer(clonedBuffer)));
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception Caught: " + cause.getMessage());
        ctx.close();
    }

    private ByteBuffer cloneBuffer(ByteBuffer originalBuffer) {
        ByteBuffer bufferCopy = ByteBuffer.allocate(originalBuffer.capacity());
        originalBuffer.rewind();
        bufferCopy.put(originalBuffer);
        bufferCopy.flip();
        return bufferCopy;
    }
}
