package main.model;

import org.bson.types.ObjectId;

public class Profissional {

    private ObjectId profissionalid;

    private String nome;
    private String funcao;

    public Profissional() {
    }

    public Profissional(String nome, String funcao) {
        this.nome = nome;
        this.funcao = funcao;
    }

    public ObjectId getProfissionalid() {
        return profissionalid;
    }

    public void setProfissionalid(ObjectId profissionalid) {
        this.profissionalid = profissionalid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}
