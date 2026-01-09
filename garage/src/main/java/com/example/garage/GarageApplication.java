package com.example.garage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Properties;


@SpringBootApplication
public class GarageApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GarageApplication.class);
        
        // On force les paramètres ici si le fichier .properties est ignoré
        Properties props = new Properties();
        props.put("spring.datasource.url", "jdbc:sqlite:racing.db");
        props.put("spring.datasource.driver-class-name", "org.sqlite.JDBC");
        props.put("spring.jpa.hibernate.ddl-auto", "update");
        props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        
        app.setDefaultProperties(props);
        app.run(args);
	}

}
