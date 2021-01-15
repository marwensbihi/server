package com.majesteye.skeye.skeyepredict.eedaserver.controller;

import com.google.gson.Gson;
import com.majesteye.skeye.skeyepredict.eedaserver.model.PredictRun;
import com.majesteye.skeye.skeyepredict.eedaserver.model.Project;
import com.majesteye.skeye.skeyepredict.eedaserver.model.RunTag;
import com.majesteye.skeye.skeyepredict.eedaserver.util.SkeyePredictControllerHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping (value = "/api")
public class SkeyePredictController {

    @Value("${skeyepredict.home}")
    private String baseHome;

    private Gson g = new Gson();

    @Value("${skeyepredict.projects.home}")
    private String projectsHome;

    @Value("${skeyepredict.predict.home}")
    private String predictHome;

    @Value("${skeyepredict.index.home}")
    private String indexHome;

    private static String PROJECT_EXTENSION = ".json";

    private static String PROPERTIES_EXTENSION = ".properties";

    private static String PREDICT_JAR = "predict-exec.jar";

    private static String INDEX_JAR = "index-exec.jar";

    @Autowired
    private SkeyePredictControllerHelper skeyePredictControllerHelper;

    //get list of projects
    //get all json files in {$projects_dir}
    public static List<File> listFiles(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        List<File> jsonFilesOnly;
        List<File> resultListTemp = new LinkedList<>();
        for (File file : resultList) {
             if (file.isDirectory()) {
                 resultListTemp.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            }
        }

        jsonFilesOnly = resultListTemp.stream()
                .filter(file -> file.getName().endsWith(PROJECT_EXTENSION))
                .collect(Collectors.toList());

        return jsonFilesOnly;
    }

    //read and transform files into JSONObject and save into JSONArray
    public static JSONArray listProjects(List<File> ls) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        for (File l : ls) {
            try (Reader reader = new FileReader(l.getAbsolutePath())) {
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                jsonArray.add(jsonObject);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }

    //send JSONArray to the client via GET/REST
    @GetMapping(value = "/getProjects")
    public List<File> getProjects() {
        log.info("skeyepredict.home:" + baseHome);
        return listProjects(listFiles(projectsHome));
    }

    //------------------------------------------------------------------------------
    //send JSONObject to the client via GET/REST
    @PostMapping(value = "/getProjectById")
    public JSONObject getProjectById(@RequestBody String id) {
        log.info("get project with id: {}", id);
        String FullModulePath = projectsHome;
        JSONArray ls = listProjects(listFiles(FullModulePath));
        JSONObject projectJson = new JSONObject();

        for (Object l : ls) {
            Project project = g.fromJson(l.toString(), Project.class);
            if (project.getId().equalsIgnoreCase(id)) {
                projectJson = (JSONObject) l;
            }
        }
        return projectJson;
    }

    //------------------------------------------------------------------------------
    //save pramConfig from project metadata
    @PostMapping(value = "/saveProperties")
    public void saveProperties(@RequestBody String id) {

        try {
            Project project = g.fromJson(getProjectById(id).toString(), Project.class);
            String paramsConfigPath = projectsHome + '/' + project.getId() + '/' + id + PROPERTIES_EXTENSION;
            Properties prop = new Properties();
            prop.setProperty("id", project.getId());
            prop.setProperty("inputDataPath", project.getInputDataPath());


            //save properties to project paramsConfigPath folder
            FileOutputStream fileOutputStream = new FileOutputStream(paramsConfigPath);
            prop.store(fileOutputStream, null);
            fileOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------
    //create new project :)
    //update project :)
    @PostMapping(value = "/createProject")
    public Boolean createProject(@RequestBody JSONObject jsonObject) {
        Project project = g.fromJson(jsonObject.toString(), Project.class);
        log.info("Creating project: {}", project.getId());
        try {
            if(skeyePredictControllerHelper.prepareEnvironment(jsonObject, projectsHome, project.getId(),
                    PROJECT_EXTENSION)) {
                return skeyePredictControllerHelper.createEedaIndex(project, indexHome, INDEX_JAR, "index", "eeda");
            }
            else{
                return false;
            }
        } catch (Exception e) {
            log.error("Sorry can't Create Project File");
            e.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------
    //remove a project :)
    @PostMapping(value = "/removeProject")
    public Boolean removeProject(@RequestBody  String id) {
        log.info("Removing project: {}", id);
        try {
            if(skeyePredictControllerHelper.removeEnvironment(id, projectsHome)) {
                if(skeyePredictControllerHelper.removeEedaIndex(id, indexHome, INDEX_JAR, "removeCore",
                        "eeda")){
                    return skeyePredictControllerHelper.removeKbaseIndex(id, indexHome, INDEX_JAR, "removeCore",
                            "kbase");
                }
                return false;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            log.error("Sorry can't Create Project File");
            e.printStackTrace();
            return false;
        }
    }

    //--------------------
    @PostMapping(value = "/runPredict")
    public void runPredict(@RequestBody JSONObject runJsonObject) throws IOException {
        PredictRun predictRun = g.fromJson(runJsonObject.toString(), PredictRun.class);
        log.info("Project to run is : {}", predictRun.getProjectId());
        String projectHome = projectsHome + '/' + predictRun.getProjectId();
        String runDir = skeyePredictControllerHelper.prepareRunEnvironment(projectHome);
        skeyePredictControllerHelper.saveRunProperties(predictRun, runDir, PROPERTIES_EXTENSION);
        skeyePredictControllerHelper
                .runPredictProcess(predictRun.getProjectId(), runDir, PROPERTIES_EXTENSION, predictHome, PREDICT_JAR);
        skeyePredictControllerHelper.cleanTemp(runDir);
    }

    //------------------------------------------------------------------------------
    //create new project :)
    //update project :)
    @PostMapping(value = "/saveModel")
    public Boolean saveModel(@RequestBody  JSONObject runJsonObject) {
        RunTag runTag = g.fromJson(runJsonObject.toString(), RunTag.class);
        log.info("Saving model: {}-{}", runTag.getProjectId(), runTag.getRunDate());
        try {
//            if(skeyePredictControllerHelper.prepareEnvironment(runJsonObject, projectsHome, runTag.getProjectId(),
//                    PROJECT_EXTENSION)) {
                return skeyePredictControllerHelper.createKbaseIndex(runTag, indexHome, INDEX_JAR, "index", "kbase");
//            }
//            else{
//                return false;
//            }
        } catch (Exception e) {
            log.error("Sorry can't Create Project File");
            e.printStackTrace();
            return false;
        }
    }

}

