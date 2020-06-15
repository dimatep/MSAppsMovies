package ms.apps.task.movies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ms.apps.task.movies.Adapters.RecyclerViewAdapter;
import ms.apps.task.movies.Database.DataBaseHandler;
import ms.apps.task.movies.Models.Movie;
import ms.apps.task.movies.R;

public class MovieListActivity extends AppCompatActivity {

    private String TAG = MovieListActivity.class.getSimpleName();

    //private ListView moviesListView;
    private FloatingActionButton addFab;
    private ProgressDialog pDialog;

    // URL to get the movies JSON
    private final static String url = "https://api.androidhive.info/json/movies.json";

    private final static int ADD_MOVIE_ACTIVITY_CODE = 1;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ArrayList<Movie> moviesList;
    private RecyclerView moviesRecycleView;

    private DataBaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        moviesList = new ArrayList<>();
        addFab = findViewById(R.id.add_movie_fab);
        moviesRecycleView = findViewById(R.id.movies_rv);
        dbHandler = new DataBaseHandler(this);

        getMovieListFromDB();


//        jsonRequest();

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });
    }

    private void getMovieListFromDB(){
        moviesList = dbHandler.getAllMovies();
        if(moviesList.size() > 0){ //there are movies in SQLite database - sort and set them to the adapter
            sortMoviesByReleaseYearASC();
            setupRecyclerView(moviesList);
        } else{ //no movies in database - need to parse the json url
            new GetMovies().execute();
        }
    }

    private void setupRecyclerView(List<Movie> moviesList) {
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(this,moviesList);
        moviesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        moviesRecycleView.setHasFixedSize(true);
        moviesRecycleView.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerViewAdapter.MoviesListener() {
            @Override
            public void OnMovieClicked(int position, View view) {
                Intent intent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie",position);
                startActivity(intent);
            }
        });
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MovieListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            Movie movie = new Movie();
                            movie.setTitle(jsonObject.getString("title"));
                            movie.setRating(jsonObject.getString("rating"));
                            movie.setReleaseYear(jsonObject.getString("releaseYear"));
                            movie.setImage_url(jsonObject.getString("image"));

                            //get genre array
                            ArrayList<String> genresData = new ArrayList<>();
                            JSONArray genreArray = jsonObject.getJSONArray("genre");
                            for(int j=0 ; j < genreArray.length(); j++) {
                                genresData.add(genreArray.get(j).toString());
                            }
                            movie.setGenre(genresData.toString());

                            dbHandler.addMovie(movie);//add movie to db
                            moviesList.add(movie); //add all the movies to array list

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    sortMoviesByReleaseYearASC(); //sort from newest to oldest
                    setupRecyclerView(moviesList); // set the recycler view adapter
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue = Volley.newRequestQueue(MovieListActivity.this);
            requestQueue.add(request);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into RecyclerView
             * */
            setupRecyclerView(moviesList);
        }
    }

    // Sort the movies from newest to oldest
    public void sortMoviesByReleaseYearASC(){
        Collections.sort(moviesList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                if(Integer.parseInt(o1.getReleaseYear()) < Integer.parseInt(o2.getReleaseYear()))
                    return 1;
                return -1;
            }
        });
    }


    /**
     * QRCode Scanner
     */
    private void scanQRCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning QRCode");
        integrator.initiateScan();
    }

    // getting the new movie by scanning json QRCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result != null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanQRCode();
                    }
                });

                builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
