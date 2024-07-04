package org.example.lter_xml_simulator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class XmlProcessor {

    private final String eqName;

    private final String originFileDate;
    private final String originFileRootPath = "/data4/lter_sim/sample_xml/csm/log/pm/ossraw";
    private String originFileTotalPath;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final String nowDate = LocalDate.now().format(formatter);

    private final String targetTimeRange = getRoundedTimeRange(LocalDateTime.now());

    private String originFilename;


    public XmlProcessor(@Value("${spring.path.eqname}") String eqName,
                        @Value("${spring.path.date}") String originFileDate) {
        this.eqName = eqName;
        this.originFileDate = originFileDate;
        this.originFileTotalPath = originFileRootPath + "/" + eqName + "/" + originFileDate;
        this.originFilename = "B" + originFileDate + "." + targetTimeRange + "_" + eqName + ".xml";
    }


    public void printTest(){
        System.out.println("originFilename : "+originFilename);
    }

    public static String getRoundedTimeRange(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (minute / 5) * 5;
        LocalDateTime endDateTime = dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);
        LocalDateTime startDateTime = endDateTime.minusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return formatter.format(startDateTime) + "-" + formatter.format(endDateTime);
    }

}
