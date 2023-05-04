package main.menu;

import com.mongodb.client.MongoDatabase;
import main.dao.AnimalDAO;
import main.dao.ProfissionalDAO;
import main.dao.ServicosRealizadosDAO;
import main.model.Animal;
import main.model.Profissional;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public abstract class MenuAnimal {
    public static void switchAnimal (MongoDatabase db){ // Responsável pelo menu dos Animais
        while1: // Label para identificar o while
        while (true){
            System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=- ANIMAL =-=-=-=-=-=-=-=-=-=-=-=-\n");
            System.out.println("Escolha uma das opcoes abaixo (Somente numeros): ");
            System.out.println("1 - Cadastrar animal;");
            System.out.println("2 - Buscar todos os animais;");
            System.out.println("3 - Buscar animais por ID do profissional;");
            System.out.println("4 - Buscar animais que ainda nao tiveram nenhum servico realizado no dia;");
            System.out.println("5 - Editar registro de animal;");
            System.out.println("6 - Excluir registro de animal;");
            System.out.println("0 - Voltar");

            Scanner scanner = new Scanner(System.in);
            Integer opcao = scanner.nextInt();

            if(opcao >= 0 && opcao < 7){
                switch1: // Label para identificar o switch
                switch (opcao){
                    case 1: // Cadastrar novo animal
                        ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);

                        if(!profissionalDAO.temRegistros()){ // Verifica se existe ao menos um registro de profissional gravado em banco
                            System.out.println("\nNao existe nenhum profissional salvo na base de dados." +
                                    "\n Por favor, cadastre um profissional para que seja possivel cadastrar um novo animal.");
                            break switch1;
                        }
                        cadastrarAnimal(db);
                        break switch1;
                    case 2: // Buscar todos os animais do banco
                        buscarAnimais(db);
                        break switch1;
                    case 3: // Buscar todos os animais relacionados à um profissional específico
                        buscarAnimaisPorProfissional(db);
                        break switch1;
                    case 4: // Busca todos os animais que ainda não tiveram nenhum serviço para o dia
                        buscarAnimaisSemServicoNoDia(db);
                        break switch1;
                    case 5:
                        editarAnimal(db);
                        break switch1;
                    case 6:
                        excluirAnimal(db);
                        break switch1;
                    case 0: // Volta para o menu inicial
                        break while1;
                    default:
                        break switch1;
                }
            } else {
                System.out.println("A opcao '" + opcao + "' nao condiz com nenhuma das alternativas. Tente novamente \n");
                continue;
            }
        }
    }

    private static void cadastrarAnimal(MongoDatabase db) { // Responsável pelo cadastro de novos animais
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nInforme o nome do animal: ");
            String nome = scanner.nextLine();
            System.out.println("Informe a raca do animal: ");
            String raca = scanner.nextLine();
            System.out.println("Informe o ID do treinador responsavel pelo animal: ");
            String treinadoridStr = scanner.nextLine();
            ObjectId treinadorid = new ObjectId(treinadoridStr);

            if ((nome == "" || nome == null) || (raca == "" || raca == null) || (treinadorid.toString() == "" || treinadorid == null)) { // Se o nome, raça ou treinador estiverem vazios, irá solicitar as informações novamente.
                System.out.println("\nUma das opcoes informadas esta vazia. Tente novamente.\n");
                continue;
            }

            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
            Profissional treinador = profissionalDAO.findById(treinadorid); // Busca o profissional no banco de dados

            if (treinador == null) {
                System.out.println("\n Nao existe nenhum profissional com o ID informado, tente novamente.");
                continue;
            }

            Animal animal = new Animal(nome, raca, treinador.getProfissionalid());
            AnimalDAO animalDAO = new AnimalDAO(db);

            animal.setAnimalid(animalDAO.gravar(animal)); // Inicia a transação para inserir o novo animal e já adiciona o ObjectID do animal inserido ao objeto

            System.out.println("Animal de ID " + animal.getAnimalid() + " foi cadastrado com sucesso!");

            break;
        }
    }

    private static void buscarAnimais (MongoDatabase db){ // Busca todos os profissionais do banco de dados
        AnimalDAO animalDAO = new AnimalDAO(db);
        List<Animal> animais = animalDAO.findAll(); // Executa select sem where no banco de dados. O ideal seria paginar



        System.out.println("\nLista de animais:\n");
        for (Animal animal : animais){
            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
            Profissional profissional = profissionalDAO.findById(animal.getTreinador());

            System.out.println("ID e nome: " + animal.getAnimalid() + " - " + animal.getNome() +
                    "\nRaca: " + animal.getRaca() +
                    "\nTreinador: " + profissional.getProfissionalid() + " - " + profissional.getNome() + "\n");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void buscarAnimaisPorProfissional(MongoDatabase db){ // Busca os animais relacionadas à um profissional específico
        AnimalDAO animalDAO = new AnimalDAO(db);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Informe o ID do profissional: ");
        String treinadoridStr = scanner.nextLine();
        ObjectId treinadorid = new ObjectId(treinadoridStr);

        List<Animal> animais = animalDAO.buscarPorProfissional(treinadorid); // Realiza o select no banco de dados

        System.out.println("\nLista de animais:\n");
        for (Animal animal : animais){
            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
            Profissional profissional = profissionalDAO.findById(animal.getTreinador());

            System.out.println("ID e nome: " + animal.getAnimalid() + " - " + animal.getNome() +
                    "\nRaca: " + animal.getRaca() +
                    "\nTreinador: " + profissional.getProfissionalid() + " - " + profissional.getNome() + "\n");
        }
    }

    private static void buscarAnimaisSemServicoNoDia(MongoDatabase db){ // Busca os animais que ainda nao tiveram nenhum servico no dia
        AnimalDAO animalDAO = new AnimalDAO(db);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Informe o dia:");
        Integer dia = scanner.nextInt();

        System.out.println("Informe o mes: ");
        Integer mes = scanner.nextInt() - 1;

        System.out.println("Informe o ano: ");
        Integer ano = scanner.nextInt();

        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, mes, dia);
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = formatoData.format(calendar.getTime());

        List<Animal> animais = animalDAO.buscarAnimaisComServicosNaoRealizados(calendar); // Realiza o select no banco de dados

        if (animais == null || animais.isEmpty()){
            System.out.println("\nTodos os animais ja tem ao menos um servico registrado para o dia.");
        } else {
            System.out.println("\nAnimais que ainda nao receberam nenhum servico no dia " + dataFormatada + ":");

            for (Animal animal : animais){
                System.out.println(animal.getAnimalid() + " - " + animal.getNome());
            }
        }
    }

    private static void editarAnimal(MongoDatabase db){
        Scanner scanner = new Scanner(System.in);
        ObjectId animalId = new ObjectId();

        // Faz a validação do input do usuário
        validador1:
        while (true){
            System.out.println("\nInforme o ID do registro que deseja editar:");
            String animalIdStr = scanner.nextLine();
            if (animalIdStr.isEmpty() || animalIdStr.isBlank() || animalIdStr == ""){
                System.out.println("\nO valor informado está inconsistente, tente novamente.");
                continue validador1;
            } else {
                animalId = new ObjectId(animalIdStr);
                break validador1;
            }
        }

        while (true){
            AnimalDAO animalDAO = new AnimalDAO(db);
            Animal animal = animalDAO.findById(animalId);

            System.out.println("\nInforme o novo nome do animal: ");
            String novoNome = scanner.nextLine();
            System.out.println("Informe a nova raca do animal: ");
            String novaRaca = scanner.nextLine();
            System.out.println("Informe o novo ID do treinador responsavel pelo animal: ");
            String treinadoridStr = scanner.nextLine();
            ObjectId novoTreinadorid = new ObjectId(treinadoridStr);

            if ((novoNome == "" || novoNome == null) || (novaRaca == "" || novaRaca == null) || (novoTreinadorid.toString() == "" || novoTreinadorid == null)) { // Se o nome, raça ou treinador estiverem vazios, irá solicitar as informações novamente.
                System.out.println("\nUma das opcoes informadas esta vazia. Tente novamente.\n");
                continue;
            }

            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
            Profissional novoProfissional = profissionalDAO.findById(novoTreinadorid);

            Animal novoAnimal = new Animal(novoNome, novaRaca, novoTreinadorid);

            // Cria o Document com o parâmetro "$set" para informar que é uma atualização, e alimenta o documento com os novos dados do registro
            Document novoAnimalDoc = new Document("$set", new Document("nome", novoAnimal.getNome())
                    .append("raca", novoAnimal.getRaca())
                    .append("treinadorid", novoAnimal.getTreinador()));

            animalDAO.atualizar(animal, novoAnimalDoc);

            System.out.println("\n Animal atualizado com sucesso!");

            break;
        }



    }

    private static void excluirAnimal(MongoDatabase db){
        Scanner scanner = new Scanner(System.in);
        ObjectId animalId = new ObjectId();

        // Faz a validação do input do usuário
        validador1:
        while (true){
            System.out.println("\nInforme o ID do registro que deseja excluir:");
            String animalIdStr = scanner.nextLine();
            if (animalIdStr.isEmpty() || animalIdStr.isBlank() || animalIdStr == ""){
                System.out.println("\nO valor informado está inconsistente, tente novamente.");
                continue validador1;
            } else {
                animalId = new ObjectId(animalIdStr);
                break validador1;
            }
        }

        AnimalDAO animalDAO = new AnimalDAO(db);
        Animal animal = animalDAO.findById(animalId);

        animalDAO.excluir(animal);

        System.out.println("Registro excluido com sucesso!");
    }
}
