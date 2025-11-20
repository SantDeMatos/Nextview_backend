package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.mysql.cj.jdbc.Driver;

public class Filme extends Conteudo {

    @Override
    public String getDataHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

    public static Log log = new Log();
    private static final BasicDataSource basicDataSource = new BasicDataSource();
    private static JdbcTemplate jdbcTemplate;

    static {
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUrl(System.getenv("DB_URL"));
        basicDataSource.setUsername(System.getenv("DB_USERNAME"));
        basicDataSource.setPassword(System.getenv("DB_PASSWORD"));
        jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

    public void ExtrairFilmes() {

        System.out.println(getDataHora() + "📄Iniciando extração de filmes...");
        log.registrar("INFO", "📄Iniciando extração de filmes...");

        String sql = " INSERT INTO Conteudo VALUES (DEFAULT, 'Movie', ?, ?, ?, ?, ?, ?, ?, ?); ";

        try(Connection conexao = basicDataSource.getConnection();
            PreparedStatement insercao = conexao.prepareStatement(sql)) {
            File arquivo = new File("conteudos.xlsx");
            Workbook workbook = new XSSFWorkbook(arquivo);

            Sheet sheet = workbook.getSheetAt(0);
            Integer numlinhas = sheet.getPhysicalNumberOfRows();
            conexao.setAutoCommit(false);

            System.out.println(getDataHora() + "📄 Planilha carregada com " + numlinhas + " linhas.");
            log.registrar("INFO", "📄 Planilha carregada com " + numlinhas + " linhas.");

            for (int i = 1; i <= numlinhas; i++) {

                // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Acessando a primeira célula da linha - Define as coluna a serem lidas
                String titulo = "";
                String diretor = "";
                String atores = "";
                Integer dtLancamento = null;
                String generos = "";
                Double notaConteudo = 0.0;
                String notaResp = "";
                String sinopse = "";
                Integer numVotos = 0;

                try {

                    for (int j = 0; j < 16; j++) {

                        Cell cell = row.getCell(j);

                        if (j == 2) {
                            titulo = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){

                                titulo = cell.getStringCellValue();

                            } else {
                                titulo = (titulo == null) ? "" : titulo.replaceAll("'", "");
                            }

                            insercao.setString(1, titulo);

                        } else if (j == 3) {
                            diretor = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){
                                diretor = cell.getStringCellValue();
                                diretor = diretor.substring(0, Math.min(diretor.length(), 255));
                            } else {
//                                diretor = "";
                            diretor = (diretor == null) ? "" : diretor.replaceAll("'", "");
                            }

                            insercao.setString(2, diretor);

                        } else if (j == 4) {

                            atores = cell.getStringCellValue();

                            if(cell != null && cell.getStringCellValue() != null){

                                atores = cell.getStringCellValue();
                                atores = atores.substring(0, Math.min(atores.length(), 255));

                            } else {
//                                atores = "";
                                atores = (atores == null) ? "" : atores.replaceAll("'", "");
                            }

                            insercao.setString(3, atores);

                        } else if (j == 7) {

                            if(cell != null) {

                                dtLancamento = (int) cell.getNumericCellValue();

                                if(dtLancamento.toString().length() > 4){

                                    dtLancamento = 0000;

                                }

                            }else{

                                dtLancamento = 0000;
                            }
                            String dataNova = dtLancamento+"-01-01";


                            insercao.setDate(4, Date.valueOf(dataNova));

                        } else if (j == 8) {

                            if(cell != null){
                                notaConteudo = cell.getNumericCellValue();
                                Integer contDigitos = (int) cell.getNumericCellValue();
                                String notaTexto =  contDigitos.toString();
                                Double div = Math.pow(10, notaTexto.length() - 1);
                                notaConteudo = contDigitos / div;
                                notaResp = notaConteudo.toString();
                            } else {
                                notaConteudo = 0.0;
                                notaResp = "0";
                            }

                            insercao.setString(6, notaResp);

                        } else if(j == 10){
                            generos = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){
                                generos = cell.getStringCellValue();
                            } else {
                                generos = "";
                            }

                            insercao.setString(5, generos);

                        } else if(j == 11){
                            sinopse = cell.getStringCellValue();
                            if(cell != null && cell.getStringCellValue() != null){
                                sinopse = cell.getStringCellValue();
                                sinopse = sinopse.substring(0, Math.min(sinopse.length(), 255));
                            } else {
                                sinopse = (sinopse == null) ? "" : sinopse.replaceAll("'", "");
                            }

                            insercao.setString(7, sinopse);

                        } else if(j == 14){
                            if(cell != null){
                                numVotos = (int) cell.getNumericCellValue();
                            } else {
                                numVotos = 0;
                            }

                            insercao.setInt(8, numVotos);
                            insercao.addBatch();

                        }
                    }

                    if(i % 2000 == 0) {
                        insercao.executeBatch();
                        conexao.commit();
                        System.out.println(getDataHora() + " ✅ Inserido com sucesso!");
                        log.registrar("INFO", " ✅ Inserido com sucesso!");
                    }

                } catch (Exception eLinha) {
                    String mensagem = " ❌ Erro ao processar linha " + i + ": " + eLinha.getMessage();
                    System.out.println(getDataHora() + mensagem);
                    log.registrar("ERRO", mensagem);
                }
            }

            insercao.executeBatch();
            conexao.commit();

            workbook.close();
            System.out.println(getDataHora() + " 🏁 Extração de filmes finalizada com sucesso.");
            log.registrar("INFO", " 🏁 Extração de filmes finalizada com sucesso.");

        } catch (IOException e) {
            String mensagem = "❌Erro ao ler o arquivo Excel: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        } catch (InvalidFormatException e) {
            String mensagem = "❌Formato inválido no arquivo Excel: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        } catch (Exception e) {
            String mensagem = "❌Erro inesperado: " + e.getMessage();
            System.out.println(getDataHora() + mensagem);
            log.registrar("ERRO", mensagem);
        }
    }

}
