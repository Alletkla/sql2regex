package sqltoregex.settings;

import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sqltoregex.settings.regexgenerator.IRegExGenerator;
import sqltoregex.settings.regexgenerator.OrderRotation;
import sqltoregex.settings.regexgenerator.SpellingMistake;
import sqltoregex.settings.regexgenerator.synonymgenerator.DateAndTimeFormatSynonymGenerator;
import sqltoregex.settings.regexgenerator.synonymgenerator.StringSynonymGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The SettingsContainer hold all enabled/selected Settings.
 */
public class SettingsContainer {
    private final Map<SettingsOption, IRegExGenerator<?>> allSettings;

    /**
     * Constructor for the SettingsContainer, with a not null Builder.
     * @param builder SettingsContainerBuilder
     */
    private SettingsContainer(Builder builder){
        Assert.notNull(builder.unModifiableMap, "Builder must not be null!");
        this.allSettings = builder.unModifiableMap;
    }

    /**
     * Return a object which is instanceof IRegExGenerator by passing a SettingsOption.
     * @param settingsOption one of enum SettingsOption
     * @return Object instanceof IRegExGenerator
     */
    public IRegExGenerator<?> get(SettingsOption settingsOption){
        for (IRegExGenerator<?> generator : this.allSettings.values()){
            if (generator.getSettingsOption() == settingsOption){
                return generator;
            }
        }
        return null;
    }

