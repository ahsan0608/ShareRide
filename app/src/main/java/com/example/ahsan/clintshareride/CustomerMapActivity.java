package com.example.ahsan.clintshareride;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.vision.text.Line;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialEditText;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,RoutingListener{


    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    //play services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FrameLayout rootLayout;

    private int status;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout, mRequest, mSettings, mHistory, mRideStatus;

    private LatLng pickupLocation,pickupLatLng;

    private Boolean requestBol = false;

    private Marker pickupMarker,pickupMarkerDriverActivity;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng;

    private float rideDistance;
    private String customerIdForDriver = "",tempKey="",tempKeyForDriver="", destinationDriver,titlee,detailss,customersIdfromRequest, driverIdForCustomer;

    String sCustomerId,sDestination,sDestinationLat,sDestinationLng,sTitle,sDetails;

    private LinearLayout mDriverInfo;
    private LinearLayout mCustomerInfo, mNewRequest;

    private ImageView mDriverProfileImage,mCustomerProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;
    private TextView mCustomerName, mCustomerDestination,mRequestTitle,mRequestDetails;
    private Button mCustomerPhone,mTest,rideComplete,bnewRequest,bCancel,bOk;

    private Switch mWorkingSwitch;

    MaterialAnimatedSwitch location_switch;

    //private RadioGroup mRadioGroup;
    GeoFire geoFire;

    private RatingBar mRatingBar;
    private String driverFoundID;
    String titleValue;
    String descValue;

    private int X=0;

    private Thread repeatTaskThread;

    private Boolean check;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        //status = 0;



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0,0.0);

        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);
        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);

        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);

        mDriverName = (TextView) findViewById(R.id.driverName);
        mDriverPhone = (TextView) findViewById(R.id.driverPhone);
        mDriverCar = (TextView) findViewById(R.id.driverCar);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        //mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        //mRadioGroup.check(R.id.UberX);

        //mLogout = (Button) findViewById(R.id.logout);
        mRequest = (Button) findViewById(R.id.request);
        mTest = findViewById(R.id.test);
        //mSettings = (Button) findViewById(R.id.settings);
        //mHistory = (Button) findViewById(R.id.history);
        mRideStatus = (Button) findViewById(R.id.rideStatus);

        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);

        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = findViewById(R.id.customerPhone);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
        mRequestTitle = findViewById(R.id.requestTitle);
        mRequestDetails = findViewById(R.id.requestDetails);
        rideComplete = findViewById(R.id.completeStatus);
        bnewRequest = findViewById(R.id.newRequest);
        bCancel = findViewById(R.id.cancel);
        bOk = findViewById(R.id.ok);

        mNewRequest = findViewById(R.id.newHelpLayout);














        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriverInfo.setVisibility(View.GONE);
                Intent i = new Intent(CustomerMapActivity.this, ChooseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });





//    ************************************************** NEEDED**************************************


        mRideStatus.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                switch(status){

                    case 1:

                        status=2;
                        mRideStatus.setText("drive completed");


                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }

                        break;

                    case 2:


                        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");

                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    tempKeyForDriver = dataSnapshot.getValue().toString();

                                    Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });





                        addCustomerRequestToDriverId();


//
//                        //final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//                        //DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");
//
//                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.exists()){
//                                    tempKeyForDriver = dataSnapshot.getValue().toString();
//
//                                    //Toast.makeText(CustomerMapActivity.this,"key YYY:"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//
//                                    DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKeyForDriver).child(driverId).child("customerRequest");
//                                    DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
//                                    moveCustomerRequestToDriverId(fromPath,toPath);
//
////                    DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("ahsan");
////                    DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child("john").child("pink");
////                    moveCustomerRequestToDriverId(fromPath,toPath);
//
//
//
//
//
//
//                                }else {
//                                    Toast.makeText(CustomerMapActivity.this,"No dataSnapshot exists!",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });







                        //Toast.makeText(CustomerMapActivity.this,"Customer Request added!",Toast.LENGTH_SHORT).show();

                        mRideStatus.setText("drive completed");

                        status=3;

                        break;

                    case 3:
                        disconnectDriver();
                        recordRide();
                        endRideDriver();
                        Toast.makeText(CustomerMapActivity.this,"Stage 3! Ended ride.",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(CustomerMapActivity.this, ChooseActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);


                        break;
                }

                return;
            }

            //return;
            //break;
        });





