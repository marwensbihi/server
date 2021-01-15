//package com.majesteye.skeye.skeyepredict.eedaserver.util;
//
//import lombok.NoArgsConstructor;
//import org.apache.solr.common.SolrInputDocument;
//import org.springframework.stereotype.Component;
//
///**
// * @author Rabie Saidi
// */
//@Component
//@NoArgsConstructor
//public class StringToDocumentConverter {
//    public SolrInputDocument convert(String instance){
//        String[] items = instance.trim().split(",");
//        SolrInputDocument document = new SolrInputDocument();
//        document.setField("ID", items[0]);
//        for(int i=1; i<items.length; i++){
//            String fieldName = FieldType.valueOf(items[i].split(":")[0]).getAttribute();
//            if(document.getFieldNames().contains(fieldName)){
//                document.getField(fieldName).addValue(items[i]);
//            }
//            else {
//                document.setField(fieldName, items[i]);
//            }
//        }
//        return document;
//    }
//
//    public static void main(String[] args) {
//        String instance = "Q58822,PATHWAY:Cofactor biosynthesis; 5@6@7@8-tetrahydromethanopterin biosynthesis,IPR:IPR006204,IPR:IPR020568,TAXON:Archaea#Euryarchaeota,TAXON:Archaea#Euryarchaeota#Methanomada group#Methanococci#Methanococcales#Methanocaldococcaceae,TAXON:Archaea,TAXON:Archaea#Euryarchaeota#Methanomada group#Methanococci#Methanococcales,TAXON:Archaea#Euryarchaeota#Methanomada group#Methanococci#Methanococcales#Methanocaldococcaceae#Methanocaldococcus,TAXON:Archaea#Euryarchaeota#Methanomada group,TAXON:Archaea#Euryarchaeota#Methanomada group#Methanococci,\n";
//        SolrInputDocument document = new StringToDocumentConverter().convert(instance);
//        System.out.println(document);
//    }
//}
