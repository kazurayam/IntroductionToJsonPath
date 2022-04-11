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


public class ProductTest {

    String readData(Path dataFile) throws IOException {
        List<String> allLines = Files.readAllLines(dataFile);
        return allLines.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void test0() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/product/1.json");
        String jsonString = readData(dataFile);
        System.out.println(jsonString);
    }

    @Test
    public void test_cold() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/product/2_3.json");
        String jsonString = readData(dataFile);
        Object dataObject =
                JsonPath.parse(jsonString)
                        .read("$[?('cold' in @['tags'])]");
        String dataString = dataObject.toString();
        System.out.println(dataString);
    }

    @Test
    public void test_house_door_price() throws IOException {
        Path dataFile = Paths.get("./src/test/fixture/product/1.json");
        String jsonString = readData(dataFile);
        List<Map<String,Object>> result =
                JsonPath.parse(jsonString)
                        //.read("$[?(@['name'] && 'home' in @['tags'])]");
                        .read("$[?(@['name'] =~ /.*door.*/i && 'home' in @['tags'])]");
        System.out.println(result.toString());
        //
        System.out.println(result.get(0).get("price"));
        assertEquals(12.5, result.get(0).get("price"));
    }
}
