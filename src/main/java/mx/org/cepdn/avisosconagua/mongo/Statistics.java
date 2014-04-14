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

package mx.org.cepdn.avisosconagua.mongo;

import com.mongodb.BasicDBObject;

/**
 *
 * @author serch
 */
public class Statistics {
    private String aviso;
    private String fecha;
    private String latitud;
    private String longitud;
    private String distancia;
    private String viento;
    private String categoria;
    private String avance;

    public Statistics(String aviso, String fecha, String latitud, String longitud, String distancia, String viento, String categoria, String avance) {
        this.aviso = aviso;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
        this.viento = viento;
        this.categoria = categoria;
        this.avance = avance;
    }
    
    public Statistics(BasicDBObject aviso){
        BasicDBObject seguimiento = (BasicDBObject)aviso.get("seguimiento");
        this.aviso = seguimiento.getString("");
        this.fecha = seguimiento.getString("");
        this.latitud = seguimiento.getString("");
        this.longitud = seguimiento.getString("");
        this.distancia = seguimiento.getString("");
        this.viento = seguimiento.getString("");
        this.categoria = seguimiento.getString("");
        this.avance = seguimiento.getString("");
        
    }

    public String getAviso() {
        return aviso;
    }

    public String getFecha() {
        return fecha;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getViento() {
        return viento;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getAvance() {
        return avance;
    }
    
    public String toString(){
        return "{\"aviso\":\""+aviso+"\",\"fecha\":\""+fecha+"\",\"latitud\":\""
                +latitud+"\",\"longitud\":\""+longitud+"\",\"distancia\":\""
                +distancia+"\",\"viento\":\""+viento+"\",\"categoria\":\""+categoria
                +"\",\"avance\":\""+avance+"\"}";
    }

    
}
