package com.sap.cloud.s4hana.migratehistoricaldata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.sap.cloud.sdk", "com.sap.cloud.s4hana.migratehistoricaldata"})
@ServletComponentScan({"com.sap.cloud.sdk", "com.sap.cloud.s4hana.migratehistoricaldata"})
public class MyApplication extends SpringBootServletInitializer {
    public static void main(final String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(MyApplication.class);
    }
    
    
}
