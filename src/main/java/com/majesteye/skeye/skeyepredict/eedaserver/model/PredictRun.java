package com.majesteye.skeye.skeyepredict.eedaserver.model;

import lombok.Data;

/**
 * @author Rabie Saidi
 */
@Data
public class PredictRun {

    private String projectId;
    //Rule params
    private Integer minConf;
    private Integer minSup;
    private Integer maxSup;
    private Integer minCondCount;
    private Integer maxCondCount;
    //Rule components
    private boolean annotationsProvided;
    private String annotations;
    private String annotationType;
    private String conditionTypes;
    //XV
    private Integer numRuns;
    private Integer numFolds;
    //Validation
    private Double fMeasureThreshold;
    private Double precisionThreshold;
    private Double accuracyThreshold;
    private Integer modelSizeLimit;
}
