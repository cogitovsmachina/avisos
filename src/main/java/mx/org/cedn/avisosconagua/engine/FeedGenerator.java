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

import com.google.publicalerts.cap.Alert;
import java.util.ArrayList;
import java.util.Date;
import mx.org.cedn.avisosconagua.mongo.CAPGenerator;
import mx.org.cedn.avisosconagua.mongo.MongoInterface;

/**
 * Utility class to generate a RSS or ATOM Feed for CONAGUA.
 * @author Hasdai Pacheco
 */
public class FeedGenerator {
    private FeedWriter feedWriter;
    private static MongoInterface mi = MongoInterface.getInstance();

    public FeedGenerator() {
        feedWriter = new FeedWriter("atom_1.0", "Alertas por ciclones tropicales", "Comisión Nacional del Agua");
        feedWriter.setPubDate(new Date(System.currentTimeMillis()));
    }
    
    public String generateXML() {
        ArrayList<String> alertIds = mi.listPublishedHurricanes(10);
        for(String id : alertIds) {
            CAPGenerator gen = new CAPGenerator(id);
            Alert alert = gen.generateAlert();
            feedWriter.addAlert(alert);
        }
        
        return feedWriter.getXML();
    }
}
