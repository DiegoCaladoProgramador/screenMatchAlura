package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.Dados;
import br.com.alura.tabelafipe.model.Modelos;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConsumoApi;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitor = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        String menu = """
                *** OPÇÔES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                
                """;

        System.out.println(menu);

        String opcao = leitor.nextLine();
        String endereco;

        System.out.println(">"+opcao+"<");

        if(opcao.toLowerCase().equals("carro")){
            System.out.println("entrei nos carros");
            endereco = URL_BASE + "carros/marcas";
        }else if(opcao.toLowerCase().equals("mot")){
            System.out.println("entrei nas motos");
            endereco = URL_BASE + "carros/marcas";
        } else {
            System.out.println("entrei nos caminhoes");
            endereco = URL_BASE + "caminhoes/marcas";
        }

        String json = consumo.obterDados(endereco);
        System.out.println(json);

        List<Dados> marcas = conversor.obterLista(json,Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta");
        String codigoMarca = leitor.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        Modelos modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("Modelos dessa marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Digite um trecho do nome do veiculo a ser buscado: ");
        String nomeVeiculo = leitor.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("modelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
        String codigoModelo = leitor.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i<anos.size(); i++){
            String enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("Todos veiculos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);



    }
}
