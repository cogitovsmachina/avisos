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
package mx.org.cedn.avisosconagua.engine.processors;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.org.cedn.avisosconagua.engine.Processor;
import mx.org.cedn.avisosconagua.mongo.MongoInterface;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author serch
 */
public class Init implements Processor {
    private static final MongoInterface mi = MongoInterface.getInstance();

    private static final int MAX_SIZE = 1 * 1024 * 1024;

    @Override
    public void invokeForm(HttpServletRequest request, HttpServletResponse response, BasicDBObject data, String parts[]) throws ServletException, IOException {
        HashMap<String, String> datos = new HashMap<>();
        if (null != data) {
            for (String key : data.keySet()) {
                datos.put(key, data.getString(key));
            }
        }
        
        //Put nhcLinks in map and get advisory for tracking
        BasicDBObject advice = mi.getAdvice((String)request.getSession(true).getAttribute("internalId"));
        if (null != advice) {
            //Get nhcLinks
            BasicDBObject section = (BasicDBObject) advice.get("precapture");
            if (null != section) {
                datos.put("nhcForecastLink", section.getString("nhcForecastLink"));
                datos.put("nhcPublicLink", section.getString("nhcPublicLink"));
            }
        }
        
        request.setAttribute("data", datos);
        request.setAttribute("bulletinType", parts[2]);
        String url = "/jsp/init.jsp";
        if (parts[2].endsWith("dp")) {
            url = "/jsp/initdp.jsp";
        }
        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }

    @Override
    public void processForm(HttpServletRequest request, String[] parts, String currentId) throws ServletException, IOException {
        HashMap<String, String> parametros = new HashMap<>();
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MAX_SIZE);
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(MAX_SIZE);
            List<FileItem> items = upload.parseRequest(request);
            String filename = null;
            for (FileItem item : items) {
                if (!item.isFormField() && item.getSize()>0) {
                    filename = processUploadedFile(item, currentId);
                    //System.out.println("poniendo: "+ item.getFieldName() + "=" +filename);
                    parametros.put(item.getFieldName(), filename);
                } else {
//                    System.out.println("item:" + item.getFieldName() + "=" + new String(item.getString().getBytes("ISO8859-1")));
//                    parametros.put(item.getFieldName(), new String(item.getString().getBytes("ISO8859-1")));
//                    System.out.println("item:" + item.getFieldName() + "=" + item.getString());
//                    System.out.println("item:" + item.getFieldName() + "=" + new String(item.getString().getBytes("ISO8859-1"),"UTF-8"));
//                    System.out.println("item:" + item.getFieldName() + "=" + new String(item.getString().getBytes("ISO8859-1")));
//                    System.out.println("item:" + item.getFieldName() + "=" + new String(item.getString().getBytes("UTF-8"),"UTF-8"));
                    parametros.put(item.getFieldName(), new String(item.getString().getBytes("ISO8859-1"),"UTF-8"));
                }
            }
            } catch (FileUploadException fue){
                fue.printStackTrace();
            }
        } else {
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
//                try {
//                    parametros.put(entry.getKey(), new String(request.getParameter(entry.getKey()).getBytes("ISO8859-1")));
//                } catch (UnsupportedEncodingException ue) {
//                    //No debe llegar a este punto
//                    assert false;
//                }
                parametros.put(entry.getKey(), request.getParameter(entry.getKey()));
            }
        }
        BasicDBObject anterior = (BasicDBObject)mi.getAdvice(currentId).get(parts[3]);
        procesaAreas(parametros, anterior);
        procesaImagen(parametros, anterior);
        MongoInterface.getInstance().savePlainData(currentId, parts[3], parametros);
    }

    private String processUploadedFile(FileItem item, String currentId) throws IOException {
        System.out.println("file: size="+item.getSize()+" name:"+item.getName());
        GridFS gridfs = mi.getImagesFS();
        String filename = currentId + ":" + item.getFieldName() + "_" + item.getName();
        gridfs.remove(filename);
        GridFSInputFile gfsFile = gridfs.createFile(item.getInputStream());
        gfsFile.setFilename(filename);
        gfsFile.setContentType(item.getContentType());
        gfsFile.save();
        return filename;
    }
    
    
    private void procesaAreas(HashMap<String, String> nuevo, BasicDBObject anterior) {
        HashMap<String, String> cambios = new HashMap<>();
        for (String key:nuevo.keySet()){
            if (key.startsWith("area-")){
                String states = "states" + key.substring(4);
                String municipalities = "municipalities" + key.substring(4);
                if (null==nuevo.get(states)||null==nuevo.get(municipalities)){
                    if (((String)nuevo.get(key)).equals(anterior.get(key))){
                        if (null!=anterior.get(states)){
                            cambios.put(states, (String)anterior.get(states));
                        }
                        if (null!=anterior.get(municipalities)){
                            cambios.put(municipalities, (String)anterior.get(municipalities));
                        }
                    }
                }
                    
            }
        }
        if (!cambios.isEmpty()){
            nuevo.putAll(cambios);
        }
    }

    private void procesaImagen(HashMap<String, String> parametros, BasicDBObject anterior) {
        if (null == parametros.get("issueSateliteImg") || "".equals(parametros.get("issueSateliteImg").trim())){
            if(null!=anterior.getString("issueSateliteImg")){
                System.out.println("Arreglando: issueSateliteImg="+anterior.getString("issueSateliteImg"));
                parametros.put("issueSateliteImg", anterior.getString("issueSateliteImg"));
            }
        }
    }
}
