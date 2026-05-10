package com.ersim.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WebSocketBroadcaster, mocking SimpMessagingTemplate.
 *
 * TODO #Sruthi: implement all tests below using Mockito.
 */
class WebSocketBroadcasterTest {

    @Test
    void broadcastQueue_sendsToTopicQueue() {
        // TODO #Sruthi: verify(messagingTemplate).convertAndSend("/topic/queue", any())
        fail("not implemented");
    }

    @Test
    void broadcastRoomStatus_sendsToTopicRooms() {
        // TODO #Sruthi: verify(messagingTemplate).convertAndSend("/topic/rooms", any())
        fail("not implemented");
    }

    @Test
    void broadcastEvent_sendsToTopicEvents() {
        // TODO #Sruthi: verify(messagingTemplate).convertAndSend("/topic/events", any())
        fail("not implemented");
    }

    @Test
    void multipleSubscribers_allReceiveUpdates() {
        // TODO #Sruthi: integration check — 3+ STOMP clients all see broadcast
        fail("not implemented");
    }
}
