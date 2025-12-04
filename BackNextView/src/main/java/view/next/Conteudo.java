package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class Conteudo {

    private TipoConteudo tipoConteudo;

    public Conteudo(TipoConteudo tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public abstract String getDataHora();

    public TipoConteudo getTipoConteudo() {
        return tipoConteudo;
    }

    @Override
    public String toString() {
        return "Conteudo{" +
                "tipoConteudo=" + tipoConteudo +
                '}';
    }
}
