package view.next;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class Main {

    public static void main(String[] args) throws IOException, InvalidFormatException {

        System.out.println("""
                     _   _                 _    __      __  _                  \s
                    | \\ | |               | |   \\ \\    / / (_)                 \s
                    |  \\| |   ___  __  __ | |_   \\ \\  / /   _    ___  __      __
                    | . ` |  / _ \\ \\ \\/ / | __|   \\ \\/ /   | |  / _ \\ \\ \\ /\\ / /
                    | |\\  | |  __/  >  <  | |_     \\  /    | | |  __/  \\ V  V /\s
                    |_| \\_|  \\___| /_/\\_\\  \\__|     \\/     |_|  \\___|   \\_/\\_/ \s
                    """);

        S3Client s3Client = new S3Provider().getS3Client();

        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket("s3-bucket-excel-nextview")
                .build();
        List<S3Object> objects = s3Client.listObjects(listObjects).contents();

        for (S3Object object : objects) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket("s3-bucket-excel-nextview")
                    .key(object.key())
                    .build();

            InputStream objectContent = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());

            Files.copy(objectContent, new File(object.key()).toPath(), StandardCopyOption.REPLACE_EXISTING);

        }

        Serie series = new Serie();
        series.ExtrairSeries();

        Filme filmes = new Filme();
        filmes.ExtrairFilmes();

        // Filmes
        String webhookUrlFilme = "https://hooks.slack.com/services/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";
        String messageFilme = String.valueOf(Filme.log.getMensagemLog());
        enviarNotificaçãoFilme(webhookUrlFilme, messageFilme);

        //Series
        String webhookUrlSerie = "https://hooks.slack.com/services/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";
        String messageSerie = String.valueOf(Serie.log);
        enviarNotificaçãoSerie(webhookUrlSerie, messageSerie);

    }

    // envia para Filmes
    public static void enviarNotificaçãoFilme(String webhookUrlFilme, String messageFilme) {
        try {
            URL url = new URL(webhookUrlFilme);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = String.format("{\"text\": \"%s\"}", messageFilme);

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

    // envia a para Series
    public static void enviarNotificaçãoSerie(String webhookUrlSerie, String messageSerie) {
        try {
            URL url = new URL(webhookUrlSerie);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = String.format("{\"text\": \"%s\"}", messageSerie);

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
