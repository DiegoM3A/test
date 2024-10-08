package pe.gob.onpe.tramitedoc.web.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.AvanceBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocRespuestaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBeansContenedor;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.RemitosResBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.AnexoDocumentoService;
import pe.gob.onpe.tramitedoc.service.AvanceService;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.DocumentoBasicoService;
import pe.gob.onpe.tramitedoc.service.DocumentoExtRecepService;
import pe.gob.onpe.tramitedoc.service.DocumentoMensajeriaService;
import pe.gob.onpe.tramitedoc.service.DocumentoObjService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.TemaService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/*interoperabilidad*/
import pe.gob.segdi.pide.wsentidad.WsEntidadInter;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.bean.DatosInterBean;/*interoperabilidad*/
import pe.gob.onpe.tramitedoc.bean.DatosPlantillaDoc;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.service.NotificacionService;

@Controller
@RequestMapping("/{version}/srDocObjeto.do")
@SessionAttributes(value = {"docSession"})
public class DocumentoObjetoController {


    @Autowired
    private DocumentoObjService documentoObjService;
     @Autowired
    private EmiDocumentoAdmService EmiDocumentoAdmService;
    @Autowired
    private AnexoDocumentoService anexoDocumentoService;
    @Autowired
    private DocumentoBasicoService documentoBasicoService;
    @Autowired
    private DocumentoExtRecepService documentoExtRecepService;    
    
    @Autowired
    private CommonQryService commonQryService; 
    
    @Autowired
    private ApplicationProperties applicationProperties;   
    
    @Autowired    
    private DocumentoMensajeriaService documentoMensajeriaService;
    
    @Autowired
    private TemaService temaService; 
    
    @Autowired
    private AvanceService avanceService; 
    
    /*interoperabilidad*/
    @Autowired
    private DatosPlantillaDao datosPlantillaDao;
    /*interoperabilidad*/    
    @Autowired
    private NotificacionService notiService;
        
