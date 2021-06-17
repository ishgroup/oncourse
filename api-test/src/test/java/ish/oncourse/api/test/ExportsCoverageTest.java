package ish.oncourse.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;




public class ExportsCoverageTest {


    @Test
    public void test() {
        Results results = Runner.builder().clientFactory(ish.oncourse.api.test.client.KarateClient::new).path(  "classpath:ish/oncourse/api/test/testExportsCoverage.feature").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }

    private static final String SERVER_API_YAML_FILE_NAME = "server-api.yaml";
    private static final String PATH_TO_API_ROOT = "../server-api/src/main/resources/";
    private static final String ENTITY_LIST_PATH = "/list/entity/";
    private static final String EMPTY_STRING = "";
    private static final String PATHS_YAML_ROOT_NAME = "paths";

    // List of entities for that we don't check exports coverage
    // This is a workaround for not fully implemented entities
    private static final List<String> EXCLUDED_ENTITIES = Arrays.asList("document");

    public static List<Map<String, String>> getListEntities() {
        Yaml yaml = new Yaml();
        Map map = null;

        try {
            map = (Map) yaml.load(new FileInputStream(new File(PATH_TO_API_ROOT + SERVER_API_YAML_FILE_NAME)));
        } catch (FileNotFoundException e) {
            Assertions.fail("server-api.yaml have been moved or this test have been moved");
        }
        List<String> entities = ((Map<String, String>) map.get(PATHS_YAML_ROOT_NAME)).keySet().stream()
                .filter(path -> path.startsWith(ENTITY_LIST_PATH))
                .map(item -> item.replaceFirst(ENTITY_LIST_PATH, EMPTY_STRING))
                .collect(Collectors.toList()).stream()
                .filter(path -> !path.contains("/"))
                .collect(Collectors.toList());
        List<Map<String, String>> result = new ArrayList<>();
        entities.forEach(entity -> {
            if (!EXCLUDED_ENTITIES.contains(entity)) {
                Map<String, String> values = new HashMap<>();
                values.put("name", entity);
                result.add(values);
            }
        });
        return result;
    }

    public static List<Map<String, String>> getListEntitiesAndExclude(List<String> excludedEntities) {
        Yaml yaml = new Yaml();
        Map map = null;

        try {
            map = (Map) yaml.load(new FileInputStream(new File(PATH_TO_API_ROOT + SERVER_API_YAML_FILE_NAME)));
        } catch (FileNotFoundException e) {
            Assertions.fail("server-api.yaml have been moved or this test have been moved");
        }
        List<String> entities = ((Map<String, String>) map.get(PATHS_YAML_ROOT_NAME)).keySet().stream()
                .filter(path -> path.startsWith(ENTITY_LIST_PATH))
                .map(item -> item.replaceFirst(ENTITY_LIST_PATH, EMPTY_STRING))
                .collect(Collectors.toList()).stream()
                .filter(path -> !path.contains("/"))
                .collect(Collectors.toList());
        List<Map<String, String>> result = new ArrayList<>();
        entities.forEach(entity -> {
            if (!excludedEntities.contains(entity)) {
                Map<String, String> values = new HashMap<>();
                values.put("name", entity);
                result.add(values);
            }
        });
        return result;
    }
}
