/*
 * SemanticWebBuilder es una plataforma para el desarrollo de portales y aplicaciones de integración,
 * colaboración y conocimiento, que gracias al uso de tecnología semántica puede generar contextos de
 * información alrededor de algún tema de interés o bien integrar información y aplicaciones de diferentes
 * fuentes, donde a la información se le asigna un significado, de forma que pueda ser interpretada y
 * procesada por personas y/o sistemas, es una creación original del Fondo de Información y Documentación
 * para la Industria INFOTEC, cuyo registro se encuentra actualmente en trámite.
 * 
 * INFOTEC pone a su disposición la herramienta SemanticWebBuilder a través de su licenciamiento abierto al público (‘open source’),
 * en virtud del cual, usted podrá usarlo en las mismas condiciones con que INFOTEC lo ha diseñado y puesto a su disposición;
 * aprender de él; distribuirlo a terceros; acceder a su código fuente y modificarlo, y combinarlo o enlazarlo con otro software,
 * todo ello de conformidad con los términos y condiciones de la LICENCIA ABIERTA AL PÚBLICO que otorga INFOTEC para la utilización
 * del SemanticWebBuilder 4.0.
 * 
 * INFOTEC no otorga garantía sobre SemanticWebBuilder, de ninguna especie y naturaleza, ni implícita ni explícita,
 * siendo usted completamente responsable de la utilización que le dé y asumiendo la totalidad de los riesgos que puedan derivar
 * de la misma.
 * 
 * Si usted tiene cualquier duda o comentario sobre SemanticWebBuilder, INFOTEC pone a su disposición la siguiente
 * dirección electrónica:
 * http://www.semanticwebbuilder.org
 */

package mx.org.cepdn.avisosconagua.engine.processors;

import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.org.cepdn.avisosconagua.engine.Processor;
import mx.org.cepdn.avisosconagua.mongo.MongoInterface;

/**
 *
 * @author serch
 */
public class Seguimiento implements Processor {

    @Override
    public void invokeForm(HttpServletRequest request, HttpServletResponse response, BasicDBObject data, String parts[]) throws ServletException, IOException {
        MongoInterface mi = MongoInterface.getInstance();
        HashMap<String, String> datos = new HashMap<>();
        if (null != data) {
            for (String key : data.keySet()) {
                datos.put(key, data.getString(key));
                //System.out.println("colocando: "+key+" : "+datos.get(key));
            }
        }
        
        //Get previously saved data to populate first row
        HashMap<String, String> seguimiento = new HashMap<>();
        BasicDBObject advice = mi.getAdvice((String)request.getSession(true).getAttribute("internalId"));
        if (null != advice) {
            //Add data from init section
            BasicDBObject section = (BasicDBObject) advice.get("init");
            if (null != section) {
                for (String key : section.keySet()) {
                    seguimiento.put(key, section.getString(key));
                }
            }
            
            //Add data from tracking section
            section = (BasicDBObject) advice.get("seguimiento");
            if (null != section) {
                for (String key : section.keySet()) {
                    seguimiento.put(key, section.getString(key));
                }
            }
        }
        
        request.setAttribute("data", datos);
        request.setAttribute("trackData", seguimiento);
        request.setAttribute("advicesList", mi.getPublisedAdvicesList());
        request.setAttribute("bulletinType", parts[2]);
        String url = "/jsp/tracking.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }

    @Override
    public void processForm(HttpServletRequest request, String[] parts, String currentId) throws ServletException, IOException {
        HashMap<String, String> parametros = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            try {
                parametros.put(entry.getKey(), new String(request.getParameter(entry.getKey()).getBytes("ISO8859-1")));
            } catch (UnsupportedEncodingException ue) {
                //No debe llegar a este punto
                assert false;
            }
        }
        MongoInterface.getInstance().savePlainData(currentId, parts[3], parametros);
    }
    
}
