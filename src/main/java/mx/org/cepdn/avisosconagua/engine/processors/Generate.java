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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.org.cepdn.avisosconagua.engine.Processor;
import mx.org.cepdn.avisosconagua.mongo.CAPFileGenerator;
import mx.org.cepdn.avisosconagua.mongo.HtmlZipGenerator;
import mx.org.cepdn.avisosconagua.mongo.MongoInterface;
import mx.org.cepdn.avisosconagua.util.Utils;

/**
 *
 * @author serch
 */
public class Generate implements Processor {
    private static final String ADVICE_ID = "internalId";

    @Override
    public void invokeForm(HttpServletRequest request, HttpServletResponse response, BasicDBObject data, String[] parts) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String adviceID = (String)request.getSession(true).getAttribute(ADVICE_ID);
        CAPFileGenerator capFile = new CAPFileGenerator(adviceID);
        capFile.generate();
        if (capFile.isOK()){
            request.setAttribute("capURL", capFile.getLink());
        }
        HtmlZipGenerator htmlFile = new HtmlZipGenerator(adviceID);
        htmlFile.generate();
        if (htmlFile.isOK()){
            request.setAttribute("htmlUrl",htmlFile.getLink());
        }
        request.setAttribute("titulo", Utils.getTituloBoletin(parts[2]));
        MongoInterface.getInstance().setGenerated(adviceID,htmlFile.getPrevious(),htmlFile.getTitle(), parts[2], capFile.getDate());
        request.setAttribute("isdp", parts[2].endsWith("dp"));
        request.setAttribute("bulletinType", parts[2]);
        RequestDispatcher rd = request.getRequestDispatcher("/jsp/finish.jsp");
        rd.forward(request, response);
    }

    @Override
    public void processForm(HttpServletRequest request, String[] parts, String currentId) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
