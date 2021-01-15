package com.majesteye.skeye.skeyepredict.eedaserver.controller;

import com.majesteye.skeye.skeyepredict.eedaserver.model.Attribute;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Entity;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Source;
import com.majesteye.skeye.skeyepredict.eedaserver.service.InstanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Rabie Saidi
 */
@Slf4j
@RestController
@RequestMapping (value = "/api")
public class EedaController {
    @Autowired
    InstanceService service;
//    @Autowired
//    QueryEditor queryEditor;

    @CrossOrigin
    @RequestMapping(value = "/getAttributes/{c}", method = RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getAttributes(@PathVariable("c") String core) throws IOException, SolrServerException {
        log.info("Extracting attributes from core: {}", core);
        String query = "*:*";
        List<Object> entities = service.retrieve(Source.eeda, core, query);

        return getAttributeMap(entities);
    }

    private Map<String, List<String>> getAttributeMap(List<Object> entities) {
        Map<String, List<String>> attributeMap = new HashMap<>();
        for(Object entityObject : entities){
            Entity entity = (Entity) entityObject;
            for (Map.Entry<String, List<Attribute>> entry : entity.getAttributeMap().entrySet()) {
                String k = entry.getKey();
                List<String> v = entry.getValue().stream().map(Attribute::getValue).collect(Collectors.toList());
                attributeMap.merge(k, v, (v1, v2) -> {
                    Set<String> newV1 = new HashSet<>(v1);
                    newV1.addAll(v2);
                    return new LinkedList<>(newV1);
                });
            }
        }

        return attributeMap;
    }
}
