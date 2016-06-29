package mx.com.cubozsoft.testingservices;

import android.app.IntentService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by carlos on 27/06/16.
 */ public class HelloIntentService extends IntentService {

    static final String BRADCAST_ACTION = "mx.com.cubozsoft.testingservices.resoult";
    static final String GETCONTEXT = "contextget";
    static Context mContextWraper = null;

    HelloIntentService() {
        super("HelloIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContextWraper = new ContextWrapper(getApplicationContext());
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
            Person obj = getPersonDataFromJson(personJsonStr);
            Intent bIntent = new Intent(BRADCAST_ACTION);
            bIntent.putExtra(Person.DATAPARCELABLE,obj);
            downloadBitmap(new URL(Uri.parse(obj.getUrlImg()).toString()),obj.getIdPicture());

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
        String idPicture;
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

        idPicture = getIdPicture(name);


        return new Person(email,address,idPicture,name,phone, urlPicture );

    }

    private String getIdPicture(String name) {
        return name.replace(" ","");
    }

    private Bitmap downloadBitmap(URL url, String name)  {
        File extStorageDirectory = mContextWraper.getFilesDir();

        FileOutputStream out = null;
        HttpURLConnection client = null;
        //forming a HttoGet request
        try {
            File file = new File(extStorageDirectory, name + ".png");
            if (file.exists()) {
                file.delete();
                file = new File(extStorageDirectory, name + ".png");
                Log.e("file exist", "" + file + ",Bitmap= " + name);
            }
            // initilize the default HTTP client object
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.connect();

            InputStream inputStream = client.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

            out = mContextWraper.openFileOutput(file.getName(),Context.MODE_PRIVATE);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e("file exist", "" + file.getAbsolutePath());

        } catch (Exception e) {
            Log.e("error:", e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (client != null) {
                    client.disconnect();
                }
                if(out != null)
                {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
