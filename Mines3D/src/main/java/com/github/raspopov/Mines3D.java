package com.github.raspopov;

import com.github.raspopov.application.MinesApplication;
import com.jme3.system.AppSettings;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Mines3D {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Mines3D.class).headless(false).run(args);
    }

    @Bean
    public ApplicationRunner applicationRunner(MinesApplication minesApplication) {
        return args -> {
            AppSettings settings = new AppSettings(true);
            settings.setResolution(1024,768);
            minesApplication.setSettings(settings);
            minesApplication.start(true);
            minesApplication.start();
        };
    }
}
