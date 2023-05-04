package main;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import main.menu.MenuAnimal;
import main.menu.MenuProfissional;
import main.menu.MenuServico;
import java.util.Scanner;

public class Aplicacao {

    public static void main(String[] args) {
        MongoClient client = new MongoClient("localhost", 27017); // Conectar ao servidor do MongoDB
        MongoDatabase db = client.getDatabase("zoologico"); // Conectar a database

        while1:
        while(true){ // Inicia o loop que controla o menu das entidades

            System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n");
            System.out.println("Escolha uma das entidades abaixo para movimenta-la (Somente numeros): ");
            System.out.println("1 - Profissional;");
            System.out.println("2 - Animal;");
            System.out.println("3 - Servicos");
            System.out.println("0 - Sair");

            Scanner scanner = new Scanner(System.in);
            Integer opcao = scanner.nextInt();

            if (opcao >= 0 && opcao < 4){ // Valida se a opção informada está dentro das opções disponíveis

                switch1: // Label do switch para identificá-lo
                switch (opcao){
                    case 1: // Abre o menu das ações referentes ao Profissional
                        MenuProfissional.switchProfissional(db);
                        break switch1;
                    case 2: // Abre o menu das ações referentes ao Animal
                        MenuAnimal.switchAnimal(db);
                        break switch1;
                    case 3: // Abre o menu das ações referentes aos Serviços
                        MenuServico.switchServicos(db);
                        break switch1;
                    case 0: // Finaliza o loop responsável pelo menu
                        break while1;
                }
            } else { // Se a opção informada não bater com nenhuma das alternativas, alerta o usuário e repete o loop.
                System.out.println("A opcao '" + opcao + "' nao condiz com nenhuma das alternativas. Tente novamente \n");
                continue;
            }
        }

        client.close(); // Encerra a conexão com o servidor
    }
}
