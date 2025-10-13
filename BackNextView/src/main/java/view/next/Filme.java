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

public class Filme {

    private List<String> tituloConteudo;
    private List<String> diretorConteudo;
    private List<String> atoresConteudo;
    private List<LocalDate> dtLancamentoCont;
    private List<String> generosConteudo;
    private List<Float> notaConteudo;
    private List<String> sinopseCont;
    private List<Integer> numVotosCont;


    public void ExtrairFilmes() throws IOException, InvalidFormatException {

        File arquivo = new File("conteudos.xlsx");
        Workbook workbook = new XSSFWorkbook(arquivo);

        Sheet sheet = workbook.getSheetAt(0);
        Integer numlinhas = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas

            String nome = "";
            String artista = "";
            String album = "";
            LocalDate dataLancamento = null;


            for (int j = 0; j < 11; j++) {


                Cell cell = row.getCell(j);

                if (j == 0) {

                    id = (int) cell.getNumericCellValue();

                } else if (j == 1) {

                    nome = cell.getStringCellValue();

                } else if (j == 2) {

                    artista = cell.getStringCellValue();

                } else if (j == 5) {

                    album = cell.getStringCellValue();
                } else if (j == 10) {

                    dataLancamento = LocalDate.from(cell.getLocalDateTimeCellValue());
                }


            }

            Musica musica = new Musica();

            musica.setId(id);
            musica.setNome(nome);
            musica.setAlbum(album);
            musica.setArtista(artista);
            musica.setDataLancamento(dataLancamento);

            listamusica.add(musica);

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

    public List<Float> getNotaConteudo() {
        return notaConteudo;
    }

    public List<String> getSinopseCont() {
        return sinopseCont;
    }

    public List<Integer> getNumVotosCont() {
        return numVotosCont;
    }

    @Override
    public String toString() {
        return "Filme{" +
                "tituloConteudo=" + tituloConteudo +
                ", diretorConteudo=" + diretorConteudo +
                ", atoresConteudo=" + atoresConteudo +
                ", dtLancamentoCont=" + dtLancamentoCont +
                ", generosConteudo=" + generosConteudo +
                ", notaConteudo=" + notaConteudo +
                ", sinopseCont=" + sinopseCont +
                ", numVotosCont=" + numVotosCont +
                '}';
    }

    }




