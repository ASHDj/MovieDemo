package com.tmdb.movies.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tmdb.movies.R;
import com.tmdb.movies.Utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tmdb.movies.Utils.Common.ChkNull;

public class MovieDetailsActivity extends Activity {
 Typeface tf,tf_bold,tf_awesome;
 SharedPreferences pref;
 FrameLayout fl_posters;
 ImageView img_poster;
 TextView txt_title,txt_overview,txt_adult;
 RatingBar ratings;
 ArrayList<String> posters = new ArrayList<String>();
 int poster_pos=0;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_movie_details);
  tf=Typeface.createFromAsset(getAssets(),"OpenSans-Regular.ttf");
  tf_bold= Typeface.createFromAsset(getAssets(),"OpenSans-Bold.ttf");
  tf_awesome = Typeface.createFromAsset(getAssets(),"fontawesome.ttf");
  setActionBar("Movie Details");
  fl_posters = (FrameLayout) findViewById(R.id.fl_movie_details_posters);
  img_poster = (ImageView) findViewById(R.id.img_movie_details_poster);
  txt_title= (TextView) findViewById(R.id.txt_movie_details_title);
  txt_overview= (TextView) findViewById(R.id.txt_movie_details_overview);
  txt_adult= (TextView) findViewById(R.id.txt_movie_details_adult);
  ratings = (RatingBar) findViewById(R.id.rating_movie_details);
  txt_title.setTypeface(tf_bold);
  txt_overview.setTypeface(tf);
  txt_adult.setTypeface(tf);

  final GestureDetector detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
   @Override
   public boolean onDown(MotionEvent e) {
    return true;
   }

   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    boolean result = false;
    try {
     float diffY = e2.getY() - e1.getY();
     float diffX = e2.getX() - e1.getX();
     if(Math.abs(diffX) > Math.abs(diffY)) {
      if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
       if (diffX > 0) {
        if(poster_pos>0){
         poster_pos--;
         Log.i("poster_pos",""+poster_pos);
         Glide.with(MovieDetailsActivity.this).load(Common.base_images_url+posters.get(poster_pos).toString()).asBitmap().placeholder(R.drawable.loader).into(img_poster);
        }
       }
       else {
        if(poster_pos<posters.size()-1){
         poster_pos++;
         Log.i("poster_pos",""+poster_pos);
         Glide.with(MovieDetailsActivity.this).load(Common.base_images_url+posters.get(poster_pos).toString()).asBitmap().placeholder(R.drawable.loader).into(img_poster);
        }
       }
       result = true;
      }
     }
    }
    catch (Exception exception) {exception.printStackTrace();}
    return result;
   }
  });

  fl_posters.setOnTouchListener(new View.OnTouchListener() {
   @Override
   public boolean onTouch(View view, MotionEvent motionEvent) {
    return detector.onTouchEvent(motionEvent);
  }});

  callWebService(Common.movie_details_url.replace("<movie_id>",getIntent().getExtras().getString("id","")),"MovieDetails");
  callWebService(Common.movie_details_images_url.replace("<movie_id>",getIntent().getExtras().getString("id","")),"MovieImages");
 }

 public void setActionBar(String title){
  View action = getLayoutInflater().inflate(R.layout.view_cust_actionbar,null);
  action.findViewById(R.id.txt_action_back).setVisibility(View.INVISIBLE);
  action.findViewById(R.id.txt_action_info).setVisibility(View.INVISIBLE);
  TextView txt_action_title = (TextView) action.findViewById(R.id.txt_action_title);
  txt_action_title.setTypeface(tf_bold);
  txt_action_title.setText(title);
  getActionBar().show();
  getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
  getActionBar().setDisplayShowTitleEnabled(false);
  getActionBar().setDisplayShowHomeEnabled(false);
  getActionBar().setDisplayHomeAsUpEnabled(false);
  getActionBar().setHomeButtonEnabled(false);
  getActionBar().setDisplayShowCustomEnabled(true);
  getActionBar().setCustomView(action,new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
 }

 //function to Call Web Service
 public void callWebService(final String url,final String tag) {
  ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
  if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
   //Show progressDialog During Processing
   final ProgressDialog prgDialog = new ProgressDialog(this);
   if(prgDialog!=null){
    prgDialog.setMessage("Please Wait..");
    //prgDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    //prgDialog.setIndeterminateDrawable(myActivity.getResources().getDrawable(R.drawable.loader));
    prgDialog.setIndeterminate(true);
    prgDialog.setCancelable(false);
    prgDialog.show();
   }
   Log.i("url", url);
   AsyncHttpClient newclient = new AsyncHttpClient();
   newclient.get(url,new AsyncHttpResponseHandler() {
    @Override  //onSucess
    public void onSuccess(String response) {
     // Hide Progress Dialog
     if(prgDialog != null && prgDialog.isShowing())
      prgDialog.dismiss();
     //Call onSuccess for Work
     MovieDetailsActivity.this.onSuccess(response,tag);
    }

    @Override //onFailure
    public void onFailure(int statusCode, Throwable error, String content) {
     // Hide Progress Dialog
     if(prgDialog != null && prgDialog.isShowing())
      prgDialog.dismiss();

     // When Http response code is '404'
     if (statusCode == 404)
      Toast.makeText(MovieDetailsActivity.this, "Requested resource not found", Toast.LENGTH_LONG).show();
      // When Http response code is '500'
     else if (statusCode == 500)
      Toast.makeText(MovieDetailsActivity.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
      // When Http response code other than 404, 500
     else
      Toast.makeText(MovieDetailsActivity.this,"Unknown error!! null returned", Toast.LENGTH_LONG).show();
    }
   });

   Handler handler = new Handler();
   handler.postDelayed(new Runnable() {
    @Override
    public void run() {
     // Hide Progress Dialog
     if(prgDialog != null && prgDialog.isShowing()) {
      Toast.makeText(MovieDetailsActivity.this,"Connection Terminated, data may not have updated",Toast.LENGTH_LONG).show();
      prgDialog.dismiss();
     }
    }
   },30000);
  }
  else
   Toast.makeText(MovieDetailsActivity.this,"Please check your Network connection..!!!", Toast.LENGTH_LONG).show();
 }

 public void onSuccess(String response,String tag){
  //Log.i("response",response);
  if(tag.equalsIgnoreCase("MovieDetails") && response!=null && response.length()>0){
   try {
    JSONObject jobj = new JSONObject(response);
    txt_title.setText(ChkNull(jobj,"title",""));
    txt_overview.setText(Html.fromHtml("<b>Overview:</b><br/>"+ChkNull(jobj,"overview","")));
    //Log.i("ratings",""+(int)Double.parseDouble(ChkNull(jobj,"popularity","0.0")));
    ratings.setRating((int)Double.parseDouble(ChkNull(jobj,"popularity","0.0")));
    txt_adult.setVisibility(ChkNull(jobj,"adult","false").equalsIgnoreCase("true")?View.VISIBLE:View.GONE);
   }
   catch (JSONException e){e.printStackTrace();}
  }
  else if(tag.equalsIgnoreCase("MovieImages") && response!=null && response.length()>0){
   try {
    JSONObject jobj = new JSONObject(response);
    JSONArray jsonArray = new JSONArray(jobj.getString("posters"));
    //Log.i("length",""+jsonArray.length());
    int max=jsonArray.length()<=5?jsonArray.length():5;
    //Log.i("max",""+max);
    posters.clear();
    for(int i=0;i<max;i++)
    if(!ChkNull(jsonArray.getJSONObject(i),"file_path","").equals(""))
    posters.add(ChkNull(jsonArray.getJSONObject(i),"file_path",""));
    //Log.i("size",""+posters.size());
    if(posters.size()>0) {
     //Log.i("img_url",Common.base_images_url+posters.get(0).toString());
     Glide.with(MovieDetailsActivity.this).load(Common.base_images_url+posters.get(poster_pos).toString()).asBitmap().placeholder(R.drawable.loader).into(img_poster);
    }
   }
   catch (JSONException e){e.printStackTrace();}
  }
 }
}
