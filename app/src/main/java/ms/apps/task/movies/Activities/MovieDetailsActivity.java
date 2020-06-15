package ms.apps.task.movies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ms.apps.task.movies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView movieTitle;
    private TextView movieRating;
    private TextView movieGenres;
    private ImageView movieImage;
    private TextView movieYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        int moviePosition = getIntent().getIntExtra("movie",0);

        movieTitle = findViewById(R.id.movie_title_tv_details);
        movieRating = findViewById(R.id.movie_rating_tv_details);
        movieGenres = findViewById(R.id.movie_genres_tv_details);
        movieImage = findViewById(R.id.movie_image_iv_details);
        movieYear = findViewById(R.id.movie_release_year_tv_details);

//        movieTitle.setText();
////        movieYear.setText();
////        movieGenres.setText();
////        movieRating.setText();
////        movieImage.
    }

    @Override
    public void onBackPressed() {
        //go back to movies list
        super.onBackPressed();
    }
}
