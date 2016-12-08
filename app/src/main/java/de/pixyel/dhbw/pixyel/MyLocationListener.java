package de.pixyel.dhbw.pixyel;
import android.content.Context;
import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;

/**
 * Created by Ich on 17.11.2016.
 */
// Klasse die einen LocationListener zu implementieren. Dieser erhält Benachrichtigugen vom LoctionManager und reagiert darauf.

public class MyLocationListener implements LocationListener{

    public static double latitude;
    public static double longitude;
    Context context;

    public MyLocationListener( Context context){
        this.context=context;
    }

    //liefert den Breitengrad als String zurück
    public static String getLati(){
        String lati= String.valueOf(latitude);
        return lati;
    }
    //liefert den Längengrad als String zurück
    public static String getLongi(){
        String longi= String.valueOf(longitude);
        return longi;
    }

    @Override // wenn sich die Position verändert hat
    public void onLocationChanged(Location location) {
        latitude= location.getLatitude();
        longitude= location.getLongitude();
        getLati();
        getLongi();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override //wenn kein Provider verfügbar ist (z.B. das GPS nicht aktiviert ist)
    public void onProviderDisabled(String provider) {

        PopUp popUp = new PopUp();
        popUp.PopUp(context, "GPS nicht aktiviert", "Bitte einschalten");
    }

}