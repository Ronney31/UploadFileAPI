package com.rnkuSpringPractice.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rnkuSpringPractice.demo.storage.FileSystemStorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private final FileSystemStorageService storageService = new FileSystemStorageService();

    @GetMapping("")
    public String listUploadedFiles(Model model) throws IOException {
    	//On call of upload, it will load all the files URL from the directory
        List<String> urls = storageService.loadAll().map(path ->
                        MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                        .build().encode().toString())
        		.collect(Collectors.toList());

        List<String> names = storageService.loadAll().map(path ->path.getFileName().toString()).collect(Collectors.toList());
        List fileAndNames = new ArrayList<Map<String,String>>(urls.size());
        for(int i=0; i<urls.size(); i++){
            Map<String,String> mp = new HashMap<>();
            mp.put("url",urls.get(i));
            mp.put("name",names.get(i));
            fileAndNames.add(mp);
        }
        model.addAttribute("files", fileAndNames);
        return "uploadForm";
    }

    
    /*
     * To View/Download the uploaded files
     */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        System.out.println("serveFile:" + filename);
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

}