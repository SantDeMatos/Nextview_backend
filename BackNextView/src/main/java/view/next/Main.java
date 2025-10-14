package view.next;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.springframework.jdbc.core.JdbcTemplate;


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



        Filme filmes = new Filme();
        filmes.ExtrairFilmes();

        Serie series = new Serie();
        series.ExtrairSeries();


    }
}
