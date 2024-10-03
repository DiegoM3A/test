package pe.gob.segdi.pide.consultas.ws;

import pe.gob.onpe.tramitedoc.util.Constantes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.service.DatosPlantillaService;
import pe.gob.segdi.pide.consultas.bean.ConsultaDniBean;

public class WSConsultaDni {

    public ConsultaDniBean consultaDni(String Url, String nuDniConsulta, String nuDniUsuario, String rucEntidad, String password)
            throws SAXException, IOException, ParserConfigurationException {

        ConsultaDniBean resultado = new ConsultaDniBean();

        String resource = Url + "nuDniConsulta=" + nuDniConsulta
                + "&nuDniUsuario=" + nuDniUsuario
                + "&nuRucUsuario=" + rucEntidad
                + "&password=" + password;

        try {
            // create HTTP Client
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create new getRequest with below mentioned URL
            HttpGet getRequest = new HttpGet(resource);

            // Add additional header to getRequest which accepts application/xml data
            getRequest.addHeader("accept", "application/xml");

            // Execute your request and catch response
            HttpResponse response = httpClient.execute(getRequest);

            // Check for HTTP response code: 200 = success
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            // Get-Capture Complete application/xml body response
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String output;
            StringBuilder cadena = new StringBuilder();

            // Simply iterate through XML response and show on console.
            while ((output = br.readLine()) != null) {
                cadena.append(output);
            }

            // Parse the String XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new java.io.StringReader(cadena.toString()));
            Document doc = builder.parse(is);

            NodeList nodeLst = doc.getElementsByTagName("datosPersona");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element eleProducto = (Element) nodeLst.item(i);

                NodeList nlsApPrimer = eleProducto.getElementsByTagName("apPrimer");
                Element eleAprimer = (Element) nlsApPrimer.item(0);
                resultado.setApPrimer(eleAprimer.getFirstChild().getNodeValue());

                NodeList nlsApSegundo = eleProducto.getElementsByTagName("apSegundo");
                Element eleApSegundo = (Element) nlsApSegundo.item(0);
                resultado.setApSegundo(eleApSegundo.getFirstChild().getNodeValue());

                NodeList nlsPrenombres = eleProducto.getElementsByTagName("prenombres");
                Element elePrenombres = (Element) nlsPrenombres.item(0);
                resultado.setPrenombres(elePrenombres.getFirstChild().getNodeValue());
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return resultado;
    }
}