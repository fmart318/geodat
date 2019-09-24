package app.geodat;

public interface ActualizarDelegate {
    void actualizarExitoso();
    void actualizarFallo();
    void notificar(String msg);
}
