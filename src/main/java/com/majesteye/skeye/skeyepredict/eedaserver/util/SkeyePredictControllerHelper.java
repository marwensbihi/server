package com.majesteye.skeye.skeyepredict.eedaserver.util;

import com.majesteye.skeye.skeyepredict.eedaserver.model.PredictRun;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Project;
import com.majesteye.skeye.skeyepredict.eedaserver.model.RunTag;
import com.majesteye.skeye.skeyepredict.predict.exception.PredictRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.apache.commons.io.FileUtils.forceDelete;

/**
 * @author Rabie Saidi
 */
@Component
@Slf4j
public class SkeyePredictControllerHelper {

    @Autowired
    private SimpMessagingTemplate template;

    public boolean prepareEnvironment(JSONObject jsonObject, String projectsHome, String id, String extension) {
        String projectDir = projectsHome + '/' + id;
        File dir = new File(projectDir);
        if(!dir.exists()) {
            dir.mkdir();
            File runsDir = new File(projectDir, "runs");
            runsDir.mkdir();

            //Write JSON file
            try (FileWriter file = new FileWriter(projectDir + '/' + id + extension)) {
                file.write(jsonObject.toJSONString());
                file.flush();
                log.info("Project File created successfully");
                return true;
            } catch (IOException e) {
                log.error("Sorry can't Create Project File");
                e.printStackTrace();
                return false;
            }
        }
        else{
            log.info("Project {} already exists", id);
            return false;
        }
    }

