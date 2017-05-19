package com.tmdb.movies.Adpaters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tmdb.movies.R;
import com.tmdb.movies.Utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MoviesAdapter extends BaseAdapter {
 private Context context;
 private ArrayList<String> movies=new ArrayList<String>();
 public Typeface tf,tf_bold,tf_awesome;

 public MoviesAdapter(Context context, ArrayList<String> movies){
  this.context = context;
  this.movies = movies;
  //Typeface of Custom Font from Assets Folder
  tf= Typeface.createFromAsset(context.getAssets(),"OpenSans-Regular.ttf");
  tf_bold= Typeface.createFromAsset(context.getAssets(),"OpenSans-Bold.ttf");
  tf_awesome = Typeface.createFromAsset(context.getAssets(), "fontawesome.ttf");

  final Comparator<String> list_comp =new Comparator<String>() {
   @Override
   public int compare(String lhs, String rhs) {
    if(!lhs.split(";;")[3].equalsIgnoreCase("null") && !rhs.split(";;")[3].equalsIgnoreCase("null")){
     try {
      Date d1 = new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(lhs.split(";;")[3]);
      Date d2 = new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(rhs.split(";;")[3]);
      return d2.compareTo(d1);
     }
     catch (ParseException e) {e.printStackTrace();
      return lhs.split(";;")[1].compareTo(lhs.split(";;")[1]);}
    }
    else
     return lhs.split(";;")[1].compareTo(lhs.split(";;")[1]);
  }};
  Collections.sort(movies,list_comp);
 }

 @Override
 public int getCount() {
  return movies.size();
 }

 @Override
 public Object getItem(int position) {
  return movies.get(position);
 }

 @Override
 public long getItemId(int pos) {return pos;}

 @Override
 public View getView(final int pos, View convertView, ViewGroup parent) {
  ViewHolder holder;
  if(convertView == null) {
   holder=new ViewHolder();
   LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
   convertView = mInflater.inflate(R.layout.view_movie, null);
   holder.poster = (ImageView) convertView.findViewById(R.id.img_movie_poster);
   holder.title = (TextView) convertView.findViewById(R.id.txt_movie_title);
   holder.title.setTypeface(tf_bold);
   holder.release_date = (TextView) convertView.findViewById(R.id.txt_movie_release_date);
   holder.release_date.setTypeface(tf);
   holder.adult = (TextView) convertView.findViewById(R.id.txt_movie_adult);
   holder.adult.setTypeface(tf);
   convertView.setTag(holder);
  }
  else
  holder=(ViewHolder)convertView.getTag();
  Glide.with(context).load(Common.base_images_url+getItem(pos).toString().split(";;")[2]).asBitmap().placeholder(R.drawable.loader).into(holder.poster);
  holder.title.setText(Common.ChkNull(getItem(pos).toString().split(";;")[1],""));
  try {
   holder.release_date.setText(Html.fromHtml("<b>Release Date:&nbsp;</b>"+new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(Common.ChkNull(getItem(pos).toString().split(";;")[3],"")))));
  }
  catch(ParseException e){e.printStackTrace();}
  holder.adult.setVisibility((getItem(pos).toString().split(";;")[4].equals("true"))? View.VISIBLE:View.GONE);
  return convertView;
 }

 static class ViewHolder{
  ImageView poster;
  TextView title,adult,release_date;
 }
}