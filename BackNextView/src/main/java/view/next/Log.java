package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.CSVOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Log {

    private String mensagem;
    private Integer qtdErro;
    private String tipoLog;

    private String getDataHora(){
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(dataFormatada);
    }

    private static final BasicDataSource basicDataSource = new BasicDataSource();
    static {
        basicDataSource.setUrl(System.getenv("BD_URL"));
        basicDataSource.setUsername(System.getenv("BD_USERNAME"));
        basicDataSource.setPassword(System.getenv("BD_PASSWORD"));
    }
    private static final JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);


    public void setTipoLog(String tipoLog) {
        this.tipoLog = tipoLog;
    }

    public void setQtdErro(Integer qtdErro) {
        this.qtdErro = qtdErro;
    }

    public String getTipoLog() {
        return tipoLog;
    }

    public String getMensagemLog(){return mensagem;}

    public void registrar(String tipoLog, String mensagem) {


        String msgLog = getDataHora() + " - " + tipoLog + ": " + mensagem;
        Integer qtdErro = 0;

        if(Objects.equals(tipoLog, "ERRO")) {
            qtdErro++;
            setQtdErro(qtdErro);
        }

        setTipoLog(tipoLog);
        System.out.println(msgLog);


        try {

            mensagem = (mensagem == null) ? "" : mensagem.replaceAll("'", "");

            String comando = """
                    INSERT INTO Log 
                    VALUES (default, '%s', '%s', %d, '%s');
                    """.formatted(getDataHora(), mensagem, qtdErro, tipoLog);

            jdbcTemplate.execute(comando);

        } catch(Exception e) {
            throw new IllegalStateException("Erro ao gerar Log."+ e);
        }

    }


}
