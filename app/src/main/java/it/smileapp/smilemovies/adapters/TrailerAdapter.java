package it.smileapp.smilemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.utilities.MoviesDB;

/**
 * Created by carde on 08/02/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private JSONArray mTrailers;
    private Context mContext;
    private TrailerClickListener mClickListener;

    public interface TrailerClickListener {
        void onTrailerClick(int position);
    }

    public TrailerAdapter(Context context, JSONArray trailers, TrailerClickListener clickListener) {
        mTrailers = trailers;
        mContext = context;
        mClickListener = clickListener;
    }

    public void reloadContent(JSONArray trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.build(position);
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.length();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private ImageView mThumbnail;
        private TextView mVideoTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            mThumbnail = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            mVideoTitle = (TextView) itemView.findViewById(R.id.trailer_film_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onTrailerClick(getAdapterPosition());
                }
            });
        }

        public void build(int position) {

            try {

                JSONObject trailer = mTrailers.getJSONObject(position);
                String videoTitle = trailer.getString("name");

                String trailerYTKey = trailer.getString("source");
                Picasso.with(mContext).load(MoviesDB.getTrailerThumbnailFromYT(trailerYTKey)).into(mThumbnail);

                mVideoTitle.setText(videoTitle);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