//        mTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("ahsan");
//                DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child("john").child("pink");
//                moveCustomerRequestToDriverId(fromPath,toPath);
//
//
//
//            }
//        });





        rideComplete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rideComplete.getBackground().setAlpha(100);

                        disconnectDriver();
                        recordRide();
                        endRideDriver();

                        //status = 0;

                        break;
                    case MotionEvent.ACTION_UP:
                        rideComplete.getBackground().setAlpha(255);
                        //status = 0;
                        X = 0;
                        rideComplete.setVisibility(View.GONE);
                        mRideStatus.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });






        rideComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                switch(status) {
//                    case 2:


                        disconnectDriver();
                        recordRide();
                        endRideDriver();
                        rideComplete.setVisibility(View.GONE);
                        mRideStatus.setVisibility(View.VISIBLE);
                        //status = 0;


                Intent i = new Intent(CustomerMapActivity.this, ChooseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                return;

                }



            //}
        });






//        mRideStatus.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//
//                if (X==1){
//
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        mRideStatus.getBackground().setAlpha(100);
//
//
////                        switch(status){
////                            case 1:
//                        //deleteTempDatabase();
//                        //Toast.makeText(CustomerMapActivity.this,"mRiderStatus status 1",Toast.LENGTH_SHORT).show();
//
//                        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                        DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("testKey");
//                        testRef.setValue("1");
//
//                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");
//
//                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    tempKeyForDriver = dataSnapshot.getValue().toString();
//
//                                    //Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
//
//
//                        status = 2;
//                        //erasePolylines();
//                        if (destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0) {
//                            getRouteToMarker(destinationLatLng);
//                        }
//                        //mRideStatus.setText("drive completed");
//
//                        break;
//                    //}
//
//
//                    case MotionEvent.ACTION_UP:
//
//                        switch (status) {
//                            case 2:
//                                addCustomerRequestToDriverId();
//                                rideComplete.setVisibility(View.VISIBLE);
//                                mRideStatus.setVisibility(View.GONE);
//                                mRideStatus.getBackground().setAlpha(255);
//
//
//                                //return;
//
//                        }
//
//                }
//
//            } else {
//                    Toast.makeText(CustomerMapActivity.this,"Not seen request",Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//
//        });







        //    ************************************************** NEEDED**************************************

        bnewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                final DatabaseReference isRequestExist = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequest");
//
//                isRequestExist.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            isRequestExist.removeValue();
//
//                            //Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });

                    X = 1;
                    status = 1;
                    mNewRequest.setVisibility(View.GONE);
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                    getAssignedCustomerRequestDetails();

                   // finish();
            }
        });












//        mRideStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                switch(status){
//                    case 1:
//                        //deleteTempDatabase();
//
//                        //Toast.makeText(CustomerMapActivity.this,"mRiderStatus status 1",Toast.LENGTH_SHORT).show();
//
//
//
//                        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");
//
//                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.exists()){
//                                    tempKeyForDriver = dataSnapshot.getValue().toString();
//
//                                    //Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
//
////                        DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKeyForDriver).child(driverId).child("customerRequest");
////                        DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId);
//                        //addCustomerRequestToDriverId(fromPath,toPath);
//
//
//                        addCustomerRequestToDriverId();
//
//
//
//
//                        //notifyCustomer();
//
//
//                        //connectDriver();
//
//
//                        status=2;
//                        //erasePolylines();
//                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//                            getRouteToMarker(destinationLatLng);
//                        }
//                        //mRideStatus.setText("drive completed");
//
//                        rideComplete.setVisibility(View.VISIBLE);
//                        mRideStatus.setVisibility(View.GONE);
//
//                        //break;
////                    case 2:
////                        //status=0;
////                        disconnectDriver();
////                        recordRide();
////                        endRideDriver();
////                        break;
//                return;
//                }
//            }











