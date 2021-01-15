package com.majesteye.skeye.skeyepredict.eedaserver.service;

import com.majesteye.skeye.skeyepredict.eedaserver.model.Source;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Rabie Saidi
 */
@Slf4j
@Component
public class InstanceService {
//    private final SolrClient solrClient;
    private final String solrUrl;

    public InstanceService(@Value("${search.data.solr.host}") String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public List<Object> retrieve(Source source, String coreName, String queryValue) throws IOException, SolrServerException {
        List<Object> data = new LinkedList<>();
        if(source == Source.eeda){
            log.info("Loading from SkyEYE-Predict EEDA");
            EntityFetcher service = new EntityFetcher();
            data.addAll(service.retrieve(solrUrl, source, coreName, queryValue));
        }
        if(source == Source.kbase){
            log.info("Loading from SkyEYE-Predict KBase");
            ModelFetcher service = new ModelFetcher();
            data.addAll(service.retrieve(solrUrl, source, coreName, queryValue));
        }
        return data;
    }
}
