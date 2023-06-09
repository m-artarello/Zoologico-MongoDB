package main.menu;

import com.mongodb.client.MongoDatabase;
import main.dao.ProfissionalDAO;
import main.model.Profissional;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Scanner;

public abstract class MenuProfissional {

    public static void switchProfissional(MongoDatabase db){ // Responsável pelo menu dos Profissionais
        while1: // Label para identificar o while
        while (true){
            System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=- PROFISSIONAL =-=-=-=-=-=-=-=-=-=-=-=-\n");
            System.out.println("Escolha uma das opcoes abaixo (Somente numeros): ");
            System.out.println("1 - Cadastrar profissional;");
            System.out.println("2 - Consultar todos os profissionais;");
            System.out.println("0 - Voltar");

            Scanner scanner = new Scanner(System.in);
            Integer opcao = scanner.nextInt();

            if(opcao >= 0 && opcao < 3){
                switch1: // Label para identificar o switch
                switch (opcao){
                    case 1: // Cadastrar um novo profissional
                        cadastrarProfissional(db);
                        break switch1;
                    case 2: // Buscar todos os profissionais do banco
                        buscarProfissionais(db);
                        break switch1;
                    case 0: // Voltar para o menu inicial
                        break while1;
                    default:
                        break switch1;
                }
            } else{
                System.out.println("A opcao '" + opcao + "' nao condiz com nenhuma das alternativas. Tente novamente \n");
                continue;
            }
        }
    }

    public static void cadastrarProfissional(MongoDatabase db){ // Método para cadastrar um novo profissional
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println("\nInforme o nome do profissional: ");
            String nome = scanner.nextLine();
            System.out.println("Informe a funcao do profissional: ");
            String funcao = scanner.nextLine();

            if ((nome == "" || nome == null) || (funcao == "" || funcao == null)){ // Se o nome ou a função estiverem vazios, irá solicitar as informações novamente.
                System.out.println("\nUma das opcoes informadas esta vazia. Tente novamente.\n");
                continue;
            }

            Profissional profissional = new Profissional(nome, funcao);
            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);

            ObjectId profissionalid = profissionalDAO.gravar(profissional); // Inicia a transação do novo registro

            System.out.println("Profissional de ID " + profissionalid + " foi cadastrado com sucesso!");

            break;
        }
    }

    public static void buscarProfissionais(MongoDatabase db){ // Responsável pela busca de todos os profissionais do banco.
        ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
        List<Profissional> profissionais = profissionalDAO.findAll(); // Executa o método que consulta os profissionais do banco. O ideal seria paginar

        System.out.println("\nLista de profissionais:\n");

        for (Profissional profissional : profissionais){
            System.out.println("ID e nome: " + profissional.getProfissionalid() + " - " + profissional.getNome());
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
