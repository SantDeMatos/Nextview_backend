package view.next;

import java.io.File;
import java.io.IOException;

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

        File arquivo = new File("MelhoresFilmes.xlsx");
        Workbook workbook = new XSSFWorkbook(arquivo);

        System.out.println("Iniciando o processo de leitura dos arquivos");

        // Acessando a primeira planilha
        Sheet sheet = workbook.getSheetAt(0);

        // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
        Row row = sheet.getRow(2);

        // Acessando a primeira célula da linha - Define as coluna a serem lidas
        Cell cell = row.getCell(1);

        String valor = cell.getStringCellValue();

        System.out.println("Linha printada: "+valor);

        workbook.close();

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:filmes");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);

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
