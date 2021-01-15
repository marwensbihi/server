package com.majesteye.skeye.skeyepredict.eedaserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Rabie Saidi
 */
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api")
public class UploadController {
    //Save the uploaded file to this folder
//    private static String UPLOADED_FOLDER = "F://temp//";
    //private static String UPLOADED_FOLDER_ = "/media/rsaidi/104f6573-3615-4bdd-af36-e064ff6290e4" +
     //       "/majesteye/rnd/experim/skeyepredict/projects/inputTemp";

    @Value("${skeyepredict.projects.home}")
    private  String UPLOADED_FOLDER;


    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER +"/inputTemp/"+ file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}
