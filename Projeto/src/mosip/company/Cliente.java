package mosip.company;

public class Cliente {

    private state estado;

    private float tempoDeChegada;
    private float tempoFimDeCorte;
    private float horaDeChegada;

    public enum state {
        ESPERA, CORTAR_BABELO;
    }

    public Cliente() {

        estado = state.ESPERA;
        tempoDeChegada = 0;
        horaDeChegada = 0;
    }

    public void mudaEstado(){
        if (estado== state.ESPERA)
            estado = state.CORTAR_BABELO;
        estado = state.ESPERA;
    }

    public void setTempoChegada(float tempo){
        tempoDeChegada = tempo;
    }

    public void setTempoCorte(float tempo){
        tempoFimDeCorte = tempo;
    }


    public state getEstado() {
        return estado;
    }

    public float getTempoDeChegada() {
        return tempoDeChegada;
    }

    public float getTempoFimDeCorte() {
        return tempoFimDeCorte;
    }

    public float getHoraDeChegada() {
        return horaDeChegada;
    }

    public void setHoraDeChegada(float horaDeChegada) {
        this.horaDeChegada = horaDeChegada;
    }
}
