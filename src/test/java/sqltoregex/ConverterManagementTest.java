package sqltoregex;

import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import sqltoregex.deparser.TestUtils;
import sqltoregex.settings.SettingsManager;
import sqltoregex.settings.SettingsOption;
import sqltoregex.settings.SettingsType;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class ConverterManagementTest {
    ConverterManagement converterManagement = new ConverterManagement(new SettingsManager());

    ConverterManagementTest() throws XPathExpressionException, ParserConfigurationException, IOException,
            SAXException, URISyntaxException {
    }

    @Test
    void rightConcatOfJoinStatements() throws JSQLParserException {
        final String sampleSolution = "SELECT col1 FROM table1 INNER JOIN table2 ON (col1=col2) AND col3 = col4";
        Map<SettingsOption, List<String>> matchingMap = new EnumMap<>(SettingsOption.class);
        matchingMap.put(SettingsOption.DEFAULT, List.of(
                "SELECT col1 FROM table1 INNER JOIN table2 ON (col1=col2) AND col3 = col4",
                "SELECT col1 FROM table1 INNER JOIN table2 WHERE (col1=col2) AND col3 = col4"
        ));

        Assertions.assertTrue(TestUtils.checkAgainstRegEx(converterManagement.deparse(sampleSolution, false,  SettingsType.ALL), "SELECT col1 FROM table1 INNER JOIN table2 ON (col1=col2) AND col3 = col4"));
        Assertions.assertTrue(TestUtils.checkAgainstRegEx(converterManagement.deparse(sampleSolution, false, SettingsType.ALL), "SELECT col1 FROM table1 INNER JOIN table2 WHERE (col1=col2) AND col3 = col4"));
    }

    @Test
    void testExpressionDeparsing() throws JSQLParserException {
        Assertions.assertEquals(
                "^col1 + col2;?$",
                converterManagement.deparse("col1+col2", true, SettingsType.ALL)
        );
        Assertions.assertThrows(JSQLParserException.class, () ->
                converterManagement.deparse("SELECT col1, col2 FROM table", true, SettingsType.ALL)
        );
    }

    @Test
    void testStatementDeparsingWithValidation() throws JSQLParserException {
        String sampleSolution = "SELECT col1, col2 FROM table";
        String parsingSolution = converterManagement.deparse(sampleSolution, false, SettingsType.ALL);
        Assertions.assertTrue(parsingSolution.startsWith("^"));
        Assertions.assertTrue(parsingSolution.endsWith("$"));
    }

    @Test
    void testStatementDeparsingWithoutValidation() throws JSQLParserException {
        String sampleSolution = "SELECT col1, col2 FROM table";
        String parsingSolution = converterManagement.deparse(sampleSolution, false, SettingsType.ALL);
        Assertions.assertTrue(parsingSolution.startsWith("^"));
        Assertions.assertTrue(parsingSolution.endsWith("$"));

        Assertions.assertEquals(
                "^col1 + col2;?$",
                converterManagement.deparse("col1+col2", true, SettingsType.ALL)
        );
    }
}