    @RequestMapping(method = RequestMethod.POST, params ="accion=goDocRutaAbrir")
    private @ResponseBody String goObtieneDocumento(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocRespuestaBean retRespuesta = new DocRespuestaBean();

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");

        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreDoc(pNuAnn, pNuEmi, pTiOpe, usuario);
                
                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoFirma(docVerBean.getNoFirma());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());

            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del documento");
                e.printStackTrace();
            }

            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }

        }
        return retval;
    }

    //YUAL
      @RequestMapping(method = RequestMethod.POST, params ="accion=goDocInicialRutaAbrir")
    private @ResponseBody String goDocInicialRutaAbrir(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
       // Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocRespuestaBean retRespuesta = new DocRespuestaBean();

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuDes = ServletUtility.getInstancia().loadRequestParameter(request, "nuDes");
        
        DocumentoAnexoBean oDocumentoAnexoBean= new DocumentoAnexoBean();
        oDocumentoAnexoBean=anexoDocumentoService.getReferenciaInicial(pNuAnn, pNuEmi, pNuDes);
        if (oDocumentoAnexoBean.getNuAnn() != null && oDocumentoAnexoBean.getNuEmi() != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreDocInicial(oDocumentoAnexoBean.getNuAnn(), oDocumentoAnexoBean.getNuEmi());
                
                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoFirma(docVerBean.getNoFirma());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());

            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del documento");
                e.printStackTrace();
            }

            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }

        }
        return retval;
    }

    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaGeneraDocx")
    private @ResponseBody String goRutaGeneraDocx(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        DocumentoObjBean documentoObjBean = null;
        
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");

        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreGeneraDocx(pNuAnn, pNuEmi, pTiOpe);
                documentoObjBean = documentoObjService.getNombreArchivo(pNuAnn, pNuEmi, pTiOpe);

                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoFirma(docVerBean.getNoFirma());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());

                if(documentoObjBean!=null){
                    
                    retRespuesta.setTieneWord((documentoObjBean.getwNombreArchivo()!=null && documentoObjBean.getwNombreArchivo().length()>0)? "SI":"NO");
                   
                }else{
                    retRespuesta.setTieneWord("NO");
                }
                                
            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del documento");
                e.printStackTrace();
            }

            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }

        }
        return retval;
    }
    
    //YUAL
     @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaGeneraActaNotificacion")
    private @ResponseBody String goRutaGeneraActaNotificacion(HttpServletRequest request, Model model) throws Exception {
        String coRespuesta = "0";
        String deNoDoc;
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pPlantillas = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        ServletContext sc = request.getSession().getServletContext();
        String rutaReporte=sc.getRealPath("/reports/"); 
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));        
        Map parametros = new HashMap(); 
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean = new  BuscarDocumentoEmiConsulBean();
         
        buscarDocumentoEmiConsulBean.setCoAnnio(pnuAnn);    
        buscarDocumentoEmiConsulBean.setCoEmi(pnuEmi);
        buscarDocumentoEmiConsulBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoEmiConsulBean.setFormatoReporte(pPlantillas);
        
        ReporteBean objReporteBean = documentoObjService.getGenerarReporteNotificacion(buscarDocumentoEmiConsulBean, parametros);
        
        /*  DatosPlantillaDoc  datosPlantilla;
             datosPlantilla = datosPlantillaDao.getDocumentoEmitido(pnuAnn, pnuEmi);
        String prutaReporte = "reporte?coReporte=TR_100_NOTI&coParametros=nombre_anio_1=''|nombre_anio_2=''"; //+ usuario.getCoUsuario();
           prutaReporte = prutaReporte+ "|numero_id=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|destinatario=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|razon_social=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|direccion=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|referencia=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|nro_documento=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|expediente=''";//+ documentoEmiBean.get;
           prutaReporte = prutaReporte+"|folio=''";//+ documentoEmiBean.get;
           
           prutaReporte = prutaReporte+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg";
*/
       if(bis!=null)bis.close();
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");        
        retval.append(objReporteBean.getcoRespuesta());
        retval.append("\",\"deRespuesta\":\"");        
        retval.append(objReporteBean.getdeRespuesta());
        retval.append("\",\"noUrl\":\"");
        retval.append(objReporteBean.getnoUrl());
        retval.append("\",\"noDoc\":\"");
        retval.append(objReporteBean.getnoDoc());
        retval.append("\"}");

        return retval.toString();
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocRutaAnexo")
    private @ResponseBody String goObtieneDocumentoAnexo(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");

        if (pNuAnn != null && pNuEmi != null && pNuAne != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                if(pNuAne.equals("ALL")){
                    docVerBean = documentoObjService.getNombreDocAnexo(pNuAnn, pNuEmi, "1");
                     retRespuesta.setNoDoc("TEMP|DocumentosAnexos.zip"); 
                     retRespuesta.setNoUrl(docVerBean.getUrlDocumento().replace("abrirAnexo","abrirAnexos"));
                }
                else{
                    docVerBean = documentoObjService.getNombreDocAnexo(pNuAnn, pNuEmi, pNuAne);
                     retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                     retRespuesta.setNoUrl(docVerBean.getUrlDocumento());
                }
                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNuAne(docVerBean.getNuAne());
                
            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del Anexo");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
            

        }
        return retval;
    }

    
    //YUAL
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCopiarAnexo")
    private @ResponseBody String goCopiarAnexo(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");
        String pNuAnnDocProyecto = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnnDocProyecto");
        String pNuEmiDocProyecto = (ServletUtility.getInstancia().loadRequestParameter(request, "nuEmiDocProyecto")).substring(4,14);
        if (pNuAnn != null && pNuEmi != null && pNuAne != null) {
            try {

                String result="";
                result = documentoObjService.CopiarAnexo(pNuAnn,pNuEmi,pNuAne,pNuAnnDocProyecto,pNuEmiDocProyecto);
                retRespuesta.setRetval("Se ha agregado el anexo al Documento en Proyecto");
                
               
                
            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del Anexo");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
            

        }
        return retval;
    }

    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocRutaAnexoArchivado")
    private @ResponseBody String goObtieneDocumentoAnexoArchivado(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "nuDes");

        if (pNuAnn != null && pNuEmi != null && pnuDes != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreDocAnexoArchivado(pNuAnn, pNuEmi, pnuDes);

                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNuAne(docVerBean.getNuAne());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());
                
            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del Anexo");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
            

        }
        return retval;
    }

    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocSeguimiento")
    public String goDocSeguimiento(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pkExp = null;
        try {
            pkExp = documentoExtRecepService.getPkExpDocExtOrigen(pkEmi);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        model.addAttribute("frm_docOrigenBean_pkExp", pkExp);        
        model.addAttribute("pkEmiDoc", pkEmi);
        return "/modalGeneral/consultaSeguimiento";
    }

    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVincularTema")
    public String goVincularTema(HttpServletRequest request, Model model) {
        
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String tipoRecep = ServletUtility.getInstancia().loadRequestParameter(request, "tipoRecep");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request); 
        TemaBean temabean =  new TemaBean();
        temabean.setCoDependencia(usuario.getCoDep());
        List<TemaBean> tema = temaService.getListTema(temabean);
        String codTema="";
        if(tipoRecep.equals("RECEPCION")){
            codTema = temaService.getEditTemaDestinos(pkEmi);         
        }
        if(tipoRecep.equals("EMISION")){
            codTema = temaService.getEditTemaRemitos(pkEmi);        
        }
        temabean.setCoTema(codTema);
        model.addAttribute("deListTema", tema);
        model.addAttribute("temaBean", temabean);
        model.addAttribute("codigoEmision", pkEmi);
        model.addAttribute("codigoTipo", tipoRecep);
        return "/modalGeneral/registroVincularTema";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabarTema",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabarTema(HttpServletRequest request, Model model){ 
        String mensaje = "NO_OK";
        String codigoTema = ServletUtility.getInstancia().loadRequestParameter(request, "coTema");
        String codigoEmision = ServletUtility.getInstancia().loadRequestParameter(request, "codigoEmision");
        String codTipo = ServletUtility.getInstancia().loadRequestParameter(request, "codTipo");
        String coRespuesta;
        String deRespuesta;         
        try{   
            if(codigoTema !=null && codigoTema.length()>0 ){
            if(codTipo.equals("EMISION"))
            mensaje = temaService.updTemaRemitos(codigoEmision,codigoTema);  
            if(codTipo.equals("RECEPCION"))
            mensaje = temaService.updTemaDestinos(codigoEmision,codigoTema);      
            }
            else {
                mensaje="Tiene que seleccionar el tema.";
            }
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta); 
        retval.append("\""); 
        retval.append("}");

        return retval.toString();        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerAvance")
    public String goVerAvance(HttpServletRequest request, Model model) {
             
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn"); 
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi"); 
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes"); 
        AvanceBean avance= new AvanceBean();
        avance.setTiAvance("RECEP");
        avance.setNuAnn(pnuAnn);
        avance.setNuEmi(pnuEmi);
        avance.setNuDes(pnuDes);        
        List<AvanceBean> ListAvance = avanceService.getListAvance(avance);        
        model.addAttribute("recepAvanceList", ListAvance); 
        model.addAttribute("tipoAvance", "RECEP"); 
        model.addAttribute("pnuAnn", pnuAnn); 
        model.addAttribute("pnuEmi", pnuEmi); 
        model.addAttribute("pnuDes", pnuDes); 
        return "/modalGeneral/registroAvance";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerAvanceConsulta")
    public String goVerAvanceConsulta(HttpServletRequest request, Model model) {
             
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn"); 
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi"); 
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes"); 
        AvanceBean avance= new AvanceBean(); 
        avance.setNuAnn(pnuAnn);
        avance.setNuEmi(pnuEmi);
        avance.setNuDes(pnuDes);
        avance.setTiAvance("RECEP");        
        List<AvanceBean> ListAvance = avanceService.getListAvance(avance);        
        model.addAttribute("recepAvanceList", ListAvance); 
        model.addAttribute("pnuAnn", pnuAnn); 
        model.addAttribute("pnuEmi", pnuEmi); 
        model.addAttribute("pnuDes", pnuDes); 
        return "/modalGeneral/consultaAvance";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabarAvance",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabarAvance(@RequestBody AvanceBean avance, HttpServletRequest request, Model model){ 
        String mensaje = "NO_OK";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);     
        /*String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn"); 
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi"); 
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes"); 
        String tipoAvance = ServletUtility.getInstancia().loadRequestParameter(request, "tipAvance"); 
        String desAvance = ServletUtility.getInstancia().loadRequestParameter(request, "desAvance");
        //AvanceBean avance= new AvanceBean();
        avance.setTiAvance(tipoAvance);
        avance.setNuAnn(pnuAnn);
        avance.setNuEmi(pnuEmi);
        avance.setNuDes(pnuDes);
        avance.setDeAvance(desAvance);
        */
        avance.setCoUser(usuario.getCoUsuario());
        
        String coRespuesta;
        String deRespuesta;         
        try{                
            mensaje = avanceService.insertAvance(avance);      
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta); 
        retval.append("\""); 
        retval.append("}");

        return retval.toString();        
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocAnexo")
    public String goDocAnexo(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        model.addAttribute("pkEmiDoc", pkEmi);
        return "/modalGeneral/consultaAnexos";
    }
    
     @RequestMapping(method = RequestMethod.POST, params = "accion=goDocEnviarNotificacion")
    public String goDocEnviarNotificacion(HttpServletRequest request, Model model) {
        //String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String ptiEnvMsj = ServletUtility.getInstancia().loadRequestParameter(request, "ptiEnvMsj");
        String pexisteDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pexisteDoc");
        String pexisteAnexo = ServletUtility.getInstancia().loadRequestParameter(request, "pexisteAnexo");
        String tipoBandeja = ServletUtility.getInstancia().loadRequestParameter(request, "tipoBandeja");
        
        //model.addAttribute("pkEmiDoc", pkEmi);
        model.addAttribute("pnuAnn", pnuAnn);
        model.addAttribute("pnuEmi", pnuEmi);
        model.addAttribute("ptiEnv", ptiEnvMsj);
        
        model.addAttribute("pexisteDoc", pexisteDoc);
        model.addAttribute("pexisteAnexo", pexisteAnexo);
        model.addAttribute("tipoBandeja", tipoBandeja);
        
        model.addAttribute("MensajeriaList",commonQryService.getLsDepenciaMensjeria());
       // model.addAttribute("pnuDes", pnuDes);
       /*interoperabilidad*/
       
        DatosInterBean datosInter=new DatosInterBean();
         
        
        String vDeDepDes="";
        String vDeNomDes="";
        String vDeCarDes="";        
       
        String vResult="N"; 
        
        datosInter=commonQryService.DatosInter(pnuAnn,pnuEmi);
                      
        if (datosInter!=null)
        {
            if(datosInter.getNuRucDes().length()>0 && !datosInter.getNuRucDes().equals("0"))
            {
 

                WsEntidadInter wSEntidad = new WsEntidadInter();
                    try {
                        System.out.println("getNuRucDes==>" + datosInter.getNuRucDes());
                        String co_par =commonQryService.obtenerValorParametro("ESTADO_MESA_VIRTUAL")+"";
                        if(co_par.equals("P")){
                        System.out.println("URL_ENTIDADES_INTER==>" + datosPlantillaDao.getParametros("URL_ENTIDADES_INTER"));                        
                        vResult= wSEntidad.getInter(datosInter.getNuRucDes(),datosPlantillaDao.getParametros("URL_ENTIDADES_INTER"));
                        }
                        else {
                        System.out.println("URL_ENTIDADES_INTERQA==>" + datosPlantillaDao.getParametros("URL_ENTIDADES_INTER_D"));                        
                        vResult= wSEntidad.getInterQA(datosInter.getNuRucDes(),datosPlantillaDao.getParametros("URL_ENTIDADES_INTER_D"));
                        }
                        vDeDepDes=datosInter.getDeDepDes();
                        vDeNomDes=datosInter.getDeNomDes();
                        vDeCarDes=datosInter.getDeCarDes();  
                   }catch(Exception e){
                       vResult = e.getMessage();
                        e.printStackTrace();
                   }
                     System.out.println("vResult==>" + vResult);
            }

            
        }

      
       model.addAttribute("psiInter",vResult);
       model.addAttribute("pDeDepDes",vDeDepDes);
       model.addAttribute("pDeNomDes",vDeNomDes);
       model.addAttribute("pDeCarDes",vDeCarDes);
       
       /*interoperabilidad*/       
        return "/modalGeneral/consultaEnvioNotificacion";
    }
     //YUAL
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocEnviarNotificacionPopUp")
    public String goDocEnviarNotificacionPopUp(HttpServletRequest request, Model model){
        DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
        model.addAttribute("pnuAnn", docSession.getNuAnn());
        model.addAttribute("pnuEmi", docSession.getNumeroDoc());
        model.addAttribute("MensajeriaList",commonQryService.getLsDepenciaMensjeria());
       // model.addAttribute("pnuDes", pnuDes);
        
        return "/modalGeneral/consultaEnvioNotificacion";
    }

    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goArDocMsjEnviados")
    public String goArDocMsjEnviados(HttpServletRequest request, Model model) {
        //String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        //String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
       DocumentoEmiBean documentoEmiArBean=new DocumentoEmiBean();
        //model.addAttribute("pkEmiDoc", pkEmi);
        model.addAttribute("pnuAnn", pnuAnn);
        model.addAttribute("pnuEmi", pnuEmi);
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute(documentoEmiArBean);
        //model.addAttribute("pnuDes", pnuDes);
        
        return "/modalGeneral/archivarDocMsj";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdArchivarDoc")
     public String goUpdDescargarMensaje(@RequestBody DocumentoEmiBean documentoEmiBean, HttpServletRequest request,Model model) {
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();          
                       
                      
        try {
            String respuesta = EmiDocumentoAdmService.updArchivarDocumento(documentoEmiBean,usuario);                       
            
            mensaje = respuesta;
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", "Datos guardados.");
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargarDocumentosAnexos")
    public String goCargarDocumentosAnexos(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        model.addAttribute("pkEmiDoc", pkEmi);
        //model.addAttribute("listaDestinatario",referencedData.getListDestinatario(pnuAnn,usuarioConfigBean.getCoDep()));    
        return "/modalGeneral/consultaCargarAnexos";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetalleDocAnexo")
    public String goDetalleDocAnexo(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String mensaje = "NO_OK";

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                RemitosResBean docRemitoBean = documentoBasicoService.getRemitoResumen(pNuAnn, pNuEmi);
                DestinoResBen docDestinoBean = documentoBasicoService.getDestinoResumen(pNuAnn, pNuEmi, pNuDes);
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(pNuAnn, pNuEmi);

                docRemitoBean.setFeEmi(docRemitoBean.getFeEmi().substring(0,19));
                
                model.addAttribute("docRemitoBean", docRemitoBean);
                model.addAttribute("docDestinoBean", docDestinoBean);
                model.addAttribute("docAnexoList", docAnexoList);

                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaAnexosDetalle";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetalleCargarAnexo")
    public String goDetalleCargarAnexo(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String mensaje = "NO_OK";

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                RemitosResBean docRemitoBean = documentoBasicoService.getRemitoResumen(pNuAnn, pNuEmi);
                DestinoResBen docDestinoBean = documentoBasicoService.getDestinoResumen(pNuAnn, pNuEmi, pNuDes);
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(pNuAnn, pNuEmi);

                model.addAttribute("docRemitoBean", docRemitoBean);
                model.addAttribute("docDestinoBean", docDestinoBean);
                model.addAttribute("docAnexoList", docAnexoList);
                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaCargarAnexosDetalle";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goListadoArchivosAnexos")
    public String goListadoArchivosAnexos(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String mensaje = "NO_OK";

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(pNuAnn, pNuEmi);
                model.addAttribute("fileSizeMaxCargo",applicationProperties.getFileSizeMaxCargo());
                model.addAttribute("docAnexoList", docAnexoList);
                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaCargarAnexosListado";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }
    
    //YUAL
     @RequestMapping(method = RequestMethod.POST, params = "accion=goVerificaCargaDoc",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goVerificaCargaDoc(HttpServletRequest request, Model model) {
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        StringBuilder retval = new StringBuilder();
        String bCargaDoc="0";
            try {
                bCargaDoc = documentoBasicoService.verificaCargaDoc(pnuAnn, pnuEmi);


            } catch (Exception e) {
                e.printStackTrace();
              
            }

            retval.append("{\"bCargaDoc\":\"");
            retval.append(bCargaDoc);
            retval.append("\""); 
            retval.append("}");
        
            
        return retval.toString();   
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goListadoArchivosAnexosModoLectura")
    public String goListadoArchivosAnexosModoLectura(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String mensaje = "NO_OK";

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(pNuAnn, pNuEmi);
                model.addAttribute("docAnexoList", docAnexoList);
                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaCargarAnexosListadoModoLectura";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goListadoMsjAnexos")
    public String goListadoMsjAnexos(HttpServletRequest request, Model model) {
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String mensaje = "NO_OK";

        /*String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;*/

        if (pNuAnn != null) {
            /*System.out.println(pNuAnn);
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);*/
            try {
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosMsjList(pNuAnn, pNuEmi);
                model.addAttribute("docAnexoMsjList", docAnexoList);
                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        
        if (mensaje.equals("OK")) {
            return "/modalGeneral/archivarDocMsjListado";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetalleSeguimiento")
    public String goDetalleSeguimiento(HttpServletRequest request, Model model) {
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pkExp = ServletUtility.getInstancia().loadRequestParameter(request, "pkExp");        
        String mensaje = "NO_OK";

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            
            try {
                RemitosResBean docRemitoBean = documentoBasicoService.getRemitoResumen(pNuAnn, pNuEmi);
                DestinoResBen docDestinoBean = documentoBasicoService.getDestinoResumen(pNuAnn, pNuEmi, pNuDes);
                
                
                List<DocumentoRecepMensajeriaBean> docMensajeriaList=documentoMensajeriaService.getLstDetalleMensajeria(docDestinoBean);
                DocumentoRecepMensajeriaBean docMensajeriaBean=new DocumentoRecepMensajeriaBean();
                if(!docMensajeriaList.isEmpty()){
                     docMensajeriaBean= docMensajeriaList.get(0);
                     docMensajeriaBean.setFecEnviomsj(docMensajeriaBean.getFecEnviomsj().substring(0,19));
                     docMensajeriaBean.setFePlaMsj(docMensajeriaBean.getFePlaMsj().substring(0,19));                     
                   
                     
                }
                else
                {
                docMensajeriaBean.setNumsj("0");
                }
                
                
                List<DocumentoRecepMensajeriaBean> docMesaVirtualList=documentoMensajeriaService.getLstDetalleMesaVirtual(docDestinoBean);
                DocumentoRecepMensajeriaBean docMesaVirtualBean=new DocumentoRecepMensajeriaBean();
                if(!docMesaVirtualList.isEmpty()){
                     docMesaVirtualBean= docMesaVirtualList.get(0);

                      docMensajeriaBean.setNumsj(docMesaVirtualBean.getSidEmiExt());
                     docMensajeriaBean.setDetipmsj(docMesaVirtualBean.getDetipmsj());
                     docMensajeriaBean.setReenvmsj(docMesaVirtualBean.getReenvmsj());
                     docMensajeriaBean.setDetipenv(docMesaVirtualBean.getDetipenv());
                     docMensajeriaBean.setDocEstadoMsj(docMesaVirtualBean.getDocEstadoMsj());
                     docMensajeriaBean.setFecEnviomsj(docMesaVirtualBean.getFecEnviomsj());
                     docMensajeriaBean.setFePlaMsj(docMesaVirtualBean.getFePlaMsj());                    
                     docMensajeriaBean.setArchivo_Cargo(docMesaVirtualBean.getArchivo_Cargo());   
                }
                else
                {
                    if(docMensajeriaBean.getNumsj()==null || docMensajeriaBean.getNumsj().isEmpty())
                        docMensajeriaBean.setNumsj("0");
                }
                
               if(docRemitoBean.getFeEmi()!=null && docRemitoBean.getFeEmi().length()>19) 
                        docRemitoBean.setFeEmi(docRemitoBean.getFeEmi().substring(0,19));                  
               
               if(docDestinoBean.getFeRecDoc()!=null && docDestinoBean.getFeRecDoc().length()>19){                   
                       docDestinoBean.setFeRecDoc(docDestinoBean.getFeRecDoc().substring(0,19));                     
                }
               if(docDestinoBean.getFeArcDoc()!=null && docDestinoBean.getFeArcDoc().length()>19){      
                docDestinoBean.setFeArcDoc(docDestinoBean.getFeArcDoc().substring(0,19));
               }
                List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(pNuAnn, pNuEmi);
                model.addAttribute("pkExp", pkExp);
                model.addAttribute("docRemitoBean", docRemitoBean);
                model.addAttribute("docDestinoBean", docDestinoBean);
                model.addAttribute("docAnexoList", docAnexoList);
                model.addAttribute("docMensajeriaBean", docMensajeriaBean);
                
                mensaje = "OK";

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = e.getMessage();
            }
        } else {
            mensaje = "Faltan Datos";
        }
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaSeguimientoDetalle";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }

    }

    @RequestMapping(method = RequestMethod.GET, params = "accion=goDocAnexoJson")
    private @ResponseBody String goDocAnexoJson(HttpServletRequest request, Model model) throws Exception {
        String retval = " ";
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                retval = anexoDocumentoService.getReferenciaJson(pNuAnn, pNuEmi, 3);
            } catch (Exception e) {
                retval = " ";
                e.printStackTrace();
            }

        }
        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accion=goDocAnexoRoot")
    private @ResponseBody String goDocAnexoRoot(HttpServletRequest request, Model model) throws Exception {
        String retval = " ";
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {

            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                retval = anexoDocumentoService.getReferenciaRoot(pNuAnn, pNuEmi, pNuDes, 3);
            } catch (Exception e) {
                retval = " ";
                e.printStackTrace();
            }

        }
        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accion=goDocSeguimientoRoot")
    private @ResponseBody String goDocSeguimientoRoot(HttpServletRequest request, Model model) throws Exception {
        String retval = " ";
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;

        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                retval = anexoDocumentoService.getSeguimientoRoot(pNuAnn, pNuEmi, pNuDes, 3);
            } catch (Exception e) {
                retval = " ";
                e.printStackTrace();
            }
        }
        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accion=goDocSeguimientoJson")
    private @ResponseBody String goDocSeguimientoJson(HttpServletRequest request, Model model) throws Exception {
        String retval = " ";
        String pkEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pkEmi");
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;
        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
            pNuDes = pkEmi.substring(14);
            try {
                retval = anexoDocumentoService.getSeguimientoJson(pNuAnn, pNuEmi, pNuDes, 3);
            } catch (Exception e) {
                retval = " ";
                e.printStackTrace();
            }
        }
        return retval;
    }
    
    LinkedList<DocumentoFileBean> files = new LinkedList<DocumentoFileBean>();
    DocumentoFileBean fileMeta = null;

    /**
     * *************************************************
     * URL: /rest/controller/upload upload(): receives files
     *
     * @param request : MultipartHttpServletRequest auto passed
     * @param response : HttpServletResponse auto passed
     * @return LinkedList<DocumentoFileBean> as json format
	 ***************************************************
     */
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpload")    
    public @ResponseBody String goUpload(MultipartHttpServletRequest request, HttpServletResponse response) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        String pNuAnn = null;
        String pNuEmi = null;
        String pkEmi = null;
        pkEmi = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pkEmi");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession((HttpServletRequest) request);
        String coUsu=usuario.getCoUsuario();
        
        String vreturn = "NO_OK";
        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
        }
        
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            //System.out.println(mpf.getOriginalFilename() + " uploaded! " + files.size());
            //2.3 create new fileMeta
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());
            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                vreturn=anexoDocumentoService.insArchivoAnexo(coUsu,pNuAnn, pNuEmi, fileMeta);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }

        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        if(vreturn.equals("NO_OK")){
            vreturn="Error al cargar el archivo en la base de datos.";
        }
        String id=fileMeta.getIdDocumento();
        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
//        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
//        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo);
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\",\"mensaje\":\"%s\"}]";
        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo,vreturn);
        return res;
        //return files;

    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadIE",produces = "text/plain; charset=utf-8")    
    public @ResponseBody String goUploadIE(MultipartHttpServletRequest request, HttpServletResponse response) {

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;

        String pNuAnn = null;
        String pNuEmi = null;
        String pkEmi = null;
        
        //pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pNuAnn");
        pkEmi = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pkEmi");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession((HttpServletRequest) request);
        String coUsu=usuario.getCoUsuario();
        String vreturn = "NO_OK";
        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
        }
        
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            //System.out.println(mpf.getOriginalFilename() + " uploaded! " + files.size());
            //2.3 create new fileMeta
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());
            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                vreturn=anexoDocumentoService.insArchivoAnexo(coUsu,pNuAnn, pNuEmi, fileMeta);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }
        if(vreturn.equals("NO_OK")){
            vreturn="Error al cargar el archivo en la base de datos.";
        }
        String id=fileMeta.getIdDocumento();
        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
//        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
//        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo);
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\",\"mensaje\":\"%s\"}]";
        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo,vreturn);
        return res;

    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadReplace")
    public @ResponseBody String goUploadReplace(MultipartHttpServletRequest request, HttpServletResponse response) {

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuAne = null;
        String pkEmi = null;
        
        //pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pNuAnn");
        pkEmi = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pkEmi");
        pNuAne = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pNuAne");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession((HttpServletRequest) request);
        String coUsu=usuario.getCoUsuario();
        
        String vreturn = "NO_OK";
        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
        }
        
        while (itr.hasNext()) {

            mpf = request.getFile(itr.next());
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());

            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                vreturn=anexoDocumentoService.updArchivoAnexo(coUsu,pNuAnn, pNuEmi,pNuAne, fileMeta);
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //2.4 add to files
            files.add(fileMeta);
     
        }
        if(vreturn.equals("NO_OK")){
            vreturn="Error al cargar el archivo en la base de datos.";
        }
        
        String id=fileMeta.getIdDocumento();
        //String nombreArchivo=fileMeta.getNombreArchivo();
        String nombreArchivo=anexoDocumentoService.getNombreArchivo(pNuAnn,pNuEmi,pNuAne);
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\",\"mensaje\":\"%s\"}]";
        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo,vreturn);
        
        
        return res;
        //return files;

    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadReplaceIE",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goUploadReplaceIE(MultipartHttpServletRequest request, HttpServletResponse response) {

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;

        String pNuAnn = null;
        String pNuEmi = null;
        String pNuAne = null;
        String pkEmi = null;
        
        //pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pNuAnn");
        pkEmi = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pkEmi");
        pNuAne = ServletUtility.getInstancia().loadRequestParameter((HttpServletRequest) request, "pNuAne");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession((HttpServletRequest) request);
        String coUsu=usuario.getCoUsuario();
        
        String vreturn = "NO_OK";
        if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
        }
        
        while (itr.hasNext()) {

            mpf = request.getFile(itr.next());
            //System.out.println(mpf.getOriginalFilename() + " uploaded! " + files.size());

          
            //2.3 create new fileMeta
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());

            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                vreturn=anexoDocumentoService.updArchivoAnexo(coUsu,pNuAnn, pNuEmi,pNuAne, fileMeta);
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //2.4 add to files
            files.add(fileMeta);
     
        }

        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        if(vreturn.equals("NO_OK")){
            vreturn="Error al cargar el archivo en la base de datos.";
        }
        String id=fileMeta.getIdDocumento();
        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
