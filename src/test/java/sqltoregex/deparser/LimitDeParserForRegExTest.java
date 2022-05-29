package sqltoregex.deparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import sqltoregex.settings.SettingsType;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LimitDeParserForRegExTest extends UserSettingsPreparer {
    StringBuilder buffer = new StringBuilder();
    StatementDeParser statementDeParser;

    LimitDeParserForRegExTest() throws XPathExpressionException, ParserConfigurationException, IOException,
            SAXException, URISyntaxException {
        super(SettingsType.ALL);
        this.statementDeParser = new StatementDeParserForRegEx(new ExpressionDeParserForRegEx(this.settingsManager),
                                                               buffer, this.settingsManager);
    }

    boolean checkAgainstRegEx(String regex, String toBeChecked) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(toBeChecked);
        return matcher.matches();
    }

    String getRegEx(String sampleSolution) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sampleSolution);
        statement.accept(statementDeParser);
        return statementDeParser.getBuffer().toString();
    }

    @Test
    void testLimit() throws JSQLParserException {
        List<String> toCheckedInput = List.of(
                "SELECT col1 LIMT 3 OFFSET 2",
                "SELECT col1 LIMIT 3 OFFST 2",
                "SELECT col1 LIMIT    3   OFFSET    2"
        );
        validateListAgainstRegEx("SELECT col1 LIMIT 2, 3", toCheckedInput, true);
    }

    @Test
    void testLimitAndOffset() throws JSQLParserException {
        List<String> toCheckedInput = List.of(
                "SELECT col1 LIMIT 3 OFFSET 2"
        );
        validateListAgainstRegEx("SELECT col1 LIMIT 3 OFFSET 2", toCheckedInput, true);
    }

    @Test
    void testLimitDeparser() {
        StringBuilder buffer = new StringBuilder();
        LimitDeParserForRegEx limitDeParserForRegEx = new LimitDeParserForRegEx(buffer);
        Assertions.assertNotNull(limitDeParserForRegEx);
    }

    @Test
    void testLimitNull() throws JSQLParserException {
        List<String> toCheckedInput = List.of(
                "SELECT col1 LIMIT null"
        );
        validateListAgainstRegEx("SELECT col1 LIMIT null", toCheckedInput, true);
    }

    @Test
    void testOffset() throws JSQLParserException {
        List<String> toCheckedInput = List.of(
                "SELECT col1 OFFSET 10 ROWS",
                "SELECT col1 OFFSET  10  ROWS",
                "SELECT col1 OFFST 10 RWS"
        );
        validateListAgainstRegEx("SELECT col1 OFFSET 10 ROWS", toCheckedInput, true);
    }

    void validateListAgainstRegEx(String sampleSolution, List<String> alternativeStatements,
                                  boolean isAssertTrue) throws JSQLParserException {
        String regex = this.getRegEx(sampleSolution);
        for (String str : alternativeStatements) {
            if (isAssertTrue) Assertions.assertTrue(checkAgainstRegEx(regex, str), str + " " + regex);
            else Assertions.assertFalse(checkAgainstRegEx(regex, str), str + " " + regex);
        }
    }

}