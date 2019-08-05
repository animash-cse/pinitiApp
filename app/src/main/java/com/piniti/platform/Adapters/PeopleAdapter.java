package com.piniti.platform.Adapters;

/**
 * Created by Animash on 5/22/2019.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.piniti.platform.Models.AddPeople;
import com.piniti.platform.R;

import java.util.List;


public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private Context context;
    private List<AddPeople> items;

    public PeopleAdapter(Context context, List<AddPeople> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.people_view, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeopleViewHolder holder, int position) {
       // final AddPeople item = items.get(position);
        holder.peopleName.setText(items.get(position).getName());
        Glide.with(context).load(items.get(position).getImage()).into(holder.peopleImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder {
        ImageView peopleImage;
        TextView peopleName;
        TextView peopleCategory;

        public PeopleViewHolder(View itemView) {
            super(itemView);
            peopleImage = (ImageView) itemView.findViewById(R.id.imagePeople);
            peopleName = (TextView) itemView.findViewById(R.id.name);
            peopleCategory = (TextView) itemView.findViewById(R.id.category);
        }
    }
}