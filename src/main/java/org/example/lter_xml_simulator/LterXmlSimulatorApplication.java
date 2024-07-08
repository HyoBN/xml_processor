package org.example.lter_xml_simulator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
public class LterXmlSimulatorApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(LterXmlSimulatorApplication.class, args);
        XmlProcessor xmlProcessor = context.getBean(XmlProcessor.class);

        String xmlStr = xmlProcessor.convertDocumentToString();
        xmlStr = xmlProcessor.updateDate(xmlStr);

        xmlProcessor.saveNewXmlAndFin(xmlStr);

//        System.out.println(xmlStr);

//        xmlProcessor.printFilePathTest();

        log.info("정상적으로 종료됨.");
        System.exit(0);
    }
}
