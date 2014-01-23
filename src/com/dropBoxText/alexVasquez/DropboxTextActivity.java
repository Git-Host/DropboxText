package com.dropBoxText.alexVasquez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DropboxTextActivity extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */
    
    ListView textFileNamesMainList;
    DropBox dropbox;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Drop box sign in and authentication
        dropbox = new DropBox(this.getApplicationContext());
        dropbox.signIn();
        
        // android user interface stuff
        setContentView(R.layout.main);
        
        textFileNamesMainList = (ListView)findViewById(R.id.textFileNamesMainList);
        
        populateListView();
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logOutMainOptionsMainMenuOption:
            		dropbox.logOut();
            		this.finish();
            		return true;
            case R.id.newTextFileOptionsMainMenuOption:
            		startActivity(new Intent(this, NewTextFile.class));
            		return true;
            case R.id.refreshMainDocumentList:
            		populateListView();
            	return true;
            		
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        dropbox.onResumeAuthentication();
                
        populateListView();
    }
    
    private void populateListView(){
    		if(DropBox.isLinked() == true){
    			dropbox.getTextFileNames();
    			textFileNamesMainList.setAdapter(DropBox.fileNameAdapter);
        		textFileNamesMainList.setOnItemClickListener(this);
    			
        }
        else{
        	textFileNamesMainList.setAdapter(null);
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		startActivity( new Intent(this, CrudTextFile.class).putExtra
				(DropBox.FILL_MAP_PATH, DropBox.fillMaps.get(position).get(DropBox.FILL_MAP_PATH)).putExtra
				(DropBox.FILL_MAP_PARENTREV, DropBox.fillMaps.get(position).get(DropBox.FILL_MAP_PARENTREV)).putExtra
				(DropBox.FILL_MAP_NAME, DropBox.fillMaps.get(position).get(DropBox.FILL_MAP_NAME)));
	}
}