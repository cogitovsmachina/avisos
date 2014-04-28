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

package mx.org.cedn.avisosconagua.util;

import java.util.HashMap;
import java.util.Locale;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
/**
 *
 * @author serch
 */
@RunWith(JUnit4.class)
public class TestUtils {
    
    @Test
    public void getDiaText(){
        //dd 'de' MMMMM 'del' yyyy
        assertEquals("20 de abril del 2014", mx.org.cedn.avisosconagua.util.Utils.getDiaText("04/20/2014"));
    }
    
    @Test
    public void getISODate(){
        //YYYY-MM-dd'T'HH:mm:ss'-06:00'
        assertEquals("2014-04-20T18:30:00-05:00", mx.org.cedn.avisosconagua.util.Utils.getISODate("20/04/2014 18:30"));
        assertEquals("2014-01-20T18:30:00-06:00", mx.org.cedn.avisosconagua.util.Utils.getISODate("20/01/2014 18:30"));
    }
    
    @Test
    public void getValidFieldFromHash(){
        HashMap<String, String> map = new HashMap<>();
        map.put("demoValue", "rigth");
        assertEquals("rigth", mx.org.cedn.avisosconagua.util.Utils.getValidFieldFromHash(map, "demoValue"));
        assertEquals("", mx.org.cedn.avisosconagua.util.Utils.getValidFieldFromHash(map, "demoNoValue"));
    }
    
    @Test
    public void tokenize() {
        assertThat(mx.org.cedn.avisosconagua.util.Utils.tokenize("{uno}{dos}{tres}", "\\{(.*?)\\}"),hasItems("uno","dos","tres")); 
    }
    
    //"\\{(.*?)\\}"
}
