package com.javaspell.spellcheck.bl.resourceloader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Named("fileLoader")
public class FileLoader{

    private static final Logger LOGGER = Logger.getLogger(FileLoader.class);
    
    @Value("${service.resources.dir}")
    private String resourcesDir;

    public List<String> loadResource(String resourceName) {

        LOGGER.info("Loading " + resourceName);

        List<String> resource = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(resourcesDir+resourceName))) {
            stream.filter(e -> e != null)
                    .forEach(resource::add);

        } catch (IOException e) {
            LOGGER.error(e);
        }

        return resource;
    }
}
