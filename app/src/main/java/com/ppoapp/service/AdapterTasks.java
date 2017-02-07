package com.ppoapp.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppoapp.R;
import com.ppoapp.entity.Content;
import com.squareup.picasso.Picasso;

import java.util.List;
public class AdapterTasks extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Content> contents;

    public AdapterTasks(Context ctx, List<Content> contents) {
        this.ctx = ctx;
        this.contents = contents;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = lInflater.inflate(R.layout.task, parent, false);
        }
        Content content = contents.get(position);
        ObjectMapper mapper = new ObjectMapper();
      /*  ImageJson imageJson = null;
        try {
            imageJson = mapper.readValue(content.getImages(), ImageJson.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ((TextView)view.findViewById(R.id.textTitle)).setText(content.getTitle());
        ((TextView)view.findViewById(R.id.textIntro)).setText(content.getImages());
        Picasso.with(ctx).load(content.getImages()).into((ImageView) view.findViewById(R.id.imageView2));

        return view;
    }
}
