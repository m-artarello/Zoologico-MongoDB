package main.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import main.model.Animal;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.*;

public class AnimalDAO {
    // Injeta a dependência de um Entity Manager que será instanciado previamente
    private MongoDatabase db;
    private MongoCollection<Document> animais;


    public AnimalDAO(MongoDatabase db) {
        this.db = db;
        this.animais = db.getCollection("animais");
    }

    // Métodos CRUD para persistência dos dados

    public ObjectId gravar(Animal animal){
        Document novoAnimal = new Document("nome", animal.getNome())
                .append("raca", animal.getRaca())
                .append("treinadorid", animal.getTreinador());

        this.animais.insertOne(novoAnimal);

        return novoAnimal.getObjectId("_id");
    }

    public void excluir(Animal animal){
        this.animais.deleteOne(Filters.eq("_id", animal.getAnimalid()));
    }

    public void atualizar(Animal animal, Document animalAtualizado){
        this.animais.updateOne(Filters.eq("_id", animal.getAnimalid()), animalAtualizado);
    }

    public Animal findById(ObjectId id){
        Document animalDoc = this.animais.find(Filters.eq("_id", id)).first(); // Busca um objeto da classe referenciada, utilizando o ID
        Animal animal = new Animal((String) animalDoc.get("nome"), (String) animalDoc.get("raca"), (ObjectId)animalDoc.get("treinadorid"));
        animal.setAnimalid((ObjectId) animalDoc.get("_id"));
        return animal;
    }

    public List<Animal> findAll(){
        FindIterable<Document> animaisDoc = this.animais.find();
        List<Animal> animais = new ArrayList<>();

        for (Document doc : animaisDoc){
            Animal animal = new Animal((String) doc.get("nome"), (String) doc.get("raca"), (ObjectId)doc.get("treinadorid"));
            animal.setAnimalid((ObjectId) doc.get("_id"));

            animais.add(animal);
        }
        return animais;
    }

    public List<Animal> buscarPorProfissional(ObjectId profissionalid){
        FindIterable<Document> animaisDoc = this.animais.find(Filters.eq("treinadorid", profissionalid)); //
        List<Animal> animais = new ArrayList<>();

        for (Document doc : animaisDoc){
            Animal animal = new Animal((String) doc.get("nome"), (String) doc.get("raca"), (ObjectId)doc.get("treinadorid"));
            animal.setAnimalid((ObjectId) doc.get("_id"));

            animais.add(animal);
        }
        return animais;
    }


    public ArrayList<ObjectId> buscarAnimaisComServicosJaRealizados(Calendar data) {
        MongoCollection<Document> servicosRealizados = db.getCollection("servicosRealizados");

        Date dataInicio = data.getTime();
        dataInicio.setHours(0);
        dataInicio.setMinutes(0);
        dataInicio.setSeconds(0);
        Date dataFinal = data.getTime();
        dataFinal.setHours(23);
        dataInicio.setMinutes(59);
        dataInicio.setSeconds(59);

        Bson filter = Filters.and(
                        Filters.gte("datahora", dataInicio),
                        Filters.lte("datahora", dataFinal)
                );

        FindIterable<Document> servicosRealizadosDoc = servicosRealizados.find(filter);

        ArrayList<ObjectId> animaisId = new ArrayList<>();

        for (Document doc : servicosRealizadosDoc){
            animaisId.add((ObjectId) doc.get("animalid"));
        }
        return animaisId;
    }

    public List<Animal> buscarAnimaisComServicosNaoRealizados(Calendar data){ // Busca os animais que não tiveram nenhum serviço realizado no dia.
        ArrayList<ObjectId> animaisId = buscarAnimaisComServicosJaRealizados(data);

        Bson filter = Filters.nin("_id", animaisId);
        FindIterable<Document> animaisDoc = this.animais.find(filter);

        List<Animal> animais = new ArrayList<>();

        for (Document doc : animaisDoc){
            Animal animal = new Animal((String) doc.get("nome"), (String) doc.get("raca"), (ObjectId)doc.get("treinadorid"));
            animal.setAnimalid((ObjectId) doc.get("_id"));

            animais.add(animal);
        }
        return animais;
    }
}
