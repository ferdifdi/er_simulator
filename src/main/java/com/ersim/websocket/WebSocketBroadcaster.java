package com.ersim.websocket;

import com.ersim.model.TriageEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Wraps SimpMessagingTemplate to push live updates to subscribed clients.
 * Topics:
 *   /topic/queue   – queue snapshots
 *   /topic/rooms   – room status snapshots
 *   /topic/events  – per-event audit messages
 */
@Component
public class WebSocketBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastQueue() {
        messagingTemplate.convertAndSend("/topic/queue", "queue-updated");
    }

    public void broadcastRoomStatus() {
        messagingTemplate.convertAndSend("/topic/rooms", "rooms-updated");
    }

    public void broadcastEvent(TriageEventLog event) {
        messagingTemplate.convertAndSend("/topic/events", event);
    }
}
