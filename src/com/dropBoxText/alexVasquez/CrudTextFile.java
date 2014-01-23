package com.dropBoxText.alexVasquez;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class CrudTextFile extends Activity implements OnClickListener {
	
	TextView textView1;
	EditText mainEditText,exisitingDocumentFileName;
	String parentRev, textFileSimpleName, path;
	// 848s8
	String newDocTitlePath;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// android user interface
		setContentView(R.layout.crud_text_file);
		exisitingDocumentFileName = (EditText)findViewById(R.id.exisitingDocumentFileName);
		mainEditText = (EditText)findViewById(R.id.mainEditText);
		
		// get document information from previous activity
		Bundle extras = this.getIntent().getExtras();
		path = extras.getString(DropBox.FILL_MAP_PATH);
		parentRev = extras.getString(DropBox.FILL_MAP_PARENTREV);
		
		// get a simplified document name from the path
		textFileSimpleName = new File(path).getName();
		textFileSimpleName =	textFileSimpleName.replaceFirst("[.][^.]+$", "");

		// sets the title edit text to the path with the removed extension
		exisitingDocumentFileName.setText(textFileSimpleName);
		
		// sets the body text of the document to the main body edit text
		mainEditText.setText(DropBox.getTextBody(path));
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.deleteDocumentCrudMenu:
            		if(DropBox.isLinked() == true){
            			DropBox.deleteDocument(path);
                		finish();
            		}
            		else{
            			DropBox.showToast("Cannot delete: Not linked");
            		}
            		
            		return true;
            case R.id.saveDocumentCrudMenu:
            		newDocTitlePath = exisitingDocumentFileName.getText().toString();
            		
            		if(textFileSimpleName.equals(newDocTitlePath)){
            			DropBox.updateTextDocument(parentRev, exisitingDocumentFileName.getText().toString(), mainEditText.getText().toString());
            			this.finish();
            		} else{
            			DropBox.showToast("The title has been changed, please rename");
            		}
            		
            		
            		return true;
            case R.id.renameDocumentCrudMenu:
            		newDocTitlePath = exisitingDocumentFileName.getText().toString();
            		if(DropBox.moveFile(path, newDocTitlePath + ".txt")){
            			textFileSimpleName = newDocTitlePath;
            			path = newDocTitlePath + ".txt";
            		}
            		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_crud_text_file, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		}
	}
}
