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
 *
 * TODO #Sruthi: full implementation of broadcast methods. Must be safe
 *               to call from worker threads (SimpMessagingTemplate is).
 */
@Component
public class WebSocketBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastQueue() {
        // TODO #Sruthi: convertAndSend("/topic/queue", currentQueueSnapshot)
    }

    public void broadcastRoomStatus() {
        // TODO #Sruthi: convertAndSend("/topic/rooms", currentRoomSnapshots)
    }

    public void broadcastEvent(TriageEventLog event) {
        // TODO #Sruthi: convertAndSend("/topic/events", event)
    }
}