//                       String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");
//
//                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.exists()){
//                                    tempKeyForDriver = dataSnapshot.getValue().toString();
//
//                                    //Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
//
//                        addCustomerRequestToDriverId();
//
//
//
//
//                        //notifyCustomer();
//
//
//                        //connectDriver();
//
//
//                        //erasePolylines();
//                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//                            getRouteToMarker(destinationLatLng);
//                        }
//                        rideComplete.setVisibility(View.VISIBLE);
//                        mRideStatus.setVisibility(View.GONE);
//                        mRideStatus.setEnabled(false);
//
//                return;


//        });




//        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
//        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    connectDriver();
//                }else{
//                    disconnectDriver();
//                }
//            }
//        });

        //initialization view
        location_switch = findViewById(R.id.location_switch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if (isOnline) {
                    connectDriver();
                    getAssignedCustomer();

                    check = true;
                    //alertRequest();
                    RepeatTask();

//                    startLocationUpdates();
//                    displayLocation();
                    Snackbar snackbar = Snackbar.make(mapFragment.getView(),"Working as driver!",Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {

                    disconnectDriver();
                    //stopLocationUpdates();
                    Snackbar snackbar = Snackbar.make(mapFragment.getView(),"Working as people!",Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbar.show();

                    check = false;

                    repeatTaskThread.interrupt();


//                    if (pickupMarker != null){
//                        pickupMarker.remove();
//                        mMap.clear();
//                        //handler.removeCallbacks(drawPathRunnable);
//                        Snackbar.make(mapFragment.getView(),"You are offline",Snackbar.LENGTH_SHORT).show();
//                    }
                }
            }
        });




        rootLayout = findViewById(R.id.rootLayout2);

//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });






        //    ************************************************** NEEDED**************************************

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (requestBol){
                    endRide();


                }else{

                    requestBol = true;

//                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//
//
//                    GeoFire geoFire = new GeoFire(ref);
//                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));

                    mRequest.setText("Getting your Driver....");

                    getClosestDriver();

                    //showDetailsDialog();
//
//                    Intent requestDetailsIntent = new Intent(CustomerMapActivity.this,RequestDetailsActivity.class);
//                    requestDetailsIntent.putExtra("driverFoundID", driverFoundID);
//                    startActivity(requestDetailsIntent);



                }
            }
        });




