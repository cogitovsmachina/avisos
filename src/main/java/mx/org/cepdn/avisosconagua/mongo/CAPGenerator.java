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

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.CapValidator;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Group;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;
import com.google.publicalerts.cap.ValuePair;
import com.mongodb.BasicDBObject;
import java.text.ParseException;
import mx.org.cepdn.avisosconagua.util.Utils;

/**
 *
 * @author serch
 */
public class CAPGenerator {

    private final String currentId;
    private final BasicDBObject aviso;
    private final BasicDBObject init;
//    private final BasicDBObject pronostico;
//    private final BasicDBObject seguimiento;
    private final BasicDBObject capInfo;
    private String _date=null;

    public CAPGenerator(String currentId) {
        this.currentId = currentId;
        aviso = MongoInterface.getInstance().getAdvice(currentId);
        init = (BasicDBObject) aviso.get("init");
//        pronostico = (BasicDBObject) aviso.get("pronostico");
//        seguimiento = (BasicDBObject) aviso.get("seguimiento");
        capInfo = (BasicDBObject) aviso.get("capInfo");

    }

    public String generate() {
        return getXML();
    }
    
    public String getDate(){
        return _date;
    }

    public Alert generateAlert() {
        return getValidAlertBuilder().build();
    }

    private String getXML() {
        CapXmlBuilder builder = new CapXmlBuilder();
        return builder.toXml(getValidAlertBuilder());
    }

