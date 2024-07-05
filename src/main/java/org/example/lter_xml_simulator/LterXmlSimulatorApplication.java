package org.example.lter_xml_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LterXmlSimulatorApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(LterXmlSimulatorApplication.class, args);
        XmlProcessor xmlProcessor = context.getBean(XmlProcessor.class);

        String xmlStr = xmlProcessor.convertDocumentToString();
        xmlStr = xmlProcessor.updateDate(xmlStr);

        xmlProcessor.saveNewXml(xmlStr);

//        System.out.println(xmlStr);

        xmlProcessor.printFilePathTest();

    }
}
