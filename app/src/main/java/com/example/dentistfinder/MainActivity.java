package com.example.dentistfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.dentistfinder.Controller.DatabaseAccess;
import com.example.dentistfinder.Utils.DentistActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public EditText Specialite;
    public TextView result;
    MapView mapView;
    private GoogleMap map;
    boolean isPermissionGranter;
    public FrameLayout container;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        Specialite = findViewById(R.id.Specialite);
        result = findViewById(R.id.result);
        Specialite.setOnEditorActionListener(editorListener);
        container=findViewById(R.id.container);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mapView.getMapAsync(this);


        Specialite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Specialite.getText().toString().isEmpty()){
                    System.out.println("" + MySQLExecuteQuery("https://dentistfinderdz.000webhostapp.com/post.php?msg=","SELECT longitude, latitude, specialite  \" +\n" +
                            "                        \"FROM PRIVE \" +\n" +
                            "                        \"WHERE specialite LIKE '%\"+spec+\"%'\" +\n" +
                            "                        \"OR nom LIKE '%\" + spec + \"%'\" +\n" +
                            "                        \"OR adresse LIKE '%\" + spec + \"%'\" "));
                }
                }
        });



       /* checkPermission();

        if (isPermissionGranter){

            if (checkGoogleAPIServices()){

                //map = mapview.getMap
                mapView.onCreate(savedInstanceState);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(true);
                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                MapsInitializer.initialize(this);
                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
                map.animateCamera(cameraUpdate);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null)
                {

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Toast.makeText(this,"Location: " +location.toString(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Location null",Toast.LENGTH_SHORT).show();
                }
                } else {
                    Toast.makeText(this,"Google Service Not available",Toast.LENGTH_SHORT).show();
            }
        }   else Toast.makeText(this,"Permission not granted!",Toast.LENGTH_SHORT).show();*/
    }

    private boolean checkGoogleAPIServices() {
        //TODO   check Google API
        return false;
    }

    private void checkPermission() {
        //TODO check permission /
    }

    private final TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch(actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT);
                    break;
            }


            return false;
        }
    };

    ArrayList<String> specialite,telephone,horaire,email,adress,jours,type,nome,lang,lat;

//Function to get infos from database

      String MySQLExecuteQuery(String url, String query){
          map.clear();
          specialite = new ArrayList();
          telephone = new ArrayList();
          horaire = new ArrayList();
          email  = new ArrayList();
          adress = new ArrayList();
          jours = new ArrayList();
          type = new ArrayList();
          nome = new ArrayList();
          lang = new ArrayList();
          lat = new ArrayList();

          String spec =Specialite.getText().toString();
          String toReturn = "Null";
          Response[] response = new Response[1];
          final OkHttpClient client = new OkHttpClient();
          RequestBody formBody = new FormBody.Builder()
                  .add("msg", "SELECT longitude, latitude, specialite, mobile, horaire,nom, email, adresse, jours, type " +
                          "FROM PRIVE " +
                          "WHERE specialite LIKE '%"+spec+"%'" +
                          "OR nom LIKE '%" + spec + "%'" +
                          "OR adresse LIKE '%" + spec + "%'" )
                  .build();
          Request request = new Request.Builder()
                  .url(url + query)
                  .post(formBody)
                  .build();
          try {
              response[0] = client.newCall(request).execute();
              JSONArray jsonArray = new JSONArray(response[0].body().string());
              //JSONObject jsonObject = new JSONObject(response[0].body().string());
              //JSONArray jsonArray = new JSONArray(response[0].body().string());
              for (int i = 0; i < jsonArray.length() ; i++) {
                  JSONObject student = jsonArray.getJSONObject(i);
                  String longitude = student.getString("longitude");
                  String latitude = student.getString("latitude");
                  specialite.add(student.getString("specialite"));
                  telephone.add(student.getString("mobile"));
                  horaire.add( student.getString("horaire"));
                  email.add(student.getString("email"));
                  adress.add(student.getString("adresse"));
                  jours.add(student.getString("jours"));
                  type.add(student.getString("type"));
                  nome.add(student.getString("nom"));
                  lat.add(student.getString("latitude"));
                  lang.add(student.getString("longitude"));

                 // Toast.makeText(getApplicationContext(),nome.get(i),Toast.LENGTH_LONG).show();
                  System.out.println(jsonArray.toString());
                  //Toast.makeText(getApplicationContext(),longitude+" et "+specialite,Toast.LENGTH_LONG).show();

                  LatLng mihoubi = new LatLng(Double.parseDouble(longitude), Double.parseDouble(latitude));
                  map.addMarker(new MarkerOptions().position(mihoubi).title(""+i).snippet(""+nome+","+specialite+""));
                  map.getMinZoomLevel();
                  //map.moveCamera((CameraUpdateFactory.newLatLngZoom(mihoubi, 16)));
                  // map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));

                  System.out.println("Object " + i + ": " + student);
                  toReturn =jsonArray.toString() ;
              }

              // toReturn = jsonArray.toString();


          } catch (IOException | JSONException e) {
              e.printStackTrace();
          }
        return toReturn;
    }


    //what is needed from map and setting markers

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map =googleMap;

