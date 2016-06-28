package mx.com.cubozsoft.testingservices;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by carlos on 27/06/16.
 */ class HelloIntentService extends IntentService {

    static final String BRADCAST_ACTION = "mx.com.cubozsoft.testingservices.resoult";

    HelloIntentService() {
        super("HelloIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String BASE_URL = "https://randomuser.me/api/";
        // Will contain the raw JSON response as a string.
        String personJsonStr = null;
        String uri = Uri.parse(BASE_URL).toString();

        String LOG_TAG = this.getClass().getSimpleName();

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(uri);
            Log.v(LOG_TAG,url.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return ;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return ;
            }
            personJsonStr = buffer.toString();

//            return getWeatherDataFromJson(personJsonStr, num_days);

            Intent bIntent = new Intent(BRADCAST_ACTION);
            bIntent.putExtra(Person.DATAPARCELABLE,getPersonDataFromJson(personJsonStr));
            sendBroadcast(bIntent);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return ;
//        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
//        return ;
    }

    Person getPersonDataFromJson(String rootJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_NAME = "name";
        final String OWM_NAME_ELEMENTS_TITLE = "title";
        final String OWM_NAME_ELEMENTS_FIRST = "first";
        final String OWM_NAME_ELEMENTS_LAST = "last";
        final String OWM_LOCATION = "location";
        final String OWM_LOCATION_STREET = "street";
        final String OWM_LOCATION_CITY = "city";
        final String OWM_LOCATION_STATE = "state";
        final String OWM_LOCATION_POSTCODE = "postcode";
        final String OWM_EMAIL = "email";
        final String OWM_PHONE = "phone";
        final String OWM_PICTURE = "picture";
        final String OWM_PICTURE_MED = "medium";

        String name;
        String address;
        String email;
        String phone;
        int idPicture;
        String urlPicture;


        JSONObject rootJson = new JSONObject(rootJsonStr);
        JSONObject  personJson = (rootJson.getJSONArray(OWM_LIST)).getJSONObject(0);

        JSONObject namePersonJson = personJson.getJSONObject(OWM_NAME);
        name = namePersonJson.getString(OWM_NAME_ELEMENTS_TITLE) +" "
                + namePersonJson.getString(OWM_NAME_ELEMENTS_FIRST) + " "
                + namePersonJson.getString(OWM_NAME_ELEMENTS_LAST);

        JSONObject addressPersonJson = personJson.getJSONObject(OWM_LOCATION);
        address = addressPersonJson.getString(OWM_LOCATION_STREET) +", "
                + addressPersonJson.getString(OWM_LOCATION_CITY) + ", "
                + addressPersonJson.getString(OWM_LOCATION_STATE) + ", "
                + addressPersonJson.getString(OWM_LOCATION_POSTCODE);


        email = personJson.getString(OWM_EMAIL);

        phone = personJson.getString(OWM_PHONE);

        JSONObject picturePersonJson = personJson.getJSONObject(OWM_PICTURE);
        urlPicture = picturePersonJson.getString(OWM_PICTURE_MED);

        idPicture = getIdPicture(urlPicture);


        return new Person(email,address,idPicture,name,phone);

    }

    private int getIdPicture(String url) {
        return -1;
    }
}
