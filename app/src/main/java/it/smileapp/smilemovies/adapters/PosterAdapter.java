package it.smileapp.smilemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.utilities.MoviesDB;

/**
 * Created by carde on 06/02/17.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private final Context mContext;
    private final PosterClickListener mClickListener;
    private JSONArray mMoviesList;

    public interface PosterClickListener{
        void onPosterClick(int position);
    }

    public PosterAdapter(Context context, JSONArray movies, PosterClickListener clickListener) {
        mContext = context;
        mMoviesList = movies;
        mClickListener = clickListener;
    }

    public void reloadMoviesList(JSONArray moviesList) {
        mMoviesList = moviesList;
        if (mMoviesList != null) {
            notifyDataSetChanged();
        }
    }


    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poster, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        try {
            if (mMoviesList != null) {
                JSONObject filmJsonObject = mMoviesList.getJSONObject(position);

                holder.buildPoster(filmJsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mMoviesList == null ? 0 : mMoviesList.length();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder {

        TextView mMovieTitle;
        ImageView mMoviePoster;
        RatingBar mMovieRating;

        public PosterViewHolder(View itemView) {
            super(itemView);

            mMovieTitle = (TextView) itemView.findViewById(R.id.poster_movie_title);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.poster_movie_image);
            mMovieRating = (RatingBar) itemView.findViewById(R.id.poster_movie_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onPosterClick(getAdapterPosition());
                }
            });

        }

        void buildPoster(JSONObject film) {
            try {

                String movieTitle = film.getString("title");
                String posterPath = film.getString("poster_path");
                float movieRating = (float) (film.getDouble("vote_average") / 2f); //I only display five stars, TMD supports 10 stars.

                mMovieTitle.setText(movieTitle);

                //In this way I load images every time that I scroll right ?? Maybe consider caching.
                Picasso.with(mContext).load(MoviesDB.getPhotoUrl(posterPath)).into(mMoviePoster);
                mMovieRating.setRating(movieRating);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
