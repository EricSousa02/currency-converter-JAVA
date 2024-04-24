package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<String> codigosMoeda = new ArrayList<>();
    private static final List<String> nomesMoeda = new ArrayList<>();

    static {
        codigosMoeda.add("ARS");
        codigosMoeda.add("BOB");
        codigosMoeda.add("BRL");
        codigosMoeda.add("CLP");
        codigosMoeda.add("COP");
        codigosMoeda.add("USD");

        nomesMoeda.add("Peso argentino");
        nomesMoeda.add("Boliviano boliviano");
        nomesMoeda.add("Real brasileiro");
        nomesMoeda.add("Peso chileno");
        nomesMoeda.add("Peso colombiano");
        nomesMoeda.add("Dólar americano");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha uma opção para a moeda de origem base:");
        for (int i = 0; i < codigosMoeda.size(); i++) {
            System.out.println((i+1) + " - " + codigosMoeda.get(i) + " - " + nomesMoeda.get(i));
        }

        System.out.print("Digite o número da moeda de origem desejada: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consome o caractere de nova linha

        if (escolha < 1 || escolha > codigosMoeda.size()) {
            System.out.println("Opção inválida!");
            return;
        }

        String moedaOrigem = codigosMoeda.get(escolha - 1);

        try {
            String apiKey = "9ca4ddbbeb3203ff450f7a03";
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + moedaOrigem);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response usando Gson
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

                // Exibir moeda de origem
                String codigoBase = jsonObject.get("base_code").getAsString();
                System.out.println("################################################################################");
                System.out.println("Moeda de origem: " + codigoBase);
                System.out.println("################################################################################");

                // Exibir taxa de conversão
                JsonObject taxasConversao = jsonObject.getAsJsonObject("conversion_rates");

                // Exibir taxa de conversão
                if (taxasConversao.has(moedaOrigem)) {
                    double taxaMoedaOrigem = taxasConversao.get(moedaOrigem).getAsDouble();

                    // Pergunta ao usuário para qual moeda deseja converter
                    System.out.println("Converter " + moedaOrigem + " para:");
                    for (int i = 0; i < codigosMoeda.size(); i++) {
                        System.out.println((i+1) + " - " + codigosMoeda.get(i) + " - " + nomesMoeda.get(i));
                    }
                    System.out.print("Digite o número da moeda para qual deseja converter: ");
                    int escolhaMoeda = scanner.nextInt();
                    scanner.nextLine(); // Consome o caractere de nova linha

                    if (escolhaMoeda < 1 || escolhaMoeda > codigosMoeda.size()) {
                        System.out.println("Opção inválida!");
                        return;
                    }

                    String moedaAlvo = codigosMoeda.get(escolhaMoeda - 1);

                    // Pergunta ao usuário o valor a ser convertido
                    System.out.println("################################################################################");
                    System.out.print("Digite o valor a ser convertido: ");
                    double valorConverter = scanner.nextDouble();


                    // Calcula a conversão
                    double valorConvertido = valorConverter * taxaMoedaOrigem / taxasConversao.get(moedaAlvo).getAsDouble();
                    String valorConvertidoFormatado = String.format("%.2f", valorConvertido);
                    System.out.println("################################################################################");
                    System.out.println("Valor convertido de " + moedaOrigem + " para " + moedaAlvo + ": " + valorConvertidoFormatado);

                } else {
                    System.out.println("Taxa de conversão não disponível.");
                }

            } else {
                System.out.println("Falha ao obter os dados. Código de resposta: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