    private Alert.Builder getValidAlertBuilder() {

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm aa"); //TODO quitar cuando clock 24hrs
        java.util.Date emi = new java.util.Date();
        try {
            String fechEmi = capInfo.getString("issueDate") + " " + capInfo.getString("issueTime");
            System.out.println("fecEmi: " + fechEmi);
            emi = sdf.parse(fechEmi);
            _date = Utils.sdf.format(emi);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        Alert.Builder builder = Alert.newBuilder()
                .setXmlns(CapValidator.CAP12_XMLNS)
                .setIdentifier(currentId)
                .setSender("smn.cna.gob.mx")
                .setSent(Utils.getISODate(Utils.sdf.format(emi)))//2003-04-02T14:39:01-05:00 2014-04-93T12:58:00-06:00
                .setStatus(Alert.Status.ACTUAL)
                .setScope(Alert.Scope.PUBLIC)
                .addCode(capInfo.getString("issueNumber"));
//                
//                .setIncidents(Group.newBuilder()
//                        .addValue("incident1").addValue("incident2").build())

        //definir si ACTUAL o UPDATE
        String updateToId = capInfo.getString("previousIssue");
        String preSent = "";
        if (null != updateToId && !"".equals(updateToId.trim())) {
            BasicDBObject ci = (BasicDBObject) MongoInterface.getInstance().getAdvice(updateToId).get("capInfo");
            try {
                String fecha = ci.getString("issueDate") + " " + ci.getString("issueTime");
                System.out.println("fecha: " + fecha);
                emi = sdf.parse(fecha);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            preSent = Utils.getISODate(Utils.sdf.format(emi));
        }
        Alert.MsgType type = (null == updateToId ? Alert.MsgType.ALERT : Alert.MsgType.UPDATE);
        builder.setMsgType(type);
        if (null != updateToId && !"".equals(updateToId.trim())) {
            builder.setReferences(Group.newBuilder()
                    .addValue("smn.cna.gob.mx," + updateToId + "," + preSent)
                    .build());
        }
        /*
         for (HashMap<String, String> event : listEvents) {
         builder.addInfo(getValidInfoBuilder(event));
         }*/
        for (String key : init.keySet()) {
            if (key.startsWith("area-")) {
                builder.addInfo(getValidInfoBuilder(key.substring(5)));
            }
        }
        return builder;
    }

    private Info.Builder getValidInfoBuilder(String areaId) {
        Info.Urgency urg = Info.Urgency.IMMEDIATE;//TODO: definir valueOf(CAPUtils.Urgency.valueOf(event.get("eventUrgency")).ordinal());
        Info.Severity sev = Info.Severity.UNKNOWN_SEVERITY; //TODO: definir el valor de la severidad
        Info.Certainty cer = Info.Certainty.OBSERVED;//TODO: valueOf(CAPUtils.Certainty.valueOf(event.get("eventCertainty")).ordinal());
        Info.Builder builder = Info.newBuilder()
                .setLanguage("es-419")
                .addCategory(Info.Category.MET)
                .setEvent(Utils.getTituloBoletin(aviso.getString("adviceType")))
                .setUrgency(urg)
                .setSeverity(sev)
                .setCertainty(cer)
                .addResponseType(Info.ResponseType.EXECUTE)
                .setSenderName("Comisión Nacional del Agua - Servicio Meteorológico Nacional")
                .setHeadline(capInfo.getString("eventHeadline"))
                .setDescription(init.getString("eventDescription"))
                .setWeb("http://smn.cna.gob.mx/")
                //                .addParameter(ValuePair.newBuilder()
                //                        .setValueName("HSAS").setValue("ORANGE").build())
                //                .setAudience("an audience")
                .setContact("webmaster@conagua.gob.mx")
                //                .addEventCode(ValuePair.newBuilder()
                //                        .setValueName("EC").setValue("v1").build())
                //                .setEffective("2003-04-02T14:39:01-05:00")
                //                .setOnset("2003-04-02T15:39:01+05:00")
                //                .setExpires("2003-04-02T16:39:01-00:00")
                .addParameter(ValuePair.newBuilder()
                        .setValueName("Elaboró").setValue(capInfo.getString("issueMetheorologist")).build())
                .addParameter(ValuePair.newBuilder()
                        .setValueName("Revisó").setValue(capInfo.getString("issueShiftBoss")).build());
        if (null != init.getString("eventRisk") && !"".equals(init.getString("eventRisk"))) {
            builder.addParameter(ValuePair.newBuilder().setValueName("semáforo").setValue(init.getString("eventRisk")).build());
        }
        if (null != init.getString("eventInstructions")) {
            builder.setInstruction(init.getString("eventInstructions"));
        }
//        for (String area : event.keySet()) {
//            if (area.startsWith("area")) {
        //System.out.println("estados: " + "states" + area.substring(4));
        String estados = init.getString("eventDistance");
        String[] aarea = init.getString("area-" + areaId).split(" ");
        if (aarea[aarea.length - 1].indexOf(",") > 0) {
            builder.addArea(getPolygonArea(aarea, estados));
        } else {
            builder.addArea(getCircleArea(aarea, estados));
        }
//            }
//        }
//                .addResource(getValidResourceBuilder());
        return builder;
    }

    private Area.Builder getPolygonArea(String[] area, String estados) {
        Polygon.Builder poli = Polygon.newBuilder();
        for (String coord : area) {
            String[] scord = coord.split(",");
            poli.addPoint(Point.newBuilder().setLatitude(Double.parseDouble(scord[0])).setLongitude(Double.parseDouble(scord[1])).build());
        }
        Area.Builder areabuild = Area.newBuilder();
        if (null != estados) {
            areabuild.setAreaDesc(estados);
        }
        areabuild.addPolygon(poli.build());
        return areabuild;
    }

    private Area.Builder getCircleArea(String[] area, String estados) {
        double latitude, logitude, radius;
        String[] scord = area[0].split(",");
        latitude = Double.parseDouble(scord[0]);
        logitude = Double.parseDouble(scord[1]);
        radius = Double.parseDouble(area[1]);
        Area.Builder areabuild = Area.newBuilder();
        if (null != estados) {
            areabuild.setAreaDesc(estados);
        }
        areabuild.addCircle(Circle.newBuilder()
                .setPoint(Point.newBuilder().setLatitude(latitude).setLongitude(logitude).build())
                .setRadius(radius).build());
        return areabuild;
    }

}
