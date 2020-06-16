package ms.apps.task.movies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
