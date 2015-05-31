package com.tekdi.foodmap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.util.ArrayList;
import java.util.List;

public class FindActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        InfoWindowAdapter,
        LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private ServeMap selectedServeMap;

    class ServeMap
    {
        ServeFoodEntity s;
        Marker m;

        ServeMap(ServeFoodEntity s, Marker m)
        {
            this.s = s;
            this.m = m;
        }
    }

    private ArrayList<ServeMap> smList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        setUpMapIfNeeded();

        smList = new ArrayList<>();

        mLocationClient = new LocationClient(this, this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link # setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
            mMap.setInfoWindowAdapter(this);

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(Location location) {
        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        if (location != null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

            new ListServersEndpointAsyncTask(this).execute();

        }
        else
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void setupServers(List<ServeFoodEntity> result) {
        for (ServeFoodEntity q : result) {
            
            if ((q.getLatitude()==null)||(q.getLongitude()==null))
                continue;
            
            Double lat = Double.parseDouble(q.getLatitude());
            Double lng = Double.parseDouble(q.getLongitude());
            LatLng currentLocation = new LatLng(lat,lng);

            BitmapDescriptor flag;

            if (q.getCuisine() == null) {
                flag = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
            }
            else if (q.getCuisine().equalsIgnoreCase("indian")) {
                flag = BitmapDescriptorFactory.fromResource(R.drawable.indian_flag);
            }
            else if (q.getCuisine().equalsIgnoreCase("italian")) {
                    flag = BitmapDescriptorFactory.fromResource(R.drawable.italy_flag);
            } else {
                flag = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
            }


            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.latitude, currentLocation.longitude))
                    .title(q.getName())
                    .snippet(q.getCuisine())
                    .icon(flag));


            smList.add(new ServeMap(q,m));
        }
    }

    /*
 * Called by Location Services when the request to connect the
 * client finishes successfully. At this point, you can
 * request the current location or start periodic updates
 */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        mCurrentLocation = mLocationClient.getLastLocation();
        setUpMap(mCurrentLocation);
        //Toast.makeText(this, "Connected " + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            // Sajid try {
            // Start an Activity that tries to resolve the error

                /* Sajid - compile error can't resole CONNECTION_FAILURE_RESOLUTION_REQUEST
                 * connectionResult.startResolutionForResult(
                 *       this,
                 *       CONNECTION_FAILURE_RESOLUTION_REQUEST);
                 */

            Toast.makeText(this, "CONNECTION_FAILURE_RESOLUTION_REQUEST",
                    Toast.LENGTH_SHORT).show();

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            // Sajid} catch (IntentSender.SendIntentException e) {
            // Sajid    // Log the error
            // Sajid    e.printStackTrace();
            // Sajid}
        } //else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //Sajid showErrorDialog(connectionResult.getErrorCode());
        //}
    }

    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mCurrentLocation.setLatitude(location.getLatitude());
        mCurrentLocation.setLongitude(location.getLongitude());
        setUpMap(mCurrentLocation);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        for (ServeMap sm : smList) {
            if (sm.m.getId().equals(marker.getId())) {
                Log.v("sajid", "Found marker " + sm.s.getName());
                selectedServeMap = sm;
                sm.m.showInfoWindow();
            }
        }
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

                Log.v("sajid", "Found info marker " + selectedServeMap.s.getName());


                Intent intent = new Intent(this, ListMenuActivity.class);
                intent.putExtra("serverId", selectedServeMap.s.getId());
                intent.putExtra("serverName", selectedServeMap.s.getName());
                intent.putExtra("source","finder");
                intent.putExtra("phone",selectedServeMap.s.getPhone());
                intent.putExtra("address",selectedServeMap.s.getAddress());

                startActivity(intent);

    }

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        Log.v("sajid","called getInfoContents");

        // Getting view from the layout file info_window_layout
        View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

        // Getting reference to the TextView to set latitude
        TextView infoName = (TextView) v.findViewById(R.id.info_window_name);
        TextView infoCuisine = (TextView) v.findViewById(R.id.info_window_cuisine);

        infoName.setText(selectedServeMap.s.getName());
        infoCuisine.setText(selectedServeMap.s.getCuisine());

        // Returning the view containing InfoWindow contents
        return v;

    }
}

