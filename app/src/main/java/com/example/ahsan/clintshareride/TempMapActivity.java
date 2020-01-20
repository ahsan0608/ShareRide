package com.example.ahsan.clintshareride;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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

public class TempMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener{

    private String customerId = "";


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


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        notifyNewRequest();



        bnewRequest = findViewById(R.id.newRequest);
        bCancel = findViewById(R.id.cancel);
        destinationLatLng = new LatLng(0.0,0.0);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mRideStatus = (Button) findViewById(R.id.rideStatus);

        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);

        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);

        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = findViewById(R.id.customerPhone);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
        mRequestTitle = findViewById(R.id.requestTitle);
        mRequestDetails = findViewById(R.id.requestDetails);

        mNewRequest = findViewById(R.id.newHelpLayout);

        status = 1;


        bnewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                disconnectDriver();
                //recordRide();
                endRideDriver();
                Toast.makeText(TempMapActivity.this,"Stage 3! Ended ride.",Toast.LENGTH_SHORT).show();

                Intent i = new Intent(TempMapActivity.this, ChooseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);


            }
        });



        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");

                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    tempKeyForDriver = dataSnapshot.getValue().toString();

                                    Toast.makeText(TempMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                addCustomerRequestToDriverId();


                mCustomerInfo.setVisibility(View.GONE);
                mNewRequest.setVisibility(View.VISIBLE);




            }
        });






//        mRideStatus.setOnClickListener(new View.OnClickListener() {



//            @Override
//            public void onClick(View v) {
//
//
//
//
//                switch(status){
//
//                    case 1:
//
//                        status=2;
//                        mRideStatus.setText("drive completed");
//
//
//                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//                            getRouteToMarker(destinationLatLng);
//                        }
//
//                        break;
//
//                    case 2:
//
//
//                        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        DatabaseReference assignedTempKeyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequestTempKey").child("tempKey");
//
//                        assignedTempKeyRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    tempKeyForDriver = dataSnapshot.getValue().toString();
//
//                                    Toast.makeText(TempMapActivity.this,"In assigned TEMPKey Ref eventListener"+tempKeyForDriver,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
//
//
//
//
//
//                        addCustomerRequestToDriverId();
//
//
//
//                        mRideStatus.setText("drive completed");
//
//                        status=3;
//
//                        break;
//
//                    case 3:
//                        disconnectDriver();
//                        //recordRide();
//                        endRideDriver();
//                        Toast.makeText(TempMapActivity.this,"Stage 3! Ended ride.",Toast.LENGTH_SHORT).show();
//
//                        Intent i = new Intent(TempMapActivity.this, ChooseActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(i);
//
//
//                        break;
//                }
//
//                return;
//            }
//
//            //return;
//            //break;
//        });














        mCustomerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerId);
                mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("phone")!=null){
                                String phone = map.get("phone").toString();
                                mCustomerPhone.setText("Phone: "+map.get("phone").toString());

                                makeCall(phone);

                            }else {
                                mCustomerPhone.setText("Phone: --");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        //mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
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

//        mSettings = (Button) findViewById(R.id.settings);
//        mLogout = (Button) findViewById(R.id.logout);
//        mRideStatus = (Button) findViewById(R.id.rideStatus);
//        mHistory = (Button) findViewById(R.id.history);
//        mRideStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch(status){
//                    case 1:
//                        status=2;
//                        erasePolylines();
//                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//                            getRouteToMarker(destinationLatLng);
//                        }
//                        mRideStatus.setText("drive completed");
//
//                        break;
//                    case 2:
//                        recordRide();
//                        endRide();
//                        break;
//                }
//            }
//        });

//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isLoggingOut = true;
//
//                disconnectDriver();
//
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });
//        mSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DriverMapActivity.this, DriverSettingsActivity.class);
//                startActivity(intent);
//                return;
//            }
//        });
//        mHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DriverMapActivity.this, HistoryActivity.class);
//                intent.putExtra("customerOrDriver", "Drivers");
//                startActivity(intent);
//                return;
//            }
//        });
//
//        getAssignedCustomer();
    }

    private void notifyNewRequest() {

        //mNewRequest.setVisibility(View.VISIBLE);
        // mCustomerInfo.setVisibility(View.VISIBLE);


//        //X = 1;
//        status = 1;
//        mNewRequest.setVisibility(View.GONE);
//        getAssignedCustomerPickupLocation();
//        getAssignedCustomerDestination();
//        getAssignedCustomerInfo();
//        getAssignedCustomerRequestDetails();




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

    private void endRideDriver() {

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
//        if(pickupMarkerDriverActivity != null){
//            pickupMarkerDriverActivity.remove();
//        }


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

    private void addCustomerRequestToDriverId() {


        //Toast.makeText(CustomerMapActivity.this,"In addCustomerRequestToDriverId",Toast.LENGTH_SHORT).show();

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

//                    DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Users").child("ahsan");
//                    DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Users").child("john").child("pink");
//                    moveCustomerRequestToDriverId(fromPath,toPath);


                    Toast.makeText(TempMapActivity.this,"Customer Request added!",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(TempMapActivity.this,"No dataSnapshot exists!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void moveCustomerRequestToDriverId(final DatabaseReference fromPath, final DatabaseReference toPath) {


        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {

                            Toast.makeText(TempMapActivity.this,"Copied!",Toast.LENGTH_SHORT).show();
                        } else {
                            //notifyCustomer();
                            Toast.makeText(TempMapActivity.this,"Copy failed!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void makeCall(String phonee) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);                                                //------********-----//
        callIntent.setData(Uri.parse(phonee));   //"tel:+8801778619115"
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                    getAssignedCustomerRequestDetails();
                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
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
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));
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

    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
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


    private void getAssignedCustomerRequestDetails() {

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("customerRequest");
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
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerId);
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


    private void endRide(){
        mRideStatus.setText("picked customer");
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId="";
        rideDistance = 0;

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){

                    if(!customerId.equals("") && mLastLocation!=null && location != null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (customerId){
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId);
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(TempMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(TempMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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





    private void connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
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
