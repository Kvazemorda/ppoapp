package com.ppoapp.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ppoapp.R;
import com.ppoapp.controller.ExpandableTextView;
import com.ppoapp.controller.MyImageLoader;
import com.ppoapp.data.dao.ContentDAO;
import com.ppoapp.entity.Content;
import com.ppoapp.entity.ImageJson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ppoapp.constant.Constans.PPOSITE;

public class AdapterContent extends BaseAdapter {
    Context ctx;
    List<Content> contents;
    LayoutInflater lInflater;


    public AdapterContent(Context ctx, ArrayList<Content> contents) {
        this.ctx = ctx;
        this.contents = contents;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected static class ViewHolder{
        TextView title;
        ExpandableTextView description;
        ImageView imageView;
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
        ViewHolder holder;

        if (convertView == null) {

            convertView = lInflater.inflate(R.layout.item_new, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textOfTitle);
            holder.description = (ExpandableTextView) convertView.findViewById(R.id.textOfIntro);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageOfNews);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Content content = (Content) getItem(position);
        try {
            holder.title.setText(content.getTitle());
            setDescription(holder.description, content);
            MyImageLoader myImageLoader = new MyImageLoader(ctx, holder.imageView);
            myImageLoader.loadImage(content);
            System.out.println(content.getLocalImage());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void setDescription(TextView description, Content content){
        if(content.getIntrotext().length() > content.getFulltext().length()){
            description.setText(Html.fromHtml(content.getIntrotext()));
        }else {
            description.setText(Html.fromHtml(content.getFulltext()));
        }
    }


}
