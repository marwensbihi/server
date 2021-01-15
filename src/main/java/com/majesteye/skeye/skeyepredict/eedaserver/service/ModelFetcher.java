package com.majesteye.skeye.skeyepredict.eedaserver.service;

import com.majesteye.skeye.skeyepredict.eedaserver.model.Source;
import com.majesteye.skeye.skeyepredict.eedaserver.util.SolrClientFactory;
import com.majesteye.skeye.skeyepredict.predict.classifier.selection.data.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rabie Saidi
 */
@Slf4j
@Component
public class ModelFetcher {

    public List<AggregatedRule> retrieve(String solrUrl, Source source, String coreName, String queryValue) throws IOException, SolrServerException {
        log.info("Starting query: {}", queryValue);
        List<AggregatedRule> aggregatedRules = new ArrayList<>();
        SolrQuery query = new SolrQuery().setRows(Integer.MAX_VALUE);
        query.set("q", queryValue);
        coreName = "skeyepredict." + source + "." + coreName;
        SolrClient solrClient = SolrClientFactory.getSolrClient(solrUrl + "/" + coreName);
        QueryResponse response = solrClient.query(query);
        SolrDocumentList documents = response.getResults();

        aggregatedRules = documents.stream()
                .map(this::convert)
                .collect(Collectors.groupingBy(Rule::getConsequentString))
                .entrySet().stream()
                .map(this::convert)
                .collect(Collectors.toList());

        log.info("Query result: {}", aggregatedRules.size());
        return aggregatedRules;
    }

    private Rule convert(SolrDocument document) {
        Rule rule = new Rule(getAntecedent(document), getConsequent(document), getMetrics(document));
        rule.setUndominatedSpaces(new ArrayList<>());
        return rule;
    }

    private List<MeasureI<Double>> getMetrics(SolrDocument document) {
        List<MeasureI<Double>> metrics = document.getFieldValues("metrics")
                .stream()
                .map(x -> new Measure<>("", Double.parseDouble(String.valueOf(x))))
                .collect(Collectors.toList());
        return metrics;
    }

    private List<String> getConsequent(SolrDocument document) {
        List<String> antecedent = document.getFieldValues("reference")
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return antecedent;
    }

    private List<String> getAntecedent(SolrDocument document) {
        List<String> consequent = document.getFieldValues("conditions")
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return consequent;
    }

    private AggregatedRule convert(Map.Entry<String, List<Rule>> ruleGroup) {
        AggregatedRule aggregatedRule = new AggregatedRuleImp(AggregatedRuleType.ANTECEDENT_BASED, ruleGroup.getKey());
        ruleGroup.getValue().forEach(aggregatedRule::addRule);
        return aggregatedRule;
    }
}
