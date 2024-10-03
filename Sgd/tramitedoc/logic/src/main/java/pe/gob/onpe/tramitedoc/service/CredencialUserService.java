/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;

/**
 *
 * @author Jhon
 */
public interface CredencialUserService {
    public List<AdmEmpleadoBean> goListaBusqUsuario(String indicador, String estado) throws Exception;
    public List<SiElementoBean> goListaBusqCredencialUser(String estado) throws Exception;
    public String updCredencial(SiElementoBean user, String codUsuario);
    public String insCredencial(SiElementoBean user, String codUsuario);
    public SiElementoBean getCredencial();
    public List<SiElementoBean> getUsuario(String usuario);
    public List<SiElementoBean> getCredencialIni();
    public String insCredencialUser(SiElementoBean userCredencial, String codUsuario);
    public String updCredencialUser(SiElementoBean userCredencial, String codUsuario);
   
}
