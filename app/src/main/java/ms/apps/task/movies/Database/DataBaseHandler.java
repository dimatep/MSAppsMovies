package ms.apps.task.movies.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ms.apps.task.movies.Models.Movie;

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MoviesDB";

    public DataBaseHandler(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MOVIES_TABLE = ("CREATE TABLE " + Movie.MovieEntry.TABLE_MOVIES + "("
                + Movie.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Movie.MovieEntry.COLUMN_TITLE + " TEXT,"
                + Movie.MovieEntry.COLUMN_IMAGE + " TEXT,"
                + Movie.MovieEntry.COLUMN_RATING + " TEXT,"
                + Movie.MovieEntry.COLUMN_RELEASE_YEAR + " TEXT,"
                + Movie.MovieEntry.COLUMN_GENRE + " TEXT)");

        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie.MovieEntry.TABLE_MOVIES);
        onCreate(db);
    }

    public long addMovie(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues movieValues = new ContentValues();

        movieValues.put(Movie.MovieEntry.COLUMN_TITLE,movie.getTitle());
        movieValues.put(Movie.MovieEntry.COLUMN_IMAGE,movie.getImage_url());
        movieValues.put(Movie.MovieEntry.COLUMN_RATING,movie.getRating());
        movieValues.put(Movie.MovieEntry.COLUMN_RELEASE_YEAR,movie.getReleaseYear());
        movieValues.put(Movie.MovieEntry.COLUMN_GENRE,movie.getGenre().toString());

        long result = db.insert(Movie.MovieEntry.TABLE_MOVIES,null,movieValues);
        db.close();
        return result;
    }

    public ArrayList<Movie> getAllMovies(){
        ArrayList<Movie> moviesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Movie.MovieEntry.TABLE_MOVIES;
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.moveToFirst()){
                do{
                    Movie movie = new Movie(
                            cursor.getString(cursor.getColumnIndex(Movie.MovieEntry.COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndex(Movie.MovieEntry.COLUMN_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(Movie.MovieEntry.COLUMN_RATING)),
                            cursor.getString(cursor.getColumnIndex(Movie.MovieEntry.COLUMN_RELEASE_YEAR)),
                            cursor.getString(cursor.getColumnIndex(Movie.MovieEntry.COLUMN_GENRE)));

                    moviesList.add(movie);
                }while (cursor.moveToNext());
            }

            cursor.close();

        }catch(SQLException e){
            e.printStackTrace();
            db.execSQL(selectQuery,null);
            return new ArrayList<Movie>(); //return empty arraylist
        }

        return moviesList;

    }

    private void readDataToDB(SQLiteDatabase db) throws IOException, JSONException {
        final String MOVIE_TITLE = "title";
        final String MOVIE_RELEASE_YEAR = "releaseYear";
        final String MOVIE_RATING = "rating";
        final String MOVIE_IMAGE = "image";
        final String MOVIE_GENRE = "genre";

        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray movieItemsJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < movieItemsJsonArray.length(); i++) {
                JSONObject movieItemObject = movieItemsJsonArray.getJSONObject(i);

                String title = movieItemObject.getString(MOVIE_TITLE);
                String release_year = movieItemObject.getString(MOVIE_RELEASE_YEAR);
                String rating = movieItemObject.getString(MOVIE_RATING);
                String image = movieItemObject.getString(MOVIE_IMAGE);
                String genre = movieItemObject.getString(MOVIE_GENRE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(Movie.MovieEntry.COLUMN_TITLE,title);
                movieValues.put(Movie.MovieEntry.COLUMN_IMAGE,image);
                movieValues.put(Movie.MovieEntry.COLUMN_RATING,rating);
                movieValues.put(Movie.MovieEntry.COLUMN_RELEASE_YEAR,release_year);
                movieValues.put(Movie.MovieEntry.COLUMN_GENRE,genre);

                db.insert(Movie.MovieEntry.TABLE_MOVIES, null, movieValues);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String readJsonDataFromFile() throws IOException{
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try{
            String jsonDataString = null;
            //inputStream = mResources.openRawResource();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null){
                builder.append(jsonDataString);
            }
        }finally {
            if(inputStream != null)
                inputStream.close();
        }

        return new String(builder);
    }
}