//35.6943099, -0.6335498
        LatLng one = new LatLng(35.6943099, -0.6335498);
        map.moveCamera(CameraUpdateFactory.newLatLng(one));

        map.animateCamera(CameraUpdateFactory.zoomTo(10));



        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(MainActivity.this, DentistActivity.class);
                int i = Integer.valueOf(marker.getTitle());
               // Toast.makeText(getApplicationContext(),specialite.get(i),Toast.LENGTH_LONG).show();
                intent.putExtra("Specialite",specialite.get(i));
                intent.putExtra("Mobile",telephone.get(i));
                intent.putExtra("Horaire",horaire.get(i));
                intent.putExtra("Email",email.get(i));
                intent.putExtra("Adresse",adress.get(i));
                intent.putExtra("Jours",jours.get(i));
                intent.putExtra("Type",type.get(i));
                intent.putExtra("Nom",nome.get(i));
                intent.putExtra("Longitude",lang.get(i));
                intent.putExtra("Latitude",lat.get(i));
                startActivity(intent);

                return true;
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void startSecond(View view) {
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }
}





    //JSONObject jsonObject = new JSONObject(response[0].body().string());

            /*
            for (int i = 0; i < jsonArray.length() ; i++) {
                try {
                    JSONObject pharmacyJsonObject = jsonArray.getJSONObject(i);
                    JSONObject pharmacyJsonGeometry = pharmacyJsonObject.getJSONObject("geometry");
                    JSONObject pharmacyJsonLocation = pharmacyJsonGeometry.getJSONObject("location");
                    String pharmacy_name = beutifyPharmacyName(pharmacyJsonObject.getString("name"));
                    String pharmacy_latitude = pharmacyJsonLocation.getString("lat");
                    String pharmacy_longitude = pharmacyJsonLocation.getString("lng");
                    float[] distance = new float[1];
                    double startLat = Double.parseDouble(pharmacy_latitude);
                    double startLong = Double.parseDouble(pharmacy_longitude);
                    double endLat = Double.parseDouble(deviceLat);
                    double endLong = Double.parseDouble(deviceLong);
                    System.out.println("Pharmacy " + i + ": " + pharmacy_name + "  " + pharmacy_latitude + ", " + pharmacy_longitude);
                    Location.distanceBetween(startLat,startLong,endLat,endLong,distance);
                    pharmacies.add(new Pharmacy(pharmacy_name,distance[0]));
                    System.out.println("Distance: " + distanceToString(distance[0]));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }*/


























        // query.setOnClickListener(new View.OnClickListener() {
         /*  @Override
           public void onClick(View v) {
               //create the instance of database access class and open database connection
               DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
               databaseAccess.open();

               //getting string value from editext
               String s=Specialite.getText().toString();
               String nom = databaseAccess.getInfo(s);

               //setting text to result field
               result.setText(nom);
               databaseAccess.close();

           }*/


   /* @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem= menu.findItem(R.id.search);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("search here");
        SearchView.OnQueryTextListener queryTextListener= new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Specialite=findViewById(R.id.Specialite);
        query=findViewById(R.id.query);
        result=findViewById(R.id.result);

        //setting onclicklistener to query button

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create the instance of database access class and open database connection
                DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                //getting string value from editext
                String s=Specialite.getText().toString();
                String Horaire = databaseAccess.getInfo(s);

                //setting text to result field
                result.setText(Horaire);
                databaseAccess.close();

            }
        });
    }*/
