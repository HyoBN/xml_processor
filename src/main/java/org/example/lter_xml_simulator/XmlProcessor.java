package org.example.lter_xml_simulator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class XmlProcessor {

    private final String eqName;

    private final String originFileDate;

    public XmlProcessor(@Value("${spring.path.eqname}") String eqName,
                        @Value("${spring.path.date}") String originFileDate) {
        this.eqName = eqName;
        this.originFileDate = originFileDate;
    }
    String originFilePath = "/data4/lter_sim/sample_xml/csm/log/pm/ossraw";


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String nowDate = LocalDate.now().format(formatter);

    String targetFilePath;



    public void printTest(){
        System.out.println("eqName : "+eqName);
    }


}
