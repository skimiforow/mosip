package mosip.company;

public class Barbeiro {

    private state estado;

    public enum state {
        LIVRE, OCUPADO;
        }

    public Barbeiro() {

        estado = state.LIVRE;
    }

    public void setEstado(state estado) {
        this.estado = estado;
    }

    public state getEstado() {
        return estado;
    }
}
