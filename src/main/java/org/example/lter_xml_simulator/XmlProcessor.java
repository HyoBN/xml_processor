package org.example.lter_xml_simulator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class XmlProcessor {

    private final String eqName;

    private final String originFileDate;
//    private final String originFileRootPath = "/data4/lter_sim/sample_xml/csm/log/pm/ossraw";
    private final String originFileRootPath = "/Users/hyobin/Desktop/workspace/spring_projects/lter_xml_simulator/src/main/resources/";

    private String originFileTotalPath;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final String nowDate = LocalDate.now().format(formatter);

    private final String targetTimeRange = getRoundedTimeRange(LocalDateTime.now());

    private String originFilename;


    public XmlProcessor(@Value("${spring.path.eqname}") String eqName,
                        @Value("${spring.path.date}") String originFileDate) {
        this.eqName = eqName;
        this.originFileDate = originFileDate;
        this.originFilename = "B" + originFileDate + "." + targetTimeRange + "_" + eqName + ".xml";
        this.originFileTotalPath = originFileRootPath + eqName + "/" + originFileDate+ "/" + originFilename;
    }


    public void printTest(){
        System.out.println("lastFilePath : "+originFileTotalPath);
    }

    public static String getRoundedTimeRange(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (minute / 5) * 5;
        LocalDateTime endDateTime = dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);
        LocalDateTime startDateTime = endDateTime.minusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return formatter.format(startDateTime) + "-" + formatter.format(endDateTime);
    }

    public Document stringToXml(String xmlRaw) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // XML 문자열을 파싱하여 DOM 객체로 변환
        Document document = (Document) builder.parse(new InputSource(new StringReader(xmlRaw)));
        return document;
    }

}
