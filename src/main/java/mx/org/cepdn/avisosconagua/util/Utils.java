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
package mx.org.cepdn.avisosconagua.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author hasdai
 */
public class Utils {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final SimpleDateFormat diaformater = new SimpleDateFormat("dd 'de' MMMMM 'del' yyyy", Locale.forLanguageTag("mx"));
    public static final SimpleDateFormat horaformater = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("mx"));
    public static final SimpleDateFormat isoformater = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss'-06:00'");

    public static Date getDateFromString(String date) throws ParseException {
        return sdf.parse(date);
    }

    public static String getDiaText(String date) {
        String fecha = "";
        try {
            fecha = diaformater.format(getDateFromString(date));
        } catch (ParseException pe) {
        }
        return fecha;
    }

    public static String getTituloBoletin(String type){
        String titulo = "";
        switch (type) {
            case "pacdp":
                titulo = "Boletín de zonas de baja presión en el Océano Pacífico con potencial ciclónico";
                break;
            case "pacht":
                titulo = "Aviso de Ciclón Tropical del Océano Pacífico";
                break;
            case "atldp":
                titulo = "Boletín de zonas de baja presión en el Océano Atlántico con potencial ciclónico";
                break;
            case "atlht":
                titulo = "Aviso de Ciclón Tropical del Océano Atlántico";
                break;
        }
        return titulo;
    }
    /**
     * Gets a String value from a map or an empty String.
     *
     * @param map Map to get value from
     * @param key Key
     * @return String value for the given key or an empty String.
     */
    public static String getValidFieldFromHash(HashMap<String, String> map, String key) {
        String ret = map.get(key);
        if (ret == null) {
            ret = "";
        }
        return ret;
    }
}
