package sqltoregex.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PropertyManagerTest {
    static PropertyManager propertyManager;
    static Set<PropertyOption> spellings;
    static Set<PropertyOption> orders;
    static Set<SimpleDateFormat> dateFormats;
    static Set<SimpleDateFormat> timeFormats;
    static Set<SimpleDateFormat> dateTimeFormats;
    static Set<String> aggregateFunctionLang;
    static String sql;

    @BeforeEach
    void beforeEach() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        this.resetSets();
    }

    void resetSets() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        propertyManager = new PropertyManager();
        spellings = new HashSet<>();
        orders = new HashSet<>();
        dateFormats = new HashSet<>();
        timeFormats = new HashSet<>();
        dateTimeFormats = new HashSet<>();
        aggregateFunctionLang = new HashSet<>();
        sql = "SELECT * FROM test";
    }

    @Test
    void testLoadDefaultProperties() {
        Set<PropertyOption> propertyOptionSet = propertyManager.readPropertyOptions();
        List<String> propertyOptionWhichHaveBeenSet = List.of(
                "KEYWORDSPELLING",
                "TABLENAMESPELLING",
                "TABLENAMEORDER",
                "COLUMNNAMESPELLING",
                "KEYWORDSPELLING",
                "COLUMNNAMEORDER",
                "DATESYNONYMS",
                "TIMESYNONYMS",
                "DATETIMESYNONYMS",
                "AGGREGATEFUNCTIONLANG"
        );
        for (String str : propertyOptionWhichHaveBeenSet) {
            Assertions.assertTrue(propertyOptionSet.toString().contains(str));
        }
    }
}
