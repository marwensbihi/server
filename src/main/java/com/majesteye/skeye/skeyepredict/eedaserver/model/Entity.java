package com.majesteye.skeye.skeyepredict.eedaserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Rabie Saidi
 */
@Data
@AllArgsConstructor
public class Entity {
    private String id;
    private Map<String, List<Attribute>> attributeMap;

    public List<Attribute> getAttributes(String attributeType){
        if(attributeMap.containsKey(attributeType))
            return attributeMap.get(attributeType);
        return new LinkedList<>();
    }
}
