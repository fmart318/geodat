package app.geodat;

import java.io.File;

import android.os.Environment;
import app.geodat.Util.Filtro;
import app.geodat.Util.FiltroCsv;
import app.geodat.Util.FiltroSavedAudios;
import app.geodat.Util.FiltroSavedImages;

public class ListadoArchivos {
    //Al método le pasamos el directorio donde vamos a buscar todos los archivos/directorios que tenga.
    public static String[] getImages(String directorioPrincipal){
        String[] archivos = new File(directorioPrincipal).list(new Filtro());
        return archivos;
    }
    
    public static String[] getCsvFiles(String directorioPrincipal){
        String[] archivos = new File(directorioPrincipal).list(new FiltroCsv());
        return archivos;
    }
    
    public static String[] getSavedImages(String directorioPrincipal){
        String[] archivos = new File(directorioPrincipal).list(new FiltroSavedImages());
        return archivos;
    }
    
    public static String[] getSavedImagesforJson(String directorioPrincipal, String json){
        String[] archivos = new File(directorioPrincipal).list(new FiltroSavedImagesForJson(json));
        return archivos;
    }
    
    public static String[] getSavedAudios(String directorioPrincipal){
        String[] archivos = new File(directorioPrincipal).list(new FiltroSavedAudios());
        return archivos;
    }
    
    public static String[] getFiles(String directorioPrincipal){
        String[] archivos = new File(directorioPrincipal).list();
        return archivos;
    }
}


