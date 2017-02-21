package com.ppoapp.service;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppoapp.R;
import com.ppoapp.entity.Content;
import com.ppoapp.entity.ImageJson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import static com.ppoapp.constant.Constans.PPOSITE;

public class AdapterContent extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Content> contents;

    public AdapterContent(Context ctx, List<Content> contents) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = lInflater.inflate(R.layout.item_new, parent, false);
        }
        Content content = contents.get(position);
        ObjectMapper mapper = new ObjectMapper();
        ImageJson imageJson = null;
        try {
            imageJson = mapper.readValue(content.getImages(), ImageJson.class);
        ((TextView)view.findViewById(R.id.textOfTitle)).setText(content.getTitle());
        ((TextView)view.findViewById(R.id.textOfIntro)).setText(Html.fromHtml(content.getIntrotext()));
        Picasso.with(ctx)
                .load(PPOSITE + imageJson.getImage_intro())
                .placeholder(R.drawable.logo_ppo)
                .into((ImageView) view.findViewById(R.id.imageOfNews));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return view;
    }

}
