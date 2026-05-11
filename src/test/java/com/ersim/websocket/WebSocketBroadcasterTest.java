package com.ersim.websocket;

import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.EventType;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for WebSocketBroadcaster, mocking SimpMessagingTemplate.
 */
class WebSocketBroadcasterTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @InjectMocks
    private WebSocketBroadcaster broadcaster;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void broadcastQueue_sendsToTopicQueue() {
        broadcaster.broadcastQueue();
        
        verify(messagingTemplate).convertAndSend("/topic/queue", "queue-updated");
    }

    @Test
    void broadcastRoomStatus_sendsToTopicRooms() {
        broadcaster.broadcastRoomStatus();
        
        verify(messagingTemplate).convertAndSend("/topic/rooms", "rooms-updated");
    }

    @Test
    void broadcastEvent_sendsToTopicEvents() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        TriageEventLog event = new TriageEventLog(p, EventType.ADMITTED, "R1");
        
        broadcaster.broadcastEvent(event);
        
        verify(messagingTemplate).convertAndSend("/topic/events", event);
    }

    @Test
    void multipleSubscribers_allReceiveUpdates() {
        broadcaster.broadcastQueue();
        broadcaster.broadcastRoomStatus();
        
        verify(messagingTemplate).convertAndSend("/topic/queue", "queue-updated");
        verify(messagingTemplate).convertAndSend("/topic/rooms", "rooms-updated");
    }
}
