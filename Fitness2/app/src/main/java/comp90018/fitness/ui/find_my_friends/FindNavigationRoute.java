package comp90018.fitness.ui.find_my_friends;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindNavigationRoute extends AsyncTask<String, Void, PolylineOptions> {

    private String TAG = "GetPathFromLocation";
    private String API_KEY = "AIzaSyDXgZKIV5QhkU0cV3rs2_8enzgqvlocVoQ";
    private LatLng source, destination;
    private RouteListener listener;

    public FindNavigationRoute(LatLng source, LatLng destination, RouteListener listener) {
        this.source = source;
        this.destination = destination;
        this.listener = listener;
    }

    public String getUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + API_KEY;

        return url;
    }

    @Override
    protected PolylineOptions doInBackground(String... url) {

        String data;

        try {
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            try {
                URL directionUrl = new URL(getUrl(source, destination));
                connection = (HttpURLConnection) directionUrl.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.toString());
                return null;
            } finally {
                inputStream.close();
                connection.disconnect();
            }
            Log.e(TAG, "Background Task data : " + data);

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(data);
                // Starts parsing data
                DirectionHelper helper = new DirectionHelper();
                routes = helper.parse(jsonObject);
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                if (routes.size() > 0){
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    // get the first route
                    List<HashMap<String, String>> path = routes.get(0);
                    // Fetching all the points in the route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                }

                // Drawing polyline in the Google Map for the route
                if (lineOptions != null) {
                    return lineOptions;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in Executing Routes : " + e.toString());
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Background Task Exception : " + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        super.onPostExecute(polylineOptions);
        if (listener != null && polylineOptions != null)
            listener.drawPath(polylineOptions);
    }
}
