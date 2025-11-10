package school.sptech;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SlackSend {

    public static void main(String[] args) {


        //teste
        logs log1 = new logs("Crítico","falha malvada",5);

        // url do slack nextview(teste)
//       
        // Recebe o texto para ser enviado ao slack
        String message = "Alerta do tipo " + log1.getTipoLog() + " detectado ás: " + log1.getDataHora();

        enviarNotificação(webhookUrl, message);
    }

    // envia a mensagem
    public static void enviarNotificação(String webhookUrl, String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = String.format("{\"text\": \"%s\"}", message);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Slack falou: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
