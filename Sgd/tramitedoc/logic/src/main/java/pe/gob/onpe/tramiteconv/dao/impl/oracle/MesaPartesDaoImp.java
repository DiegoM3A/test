/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProcesoExpBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.MesaPartesDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;

/**
 *
 * @author ECueva
 */
@Repository("mesaPartesDao")
public class MesaPartesDaoImp extends SimpleJdbcDaoBase  implements MesaPartesDao{
    
    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public List<DocumentoExtRecepBean> getDocumentosExtRecep(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean) {
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();        
        sql.append("SELECT X.*,");
        sql.append("PK_SGD_DESCRIPCION.TI_DESTINO (X.TI_EMI) DE_TI_EMI,");
//        sql.append(" DECODE");
//        sql.append("    (X.TI_EMI,");
//        sql.append(" 	'01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA (X.CO_DEP_EMI)");
//        sql.append(" 	 || ' - '");
//        sql.append(" 	 || PK_SGD_DESCRIPCION.DE_NOM_EMP (X.CO_EMP_EMI),");
//        sql.append(" 	'02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (X.NU_RUC_EMI),");
//        sql.append(" 	'03', PK_SGD_DESCRIPCION.ANI_SIMIL (X.NU_DNI_EMI),");
//        sql.append(" 	'04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (X.CO_OTR_ORI_EMI),");
//        sql.append(" 	'05', PK_SGD_DESCRIPCION.DE_NOM_EMP (X.CO_EMP_EMI)");
//        sql.append("    ) DE_ORI_EMI_MP,PK_SGD_DESCRIPCION.DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append("PK_SGD_DESCRIPCION.TI_EMI_EMP(X.NU_ANN, X.NU_EMI) DE_ORI_EMI_MP,");
        sql.append("PK_SGD_DESCRIPCION.DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" DECODE (X.TI_EMI,");
        sql.append(" 		'01', X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG,");
        sql.append(" 		'05', X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG,");
        sql.append(" 		X.DE_DOC_SIG");
        sql.append(" 	   ) NU_DOC,");
        sql.append(" DECODE (X.NU_CANDES,");
        sql.append(" 		1, PK_SGD_DESCRIPCION.TI_DES_EMP (X.NU_ANN, X.NU_EMI),");
        sql.append(" 		PK_SGD_DESCRIPCION.TI_DES_EMP_V (X.NU_ANN, X.NU_EMI)");
        sql.append(" 	   ) DE_EMP_DES,"); 
        sql.append(" PK_SGD_DESCRIPCION.ESTADOS_MP(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI_MP,");        
        sql.append(" PK_SGD_DESCRIPCION.DE_LOCAL(X.CO_LOC_EMI) DE_LOC_EMI,");
        sql.append(" PK_SGD_DESCRIPCION.DE_DEPENDENCIA(X.CO_DEP) DE_DEPENDENCIA,");
        
        sql.append(" ROWNUM");
        sql.append(" FROM ( ");
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_EXPEDIENTE,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA, TO_CHAR(C.FE_EXP,'DD/MM/YYYY') FE_EXP, PK_SGD_DESCRIPCION.DE_DOMINIOS('TIP_EXPEDIENTE',C.CCOD_TIPO_EXP) CO_TIPO_EXP,");
        sql.append(" A.DE_ASU,A.CO_DEP_EMI,A.CO_EMP_EMI,A.CO_OTR_ORI_EMI,A.CO_TIP_DOC_ADM,A.NU_DOC_EMI,A.DE_DOC_SIG,");
        sql.append(" B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.TI_EMI,A.NU_RUC_EMI,A.NU_DNI_EMI,A.NU_CANDES,A.ES_DOC_EMI,A.CO_LOC_EMI,");
        sql.append(" A.CO_DEP,A.CCOD_ORIGING");
        sql.append(" FROM TDTV_REMITOS A, TDTX_REMITOS_RESUMEN B, TDTC_EXPEDIENTE C");
        sql.append(" WHERE");
        sql.append(" B.NU_ANN=A.NU_ANN");
        sql.append(" AND B.NU_EMI=A.NU_EMI");
        sql.append(" AND C.NU_ANN_EXP=A.NU_ANN_EXP AND C.NU_SEC_EXP=A.NU_SEC_EXP");     
        String pNuAnn = buscarDocumentoExtRecepBean.getCoAnnio();
        String pEsFiltroFecha = buscarDocumentoExtRecepBean.getEsFiltroFecha();
        
//        if (!(pEsFiltroFecha.equals("1")&&pNuAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNuAnn);
//        }       
        sql.append(" AND A.CO_GRU = :pCoGru");        
        objectParam.put("pCoGru", buscarDocumentoExtRecepBean.getCoGrupo());
//        sql.append(" AND A.ES_DOC_EMI<>9");
        sql.append(" AND A.TI_EMI<>'01'");
        sql.append(" AND A.ES_ELI='0'");
        sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");        
        objectParam.put("pCoDepEmi", buscarDocumentoExtRecepBean.getCoDepEmi());

        String pTipoBusqueda = buscarDocumentoExtRecepBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoExtRecepBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        
        String auxTipoAcceso=buscarDocumentoExtRecepBean.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        if(tiAcceso.equals("1")){//acceso personal
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocumentoExtRecepBean.getCoEmpleado());            
        }else {//acceso total
            if(buscarDocumentoExtRecepBean.getInMesaPartes().equals("0") /*&& buscarDocumentoExtRecepBean.getInCambioEst().equals("0")*/){
                bBusqDep = true;
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocumentoExtRecepBean.getCoDependencia());                   
            }
        }/*else if(tiAcceso.equals("0")){//acceso total
            if(!buscarDocumentoExtRecepBean.getInCambioEst().equals("1")){
                bBusqLocal = true;
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi");
                objectParam.put("pcoLocEmi", buscarDocumentoExtRecepBean.getCoLocal());
            }
        }*/
        
        //Filtro
        //if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoExtRecepBean.getCoTipoDoc()!= null && buscarDocumentoExtRecepBean.getCoTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoExtRecepBean.getCoTipoDoc());
            }            
            if (buscarDocumentoExtRecepBean.getCoEstadoDoc()!= null && buscarDocumentoExtRecepBean.getCoEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pCoEsDocEmi ");
                objectParam.put("pCoEsDocEmi", buscarDocumentoExtRecepBean.getCoEstadoDoc());
            }
            if(buscarDocumentoExtRecepBean.getCoTipoEmisor()!= null && buscarDocumentoExtRecepBean.getCoTipoEmisor().trim().length() > 0){
                sql.append(" AND A.TI_EMI = :pCoTipoEmisor ");
                objectParam.put("pCoTipoEmisor", buscarDocumentoExtRecepBean.getCoTipoEmisor());                
            }
            /*if (buscarDocumentoExtRecepBean.getCoLocEmi()!= null && buscarDocumentoExtRecepBean.getCoLocEmi().trim().length() > 0 && !bBusqLocal) {
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi ");
                objectParam.put("pcoLocEmi", buscarDocumentoExtRecepBean.getCoLocEmi());
            } */     
            if (buscarDocumentoExtRecepBean.getCoDepOriRec()!= null && buscarDocumentoExtRecepBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocumentoExtRecepBean.getCoDepOriRec());                
            }
            
            if(buscarDocumentoExtRecepBean.getCoProceso()!=null&&buscarDocumentoExtRecepBean.getCoProceso().trim().length()>0){
                if(buscarDocumentoExtRecepBean.getCoProceso().equals("CON_TUPA")){
                    sql.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                }else{
                    sql.append(" AND B.CO_PROCESO_EXP = :pcoProceso ");
                    objectParam.put("pcoProceso", buscarDocumentoExtRecepBean.getCoProceso()); 
                }
            }
            
            if (buscarDocumentoExtRecepBean.getEsFiltroFecha() != null
                    && (pEsFiltroFecha.equals("1") || pEsFiltroFecha.equals("3") || pEsFiltroFecha.equals("2"))) {
                String vFeEmiIni = buscarDocumentoExtRecepBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoExtRecepBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }            
        //}   
        
        //Busqueda
        //if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoExtRecepBean.getBusNumDoc() != null && buscarDocumentoExtRecepBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoExtRecepBean.getBusNumDoc());
            }
            if (buscarDocumentoExtRecepBean.getBusNumExpediente() != null && buscarDocumentoExtRecepBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoExtRecepBean.getBusNumExpediente());
            }
            if (buscarDocumentoExtRecepBean.getBusAsunto() != null && buscarDocumentoExtRecepBean.getBusAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                objectParam.put("pDeAsunto", buscarDocumentoExtRecepBean.getBusAsunto().trim());
            }
            
            if (buscarDocumentoExtRecepBean.getCoTipoExp() != null && !buscarDocumentoExtRecepBean.getCoTipoExp().equals("")) {
                sql.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                objectParam.put("codTipoExp", buscarDocumentoExtRecepBean.getCoTipoExp()); 
            }
            
            if (buscarDocumentoExtRecepBean.getCoOriDoc()!= null && !buscarDocumentoExtRecepBean.getCoOriDoc().equals("")) {
                sql.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                objectParam.put("coOriDoc", buscarDocumentoExtRecepBean.getCoOriDoc()); 
            }


            if(buscarDocumentoExtRecepBean.getBusResultado().equals("1"))
            {
                if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("03")){
                sql.append(" AND A.NU_DNI_EMI = :pNumDni");
                objectParam.put("pNumDni", buscarDocumentoExtRecepBean.getBusNumDni());
                }
                else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("02")){
                    sql.append(" AND A.NU_RUC_EMI = :pNumRuc");
                    objectParam.put("pNumRuc", buscarDocumentoExtRecepBean.getBusNumRuc());
                }
                else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("04")){
                    sql.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                    objectParam.put("pCoOtr", buscarDocumentoExtRecepBean.getBusCoOtros());
                }
            }
        //}

        sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") X ");
        //sql.append("WHERE ROWNUM < 51");        
        sql.append("WHERE ROWNUM <=  ").append(applicationProperties.getTopRegistrosConsultas());
        List<DocumentoExtRecepBean> list = new ArrayList<DocumentoExtRecepBean>();

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam,BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    
    
    @Override
    public String insExpedienteBean(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        
        String clave = Utilidades.generateRandomLetter(4);
        
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("select lpad(NVL(max(nu_sec_exp),0) +1, 10, '0') \n"
                + "from tdtc_expediente\n"
                + "where nu_ann_exp = ?");        
        
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("INSERT INTO TDTC_EXPEDIENTE(\n" +
                        "nu_ann_exp,\n" +
                        "nu_sec_exp,\n" +
                        "fe_exp,\n" +
                        "fe_vence,\n" +
                        "co_proceso,\n" +
                        "de_detalle,\n" +
                        "co_dep_exp,\n" +
                        "co_gru,\n" +
                        "nu_corr_exp,\n" +
                        "nu_expediente,\n" +
                        "us_crea_audi,\n" +
                        "fe_crea_audi,\n" +
                        "us_modi_audi,\n" +
                        "fe_modi_audi,\n" +
                        "es_estado,\n" +
                        "ccod_tipo_exp,CCLAVE)\n" +
                        "VALUES(?,?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual)," +
                        "(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),?,?,?,'3',?,?,?,SYSDATE,?,SYSDATE,'0',?, ?)");
        try{
            String snuSecExp = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class, new Object[]{expedienteBean.getNuAnnExp()});            
            expedienteBean.setNuSecExp(snuSecExp);
            
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{expedienteBean.getNuAnnExp(), snuSecExp, expedienteBean.getFeExp(),
                expedienteBean.getFeVence(),expedienteBean.getCoProceso(), expedienteBean.getDeDetalle(), expedienteBean.getCoDepEmi(),
                expedienteBean.getNuCorrExp(), expedienteBean.getNuExpediente(),expedienteBean.getUsCreaAudi(), 
                expedienteBean.getUsCreaAudi(), expedienteBean.getCoTipoExp(), clave});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Expediente Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String updExpedienteBean(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update TDTC_EXPEDIENTE \n"
                + "set US_MODI_AUDI=?\n"
                + ",CCOD_TIPO_EXP=?\n"
                + ",FE_VENCE=to_date(?,'dd/mm/yyyy hh24:mi:ss')\n"
                + ",CO_PROCESO=?\n"
                + ",FE_MODI_AUDI=SYSDATE\n"
                + "where\n"
                + "NU_ANN_EXP=? and\n"
                + "NU_SEC_EXP=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{expedienteBean.getUsCreaAudi(), expedienteBean.getCoTipoExp(),expedienteBean.getFeVence(),
            expedienteBean.getCoProceso(),expedienteBean.getNuAnnExp(),expedienteBean.getNuSecExp()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }    
    
    @Override
    public String updDocumentoExtBean(String nuAnn, String nuEmi, DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean, String pcoUserMod){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_remitos A \n"
                + "set A.CO_USE_MOD=?,");
        if (documentoExtRecepBean != null) {
            documentoExtRecepBean.setNuAnn(nuAnn);
            sqlUpd.append("A.DE_ASU=?\n"
                    + ",A.COBS_DOCUMENTO=?\n"
                    + ",A.DE_DOC_SIG=?\n"
                    + ",A.CO_TIP_DOC_ADM=?\n"
                    + ",A.NU_FOLIOS=?\n"
                    + ",A.NU_DIA_ATE=?,\n");
            sqlUpd.append("A.CCOD_ORIGING=?,\n");            
            sqlUpd.append("A.CNUM_DNIMSG=?,\n");
            sqlUpd.append("A.NRO_SOBREAUT=?,\n");
            sqlUpd.append("A.AN_SOBREAUT=?,\n");            
            sqlUpd.append("A.CDOC_DESTRAM=?,\n");

            //para verificar numero correlativo de documento
            if(documentoExtRecepBean.getNuCorDoc()==null || documentoExtRecepBean.getNuCorDoc().trim().equals("") ){
                String vnuCorDoc = getNroCorrelativoDocumento(nuAnn,documentoExtRecepBean.getCoDepEmi(),documentoExtRecepBean.getTiEmi());
                documentoExtRecepBean.setNuCorDoc(vnuCorDoc);
                sqlUpd.append("A.NU_COR_DOC=").append(vnuCorDoc).append(",\n");
            }
//            //Para verificar si ya tiene un numero correlativo de emision
//            if (documentoExtRecepBean.getNuCorEmi()==null || documentoExtRecepBean.getNuCorEmi().trim().equals("") ){
//                String vnuCorEmi = getNroCorrelativoEmision(documentoExtRecepBean.getNuAnn(),documentoExtRecepBean.getCoDepEmi());
//                documentoExtRecepBean.setNuCorEmi(vnuCorEmi);
//                sqlUpd.append("A.NU_COR_EMI=").append(vnuCorEmi).append(",\n");
//            }
            sqlUpd.append("A.CIND_SENSIBLE='" ).append(documentoExtRecepBean.getSensible()==null||documentoExtRecepBean.getSensible().equals("null")?"0":documentoExtRecepBean.getSensible()).append("',\n");
            sqlUpd.append("A.NU_COPIA='" ).append(documentoExtRecepBean.getNuCopia()==null||documentoExtRecepBean.getNuCopia().equals("null")?"1":documentoExtRecepBean.getNuCopia()).append("',\n");
            sqlUpd.append("A.NU_ANEXO='" ).append(documentoExtRecepBean.getNuAnexo()==null||documentoExtRecepBean.getNuAnexo().equals("null")?" ":documentoExtRecepBean.getNuAnexo()).append("',\n");
        
        }
