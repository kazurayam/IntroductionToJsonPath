package com.kazurayam.introductiontojsonpath;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://www.baeldung.com/guide-to-jayway-jsonpath
 */
public class JaywayJsonPathTest {

    String readData(Path dataFile) throws IOException {
        List<String> allLines = Files.readAllLines(dataFile);
        return allLines.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void test0() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/data01.json");
        String jsonDataSourceString = readData(dataFile);
        System.out.println(jsonDataSourceString);
    }

    @Test
    public void test4_1_AccessToDocuments() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/data01.json");
        String jsonDataSourceString = readData(dataFile);
        DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);
        //
        String jsonpathCreatorName =
                jsonContext.read("$['tool']['jsonpath']['creator']['name']");
        assertEquals("Jayway Inc.", jsonpathCreatorName);
        //
        List<String> jsonpathCreatorLocation =
                jsonContext.read("$['tool']['jsonpath']['creator']['location'][*]");
        assertTrue(jsonpathCreatorLocation.toString().contains("Malmo"));
        assertTrue(jsonpathCreatorLocation.toString().contains("San Francisco"));
        assertTrue(jsonpathCreatorLocation.toString().contains("Helsingborg"));
    }

    @Test
    public void test4_2_Predicates() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/4_2_Predicates.json");
        String jsonDataSourceString = readData(dataFile);
        Filter expensiveFilter = Filter.filter(Criteria.where("price").gt(20.00));
        List<Map<String,Object>> expensive =
                JsonPath.parse(jsonDataSourceString)
                        .read("$['book'][?]", expensiveFilter);
        predicateUsageAssertionHelper(expensive);
        //
        expensive =
                JsonPath.parse(jsonDataSourceString)
                        .read("$['book'][?(@['price'] > $['price range']['medium'])]");
        predicateUsageAssertionHelper(expensive);
    }

    private void predicateUsageAssertionHelper(List<?> predicate) {
        assertTrue(predicate.toString().contains("Beginning JSON"));
        assertTrue(predicate.toString().contains("JSON at Work"));
        assertFalse(predicate.toString().contains("Learn JSON in a DAY"));
        assertFalse(predicate.toString().contains("JSON: Questions and Answers"));

    }
}
