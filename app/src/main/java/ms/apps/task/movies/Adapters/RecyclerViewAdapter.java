package ms.apps.task.movies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ms.apps.task.movies.Models.Movie;
import ms.apps.task.movies.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private MoviesListListener listener = null; //click listener
    private Context mContext;
    private List<Movie> movies;
    private RequestOptions option;

    public RecyclerViewAdapter(Context mContext, List<Movie> movies) {
        this.mContext = mContext;
        this.movies = movies;

        //request option for glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    public interface MoviesListListener{
        void OnMovieClicked(int position, Movie model);
    }



    public void setOnClickMovieListListener(MoviesListListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Movie model = movies.get(position);

        holder.movie_title.setText(model.getTitle());
        holder.movie_rating.setText(model.getRating());
        holder.movie_release_year.setText(model.getReleaseYear());
        holder.movie_genres.setText(model.getGenre().toString());
        // parse image url and set it to image view
        Glide.with(mContext).load(model.getImage_url()).apply(option).into(holder.movie_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnMovieClicked(position,model);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView movie_title;
        TextView movie_rating;
        TextView movie_release_year;
        TextView movie_genres;
        ImageView movie_image;


        public MyViewHolder(View itemView){
            super(itemView);

            movie_title = itemView.findViewById(R.id.movie_title_tv);
            movie_rating = itemView.findViewById(R.id.movie_rating_tv);
            movie_release_year = itemView.findViewById(R.id.movie_release_year_tv);
            movie_genres = itemView.findViewById(R.id.movie_genre_tv);
            movie_image = itemView.findViewById(R.id.movie_image_iv);

//            // set click listener to every movie on the list to get to details activity
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(listener!=null){
//                        listener.OnMovieClicked(getAdapterPosition(), v);
//                    }
//                }
//            });
        }
    }
}
