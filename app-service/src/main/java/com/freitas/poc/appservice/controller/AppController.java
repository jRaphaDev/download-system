package com.freitas.poc.appservice.controller;

import com.freitas.poc.appservice.utils.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class AppController {

    private static final String DIRECTORY = "/Users/Freitas/Documents/estudos/download-upload-system";
    private static final String DEFAULT_FILE_NAME = "teste.txt";

    private static final String BASE_DIRECTORY = System.getProperty("user.dir")+"/arquivos";


    @Autowired
    private ServletContext servletContext;

    @RequestMapping(value = "download", method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> downloadFile(HttpServletResponse response,
            @RequestParam(defaultValue = DEFAULT_FILE_NAME) String fileName) throws IOException {

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);

        File file = new File(DIRECTORY + "/" + fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List<Path>> list(HttpServletResponse response) throws IOException {
        Stream<Path> path = Files.walk(Paths.get(BASE_DIRECTORY))
                .filter(Files::isRegularFile);
        return ResponseEntity.ok().body(path.collect(Collectors.toList()));
    }

}
