package rest.studentproject.oxford.dictionary.api;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonOxfordDictionary {

    private JsonOxfordDictionary() {}

    /**
     * Write given words in a json dictionary with the given form (singular or plural)
     * @param pathSegment
     * @param idWord
     * @param isSingular
     */
    public static void writeWordToJson(String pathSegment, String idWord, boolean isSingular) {
        try (FileReader reader = new FileReader(OxfordConstants.PATH_TO_ENGLISH_DICTIONARY_JSON);){
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray arrayWords = (JSONArray) jsonObject.get(OxfordConstants.WORDS);
            populateJsonArrayWithWords(arrayWords, pathSegment, idWord, isSingular);
            jsonObject.put(OxfordConstants.WORDS, arrayWords);
            Files.write(Paths.get(OxfordConstants.PATH_TO_ENGLISH_DICTIONARY_JSON), jsonObject.toJSONString().getBytes());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * If a given word from a pathSegment is different from the idWord (which comes from the OxfordApi),
     * then we add two words with the corresponding form (singular or plural) to the jsonDictionary. If the words are the same,
     * then only one word with the corresponding form is added.
     * @param jsonArray
     * @param pathSegment
     * @param idWord
     * @param isSingular
     */
    public static void populateJsonArrayWithWords(JSONArray jsonArray, String pathSegment, String idWord, boolean isSingular) {
        JSONObject sampleObjectSingular = new JSONObject();
        JSONObject sampleObjectPlural = new JSONObject();
        if(isSingular){
            sampleObjectSingular.put("word", idWord);
            sampleObjectSingular.put("form", OxfordConstants.SINGULAR);
            jsonArray.add(sampleObjectSingular);
        }else{
            sampleObjectSingular.put("word", idWord);
            sampleObjectSingular.put("form", OxfordConstants.SINGULAR);
            jsonArray.add(sampleObjectSingular);
            sampleObjectPlural.put("word", pathSegment);
            sampleObjectPlural.put("form", OxfordConstants.PLURAL);
            jsonArray.add(sampleObjectPlural);
        }
    }

    /**
     * Method to check if a word is present in the JsonDictionary. If it is present, then we get a tuple containing a boolean indicating
     * the presence of the word (true if the word is present) and the corresponding form of the word (plural or singular).
     * @param word
     * @return
     */
    public static ImmutablePair<Boolean, String> checkIfWordInJson(String word)
    {
        try (FileReader reader = new FileReader(OxfordConstants.PATH_TO_ENGLISH_DICTIONARY_JSON);){
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray arrayWords = (JSONArray) jsonObject.get(OxfordConstants.WORDS);
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


