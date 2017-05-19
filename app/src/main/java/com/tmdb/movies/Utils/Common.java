package com.tmdb.movies.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/5/17.
 */

public class Common {
 //Url to Get Upcoming Movies
 public static String movies_url="https://api.themoviedb.org/3/movie/upcoming?api_key=b7cd3340a794e5a2f35e3abb820b497f";

 //Url to Get Movie Details
 public static String movie_details_url="https://api.themoviedb.org/3/movie/<movie_id>?api_key=b7cd3340a794e5a2f35e3abb820b497f"; //Replace <movie_id>

 //Url to Get Upcoming Movies
 public static String movie_details_images_url="https://api.themoviedb.org/3/movie/<movie_id>/images?api_key=b7cd3340a794e5a2f35e3abb820b497f"; //Replace <movie_id>

 //Base Url for Images
 public static String base_images_url="http://image.tmdb.org/t/p/w500";

 ///Function to Check Null Value
 public static String ChkNull(String strToChk,String defaultStr) {
  if(strToChk!=null && strToChk.length()>0 && !strToChk.toLowerCase().equals("null"))// && !strToChk.toLowerCase().equals("none"))
   return strToChk;
  else
   return defaultStr;
 }

 //Function to Check Null Value
 public static String ChkNull(JSONObject jobj, String jid, String defaultStr){
  try {
   if(jobj.has(jid) && jobj.getString(jid) != null && jobj.getString(jid).length() > 0 && !jobj.getString(jid).toLowerCase().equals("null"))// && !strToChk.toLowerCase().equals("none"))
    return jobj.getString(jid);
   else
    return defaultStr;
  }catch (JSONException e){e.printStackTrace();return defaultStr;}
 }

 /*//Seprator used for toString & toList
 public static final String seprator ="%@#";

 //Function Converts ArrayList<String> to String
 public static String toString(ArrayList<String> alist){
  String str="";
  if(alist.size()>0)
  for(int i=0;i<alist.size();i++)
   str+=seprator+alist.get(i);
  return (str.length()>seprator.length())?str.substring(seprator.length()):str;
 }

 //Function Converts String back to ArrayList<String>
 public static ArrayList<String> toList(String alistStr){
  ArrayList<String> alist = new ArrayList<String>();
  if(alistStr!=null && !alistStr.equals(""))
  for(int i=0;i<alistStr.split(seprator).length;i++)
   alist.add(alistStr.split(seprator)[i]);
  return alist;
 }*/
}
