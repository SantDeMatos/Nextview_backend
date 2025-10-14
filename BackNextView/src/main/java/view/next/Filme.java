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


    public void ExtrairFilmes() throws IOException, InvalidFormatException {


        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/nextview");
        basicDataSource.setUsername("nextview");
        basicDataSource.setPassword("Sptech#2024");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);

        File arquivo = new File("conteudos.xlsx");
        Workbook workbook = new XSSFWorkbook(arquivo);


        Sheet sheet = workbook.getSheetAt(0);
        Integer numlinhas = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numlinhas; i++) {

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            Row row = sheet.getRow(i);

            // Acessando a primeira célula da linha - Define as coluna a serem lidas

            String titulo = "";
            String diretor = "";
            String atores = "";
            LocalDate dtLancamento= null;
            String generos = "";
             Double notaConteudo = 0.0;
             String sinopse = "";
             Integer numVotos = 0;


            for (int j = 0; j < 16; j++) {


                Cell cell = row.getCell(j);

                if (j == 2) {

                    titulo = cell.getStringCellValue();

                } else if (j == 3) {

                    diretor = cell.getStringCellValue();

                } else if (j == 4) {

                    atores = cell.getStringCellValue();

                } else if (j == 6) {

                    if(cell != null && cell.getLocalDateTimeCellValue() != null){

                        dtLancamento = cell.getLocalDateTimeCellValue().toLocalDate();

                    }else{

                        dtLancamento = LocalDate.of(1000,2,10);
                    }


                } else if (j == 8) {

                    if(cell != null){

                        notaConteudo = cell.getNumericCellValue();

                    }else{

                        notaConteudo = 0.0;
                    }


                }else if(j == 10){

                    generos = cell.getStringCellValue();

                }else if(j == 12){

                    sinopse = cell.getStringCellValue();

                }else if(j == 13){


                    if(cell != null){

                        numVotos = (int) cell.getNumericCellValue();

                    }else{

                        numVotos = 0;
                    }


                }



            }




            System.out.println("Realizando a conexão com o banco de dados");



            sinopse = sinopse.replaceAll("'","");
            atores = atores.replaceAll("'", "");
            titulo = titulo.replaceAll("'","");
            diretor = diretor.replaceAll("'","");
            atores = atores.substring(0, Math.min(atores.length(), 255));
            diretor = diretor.substring(0, Math.min(diretor.length(), 255));
            sinopse = sinopse.substring(0, Math.min(sinopse.length(), 255));

            String comando = """
               INSERT INTO Conteudo VALUES (DEFAULT,'Movie', '%s', '%s', '%s', '%s','%s', %.0f, '%s', %d);
                """.formatted(titulo,diretor,atores,dtLancamento.toString(),generos, notaConteudo,sinopse, numVotos );

            System.out.println(comando);

            jdbcTemplate.execute(comando);

        }


        workbook.close();

    }


    }




