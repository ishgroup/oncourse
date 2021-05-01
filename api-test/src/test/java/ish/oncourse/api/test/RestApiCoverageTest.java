package ish.oncourse.api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * This test shows us what paths from server-api.yaml aren't covered with karate features
 */
public class RestApiCoverageTest {
    private String pathToFeatures = "src/test/resources/ish/oncourse/api/test";
    private String serverYamlFile = "server-api.yaml";
    private String pathToYamlFiles = "../server-api/src/main/resources/";

    private static final Logger logger = LogManager.getLogger();

    //    @Test
    public void testAllRestRequestsCoveredWithKarateFeatures() {
        List<String> featureList = getFeatureListFromYaml();

        List<String> remainingFeaturesToCreate = featureList.stream().filter(f -> !new File(pathToFeatures + f).exists()).collect(Collectors.toList());

        if (!remainingFeaturesToCreate.isEmpty()) {
            fail("You should realize all remaining features to pass this test:\n" + String.join("\n", remainingFeaturesToCreate));
        }
    }

    private List<String> getFeatureListFromYaml() {
        Yaml yaml = new Yaml();
        Map map = null;
        List<String> featureList = new ArrayList<>();
        Map<String, String> pathList = new HashMap<>();

        try {
            map = (Map) yaml.load(new FileInputStream(new File(pathToYamlFiles + serverYamlFile)));
        } catch (FileNotFoundException e) {
            fail("server-api.yaml have been moved or this test have been moved");
        }

        ((Map) map.get("paths")).forEach((key, value) -> pathList.put(key + "/", (String) ((Map) value).get("$ref")));

        pathList.forEach((k, v) -> {
            try {
                Map y = (Map) yaml.load(new FileInputStream(new File(v.replace("./", pathToYamlFiles))));
                y.keySet().forEach(a -> featureList.add(k + a + ".feature"));
            } catch (FileNotFoundException e) {
                logger.catching(e);
            }
        });

        return featureList;
    }
}
