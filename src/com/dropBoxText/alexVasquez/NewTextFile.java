package com.dropBoxText.alexVasquez;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class NewTextFile extends Activity implements OnClickListener{
	
	EditText newTextFileTitleEditText,newTextFileBodyEditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// android user interface
		setContentView(R.layout.new_text_file);
		
		newTextFileTitleEditText = (EditText)findViewById(R.id.newTextFileTitleEditText);
		newTextFileTitleEditText.setText(null);
		
		newTextFileBodyEditText = (EditText)findViewById(R.id.newTextFileBodyEditText);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.saveDocumentNewDocument:
            	if(DropBox.isLinked() == true){
            		if(newTextFileTitleEditText.getText().toString().equals("")){
            			DropBox.showToast("Enter a title to save");
            		}
            		else{
            			DropBox.saveNewText(newTextFileTitleEditText.getText().toString(), newTextFileBodyEditText.getText().toString());
        				this.finish();
            		}
    			}
    			else{
    				DropBox.showToast("Dropbox is not linked");
    			}
            		return true;
            		
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newdocument_menu, menu);
        return true;
    }
    
}