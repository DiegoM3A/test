/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author ecueva
 */
public interface ReporteService {
    void ejecutaReporte(HttpServletRequest request, HttpServletResponse response , String pdataSource);    
    void ejecutaReporteLista(HttpServletRequest request, HttpServletResponse response );    
    void abrirReporteTemporal(HttpServletRequest request, HttpServletResponse response );
}
