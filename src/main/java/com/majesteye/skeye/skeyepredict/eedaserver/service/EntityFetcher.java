package com.majesteye.skeye.skeyepredict.eedaserver.service;

import com.majesteye.skeye.skeyepredict.eedaserver.model.Attribute;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Entity;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Source;
import com.majesteye.skeye.skeyepredict.eedaserver.util.SolrClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rabie Saidi
 */
@Slf4j
@Component
public class EntityFetcher {

    public List<Entity> retrieve(String solrUrl, Source source, String coreName, String queryValue) throws IOException, SolrServerException {
        log.info("Starting query: {}", queryValue);
        List<Entity> instances;
        SolrQuery query = new SolrQuery().setRows(Integer.MAX_VALUE);
        query.set("q", queryValue);
        coreName = "skeyepredict." + source + "." + coreName;
        SolrClient solrClient = SolrClientFactory.getSolrClient(solrUrl + "/" + coreName);
        QueryResponse response = solrClient.query(query);
        SolrDocumentList documents = response.getResults();
        instances = documents.stream().map(this::convert).collect(Collectors.toList());
        log.info("Query result: {}", instances.size());
        return instances;
    }

    private Entity convert(SolrDocument document) {
        String id = document.getFieldValue("ID").toString();
        id = id.substring(1, id.length() - 1);
        List<String> fieldNames = document.getFieldNames().stream()
                .filter(name -> !name.equals("ID") && !name.equals("id") && !name.equals("_version_"))
                .collect(Collectors.toList());
        Map<String, List<Attribute>> attributes = new HashMap<>();
        for(String fieldName : fieldNames){
            attributes.put(fieldName, document.getFieldValues(fieldName).stream()
                    .map(fieldValue -> fieldToAttribute(fieldName, fieldValue.toString()))
                    .collect(Collectors.toList()));
        }
        return new Entity(id, attributes);
    }

    private Attribute fieldToAttribute(String fieldName, String fieldValue) {
        return new Attribute(fieldName, fieldValue);
    }

}
