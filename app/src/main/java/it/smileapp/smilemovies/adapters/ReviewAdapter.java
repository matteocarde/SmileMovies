package it.smileapp.smilemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.smileapp.smilemovies.R;

/**
 * Created by carde on 08/02/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private JSONArray mReviews;
    private Context mContext;

    public ReviewAdapter(Context context, JSONArray reviews) {
        mReviews = reviews;
        mContext = context;
    }

    /**
     * Reloads the content of the Adapter
     *
     * @param reviews - The new JSONArray containing the new reviews to be displayed
     */
    public void reloadContent(JSONArray reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.build(position);
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.length();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewText;
        private TextView mReviewAuthor;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mReviewAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mReviewText = (TextView) itemView.findViewById(R.id.review_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReviewText.getLineCount() == 3) {
                        mReviewText.setMaxLines(100);
                    } else {
                        mReviewText.setMaxLines(3);
                    }
                    ;
                }
            });
        }

        public void build(int position) {

            try {

                JSONObject review = mReviews.getJSONObject(position);
                String author = review.getString("author");
                String text = review.getString("content");

                mReviewAuthor.setText(author);
                mReviewText.setText(text);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
