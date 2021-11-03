package comp90018.fitness.ui.exercise;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class Pedometer implements SensorEventListener {
    private Context mContext;
    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private TextView textView;
    private int mStepDetector = 0;
    private int mStepCounter = 0;
    private Double outdistance = 0.0;
    private String TAG = "Pedometer";
    private double longitude = 0;
    private double latitude = 0;
    private double preLongitude = 0;
    private double preLatitude = 0;


    public Pedometer(Context context){
//        info = new ThirdActivity_info();
//        gender = info.gender;
//        height = info.height;
//        weight = info.weight;

        mContext = context;
        enableSensor();
        locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.ACCURACY_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    if(preLatitude!=0){
                        float[] results = new float[1];
                        Location.distanceBetween(preLatitude,preLongitude,latitude,longitude,results);
                        outdistance = outdistance + results[0];
                    }
                    preLatitude = latitude;
                    preLongitude = longitude;
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String provider) {



                }
                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
        else {
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {

                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        }
        if(preLatitude!=0){
            float[] results = new float[1];
            Location.distanceBetween(preLatitude,preLongitude,latitude,longitude,results);
            outdistance = outdistance + results[0];
        }
        preLatitude = latitude;
        preLongitude = longitude;


    }


    public static double getDistatce(double lat1, double lat2, double lon1,    double lon2) {
        double R = 6371;
        double distance = 0.0;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
        return distance;
    }


    public void enableSensor(){
        int suitable = 0;
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);



        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG,"get step");
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

            if (event.values[0] == 1.0f) {
                mStepDetector++;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            mStepCounter = (int) event.values[0];

        } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //float distance = (float)Math.sqrt(Math.pow(event.values[0],2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2));
        }

        EventBus.getDefault().post(new PedometerMessage(mStepCounter,mStepDetector,outdistance));

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}