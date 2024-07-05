package org.example.lter_xml_simulator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class XmlProcessor{

    private final String eqName;
    private boolean isMidNight = false;

    private final String originFileDate;
    private String originFileDateForFileName;
//    private final String originFileRootPath = "/data4/lter_sim/sample_xml/csm/log/pm/ossraw";
    private final String originFileRootPath = "/Users/hyobin/Desktop/workspace/spring_projects/lter_xml_simulator/src/main/resources/";

    private String originFileTotalPath;
    private String targetFileTotalPath;
    private String targetFinFileTotalPath;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter formatterDash = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String nowDate = LocalDate.now().format(formatter);
    private String nowDateForDirectory = LocalDate.now().format(formatter);
    private String nowDateForFileName = LocalDate.now().format(formatter);

    private final String targetTimeRange = getRoundedTimeRange(LocalDateTime.now());

    private final String originFilename;
    private String targetFilename;


    public XmlProcessor(@Value("${spring.path.eqname}") String eqName,
                        @Value("${spring.path.date}") String originFileDate) throws Exception{
        if (targetTimeRange.equals("2355-0000")) {
            isMidNight = true;
            nowDateForFileName = (LocalDate.now().minusDays(1)).format(formatter);
            LocalDate localDate = LocalDate.parse(originFileDate, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDateTime localDateTime = localDate.atStartOfDay();
            LocalDateTime previousDayDateTime = localDateTime.minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            originFileDateForFileName = formatter.format(previousDayDateTime);
        } else{
            originFileDateForFileName = originFileDate;
        }
        this.eqName = eqName;
        this.originFileDate = originFileDate;
        this.originFilename = "B" + originFileDateForFileName + "." + targetTimeRange + "_" + eqName + ".xml";
        this.targetFilename = "B" + nowDateForFileName + "." + targetTimeRange + "_" + eqName + ".xml";

        this.originFileTotalPath = originFileRootPath + eqName + "/" + originFileDate+ "/" + originFilename;
        this.targetFileTotalPath = originFileRootPath + eqName + "/" + nowDateForDirectory + "/" + targetFilename;
        this.targetFinFileTotalPath = targetFileTotalPath.replace(".xml", ".fin");
    }



    String nowDateDash = LocalDate.now().format(formatterDash);
    public void printFilePathTest(){
        log.error("originFilePath : " + originFileTotalPath);
        log.error("targetFilePath : " + targetFileTotalPath);
        log.error("midnight : " + isMidNight);

    }

    public static String getRoundedTimeRange(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (minute / 5) * 5;
        LocalDateTime endDateTime = dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);

        // 2355-0000 예외처리 테스트용
                endDateTime = LocalDateTime.of(2024, 7, 5, 0, 0);

        LocalDateTime startDateTime = endDateTime.minusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return formatter.format(startDateTime) + "-" + formatter.format(endDateTime);
    }

    // 문자열을 Document 객체로 변환하는 메소드
    private static Document convertStringToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlStr)));
    }


    public String convertDocumentToString() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(originFileTotalPath));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (Exception e) {
            log.error("Error converting document", e);
        }
        return null;
    }

    public String updateDate(String xmlStr){
        String originFileDateDash = originFileDate.substring(0, 4) + "-" + originFileDate.substring(4, 6) + "-" + originFileDate.substring(6, 8);
        String originFileDateForNameDash = originFileDateForFileName.substring(0, 4) + "-" + originFileDateForFileName.substring(4, 6) + "-" + originFileDateForFileName.substring(6, 8);

        log.error("orign : {}, now : {}", originFileDateDash, nowDateDash);
        log.error(targetFileTotalPath);
        try{
            xmlStr = xmlStr.replaceAll(originFileDateDash, nowDateDash);
            if (isMidNight) {
                xmlStr = xmlStr.replace("beginTime=\"" + originFileDateForNameDash, "beginTime=\"" + LocalDate.now().minusDays(1).format(formatterDash));

            }
        } catch (Exception e){
            log.error("Error converting document", e);
        }

        return xmlStr;
    }

    public void saveNewXmlAndFin(String xmlStr){

        File targetXmlFile = new File(targetFileTotalPath);
        File targetFinFile = new File(targetFinFileTotalPath);
        File parentDir = targetXmlFile.getParentFile();
        if(!parentDir.exists()){
            if(parentDir.mkdirs()){
                log.error("디렉토리 생성함");
            } else{
                log.error("디렉토리 생성 실패");
                return;
            }
        }

        try(FileWriter fw = new FileWriter(targetXmlFile)){
            fw.write(xmlStr);
        } catch (IOException e){
            log.error("xml 파일 저장 에러 : {}",e.getMessage());
        }

        try (FileWriter fw = new FileWriter(targetFinFile)) {
            fw.write("");
        } catch (IOException e){
            log.error("fin 파일 저장 에러 : {}", e.getMessage());
        }

    }
}
