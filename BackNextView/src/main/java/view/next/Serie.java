package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Serie {

    private List<String> tituloConteudo;
    private List<String> diretorConteudo;
    private List<String> atoresConteudo;
    private List<LocalDate> dtLancamentoCont;
    private List<String> generosConteudo;
    private List<Double> notaConteudo;
    private List<String> sinopseCont;
    private List<Integer> numVotosCont;


    public void ExtrairSerie() throws IOException, InvalidFormatException {

        File arquivo = new File("conteudos.xlsx");
        Workbook workbook = new XSSFWorkbook(arquivo);

        Sheet sheet = workbook.getSheetAt(1);
        Integer numlinhas = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas

            String nome = "";
            String artista = "";
            String album = "";
            LocalDate dataLancamento = null;



        }


        workbook.close();


        System.out.println("Realizando a conexão com o banco de dados");

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/nextview");
        basicDataSource.setUsername("nextview");
        basicDataSource.setPassword("Sptech#2024");

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

    public List<String> getTituloConteudo() {
        return tituloConteudo;
    }

    public List<String> getDiretorConteudo() {
        return diretorConteudo;
    }

    public List<String> getAtoresConteudo() {
        return atoresConteudo;
    }

    public List<LocalDate> getDtLancamentoCont() {
        return dtLancamentoCont;
    }

    public List<String> getGenerosConteudo() {
        return generosConteudo;
    }

    public List<Double> getNotaConteudo() {
        return notaConteudo;
    }

    public List<String> getSinopseCont() {
        return sinopseCont;
    }

    public List<Integer> getNumVotosCont() {
        return numVotosCont;
    }
}
