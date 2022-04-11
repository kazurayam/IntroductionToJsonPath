package com.kazurayam.introductiontojsonpath;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://www.baeldung.com/guide-to-jayway-jsonpath
 */
public class JaywayJsonPathTest {

    private static final Path dataFile = Paths.get("./src/test/fixture/data01.json");
    private static String jsonDataSourceString;

    private String jsonpathCreatorNamePath = "$['tool']['jsonpath']['creator']['name']";
    private String jsonpathCreatorLocationPath = "$['tool']['jsonpath']['creator']['location'][*]";

    @BeforeAll
    public static void beforeAll() throws IOException {
        List<String> allLines = Files.readAllLines(dataFile);
        jsonDataSourceString = allLines.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void test0() {
        System.out.println(jsonDataSourceString);
    }

    @Test
    public void test4_1_AccessToDocuments() {
        DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);
        String jsonpathCreatorName = jsonContext.read(jsonpathCreatorNamePath);
        List<String> jsonpathCreatorLocation = jsonContext.read(jsonpathCreatorLocationPath);
        assertEquals("Jayway Inc.", jsonpathCreatorName);
        assertTrue(jsonpathCreatorLocation.toString().contains("Malmo"));
    }
}
