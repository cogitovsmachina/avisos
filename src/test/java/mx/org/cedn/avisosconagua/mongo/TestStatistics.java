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

package mx.org.cedn.avisosconagua.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author serch
 */

@RunWith(JUnit4.class)
public class TestStatistics {
    BasicDBObject aviso;

    @Before
    public void prepare(){
        aviso = (BasicDBObject)JSON.parse("{\n" +
"        \"init\" : {\n" +
"                \"issueLocalTime\" : \"18:00 hrs (00:00 GMT)\",\n" +
"                \"eventDistance\" : \"A 225 km al norte de Cancún\",\n" +
"                \"eventWindSpeedSust\" : \"55\",\n" +
"                \"eventCLat\" : \"15.7\",\n" +
"                \"eventCLon\" : \"101.3\",\n" +
"                \"eventWindSpeedMax\" : \"75\",\n" +
"                \"eventCurrentPath\" : \"Noroeste (320) a 6 km/h\",\n" +
"        },\n" +
"        \"capInfo\" : {\n" +
"                \"eventCategory\" : \"TT\",\n" +
"                \"issueNumber\" : \"5\",\n" +
"                \"issueDate\" : \"05/16/2014\",\n" +
"        }\n" +
"}\n" +
"");
        
    }
    
    @Test 
    public void statistics(){
        
        mx.org.cedn.avisosconagua.mongo.Statistics stat = new mx.org.cedn.avisosconagua.mongo.Statistics(aviso);
        System.out.println("res: "+stat.toString());
        Assert.assertEquals("{\"aviso\":\"5\",\"fecha\":\"16 may / 18:00\",\"latitud\":\"15.7\","
                + "\"longitud\":\"101.3\",\"distancia\":\"A 225 km al norte de Cancún\","
                + "\"viento\":\"55 / 75\",\"categoria\":\"TT\",\"avance\":\"Noroeste (320) a 6 km/h\"}",stat.toString());
    }
}
