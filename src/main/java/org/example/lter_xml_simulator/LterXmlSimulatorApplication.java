package org.example.lter_xml_simulator;

import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LterXmlSimulatorApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(LterXmlSimulatorApplication.class, args);
        XmlProcessor xmlProcessor = context.getBean(XmlProcessor.class);

        xmlProcessor.printTest();

    }
}
