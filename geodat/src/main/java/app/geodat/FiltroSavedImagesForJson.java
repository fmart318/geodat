package app.geodat;

import java.io.File;
import java.io.FilenameFilter;

public class FiltroSavedImagesForJson implements FilenameFilter {
	
	String json = "";
	
	public FiltroSavedImagesForJson(String json){
		this.json = json;
	}
	
	@Override
	public boolean accept(File arg0, String arg1) {
		
		//if()
		
		return false;
	}

}
