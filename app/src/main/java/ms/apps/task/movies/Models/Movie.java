package ms.apps.task.movies.Models;

import android.provider.BaseColumns;

import java.util.ArrayList;

public class Movie{

    private String title;
    private String image_url;
    private String rating;
    private String releaseYear;
    private String genre;

    public class MovieEntry implements BaseColumns {
        public static final String TABLE_MOVIES = "MoviesTable";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_YEAR = "releaseYear";
        public static final String COLUMN_GENRE = "genre";
    }

    public Movie(){}

    public Movie(String title, String image_url, String rating, String releaseYear, String genre) {
        this.title = title;
        this.image_url = image_url;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String  releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