//        if (expedienteBean != null) {
//            sqlUpd.append("A.NU_ANN_EXP='").append(expedienteBean.getNuAnnExp()).append("',\n");
//            sqlUpd.append("A.NU_SEC_EXP='").append(expedienteBean.getNuSecExp()).append("',\n");
//        }
        if (remitenteDocExtRecepBean != null) {
            sqlUpd.append("A.TI_EMI=?,\n");            
            sqlUpd.append("A.NU_RUC_EMI=?,\n");
            sqlUpd.append("A.NU_DNI_EMI=?,\n");  
            sqlUpd.append("A.CO_OTR_ORI_EMI=?,\n");            
            sqlUpd.append("A.CDIR_REMITE=?,\n");    
            sqlUpd.append("A.CEXP_CORREOE=?,\n");   
            sqlUpd.append("A.CTELEFONO=?,\n");    
            sqlUpd.append("A.REMI_TI_EMI=?,\n");
            sqlUpd.append("A.REMI_NU_DNI_EMI=?,\n");
            sqlUpd.append("A.REMI_CARGO=?,\n");    
            sqlUpd.append("A.CONG_CO_OTR_ORI=?,\n");    
            sqlUpd.append("A.IND_TIPOCONG=?,\n");    
            sqlUpd.append("A.IND_TIPOCONGINV=?,\n");    
            sqlUpd.append("A.REMI_CO_OTR_ORI_EMI=?,\n");    
            sqlUpd.append("A.CCOD_DPTO=?,\n");    
            sqlUpd.append("A.CCOD_PROV=?,\n");    
            sqlUpd.append("A.CCOD_DIST=?,\n");       
            
            sqlUpd.append("A.AUT_CORREOE='" ).append(remitenteDocExtRecepBean.getNotificado()==null||remitenteDocExtRecepBean.getNotificado().equals("null")?"0":remitenteDocExtRecepBean.getNotificado()).append("',\n");
            sqlUpd.append("A.IND_REITCONGINV='" ).append(remitenteDocExtRecepBean.getReiterativo()==null||remitenteDocExtRecepBean.getReiterativo().equals("null")?"0":remitenteDocExtRecepBean.getReiterativo()).append("',\n");
        }
        sqlUpd.append("A.FE_USE_MOD=SYSDATE "
                + "where\n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?");

        try {
            if (documentoExtRecepBean != null && remitenteDocExtRecepBean == null) {
                this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod, 
                            documentoExtRecepBean.getDeAsu(), 
                            documentoExtRecepBean.getDeObservacion(), 
                            documentoExtRecepBean.getDeDocSig(),
                            documentoExtRecepBean.getCoTipDocAdm(), 
                            documentoExtRecepBean.getNuFolios(), 
                            documentoExtRecepBean.getNuDiaAte(),
                            documentoExtRecepBean.getCoOriDoc(),            
                            documentoExtRecepBean.getNroDniTramitante(),
                            documentoExtRecepBean.getnSobre(),
                            documentoExtRecepBean.getAnioSobre(),            
                            documentoExtRecepBean.getCoTraDest(),
                            nuAnn, 
                            nuEmi});
            } else if (documentoExtRecepBean == null && remitenteDocExtRecepBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod,                             
                            remitenteDocExtRecepBean.getTiEmi(),           
                            remitenteDocExtRecepBean.getNuRuc(),
                            remitenteDocExtRecepBean.getNuDni(),  
                            remitenteDocExtRecepBean.getCoOtros(),            
                            remitenteDocExtRecepBean.getDeDireccion(),    
                            remitenteDocExtRecepBean.getDeCorreo(),   
                            remitenteDocExtRecepBean.getTelefono(),    
                            remitenteDocExtRecepBean.getEmiResp(),
                            remitenteDocExtRecepBean.getNuDniRes(),
                            remitenteDocExtRecepBean.getDeCargo(),    
                            remitenteDocExtRecepBean.getCoComision(),    
                            remitenteDocExtRecepBean.getCoTipoCongresista(),    
                            remitenteDocExtRecepBean.getCoTipoInv(),    
                            remitenteDocExtRecepBean.getCoOtrosRes(),    
                            remitenteDocExtRecepBean.getIdDepartamento(),    
                            remitenteDocExtRecepBean.getIdProvincia(),    
                            remitenteDocExtRecepBean.getIdDistrito(),
                            nuAnn, 
                            nuEmi});
            }else if (documentoExtRecepBean != null && remitenteDocExtRecepBean != null){
                    this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod, 
                            documentoExtRecepBean.getDeAsu(), 
                            documentoExtRecepBean.getDeObservacion(), 
                            documentoExtRecepBean.getDeDocSig(),
                            documentoExtRecepBean.getCoTipDocAdm(), 
                            documentoExtRecepBean.getNuFolios(), 
                            documentoExtRecepBean.getNuDiaAte(),
                            documentoExtRecepBean.getCoOriDoc(),            
                            documentoExtRecepBean.getNroDniTramitante(),
                            documentoExtRecepBean.getnSobre(),
                            documentoExtRecepBean.getAnioSobre(),            
                            documentoExtRecepBean.getCoTraDest(),
                            remitenteDocExtRecepBean.getTiEmi(),           
                            remitenteDocExtRecepBean.getNuRuc(),
                            remitenteDocExtRecepBean.getNuDni(),  
                            remitenteDocExtRecepBean.getCoOtros(),            
                            remitenteDocExtRecepBean.getDeDireccion(),    
                            remitenteDocExtRecepBean.getDeCorreo(),   
                            remitenteDocExtRecepBean.getTelefono(),    
                            remitenteDocExtRecepBean.getEmiResp(),
                            remitenteDocExtRecepBean.getNuDniRes(),
                            remitenteDocExtRecepBean.getDeCargo(),    
                            remitenteDocExtRecepBean.getCoComision(),    
                            remitenteDocExtRecepBean.getCoTipoCongresista(),    
                            remitenteDocExtRecepBean.getCoTipoInv(),    
                            remitenteDocExtRecepBean.getCoOtrosRes(),    
                            remitenteDocExtRecepBean.getIdDepartamento(),    
                            remitenteDocExtRecepBean.getIdProvincia(),    
                            remitenteDocExtRecepBean.getIdDistrito(),
                            nuAnn, 
                            nuEmi});
            }else{
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, nuAnn, nuEmi});
            }
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Documento Duplicado";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }    

    @Override
    public String insDocumentoExtBean(DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select lpad(SEC_REMITOS_NU_EMI.NextVal, 10, '0') from dual");

        sqlUpd.append("INSERT INTO tdtv_remitos(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "NU_COR_EMI,\n"
                + "CO_LOC_EMI,\n"
                + "CO_DEP_EMI,\n"
                + "CO_EMP_EMI,\n"
                + "CO_EMP_RES, \n"
                + "TI_EMI,\n"
                + "NU_DNI_EMI,\n"
                + "NU_RUC_EMI,\n"
                + "CO_OTR_ORI_EMI,\n"
                + "FE_EMI,\n"
                + "CO_GRU,\n"
                + "DE_ASU, \n"
                + "ES_DOC_EMI, \n"
                + "NU_DIA_ATE, \n"
                + "CO_TIP_DOC_ADM, \n"
                + "NU_COR_DOC, \n"
                + "DE_DOC_SIG, \n"
                + "NU_ANN_EXP, \n"
                + "NU_SEC_EXP, \n"
                + "NU_DET_EXP, \n"
                + "NU_FOLIOS, \n"
                + "ES_ELI,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE,\n"
                + "CO_USE_MOD,\n"
                + "CO_DEP,\n"
                + "FE_USE_MOD,\n"
                + "CDIR_REMITE,\n"
                + "AUT_CORREOE,\n"
                + "CEXP_CORREOE,\n"
                + "CTELEFONO,\n"
                + "CCOD_DPTO,\n"
                + "CCOD_PROV,\n"
                + "CCOD_DIST,\n"
                + "REMI_TI_EMI,\n"
                + "REMI_NU_DNI_EMI,\n"
                + "REMI_CO_OTR_ORI_EMI,\n"
                + "REMI_CARGO,\n"
                + "CCOD_ORIGING,\n"
                + "CNUM_DNIMSG,\n"
                + "COBS_DOCUMENTO,\n"
                + "CDOC_DESTRAM,\n"
                + "CONG_CO_OTR_ORI,\n"
                + "IND_TIPOCONG,\n"
                + "IND_TIPOCONGINV,\n"
                + "IND_REITCONGINV,\n"
                + "NRO_SOBREAUT,\n"
                + "AN_SOBREAUT,\n"
                + "CIND_SENSIBLE,\n"
                + "NU_COPIA,\n"
                + "NU_ANEXO)\n"
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),'3',?,'5',?,?,?,?,?,?,'1',?,'0',?,SYSDATE,?,?,SYSDATE,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


        try {
            String snuEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            documentoExtRecepBean.setNuEmi(snuEmi);

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getNuAnn(), snuEmi, expedienteBean.getNuCorrExp(),
                remitenteDocExtRecepBean.getCoLocEmi(), remitenteDocExtRecepBean.getCoDepEmi(), remitenteDocExtRecepBean.getCoEmpEmi(), remitenteDocExtRecepBean.getCoEmpRes(),
                remitenteDocExtRecepBean.getTiEmi(),remitenteDocExtRecepBean.getNuDni(),remitenteDocExtRecepBean.getNuRuc(),remitenteDocExtRecepBean.getCoOtros(),expedienteBean.getFeExp(),
                documentoExtRecepBean.getDeAsu(),documentoExtRecepBean.getNuDiaAte(), documentoExtRecepBean.getCoTipDocAdm(),documentoExtRecepBean.getNuCorDoc(), documentoExtRecepBean.getDeDocSig(),
                expedienteBean.getNuAnnExp(), expedienteBean.getNuSecExp(),documentoExtRecepBean.getNuFolios(), documentoExtRecepBean.getCoUseMod(), documentoExtRecepBean.getCoUseMod(), remitenteDocExtRecepBean.getCoDep(),
                remitenteDocExtRecepBean.getDeDireccion(), remitenteDocExtRecepBean.getNotificado(), remitenteDocExtRecepBean.getDeCorreo(), remitenteDocExtRecepBean.getTelefono(),
                remitenteDocExtRecepBean.getIdDepartamento(), remitenteDocExtRecepBean.getIdProvincia(), remitenteDocExtRecepBean.getIdDistrito(), remitenteDocExtRecepBean.getEmiResp(), remitenteDocExtRecepBean.getNuDniRes(),
                remitenteDocExtRecepBean.getCoOtrosRes(), remitenteDocExtRecepBean.getDeCargo(), documentoExtRecepBean.getCoOriDoc(), documentoExtRecepBean.getNroDniTramitante(),
                documentoExtRecepBean.getDeObservacion(), documentoExtRecepBean.getCoTraDest(), remitenteDocExtRecepBean.getCoComision(), remitenteDocExtRecepBean.getCoTipoCongresista(), remitenteDocExtRecepBean.getCoTipoInv(),
                remitenteDocExtRecepBean.getReiterativo(), documentoExtRecepBean.getnSobre(), documentoExtRecepBean.getAnioSobre(), documentoExtRecepBean.getSensible(), documentoExtRecepBean.getNuCopia(),documentoExtRecepBean.getNuAnexo()
                });

            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String insDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO TDTV_DESTINOS(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "NU_DES,\n"
                + "CO_LOC_DES,\n"
                + "CO_DEP_DES,\n"
                + "TI_DES,\n"
                + "CO_EMP_DES,\n"
                + "CO_PRI,\n"
                + "DE_PRO,\n"
                + "CO_MOT, \n"
                /*+ "CO_OTR_ORI_DES, \n"
                + "NU_DNI_DES, \n"
                + "NU_RUC_DES, \n"*/
                + "ES_ELI,\n"
                + "FE_USE_CRE,\n"
                + "FE_USE_MOD,\n"
                + "CO_USE_MOD,\n"
                + "CO_USE_CRE,\n"
                + "ES_DOC_REC,\n"
                + "ES_ENV_POR_TRA)\n"
                + "VALUES(?,?,?,?,?,?,?,?,?,?,'0',SYSDATE,SYSDATE,?,?,'0','0')");

        try {
            
            String vnuDes = this.jdbcTemplate.queryForObject("select nvl(MAX(a.nu_des) + 1,1) FROM tdtv_destinos a where \n" +
                                                            "A.NU_ANN=? and\n" +
                                                            "A.NU_EMI=?", String.class, new Object[]{nuAnn,nuEmi});
            destinatarioDocumentoEmiBean.setNuDes(vnuDes);
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn, nuEmi, vnuDes, destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(), destinatarioDocumentoEmiBean.getCoTipoDestino(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                /*destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),*/
                destinatarioDocumentoEmiBean.getCoUseCre(), destinatarioDocumentoEmiBean.getCoUseCre()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }
    
    @Override
    public String insReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO tdtr_referencia(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "CO_REF,\n"
                + "NU_ANN_REF,\n"
                + "NU_EMI_REF,\n"
                + "NU_DES_REF,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE)\n"
                + "VALUES(?,?,?,?,?,?,?,SYSDATE)");
        
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select lpad(SEC_REFERENCIA_CO_REF.NextVal, 10, '0') from dual");        

        try {
            String scoRef = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            ref.setCoRef(scoRef);            
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pnuAnn, pnuEmi, scoRef, ref.getNuAnn(), ref.getNuEmi(),ref.getNuDes(),
                ref.getCoUseCre()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }    
    
    @Override
    public String getNroCorrelativoDocumento(String pnuAnn, String pcoDepEmi, String ptiEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("	  select NVL(max(nu_cor_doc),0)+1 \n" +
                    "	  from tdtv_remitos\n" +
                    "	  where nu_ann=?\n" +
                    "	  and co_dep_emi=?\n" +
                    "	  AND TI_EMI=?");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pnuAnn,pcoDepEmi,ptiEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "NO_OK";
        }
        return vReturn;
    }
    
    @Override
    public DocumentoExtRecepBean getDocumentoExtRecepNew(String coDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("select substr(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(CO_DEPENDENCIA),1,6) DE_DOC_SIG_G,"
                + " PK_SGD_DESCRIPCION.ESTADOS_MP('5','TDTV_REMITOS') DE_ES_DOC_EMI_MP,"
                + " CO_EMPLEADO co_emp_emi"
                + " from rhtm_dependencia"
                + " where CO_DEPENDENCIA = ?");

        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{coDependencia});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }    
    
    @Override
    public List<MotivoBean> getLstMotivoxTipoDocumento(){
        StringBuffer sql = new StringBuffer();
        List<MotivoBean> list = new ArrayList<MotivoBean>();

        sql.append("SELECT DE_MOT,CO_MOT FROM TDTR_MOTIVO order by 1");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ReferenciaDocExtRecepBean> getLstDocExtReferencia(String pnuAnn,String pcoTiDoc,String pcoDepEmi,String pnuExpediente){
        StringBuffer sql = new StringBuffer();
        List<ReferenciaDocExtRecepBean> list = new ArrayList<ReferenciaDocExtRecepBean>();

        /*sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_TIP_DOC_ADM CO_TIP_DOC,B.NU_EXPEDIENTE,TO_CHAR(B.FE_EXP,'DD/MM/YY') FE_EXP, "
                + "SUBSTR(a.de_asu,1,59) de_asu,a.de_doc_sig "
                + "FROM TDTV_REMITOS A,TDTC_EXPEDIENTE B\n" +
                    "WHERE A.NU_ANN_EXP=B.NU_ANN_EXP AND\n" +
                    "A.NU_SEC_EXP=B.NU_SEC_EXP AND\n" +
                    "A.NU_COR_EMI=B.NU_CORR_EXP AND\n" +
                    "A.NU_ANN=? AND A.CO_TIP_DOC_ADM=? AND\n" +
                    "A.CO_DEP_EMI=? AND\n" +
                    "B.NU_CORR_EXP = ? AND\n" +
                    "A.ES_DOC_EMI NOT IN ('9','7','5')AND \n" +
                    "A.TI_EMI NOT IN('01','05') AND\n" +
                    "A.CO_GRU = '3' AND\n" +
                    "A.NU_DET_EXP =1 AND\n" +
                    "A.ES_ELI='0' AND\n" +
                    "B.ES_ESTADO='0'");*/
        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_TIP_DOC_ADM CO_TIP_DOC,B.NU_EXPEDIENTE,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EXP,\n" +
                    "SUBSTR(a.de_asu,1,59) de_asu,\n"+
                    " a.de_doc_sig,\n" +
                    "PK_SGD_DESCRIPCION.de_documento (a.co_tip_doc_adm) de_tip_doc_adm, \n"+
                    "DECODE (a.ti_emi,'01', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,\n"+
                    "         '05', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,a.de_doc_sig) li_nu_doc,\n"+                    
                    "substr(PK_SGD_DESCRIPCION.de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +                   
                    "DECODE(a.CO_GRU,'1','INTERNO','3','EXTERNO') de_procedencia\n" +
                    "FROM \n" +
                    "TDTV_REMITOS A LEFT JOIN\n" +
                    "TDTC_EXPEDIENTE B ON\n" +
                    "( A.NU_ANN_EXP=B.NU_ANN_EXP AND\n" +
                    "A.NU_SEC_EXP=B.NU_SEC_EXP \n" +     //AND
                    " )\n" +                            //A.NU_COR_EMI=B.NU_CORR_EXP 
                    "INNER JOIN TDTV_DESTINOS D \n" +
                    "ON (A.NU_ANN=D.NU_ANN AND A.NU_EMI=D.NU_EMI)\n" +
                    "WHERE\n" +
                    "A.NU_ANN=? AND \n" +
                    "(A.CO_TIP_DOC_ADM=? or 1=1 ) AND\n"// 
                  //  "A.CO_DEP_EMI=? AND\n"
                );
        if(pnuExpediente.trim().length()>0){
            sql.append("(B.NU_EXPEDIENTE = '"+pnuExpediente+"' OR A.NU_DOC_EMI = '%"+pnuExpediente+"%'" );
            sql.append(" OR A.NU_DOC_EMI||'-'||A.NU_ANN||'-'||(SELECT de_par FROM TDTR_PARAMETROS where CO_PAR='DE_INSTITUCION' )||'-'||A.DE_DOC_SIG like UPPER('%"+pnuExpediente+"%')) AND\n" );
        }
        sql.append("A.ES_DOC_EMI NOT IN ('9','7','5') AND \n" +
                    "A.TI_EMI NOT IN('05') AND\n" +
                    "A.CO_GRU in ('1','3') AND\n" +
                    "A.NU_DET_EXP =1 AND\n" +
                    "A.ES_ELI='0' AND\n" +
                   // "B.ES_ESTADO='0' AND\n" +
                    "D.NU_DES='1'\n" +
                    "order by 1 desc");
        sql.append(") A ");
        sql.append("WHERE ROWNUM < 201");  

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
                    new Object[]{pnuAnn,pcoTiDoc/*,pcoDepEmi*/});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String getNroCorrelativoEmision(String pnuAnn, String pcoDepEmi) {
        String vReturn = "1";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT NVL( MAX( a.NU_COR_EMI) , 0)+1\n" +
                        "FROM TDTV_REMITOS a\n" +
                        "WHERE NU_ANN = ?\n" +//:B_02.NU_ANN
                        "AND CO_DEP_EMI=?\n" +//:B_02.CO_DEP_EMI
                        "AND co_gru ='3'");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pnuAnn,pcoDepEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "1";
        }
        return vReturn;
    }
    
    @Override
    public String updDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update TDTV_DESTINOS \n"
                + "set CO_LOC_DES=?\n"
                + ",CO_DEP_DES=?\n"
                + ",CO_EMP_DES=?\n"
                + ",CO_PRI=?\n"
                + ",DE_PRO=?\n"
                + ",CO_MOT=?\n"
                + ",CO_OTR_ORI_DES=? \n"
                + ",NU_DNI_DES=? \n"
                + ",NU_RUC_DES=? \n"
                + ",FE_USE_MOD=SYSDATE\n"
                + ",CO_USE_MOD=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? and\n"
                + "NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),
                destinatarioDocumentoEmiBean.getCoUseCre(), nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String delDestinatarioDocumentoEmi(String nuAnn, String nuEmi, String pnuDes){
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("delete from TDTV_DESTINOS\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn, nuEmi, pnuDes});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    } 
    
    @Override
    public String updReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtr_referencia \n"
                + "set NU_ANN_REF = ?, \n"
                + "NU_EMI_REF = ?, \n"
                + "NU_DES_REF = ?\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_REF = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{ref.getNuAnn(), ref.getNuEmi(), ref.getNuDes(),
                pnuAnn, pnuEmi, ref.getCoRef()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;        
    }
    
    @Override
    public String delReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, String pnuAnnRef,String pnuEmiRef){
        String vReturn = "NO_OK";
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("DELETE FROM tdtr_referencia\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND NU_ANN_REF = ? AND NU_EMI_REF = ?");

        try {
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{pnuAnn, pnuEmi, pnuAnnRef, pnuEmiRef});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;        
    }    

    @Override
    public DocumentoExtRecepBean getDocumentoExtRec(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(A.CO_DEP_EMI) DE_DOC_SIG_G,\n" +
                    "A.CO_LOC_EMI,A.CO_EMP_EMI,A.CO_DEP_EMI,A.NU_COR_DOC,B.NU_ANN_EXP,B.NU_SEC_EXP,TO_CHAR(B.FE_EXP,'DD/MM/YYYY HH24:MI') FE_EXP,\n" +
                    "TO_CHAR(B.FE_VENCE,'DD/MM/YYYY') FE_VENCE,B.NU_CORR_EXP,B.NU_EXPEDIENTE,B.CO_PROCESO,\n" +
                    "A.ES_DOC_EMI CO_ES_DOC_EMI_MP,PK_SGD_DESCRIPCION.ESTADOS_MP(A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI_MP,\n" +
                    "A.TI_EMI,A.NU_DNI_EMI NU_DNI,PK_SGD_DESCRIPCION.ANI_SIMIL(A.NU_DNI_EMI) DE_NU_DNI,\n" +
                    "A.NU_RUC_EMI NU_RUC,PK_SGD_DESCRIPCION.DE_PROVEEDOR(A.NU_RUC_EMI) DE_NU_RUC,A.CO_OTR_ORI_EMI CO_OTROS,\n" +
                    "A.CO_EMP_RES,PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_RES,A.CO_TIP_DOC_ADM,A.DE_DOC_SIG,\n" +
                    "A.NU_FOLIOS,A.DE_ASU,A.NU_DIA_ATE, A.CDIR_REMITE DE_DIRECCION, NVL(A.AUT_CORREOE,'0') NOTIFICADO, A.CEXP_CORREOE DE_CORREO, A.CTELEFONO TELEFONO, A.CCOD_PROV ID_PROVINCIA, A.CCOD_DIST ID_DISTRITO,  A.CCOD_DPTO ID_DEPARTAMENTO ,\n" +
                    "A.REMI_TI_EMI EMI_RESP, A.REMI_NU_DNI_EMI NU_DNI_RES, PK_SGD_DESCRIPCION.ANI_SIMIL(A.REMI_NU_DNI_EMI) DE_NU_DNI_RES,A.REMI_CO_OTR_ORI_EMI CO_OTROS_RES,PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI) DE_NOM_OTROS_RES, A.REMI_CARGO DE_CARGO, A.CCOD_ORIGING CO_ORI_DOC, A.CNUM_DNIMSG NRO_DNI_TRAMITANTE, A.COBS_DOCUMENTO DE_OBSERVACION, A.CDOC_DESTRAM CO_TRA_DEST,\n" +
                    "A.CONG_CO_OTR_ORI CO_COMISION, A.IND_TIPOCONG CO_TIPO_CONGRESISTA, A.IND_TIPOCONGINV CO_TIPO_INV, NVL(A.IND_REITCONGINV,'0') REITERATIVO, A.NRO_SOBREAUT NSOBRE, A.AN_SOBREAUT ANIO_SOBRE, B.CCOD_TIPO_EXP CO_TIPO_EXP, NVL(A.CIND_SENSIBLE,'0') SENSIBLE, NVL(A.NU_COPIA,'1') nuCopia, NVL(A.NU_ANEXO,' ') nuAnexo,\n" +
                    " PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(B.FE_EXP,SysDate) as nuDiasHabiles\n"+
                    "FROM TDTV_REMITOS A left join TDTC_EXPEDIENTE B\n" +
                    "ON A.NU_ANN_EXP = B.NU_ANN_EXP AND A.NU_SEC_EXP = B.NU_SEC_EXP\n" +
                    "WHERE\n" +
                    "A.NU_ANN=?\n" +
                    "AND A.NU_EMI=?");

        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
    
    @Override
    public Map getDesFieldOtro(String pcoOtros){
        Map mapResult=null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||C.DE_RAZ_SOC_OTR DE_NOM_OTROS,\n" +
                    "     NVL(B.CELE_DESELE,'   ') DE_DOC_OTROS,\n" +
                    "     C.NU_DOC_OTR_ORI NU_DOC_OTROS \n" +
                    "  FROM TDTR_OTRO_ORIGEN C, (\n" +
                    "  SELECT CELE_CODELE, CELE_DESELE\n" +
                    "    FROM SI_ELEMENTO\n" +
                    "   WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n" +
                    "WHERE C.CO_TIP_OTR_ORI = B.CELE_CODELE(+)\n" +
                    "AND C.CO_OTR_ORI = ?");
        
        try {
               mapResult = this.jdbcTemplate.queryForMap(sql.toString(), new Object[]{pcoOtros});    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestinoEmiDoc(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = null;
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,NVL2(a.co_loc_des,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local,\n" +
                    "a.co_dep_des co_dependencia,NVL2(a.co_dep_des,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep_des), 1, 100),NULL) de_dependencia,\n" +
                    "a.co_emp_des co_empleado,NVL2(a.co_emp_des,substr(PK_SGD_DESCRIPCION.DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado,\n" +
                    "a.co_mot co_tramite,NVL2(a.co_mot,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100),NULL) de_tramite,\n" +
                    "a.co_pri co_prioridad,\n" +
                    "a.de_pro de_indicaciones,\n" +
                    "a.ti_des co_tipo_destino,\n" +
                    "'BD' accionBD\n" +
                    "FROM tdtv_destinos a\n" +
                    "where nu_ann = ? and nu_emi = ?\n" +
                    "AND ES_ELI='0' AND NU_EMI_REF is null\n" +
                    "order by 3");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ReferenciaDocExtRecepBean> getLstReferenciaDoc(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        List<ReferenciaDocExtRecepBean> list;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_TIP_DOC_ADM CO_TIP_DOC,\n" +
                    "SUBSTR(PK_SGD_DESCRIPCION.DE_NU_EXPEDIENTE(A.NU_ANN_EXP,A.NU_SEC_EXP),1,20) NU_EXPEDIENTE,\n" +
                    "TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EXP,'BD' ACCION_BD,B.CO_REF \n" +
                    "FROM TDTV_REMITOS A,TDTR_REFERENCIA B\n" +
                    "WHERE \n" +
                    "A.NU_ANN = B.NU_ANN_REF AND\n" +
                    "A.NU_EMI = B.NU_EMI_REF AND\n" +
                    "B.NU_ANN = ? AND\n" +
                    "B.NU_EMI = ? AND\n" +
                    "A.ES_DOC_EMI NOT IN ('9','7','5')AND \n" +
                    "A.NU_DET_EXP =1 AND\n" +
                    "A.ES_ELI='0'");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public DocumentoExtRecepBean getDocumentoExtRecBasic(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,\n" +
                    "A.CO_LOC_EMI,A.CO_EMP_EMI,A.CO_DEP_EMI,A.NU_COR_DOC,\n" +
                    "A.ES_DOC_EMI CO_ES_DOC_EMI_MP,\n" +
                    "A.TI_EMI,\n" +
                    "A.CO_EMP_RES,A.CO_TIP_DOC_ADM,A.DE_DOC_SIG,\n" +
                    "A.NU_FOLIOS,A.NU_ANN_EXP,A.NU_SEC_EXP\n" +
                    "FROM TDTV_REMITOS A\n" +
                    "WHERE\n" +
                    "A.NU_ANN=?\n" +
                    "AND A.NU_EMI=?\n" +
                    "AND CO_GRU='3' \n" +
                    "AND ES_ELI='0'\n" +
                    "AND NU_DET_EXP='1'\n" +
                    "AND ES_DOC_EMI <> '9'");

        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
    
    @Override
    public String updEstadoDocumentoExt(DocumentoExtRecepBean documentoExtRecepBean){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update tdtv_remitos \n"
                + "set CO_USE_MOD=? \n"
                + ",FE_USE_MOD=SYSDATE \n"
                + ",FE_EMI=SYSDATE \n"
                + ",ES_DOC_EMI=? \n"
                + "WHERE NU_ANN=? \n"
                + "AND NU_EMI=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getCoUseMod(),
                documentoExtRecepBean.getCoEsDocEmiMp(), documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getNuEmi()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String anularDocumentoExtRecep(DocumentoExtRecepBean documentoExtRecepBean){
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT nvl(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM tdtv_remitos\n"
                + "WHERE nu_ann         = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_remitos \n"
                + "set es_doc_emi = ?,\n"
                + "nu_cor_doc = ?,fe_use_mod=SYSDATE,co_use_mod=? \n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");

        try {
            String snuCorDoc = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getTiEmi(),
                documentoExtRecepBean.getCoDepEmi(), documentoExtRecepBean.getCoTipDocAdm()});

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getCoEsDocEmiMp(), snuCorDoc, documentoExtRecepBean.getCoUseMod(),
                documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public ReferenciaDocExtRecepBean getRefAtenderDocExtRec(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,SUBSTR(B.NU_EXPEDIENTE,1,20) NU_EXPEDIENTE,\n" +
                    "TO_CHAR(B.FE_EXP,'DD/MM/YY') FE_EXP,A.CO_TIP_DOC_ADM CO_TIP_DOC,\n" +
                    "'INS' ACCION_BD\n" +
                    "FROM TDTV_REMITOS A,TDTC_EXPEDIENTE B\n" +
                    "WHERE A.NU_ANN = ? AND A.NU_EMI = ? AND\n" +
                    "B.NU_ANN_EXP=A.NU_ANN_EXP AND B.NU_SEC_EXP=A.NU_SEC_EXP AND\n" +
                    "A.ES_DOC_EMI NOT IN ('9','7','5')AND \n" +
                    //"A.TI_EMI NOT IN('01','05') AND +\n" +
                    //"A.CO_GRU = '3' AND +\n" +
                    //"A.NU_DET_EXP =1 AND +\n" +
                    "A.ES_ELI='0' AND B.ES_ESTADO='0'");
        ReferenciaDocExtRecepBean referenciaDocExtRecepBean = null;
        try {
            referenciaDocExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            referenciaDocExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referenciaDocExtRecepBean;        
    }

    @Override
    public String getNumeroExpediente(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("select lpad(rtrim(ltrim(to_char(NVL(max(NU_CORR_EXP),0) +1 ))), 7, '0') nuCorrExp, \n"
                + " substr(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(?),1,6) deSiglaCorta\n"
                + "from tdtc_expediente\n"
                + "where nu_ann_exp = ?\n"
                + "and co_dep_exp = ?\n"
                + "and co_gru     = '3'");
        
        
        
        try{
            Map mapResult = this.jdbcTemplate.queryForMap(sqlQry1.toString(), new Object[]{expedienteBean.getCoDepEmi(), expedienteBean.getNuAnnExp(), expedienteBean.getCoDepEmi()});
            String snuCorrExp = String.valueOf(mapResult.get("nuCorrExp"));
            String sdeSiglaCorta = String.valueOf(mapResult.get("deSiglaCorta"));
            expedienteBean.setNuCorrExp(snuCorrExp);
//            expedienteBean.setNuExpediente(Utilidades.AjustaCampoIzquierda(sdeSiglaCorta, 6, "0") + expedienteBean.getNuAnnExp() + snuCorrExp);
            expedienteBean.setNuExpediente(expedienteBean.getNuAnnExp()+"-"+snuCorrExp);
            
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    /*@Override
    public String[] getNuDiaAteTupa(String pcoProceso) {
       String vReturn = "NO_OK|"; 
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT NVL(NU_PLAZO,'0') NU_PLAZO FROM TDTR_PROCESOS_EXP\n" +
                        "WHERE CO_PROCESO=?");       
        try{
            String snuPlazo = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pcoProceso});
            vReturn = "OK|"+snuPlazo;
        } catch (Exception e) {
            e.printStackTrace();
        }       
       return vReturn.split("\\|");
    }*/
    
    @Override
    public String updFechaExpedienteMP(String coUser, String nuAnnExp, String nuSecExp){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("UPDATE TDTC_EXPEDIENTE \n"
                + "SET US_MODI_AUDI=? \n"
                + ",FE_MODI_AUDI=SYSDATE \n"
                + ",FE_EXP=SYSDATE \n"
                + "WHERE NU_ANN_EXP=? \n"
                + "AND NU_SEC_EXP=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUser,
                nuAnnExp, nuSecExp});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String getPermisoChangeEstadoMP(String coEmp, String coDep){
        String vReturn="NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT IN_CAMBIO_EST FROM TDTR_PERMISO_MP\n" +
                        "WHERE CO_EMP=? AND CO_DEP=?\n" +
                        "AND ES_ELI='0'");       
        try{
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{coEmp, coDep});
        } catch (EmptyResultDataAccessException e) {
            vReturn = "0";
        }  catch (Exception e) {
            e.printStackTrace();
        }           
        
        return vReturn;
    }
    
    @Override
    public ProcesoExpBean getProcesoExpediente(String pcoProceso){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NU_PLAZO NU_DIAS_PLAZO, DE_ASUNTO FROM TDTR_PROCESOS_EXP\n" +
                    "WHERE CO_PROCESO=? AND ES_ESTADO = '1'");
        ProcesoExpBean proceso = null;
        try {
            proceso = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ProcesoExpBean.class),
                    new Object[]{pcoProceso});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proceso;        
    }
    
    @Override
    public DestinatarioDocumentoEmiBean getEmpleadoDestinoDocExtMp(String pcoDependencia) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.CO_LOC CO_LOCAL,PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC) DE_LOCAL,B.CO_EMPLEADO CO_EMPLEADO,PK_SGD_DESCRIPCION.DE_NOM_EMP(B.CO_EMPLEADO) DE_EMPLEADO,\n" +
                    "PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEPENDENCIA) DE_DEPENDENCIA,B.CO_DEPENDENCIA\n" +
                    "FROM SITM_LOCAL_DEPENDENCIA A,RHTM_DEPENDENCIA B\n" +
                    //"WHERE B.CO_DEPENDENCIA = ? AND A.CO_DEP = ?");
                    "WHERE B.CO_DEPENDENCIA = A.CO_DEP AND B.IN_MESA_PARTES='1'");

        DestinatarioDocumentoEmiBean destino = null;
        try {
//            destino = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
//                    new Object[]{pcoDependencia, pcoDependencia});
            destino = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class));            
        } catch (EmptyResultDataAccessException e) {
            destino = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destino;
    }
    
    @Override
    public List<RemitenteBean> getAllDependencias(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT DE_DEPENDENCIA descrip,\n" +
                    "       CO_DEPENDENCIA codDep,\n" +
                    "       DE_CORTA_DEPEN\n" +
                    "  FROM RHTM_DEPENDENCIA\n" +
                    " WHERE co_nivel <> '6'\n" +
                    "   AND IN_BAJA = '0'\n" +
                    "   order by 1");

        
        List<RemitenteBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp, String codProceso){
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT \n" +
                    "(SELECT DE_DESCRIPCION FROM TDTR_REQUISITOS RQ WHERE RQ.COD_REQ=RQEXP.COD_REQUISITO AND RQ.ES_ESTADO='1') DESCRIPCION,\n" +
                    "RQEXP.IN_REQUISITO_PRESENTE DOC_PRESENTE,\n" +
                    "RQEXP.COD_REQUISITO COD_REQUISITO,\n" +
                    "RQEXP.NU_CORRELATIVO NU_CORRELATIVO,\n" +
                    "RQEXP.IN_OBLIGATORIO as inObligatorio \n" +
                    "FROM TDTX_REQUISITO_EXPEDIENTE RQEXP\n" +
                    "WHERE RQEXP.NU_ANN_EXP=?\n" +
                    "AND RQEXP.NU_SEC_EXP=?\n" +
                    "AND RQEXP.COD_PROCESO=?\n" +
                    "AND RQEXP.ES_ESTADO='1'");

        
        List<RequisitoBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{nuAnnExp, nuSecExp, codProceso});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String updReqExpedienteDocExtRec(RequisitoBean req, String nuAnnExp, String nuSecExp, String codProceso, String coUsuario) {
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();

        sqlUpd.append("UPDATE TDTX_REQUISITO_EXPEDIENTE\n" +
                        "SET IN_REQUISITO_OK=DECODE(IN_OBLIGATORIO, '1', DECODE(?,'1','1','0'), '1'),\n" +
                        "IN_REQUISITO_PRESENTE=?,\n" +
                        "US_MODI_AUDI=?,\n" +
                        "FE_MODI_AUDI=SYSDATE\n" +
                        "WHERE\n" +
                        "NU_ANN_EXP=? AND NU_SEC_EXP=? AND\n" +
                        "COD_REQUISITO=? AND COD_PROCESO=? AND NU_CORRELATIVO=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{req.isDocPresente(), req.isDocPresente(), coUsuario,
                                        nuAnnExp, nuSecExp, req.getCodRequisito(), codProceso, req.getNuCorrelativo()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }
    
    @Override
    public String getRequisitoPendiente(String nuAnnExp, String nuSecExp){
        String vReturn = "NO_OK";
        StringBuilder sqlQry = new StringBuilder();
        sqlQry.append("SELECT COUNT(1)\n" +
                    "FROM TDTX_REQUISITO_EXPEDIENTE\n" +
                    "WHERE NU_ANN_EXP=? AND NU_SEC_EXP=?\n" +
                    "AND IN_REQUISITO_OK='0'\n" +
                    "AND ES_ESTADO='1'");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{nuAnnExp, nuSecExp});
            if(vReturn!=null&&vReturn.equals("0")){
                vReturn="OK";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String getPkExpDocExtOrigen(String pnuAnn, String pnuEmi){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT REX.NU_ANN_EXP||REX.NU_SEC_EXP FROM\n" +
                    "( SELECT NU_ANN, NU_EMI FROM \n" +
                    "(SELECT NU_ANN, NU_EMI FROM TDTR_ARBOL_SEG START WITH PK_REF = ?||?||'0' CONNECT BY PK_EMI = PRIOR PK_REF ORDER BY ROWNUM DESC)\n" +
                    "WHERE ROWNUM = 1\n" +
                    ")DO, TDTV_REMITOS RE, TDTX_REQUISITO_EXPEDIENTE REX\n" +
                    "WHERE RE.NU_ANN=DO.NU_ANN\n" +
                    "AND RE.NU_EMI=DO.NU_EMI\n" +
                    "AND RE.ES_ELI='0'\n" +
                    "AND RE.CO_GRU='3'\n" +
                    "AND RE.TI_EMI<>'01'\n" +
                    "AND REX.NU_ANN_EXP=RE.NU_ANN_EXP\n" +
                    "AND REX.NU_SEC_EXP=RE.NU_SEC_EXP");

        String pkExp = null;
        try {
            pkExp = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi});            
        } catch (EmptyResultDataAccessException e) {
            pkExp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkExp;
    }
    
    @Override
    public List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp){
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT \n" +
                    "(SELECT DE_DESCRIPCION FROM TDTR_REQUISITOS RQ WHERE RQ.COD_REQ=RQEXP.COD_REQUISITO AND RQ.ES_ESTADO='1') DESCRIPCION,\n" +
                    "RQEXP.IN_REQUISITO_PRESENTE DOC_PRESENTE,\n" +
                    "RQEXP.COD_REQUISITO COD_REQUISITO,\n" +
                    "RQEXP.NU_CORRELATIVO NU_CORRELATIVO\n" +
                    "FROM TDTX_REQUISITO_EXPEDIENTE RQEXP\n" +
                    "WHERE RQEXP.NU_ANN_EXP=?\n" +
                    "AND RQEXP.NU_SEC_EXP=?\n" +
                    "AND RQEXP.ES_ESTADO='1'");

        
        List<RequisitoBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{nuAnnExp, nuSecExp});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;  
    }    

    @Override
    public List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        
        prutaReporte.append("SELECT  (EX.NU_ANN_EXP || '-' || EX.NU_CORR_EXP) AS nuExpediente, ");
        prutaReporte.append("		(EX.NU_ANN_EXP || '-' || EX.NU_CORR_EXP) as NU_CORR_EXP, ");
        prutaReporte.append("		(SELECT DEP.TITULO_DEP FROM RHTM_DEPENDENCIA DEP WHERE DEP.CO_DEPENDENCIA=RE.CO_DEP_EMI) DE_DEPENDENCIA, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_INSTITUCION') DE_INSTITUCION, ");
        prutaReporte.append("		NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='ANEXO_MESA_PARTES'), ' ') ANEXO_MESA_PARTES, ");
        prutaReporte.append("		NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='FONO_INSTITUCION'), ' ') FONO_INSTITUCION, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='PAG_WEB') PAG_WEB, ");
        prutaReporte.append("		NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_MESA_PARTES'), ' ') DE_MESA_PARTES, ");
        prutaReporte.append("		NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_SLOGAN'), ' ') DE_SLOGAN, ");
        prutaReporte.append("		NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='TIPO_VOUCHER_MP'), ' ') tiVoucherMP, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.TI_EMI_EMP(RE.NU_ANN, RE.NU_EMI) DE_ORI_EMI,RE.NU_FOLIOS as nuFolios,");
        prutaReporte.append("		(SELECT LISTAGG(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(CO_DEP_DES), ',') WITHIN GROUP (ORDER BY  CO_DEP_DES) FROM TDTV_DESTINOS DES WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT<>'1') deDepDes, ");
        prutaReporte.append("		EX.FE_EXP, (SELECT COD_USER FROM SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES) COD_USER, ");
        prutaReporte.append("		(SELECT CDES_USER FROM SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES) deUsuario,(SELECT LISTAGG(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(CO_DEP_DES), ',') WITHIN GROUP (ORDER BY  CO_DEP_DES) FROM TDTV_DESTINOS DES WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT='1') as nuCopias,NVL(RE.NU_ANEXO,' ') as deAne, ");
        
        prutaReporte.append("		(SELECT LISTAGG(exp.NU_EXPEDIENTE, '/ ') WITHIN GROUP (ORDER BY  exp.NU_EXPEDIENTE) FROM tdtr_referencia ref inner join tdtv_remitos rt on ref.NU_ANN_REF=rt.NU_ANN and ref.NU_EMI_REF=rt.nu_EMI"
                + " inner join tdtc_expediente exp on exp.nu_sec_exp=rt.nu_sec_exp where ref.nu_emi=RE.nu_EMI and ref.nu_ann=RE.nu_ANN) nuEmiRef, ");
        prutaReporte.append("		EX.CCLAVE,(CASE WHEN RE.COBS_DOCUMENTO IS NULL THEN ' ' ELSE 'Observación:'||RE.COBS_DOCUMENTO END) as deObsDoc, ");
        prutaReporte.append("		(CASE WHEN RE.ES_DOC_EMI='8' THEN NVL((SELECT PAR.DE_PAR FROM TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='OBS_VOUCHER_MP'), ' ') ELSE ' ' END) as obsVoucherMP ");
        prutaReporte.append(" FROM TDTV_REMITOS RE, ");
        prutaReporte.append("	 TDTC_EXPEDIENTE EX ");   
        
        try {
            prutaReporte.append("WHERE RE.NU_ANN = :nu_ann ");
            objectParam.put("nu_ann", documento.getNuAnn());
            prutaReporte.append("AND RE.NU_EMI = :nu_emi ");
            objectParam.put("nu_emi", documento.getNuEmi());
            prutaReporte.append("AND RE.NU_ANN_EXP=EX.NU_ANN_EXP ");
            prutaReporte.append("AND RE.NU_SEC_EXP=EX.NU_SEC_EXP ");

            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public List<DocumentoExtRecepBean> getListaReporteBusqueda(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        
        prutaReporte.append("SELECT  A.NU_COR_EMI, ");
        prutaReporte.append("	     TO_CHAR(A.FE_EMI, 'DD/MM/YYYY') FE_EMI_CORTA, ");
        prutaReporte.append("	     (SELECT CDOC_DESDOC ");
        prutaReporte.append("	     FROM SI_MAE_TIPO_DOC ");
        prutaReporte.append("	     WHERE CDOC_TIPDOC = A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,B.NU_DOC, ");
        prutaReporte.append("	     (select CELE_DESELE ");
        prutaReporte.append("	     from SI_ELEMENTO ");
        prutaReporte.append("	     WHERE CTAB_CODTAB='TIP_DESTINO' ");
        prutaReporte.append("	     AND CELE_CODELE=A.TI_EMI) tiRemitente, ");
        prutaReporte.append("	     PK_SGD_DESCRIPCION.TI_EMI_EMP(A.NU_ANN, A.NU_EMI) deRemitente, ");
        prutaReporte.append("	     CASE A.NU_CANDES ");
        prutaReporte.append("	     	WHEN 1 THEN PK_SGD_DESCRIPCION.TI_DES_EMP(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     	ELSE PK_SGD_DESCRIPCION.TI_DES_EMP_V(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     END deEmpPro, ");
        prutaReporte.append("	     A.DE_ASU, ");
        prutaReporte.append("	     (SELECT DE_EST ");
        prutaReporte.append("	     FROM TDTR_ESTADOS ");
        prutaReporte.append("	     WHERE CO_EST || DE_TAB = A.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("	     A.NU_DIA_ATE, ");
        prutaReporte.append("	     B.NU_EXPEDIENTE, ");
        prutaReporte.append("	     TO_CHAR(C.FE_VENCE, 'DD/MM/YYYY') FE_EXP_VENCE_CORTA, ");
        prutaReporte.append("	     (SELECT DE_NOMBRE ");
        prutaReporte.append("	     FROM TDTR_PROCESOS_EXP ");
        prutaReporte.append("	     WHERE CO_PROCESO = B.CO_PROCESO_EXP ");
        prutaReporte.append("	     AND ES_ESTADO='1') DE_PROCESO_EXP, ");
        prutaReporte.append("	     B.CO_PROCESO_EXP AS coProcesoExp, ");
        prutaReporte.append("	     A.NU_FOLIOS AS nuFolios, ");
        prutaReporte.append("	     PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_REC, ");
        prutaReporte.append("        PK_SGD_DESCRIPCION.DE_DOMINIOS('ORI_DOCUMENTO',A.CCOD_ORIGING) deOriEmiMp, ");
        prutaReporte.append("        PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP) deDependencia ");
        
        prutaReporte.append("FROM TDTV_REMITOS A, ");
        prutaReporte.append("     TDTX_REMITOS_RESUMEN B, ");
        prutaReporte.append("     TDTC_EXPEDIENTE C ");
        prutaReporte.append("WHERE A.NU_ANN=B.NU_ANN ");
        prutaReporte.append("AND A.NU_EMI=B.NU_EMI ");
        prutaReporte.append("AND C.NU_ANN_EXP=A.NU_ANN_EXP ");
        prutaReporte.append("AND C.NU_SEC_EXP=A.NU_SEC_EXP ");
        
        try {
            String pnuAnn = buscarDocumentoExtRecepBean.getCoAnnio();
            if (!(buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pnuAnn).append("'");
            }
            prutaReporte.append(" AND A.CO_GRU = '3'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoExtRecepBean.getCoDepEmi()).append("'");

            String auxTipoAcceso=buscarDocumentoExtRecepBean.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if (tiAcceso.equals("1")) { // acceso personal
                prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoExtRecepBean.getCoEmpleado()).append("'");
            } else {
                if(buscarDocumentoExtRecepBean.getInMesaPartes().equals("0")/* && buscarDocExt.getInCambioEst().equals("0")*/){
                    bBusqDep = true;
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtRecepBean.getCoDependencia()).append("'");        
                }
            }            
            //String pTipoBusqueda = buscarDocumentoExtRecepBean.getTipoBusqueda();
            
            //if (pTipoBusqueda.equals("1") && buscarDocumentoExtRecepBean.isEsIncluyeFiltro()) {
             //   bBusqFiltro = true;
            //}
            
            //Filtro
//            if (pTipoBusqueda.equals("0") || bBusqFiltro) {
                if (buscarDocumentoExtRecepBean.getCoTipoDoc() != null && buscarDocumentoExtRecepBean.getCoTipoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoExtRecepBean.getCoTipoDoc()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoEstadoDoc() != null && buscarDocumentoExtRecepBean.getCoEstadoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoExtRecepBean.getCoEstadoDoc()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoTipoEmisor()!= null && buscarDocumentoExtRecepBean.getCoTipoEmisor().trim().length() > 0) {
                    prutaReporte.append(" AND A.TI_EMI = '").append(buscarDocumentoExtRecepBean.getCoTipoEmisor()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoDepOriRec()!= null && buscarDocumentoExtRecepBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtRecepBean.getCoDepOriRec()).append("'");    
                }               

                if(buscarDocumentoExtRecepBean.getCoProceso()!=null&&buscarDocumentoExtRecepBean.getCoProceso().trim().length()>0){
                    if(buscarDocumentoExtRecepBean.getCoProceso().equals("CON_TUPA")){
                        prutaReporte.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                    }else{
                        prutaReporte.append(" AND B.CO_PROCESO_EXP = '").append(buscarDocumentoExtRecepBean.getCoProceso()).append("'");
                    }
                }            
                //if (buscarDocumentoExtRecepBean.getCoDepDestino() != null && buscarDocumentoExtRecepBean.getCoDepDestino().trim().length() > 0) {
                //    prutaReporte.append(" AND INSTR(B.TI_EMI_DES, '").append(buscarDocumentoExtRecepBean.getCoDepDestino()).append("') > 0 ");
                //}
                if (buscarDocumentoExtRecepBean.getEsFiltroFecha() != null
                        && (buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("1") || buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoExtRecepBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoExtRecepBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0 && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        prutaReporte.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin)
                            .append("','dd/mm/yyyy') + 0.99999 ");
                    }
                }
//            }
            //Busqueda
//            if (pTipoBusqueda.equals("1")) {         
                if (buscarDocumentoExtRecepBean.getBusNumEmision() != null && buscarDocumentoExtRecepBean.getBusNumEmision().trim().length() > 0) {
                    prutaReporte.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.getInteger(buscarDocumentoExtRecepBean.getBusNumEmision()));
                }

                if (buscarDocumentoExtRecepBean.getBusNumDoc()!= null && buscarDocumentoExtRecepBean.getBusNumDoc().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", buscarDocumentoExtRecepBean.getBusNumDoc());
                }

                if (buscarDocumentoExtRecepBean.getBusNumExpediente() != null && buscarDocumentoExtRecepBean.getBusNumExpediente().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                    objectParam.put("pnuExpediente", buscarDocumentoExtRecepBean.getBusNumExpediente());
                }
                // Busqueda del Asunto
                if (buscarDocumentoExtRecepBean.getBusAsunto()!= null && buscarDocumentoExtRecepBean.getBusAsunto().trim().length() > 1) {                    
                   // prutaReporte.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                 prutaReporte.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                 objectParam.put("pDeAsunto", buscarDocumentoExtRecepBean.getBusAsunto().trim());
                 
                }
                
                if (buscarDocumentoExtRecepBean.getCoTipoExp() != null && !buscarDocumentoExtRecepBean.getCoTipoExp().equals("")) {
                    prutaReporte.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                    objectParam.put("codTipoExp", buscarDocumentoExtRecepBean.getCoTipoExp()); 
                }

                if (buscarDocumentoExtRecepBean.getCoOriDoc()!= null && !buscarDocumentoExtRecepBean.getCoOriDoc().equals("")) {
                    prutaReporte.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                    objectParam.put("coOriDoc", buscarDocumentoExtRecepBean.getCoOriDoc()); 
                }

                if(buscarDocumentoExtRecepBean.getBusResultado().equals("1"))
                {
                    if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("03")){
                    prutaReporte.append(" AND A.NU_DNI_EMI = :pNumDni");
                    objectParam.put("pNumDni", buscarDocumentoExtRecepBean.getBusNumDni());
                    }
                    else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("02")){
                        prutaReporte.append(" AND A.NU_RUC_EMI = :pNumRuc");
                        objectParam.put("pNumRuc", buscarDocumentoExtRecepBean.getBusNumRuc());
                    }
                    else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("04")){
                        prutaReporte.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                        objectParam.put("pCoOtr", buscarDocumentoExtRecepBean.getBusCoOtros());
                    }
                }
