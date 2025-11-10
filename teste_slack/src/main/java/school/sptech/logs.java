package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class logs {

    private String mensagem;
    private Integer qtdErro;
    private String tipoLog;
    private String alerta;

    public String getDataHora(){
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(dataFormatada);
    }

    // Constructor

    public logs(String tipoLog, String mensagem, Integer qtdErro) {
        this.tipoLog = tipoLog;
        this.mensagem = mensagem;
        this.qtdErro = qtdErro;
        this.mensagem = mensagem;
    }

    // getters

    public String getMensagem() {
        return mensagem;
    }

    public Integer getQtdErro() {
        return qtdErro;
    }

    public String getTipoLog() {
        return tipoLog;
    }

    public String getAlerta() {
        return alerta;
    }

    }




