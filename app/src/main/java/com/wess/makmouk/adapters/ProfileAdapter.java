package com.wess.makmouk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;
import com.wess.makmouk.databases.Profile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profiles;
    private OnProfileClickListener listener;

    public ProfileAdapter(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void updateProfiles(List<Profile> newProfiles) {
        this.profiles = newProfiles;
        notifyDataSetChanged();
    }

    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profiles.get(position);
        holder.profileName.setText(profile.getName());
        holder.profileImage.setImageResource(R.drawable.profile_ic);

        holder.selectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public interface OnProfileClickListener {
        void onProfileClick(Profile profile);
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        Button selectButton;

        ProfileViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            selectButton = itemView.findViewById(R.id.select_button);
        }
    }
}

