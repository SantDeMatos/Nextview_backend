package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Serie {

    private String getDataHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

    Log log = new Log();

    public void ExtrairSeries() {

        System.out.println(getDataHora() + "📄Iniciando extração de séries...");
        log.registrar("INFO", "📄Iniciando extração de séries...");

        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(System.getenv("BD_URL"));
            basicDataSource.setUsername(System.getenv("BD_USERNAME"));
            basicDataSource.setPassword(System.getenv("BD_PASSWORD"));

            JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
            System.out.println(getDataHora() + "🔗Conexão com o banco de dados estabelecida.");
            log.registrar("INFO", "🔗Conexão com o banco de dados estabelecida.");

            File arquivo = new File("conteudos.xlsx");
            Workbook workbook = new XSSFWorkbook(arquivo);
            Sheet sheet = workbook.getSheetAt(0);
            int numlinhas = sheet.getPhysicalNumberOfRows();
            System.out.println(getDataHora() + "📄Planilha carregada com " + numlinhas + " linhas.");
            log.registrar("INFO", "📄Planilha carregada com " + numlinhas + " linhas.");

            for (int i = 1; i < numlinhas; i++) {

                // Acessando a primeira linha da planilha - Define qual linha da coluna será lida

                Row row = sheet.getRow(i);
                // Acessando a primeira célula da linha - Define as coluna a serem lidas

                if (row == null) continue;


                String titulo = "";
                String diretor = "";
                String atores = "";
                LocalDate dtLancamento = null;
                String generos = "";
                String notaResp = "";
                Double notaConteudo = 0.0;
                String sinopse = "";
                Integer numVotos = 0;

                try {
                    for (int j = 0; j < 16; j++) {
                        Cell cell = row.getCell(j);

                        if (j == 2) {

                            titulo = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){

                                titulo = cell.getStringCellValue();

                            }else{

                                titulo = "";
                            }


                        } else if (j == 3) {

                            diretor = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){

                                diretor = cell.getStringCellValue();

                            }else{

                                diretor = "";
                            }


                        } else if (j == 4) {

                            atores = cell.getStringCellValue();

                            if(cell != null && cell.getStringCellValue() != null){

                                atores = cell.getStringCellValue();

                            }else{

                                atores = "";
                            }

                        } else if (j == 6) {

                            if(cell != null && cell.getLocalDateTimeCellValue() != null){

                                dtLancamento = cell.getLocalDateTimeCellValue().toLocalDate();

                            }else{

                                dtLancamento = LocalDate.of(1000,2,10);
                            }


                        } else if (j == 8) {

                            if(cell != null){

                                notaConteudo = cell.getNumericCellValue();
                                Integer contDigitos = (int) cell.getNumericCellValue();

                                String notaTexto =  contDigitos.toString();

                                Double div = Math.pow(10, notaTexto.length() - 1);

                                notaConteudo = contDigitos / div;

                                notaResp = notaConteudo.toString();


                            }else{

                                notaConteudo = 0.0;
                                notaResp = "0";
                            }


                        }else if(j == 10){

                            generos = cell.getStringCellValue();

                            if(cell != null && cell.getStringCellValue() != null){

                                generos = cell.getStringCellValue();


                            }else{

                                generos = "";
                            }


                        }else if(j == 12){

                            sinopse = cell.getStringCellValue();

                            if(cell != null && cell.getStringCellValue() != null){

                                sinopse = cell.getStringCellValue();


                            }else{

                                sinopse = "";
                            }

                        }else if(j == 13){


                            if(cell != null){

                                numVotos = (int) cell.getNumericCellValue();

                            }else{

                                numVotos = 0;
                            }


                        }
                    }

                    sinopse = (sinopse == null) ? "" : sinopse.replaceAll("'", "");
                    atores = (atores == null) ? "" : atores.replaceAll("'", "");
                    titulo = (titulo == null) ? "" : titulo.replaceAll("'", "");
                    diretor = (diretor == null) ? "" : diretor.replaceAll("'", "");

                    atores = atores.substring(0, Math.min(atores.length(), 255));
                    diretor = diretor.substring(0, Math.min(diretor.length(), 255));
                    sinopse = sinopse.substring(0, Math.min(sinopse.length(), 255));

                    String comando = """
                        INSERT INTO Conteudo 
                        VALUES (DEFAULT, 'Tv Show', '%s', '%s', '%s', '%s', '%s', %s, '%s', %d);
                    """.formatted(titulo, diretor, atores, dtLancamento.toString(), generos, notaResp, sinopse, numVotos);

                    jdbcTemplate.execute(comando);
                    System.out.println(getDataHora() + " ✅ Inserido com sucesso: " + titulo);
                    log.registrar("INFO", " ✅ Inserido com sucesso: " + titulo);

                } catch (Exception eLinha) {
                    String mensagem = " ❌ Erro ao processar linha " + i + ": " + eLinha.getMessage();
                    System.out.println(getDataHora() + mensagem);
                    log.registrar("ERRO", mensagem);
                }
            }

            workbook.close();
            System.out.println(getDataHora() + " 🏁 Extração de séries finalizada com sucesso.");
            log.registrar("INFO", " 🏁 Extração de séries finalizada com sucesso.");

        } catch (IOException e) {
            String mensagem = " ❌ Erro ao ler o arquivo Excel: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        } catch (InvalidFormatException e) {
            String mensagem = " ❌ Formato inválido no arquivo Excel: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        } catch (Exception e) {
            String mensagem = " ❌ Erro inesperado: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        }
//        log.ExecutarLog();
    }
}
