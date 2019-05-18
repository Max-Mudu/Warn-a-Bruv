package com.example.mudu.warnabruv;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = Home.class.getSimpleName();

    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    //Play services
    private static final int MY_PERMISSION_REQUEST_CODE = 5000;
    private static final int PLAY_SERVICE_RES_REQUEST = 5001;

    private ImageView locationButton;

    private GoogleApiClient mGoogleApiClient;
    private Task<Location> mLastLocation;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire mGeoFire;

    Marker mCurrent;
    SupportMapFragment mapFragment;

    /*
    public Home() {
        auth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                                                                    R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ProfileFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        disableNavigationViewScrollbars(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_home) {

                } else if (id == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                } else if (id == R.id.nav_settings) {

                } else if (id == R.id.nav_help) {

                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                transaction.commit();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        checkConnection();
        scheduleJob();

        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(Home.this, MainActivity.class));
                }
            }
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            checkPlayServices();

            if (mapFragment != null &&
                    Objects.requireNonNull(mapFragment.getView()).findViewById(Integer.parseInt("1")) != null) {
                //Get the button view
                locationButton = ((View) Objects.requireNonNull(mapFragment.getView()).findViewById(Integer.parseInt("1"))
                        .getParent()).findViewById(Integer.parseInt("2"));
                //Set custom icon
                locationButton.setBackgroundResource(R.drawable.ic_my_location);
                //Place it in the bottom right
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 30);
                locationButton.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
                displayLocation();
                Snackbar.make(Objects.requireNonNull(mapFragment.getView()), "You Are Online", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        */

        //Geo Fire
        drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        mGeoFire = new GeoFire(drivers);

        setUpLocation();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.profile, menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.drawer_menu, menu);
          return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
//        if (((FirebaseApplication)getApplication()).mAuthListener != null) {
//            auth.removeAuthStateListener(((FirebaseApplication)getApplication()).mAuthListener);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);

//        auth.addAuthStateListener(((FirebaseApplication)getApplication()).mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.parseColor("#B30000");
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                api.getErrorDialog(this, resultCode, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.i("MainActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
            }
        }
    };

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setMessage(R.string.location_permission_text)
                        .setIcon(R.drawable.ic_near_me_black)
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions();
                            }
                        })
                        .create()
                        .show();
            } else {
                //No explanation needed. Continue to request the necessary permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    private void requestPermissions() {
        //Request runtime permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
        mLastLocation = mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Get last known location. In some rare situations this can be null.
                        if (location != null) {
                            onLocationChanged(location);
                        } else {
                            Log.d("Error", "Cannot get your Location");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last location");
                        e.printStackTrace();
                    }
                });
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startLocationUpdates() {
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
        //Disable Location
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API client connection failed!");
    }

    @Override
    public void onLocationChanged(Location location) {
        mGoogleApiClient.connect();
        if (!locationButton.isEnabled()) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            //update to firebase
            mGeoFire.setLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                    new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Add Marker
                            if (mCurrent != null) {
                                mCurrent.remove(); //Removing Marker that is already there
                            }
                            mCurrent = mMap.addMarker(new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                                                        .position(new LatLng(latitude, longitude))
                                                        .title("Me"));

                            //Move camera to this position
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                                                                                    21));
                            //Draw animation to rotate marker
                            rotateMarker(mCurrent, -360, mMap);
                        }
                    });
        }
    }

    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float)elapsed / duration);
                float rot = t * i + (1 - t) * startRotation;
                mCurrent.setRotation(-rot > 180?rot/2:rot);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
