package view.next;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

        System.out.println("Realizando a conexão com o banco de dados");

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/testeapache");
        basicDataSource.setUsername("nextview");
        basicDataSource.setPassword("Sptech#2024");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);

        System.out.println("Criando as listas para adição futura no banco de dados");

        List<Integer> listaid = new ArrayList<>();
        List<String> listatitulo = new ArrayList<>();
        List<Date> listadata = new ArrayList<>();

        System.out.println("Definindo qual planilha será lida e seu formato");

        File arquivo = new File("MelhoresFilmes.xlsx");
        Workbook workbook = new XSSFWorkbook(arquivo);

        System.out.println("Iniciando o processo de leitura dos arquivos");

        System.out.println("Acessando a primeira planilha");

        System.out.println("Lendo a primeira coluna de Id");


        // Acessando a primeira planilha
        Sheet sheet = workbook.getSheetAt(0);

        Integer numlinhas = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas
            Cell cell = row.getCell(0);

            Integer valor = (int) cell.getNumericCellValue();

            listaid.add(valor);

            workbook.close();

        }

        System.out.println("Lendo a segunda coluna de Título");


        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas
            Cell cell = row.getCell(1);

            String valor = cell.getStringCellValue();

            listatitulo.add(valor);

            workbook.close();

        }

        System.out.println("Lendo a terceira coluna de Data de Lançamento");


        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas
            Cell cell = row.getCell(2);

            String valor = String.valueOf(cell.getNumericCellValue());

            listadata.add(valor);

            workbook.close();

        }

        System.out.println(listadata);


        jdbcTemplate.execute("""
    CREATE TABLE filme (
        id INT AUTO_INCREMENT PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        ano INT NOT NULL,
        genero VARCHAR(255) NOT NULL,
        diretor VARCHAR(255) NOT NULL
    )
    """);





    }
}
