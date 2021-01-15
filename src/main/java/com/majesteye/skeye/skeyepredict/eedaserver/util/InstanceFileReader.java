//package com.majesteye.skeye.skeyepredict.eedaserver.util;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.solr.common.SolrInputDocument;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Collection;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * @author Rabie Saidi
// */
//@Slf4j
//@Component
//public class InstanceFileReader {
//    @Autowired
//    private StringToDocumentConverter converter;
//    private final String path;
//
//    public InstanceFileReader(@Value("${index.raw-data.path}") String path) {
//        this.path = path;
//    }
//
//    public Collection<SolrInputDocument> read(){
//        Collection<SolrInputDocument> documents = null;
//        try(Stream<String> stream = Files.lines(Paths.get(path))){
//            documents = stream.map(instance -> converter.convert(instance))
//                    .collect(Collectors.toList());
//        }
//        catch (IOException e) {
//            log.info(e.getMessage());
//        }
//        return documents;
//    }
//}
