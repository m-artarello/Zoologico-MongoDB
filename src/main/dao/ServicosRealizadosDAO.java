package main.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import main.model.ServicosRealizados;
import main.model.Servico;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.*;

public class ServicosRealizadosDAO {
    // Injeta a dependência de um Entity Manager que será instanciado previamente
    private MongoDatabase db;
    private MongoCollection<Document> servicosRealizados;

    public ServicosRealizadosDAO(MongoDatabase db) {
        this.db = db;
        this.servicosRealizados = db.getCollection("servicosRealizados");
    }

    // Métodos CRUD para persistência dos dados

    public void gravar(ServicosRealizados servicosRealizados){
        System.out.println(servicosRealizados.getDatahora().getTime());

        Document novoServico = new Document("servico", servicosRealizados.getServico().toString())
                .append("animalid", servicosRealizados.getAnimal())
                .append("profissionalid", servicosRealizados.getProfissional())
                .append("datahora", servicosRealizados.getDatahora().getTime());

        this.servicosRealizados.insertOne(novoServico);
    }

    public void excluir(ServicosRealizados servicosRealizados){
        this.servicosRealizados.deleteOne(Filters.eq("_id", servicosRealizados.getServicosrealizadosid()));
    }

    public void atualizar(ServicosRealizados servicosRealizados, Document servicoAtualizado){
        this.servicosRealizados.updateOne(Filters.eq("_id", servicosRealizados.getServicosrealizadosid()), servicoAtualizado);
    }

    public ServicosRealizados findById(ObjectId id){
        Document servicosRealizadosDoc = this.servicosRealizados.find(Filters.eq("_id", id)).first(); // Busca um objeto da classe referenciada, utilizando o ID

        ServicosRealizados servicosRealizados = new ServicosRealizados(Servico.valueOf((String) servicosRealizadosDoc.get("servico")), (ObjectId) servicosRealizadosDoc.get("animalid"),
                (ObjectId) servicosRealizadosDoc.get("profissionalid"), (Calendar) servicosRealizadosDoc.get("datahora"));
        servicosRealizados.setServicosrealizadosid((ObjectId) servicosRealizadosDoc.get("_id"));

        return servicosRealizados;
    }

    public List<ServicosRealizados> findAll(){
        MongoCursor<Document> servicosRealizadosDoc = this.servicosRealizados.find().iterator();
        List<ServicosRealizados> servicosRealizados = null;

        while (servicosRealizadosDoc.hasNext()){
            ServicosRealizados servicoRealizado = new ServicosRealizados(
                    Servico.valueOf((String) servicosRealizadosDoc.next().get("servico")),
                    (ObjectId) servicosRealizadosDoc.next().get("animalid"),
                    (ObjectId) servicosRealizadosDoc.next().get("profissionalid"),
                    (Calendar) servicosRealizadosDoc.next().get("datahora"));
            servicoRealizado.setServicosrealizadosid((ObjectId) servicosRealizadosDoc.next().get("_id"));

            servicosRealizados.add(servicoRealizado);
        }
        return servicosRealizados;
    }

    public List<ServicosRealizados> buscaServicosPorDataEAnimal(Calendar data, ObjectId animalid){ // Irá retornar os serviços ja realizados no dia
        Date dataInicio = data.getTime();
        dataInicio.setHours(0);
        dataInicio.setMinutes(0);
        dataInicio.setSeconds(0);
        Date dataFinal = data.getTime();
        dataFinal.setHours(23);
        dataInicio.setMinutes(59);
        dataInicio.setSeconds(59);

        Bson filter = Filters.and(
                        Filters.and(
                                Filters.gte("datahora", dataInicio),
                                Filters.lte("datahora", dataFinal)
                        ),
                        Filters.eq("animalid", animalid)
        );

        FindIterable<Document> servicosRealizadosDoc = this.servicosRealizados.find(filter);
        List<ServicosRealizados> servicosRealizados = new ArrayList<>();

        for (Document doc : servicosRealizadosDoc){
            Calendar datahora = Calendar.getInstance();
            datahora.setTime(doc.getDate("datahora"));
            ServicosRealizados servicoRealizado = new ServicosRealizados(
                    Servico.valueOf((String) doc.get("servico")),
                    (ObjectId) doc.get("animalid"),
                    (ObjectId) doc.get("profissionalid"),
                    datahora);
            servicoRealizado.setServicosrealizadosid((ObjectId) doc.get("_id"));

            servicosRealizados.add(servicoRealizado);
        }
        return servicosRealizados;
    }

    public List<Servico> buscaServicosNaoRealizadosNoDia(Calendar data, ObjectId animalid){
        ArrayList<Servico> servicosNaoRealizados = new ArrayList<>(List.of(Servico.values())); // Alimenta a lista de serviços disponíveis
        List<ServicosRealizados> servicosRealizados = buscaServicosPorDataEAnimal(data, animalid); // Busca serviços já realizados no dia para o animal
        List<Servico> servicosJaRealizados = new ArrayList<Servico>();

        if (servicosRealizados == null || servicosRealizados.isEmpty()){ // Verifica se nenhum serviço foi realizado no dia
            return servicosNaoRealizados; // Se nenhum serviço foi realizado, retorna a lista cheia de serviços disponíveis
        } else { // Se não, filtra os serviços não realizados com base nos serviços já realizados no dia para o animal
            for (ServicosRealizados servicos : servicosRealizados) {
                servicosJaRealizados.add(servicos.getServico());
            }
            servicosNaoRealizados.removeIf(servico -> servicosJaRealizados.contains(servico)); // Remove da lista os serviços que já foram realizados no dia
        }
        return servicosNaoRealizados;
    }
}