    /**
     * Return a map with objects which are instanceof IRegExGenerator by passing a class.
     * @param clazz Class of object which is instanceof IRegExGenerator
     * @param <T> instanceof IRegExGenerator
     * @return Map with key = SettingsOption and value = object instanceof IRegExGenerator
     */
    public <T extends IRegExGenerator<?>> Map<SettingsOption, T> get(Class<T> clazz){
        Map<SettingsOption, T> map = new EnumMap<>(SettingsOption.class);

        for (Map.Entry<SettingsOption,?> entry : this.allSettings.entrySet()){
            try{
                map.put(entry.getKey(), clazz.cast(entry.getValue()));
            }catch (ClassCastException e){
                //continue trying to cast other values
            }
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Get all currently saved Settings.
     * @return Map with key = SettingsOption and value = object instanceof IRegExGenerator
     */
    public Map<SettingsOption, IRegExGenerator<?>> getAllSettings(){
        return this.allSettings;
    }

    /**
     * Get a new SettingsContainer-Builder
     * @return Builder
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Specific builder static inner class, to build the SettingsContainer.
     */
    public static final class Builder {
        private static final String UNSUPPORTED_BUILD_WITH = "Unsupported build with:";
        private static final String STRING_SYNONYM_DELIMITER = ";";
        private final Map<SettingsOption,IRegExGenerator<?>> modifiableMap = new EnumMap<>(SettingsOption.class);
        Map<SettingsOption,IRegExGenerator<?>> unModifiableMap;

        /**
         * Append an entry to the builder map, which holds SettingOptions and related setting objects. In this case, by passing an object instanceof {@link IRegExGenerator}.
         * @param regExGenerator object instanceof {@link IRegExGenerator}
         * @return this builder
         */
        public Builder with(IRegExGenerator<?> regExGenerator){
            this.modifiableMap.put(regExGenerator.getSettingsOption(), regExGenerator);
            return this;
        }

        /**
         * Append an or multiple entry to the builder map, which holds SettingOptions and related setting objects. In this case, by passing a settings container.
         * @param settingsContainer SettingsContainer
         * @return this builder
         */
        public Builder with(SettingsContainer settingsContainer) {
            modifiableMap.putAll(settingsContainer.getAllSettings());
            return this;
        }

        /**
         * Append an or multiple entry to the builder map, which holds SettingOptions and related setting objects. In this case, by passing the SettingsManager and one of enum SettingsType.
         * @param settingsManager SettingsManager
         * @param settingsType one of enum SettingsType
         * @return this builder
         */
        public SettingsContainer with(SettingsManager settingsManager, SettingsType settingsType){
            this.with(settingsManager.getSettingsContainer(settingsType));
            return this.build();
        }

        /**
         * Append an entry to the builder map, which holds SettingOptions and related setting objects. In this case, by passing a NodeList and one of enum SettingsOption.
         * @param nodeList NodeList from the xml parsing process
         * @param settingsOption one of enum SettingsOption
         * @return this builder
         */
        public Builder withNodeList(NodeList nodeList, SettingsOption settingsOption) {
            if (nodeList.item(0).getTextContent().equals("false")) {
                return this;
            }

            switch (settingsOption) {
                case KEYWORDSPELLING, TABLENAMESPELLING, COLUMNNAMESPELLING, INDEXCOLUMNNAMESPELLING, TABLENAMEORDER, COLUMNNAMEORDER, GROUPBYELEMENTORDER, INDEXCOLUMNNAMEORDER, INSERTINTOVALUESORDER -> this.with(
                        settingsOption);
                case DATESYNONYMS, TIMESYNONYMS, DATETIMESYNONYMS -> {
                    Set<String> valueList = new HashSet<>();
                    SettingsNodeListIterator settingsNodeListIterator = new SettingsNodeListIterator(nodeList);
                    for (Node node : settingsNodeListIterator) {
                        valueList.add(node.getTextContent());
                    }
                    this.withStringSet(valueList, settingsOption);
                }
                case AGGREGATEFUNCTIONLANG, DATATYPESYNONYMS, OTHERSYNONYMS -> {
                    List<Node> valuePairsForSynonyms = new LinkedList<>();
                    SettingsNodeListIterator valueTagIterator = new SettingsNodeListIterator(nodeList);
                    for (Node node : valueTagIterator) {
                        valuePairsForSynonyms.add(node);
                    }
                    Set<String> pairOfSynonymList = new HashSet<>();
                    for (Node valueNode : valuePairsForSynonyms) {
                        String valuePair = valueNode.getTextContent();
                        pairOfSynonymList.add(valuePair);
                    }
                    this.withStringSet(pairOfSynonymList, settingsOption);
                }
                case DEFAULT -> {
                    //pass because nothing needs to be added for default
                }
                default -> throw new IllegalArgumentException(UNSUPPORTED_BUILD_WITH + settingsOption);
            }
            return this;
        }

        public Builder with(SettingsOption settingsOption) {
            switch (settingsOption) {
                case KEYWORDSPELLING, TABLENAMESPELLING, COLUMNNAMESPELLING, INDEXCOLUMNNAMESPELLING -> {
                    SpellingMistake spellingMistake = new SpellingMistake(settingsOption);
                    this.modifiableMap.put(settingsOption, spellingMistake);
                }
                case TABLENAMEORDER, COLUMNNAMEORDER, INDEXCOLUMNNAMEORDER, GROUPBYELEMENTORDER, INSERTINTOVALUESORDER -> {
                    OrderRotation orderRotation = new OrderRotation(settingsOption);
                    this.modifiableMap.put(settingsOption, orderRotation);
                }
                default -> throw new IllegalArgumentException(UNSUPPORTED_BUILD_WITH + settingsOption);
            }
            return this;
        }

        public Builder withSettingsOptionSet(Set<SettingsOption> settingsOptions) {
            Assert.notNull(settingsOptions, "Set of settings options must not be null");
            for (SettingsOption settingsOption : settingsOptions) {
                with(settingsOption);
            }
            return this;
        }



        public Builder withSimpleDateFormatSet(Set<SimpleDateFormat> synonyms, SettingsOption settingsOption) {
            Assert.notNull(synonyms, "Set of simple date formats options must not be null");
            switch (settingsOption) {
                case DATESYNONYMS, TIMESYNONYMS, DATETIMESYNONYMS -> {
                    DateAndTimeFormatSynonymGenerator synonymGenerator = new DateAndTimeFormatSynonymGenerator(
                            settingsOption);
                    for (SimpleDateFormat format : synonyms) {
                        synonymGenerator.addSynonym(format);
                    }
                    this.modifiableMap.put(settingsOption, synonymGenerator);
                }
                default -> throw new IllegalArgumentException(UNSUPPORTED_BUILD_WITH + settingsOption);
            }
            return this;
        }

        /**
         * Creates a {@link IRegExGenerator} depending on the provided settingsOption and synonym Set.
         * If the synonymSet consists of delimited Strings they can splitted
         * and added with {@link sqltoregex.settings.regexgenerator.synonymgenerator.SynonymGenerator#addSynonymFor(Object, Object)}
         *
         * @param synonyms
         * @param settingsOption
         * @return
         */
        public Builder withStringSet(Set<String> synonyms, SettingsOption settingsOption) {
            switch (settingsOption) {
                case DATESYNONYMS, TIMESYNONYMS, DATETIMESYNONYMS -> {
                    DateAndTimeFormatSynonymGenerator synonymGenerator = new DateAndTimeFormatSynonymGenerator(
                            settingsOption);
                    for (String format : synonyms) {
                        synonymGenerator.addSynonym(new SimpleDateFormat(format));
                    }
                    this.modifiableMap.put(settingsOption, synonymGenerator);
                }
                case AGGREGATEFUNCTIONLANG, DATATYPESYNONYMS, OTHERSYNONYMS -> {
                    if (synonyms != null && !synonyms.isEmpty()) {
                        StringSynonymGenerator aggregateFunctionSynonymGenerator = new StringSynonymGenerator(
                                settingsOption);
                        for (String singleSynonym : synonyms) {
                            if (singleSynonym.split(STRING_SYNONYM_DELIMITER).length == 1){
                                aggregateFunctionSynonymGenerator.addSynonym(singleSynonym);
                            }else {
                                aggregateFunctionSynonymGenerator.addSynonymFor(
                                        singleSynonym.split(STRING_SYNONYM_DELIMITER)[0].strip(),
                                        singleSynonym.split(STRING_SYNONYM_DELIMITER)[1].strip());
                            }
                        }
                        this.modifiableMap.put(settingsOption, aggregateFunctionSynonymGenerator);
                    }
                }
                default -> throw new IllegalArgumentException(UNSUPPORTED_BUILD_WITH + settingsOption);
            }
            return this;
        }

        public SettingsContainer build(){
            this.unModifiableMap = Collections.unmodifiableMap(this.modifiableMap);
            return new SettingsContainer(this);
        }

    }

    /**
     * Overrides default equals method. Objects are equal, if the allSettings map is equal.
     * @param o to compare object
     * @return boolean if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingsContainer that)) return false;
        return allSettings.equals(that.allSettings);
    }

    /**
     * Overrides default hashCode() method. HashCode candidate is the allSettings map.
     * @return hashcode as int
     */
    @Override
    public int hashCode() {
        return Objects.hash(allSettings);
    }
}
