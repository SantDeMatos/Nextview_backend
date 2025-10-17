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

public class Filme {

    private String getDataHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

    public void ExtrairFilmes() {

        System.out.println(getDataHora() + "📄Iniciando extração de filmes...");

        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(System.getenv("BD_URL"));
            basicDataSource.setUsername(System.getenv("BD_USERNAME"));
            basicDataSource.setPassword(System.getenv("BD_PASSWORD"));

            JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
            System.out.println(getDataHora() + "🔗Conexão com o banco de dados estabelecida");

            File arquivo = new File("conteudos.xlsx");
            Workbook workbook = new XSSFWorkbook(arquivo);


            Sheet sheet = workbook.getSheetAt(0);
            Integer numlinhas = sheet.getPhysicalNumberOfRows();


            System.out.println(getDataHora() + "📄 Planilha carregada com " + numlinhas + " linhas.");

            for (int i = 1; i < numlinhas; i++) {

                // Acessando a primeira linha da planilha - Define qual linha da coluna será lida
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Acessando a primeira célula da linha - Define as coluna a serem lidas
                String titulo = "";
                String diretor = "";
                String atores = "";
                LocalDate dtLancamento = null;
                String generos = "";
                String notaResp = "";
                String sinopse = "";
                Integer numVotos = 0;

                try {
                    for (int j = 0; j < 16; j++) {
                        Cell cell = row.getCell(j);

                        // simplifiquei
                        if (j == 2) titulo = (cell != null) ? cell.getStringCellValue() : "";
                        else if (j == 3) diretor = (cell != null) ? cell.getStringCellValue() : "";
                        else if (j == 4) atores = (cell != null) ? cell.getStringCellValue() : "";
                        else if (j == 6) dtLancamento = (cell != null && DateUtil.isCellDateFormatted(cell))
                                ? cell.getLocalDateTimeCellValue().toLocalDate()
                                : LocalDate.of(1000, 2, 10);
                        else if (j == 8) notaResp = (cell != null)
                                ? String.format("%.1f", cell.getNumericCellValue())
                                : "0";
                        else if (j == 10) generos = (cell != null) ? cell.getStringCellValue() : "";
                        else if (j == 12) sinopse = (cell != null) ? cell.getStringCellValue() : "";
                        else if (j == 13) numVotos = (cell != null) ? (int) cell.getNumericCellValue() : 0;
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
                        VALUES (DEFAULT, 'Movie', '%s', '%s', '%s', '%s', '%s', %s, '%s', %d);
                    """.formatted(titulo, diretor, atores, dtLancamento.toString(), generos, notaResp, sinopse, numVotos);

                    jdbcTemplate.execute(comando);
                    System.out.println(getDataHora() + "✅Inserido com sucesso: " + titulo);

                } catch (Exception eLinha) {
                    System.out.println(getDataHora() + "❌Erro ao processar linha " + i + ": " + eLinha.getMessage());
                }
            }

            workbook.close();
            System.out.println(getDataHora() + " 🏁 Extração de filmes finalizada com sucesso.");

        } catch (IOException e) {
            System.out.println(getDataHora() + "❌Erro ao ler o arquivo Excel: " + e.getMessage());
        } catch (InvalidFormatException e) {
            System.out.println(getDataHora() + "❌Formato inválido no arquivo Excel: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(getDataHora() + "❌Erro inesperado: " + e.getMessage());
        }
    }
}
