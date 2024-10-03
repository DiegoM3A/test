
package pe.gob.segdi.pide.wsentidad.qa;

import java.util.List;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Entidad", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Entidad {
 
    /**
     * 
     * @param vrucent
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "validarEntidad", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/", className = "pe.gob.segdi.pide.wsentidad.validarEntidad")
    @ResponseWrapper(localName = "validarEntidadResponse", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/", className = "pe.gob.segdi.pide.wsentidad.validarEntidadResponse")
    public String validarEntidad(
        @WebParam(name = "vrucent", targetNamespace = "")
        String vrucent);
 

}
