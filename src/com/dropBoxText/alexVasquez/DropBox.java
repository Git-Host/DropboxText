package com.dropBoxText.alexVasquez;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class DropBox {
	
	///////////////////////////////////////////////////////////////
	//		app specific settings							/////
	/////////////////////////////////////////////////////////////
	//private static final String TAG = "dropboxText";
	final static private String APP_KEY = "USE YOUR OWN KEY";
	final static private String APP_SECRET = "";
	//final static private String APP_FOLDER_NAME = "pillarsWriter";
	///////////////////////////////////////////////////////////////
	//		end of app specific settings						/////
	/////////////////////////////////////////////////////////////
	
	// You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    static Context mContext;
    static Boolean mLoggedIn = false;
    
    final static String FILL_MAP_NAME = "name";
    final static String FILL_MAP_PATH = "path";
    final static String FILL_MAP_PARENTREV = "parentRev";
    
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    
    static AndroidAuthSession session;
    static DropboxAPI<AndroidAuthSession> mApi;
    static SimpleAdapter fileNameAdapter = null;
    static ArrayList<HashMap<String, String>> fillMaps;
    
    
    
    DropBox(Context context){
    		mContext = context;
    }
	
	public void signIn(){
		if (getKeys() == null){
			showToast("No keys, going to authorize on web site or app");
			AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
			 session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);

			 // When user wants to link to Dropbox, within an activity:
			 session.startAuthentication(mContext);
		}
		else{
			showToast("yes keys, going to authorize, but no web site or app since keys are stored");
			AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
			session = new AndroidAuthSession(
					appKeyPair, ACCESS_TYPE, new AccessTokenPair(getKeys()[0], getKeys()[1]));
			mApi = new DropboxAPI<AndroidAuthSession>(session);
		}
	}
	
	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
	public static boolean isLinked(){
		return session.isLinked();
	}
	private String[] getKeys() {
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }
	
	public void onResumeAuthentication(){
		
		if (session.authenticationSuccessful()) {
			   try {
			     session.finishAuthentication();
			     
			     mApi = new DropboxAPI<AndroidAuthSession>(session);

			     // Store it locally in our app for later use
			     AccessTokenPair tokens = session.getAccessTokenPair();
	             storeKeys(tokens.key, tokens.secret);
	             showToast("keys are being stored");
	                
			   } catch (IllegalStateException e) {
			     showToast("Error: " + e);
			   }
			 }
	}
	
	public static void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
	
	public static String getTextBody(String path){
		
		ByteArrayOutputStream hello = new ByteArrayOutputStream();
		String pathOutPut = null;
        
        try {
            DropboxFileInfo info = mApi.getFile(path, null, hello, null);
            
            pathOutPut = hello.toString();
            
            Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
            // /path/to/new/file.txt now has stuff in it.
        } catch (DropboxUnlinkedException e){
			DropBox.showToast("DropboxUnlinkedException: " + e);
		} catch (DropboxServerException e){
			DropBox.showToast("DropboxServerException: " + e);
		} catch (DropboxIOException e){
			DropBox.showToast("DropboxIOException: " + e);
		} catch (DropboxException e) {
			DropBox.showToast("DropboxException: " + e);
			e.printStackTrace();
		} finally {
            if (hello != null) {
                try {
                	hello.close();
                } catch (IOException e) {
                	DropBox.showToast("IOException: " + e);
                }
            }
        }
		
		return pathOutPut;
	}
	
	public void logOut() {
        // Remove credentials from the session
		session.unlink();

        // Clear our stored keys
        clearKeys();

        showToast("logged out!");
    }
	
	 /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    static public boolean updateTextDocument(String parentRev,String newTitle, String newBody){
		String fileContents = newBody;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());
		try {
			
		    Entry newEntry = mApi.putFile("/" + newTitle + ".txt", inputStream,
		    		fileContents.length(), parentRev, new ProgressListener(){
					@Override
					public void onProgress(long total, long part) {
						if(total == part){
							showToast("saved!");
							
						}
					}
		    });
		    Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
		    
		    return true;
		    
		} catch (DropboxUnlinkedException e) {
		    // User has unlinked, ask them to link again here.
		    Log.e("DbExampleLog", "User has unlinked.");
		    return false;
		} catch (DropboxException e) {
		    Log.e("DbExampleLog", "Something went wrong while uploading.");
		    return false;
		} finally {
		    if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		        		showToast("Save exception: "+ e);
		        }
		    }
		}
    }
    
    static public boolean moveFile(String fromPath, String toPath){
    		try {
				mApi.move(fromPath, toPath);
				DropBox.showToast("Cool beans! File was moved");
				return true;
			} catch (DropboxUnlinkedException e){
				DropBox.showToast("DropboxUnlinkedException: " + e);
				return false;
			} catch (DropboxServerException e){
				DropBox.showToast("DropboxServerException: " + e);
				return false;
			} catch (DropboxIOException e){
				DropBox.showToast("DropboxIOException: " + e);
				return false;
			} catch (DropboxException e) {
				DropBox.showToast("DropboxException: " + e);
				return false;
			}
    }
    
    static public void deleteDocument(String path){
    		try {
				mApi.delete(path);
				showToast("Deleted: " + path);
				
			} catch (DropboxUnlinkedException e) {
     		    // User has unlinked, ask them to link again here.
     		    Log.e("DbExampleLog", "User has unlinked.");
     		    showToast("DropboxUnlinked: " + e);
     		} catch (DropboxServerException e) {
     		    // User has unlinked, ask them to link again here.
     		    Log.e("DbExampleLog", "User has DropboxServerException.");
     		    showToast("DropboxServerException: " + e);
     		} catch (DropboxIOException e) {
     		    // User has unlinked, ask them to link again here.
     		    Log.e("DbExampleLog", "User has DropboxIOException.");
     		    showToast("DropboxIOException: " + e);
     		} catch (DropboxException e) {
     		    Log.e("DbExampleLog", "Something went wrong while uploading.");
     		   showToast("DropboxException: " + e);
     		}
    }
    
    public void getTextFileNames(){
    
    	fileNameAdapter = null;
    	
    	if (session.isLinked()){
    		showToast("session is linked and fetching file names");
    		Entry entries = new Entry();
    		
    		/*
    		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apple = "" + sharedPrefs.getString("pref_root", "/");
        */
    		
    		try {
    				entries = mApi.metadata("/", 100, null, true, null);
    		} catch (DropboxException e) {
    			Toast.makeText(mContext, "text: " + e, Toast.LENGTH_LONG).show();
    		}
    		List <Entry> contents = entries.contents;
    		
    		int [] to = {R.id.listText};
    		String[] from = {FILL_MAP_NAME,FILL_MAP_PATH,FILL_MAP_PARENTREV};
    		
    		fillMaps = new ArrayList<HashMap<String, String>>();
    		
    		for(int i = 0; i < contents.size(); i++){
    			HashMap<String, String> map = new HashMap<String, String>();
    			String filename = contents.get(i).fileName();
    			
    			if (contents.get(i).mimeType.equals("text/plain")){
    				map.put(FILL_MAP_NAME, "" +  filename.replaceFirst("[.][^.]+$", ""));
    				map.put(FILL_MAP_PATH, "" + contents.get(i).path);
    				map.put(FILL_MAP_PARENTREV, "" + contents.get(i).rev);
    				fillMaps.add(map);
    			}
    		}
    		
    		fileNameAdapter = new SimpleAdapter(mContext, fillMaps, R.layout.list_item, from, to);
    	}
    	else{
    		showToast("not linked: returning null for file names adapter, because not linked");
    	}
	}
    
    static public void saveNewText(String title, String body){
    		String fileContents = body;
 		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());
 		try {
 			
 		    Entry newEntry = mApi.putFile("/" + title +".txt", inputStream,
 		    		fileContents.length(), null, new ProgressListener(){
						@Override
						public void onProgress(long total, long part) {
							if(total == part){
								showToast("saved!");
							}
						}
 		    });
 		    Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
 		} catch (DropboxUnlinkedException e) {
 		    // User has unlinked, ask them to link again here.
 		    Log.e("DbExampleLog", "User has unlinked.");
 		} catch (DropboxException e) {
 		    Log.e("DbExampleLog", "Something went wrong while uploading.");
 		} finally {
 		    if (inputStream != null) {
 		        try {
 		            inputStream.close();
 		        } catch (IOException e) {
 		        		showToast("Save exception: "+ e);
 		        }
 		    }
 		}
    }
}