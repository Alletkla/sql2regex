package sqltoregex.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import sqltoregex.settings.SettingsManager;
import sqltoregex.settings.SettingsOption;
import sqltoregex.settings.regexgenerator.synonymgenerator.StringSynonymGenerator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GraphPreProcessorTest {

    private SettingsManager settingsManager = new SettingsManager();

    GraphPreProcessorTest() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
    }

    @Test
    void getSynonymMap(){
        Map<String, Set<String>> map = GraphPreProcessor.getSynonymMap(settingsManager.getSynonymManagerBySettingOption(
                SettingsOption.AGGREGATEFUNCTIONLANG, StringSynonymGenerator.class).getGraph());
        Assertions.assertTrue(map.containsKey("AVG"));
        Assertions.assertTrue(map.containsKey("SUM"));
        Assertions.assertEquals(2, map.size());
    }
}