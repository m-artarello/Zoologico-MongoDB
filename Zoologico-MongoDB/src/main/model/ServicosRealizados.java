package main.model;

import org.bson.types.ObjectId;
import java.util.Calendar;

public class ServicosRealizados {

    private ObjectId servicosrealizadosid;

    private Servico servico;

    private ObjectId animalid;

    private ObjectId profissionalid;
    private Calendar datahora;

    public ServicosRealizados() {
    }

    public ServicosRealizados(Servico servico, ObjectId animalid, ObjectId profissionalid, Calendar datahora) {
        this.servico = servico;
        this.animalid = animalid;
        this.profissionalid = profissionalid;
        this.datahora = datahora;
    }

    public ObjectId getServicosrealizadosid() {
        return servicosrealizadosid;
    }

    public void setServicosrealizadosid(ObjectId servicosrealizadosid) {
        this.servicosrealizadosid = servicosrealizadosid;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public ObjectId getAnimal() {
        return animalid;
    }

    public void setAnimal(ObjectId animalid) {
        this.animalid = animalid;
    }

    public ObjectId getProfissional() {
        return profissionalid;
    }

    public void setProfissional(ObjectId profissionalid) {
        this.profissionalid = profissionalid;
    }

    public Calendar getDatahora() {
        return datahora;
    }

    public void setDatahora(Calendar datahora) {
        this.datahora = datahora;
    }
}
