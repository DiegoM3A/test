/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.pe.oti.pcm.rest;

import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.gob.onpe.libreria.json.JSONException;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;

/**
 *
 * @author ypino

@Path("/DocumentoRestService") */
@ApplicationPath("resources")
public class DocumentoRestService extends Application{
/*
      private static final long serialVersionUID = 1L;
        @GET
        
        @Produces("application/json")
        public Response getUsers() throws JSONException {
                List<User> users= new ArrayList<User>();
                users.add(new User("admin"));
                users.add(new User("john"));
                users.add(new User("usuario2"));
                return Response.status(200).entity(users.toString()).build();
        }
    */
//    
//    @POST
//    @Path("/validarUsuario")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_JSON})
//    public DocumentoObjBean validarUsuario(DocumentoObjBean objDocumento)
//    {
//        objDocumento.setDeRespuesta("0");
//        if(objDocumento.getCoUseMod().equals("0300"))
//        {
//            objDocumento.setDeRespuesta("1");
//        }
//        return objDocumento;
//    }
//
//    @POST
//    @Path("/lista")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_JSON})
//    public String lista()
//    {  
//        return "1";
//    }    
}
