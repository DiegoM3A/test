/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.pide.wsentidad;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * @author mvaldera
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarEntidad", propOrder = {
    "vrucent"
})
public class validarEntidad {
    String vrucent;

    public String getVrucent() {
        return vrucent;
    }

    public void setVrucent(String vrucent) {
        this.vrucent = vrucent;
    }
    
}
