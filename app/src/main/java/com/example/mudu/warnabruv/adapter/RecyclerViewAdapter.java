package com.example.mudu.warnabruv.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mudu.warnabruv.R;
import com.example.mudu.warnabruv.UserProfile;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<UserProfile> user;

    protected Context context;

    public RecyclerViewAdapter(Context context, List<UserProfile> user) {
        this.user = user;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_data_list, viewGroup, false);
        viewHolder = new RecyclerViewHolders(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders recyclerViewHolders, int i) {
        recyclerViewHolders.profileHeader.setText(user.get(i).getHeader());
        recyclerViewHolders.profileContent.setText(user.get(i).getProfileContent());
    }

    @Override
    public int getItemCount() {
        return this.user.size();
    }
}