//        mSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class);
//                startActivity(intent);
//                return;
//            }
//        });
//
//        mHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
//                intent.putExtra("customerOrDriver", "Customers");
//                startActivity(intent);
//                return;
//            }
//        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();
            }
            @Override
            public void onError(Status status1) {
                // TODO: Handle the error.
            }
        });
        //getAssignedCustomer();
    }

    private void RepeatTask()
    {
        repeatTaskThread = new Thread()
        {
            public void run()
            {
                while (true)
                {

//                    FetchURL fu = new FetchURL();
//                    fu.Run("http://192.168.0.10/joins.txt");
//                    String o = fu.getOutput();
                    // Update TextView in runOnUiThread
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            final DatabaseReference requestId = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("Request");


                            requestId.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){

                                        requestId.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Toast.makeText(CustomerMapActivity.this,"New Child Added!",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });






                        }
                    });
                    try
                    {
                        // Sleep for
                        Thread.sleep(5000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        repeatTaskThread.start();
    }

    private void alertRequest() {

        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference requestId = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("Request");

        requestId.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(CustomerMapActivity.this,"New Child Added!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void notifyCustomer() {


        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference requestedCustomerId = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest").child("customerRideId");

        requestedCustomerId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customersIdfromRequest = dataSnapshot.getValue().toString();

                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(customersIdfromRequest).child("receivedDriversId");

                    //DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData");
                    //String requestTempId = tempRef.push().getKey();
                    customerRef.setValue(driverId);

                    Toast.makeText(CustomerMapActivity.this,"customersIdfromRequest: "+customersIdfromRequest,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }

    private void deleteTempDatabase() {

    }




    private void addCustomerRequestToDriverId() {



        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");

        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    tempKeyForDriver = dataSnapshot.getValue().toString();

                    //Toast.makeText(CustomerMapActivity.this,"key YYY:"+tempKeyForDriver,Toast.LENGTH_SHORT).show();

                    DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKeyForDriver).child(driverId).child("customerRequest");
                    DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
                    moveCustomerRequestToDriverId(fromPath,toPath);


                    Toast.makeText(CustomerMapActivity.this,"Customer request added.",Toast.LENGTH_SHORT).show();


//                    DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("ahsan");
//                    DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child("john").child("pink");
//                    moveCustomerRequestToDriverId(fromPath,toPath);



                }else {
                    Toast.makeText(CustomerMapActivity.this,"No dataSnapshot exists!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

//        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKeyForDriver).child(driverId).child("customerRequest");
//        Toast.makeText(CustomerMapActivity.this,"tempKeyForDriver XXX :"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//
//
//        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
//
//
//                    Toast.makeText(CustomerMapActivity.this,"In assigned CUSTOMER Ref eventListener IF loop",Toast.LENGTH_SHORT).show();
//
//
//
//                    if(dataSnapshot.child("customerRideId")!=null){
//                        sCustomerId = dataSnapshot.child("customerRideId").getValue().toString();
//                    }
//                    if(dataSnapshot.child("destination")!=null){
//                        sDestination = dataSnapshot.child("destination").getValue().toString();
//                    }
//                    if(dataSnapshot.child("destinationLat")!=null){
//                        sDestinationLat = dataSnapshot.child("destinationLat").getValue().toString();
//                    }
//                    if(dataSnapshot.child("destinationLng").getValue()!=null){
//                        sDestinationLng = dataSnapshot.child("destinationLng").getValue().toString();
//                    }
//                    if(dataSnapshot.child("title").getValue()!=null){
//                        sTitle = dataSnapshot.child("title").getValue().toString();
//                    }
//                    if(dataSnapshot.child("details").getValue()!=null){
//                        sDetails = dataSnapshot.child("details").getValue().toString();
//                    }
//
//                }else {
//                    Toast.makeText(CustomerMapActivity.this,"No data found!",Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//
//        //tempDatabase
//        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
//
//        HashMap map = new HashMap();
//        map.put("customerRideId", sCustomerId);
//        map.put("destination", sDestination);
//        map.put("destinationLat", sDestinationLat);
//        map.put("destinationLng", sDestinationLng);
//        map.put("title",sTitle);
//        map.put("details",sDetails);
//
//        driverRef.updateChildren(map);

    }








    private void moveCustomerRequestToDriverId(final DatabaseReference fromPath, final DatabaseReference toPath) {


        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {

                            Toast.makeText(CustomerMapActivity.this,"Copied!",Toast.LENGTH_SHORT).show();
                        } else {
                            //notifyCustomer();
                            Toast.makeText(CustomerMapActivity.this,"Copy failed!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }






    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(customerIdForDriver).child("history");

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerIdForDriver);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }



    //REAL

//    private void getAssignedCustomer(){
//        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest").child("customerRideId");
//        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    status = 1;
//                    customerIdForDriver = dataSnapshot.getValue().toString();
//                    getAssignedCustomerPickupLocation();
//                    getAssignedCustomerDestination();
//                    getAssignedCustomerInfo();
//                }else{
//                    endRideDriver();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }



    private void getAssignedCustomer(){

        //Toast.makeText(CustomerMapActivity.this,"Inside getAssignedCustomer",Toast.LENGTH_SHORT).show();

        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedNewReq = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("Request").child("New");

        assignedNewReq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    //Toast.makeText(CustomerMapActivity.this,"Got customerIdForDriver "+customerIdForDriver,Toast.LENGTH_SHORT).show();

                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                    getAssignedCustomerRequestDetails();


                    //customerIdForDriver = dataSnapshot.getValue().toString();

                    //status = 1;
                    notifyNewRequest();
                }else{
                    endRideDriver();
                    //Toast.makeText(CustomerMapActivity.this,"Cant find customerIdForDriver",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Toast.makeText(CustomerMapActivity.this,"Driver id: "+driverId,Toast.LENGTH_SHORT).show();

    }

    private void notifyNewRequest() {

        mNewRequest.setVisibility(View.VISIBLE);




//        Intent intent = new Intent(CustomerMapActivity.this,TempMapActivity.class);
//        startActivity(intent);




//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        final DatabaseReference isRequestExist = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequest");
//
//        isRequestExist.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    isRequestExist.removeValue();
//                    //Toast.makeText(CustomerMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

        return;

    }

    private void endRideDriver(){

        mRideStatus.setText("picked customer");
        //erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequest");
        driverRef.removeValue();


        DatabaseReference customerReqKey = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequestTempKey");
        customerReqKey.removeValue();

        DatabaseReference tempData = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKeyForDriver);
        tempData.removeValue();




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");


        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerIdForDriver);

        customerIdForDriver="";
        rideDistance = 0;


        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if(pickupMarkerDriverActivity != null){
            pickupMarkerDriverActivity.remove();
        }


        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.drawable.ic_person_outline_black_24dp);


        //restartActivity();
    }

    private void getAssignedCustomerRequestDetails() {

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKey).child(driverId).child("customerRequest");

        //Toast.makeText(CustomerMapActivity.this,"getAssignedCustomerRequestDetails",Toast.LENGTH_SHORT).show();

        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("title")!=null){
                        titlee = map.get("title").toString();
                        mRequestTitle.setText("Title: " + titlee);
                    }
                    else{
                        mRequestTitle.setText("Title: --");
                    }

                    if(map.get("details") != null){
                        detailss = map.get("title").toString();
                        mRequestDetails.setText("Details: "+detailss);
                    }
                    else {
                        mRequestDetails.setText("Details: --");
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerIdForDriver);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mCustomerName.setText("Name: "+map.get("name").toString());
                    }else {
                        mCustomerName.setText("Name: --");
                    }
                    if(map.get("phone")!=null){
                        mCustomerPhone.setText("Phone: "+map.get("phone").toString());
                    }else {
                        mCustomerPhone.setText("Phone: --");
                    }
                    if(map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData").child(tempKey).child(driverId).child("customerRequest");

        //DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");

        //Toast.makeText(CustomerMapActivity.this,"getAssignedCustomerDestination",Toast.LENGTH_SHORT).show();

        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerIdForDriver).child("l");

        //Toast.makeText(CustomerMapActivity.this,"getAssignedCustomerPickupLocation",Toast.LENGTH_SHORT).show();


        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerIdForDriver.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarkerDriverActivity = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getRouteToMarker(LatLng pickupLatLng) {
        if (pickupLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    private void connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallbackDriver, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }


    private void disconnectDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallbackDriver);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        //Toast.makeText(CustomerMapActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();
    }


    private void showDetailsDialog() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Details");
        dialog.setMessage("Please write details of your request");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.details_about_help,null);

        final MaterialEditText editTitlee = register_layout.findViewById(R.id.edtTitle);
        final MaterialEditText editDetails = register_layout.findViewById(R.id.edtDetails);

        dialog.setView(register_layout);


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();



                //btnSignIn.setEnabled(false);


                if (TextUtils.isEmpty(editTitlee.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Title", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editDetails.getText().toString())) {
                    Snackbar.make(rootLayout, "Details field can't be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

//                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
//                waitingDialog.show();

                titleValue = editTitlee.getText().toString().trim();
                descValue = editDetails.getText().toString().trim();

                dialogInterface.dismiss();
                //getClosestDriver();




            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        dialog.show();


    }

    private int radius = 1;
    private Boolean driverFound = false;



    DatabaseReference mCustomerDatabase;
    List<String> commentKeys = new ArrayList<String>();
    GeoQuery geoQuery;


    private void getClosestDriver(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Details");
        dialog.setMessage("Please write details of your request");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.details_about_help,null);

        final MaterialEditText editTitlee = register_layout.findViewById(R.id.edtTitle);
        final MaterialEditText editDetails = register_layout.findViewById(R.id.edtDetails);

        dialog.setView(register_layout);


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

                //btnSignIn.setEnabled(false);

                if (TextUtils.isEmpty(editTitlee.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Title", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editDetails.getText().toString())) {
                    Snackbar.make(rootLayout, "Details field can't be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

//                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
//                waitingDialog.show();

                titleValue = editTitlee.getText().toString().trim();
                descValue = editDetails.getText().toString().trim();



                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");



                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                //DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("customerRequestTempKey").child("tempKey");

                //DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData");
                //String requestTempId = tempRef.push().getKey();
                //String order_number = String.valueOf(System.currentTimeMillis());
                //driverRef.setValue(order_number);
//

                //DatabaseReference refReq = FirebaseDatabase.getInstance().getReference("CustomerRequest");

                final String order_number = String.valueOf(System.currentTimeMillis());

                HashMap map = new HashMap();
                map.put("customerRideId", userId);
                map.put("destination", destination);
                map.put("destinationLat", destinationLatLng.latitude);
                map.put("destinationLng", destinationLatLng.longitude);
                map.put("title",titleValue);
                map.put("details",descValue);
                map.put("requestKey",order_number);
                driverRef.child("MyRequest").child(order_number).updateChildren(map);






                GeoFire geoFireSetLocation = new GeoFire(ref);
                geoFireSetLocation.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));




                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));




                DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

                GeoFire geoFire = new GeoFire(driverLocation);
                geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
                geoQuery.removeAllListeners();

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        if (!driverFound && requestBol){

                            commentKeys.add(key);

                            String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//                            DatabaseReference notifyDriverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("NewReq");
//                            notifyDriverRef.setValue("Yes");


                            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

                            //DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("customerRequestTempKey").child("tempKey");

                            //DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child("tempData");
                            //String requestTempId = tempRef.push().getKey();
                            //String order_number = String.valueOf(System.currentTimeMillis());
                            //driverRef.setValue(order_number);
//

                            //DatabaseReference refReq = FirebaseDatabase.getInstance().getReference("CustomerRequest");

                            HashMap map = new HashMap();
                            map.put("customerRideId", customerId);
                            map.put("destination", destination);
                            map.put("destinationLat", destinationLatLng.latitude);
                            map.put("destinationLng", destinationLatLng.longitude);
                            map.put("title",titleValue);
                            map.put("details",descValue);
                            map.put("requestKey",order_number);
                            driverRef.child("Request").child(order_number).updateChildren(map);


                            //                            //tempDatabase

//                            HashMap mapTemp = new HashMap();
//                            mapTemp.put("customerRideId", customerId);
//                            mapTemp.put("destination", destination);
//                            mapTemp.put("destinationLat", destinationLatLng.latitude);
//                            mapTemp.put("destinationLng", destinationLatLng.longitude);
//                            mapTemp.put("title",titleValue);
//                            mapTemp.put("details",descValue);
//                            mapTemp.put("tempKey",order_number);
//                            tempRef.child(order_number).child(key).child("customerRequest").updateChildren(mapTemp);





                            //popUpDriverInfo();



                            mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

                            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                        Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                        if (driverFound){
                                            return;
                                        }

                                        driverFound = true;
                                        driverFoundID = dataSnapshot.getKey();

//                                        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
//                                        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                        HashMap map = new HashMap();
//                                        map.put("customerRideId", customerId);
//                                        map.put("destination", destination);
//                                        map.put("destinationLat", destinationLatLng.latitude);
//                                        map.put("destinationLng", destinationLatLng.longitude);
//                                        map.put("title",titleValue);
//                                        map.put("details",descValue);
//                                        driverRef.updateChildren(map);

                                        getDriverLocation();
                                        getDriverInfo();
                                        getHasRideEnded();
                                        mRequest.setText("Looking for Driver Location....");
                                        //restartActivity();

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                        } else {
                            Toast.makeText(CustomerMapActivity.this,"Can't find",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {
//                if (!driverFound)
//                {
//                    radius++;
//                    getClosestDriver();
//                }
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });


                //dialogInterface.dismiss();
                //getClosestDriver();


            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        dialog.show();

    }

    private void popUpDriverInfo() {

        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference assignedDriverfId = FirebaseDatabase.getInstance().getReference().child("Users").child(customerId).child("receivedDriversId");

        //Toast.makeText(CustomerMapActivity.this,"ID : "+userId,Toast.LENGTH_SHORT).show();

        assignedDriverfId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    //Toast.makeText(CustomerMapActivity.this,"customerRequest EXIST",Toast.LENGTH_SHORT).show();

                    //getDriverLocation();
                    //getDriverInfo();
                    //();
                    mRequest.setText("Looking for Driver Location....");




                }else{

                    //Toast.makeText(CustomerMapActivity.this,"customerRequest doesn't EXIST",Toast.LENGTH_SHORT).show();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;

    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Request Picked! ");
                        mRequest.setBackgroundColor(Color.GREEN);
                    }else{
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                    }



                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
//    private void getDriverInfo(){
//        mDriverInfo.setVisibility(View.VISIBLE);
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("receivedDriversId");
//
//
//
//        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    driverIdForCustomer = dataSnapshot.getValue().toString();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//
//    }



    private void getDriverInfo(){
        //mDriverInfo.setVisibility(View.VISIBLE);


        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name")!=null){
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }










    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID).child("customerRequest").child("customerRideId");

        //Toast.makeText(CustomerMapActivity.this,"getHasRideEnded",Toast.LENGTH_SHORT).show();

        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    //endRide();
                    //endRideDriver();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }




    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }



        LocationCallback mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    if(getApplicationContext()!=null){
                        mLastLocation = location;

                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//                    if(!getDriversAroundStarted)
//                        getDriversAround();
                    }
                }
            }
        };








        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null){
            if (location_switch.isChecked()) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                if (pickupMarker != null) {
                    pickupMarker.remove();
                    pickupMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                            .position(new LatLng(latitude,longitude))
                            .title("Your Location"));

                    //move camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));
                    //Draw animation rotate marker
                    rotateMarker(pickupMarker,-360,mMap);
                }
            }
        } else {
            Log.d("ERROR","Cant get your location");
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
                float t = interpolator.getInterpolation((float) elapsed/duration);
                float rot = t*i+(1-t)*startRotation;
                mCurrent.setRotation(-rot > 180 ? rot/2:rot);

                if (t<1.0) {
                    handler.postDelayed(this,16);
                }
            }
        });

    }



    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID).child("customerRequest");

            //Toast.makeText(CustomerMapActivity.this,"endRide",Toast.LENGTH_SHORT).show();

            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

        //Toast.makeText(CustomerMapActivity.this,"endRideGeoFire",Toast.LENGTH_SHORT).show();


        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("call Uber");

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }




    LocationCallback mLocationCallbackDriver = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(final Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){

                    if(!customerIdForDriver.equals("") && mLastLocation!=null && location != null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");

                    DatabaseReference refThinking = FirebaseDatabase.getInstance().getReference("driversThinking");

                    //Toast.makeText(CustomerMapActivity.this,"Location Callback",Toast.LENGTH_SHORT).show();

                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    final GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    final GeoFire geoFireWorking = new GeoFire(refWorking);


                    DatabaseReference assignedCustomerRefId = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Request");

                    //Toast.makeText(CustomerMapActivity.this,"ID : "+userId,Toast.LENGTH_SHORT).show();

                        assignedCustomerRefId.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    //Toast.makeText(CustomerMapActivity.this,"customerRequest EXIST",Toast.LENGTH_SHORT).show();

                                    geoFireAvailable.removeLocation(userId);
                                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));


                                }else{

                                    //Toast.makeText(CustomerMapActivity.this,"customerRequest doesn't EXIST",Toast.LENGTH_SHORT).show();

                                    geoFireWorking.removeLocation(userId);
                                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });






//                    switch (customerIdForDriver){
//
//                        case "":
//
//                            geoFireWorking.removeLocation(userId);
//                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//
//
////                            DatabaseReference assignedCustomerRefId = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequest");
////
////                            assignedCustomerRefId.addValueEventListener(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    if(dataSnapshot.exists()){
////
////                                        geoFireAvailable.removeLocation(userId);
////                                        geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
////
////
////                                    }else{
////
////                                        geoFireWorking.removeLocation(userId);
////                                        geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
////
////                                    }
////                                }
////
////                                @Override
////                                public void onCancelled(DatabaseError databaseError) {
////                                }
////                            });
//
//                            break;
//
//                        default:
//                            geoFireAvailable.removeLocation(userId);
//                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                            break;
//                    }
                }
            }
        }
    };








    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//                    if(!getDriversAroundStarted)
//                        getDriversAround();
                }
            }
        }
    };

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/



    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }




    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();

    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        //Toast.makeText(CustomerMapActivity.this,"getDriverAround",Toast.LENGTH_SHORT).show();

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));
                mDriverMarker.setTag(key);

                markers.add(mDriverMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}