//        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
//        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo);
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\",\"mensaje\":\"%s\"}]";
        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo,vreturn);
        
        return res;
        //return files;

    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goActualizaDescripAnex")
    public String goActualizaDescripAnex(@RequestBody DocumentoAnexoBeansContenedor listaDocs,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String rowCount = null;
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        rowCount = ServletUtility.getInstancia().loadRequestParameter(request, "rowCount");
        
        try{
            mensaje=anexoDocumentoService.updAnexoDetalle(listaDocs,coUsu,rowCount);
        }catch(validarDatoException e){
           mensaje=(e.valorMsg);
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }        
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaCargaDoc")
    private @ResponseBody String goRutaCargaDoc(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        String pinFor = ServletUtility.getInstancia().loadRequestParameter(request, "inFor"); // Indica si va retornar el formato del documento

        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreCargaDoc(pNuAnn, pNuEmi, pTiOpe);

                retval.append("{\"retval\":\"");
                retval.append(docVerBean.getDeMensaje());
                retval.append("\",\"nuAnn\":\"");
                retval.append(docVerBean.getNuAnn());
                retval.append("\",\"nuEmi\":\"");
                retval.append(docVerBean.getNuEmi());
                retval.append("\",\"nuSecFirma\":\"");
                retval.append(docVerBean.getNuSecFirma());
                retval.append("\",\"coDoc\":\"");
                retval.append(docVerBean.getCoDocEmi());
                retval.append("\",\"noDoc\":\"");
                retval.append(docVerBean.getNoDocumento());
                retval.append("\",\"noFirma\":\"");
                retval.append(docVerBean.getNoFirma());
                retval.append("\",\"noUrl\":\"");
                retval.append(docVerBean.getUrlDocumento());
                if (pinFor!=null && pinFor.equals("1")){
                    retval.append("\",\"inFor\":\"");
                    retval.append(documentoObjService.getFormatoDoc(pNuAnn, pNuEmi, pTiOpe));
                }
                retval.append("\"}");
            } catch (Exception e) {
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
                e.printStackTrace();
            }

        }
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaFirmaDoc")
    private @ResponseBody String goRutaFirmaDoc(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        DocumentoVerBean docSession = new DocumentoVerBean();
        
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {
                ServletContext sc = request.getSession().getServletContext();
                String rutaBase = sc.getRealPath("/reports/");
                
                docSession = documentoObjService.getNombreFirmaDoc(pNuAnn, pNuEmi, pTiOpe, usuarioConfigBean, rutaBase);

                retval.append("{\"retval\":\"");
                retval.append(docSession.getDeMensaje());
                retval.append("\",\"nuAnn\":\"");
                retval.append(docSession.getNuAnn());
                retval.append("\",\"nuEmi\":\"");
                retval.append(docSession.getNuEmi());
                retval.append("\",\"inFirma\":\"");
                retval.append(docSession.getInTipoFirma());
                retval.append("\",\"nuSecFirma\":\"");
                retval.append(docSession.getNuSecFirma());
                retval.append("\",\"noPrefijo\":\"");
                retval.append(docSession.getNoPrefijo());
                retval.append("\",\"numeroDoc\":\"");
                retval.append(docSession.getNumeroDoc());
                retval.append("\",\"fechaFirma\":\"");
                retval.append(docSession.getFeFirma());
                retval.append("\",\"noDoc\":\"");
                retval.append(docSession.getNoDocumento());
                retval.append("\",\"noUrl\":\"");
                retval.append(docSession.getUrlDocumento());
                retval.append("\"}");
            } catch (Exception e) {
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
                e.printStackTrace();
            }

        }
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaCargaFirmaDoc")
    private @ResponseBody String goRutaCargaFirmaDoc(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        String pNuSecFirma = ServletUtility.getInstancia().loadRequestParameter(request, "nuSecFirma");
        String tipoDocumento = ServletUtility.getInstancia().loadRequestParameter(request, "tipoDocumento");
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null && pNuSecFirma != null ) {
            if (pNuSecFirma.equals(docSession.getNuSecFirma()) || tipoDocumento.equals("232")) {
                try {
                    DocumentoVerBean docVerBean = new DocumentoVerBean();
                    docVerBean = documentoObjService.getNombreCargaFirmaDoc(pNuAnn, pNuEmi, pTiOpe);
                    if(docVerBean.getDeMensaje().equals("OK")){
                        docSession.setDeMensaje(docVerBean.getDeMensaje());
                        docSession.setUrlDocumento(docVerBean.getUrlDocumento());
                        docSession.setNuSecFirma(docVerBean.getNuSecFirma());
                        
                        retval.append("{\"retval\":\"");
                        retval.append(docSession.getDeMensaje());
                        retval.append("\",\"nuAnn\":\"");
                        retval.append(docSession.getNuAnn());
                        retval.append("\",\"nuEmi\":\"");
                        retval.append(docSession.getNuEmi());
                        retval.append("\",\"nuSecFirma\":\"");
                        retval.append(docSession.getNuSecFirma());
                        retval.append("\",\"noFirma\":\"");
                        retval.append(docSession.getNoFirma());
                        retval.append("\",\"noUrl\":\"");
                        retval.append(docSession.getUrlDocumento());
                        retval.append("\"}");                        
                    }

                } catch (Exception e) {
                    retval.append("{\"retval\":");
                    retval.append("false");
                    retval.append("}");
                    e.printStackTrace();
                }
            }else{
                retval.append("{\"retval\":");
                retval.append("\"Documento ha cambiado, proceda a firmar nuevamente\"");
                retval.append("}");
            }
        }else{
            retval.append("{\"retval\":");
            retval.append("\"Error en Datos\"");
            retval.append("}");
        }
        
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocRutaAbrirEmi")
    private @ResponseBody String goDocRutaAbrirEmi(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");

        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {
                ServletContext sc = request.getSession().getServletContext();
                String rutaBase = sc.getRealPath("/reports/");

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoObjService.getNombreDocEmi(pNuAnn, pNuEmi, pTiOpe,usuario, rutaBase);

                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoFirma(docVerBean.getNoFirma());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());

            } catch (Exception e) {
                retRespuesta.setRetval("Error al obtener nombre del Documento");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
        }
        return retval;
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaVoBoDoc")
    private @ResponseBody String goRutaVoBoDoc(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        DocumentoVerBean docSession = new DocumentoVerBean();
        
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {
                
                ServletContext sc = request.getSession().getServletContext();
                String rutaBase = sc.getRealPath("/reports/");

                docSession = documentoObjService.getNombreVoBoDoc(pNuAnn, pNuEmi, pTiOpe, usuarioConfigBean, rutaBase);

                retval.append("{\"retval\":\"");
                retval.append(docSession.getDeMensaje());
                retval.append("\",\"nuAnn\":\"");
                retval.append(docSession.getNuAnn());
                retval.append("\",\"nuEmi\":\"");
                retval.append(docSession.getNuEmi());
                retval.append("\",\"inFirma\":\"");
                retval.append(docSession.getInTipoFirma());
                retval.append("\",\"nuSecFirma\":\"");
                retval.append(docSession.getNuSecFirma());
                retval.append("\",\"noPrefijo\":\"");
                retval.append(docSession.getNoPrefijo());
                retval.append("\",\"numeroDoc\":\"");
                retval.append(docSession.getNumeroDoc());
                retval.append("\",\"fechaFirma\":\"");
                retval.append(docSession.getFeFirma());
                retval.append("\",\"noDoc\":\"");
                retval.append(docSession.getNoDocumento());
                retval.append("\",\"noUrl\":\"");
                retval.append(docSession.getUrlDocumento());
                retval.append("\"}");
            } catch (Exception e) {
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
                e.printStackTrace();
            }

        }
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaCargaVoBoDoc")
    private @ResponseBody String goRutaCargaVoBoDoc(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        String pNuSecFirma = ServletUtility.getInstancia().loadRequestParameter(request, "nuSecFirma");
        
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null && pNuSecFirma != null ) {
            if (pNuSecFirma.equals(docSession.getNuSecFirma())) {
                try {
                    DocumentoVerBean docVerBean = new DocumentoVerBean();
                    docVerBean = documentoObjService.getNombreCargaVoBoDoc(pNuAnn, pNuEmi, pTiOpe);
                    if(docVerBean.getDeMensaje().equals("OK")){
                        docSession.setDeMensaje(docVerBean.getDeMensaje());
                        docSession.setUrlDocumento(docVerBean.getUrlDocumento());
                        docSession.setNuSecFirma(docVerBean.getNuSecFirma());
                        
                        retval.append("{\"retval\":\"");
                        retval.append(docSession.getDeMensaje());
                        retval.append("\",\"nuAnn\":\"");
                        retval.append(docSession.getNuAnn());
                        retval.append("\",\"nuEmi\":\"");
                        retval.append(docSession.getNuEmi());
                        retval.append("\",\"nuSecFirma\":\"");
                        retval.append(docSession.getNuSecFirma());
                        retval.append("\",\"noFirma\":\"");
                        retval.append(docSession.getNoFirma());
                        retval.append("\",\"noUrl\":\"");
                        retval.append(docSession.getUrlDocumento());
                        retval.append("\"}");                        
                    }

                } catch (Exception e) {
                    retval.append("{\"retval\":");
                    retval.append("false");
                    retval.append("}");
                    e.printStackTrace();
                }
            }else{
                retval.append("{\"retval\":");
                retval.append("\"Documento ha cambiado, proceda a firmar nuevamente\"");
                retval.append("}");
            }
        }else{
            retval.append("{\"retval\":");
            retval.append("\"Error en Datos\"");
            retval.append("}");
        }
        
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaFirmaDocAnexo")
    private @ResponseBody String goRutaFirmaDocAnexo(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        DocumentoVerBean docSession = new DocumentoVerBean();
        
        if (pNuAnn != null && pNuEmi != null && pNuAne != null) {
            try {

                docSession = documentoObjService.getNombreFirmaDocAnexo(pNuAnn, pNuEmi, pNuAne, pTiOpe, usuarioConfigBean);

                retval.append("{\"retval\":\"");
                retval.append(docSession.getDeMensaje());
                retval.append("\",\"nuAnn\":\"");
                retval.append(docSession.getNuAnn());
                retval.append("\",\"nuEmi\":\"");
                retval.append(docSession.getNuEmi());
                retval.append("\",\"inFirma\":\"");
                retval.append(docSession.getInTipoFirma());
                retval.append("\",\"nuSecFirma\":\"");
                retval.append(docSession.getNuSecFirma());
                retval.append("\",\"noPrefijo\":\"");
                retval.append(docSession.getNoPrefijo());
                retval.append("\",\"numeroDoc\":\"");
                retval.append(docSession.getNumeroDoc());
                retval.append("\",\"fechaFirma\":\"");
                retval.append(docSession.getFeFirma());
                retval.append("\",\"noDoc\":\"");
                retval.append(docSession.getNoDocumento());
                retval.append("\",\"noUrl\":\"");
                retval.append(docSession.getUrlDocumento());
                retval.append("\"}");
            } catch (Exception e) {
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
                e.printStackTrace();
            }

        }
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaCargaFirmaDocAnexo")
    private @ResponseBody String goRutaCargaFirmaDocAnexo(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        String pNuSecFirma = ServletUtility.getInstancia().loadRequestParameter(request, "nuSecFirma");
        
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null && pNuSecFirma != null && pNuAne != null) {
            if (pNuSecFirma.equals(docSession.getNuSecFirma())) {
                try {
                    DocumentoVerBean docVerBean = new DocumentoVerBean();
                    docVerBean = documentoObjService.getNombreCargaFirmaDocAnexo(pNuAnn, pNuEmi, pNuAne, pTiOpe);
                    if(docVerBean.getDeMensaje().equals("OK")){
                        docSession.setDeMensaje(docVerBean.getDeMensaje());
                        docSession.setUrlDocumento(docVerBean.getUrlDocumento());
                        docSession.setNuSecFirma(docVerBean.getNuSecFirma());
                        
                        retval.append("{\"retval\":\"");
                        retval.append(docSession.getDeMensaje());
                        retval.append("\",\"nuAnn\":\"");
                        retval.append(docSession.getNuAnn());
                        retval.append("\",\"nuEmi\":\"");
                        retval.append(docSession.getNuEmi());
                        retval.append("\",\"nuSecFirma\":\"");
                        retval.append(docSession.getNuSecFirma());
                        retval.append("\",\"noFirma\":\"");
                        retval.append(docSession.getNoFirma());
                        retval.append("\",\"noUrl\":\"");
                        retval.append(docSession.getUrlDocumento());
                        retval.append("\"}");                        
                    }

                } catch (Exception e) {
                    retval.append("{\"retval\":");
                    retval.append("false");
                    retval.append("}");
                    e.printStackTrace();
                }
            }else{
                retval.append("{\"retval\":");
                retval.append("\"Documento ha cambiado, proceda a firmar nuevamente\"");
                retval.append("}");
            }
        }else{
            retval.append("{\"retval\":");
            retval.append("\"Error en Datos\"");
            retval.append("}");
        }
        
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargaDocAnexo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goCargaDocAnexo(HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");
        String pNuSecFirma = ServletUtility.getInstancia().loadRequestParameter(request, "nuSecFirma");
        
       pNuSecFirma=applicationProperties.getRutaTemporal()+"//"+pNuSecFirma;
       try{
          mensaje = documentoObjService.cargaDocAnexo(pNuAnn,pNuEmi,pNuAne,pNuSecFirma,usuario.getCoUsuario(),docSession.getNoDocumento());

       }catch(validarDatoException e){
           mensaje=(e.valorMsg);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"}");
       } else {
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
    }    

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDocRutaAbrirEmiReporte")
    public @ResponseBody String goDocRutaAbrirEmiReporte(HttpServletRequest request, Model model)
    {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        String pAbreWord = ServletUtility.getInstancia().loadRequestParameter(request, "pAbreWord");
        pAbreWord = pAbreWord.equals("")?"NO":pAbreWord;  

        String rutaBase = "";
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {
                ServletContext sc = request.getSession().getServletContext();
                rutaBase = sc.getRealPath("/reports/");
                DocumentoVerBean docVerBean = new DocumentoVerBean();
                //docVerBean = documentoObjService.getNombreDocEmi(pNuAnn, pNuEmi, pTiOpe,usuario);
                //documentoObjService.getNombreDoc(pNuAnn, pNuEmi, pTiOpe, usuario);
                docVerBean = documentoObjService.getNombreDocEmiReporte(pNuAnn, pNuEmi, pTiOpe,usuario,rutaBase);
                
                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
                                
                if(pAbreWord.equals("SI")){
                    System.out.println(docVerBean.getNombreDocumentoWord());
                    retRespuesta.setNoDoc(docVerBean.getNombreDocumentoWord());
                }else{
                    retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                }
                    
                
//                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoFirma(docVerBean.getNoFirma());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());

            } catch (Exception e) {
                retRespuesta.setRetval("Error al obtener nombre del Documento");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
        }
        return retval;
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaFirmaDocReporte")
    private @ResponseBody String goRutaFirmaDocReporte(HttpServletRequest request, Model model) throws Exception {
        StringBuilder retval = new StringBuilder();
        
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pTiOpe = ServletUtility.getInstancia().loadRequestParameter(request, "tiOpe");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        
        
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        DocumentoVerBean docSession = new DocumentoVerBean();
        String rutaBase = "";
        if (pNuAnn != null && pNuEmi != null && pTiOpe != null) {
            try {
                ServletContext sc = request.getSession().getServletContext();
                rutaBase = sc.getRealPath("/reports/");
                DocumentoVerBean docVerBean = new DocumentoVerBean();    
                docSession = documentoObjService.getNombreFirmaDocReporte(pNuAnn, pNuEmi, pTiOpe, usuarioConfigBean, rutaBase);

                retval.append("{\"retval\":\"");
                retval.append(docSession.getDeMensaje());
                retval.append("\",\"nuAnn\":\"");
                retval.append(docSession.getNuAnn());
                retval.append("\",\"nuEmi\":\"");
                retval.append(docSession.getNuEmi());
                retval.append("\",\"inFirma\":\"");
                retval.append(docSession.getInTipoFirma());
                retval.append("\",\"nuSecFirma\":\"");
                retval.append(docSession.getNuSecFirma());
                retval.append("\",\"noPrefijo\":\"");
                retval.append(docSession.getNoPrefijo());
                retval.append("\",\"numeroDoc\":\"");
                retval.append(docSession.getNumeroDoc());
                retval.append("\",\"fechaFirma\":\"");
                retval.append(docSession.getFeFirma());
                retval.append("\",\"noDoc\":\"");
                retval.append(docSession.getNoDocumento());
                retval.append("\",\"cargoEmpleado\":\"");
                retval.append(docSession.getCargoEmpleado());
                //YUAL
                retval.append("\",\"dni\":\"");
                retval.append(usuario.getNuDni());
                retval.append("\",\"coUsuario\":\"");
                retval.append(usuario.getCoUsuario());
                
                //Traer el cod de verificación
                DocumentoDatoBean oDocumentoDatoBean= new DocumentoDatoBean();
                oDocumentoDatoBean= documentoObjService.getDatosDoc(pNuAnn, pNuEmi);
                  retval.append("\",\"coVerExt\":\"");
                retval.append(oDocumentoDatoBean.getCoVerExt());
                //END YUAL
                retval.append("\",\"noUrl\":\"");
                retval.append(docSession.getUrlDocumento());
                retval.append("\"}");
            } catch (Exception e) {
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
                e.printStackTrace();
            }

        }
        model.addAttribute("docSession",docSession);
        return retval.toString();
    }
    
      @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdEnvioNotificacion", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goUpdEnvioNotificacion(HttpServletRequest request, Model model) {
        //String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        //String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
        String nCodAccion = ServletUtility.getInstancia().loadRequestParameter(request, "nCodAccion");
        String nCodUrgencia = ServletUtility.getInstancia().loadRequestParameter(request, "nCodUrgencia");
        String nCodDepMsj = ServletUtility.getInstancia().loadRequestParameter(request, "nCodDepMsj");
        
        
          
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String msg[] = new String[2];
        try {
          DocumentoEmiBean documentoEmiBean= new DocumentoEmiBean();
          documentoEmiBean.setNuEmi(pnuEmi);
          documentoEmiBean.setNuAnn(pnuAnn);
          //documentoEmiBean.setEsDocEmi(nCodAccion);
          documentoEmiBean.setTiEnvMsj(nCodAccion);
          documentoEmiBean.setCoPrioridad(nCodUrgencia);
          documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
          if(nCodAccion.equals("0"))
          {
           documentoEmiBean.setCoDepEmi(nCodDepMsj);
          }
          else
          { documentoEmiBean.setCoDepEmi(usuario.getCoDep());
          }
              
                 
                    
            String vReturn=EmiDocumentoAdmService.changeToEnvioNotificacion(documentoEmiBean,usuario);
            if(vReturn.equals("OK")){
                msg[0]="1";
                msg[1]="Datos Guardados";
            } 
            
            else{
                if (vReturn.equals("NO_OK"))
                {                       
                msg[0]="0";
                msg[1]="Verificar sino existen archivos registrados";                    
                }
                else
                {
                msg[0]="0";
                msg[1]="Error al actualizar el registro:"+vReturn;                      
                }    

                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg[0] = "0";
            msg[1] = "Error en el metodo";
        }
        StringBuilder retval = new StringBuilder();
       // retval.append("{\"coRespuesta\":\"");
        retval.append(msg[0]);
       // retval.append("\",\"deRespuesta\":\"");
        retval.append(msg[1]);
        //retval.append("\"}");
        return retval.toString();
    }
    /*interoperabilidad*/
   @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdEnvioMesaVirtual", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goUpdEnvioMesaVirtual(HttpServletRequest request, Model model) {
        //String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        //String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
        String nDeDepDes= ServletUtility.getInstancia().loadRequestParameter(request, "nDeDepDes");
        String nDeNomDes = ServletUtility.getInstancia().loadRequestParameter(request, "nDeNomDes");
        String nDeCarDes = ServletUtility.getInstancia().loadRequestParameter(request, "nDeCarDes"); 
          
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String msg[] = new String[2];
        
        try {
          
          DatosInterBean datosInter=new DatosInterBean();
          
          datosInter.setNuEmi(pnuEmi);
          datosInter.setNuAnn(pnuAnn);
          //documentoEmiBean.setEsDocEmi(nCodAccion);
          datosInter.setDeDepDes(nDeDepDes);
          datosInter.setDeNomDes(nDeNomDes);
          datosInter.setDeCarDes(nDeCarDes);
          datosInter.setCoUseMod(usuario.getCoUsuario());
             
                 
                    
            String vReturn=EmiDocumentoAdmService.changeToEnvioMesaVirtual(datosInter,usuario);
            
            if(vReturn.equals("OK")){
                msg[0]="1";
                msg[1]="Datos Guardados";
               //YUAL  -NOTIFICACION
               notiService.getLsMesaVirtualNotificar(datosInter.getNuEmi(),datosInter.getNuAnn(),"");
        
                
            } 
            
            else{
                if (vReturn.equals("NO_OK"))
                {                       
                msg[0]="0";
                msg[1]="Verificar sino existen archivos registrados";                    
                }
                else
                {
                msg[0]="0";
                msg[1]="Error al actualizar el registro:"+vReturn;                      
                }    

                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg[0] = "0";
            msg[1] = "Error en el metodo";
        }
        StringBuilder retval = new StringBuilder();
       // retval.append("{\"coRespuesta\":\"");
        retval.append(msg[0]);
       // retval.append("\",\"deRespuesta\":\"");
        retval.append(msg[1]);
        //retval.append("\"}");
        return retval.toString();
    }
    /*interoperabilidad*/
}
