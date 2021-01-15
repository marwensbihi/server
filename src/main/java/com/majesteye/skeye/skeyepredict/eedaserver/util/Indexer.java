//package com.majesteye.skeye.skeyepredict.eedaserver.util;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.common.SolrInputDocument;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Collection;
//
///**
// * @author Rabie Saidi
// */
//@Slf4j
//@Component
//public class Indexer {
//    @Autowired
//    private InstanceFileReader reader;
//    private final SolrClient solrClient;
//
//    public Indexer(@Value("${index.data.solr.host}") String solrUrl) {
//        solrClient = SolrClientFactory.getSolrClient(solrUrl);
//    }
//
//    public void index() throws IOException, SolrServerException {
//        Collection<SolrInputDocument> documents = reader.read();
//        log.info(documents.toString());
//
//        solrClient.add(documents);
//        solrClient.commit();
//    }
//}
