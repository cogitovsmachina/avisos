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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author hasdai
 */
public class Utils {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat sdfjd = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat diaformater = new SimpleDateFormat("dd 'de' MMMMM 'del' yyyy", Locale.forLanguageTag("es-mx"));
    public static final SimpleDateFormat horaformater = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("es-mx"));
    public static final SimpleDateFormat isoformater = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ssXXX"); 

    public static Date getDateFromString(String date) throws ParseException {
        return sdfjd.parse(date);
    }

    public static String getDiaText(String date) {
        String fecha = "";
        try {
            Date fec = getDateFromString(date);
            fecha = diaformater.format(fec);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return fecha;
    }
    
    public static String getISODate(String date){
        String fecha = "";
        try {
            Date fec = sdf.parse(date);
            fecha = isoformater.format(fec);
        } catch (ParseException pe){
            pe.printStackTrace();
        }
        return fecha;
    }

    public static String getTituloBoletinHtml(String type){
        String titulo = "";
        switch (type) {
            case "pacdp":
                titulo = "Aviso de zonas de baja presi&oacute;n en el Pac&iacute;fico con potencial cicl&oacute;nico";
                break;
            case "pacht":
                titulo = "Aviso de Cicl&oacute;n Tropical en el Pac&iacute;fico";
                break;
            case "atldp":
                titulo = "Aviso de zonas de baja presi&oacute;n en el Atl&aacute;ntico con potencial cicl&oacute;nico";
                break;
            case "atlht":
                titulo = "Aviso de Cicl&oacute;n Tropical en el Atl&aacute;ntico";
                break;
        }
        return titulo;
    }
    
    public static String getTituloBoletin(String type){
        String titulo = "";
        switch (type) {
            case "pacdp":
                titulo = "Aviso de zonas de baja presión en el Pacífico con potencial ciclónico";
                break;
            case "pacht":
                titulo = "Aviso de Ciclón Tropical en el Pacífico";
                break;
            case "atldp":
                titulo = "Aviso de zonas de baja presión en el Atlántico con potencial ciclónico";
                break;
            case "atlht":
                titulo = "Aviso de Ciclón Tropical en el Océano Atlántico";
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
    
    public static String escapeQuote(String string){
        return string.replaceAll("\"", "&quot;");
    }
    
    /**
     * Tokenizes a string splitting in base to the provided regexp.
     * @param source Source string to tokenize.
     * @param regex Regular expression for token matching.
     * @return List of tokens.
     */
    public static ArrayList<String> tokenize(String source, String regex) {
        ArrayList<String> ret = new ArrayList<String>();
        Pattern logEntry = Pattern.compile(regex);
        Matcher matchPattern = logEntry.matcher(source);

        while(matchPattern.find()) {
            ret.add(matchPattern.group(1));
        }
        return ret;
    }
}
