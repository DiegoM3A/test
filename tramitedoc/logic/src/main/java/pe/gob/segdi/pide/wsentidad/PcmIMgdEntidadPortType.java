/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.pide.wsentidad;

import java.util.List;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;
import pe.gob.segdi.wsentidad.ws.EntidadBean;
import pe.gob.segdi.wsentidad.ws.ObjectFactory;

/**
 *
 * @author mvaldera
 */
@WebService(name = "PcmIMgdEntidadPortType", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface PcmIMgdEntidadPortType {


    /**
     * 
     * @param rucent
     * @return
     *     returns java.util.List<pe.gob.segdi.wsentidad.ws.EntidadBean>
     */

    
    @WebMethod(action = "validarEntidad")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "validarEntidad", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/", className = "pe.gob.segdi.pide.wsentidad.validarEntidad")
    @ResponseWrapper(localName = "validarEntidadResponse", targetNamespace = "http://ws.wsentidad.segdi.gob.pe/", className = "pe.gob.segdi.pide.wsentidad.validarEntidadResponse")
    public String validarEntidad(
        @WebParam(name = "vrucent", targetNamespace = "")
        String rucent);

}
