package com.aryupay.helpingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.modal.blogdetails.Image;
import com.bumptech.glide.Glide;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class FlipperAdapter extends BaseAdapter {
    private Context mCtx;
    private ArrayList<Image> heros;


    //private Button textView_Button;
    public FlipperAdapter(Context mCtx, ArrayList<Image> heros) {
        this.mCtx = mCtx;
        this.heros = heros;
    }

    @Override
    public int getCount() {
        return heros.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Image hero = heros.get(position);
        Fresco.initialize(mCtx);
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.flipper_items, null);

      /*  textView = (TextView) view.findViewById(R.id.textView);

        textView_SubTitle = (TextView) view.findViewById(R.id.textView_SubTitle);
        textView_Desc = (TextView) view.findViewById(R.id.textView_Desc);
        textView_Button = view.findViewById(R.id.textView_Button);
*/
        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.image);
       /* textView.setText(hero.getSlideTitle());

        textView_SubTitle.setText(hero.getSlideSubTitle());
        textView_Desc.setText(hero.getSlideDescription());
        textView_Button.setText(hero.getButtonText());*/
//        textView_Button.setBackgroundColor(Color.parseColor("#EC166B"));
//        textView_Button.setTextColor(Color.parseColor("#FFFFFF"));


        Uri uri = Uri.parse(BuildConstants.Main_Image + hero.getPath().replace("public", "storage"));
        imageView.setImageURI(uri);

        return view;
    }


}
