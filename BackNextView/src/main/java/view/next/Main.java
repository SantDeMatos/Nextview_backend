package view.next;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

//        System.out.println(System.getenv("DB_USER"));
//        System.out.println(System.getenv("DB_PASSWORD"));
//        System.out.println(System.getenv("DB_URL"));

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

//        Filme filmes = new Filme();
//        filmes.definirCredenciais();
//        filmes.ExtrairFilmes();

        Serie series = new Serie();
        series.definirCredenciais();
        series.ExtrairSeries();

        Filme filmes = new Filme();
        filmes.definirCredenciais();
        filmes.ExtrairFilmes();

    }
}
