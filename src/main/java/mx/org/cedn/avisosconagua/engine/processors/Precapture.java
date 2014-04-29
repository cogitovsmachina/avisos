/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.org.cedn.avisosconagua.engine.processors;

import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.org.cedn.avisosconagua.engine.Processor;
import mx.org.cedn.avisosconagua.mongo.MongoInterface;

/**
 *
 * @author Hasdai Pacheco
 */
public class Precapture implements Processor {

    @Override
    public void invokeForm(HttpServletRequest request, HttpServletResponse response, BasicDBObject data, String[] parts) throws ServletException, IOException {
        HashMap<String, String> datos = new HashMap<>();
        if (null != data) {
            for (String key : data.keySet()) {
                datos.put(key, data.getString(key));
                //System.out.println("colocando: "+key+" : "+datos.get(key));
            }
        }
        request.setAttribute("data", datos);
        request.setAttribute("bulletinType", parts[2]);
        request.setAttribute("advicesList", MongoInterface.getInstance().getPublisedAdvicesList(parts[2]));
        String url = "/jsp/precapture.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }

    @Override
    public void processForm(HttpServletRequest request, String[] parts, String currentId) throws ServletException, IOException {
        HashMap<String, String> parametros = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            try {
                parametros.put(entry.getKey(), new String(request.getParameter(entry.getKey()).getBytes("ISO8859-1"),"UTF-8"));
            } catch (UnsupportedEncodingException ue) {
                //No debe llegar a este punto
                assert false;
            }
        }
        MongoInterface.getInstance().savePlainData(currentId, parts[3], parametros);
    }
    
}
