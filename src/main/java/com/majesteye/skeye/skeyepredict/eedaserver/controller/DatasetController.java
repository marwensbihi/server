package com.majesteye.skeye.skeyepredict.eedaserver.controller;

import com.majesteye.skeye.skeyepredict.eedaserver.model.Source;
import com.majesteye.skeye.skeyepredict.eedaserver.service.InstanceService;
import com.majesteye.skeye.skeyepredict.eedaserver.util.QueryEditor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Rabie Saidi
 */

@Slf4j
@RestController
@RequestMapping (value = "/api")
public class DatasetController {
    @Autowired
    InstanceService service;
    @Autowired
    QueryEditor queryEditor;

    @CrossOrigin
    @RequestMapping (value = "/search/{s}/{c}/{q}", method = RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
    public List<Object> search(@PathVariable("s") String indexType, @PathVariable("c") String core, @PathVariable("q") String query) throws IOException, SolrServerException {
        log.info("Index Type: {}", indexType);
        Source sourceEnum = Source.valueOf(indexType);
        log.info("Core: {}", core);
        log.info("Query: {}", query);// IPR:IPR10264
//        String internalQuery = queryEditor.edit(query);//CRITERION_5:"IPR:IPR10264"
//        return service.retrieve(internalQuery);
        return service.retrieve(sourceEnum, core, query);

    }

}
