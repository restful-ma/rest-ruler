package rest.studentproject.oxford.dictionary.api;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class OxfordDictionariesApi {

    private OxfordDictionariesApi() {}

    /**
     * Method that check if a given word is singular or plural using the OxfordDictioanryAPI
     * @param pathSegment
     * @return
     */
    public static boolean checkWordUsingOxfordDictionariesAPI(String pathSegment) {
        boolean isSingular = false;
        String restUrl = OxfordConstants.REST_URL + pathSegment;
        try {
            // Connect to the OxfordDictionaryAPI
            URL url = new URL(restUrl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", OxfordConstants.APP_ID);
            urlConnection.setRequestProperty("app_key", OxfordConstants.APP_KEY);

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
            ImmutablePair<Boolean, String> isWordInJson = JsonOxfordDictionary.checkIfWordInJson(pathSegment);
            if(!Boolean.TRUE.equals(isWordInJson.getLeft())){
                JsonOxfordDictionary.writeWordToJson(pathSegment, idWord.toLowerCase(), isPathSegmentEqualToIdWord);
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

}
