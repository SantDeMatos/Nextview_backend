package view.next;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class Conteudo {

    public Conteudo() {
    }

    public abstract String getDataHora();

}
