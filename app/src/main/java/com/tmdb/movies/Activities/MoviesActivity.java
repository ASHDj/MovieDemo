package com.tmdb.movies.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tmdb.movies.Adpaters.MoviesAdapter;
import com.tmdb.movies.R;
import com.tmdb.movies.Utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tmdb.movies.Utils.Common.ChkNull;

public class MoviesActivity extends Activity {
 Typeface tf,tf_bold,tf_awesome;
 SharedPreferences pref;
 TextView txt_action_back,txt_action_title,txt_action_info;
 ListView list_movies;
 TextView txt_info;
 ArrayList<String> movies = new ArrayList<String>();

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_movies);
  tf=Typeface.createFromAsset(getAssets(),"OpenSans-Regular.ttf");
  tf_bold= Typeface.createFromAsset(getAssets(),"OpenSans-Bold.ttf");
  tf_awesome = Typeface.createFromAsset(getAssets(),"fontawesome.ttf");
  setActionBar("Upcoming Movies");
  list_movies= (ListView) findViewById(R.id.list_movies);
  txt_info = (TextView) findViewById(R.id.txt_info);
  txt_info.setTypeface(tf);
  txt_info.setText(Html.fromHtml("Developed By:<br/><big>"+getResources().getString(R.string.info_name)+"</big>"));
  txt_info.setVisibility(View.GONE);
  list_movies.setVisibility(View.VISIBLE);
  list_movies.setAdapter(new MoviesAdapter(MoviesActivity.this,movies));
  list_movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
   @Override
   public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
    //Call Movie Details Activity here
    Intent intent = new Intent(MoviesActivity.this,MovieDetailsActivity.class);
    intent.putExtra("id",list_movies.getAdapter().getItem(pos).toString().split(";;")[0]);
    startActivity(intent);
  }});
  callWebService(Common.movies_url,"Movies");
 }

 public void setActionBar(String title){
  View action = getLayoutInflater().inflate(R.layout.view_cust_actionbar,null);
  txt_action_back = (TextView) action.findViewById(R.id.txt_action_back);
  txt_action_title = (TextView) action.findViewById(R.id.txt_action_title);
  txt_action_info = (TextView) action.findViewById(R.id.txt_action_info);
  txt_action_title.setTypeface(tf_bold);
  txt_action_back.setTypeface(tf_awesome);
  txt_action_info.setTypeface(tf_awesome);
  txt_action_back.setText(R.string.icon_info);
  txt_action_info.setText(R.string.icon_info);
  txt_action_title.setText(title);
  txt_action_info.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
    txt_action_title.setText("Information");
    txt_action_info.setVisibility(View.INVISIBLE);
    txt_info.setVisibility(View.VISIBLE);
    list_movies.setVisibility(View.GONE);
  }});
  getActionBar().show();
  getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
  getActionBar().setDisplayShowTitleEnabled(false);
  getActionBar().setDisplayShowHomeEnabled(false);
  getActionBar().setDisplayHomeAsUpEnabled(false);
  getActionBar().setHomeButtonEnabled(false);
  getActionBar().setDisplayShowCustomEnabled(true);
  getActionBar().setCustomView(action,new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
 }

 @Override
 protected void onResume() {
  super.onResume();
  //if(list_movies!=null && movies!=null && movies.size()==0)
   //callWebService(Common.movies_url,"Movies");
 }

 @Override
 public void onBackPressed() {
  if(txt_action_title.getText().toString().equalsIgnoreCase("Information") && txt_info.getVisibility()==View.VISIBLE){
   txt_action_title.setText("Upcoming Movies");
   txt_action_info.setVisibility(View.VISIBLE);
   txt_info.setVisibility(View.GONE);
   list_movies.setVisibility(View.VISIBLE);
  }
  else
   super.onBackPressed();
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
     MoviesActivity.this.onSuccess(response,tag);
    }

    @Override //onFailure
    public void onFailure(int statusCode, Throwable error, String content) {
     // Hide Progress Dialog
     if(prgDialog != null && prgDialog.isShowing())
      prgDialog.dismiss();

     // When Http response code is '404'
     if (statusCode == 404)
      Toast.makeText(MoviesActivity.this, "Requested resource not found", Toast.LENGTH_LONG).show();
      // When Http response code is '500'
     else if (statusCode == 500)
      Toast.makeText(MoviesActivity.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();
      // When Http response code other than 404, 500
     else
      Toast.makeText(MoviesActivity.this,"Unknown error!! null returned", Toast.LENGTH_LONG).show();
    }
   });

   Handler handler = new Handler();
   handler.postDelayed(new Runnable() {
    @Override
    public void run() {
     // Hide Progress Dialog
     if(prgDialog != null && prgDialog.isShowing()) {
      Toast.makeText(MoviesActivity.this,"Connection Terminated, data may not have updated",Toast.LENGTH_LONG).show();
      prgDialog.dismiss();
     }
    }
   },30000);
  }
  else
   Toast.makeText(MoviesActivity.this,"Please check your Network connection..!!!", Toast.LENGTH_LONG).show();
 }

 public void onSuccess(String response,String tag){
  //Log.i("response",response);
  if(tag.equalsIgnoreCase("Movies") && response!=null && response.length()>0){
   try {
    JSONObject jobj = new JSONObject(response);
    if(jobj.getInt("page")==1){
     JSONArray jarray = new JSONArray(jobj.getString("results"));
     movies.clear();
     for(int i=0;i<jarray.length();i++){
      movies.add(ChkNull(jarray.getJSONObject(i),"id","null")+";;"+
      ChkNull(jarray.getJSONObject(i),"title","null")+";;"+
      ChkNull(jarray.getJSONObject(i),"poster_path","null")+";;"+
      ChkNull(jarray.getJSONObject(i),"release_date","null")+";;"+
      ChkNull(jarray.getJSONObject(i),"adult","null"));
     }
     list_movies.setAdapter(new MoviesAdapter(MoviesActivity.this,movies));
    }
   }catch (JSONException e){e.printStackTrace();}
  }
 }
}
