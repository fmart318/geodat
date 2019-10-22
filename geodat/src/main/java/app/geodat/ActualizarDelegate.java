package app.geodat;

public interface ActualizarDelegate {
    void actualizarExitoso();
    void actualizarFallo();
    void sinConexion();
    void notificar(String msg);
}
