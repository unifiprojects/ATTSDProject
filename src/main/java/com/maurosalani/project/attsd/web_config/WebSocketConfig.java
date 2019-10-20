package com.maurosalani.project.attsd.web_config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.matteomauro.notification_server.WebSocketServer;

@Configuration
public class WebSocketConfig extends ResourceConfig {

    public WebSocketConfig() {
        register(WebSocketServer.class);
    }
}
