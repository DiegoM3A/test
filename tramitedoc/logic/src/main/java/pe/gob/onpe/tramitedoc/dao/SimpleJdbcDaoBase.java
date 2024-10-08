package pe.gob.onpe.tramitedoc.dao;

import javax.sql.DataSource;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;


@Repository
public class SimpleJdbcDaoBase {
    //Log4j
    //protected final Log logger = LogFactory.getLog(getClass());

//    protected SimpleJdbcTemplate simpleJdbcTemplate;
    protected JdbcTemplate jdbcTemplate;
    protected DataSource dataSource;
    protected SimpleJdbcCall simpleJdbcCall;
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /*
     * Metodo que sirve para inyectar el datasource y configurar un simpleJdbcTemplate.
     * @param de nombre = dataSource es de tipo = DataSource
     * @Nota: El nombre "init" puede ser cualquier otro nombre lo que manda es el autowired
     * @return nada
     */

    @Autowired
    public void init(@Qualifier("dataSource")DataSource dataSource){
        this.dataSource = dataSource;
        //this.simpleJdbcTemplate = new SimpleJdbcTemplate(this.dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }
}
