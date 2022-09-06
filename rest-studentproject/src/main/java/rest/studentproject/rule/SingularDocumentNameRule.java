package rest.studentproject.rules;


import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rest.studentproject.rules.constants.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class SingularDocumentNameRule implements IRestRule{

    private static final String TITLE = "A singular noun should be used for document names";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List.of(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    public static final String PLURAL = "plural";
    public static final String SINGULAR = "singular";
    public static final String WORDS = "words";
    private boolean isActive;
    private static final String REST_URL = "https://od-api.oxforddictionaries.com:443/api/v2/words/en-gb?q=";
    private static final String APP_ID = "9961257e";
    private static final String APP_KEY = "6d0fc1bc7946a0fde920f8dfd4263d37";
    private static final String PATH_TO_ENGLISH_DICTIONARY_JSON = "src/main/java/rest/studentproject/docs/dictionaryWordForm.json";
    public static final String WITH_PATH_SEGMENT = " With pathSegment: ";

    public SingularDocumentNameRule(boolean isActive) {
        this.isActive = isActive;
    }


    /**
     *
     */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /**
     *
     */
    @Override
    public RuleCategory getCategory() { return RULE_CATEGORY; }

    /**
     *
     */
    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    /**
     *
     */
    @Override
    public RuleType getRuleType() {
        return RULE_TYPE;
    }

    /**
     *
     */
    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST;
    }

    /**
     *
     */
    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     *
     */
    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Rule to check if the path segments could contain more than one word, if so there is a violation.
     *
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI)  {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty()) return violations;
        // Loop through the paths
        return getLstViolations(violations, paths);
    }

    private List<Violation> getLstViolations(List<Violation> violations, Set<String> paths) {
        for (String path : paths) {
            if (path.trim().equals("")) continue;
            // Get the path without the curly braces
            String[] pathSegments = path.split("/");
            // Extract path segments based on / char and check if there are violations
            Violation violation = getLstViolationsFromPathSegments(path, pathSegments);
            if (violation != null) violations.add(violation);


        }
        return violations;
    }

    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments){
        String switchPathSegment = "";
        // Check if a path is starting with a plural or singular word.
        String firstPathSegment = pathSegments[0].trim().toLowerCase();
        if(!firstPathSegment.isEmpty()) {
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }else if(pathSegments.length > 1){
            firstPathSegment = pathSegments[1].trim().toLowerCase();
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }

        for (String pathSegment : pathSegments) {
            if (pathSegment.isEmpty()) continue;
            if (pathSegment.contains("{")) {
                switchPathSegment = PLURAL;
                continue;
            }
            ImmutablePair<Boolean, String> isPathSegmentInJson = checkIfWordInJson(pathSegment);
            if (Boolean.TRUE.equals(isPathSegmentInJson.getLeft()) && !switchPathSegment.equals(isPathSegmentInJson.getRight())) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SINGULARDOCUMENTNAME, path, ErrorMessage.SINGULARDOCUMENTNAME + WITH_PATH_SEGMENT + pathSegment);
            }
            boolean isSingular = checkWordUsingOxfordDictionariesAPI(pathSegment.trim().toLowerCase());
            if(!isSingular && switchPathSegment.equals(SINGULAR)) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SINGULARDOCUMENTNAME, path, ErrorMessage.SINGULARDOCUMENTNAME + WITH_PATH_SEGMENT + pathSegment);
            }
            switchPathSegment = getControlPathSegmentForRule(switchPathSegment.equals(PLURAL));

        }
        return null;
    }

    private String getControlPathSegmentForRule(boolean equals) {
        if (equals) {
            return SINGULAR;
        } else {
            return PLURAL;
        }
    }

    private String getPluralOrSingularOfWord(String firstPathSegment) {
        String switchPathSegment;
        boolean firstPathSegmentForm = checkWordUsingOxfordDictionariesAPI(firstPathSegment);
        switchPathSegment = getControlPathSegmentForRule(firstPathSegmentForm);
        return switchPathSegment;
    }

    private boolean checkWordUsingOxfordDictionariesAPI(String pathSegment) {
        boolean isSingular = false;
        final String restUrl = REST_URL + pathSegment;
        try {
            URL url = new URL(restUrl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", APP_ID);
            urlConnection.setRequestProperty("app_key", APP_KEY);

            // read the output from the server
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(in);
            // Read results path
            JSONArray resultsArray = (JSONArray) jsonObject.get("results");
            // In the first element of results is contained the id of the word. This is always in singular.
            JSONObject resultsJson = (JSONObject)resultsArray.get(0);
            String idWord = (String) resultsJson.get("id");
            // If the given pathSegment is equal to the idWord, then the pathSegment is singular.
            boolean isPathSegmentEqualToIdWord = pathSegment.equals(idWord.toLowerCase());
            ImmutablePair<Boolean, String> isWordInJson = checkIfWordInJson(pathSegment);
            if(!Boolean.TRUE.equals(isWordInJson.getLeft())){
                writeWordToJson(pathSegment, idWord.toLowerCase(), isPathSegmentEqualToIdWord);
            }
            if(isPathSegmentEqualToIdWord) isSingular = true;
        } catch (IOException | ParseException e) {
            // If the word is not found using the API, then we need to catch the exception.
            if(e instanceof FileNotFoundException) {
                // If the word is not in the dictionary, then it is "always" singular.
                return true;
            }
            e.printStackTrace();
        }

        return isSingular;
    }

    public static void writeWordToJson(String pathSegment, String idWord, boolean isSingular) {
        try (FileReader reader = new FileReader(PATH_TO_ENGLISH_DICTIONARY_JSON);){
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray arrayWords = (JSONArray) jsonObject.get(WORDS);
            populateJsonArrayWithWords(arrayWords, pathSegment, idWord, isSingular);
            jsonObject.put(WORDS, arrayWords);
            Files.write(Paths.get(PATH_TO_ENGLISH_DICTIONARY_JSON), jsonObject.toJSONString().getBytes());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void populateJsonArrayWithWords(JSONArray jsonArray, String pathSegment, String idWord, boolean isSingular) {
        JSONObject sampleObjectSingular = new JSONObject();
        JSONObject sampleObjectPlural = new JSONObject();
        if(isSingular){
            sampleObjectSingular.put("word", idWord);
            sampleObjectSingular.put("form", SINGULAR);
            jsonArray.add(sampleObjectSingular);
        }else{
            sampleObjectSingular.put("word", idWord);
            sampleObjectSingular.put("form", SINGULAR);
            jsonArray.add(sampleObjectSingular);
            sampleObjectPlural.put("word", pathSegment);
            sampleObjectPlural.put("form", PLURAL);
            jsonArray.add(sampleObjectPlural);
        }
    }

    private static ImmutablePair<Boolean, String> checkIfWordInJson(String word)
    {
        try (FileReader reader = new FileReader(PATH_TO_ENGLISH_DICTIONARY_JSON);){
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray arrayWords = (JSONArray) jsonObject.get(WORDS);
            for (Object o : arrayWords) {
                JSONObject jsonObject1 = (JSONObject) o;
                String wordInJson = (String) jsonObject1.get("word");
                if(wordInJson.equals(word)) {
                    return ImmutablePair.of(true, jsonObject1.get("form").toString());
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return ImmutablePair.of(false, "");
    }

}
