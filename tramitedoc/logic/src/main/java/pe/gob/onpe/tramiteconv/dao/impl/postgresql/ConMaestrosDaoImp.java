/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.ConAnioBean;
import pe.gob.onpe.tramitedoc.bean.ConDestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.ConDiaBean;
import pe.gob.onpe.tramitedoc.bean.ConEstadoBean;
import pe.gob.onpe.tramitedoc.bean.ConExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ConMesBean;
import pe.gob.onpe.tramitedoc.bean.ConRemitenteBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoRemitenteBean;
import pe.gob.onpe.tramitedoc.dao.ConMaestrosDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author NGilt
 */
@Repository("conMaestrosDao")
public class ConMaestrosDaoImp extends SimpleJdbcDaoBase implements ConMaestrosDao {

    public List<ConAnioBean> listConAnio() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CANO_ANOEJE CO_ANIO, CANO_ANOEJE DE_ANIO ");
        sql.append("FROM IDOSGD.SI_MAE_ANO_EJECUCION ");
        sql.append("ORDER BY 2 DESC ");
        List<ConAnioBean> list = new ArrayList<ConAnioBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConAnioBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConMesBean> listConMes() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT upper(CELE_DESELE) DE_MES, CELE_CODELE CO_MES FROM IDOSGD.SI_ELEMENTO ");
        sql.append("WHERE CTAB_CODTAB=idosgd.pk_sgd_descripcion_fu_ti_meses() ");
        sql.append("AND CELE_CODELE != '00' ");
        sql.append("union select '.:TODOS:.', null ORDER BY 2");
        List<ConMesBean> list = new ArrayList<ConMesBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConMesBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConDiaBean> listConDia() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESELE DE_DIA, CELE_CODELE CO_DIA FROM IDOSGD.SI_ELEMENTO ");
        sql.append("WHERE CTAB_CODTAB=idosgd.pk_sgd_descripcion_fu_ti_dias()"); 
        sql.append("union select '.:TODOS:.', null  ORDER BY 2 ");

        List<ConDiaBean> list = new ArrayList<ConDiaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConDiaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConDestinatarioBean> listConDestinatarios(String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_DEPENDENCIA, CO_DEPENDENCIA from IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("WHERE  ? IN (CO_DEPENDENCIA,CO_DEPEN_PADRE) OR CO_DEPENDENCIA IN (select co_dep_ref from IDOSGD.tdtx_referencia ");
        sql.append("where co_dep_emi = ? and ti_emi = 'D' and es_ref = '1') ORDER BY 1 ");

        List<ConDestinatarioBean> list = new ArrayList<ConDestinatarioBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConDestinatarioBean.class), new Object[]{codDependencia, codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConTipoRemitenteBean> listConTipoRemitente() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESELE DE_TIPO_REMITENTE,CELE_CODELE CO_TIPO_REMITENTE  FROM IDOSGD.SI_ELEMENTO ");
        sql.append("WHERE CTAB_CODTAB='TIP_DESTINO' AND CELE_CODELE NOT IN ('05') ");
        sql.append("UNION SELECT '.:TODOS:.' CELE_DESELE,NULL CELE_CODELE ORDER BY 1 ");

        List<ConTipoRemitenteBean> list = new ArrayList<ConTipoRemitenteBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConTipoRemitenteBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConExpedienteBean> listConExpediente(String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT de_exp,co_exp FROM IDOSGD.tdtr_expediente  WHERE ");
        sql.append("co_dep = ? ");
        sql.append("UNION SELECT '.: TODOS :.', NULL  ");
        sql.append("UNION SELECT '.:SIN EXPEDIENTE:.', 'SINEX' ORDER BY 1 ");

        List<ConExpedienteBean> list = new ArrayList<ConExpedienteBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConExpedienteBean.class), new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConRemitenteBean> listConRemitente(String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_DEPENDENCIA, CO_DEPENDENCIA FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("WHERE CO_DEPENDENCIA IN (SELECT CO_DEP FROM IDOSGD.TDTX_RESUMEN_DEP WHERE CO_DEP_REF = ? AND TI_TAB = 'D' AND TI_EMI = '01') ");
        sql.append("union select '.:TODOS:.', null  ORDER BY 1 ");

        List<ConRemitenteBean> list = new ArrayList<ConRemitenteBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConRemitenteBean.class), new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConEstadoBean> listConEstado() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_EST,CO_EST FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB='TDTV_DESTINOS' UNION SELECT '.: TODOS :.',NULL  ORDER BY CO_EST  ");

        List<ConEstadoBean> list = new ArrayList<ConEstadoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConEstadoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ConTipoDocumentoBean> listConTipoDocumento(String codDependencia) {
        StringBuffer sql = new StringBuffer();         
        sql.append("SELECT DE_TIP_DOC,CO_TIP_DOC FROM "
                + "( \n" +
                    "SELECT DISTINCT\n" +
                    "A.CO_DEP CO_DEP_DES ,CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
                    "from IDOSGD.tdtx_resumen_doc A , IDOSGD.SI_MAE_TIPO_DOC B\n" +
                    "WHERE ti_tab = 'D'\n" +
                    "AND A.CO_DOC = B.CDOC_TIPDOC) tb ");
        sql.append("WHERE CO_DEP_DES =?  UNION SELECT '.:TODOS:.' DESCRI , ");
        sql.append("NULL CODIGO  ORDER BY 1 ");
        
        List<ConTipoDocumentoBean> list = new ArrayList<ConTipoDocumentoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ConTipoDocumentoBean.class), new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
}
