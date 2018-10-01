package com.maths22.ftclivescoring.listeners;

import com.maths22.ftclivescoring.messaging.Messages;
import com.maths22.ftclivescoring.services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConfigUpdateListener implements ApplicationListener<ConfigService.ConfigUpdateEvent> {
    private final SimpMessagingTemplate stompClient;

    @Autowired
    public ConfigUpdateListener(SimpMessagingTemplate stompClient) {
        this.stompClient = stompClient;
    }

    @Override
    public void onApplicationEvent(ConfigService.ConfigUpdateEvent event) {
        stompClient.convertAndSend("/topic/config/"+event.getField(),
                new Messages.ConfigUpdate(event.getOrigin(), event.getField(), event.getUpdatedProps()));
    }
}
