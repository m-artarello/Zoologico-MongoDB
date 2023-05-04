package main.model;

import org.bson.types.ObjectId;

public class Animal {

    private ObjectId animalid;

    private String nome;

    private String raca;

    private ObjectId treinadorid;

    public Animal() {
    }

    public Animal(String nome, String raca, ObjectId treinadorid) {
        this.nome = nome;
        this.raca = raca;
        this.treinadorid = treinadorid;
    }

    public ObjectId getAnimalid() {
        return animalid;
    }

    public void setAnimalid(ObjectId animalid) {
        this.animalid = animalid;
    }

    public void setTreinadorid(ObjectId treinadorid) {
        this.treinadorid = treinadorid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public ObjectId getTreinador() {
        return treinadorid;
    }

    public void setTreinador(ObjectId treinadorId) {
        this.treinadorid = treinadorId;
    }
}