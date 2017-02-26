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
import it.smileapp.smilemovies.utilities.MoviesDBRequests;

/**
 * Created by carde on 08/02/17.
 */

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private JSONArray mActors;
    private Context mContext;

    public ActorAdapter(Context context, JSONArray actors) {
        mActors = actors;
        mContext = context;
    }

    /**
     * Reloads the content of the Adapter
     * @param actors - The new JSONArray containing the new actors
     */
    public void reloadContent(JSONArray actors){
        mActors = actors;
        notifyDataSetChanged();
    }

    @Override
    public ActorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_actor, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActorViewHolder holder, int position) {
        holder.build(position);
    }

    @Override
    public int getItemCount() {
        return mActors == null ? 0 : mActors.length();
    }

    public class ActorViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePicture;
        private TextView mActorName;
        private TextView mActorRole;

        public ActorViewHolder(View itemView) {
            super(itemView);

            mProfilePicture = (ImageView) itemView.findViewById(R.id.trailer_actor_picture);
            mActorName = (TextView) itemView.findViewById(R.id.trailer_actor_name);
            mActorRole = (TextView) itemView.findViewById(R.id.trailer_actor_role);
        }

        public void build(int position) {
            try {

                JSONObject actor = mActors.getJSONObject(position);
                String actorName = actor.getString("name");
                String actorRole = actor.getString("character");

                String profilePath = actor.getString("profile_path");
                if (profilePath != "null") {
                    Picasso.with(mContext).load(MoviesDBRequests.getPhotoUrl(profilePath)).into(mProfilePicture);
                }

                mActorName.setText(actorName);
                mActorRole.setText(actorRole);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
