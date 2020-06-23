package ms.apps.task.movies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import ms.apps.task.movies.Database.DataBaseHandler;
import ms.apps.task.movies.Models.Movie;
import ms.apps.task.movies.R;

public class MainActivity extends AppCompatActivity {

    /**
     * This application will parse json from url and add all the movies
     * to SQLite Database.
     * All the movies will be shown in a Recycler View list in card views.
     * Add new movies by scanning QR Code and update the database if needed
     *
     * @author  Dima Tepliakov
     * @version 1.0
     * @since   16/06/2020
     */

    private DataBaseHandler db = null;
    private ArrayList<Movie> moviesList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseHandler(this);
        moviesList = db.getAllMovies();

        // check for network connection
        // if there are movies on database but no internet connection show the movieListActivity
        if (isNetworkAvailable(this) || moviesList.size() > 0) {
            Intent intent = new Intent(MainActivity.this, MovieListActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Network network = connectivityManager.getActiveNetwork();
            if(network == null){
                return false; //if empty return false
            }

            NetworkCapabilities activeNetwork = connectivityManager.getNetworkCapabilities(network);
            if(activeNetwork == null){
                return false; //if empty return false
            }

            if(activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true;
            }else {
                return false;
            }

        }else{
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return ((networkInfo != null) && networkInfo.isConnectedOrConnecting());
        }
    }
}
