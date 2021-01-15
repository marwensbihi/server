package com.majesteye.skeye.skeyepredict.eedaserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rabie Saidi
 */
@Data
@AllArgsConstructor
public class Attribute {
//    private String type; //CRITERION, GOAL
//    private int index; //1, 2, 3, ..
//    private String name; //IPR, TAXON, AGE,
//    private String value; //any value
////    private boolean predicted; //true, false

    private final String attributeType;
    private final String value;
}
