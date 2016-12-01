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

    double latitude;
    double longitude;
    Context context;

    public MyLocationListener( Context context){
        this.context=context;
    }

    @Override // wenn sich die Position verändert hat
    public void onLocationChanged(Location location) {
        latitude= location.getLatitude();
        longitude= location.getLongitude();
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