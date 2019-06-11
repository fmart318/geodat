package app.geodat.Util;

import java.io.File;
import java.io.FilenameFilter;

public class FiltroSavedAudios implements FilenameFilter {

	@Override
	public boolean accept(File dir, String filename) {
		if(filename.endsWith(".wav"))
            return true;
 
        return false;
	}

}
