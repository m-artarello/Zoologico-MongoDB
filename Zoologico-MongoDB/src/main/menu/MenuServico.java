package main.menu;

import com.mongodb.client.MongoDatabase;
import main.dao.AnimalDAO;
import main.dao.ProfissionalDAO;
import main.dao.ServicosRealizadosDAO;
import main.model.Animal;
import main.model.Profissional;
import main.model.Servico;
import main.model.ServicosRealizados;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public abstract class MenuServico {

    public static void switchServicos(MongoDatabase db){ // Responsável pelo menu dos Serviços
        while1: // Label para identificar o while
        while (true) {
            System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=- SERVICO =-=-=-=-=-=-=-=-=-=-=-=-\n");
            System.out.println("Escolha uma das opcoes abaixo (Somente numeros): ");
            System.out.println("1 - Consultar lista de servicos;");
            System.out.println("2 - Consultar servicos ainda nao realizados para um animal no dia;");
            System.out.println("3 - Registrar a realizacao de um novo servico;");
            System.out.println("0 - Voltar");

            Scanner scanner = new Scanner(System.in);
            Integer opcao = scanner.nextInt();

            if(opcao >= 0 && opcao < 4) {
                switch1: // Label para identificar o switch
                switch (opcao) {
                    case 1: // Consultar lista completa de servicos
                        consultarListaServicos();
                        break switch1;
                    case 2: // Consultar servicos não realizados em um dia específico para um animal específico
                        consultarServicosNaoRealizadosNoDiaPorAnimal(db);
                        break switch1;
                    case 3: // Registrar a realização de um serviço
                        cadastrarServico(db);
                        break switch1;
                    case 0: // Voltar oa menu inicial
                        break while1;
                }
            }
        }
    }

    public static void consultarListaServicos(){ // Consulta lista padrão de serviços através do Enum
        System.out.println("=-=-=-=-=-=-=-=-=- LISTA DIARIA DE SERVICOS =-=-=-=-=-=-=-=-=-\n");
        Integer i = 0;

        for (Servico servico : Servico.values()){
            System.out.println(i + " - " + servico.getDescricao());
            i++;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void consultarServicosNaoRealizadosNoDiaPorAnimal(MongoDatabase db){ // Consulta os servicos ainda nao realizados no dia para um animal especifico
        Scanner scanner = new Scanner(System.in);
        ObjectId animalid = new ObjectId();

        while (true){
            System.out.println("Informe o ID do animal: ");
            String animalidStr = scanner.nextLine();
            animalid = new ObjectId(animalidStr);

            AnimalDAO animalDAO = new AnimalDAO(db);
            Animal animal = animalDAO.findById(animalid); // Busca o animal infdrmado pelo usuário

            if(animal == null){
                System.out.println("Nenhum animal encontrado com o ID informado, tente novamente.");
                continue;
            } else {
                break;
            }
        }

        System.out.println("Informe o dia");
        Integer dia = scanner.nextInt();

        System.out.println("Informe o mes: ");
        Integer mes = scanner.nextInt() - 1;

        System.out.println("Informe o ano: ");
        Integer ano = scanner.nextInt();

        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, mes, dia);
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = formatoData.format(calendar.getTime());

        ServicosRealizadosDAO servicosRealizadosDAO = new ServicosRealizadosDAO(db);
        List<Servico> servicosNaoRealizados = servicosRealizadosDAO.buscaServicosNaoRealizadosNoDia(calendar, animalid); // Realiza o select no banco de dados

        System.out.println("\nServicos nao realizados para o animal de ID " + animalid + " no dia " + dataFormatada + "\n");

        for (Servico servico : servicosNaoRealizados){
            System.out.println(servico.getDescricao());
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cadastrarServico(MongoDatabase db){ // Registrar serviços realizados


        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nInforme o servico que deseja registrar (Somente numeros): ");
            consultarListaServicos(); // Consome o método que imprime a lista padrão de serviços
            Integer opcao = scanner.nextInt();
            Servico servico = null;

            if (!(opcao >= 0 && opcao < Servico.values().length)){ // Verifica se a opção informada está dentro dos valores disponíveis
                System.out.println("Nenhuma altenativa condiz com o valor informado. \nTente novamente.");
                continue;
            } else {
                servico = Servico.values()[opcao];
            }

            Boolean ctrl = true;

            ObjectId animalid = new ObjectId();

            validadorId:
            while(ctrl){
                System.out.println("\nInforme o ID do animal que deseja registrar o servico '" + Servico.values()[opcao].getDescricao() + "'.");
                String animalidStr = scanner.nextLine();

                if(animalidStr.isBlank() || animalidStr.isEmpty() || animalidStr == ""){
                    System.out.println("\nNenhum ID informado. Por favor tente novamente");
                    continue validadorId;
                } else {
                    animalid = new ObjectId(animalidStr);
                    ctrl = false;
                }
            }

            AnimalDAO animalDAO = new AnimalDAO(db);
            Animal animal = animalDAO.findById(animalid); // Busca o animal informado pelo usuário no banco de dados

            if(animal == null){
                System.out.println("Nenhum animal com o ID '" + animalid + "' foi encontrado. \nTente novamente.");
                continue;
            }

            System.out.println("\nInforme o ID do treinador/profissional que realizou o servico '" + Servico.values()[opcao].getDescricao() + "'.");
            String treinadoridStr = scanner.nextLine();
            ObjectId treinadorid = new ObjectId(treinadoridStr);

            ProfissionalDAO profissionalDAO = new ProfissionalDAO(db);
            Profissional profissional = profissionalDAO.findById(treinadorid); // Busca o profissional informado pelo usuário no banco de dados

            if(profissional == null){
                System.out.println("Nenhum profissional com o ID '" + animalid + "' foi encontrado. \nTente novamente.");
                continue;
            }

            System.out.println("Informe o dia do mes em que o servico foi realizado");
            Integer dia = scanner.nextInt();

            System.out.println("Informe o mes: ");
            Integer mes = scanner.nextInt() - 1;

            System.out.println("Informe o ano: ");
            Integer ano = scanner.nextInt();

            System.out.println("Informe a hora: ");
            Integer hora = scanner.nextInt();

            System.out.println("Informe o minuto: ");
            Integer minuto = scanner.nextInt();

            Calendar calendar = Calendar.getInstance();
            calendar.set(ano, mes, dia, hora, minuto);
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataFormatada = formato.format(calendar.getTime());

            ServicosRealizados servicoRealizado = new ServicosRealizados(servico, animal.getAnimalid(), profissional.getProfissionalid(), calendar);
            ServicosRealizadosDAO servicosRealizadosDAO = new ServicosRealizadosDAO(db);

            servicosRealizadosDAO.gravar(servicoRealizado); // Inicia transação de inserção

            System.out.println("\nO servico com o dados abaixo foi gravado com sucesso!");
            System.out.println("Servico: " + servico.getDescricao() + "\n" +
                    "Animal: " + animal.getAnimalid() + " - " + animal.getNome() +
                    "\nProfissional: " + profissional.getProfissionalid() + " - " + profissional.getNome() +
                    "\nData e hora: " +  dataFormatada);
            break;
        }

    }
}
