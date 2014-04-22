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
package mx.org.cedn.avisosconagua.engine;

import com.mongodb.BasicDBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.org.cedn.avisosconagua.mongo.MongoInterface;

/**
 *
 * @author serch
 */
@WebServlet(name = "Controler", urlPatterns = {"/ctrl/*"})
public class Controler extends HttpServlet {

    private static final HashMap<String, List<String>> control = new HashMap<>();
    private static final HashMap<String, Processor> processors = new HashMap<>();
    private static final String ADVICE_ID = "internalId";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(Controler.class.getResourceAsStream("/types.properties")))) {
            String linea;
            while ((linea = input.readLine()) != null) {
                String[] kv = linea.split("=");
                if (kv.length == 2) {
                    String[] val = kv[1].split(",");
                    List<String> list = Arrays.asList(val);
                    control.put(kv[0], list);
                    for (String proc : list) {
                        if (!processors.containsKey(proc)) {
                            String clazzName = "mx.org.cedn.avisosconagua.engine.processors." + proc.substring(0, 1).toUpperCase() + proc.substring(1);
                            Class clazz = Class.forName(clazzName);
                            Processor procObj = (Processor) clazz.newInstance();
                            processors.put(proc, procObj);
                        }
                    }
                }

            }
        } catch (IOException | ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String currentId = (String) request.getSession(true).getAttribute(ADVICE_ID);
        String[] parts = request.getRequestURI().split("/");
        if (parts.length > 3 && control.containsKey(parts[2])) {
            List<String> flujo = control.get(parts[2]);
            BasicDBObject datos = null;
            if (flujo.contains(parts[3])) {
                if (!"capgen".equals(parts[2])) {
                    processors.get(parts[3]).invokeForm(request, response,
                            null, parts);
                    return;
                }
                if (parts.length > 4 && !"new".equals(parts[4])) {
                    datos = MongoInterface.getInstance().getAdvice(parts[4]);
                    if (null != datos) {
                        currentId = parts[4];
                        request.getSession(true).setAttribute(ADVICE_ID, currentId);
                        response.sendRedirect("/" + parts[1] + "/" + parts[2] + "/" + parts[3]);
                        return;
                    }
                }
                if ((null == currentId && parts[3].equals(flujo.get(0))) || (parts.length > 4 && "new".equals(parts[4]))) {
                    currentId = UUID.randomUUID().toString();
                    request.getSession(true).setAttribute(ADVICE_ID, currentId);
                    MongoInterface.getInstance().createNewAdvice(currentId, parts[2]);
                    response.sendRedirect("/" + parts[1] + "/" + parts[2] + "/" + parts[3]);
                    return;
                }
                if (null == datos) {
                    datos = MongoInterface.getInstance().getAdvice(currentId);
                }

                cleanAttributes(request);
                processors.get(parts[3]).invokeForm(request, response,
                        (BasicDBObject) datos.get(parts[3]), parts);

            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String currentId = (String) request.getSession(true).getAttribute(ADVICE_ID);
        if (null == currentId) {
            response.sendError(500, "Call outside the flow");
        }
        String[] parts = request.getRequestURI().split("/");
        if (parts.length > 3 && control.containsKey(parts[2])
                && control.get(parts[2]).contains(parts[3])) {
            processors.get(parts[3]).processForm(request, parts, currentId);
            response.sendRedirect("/ctrl/" + parts[2] + "/" + getNext(parts));
        } else {
            response.sendError(404);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void cleanAttributes(HttpServletRequest request) {
        Enumeration<String> en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String act = en.nextElement();
            request.removeAttribute(act);
        }
    }

    private String getNext(String[] parts) {
        List<String> flujo = control.get(parts[2]);
        int idx = flujo.indexOf(parts[3]) + 1;
        if (idx < flujo.size()) {
            return flujo.get(idx);
        } else {
            return null;
        }
    }

}
