package ms.apps.task.movies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import ms.apps.task.movies.Adapters.RecyclerViewAdapter;
import ms.apps.task.movies.Database.DataBaseHandler;
import ms.apps.task.movies.Models.Movie;
import ms.apps.task.movies.R;

public class MovieListActivity extends AppCompatActivity {

    private FloatingActionButton addFab;
    private ProgressDialog pDialog;

    // URL to get the movies JSON
    private final static String url = "https://api.androidhive.info/json/movies.json";

    public final static String EXTRA_MOVIE_DETAILS = "extra_movie_details";

    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ArrayList<Movie> moviesList;
    private RecyclerView moviesRecycleView;
    private FrameLayout recyclerViewMovieListLayout;

    private DataBaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        moviesList = new ArrayList<>();
        addFab = findViewById(R.id.add_movie_fab);
        moviesRecycleView = findViewById(R.id.movies_rv);
        recyclerViewMovieListLayout = findViewById(R.id.movies_list_fl);
        dbHandler = new DataBaseHandler(this);
        getMovieListFromDB();

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

        mAdapter.setOnClickMovieListListener(new RecyclerViewAdapter.MoviesListListener() {
            @Override
            public void OnMovieClicked(int position, Movie model) {
                Intent intent = new Intent(MovieListActivity.this,MovieDetailsActivity.class);
                intent.putExtra(EXTRA_MOVIE_DETAILS, model);
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
                    Toast.makeText(MovieListActivity.this, "Failed Loading Data! Please try again", Toast.LENGTH_SHORT).show();
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

    public boolean containsName(final ArrayList<Movie> list, final String name){
        if (list.stream().filter(new Predicate<Movie>() {
            @Override
            public boolean test(Movie o) {
                return o.getTitle().equals(name);
            }
        }).findFirst().isPresent())
            return true;
        else return false;
    }

    // getting the new movie by scanning json QRCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result != null){
            if(result.getContents() != null){
                try{
                    //create new Movie object and get all the data from it
                    Movie newMovie = new Movie();
                    JSONObject obj = new JSONObject(result.getContents());

                    newMovie.setTitle(obj.getString("title"));
                    newMovie.setReleaseYear(obj.getString("releaseYear"));
                    newMovie.setRating(obj.getString("rating"));
                    newMovie.setImage_url(obj.getString("image"));

                    //get the genres array
                    ArrayList<String> genresData = new ArrayList<>();
                    JSONArray genreArray = obj.getJSONArray("genre");
                    for(int i=0 ; i < genreArray.length(); i++) {
                        genresData.add(genreArray.get(i).toString());
                    }
                    newMovie.setGenre(genresData.toString());

                    Log.e("newMovie",newMovie.getTitle()+ " " + newMovie.getReleaseYear()
                            + " " + newMovie.getRating()
                            + " " + newMovie.getGenre()
                            + " " + newMovie.getImage_url());

                    if(containsName(moviesList,newMovie.getTitle())){ // check if we have this movie in array list
                        Log.e("isContains","Contains");
                        // show snack bar
                        Snackbar.make(recyclerViewMovieListLayout,
                                "Current movie already exist in the Database",
                                Snackbar.LENGTH_LONG).show();

                    }else{ //do not contains in database - add the movie to list
                        Log.e("isContains","notContains");
                        // add to the list and update the recycler view
                        DataBaseHandler db = new DataBaseHandler(this);
                        db.addMovie(newMovie);
                        getMovieListFromDB();
                        Toast.makeText(this, "New Movie as been added", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
