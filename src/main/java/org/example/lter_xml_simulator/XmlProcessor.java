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

    private final String originFileDate;
//    private final String originFileRootPath = "/data4/lter_sim/sample_xml/csm/log/pm/ossraw";
    private final String originFileRootPath = "/Users/hyobin/Desktop/workspace/spring_projects/lter_xml_simulator/src/main/resources/";

    private String originFileTotalPath;
    private String targetFileTotalPath;
    private String targetFinFileTotalPath;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter formatterDash = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String nowDate = LocalDate.now().format(formatter);

    private final String targetTimeRange = getRoundedTimeRange(LocalDateTime.now());

    private String originFilename;
    private String targetFilename;


    public XmlProcessor(@Value("${spring.path.eqname}") String eqName,
                        @Value("${spring.path.date}") String originFileDate) throws Exception{
        this.eqName = eqName;
        this.originFileDate = originFileDate;
        this.originFilename = "B" + originFileDate + "." + targetTimeRange + "_" + eqName + ".xml";
        this.targetFilename = "B" + nowDate + "." + targetTimeRange + "_" + eqName + ".xml";
        this.originFileTotalPath = originFileRootPath + eqName + "/" + originFileDate+ "/" + originFilename;
        this.targetFileTotalPath = originFileRootPath + eqName + "/" + nowDate + "/" + targetFilename;
        this.targetFinFileTotalPath = targetFileTotalPath.replace(".xml", ".fin");
    }

    String newBeginTime = "2022-08-12T00:00:00.000+09:00";

    String nowDateDash = LocalDate.now().format(formatterDash);



    public void printFilePathTest(){
        log.error("originFilePath : " + originFileTotalPath);
        log.error("targetFilePath : " + targetFileTotalPath);

    }
//    public static String formatDateStringToDash(String inputDate) {
//        return inputDate.substring(0, 4) + "-" + inputDate.substring(4, 6) + "-" + inputDate.substring(6, 8);
//    }

    public static String getRoundedTimeRange(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (minute / 5) * 5;
        LocalDateTime endDateTime = dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);
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
        log.error("orign : {}, now : {}", originFileDateDash, nowDateDash);
        log.error(targetFileTotalPath);
        xmlStr = xmlStr.replaceAll(originFileDateDash, nowDateDash);
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
