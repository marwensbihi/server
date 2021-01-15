package com.majesteye.skeye.skeyepredict.eedaserver.controller;

import com.majesteye.skeye.skeyepredict.eedaserver.model.VersionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rabie Saidi
 */

@Slf4j
@RestController
@RequestMapping (value = "/api")
public class VersionController {
    @CrossOrigin
    @RequestMapping (value = "/version", method = RequestMethod.GET, produces = {"application/json"})
    public VersionModel fetchVersion() {
        String version = "v 1.0";
        VersionModel versionModel = new VersionModel(version);
        log.info("Version: {}", versionModel);
        return versionModel;
//        return version;
    }
}
