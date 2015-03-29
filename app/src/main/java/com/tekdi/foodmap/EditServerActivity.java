package com.tekdi.foodmap;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditServerActivity extends FragmentActivity
        implements OnItemSelectedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private String cuisineSelected = "";
    private Long serverId;
    private Boolean addingNewServer = false;
    private ProgressBar mActivityIndicator;
    private EditText mAddress;
    private Location mLocation;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_server);

        Spinner spinner = (Spinner) findViewById(R.id.cuisine_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cuisine_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        if (Prefs.getServeIdPref(this).equals("")) {
            setTitle("Add Server");
            addingNewServer = true;

        } else {
            EditText editText = (EditText) findViewById(R.id.server_name_text);
            editText.setText(Prefs.getSERVER_NAME_PREF(this));
            editText = (EditText) findViewById(R.id.server_phone_text);
            editText.setText(Prefs.getSERVER_PHONE_PREF(this));
            editText = (EditText) findViewById(R.id.server_address_text);
            editText.setText(Prefs.getSERVER_ADDRESS_PREF(this));
            String cuisine = Prefs.getSERVER_CUISINE_PREF(this);
            serverId = Long.parseLong(Prefs.getServeIdPref(this));
            spinner.setSelection(adapter.getPosition(cuisine));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_serve, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_save:
                setupServer();
                break;
            case R.id.action_cancel:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        // mLocationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mLocationClient != null)
            mLocationClient.disconnect();
        super.onStop();
    }

    /*
* Called by Location Services when the request to connect the
* client finishes successfully. At this point, you can
* request the current location or start periodic updates
*/
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        mLocation = mLocationClient.getLastLocation();
        if (mLocation == null) {
            Toast.makeText(this, "Unable to get location.", Toast.LENGTH_SHORT).show();
        } else {
            getAddress(getWindow().getDecorView().findViewById(android.R.id.content));
            //Toast.makeText(this, "Connected " + mLocation.getLatitude() + "," + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
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
        mLocation.setLatitude(location.getLatitude());
        mLocation.setLongitude(location.getLongitude());
        getAddress(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        cuisineSelected = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onServerSetupGetPhone(View v) {
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        EditText editText = (EditText) findViewById(R.id.server_phone_text);
        editText.setText(mPhoneNumber);
    }

    public void onServerSetupGetAddress(View v) {

        mActivityIndicator =
                (ProgressBar) findViewById(R.id.address_progress);
        mAddress =(EditText) findViewById(R.id.server_address_text);
        mLocationClient = new LocationClient(this, this, this);

        mLocationClient.connect();

    }

    private void setupServer() {

        EditText editText = (EditText) findViewById(R.id.server_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_phone_text);
        String phone = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_address_text);
        String address = editText.getText().toString();

        ServeFoodEntity server = new ServeFoodEntity();
        server.setId(serverId);
        server.setAddress(address);
        server.setCuisine(cuisineSelected);
        server.setName(name);
        server.setPhone(phone);
        Log.v("sajid", "sajid getting reg id " + Prefs.getDeviceRegIdPref(this));
        server.setServerRegId(Prefs.getDeviceRegIdPref(this));

        Geocoder coder = new Geocoder(this);
        List<Address> addresses;
        Double latitude;
        Double longitude;

        if (Geocoder.isPresent()) {
            try {
                addresses = coder.getFromLocationName(address, 5);
                Address location = addresses.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                server.setLatitude(latitude.toString());
                server.setLongitude(longitude.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (addingNewServer)
            new ServeFoodEntityEndpointAsyncTask(this).execute(new Pair<Context, ServeFoodEntity>(this, server));
        else
            new ServeFoodEntityEditEndpointAsyncTask(this).execute(new Pair<Context, ServeFoodEntity>(this, server));
    }

    public void doneEditingServer(ServeFoodEntity result) {
        if (result != null) {
            Prefs.setServerIdPref(this, result.getId().toString());
            Prefs.setSERVER_NAME_PREF(this, result.getName());
            Prefs.setSERVER_PHONE_PREF(this, result.getPhone());
            Prefs.setSERVER_ADDRESS_PREF(this, result.getAddress());
            Prefs.setSERVER_CUISINE_PREF(this, result.getCuisine());
        } else {
            Log.v("sajid","EditServerActivity : failed to setup server");
            Toast.makeText(this, "Failed to setup server", Toast.LENGTH_SHORT).show();

        }
        finish();
    }

    public void getAddress(View v) {
        // Ensure that a Geocoder services is available
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.GINGERBREAD
                &&
                Geocoder.isPresent()) {
            // Show the activity indicator
            mActivityIndicator.setVisibility(View.VISIBLE);
            /*
             * Reverse geocoding is long-running and synchronous.
             * Run it on a background thread.
             * Pass the current location to the background task.
             * When the task finishes,
             * onPostExecute() displays the address.
             */
            (new GetAddressTask(this)).execute(mLocation);
        }
    }

    private class GetAddressTask extends
            AsyncTask<Location, Void, String> {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("sajid",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("Unable to get address. Try again or enter manually.");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("sajid", errorString);
                e2.printStackTrace();
                return "Unable to get address. Try again or enter manually.";
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                return String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
            } else {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            // Set activity indicator visibility to "gone"
            mActivityIndicator.setVisibility(View.GONE);
            // Display the results of the lookup.
            mAddress.setText(address);
        }
    }
    
    public void onConnectStripe(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_5Wea0rWSuwNYSD77NmQ6Xzn1jbbBXHkh"));
        startActivity(browserIntent);
        
    }
}
