package app.geodat.Util;

import java.io.File;
import java.io.FilenameFilter;

public class FiltroSavedImages implements FilenameFilter {
    @Override
    public boolean accept(File directorio, String nombreArchivo) {
        if(nombreArchivo.endsWith(".jpg") && !(nombreArchivo.contains("TEMP")))
            return true;
 
        return false;
    }
}