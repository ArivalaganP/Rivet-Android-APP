package com.rivet.app.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.util.Log;

import com.rivet.app.BaseActivity;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;

public class EmailSender {
	
	private Context context;

	public EmailSender(Context context) {
		this.context = context ;
	}

	public void sendEmailMessage() {
		
		((BaseActivity)context).postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityShareOnEmail);
		
		String storyTitle = (ContentManager.getContentManager(context).getPlaylistManager().getCurrentStory().getTitle());
		String storyURl = "http://www.rivetnewsradio.com/share/"+(ContentManager.getContentManager(context).getPlaylistManager().getCurrentStory().getTrackID());
		String subject = "Listen to " + '"' + storyTitle + '"' ;
		String prebody = RConstants.CHECK_OUT_RIVET ;
		
		String totalBody = prebody + "\n" + "<a href=\"" + storyURl + "\">" + storyTitle+ "</a>" + "<br/><br/>"  ;  
		String  imageBody = "\n" + "<a href=\"" + "http://tinyurl.com/rivet-android" + '"' + ">" +
							"<img style=\"border:0;\" src="+ '"' +"http://www.rivetnewsradio.com/images/badge-small-google.png" + '"' +" alt=\"Rivet Radio\" width=\"160\" height=\"70\"></a>" ;
		
		totalBody = totalBody.concat(imageBody);
		Intent mailIntent = new Intent();
		mailIntent.setAction(Intent.ACTION_SEND);
		mailIntent.setType("message/rfc822");
		mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(totalBody));  
		  
		/*String  urlOfImageToDownload = "http://www.rivetnewsradio.com/images/badge-small-google.png";
		  if (urlOfImageToDownload != null && Environment.MEDIA_MOUNTED.equals(
			        Environment.getExternalStorageState())) {
			    // Download the icon...
			    URL iconUrl = null;
				try {
					iconUrl = new URL(urlOfImageToDownload);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    HttpURLConnection connection = null;
				try {
					connection = (HttpURLConnection) iconUrl.openConnection();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			    connection.setDoInput(true);
			    try {
					connection.connect();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    InputStream input = null;
				try {
					input = connection.getInputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    Bitmap immutableBpm = BitmapFactory.decodeStream(input);
			 
			    // Save the downloaded icon to the pictures folder on the SD Card
			    File directory = Environment.getExternalStoragePublicDirectory(
			        Environment.DIRECTORY_PICTURES);
			    directory.mkdirs(); // Make sure the Pictures directory exists.
			    File destinationFile = new File(directory, "play");
			    FileOutputStream out = null;
				try {
					out = new FileOutputStream(destinationFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    immutableBpm.compress(Bitmap.CompressFormat.PNG, 90, out);
			    try {
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Uri mediaStoreImageUri = Uri.fromFile(destinationFile);     
			 
			    // Add the attachment to the intent
			    mailIntent.putExtra(Intent.EXTRA_STREAM, mediaStoreImageUri);
			}            */           
		  
		  context.startActivity(Intent.createChooser(mailIntent, subject));
		  
		  

		}
	
}
