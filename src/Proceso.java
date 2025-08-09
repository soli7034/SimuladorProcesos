public class Proceso {
    enum Estado { ESPERANDO, EJECUTANDO, PAUSADO, TERMINADO }

    int pid;
    String nombre;
    int ram;
    int duracion;
    private int duracionRestante;
    private Estado estado;

    public Proceso(int pid, String nombre, int ram, int duracion) {
        this.pid = pid;
        this.nombre = nombre;
        this.ram = ram;
        this.duracion = duracion;
        this.duracionRestante = duracion;
        this.estado = Estado.ESPERANDO;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void decrementarDuracion() {
        if (duracionRestante > 0 && estado == Estado.EJECUTANDO) {
            duracionRestante--;
        }
    }

    public int getDuracionRestante() {
        return duracionRestante;
    }

    @Override
    public String toString() {
        return "No: " + pid + ", Nombre: " + nombre + ", RAM: " + ram + " MB, Duración: " + duracion + " s, Estado: " + estado;
    }
}