//            }

            prutaReporte.append(" ORDER BY A.NU_COR_EMI DESC ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }

    @Override
    public String getPassDniPide(String pctabCodTab, String Dni) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESCOR\n" +
                   "FROM   SI_ELEMENTO\n" +
                   "WHERE  CTAB_CODTAB = ? AND CELE_DESELE= ?");

        String pkExp = null;
        try {
            pkExp = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pctabCodTab, Dni});            
        } catch (EmptyResultDataAccessException e) {
            pkExp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkExp;
    }
    @Override
    public DocumentoExtRecepBean getDocumentosExpInteExt(String nuExpediente) { 
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();  
        sql.append(" SELECT X.* FROM ( SELECT  A.NU_ANN,A.NU_EMI,B.NU_EXPEDIENTE \n" +
                "         FROM IDOSGD.TDTV_REMITOS A\n" +
                "         inner join IDOSGD.TDTX_REMITOS_RESUMEN B on B.NU_ANN=A.NU_ANN  AND B.NU_EMI=A.NU_EMI\n" +
                "         inner join IDOSGD.TDTC_EXPEDIENTE C on C.NU_ANN_EXP=A.NU_ANN_EXP AND C.NU_SEC_EXP=A.NU_SEC_EXP\n" +
                "         WHERE  A.ES_ELI='0' ");     
       
        if(nuExpediente.length()==12)
            sql.append(" AND A.TI_EMI='03'"); 
              
          
        sql.append(" AND B.NU_EXPEDIENTE = ? ");
        objectParam.put("pnuExpediente", nuExpediente);
                         
        sql.append(" ORDER BY A.NU_EMI  ) X WHERE ROWNUM=1 ");
        
        DocumentoExtRecepBean documentoExtRecepBean = null;

        try { 
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{nuExpediente});
        }catch (EmptyResultDataAccessException e) {
            
        }catch (Exception e) {
             
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
}
