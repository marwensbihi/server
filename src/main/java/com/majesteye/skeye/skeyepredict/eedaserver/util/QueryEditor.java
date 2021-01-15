package com.majesteye.skeye.skeyepredict.eedaserver.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Rabie Saidi
 */

@Component
public class QueryEditor {
    private final Map<String, String> indexFieldMap= new HashMap<>();

    @SneakyThrows
    public QueryEditor() {
        Properties properties = new Properties();
        properties.load(QueryEditor.class.getClassLoader().getResourceAsStream("index-field-mapping.properties"));

        indexFieldMap.putAll(properties.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> e.getValue().toString())));
    }
    //*:B =>
    //A:B => C_3:"A:B"
    public String edit(String query) {
        if(query.equals("*:*")){
            return query;
        }
        else {
            String queryAttribute = query.split(":")[0];
            String fieldName = queryAttribute.equals("*")? queryAttribute : indexFieldMap.get(queryAttribute);
            if (fieldName.equals("*")) {
                List<String> editedQueries = indexFieldMap.entrySet().stream().map(e -> {
                    String queryValue = query.split(":")[1];
                    String editedQuery = String.format("%s:\"%s:%s\"", e.getValue(), e.getKey(), queryValue);
                    return editedQuery;
                }).collect(Collectors.toList());
                String editedQuery = String.join(" OR ", editedQueries);
                return editedQuery;
            } else {
                String editedQuery = String.format("%s:\"%s\"", fieldName, query);
//                String editedQuery = String.format("%s:*", fieldName);
                return editedQuery;
            }
        }
    }
}
