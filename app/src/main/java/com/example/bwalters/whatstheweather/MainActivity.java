package com.example.bwalters.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherTextView;

    public void findWeather(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            if (cityName.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), "Enter a City Name!", Toast.LENGTH_LONG).show();
            } else {
                DownloadTask task = new DownloadTask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=2dff0ce8aff09e5d9317a5ad648dcc03");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        weatherTextView = findViewById(R.id.weatherTextView);

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader =  new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                    return result;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.i("Result : ","not null");
                try {
                    String message = "";
                    JSONObject jsonObject = new JSONObject(result);
                    String weatherData = jsonObject.getString("weather");
                    Log.i("Weather Data : ", weatherData);
                    JSONArray array = new JSONArray(weatherData);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonPart = array.getJSONObject(i);
                        String main = "";
                        String description = "";
                        main = jsonPart.getString("main");
                        description = jsonPart.getString("description");
                        if (main != "" && description != "") {
                            message += main + ":" + description + "\r\n";
                        }
                    }

                    if (message != null) {
                        weatherTextView.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could Not Found the Weather Detailes!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could Not Found the Weather Detailes!!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }


        }
    }

}
