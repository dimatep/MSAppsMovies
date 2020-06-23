package ms.apps.task.movies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ms.apps.task.movies.Models.Movie;
import ms.apps.task.movies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private Toolbar detailsToolbar;
    private TextView movieRating;
    private TextView movieGenres;
    private ImageView movieImage;
    private TextView movieYear;

    private Movie movie = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieRating = findViewById(R.id.movie_rating_tv_details);
        movieGenres = findViewById(R.id.movie_genres_tv_details);
        movieImage = findViewById(R.id.movie_image_iv_details);
        movieYear = findViewById(R.id.movie_release_year_tv_details);
        detailsToolbar = findViewById(R.id.movie_detail_toolbar);

        // get movie details from list activity
        if(getIntent().hasExtra(MovieListActivity.EXTRA_MOVIE_DETAILS)){
            movie =getIntent().getParcelableExtra(MovieListActivity.EXTRA_MOVIE_DETAILS);
        }

        if(movie != null) {
            setSupportActionBar(detailsToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(movie.getTitle());

            detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            assignAllMovieInfo();
        }
    }

    public void assignAllMovieInfo(){
        movieYear.setText(movie.getReleaseYear());
        movieGenres.setText(movie.getGenre().toString());
        movieRating.setText(movie.getRating());
        Glide.with(this).load(movie.getImage_url()).into(movieImage);
    }


    @Override
    public void onBackPressed() {
        //go back to movies list
        super.onBackPressed();
    }
}
