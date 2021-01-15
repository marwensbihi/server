package com.majesteye.skeye.skeyepredict.eedaserver.util;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.stereotype.Component;

/**
 * @author Rabie Saidi
 */
@Component
public class SolrClientFactory {

    public static SolrClient getSolrClient(String solrUrl){
        return new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }
}