    public void runPredictProcess(String projectId,
                                  String runDir,
                                  String propertiesExtension,
                                  String predictHome,
                                  String predictJarName) throws IOException {
        //init
        log.info("Running project: {}", projectId);
        String predictProperties =  runDir + "/.temp/predict" + propertiesExtension;
        String runProperties =  runDir + '/' + projectId + propertiesExtension;
        String predictJar = predictHome + "/bin/" + predictJarName;

        ProcessBuilder ps = new ProcessBuilder();
//        String cl = "--annotationType=EC --task=runPipeline --spring.config.additional-location=classpath:/pipeline-dev-local.properties";
        ps.command("java", "-jar", predictJar,
                "--annotationType=EC",
                "--task=runPipeline",
                "--spring.config.additional-location=" + predictProperties + ',' + runProperties);

        ps.directory(new File(runDir));
        ps.redirectErrorStream(true);

        //start prosses :)
        Process pr = ps.start();
        //get the pid of the process

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            log.info("Predict line: {}", line);
            //send the line msg to the topic [/topic/prediction] so client will be notfiy!
            template.convertAndSend("/topic/prediction", line);
        }
        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("ERROR");
        }
        log.info("Done!");

        in.close();
    }

    public String prepareRunEnvironment(String projectHome) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd-hh-mm-ss");
        String time = dateFormat.format(now);
        File outDir = new File(projectHome, time);
        boolean outDirMade = outDir.mkdir();
        if(outDirMade) {
            log.info("outDir successfully made at: {}", outDir.getPath());
            prepareTempFiles(outDir.getPath());
            return outDir.getPath();
        }
        else{
            log.error("outDir failed to be made at: {}", outDir.getPath());
            return "";
        }
    }

    public void saveRunProperties(PredictRun predictRun, String runDir, String propertiesExtension) {
        try {
            String runProperties = runDir + "/" + predictRun.getProjectId() + propertiesExtension;
            Properties prop = new Properties();

            prop.setProperty("search.data.api.core", predictRun.getProjectId());

            prop.setProperty("predict.path-params.output-folder", runDir);

            prop.setProperty("predict.minConf", predictRun.getMinConf().toString());
            prop.setProperty("predict.minSup", predictRun.getMinSup().toString());
            prop.setProperty("predict.maxSup", predictRun.getMaxSup().toString());
            prop.setProperty("predict.minCondCount", predictRun.getMinCondCount().toString());
            prop.setProperty("predict.maxCondCount", predictRun.getMaxCondCount().toString());

            prop.setProperty("predict.annotationsProvided", String.valueOf(predictRun.isAnnotationsProvided()));
            prop.setProperty("predict.annotations", predictRun.getAnnotations());
            prop.setProperty("predict.annotationType", predictRun.getAnnotationType());
            prop.setProperty("predict.conditionTypes", predictRun.getConditionTypes());

            prop.setProperty("predict.validation-params.numRuns", predictRun.getNumRuns().toString());
            prop.setProperty("predict.validation-params.numFolds", predictRun.getNumFolds().toString());

            prop.setProperty("predict.validation-params.fMeasureThreshold", predictRun.getFMeasureThreshold().toString());
            prop.setProperty("predict.validation-params.precisionThreshold", predictRun.getPrecisionThreshold().toString());
            prop.setProperty("predict.validation-params.accuracyThreshold", predictRun.getAccuracyThreshold().toString());
            prop.setProperty("predict.validation-params.modelSizeLimit", predictRun.getModelSizeLimit().toString());

            //save properties to project paramsConfigPath folder
            FileOutputStream fileOutputStream = new FileOutputStream(runProperties);
            prop.store(fileOutputStream, null);
            fileOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void prepareTempFiles(String runDir){
        File tempDir = new File(runDir, ".temp");
        boolean tempDirMade = tempDir.mkdir();
        if(tempDirMade) {
            log.info("tempDir successfully made at: {}", tempDir.getPath());
        }
        else{
            log.error("tempDir failed to be made at: {}", tempDir.getPath());
        }

        try {
            FileUtils.copyInputStreamToFile(
                    this.getClass().getResourceAsStream("/rg"),
                    new File(tempDir, "rg"));
            new File(tempDir.getPath() + "/rg").setExecutable(true, false);
            FileUtils.copyInputStreamToFile(
                    this.getClass().getResourceAsStream("/predict.properties"),
                    new File(tempDir, "predict.properties"));
            log.info("rg copied to " + tempDir);
        } catch (IOException e) {
            log.error("rg failed to be copied to {}", tempDir);
            throw new PredictRuntimeException(e.getMessage(), e);
        }
    }

    public void cleanTemp(String runDir){
        File tempDir = new File(runDir, ".temp");
        try {
            forceDelete(tempDir);
        } catch (IOException exception) {
            log.warn("Failed to delete folder: {}", tempDir);
        }
    }

    public Boolean createEedaIndex(Project project, String indexHome, String IndexJarName, String task, String sink) {
        try{
            runIndexProcess(project.getId(), project.getInputDataPath(), indexHome, IndexJarName, task, sink);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void runIndexProcess(String projectId,
                                String projectInputDataPath,
                                String indexHome,
                                String IndexerJarName,
                                String task,
                                String sink) throws IOException {

        log.info("Project id: {}", projectId);
        String indexerJar = indexHome + "/bin/" + IndexerJarName;
        ProcessBuilder ps = new ProcessBuilder();
        if(task.equals("index")) {
            ps.command("java", "-jar", indexerJar,
//                "--index.data.solr.host=http://localhost:8983/solr",
//                "--index.data.solr.home=" + indexHome + "/solr",
                    "--sink=" + sink,
                    "--core=" + projectId,
                    "--task=" + task,
                    "--data=" + projectInputDataPath
            );
        }
        else if(task.equals("removeCore")) {
            ps.command("java", "-jar", indexerJar,
//                "--index.data.solr.host=http://localhost:8983/solr",
//                "--index.data.solr.home=" + indexHome + "/solr",
                    "--sink=" + sink,
                    "--core=" + projectId,
                    "--task=" + task
            );
        }
//        ps.directory(new File(runDir));
        ps.directory(new File(indexHome));
        ps.redirectErrorStream(true);
        Process pr = ps.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            log.info("Indexer line: {}", line);
            //send the line msg to the topic [/topic/prediction] so client will be notfiy!
            template.convertAndSend("/topic/index", line);
        }
        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("ERROR");
        }
        log.info("Done!");

        in.close();
    }

    public Boolean removeEedaIndex(String id, String indexHome, String IndexJarName, String task, String sink) {
        try{
            runIndexProcess(id, "",indexHome, IndexJarName, task, sink);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeEnvironment(String id, String projectsHome) {
        File projectDir = new File(projectsHome, id);
        try {
            forceDelete(projectDir);
        } catch (IOException exception) {
            log.warn("Failed to delete folder: {}", projectDir);
            return false;
        }
        return true;
    }

    public Boolean removeKbaseIndex(String id, String indexHome, String IndexJarName, String task, String sink) {
        try{
            runIndexProcess(id, "",indexHome, IndexJarName, task, sink);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean createKbaseIndex(RunTag runTag, String indexHome, String IndexJarName, String task, String sink) {
        try{
            String projectInputModelPath = new File(indexHome).getParent()
                    + "/projects/"
                    + runTag.getProjectId() + '/'
                    + runTag.getRunDate()
                    + "/predict-rules.json";
            runIndexProcess(runTag.getProjectId(), projectInputModelPath, indexHome, IndexJarName, task, sink);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
