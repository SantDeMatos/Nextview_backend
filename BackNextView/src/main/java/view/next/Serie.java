package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.mysql.cj.jdbc.Driver;
import static java.sql.Date.valueOf;

public class Serie extends Conteudo {

    @Override
    public String getDataHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

    public static Log log = new Log();

    private static final BasicDataSource bs = new BasicDataSource();
    private static JdbcTemplate jdbcTemplate;

    static {
            bs.setDriverClassName("com.mysql.cj.jdbc.Driver");
            bs.setUrl(System.getenv("DB_URL"));
            bs.setUsername(System.getenv("DB_USERNAME"));
            bs.setPassword(System.getenv("DB_PASSWORD"));
//            log.registrar("INFO", "🔗Conexão com o banco de dados estabelecida.");
            jdbcTemplate = new JdbcTemplate(bs);
    }

    public void ExtrairSeries() {

        System.out.println(getDataHora() + "📄Iniciando extração de séries...");
        log.registrar("INFO", "📄Iniciando extração de séries...");

        String sql = " INSERT INTO Conteudo VALUES (DEFAULT, 'Tv Show', ?, ?, ?, ?, ?, ?, ?, ?); ";

        try(Connection conexao = bs.getConnection();
            PreparedStatement insercao = conexao.prepareStatement(sql)) {

            conexao.setAutoCommit(false); // Desativando o commit automático dos dados

            // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
            File arquivo = new File("conteudos.xlsx");
            Workbook workbook = new XSSFWorkbook(arquivo);
            Sheet sheet = workbook.getSheetAt(1);
            int numlinhas = sheet.getPhysicalNumberOfRows();
            System.out.println(getDataHora() + "📄Planilha carregada com " + numlinhas + " linhas.");
            log.registrar("INFO", "📄Planilha carregada com " + numlinhas + " linhas.");
            Integer contador = 1;

            for (int i = 1; i <= numlinhas; i++) {

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
                                if (cell != null && cell.getStringCellValue() != null) {
                                    titulo = cell.getStringCellValue();
                                } else {
                                    titulo = (titulo == null) ? "" : titulo.replaceAll("'", "");
                                }

                                insercao.setString(1, titulo);

                            } else if (j == 3) {
                                diretor = cell.getStringCellValue();
                                if (cell != null && cell.getStringCellValue() != null) {
                                    diretor = cell.getStringCellValue();
                                    diretor = diretor.substring(0, Math.min(diretor.length(), 255));
                                } else {
                                    diretor = (diretor == null) ? "" : diretor.replaceAll("'", "");
                                }

                                insercao.setString(2, diretor);

                            } else if (j == 4) {
                                atores = cell.getStringCellValue();
                                if (cell != null && cell.getStringCellValue() != null) {
                                    atores = cell.getStringCellValue();
                                    atores = atores.substring(0, Math.min(atores.length(), 255));
                                } else {
//                                atores = "";
                                    atores = (atores == null) ? "" : atores.replaceAll("'", "");
                                }

                                insercao.setString(3, atores);

                            } else if (j == 7) {

                                if (cell != null && cell.getLocalDateTimeCellValue() != null) {
                                    dtLancamento = cell.getLocalDateTimeCellValue().toLocalDate();
                                } else {
                                    dtLancamento = LocalDate.of(1000, 2, 10);
                                }

                                insercao.setDate(4, Date.valueOf(dtLancamento));

                            } else if (j == 8) {
                                if (cell != null) {

                                    notaConteudo = cell.getNumericCellValue();
                                    Integer contDigitos = (int) cell.getNumericCellValue();

                                    String notaTexto = contDigitos.toString();

                                    Double div = Math.pow(10, notaTexto.length() - 1);

                                    notaConteudo = contDigitos / div;

                                    notaResp = notaConteudo.toString();

                                } else {
                                    notaConteudo = 0.0;
                                    notaResp = "0";
                                }

                                insercao.setString(6, notaResp);

                            } else if (j == 10) {
                                generos = cell.getStringCellValue();
                                if (cell != null && cell.getStringCellValue() != null) {
                                    generos = cell.getStringCellValue();
                                } else {
                                    generos = "";
                                }

                                insercao.setString(5, generos);

                            } else if (j == 11) {
                                sinopse = cell.getStringCellValue();
                                if (cell != null && cell.getStringCellValue() != null) {
                                    sinopse = cell.getStringCellValue();
                                    sinopse = sinopse.substring(0, Math.min(sinopse.length(), 255));
                                } else {
                                    sinopse = "";
                                    sinopse = (sinopse == null) ? "" : sinopse.replaceAll("'", "");
                                }

                                insercao.setString(7, sinopse);

                            } else if (j == 13) {

                                if (cell != null) {
                                    numVotos = (int) cell.getNumericCellValue();
                                } else {
                                    numVotos = 0;
                                }
                                insercao.setInt(8, numVotos);
                            }
                        }

                        insercao.addBatch();

                            if(i == 2000) {
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 2000 && i == 4000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 4000 && i == 6000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 6000 && i == 8000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 8000 && i == 10000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 10000 && i == 12000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 12000 && i == 14000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                            if(i > 14000 && i == 16000) {
                                contador += 2000;
                                insercao.executeBatch();
                                conexao.commit();
                                System.out.println(getDataHora() + " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                                log.registrar("INFO", " Linhas " + contador + " a " + i + " extraídas com sucesso.");
                            }

                    } catch (Exception eLinha) {
                        String mensagem = " ❌ Erro ao processar linha " + i + ": " + eLinha.getMessage();
                        System.out.println(getDataHora() + mensagem);
                        log.registrar("ERRO", mensagem);
                        eLinha.printStackTrace();
                }
            }

            insercao.executeBatch();
            conexao.commit();

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
    }
}
