package com.kazurayam.introductiontojsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<Map<String, Object>> expensive =
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

    @Test
    public void test6_1_GettingObjectDataGivenIDs() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/6_Example.json");
        String jsonString = readData(dataFile);
        //System.out.println(jsonDataSourceString);
        Object dataObject =
                JsonPath.parse(jsonString).read("$[?(@.id == 2)]");
        String dataString = dataObject.toString();
        assertTrue(dataString.contains("2"));
        assertTrue(dataString.contains("Quantum of Solace"));
        assertTrue(dataString.contains("Twenty-second James Bond"));
    }

    @Test
    public void test6_2_GettingTheMovieTitleGivenStarring() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/6_Example.json");
        String jsonString = readData(dataFile);
        //System.out.println(jsonDataSourceString);
        List<Map<String, Object>> dataList =
                JsonPath.parse(jsonString)
                        .read("$[?('Eva Green' in @['starring'])]");
        String title = (String) dataList.get(0).get("title");
        assertEquals("Casino Royale", title);
    }

    @Test
    public void test6_3_CalculationOfTheTotalRevenue() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/6_Example.json");
        String jsonString = readData(dataFile);
        DocumentContext context = JsonPath.parse(jsonString);
        int length = context.read("$.length()");
        long revenue = 0;
        for (int i = 0; i < length; i++) {
            revenue += context
                    .read("$[" + i + "]['box office']",
                            Long.class);
        }
        assertEquals(
                594275385L + 591692078L + 1110526981L + 879376275L
                , revenue);
    }

    @Test
    public void test6_4_HighestRevenueMovie() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/6_Example.json");
        String jsonString = readData(dataFile);
        DocumentContext context = JsonPath.parse(jsonString);
        List<Object> revenueList = context.read("$[*]['box office']");
        Integer[] revenueArray = revenueList.toArray(new Integer[0]);
        Arrays.sort(revenueArray);
        //
        int highestRevenue = revenueArray[revenueArray.length - 1];
        Configuration pathConfiguration =
                Configuration.builder().options(
                        Option.AS_PATH_LIST
                ).build();
        List<String> pathList =
                JsonPath.using(pathConfiguration)
                        .parse(jsonString)
                        .read("$[?(@['box office'] == " + highestRevenue + ")]");
        pathList.stream().forEach(System.out::println);
        //
        Map<String, String> dataRecord =
                context.read(pathList.get(0));
        String title = dataRecord.get("title");
        //
        assertEquals("Skyfall", title);
    }

    @Test
    public void test6_5_LatestMovieOfADirector() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/6_Example.json");
        String jsonString = readData(dataFile);
        DocumentContext context = JsonPath.parse(jsonString);
        //
        List<Map<String,Object>> dataList =
                context.read("$[?(@.director == 'Sam Mendes')]");
        //dataList.stream().forEach(System.out::println);
        List<Object> dateList = new ArrayList<>();
        for (Map<String,Object> item : dataList) {
            Object date = item.get("release date");
            dateList.add(date);
        }
        Long[] dateArray = dateList.toArray(new Long[0]);
        Arrays.sort(dateArray);
        //
        long latestTime = dateArray[dateArray.length - 1];
        List<Map<String,Object>> finalDataList =
                context.read("$[?(@['director'] == 'Sam Mendes' && @['release date'] == " + latestTime + ")]");
        String title = (String)finalDataList.get(0).get("title");
        assertEquals("Spectre", title);
    }
}