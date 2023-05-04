package main.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import main.model.Profissional;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalDAO {
    // Injeta a dependência de um Entity Manager que será instanciado previamente
    private MongoDatabase db;
    private MongoCollection<Document> profissionais;

    public ProfissionalDAO(MongoDatabase db) {
        this.db = db;
        this.profissionais = db.getCollection("profissionais");
    }

    // Métodos CRUD para persistência dos dados

    public ObjectId gravar(Profissional profissional){
        Document novoProfissional = new Document("nome", profissional.getNome())
                .append("raca", profissional.getFuncao());

        this.profissionais.insertOne(novoProfissional);

        return novoProfissional.getObjectId("_id");
    }

    public void excluir(Profissional profissional){
        this.profissionais.deleteOne(Filters.eq("_id", profissional.getProfissionalid()));
    }

    public void atualizar(Profissional profissional, Document profissionalAtualizado){
        this.profissionais.updateOne(Filters.eq("_id", profissional.getProfissionalid()), profissionalAtualizado);
    }

    public Profissional findById(ObjectId id){
        Document profissionalDoc = this.profissionais.find(Filters.eq("_id", id)).first(); // Busca um objeto da classe referenciada, utilizando o ID

        Profissional profissional = new Profissional((String) profissionalDoc.get("nome"), (String) profissionalDoc.get("funcao"));
        profissional.setProfissionalid((ObjectId) profissionalDoc.get("_id"));

        return profissional;
    }

    public List<Profissional> findAll(){
        FindIterable<Document> profissionaisDoc = this.profissionais.find();
        List<Profissional> profissionais = new ArrayList<>();

        for (Document doc : profissionaisDoc){
            Profissional profissional = new Profissional((String) doc.get("nome"), (String) doc.get("funcao"));
            profissional.setProfissionalid((ObjectId) doc.get("_id"));

            profissionais.add(profissional);
        }
        return profissionais;
    }

    public Boolean temRegistros() {
        MongoCursor<Document> profissionaisDoc = this.profissionais.find().iterator();

        if (profissionaisDoc.hasNext()){ // Verificar se existem registros no banco de dados
            return true;
        }
        return false;
    }
}
