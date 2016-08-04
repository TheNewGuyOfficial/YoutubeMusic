package com.thenewguy.official.youtubemusic;

/**
 * Created by Fynn on 7/29/2016.
 */
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper extends AsyncTask<String, Void, String>{

    private static final String USER_AGENT = "Mozilla/5.0";

    private OnCompleted listener;

    public HttpHelper(OnCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String theParam = params[0];
        String returnString = "";

        try {

            Log.v("Json Url", theParam);

            URL obj = new URL(theParam);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            returnString = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnString;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onTaskCompleted(s);
    }


    public interface OnCompleted {
        void onTaskCompleted(String jsonString);
    }
}