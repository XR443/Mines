package com.github.raspopov.config;

import com.jme3.app.ChaseCameraAppState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfiguration {

    @Bean
    public ChaseCameraAppState chaseCameraAppState() {
        return new ChaseCameraAppState();
    }
}
