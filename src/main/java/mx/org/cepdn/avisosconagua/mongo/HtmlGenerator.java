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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import mx.org.cepdn.avisosconagua.util.Utils;

/**
 *
 * @author serch
 */
public class HtmlGenerator {

    private final String currentId;
    private static final String backimg = "fondo.gif";
    private boolean isDP;
    private String principalFile = null;
    private String pronosticoFile = null;
    private String previous = null;
    private String title = null;

    public HtmlGenerator(final String currentId) {
        this.currentId = currentId;
    }

    public boolean isDP() {
        return isDP;
    }

    public String getPrincipalFile() {
        return principalFile;
    }

    public String getPronosticoFile() {
        return pronosticoFile;
    }
    
    public String getPrevious() {
        return previous;
    }
    
    public String getTitle() {
        return title;
    }

//init,pronostico,seguimiento,capInfo,preview,generate
    public String generate(final boolean publish) {
        BasicDBObject aviso = MongoInterface.getInstance().getAdvice(currentId);
        BasicDBObject init = (BasicDBObject) aviso.get("init");
        BasicDBObject pronostico = (BasicDBObject) aviso.get("pronostico");
        BasicDBObject capInfo = (BasicDBObject) aviso.get("capInfo");
        String imagefolder = publish ? currentId + "/" : "/getImage/";
        principalFile = init.getString("issueSateliteImg");
        if (null != pronostico) {
            pronosticoFile = pronostico.getString("issueSateliteLocationImg");
        }
        String interpol = init.getString("eventCCalc");
        interpol = "interpolation".equals(interpol)?"(Por interpolaci&oacute;n)":"";
        String titulo = Utils.getTituloBoletinHtml(aviso.getString(MongoInterface.ADVICE_TYPE));
        isDP = aviso.getString(MongoInterface.ADVICE_TYPE).endsWith("dp");
        title = capInfo.getString("issueNumber")+" "+titulo;
        if (isDP) {
            return header + getEncabezado(backimg, titulo,
                    Utils.getDiaText(capInfo.getString("issueDate")),
                    capInfo.getString("issueNumber"), capInfo.getString("issueTime"), getSistemaLegend(init.getString("eventCoastDistance")))
                    + getTitulo(capInfo.getString("eventHeadline"), imagefolder + init.getString("issueSateliteImg"),
                            init.getString("issueSateliteImgFooter"), 
                            cleanPs(init.getString("eventDescriptionHTML")))
                    + get1r2c("HORA LOCAL (HORA GMT)", init.getString("issueLocalTime"))
                    + get1r3c("UBICACI&Oacute;N DEL CENTRO DE BAJA PRESI&Oacute;N", "LATITUD NORTE: " + init.getString("eventCLat") + "°", "LONGITUD OESTE: " + init.getString("eventCLon") + "°", interpol)
                    + get1r2c("DISTANCIA AL LUGAR M&Aacute;S CERCANO", init.getString("eventDistance"))
                    + get1r2c("DESPLAZAMIENTO ACTUAL:", init.getString("eventCurrentPath"))
                    + get1r3c("VIENTOS M&Aacute;XIMOS [Km/h]:", "SOSTENIDOS: " + init.getString("eventWindSpeedSust"), "RACHAS: " + init.getString("eventWindSpeedMax"),"")
                    + get1r2c("PRESI&Oacute;N M&Iacute;NIMA CENTRAL [hPa]:", init.getString("eventMinCP"))
                    + get1r2c("POTENCIAL DE DESARROLLO EN 48 HORAS", init.getString("eventForecast48h")+"%")
                    + get1r2c("POTENCIAL DE DESARROLLO EN CINCO D&Iacute;AS", init.getString("eventForecast5d")+"%")
                    + get1r2c("PRON&Oacute;STICO DE LLUVIA:", init.getString("eventRainForecast"))
                    + getFooter(capInfo.getString("issueMetheorologist"), capInfo.getString("issueShiftBoss"), capInfo.getString("issueFooter"));
        } else {
            previous = capInfo.getString("previousIssue");
            String wind = "";
            if ((!"".equals(init.getString("eventWind120kmNE")))
                    || (!"".equals(init.getString("eventWind120kmSE")))
                    || (!"".equals(init.getString("eventWind120kmSO")))
                    || (!"".equals(init.getString("eventWind120kmNO")))) {
                wind += get1r5c("RADIO DE VIENTOS DE 120 km/h", init.getString("eventWind120kmNE"),
                        init.getString("eventWind120kmSE"), init.getString("eventWind120kmSO"), init.getString("eventWind120kmNO"));
            }
            if ((!"".equals(init.getString("eventWind95kmNE")))
                    || (!"".equals(init.getString("eventWind95kmSE")))
                    || (!"".equals(init.getString("eventWind95kmSO")))
                    || (!"".equals(init.getString("eventWind95kmNO")))) {
                wind += get1r5c("RADIO DE VIENTOS DE 95 km/h", init.getString("eventWind95kmNE"),
                        init.getString("eventWind95kmSE"), init.getString("eventWind95kmSO"), init.getString("eventWind95kmNO"));
            }
            if ((!"".equals(init.getString("eventWind63kmNE")))
                    || (!"".equals(init.getString("eventWind63kmSE")))
                    || (!"".equals(init.getString("eventWind63kmSO")))
                    || (!"".equals(init.getString("eventWind63kmNO")))) {
                wind += get1r5c("RADIO DE VIENTOS DE 63 km/h", init.getString("eventWind63kmNE"),
                        init.getString("eventWind63kmSE"), init.getString("eventWind63kmSO"), init.getString("eventWind63kmNO"));
            }
            if ((!"".equals(init.getString("seas4mNE")))
                    || (!"".equals(init.getString("seas4mSE")))
                    || (!"".equals(init.getString("seas4mSO")))
                    || (!"".equals(init.getString("seas4mNO")))) {
                wind += get1r5c("OLEAJE 4 m", init.getString("seas4mNE"),
                        init.getString("seas4mSE"), init.getString("seas4mSO"), init.getString("seas4mNO"));
            }
            
            String seccionB = "";
            String data = pronostico.getString("forecastData");
            ArrayList<String> rows = Utils.tokenize(data, "\\{(.*?)\\}");
            for(String row:rows){
                String[] values = row.split("\\|");
                seccionB += getRowSecB(values[0], values[1], values[2], values[3], values[4], values[5]);
            }
            String sectionC ="";
            ArrayList<Statistics> secclist = MongoInterface.getInstance().getAdviceChain(currentId);
            for (Statistics secc : secclist){
                sectionC += getSectionCRow(secc.getAviso(), secc.getFecha(), secc.getLatitud(), 
                        secc.getLongitud(), secc.getDistancia(), secc.getViento(), secc.getCategoria(), secc.getAvance());
            }
            
            return header + getEncabezado(backimg, titulo,
                    Utils.getDiaText(capInfo.getString("issueDate")),
                    capInfo.getString("issueNumber"), capInfo.getString("issueTime"), getSistemaLegend(init.getString("eventCoastDistance")))
                    + getTitulo(capInfo.getString("eventHeadline"), imagefolder + init.getString("issueSateliteImg"),
                            init.getString("issueSateliteImgFooter"), 
                            cleanPs(init.getString("eventDescriptionHTML")))
                    + get1r2c("HORA LOCAL (HORA GMT)", init.getString("issueLocalTime"))
                    + get1r3c("UBICACI&Oacute;N DEL CENTRO DEL CICL&Oacute;N", "LATITUD NORTE: " + init.getString("eventCLat") + "°", "LONGITUD OESTE: " + init.getString("eventCLon") + "°", interpol)
                    + get1r2c("DISTANCIA AL LUGAR M&Aacute;S CERCANO", init.getString("eventDistance"))
                    + getZonaAlerta(init.getString("areaDescription"))
                    + get1r2c("DESPLAZAMIENTO ACTUAL:", init.getString("eventCurrentPath"))
                    + get1r3c("VIENTOS M&Aacute;XIMOS [Km/h]:", "SOSTENIDOS: " + init.getString("eventWindSpeedSust"), "RACHAS: " + init.getString("eventWindSpeedMax"), "")
                    + get1r2c("PRESI&Oacute;N M&Iacute;NIMA CENTRAL [hPa]:", init.getString("eventMinCP")+ " hPa")
                    + get1r2c("DIAMETRO DEL OJO [Km]", init.getString("eventCDiameter"))
                    + vientosTitle
                    + wind
                    + get1r2c("DIAMETRO PROMEDIO DE FUERTE CONVECCI&Oacute;N", init.getString("eventDiameterConvection"))
                    + get1r2c("COMENTARIOS ADICIONALES:", cleanPs(init.getString("eventComments")))
                    + get1r2c("RECOMENDACIONES", cleanPs(init.getString("eventInstructions")))
                    + headerSecB //TODO Sección B pronostico
                    + seccionB
                    + getImagenSecB(imagefolder + pronostico.getString("issueSateliteLocationImg"),pronostico.getString("issueSateliteLocationImgFooter") )
                    + tituloSecC 
                    + sectionC
                    + getFooter(capInfo.getString("issueMetheorologist"), capInfo.getString("issueShiftBoss"), capInfo.getString("issueFooter"));
        }
    }

    private static String cleanPs(String stoclean) {
        return stoclean.replaceAll("<p>", "").replaceAll("</p>", "<br>");

    }

    private static String getSistemaLegend(String key) {
        String ret = "";
        switch (key) {
            case "lessthan500km":
                ret = "Sistema a menos de 500 km de las costas";
                break;
            case "morethan500km":
                ret = "Sistema a mas de 500 km de las costas";
                break;
            case "land":
                ret = "Sistema en tierra";
                break;
        }
        return ret;
    }

    private static String getFooter(String elaboro, String reviso, String pie) {
        return " <tr style='mso-yfti-irow:68;height:20.85pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>ELABOR&Oacute; (SMN): " + elaboro + "<span\n"
                + "  style=\"mso-spacerun:yes\">  </span><span\n"
                + "  style=\"mso-spacerun:yes\">                       </span><span\n"
                + "  style=\"mso-spacerun:yes\">   </span>REVIS&Oacute;: " + reviso + "<o:p></o:p></span></em></p>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>SMN EN COORDINACI&Oacute;N CON SERVICIOS ESPECIALIZADOS:\n"
                + "  CFE, SEGOB (CNPC-CENAPRED), SEMAR, SEDENA, SCT (SENEAM, DM) E IMTA. <o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>\n"
                + " <tr style='mso-yfti-irow:69;mso-yfti-lastrow:yes;height:20.85pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + pie + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>\n"
                + " <![if !supportMisalignedColumns]>\n"
                + " <tr height=0>\n"
                + "  <td width=61 style='border:none'></td>\n"
                + "  <td width=32 style='border:none'></td>\n"
                + "  <td width=18 style='border:none'></td>\n"
                + "  <td width=56 style='border:none'></td>\n"
                + "  <td width=63 style='border:none'></td>\n"
                + "  <td width=26 style='border:none'></td>\n"
                + "  <td width=7 style='border:none'></td>\n"
                + "  <td width=40 style='border:none'></td>\n"
                + "  <td width=21 style='border:none'></td>\n"
                + "  <td width=4 style='border:none'></td>\n"
                + "  <td width=2 style='border:none'></td>\n"
                + "  <td width=17 style='border:none'></td>\n"
                + "  <td width=45 style='border:none'></td>\n"
                + "  <td width=2 style='border:none'></td>\n"
                + "  <td width=9 style='border:none'></td>\n"
                + "  <td width=33 style='border:none'></td>\n"
                + "  <td width=16 style='border:none'></td>\n"
                + "  <td width=5 style='border:none'></td>\n"
                + "  <td width=59 style='border:none'></td>\n"
                + " </tr>\n"
                + " <![endif]>\n"
                + "</table>\n"
                + "\n"
                + "<p class=MsoNormal style='text-align:justify'><span lang=ES-MX\n"
                + "style='font-size:3.0pt;font-family:Calibri;mso-bidi-font-family:Arial;\n"
                + "mso-ansi-language:ES-MX'><o:p>&nbsp;</o:p></span></p>\n"
                + "\n"
                + "</div>\n"
                + "\n"
                + "</body>\n"
                + "\n"
                + "</html>";
    }

    private static String getSectionCRow(String c1, String c2, String c3, String c4, String c5, String c6, String c7, String c8) {
        return " <tr style='mso-yfti-irow:34;height:20.85pt'>\n"
                + "  <td width=54 style='width:53.7pt;border:solid windowtext 1.0pt;border-top:\n"
                + "  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c1 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=54 colspan=2 style='width:53.8pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c2 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=45 style='width:44.5pt;border-top:none;border-left:none;border-bottom:\n"
                + "  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n"
                + "  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n"
                + "  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c3 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=50 style='width:49.6pt;border-top:none;border-left:none;border-bottom:\n"
                + "  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n"
                + "  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n"
                + "  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c4 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=78 colspan=4 style='width:78.15pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c5 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=50 colspan=4 style='width:49.7pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c6 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=48 colspan=2 style='width:48.0pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c7 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=73 colspan=4 style='width:72.5pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c8 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String tituloSecC = " <tr style='mso-yfti-irow:32;height:20.85pt'>\n"
            + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
            + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
            + "  background:#BFBFBF;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><b style='mso-bidi-font-weight:normal'><span\n"
            + "  lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\n"
            + "  \"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
            + "  italic'>SECCI&Oacute;N C. TABLA DE SEGUIMIENTO DEL CICL&Oacute;N TROPICAL</span></b></em><em><span\n"
            + "  lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\n"
            + "  \"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
            + "  italic'><o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + " </tr>\n"
            + " <tr style='mso-yfti-irow:33;height:20.85pt'>\n"
            + "  <td width=54 style='width:53.7pt;border:solid windowtext 1.0pt;border-top:\n"
            + "  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
            + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Aviso No.<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=54 colspan=2 style='width:53.8pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Fecha / Hora local CDT<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=45 style='width:44.5pt;border-top:none;border-left:none;border-bottom:\n"
            + "  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n"
            + "  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n"
            + "  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Lat. Norte<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=50 style='width:49.6pt;border-top:none;border-left:none;border-bottom:\n"
            + "  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n"
            + "  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n"
            + "  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Long. Oeste<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=78 colspan=4 style='width:78.15pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Distancia m&aacute;s cercana (km)<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=50 colspan=4 style='width:49.7pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Viento m&iacute;x. / rachas<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=48 colspan=2 style='width:48.0pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Categor&iacute;a<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=73 colspan=4 style='width:72.5pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:8.0pt;\n"
            + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>Avance<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + " </tr>";

    private static String getImagenSecB(String file, String leyenda) {
        return " <tr style='mso-yfti-irow:31;height:289.55pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:289.55pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><span lang=ES style='mso-fareast-language:ES-MX'><!--[if gte vml 1]><v:shape\n"
                + "   id=\"_x0000_i1026\" type=\"#_x0000_t75\" style='width:425pt;height:304pt'>\n"
                + "   <v:imagedata src=\"" + file + "/>\n"
                + "  </v:shape><![endif]--><![if !vml]><img width=427 height=306\n"
                + "  src=\"" + file + "\" v:shapes=\"_x0000_i1026\"><![endif]></span><span\n"
                + "  lang=ES style='font-size:5.0pt;mso-fareast-language:ES-MX;mso-no-proof:yes'><o:p></o:p></span></p>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><b style='mso-bidi-font-weight:normal'><span\n"
                + "  lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\n"
                + "  \"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
                + "  italic'>" + leyenda + "<o:p></o:p></span></b></em></p>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><b style='mso-bidi-font-weight:normal'><span\n"
                + "  lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\n"
                + "  \"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
                + "  italic'><o:p>&nbsp;</o:p></span></b></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String getRowSecB(String c1, String c2, String c3, String c4, String c5, String c6) {
        return " <tr style='mso-yfti-irow:25;height:20.85pt'>\n"
                + "  <td width=88 colspan=2 style='width:88.25pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + c1 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=64 colspan=2 style='width:63.75pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + c2 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=78 colspan=2 style='width:77.95pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + c3 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=78 colspan=5 style='width:78.05pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + c4 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=78 colspan=4 style='width:78.0pt;border-top:none;border-left:none;\n"
                + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + c5 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=128 colspan=4 style='width:127.8pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c6 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String headerSecB = " <tr style='mso-yfti-irow:23;height:20.85pt'>\n"
            + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
            + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
            + "  background:#BFBFBF;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><b style='mso-bidi-font-weight:normal'><span\n"
            + "  lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\n"
            + "  \"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
            + "  italic'>SECCI&Oacute;N B. PRON&Oacute;STICO DE DESPLAZAMIENTO Y EVOLUCI&Oacute;N<o:p></o:p></span></b></em></p>\n"
            + "  </td>\n"
            + " </tr>\n"
            + " <tr style='mso-yfti-irow:24;height:20.85pt'>\n"
            + "  <td width=88 colspan=2 style='width:88.25pt;border:solid windowtext 1.0pt;\n"
            + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
            + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>PRON&Oacute;STICO VALIDO AL <o:p></o:p></span></em></p>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>D&Iacute;A/HORA LOCAL TIEMPO CENTRO <o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=64 colspan=2 style='width:63.75pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>LATITUD NORTE<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=78 colspan=2 style='width:77.95pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>LONG. OESTE<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=78 colspan=5 style='width:78.05pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>VIENTOS [Km/h]<o:p></o:p></span></em></p>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>SOST. / RACHAS<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=78 colspan=4 style='width:78.0pt;border-top:none;border-left:none;\n"
            + "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>CATEGOR&Iacute;A<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + "  <td width=128 colspan=4 style='width:127.8pt;border-top:none;border-left:\n"
            + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
            + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
            + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES-MX style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
            + "  font-style:normal;mso-bidi-font-style:italic'>UBICACI&Oacute;N (en km)<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + " </tr>";

    private static String get1r5c(String c1, String c2, String c3, String c4, String c5) {
        return " <tr style='mso-yfti-irow:16;height:20.85pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><span lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'>" + c1 + "<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=69 colspan=3 valign=top style='width:69.1pt;border-top:none;\n"
                + "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\"'>" + c2 + " km NE<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=69 colspan=3 valign=top style='width:69.1pt;border-top:none;\n"
                + "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\"'>" + c3 + " km SE<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=69 colspan=4 valign=top style='width:69.1pt;border-top:none;\n"
                + "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\"'>" + c4 + " km SO<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=69 colspan=2 valign=top style='width:69.4pt;border-top:none;\n"
                + "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\"'>" + c5 + " km NO<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String vientosTitle = " <tr style='mso-yfti-irow:15;height:20.85pt'>\n"
            + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
            + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
            + "  background:#D9D9D9;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
            + "  <p class=MsoNormal align=center style='text-align:center;mso-element:frame;\n"
            + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
            + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
            + "  mso-height-rule:exactly'><em><span lang=ES style='font-size:10.0pt;\n"
            + "  mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";font-style:normal;\n"
            + "  mso-bidi-font-style:italic'>EXTENSI&Oacute;N EN CUADRANTES DE RADIOS DE VIENTOS Y\n"
            + "  OLEAJE<o:p></o:p></span></em></p>\n"
            + "  </td>\n"
            + " </tr>";

    private static String getZonaAlerta(String mensaje) {
        return " <tr style='mso-yfti-irow:9;height:28.05pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  background:#D9D9D9;padding:0cm 5.4pt 0cm 5.4pt;height:28.05pt'>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><span lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'>ZONA DE\n"
                + "  ALERTA</span><em><span lang=ES style='font-size:10.0pt;mso-bidi-font-size:\n"
                + "  12.0pt;font-family:\"Arial Narrow\";font-style:normal;mso-bidi-font-style:italic'><o:p></o:p></span></em></p>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><em><span lang=ES style='font-size:9.0pt;mso-bidi-font-size:12.0pt;\n"
                + "  font-family:\"Arial Narrow\";font-style:normal;mso-bidi-font-style:italic'>[SE\n"
                + "  ESTABLECE EN COORDINACI&Oacute;N CON EL CMRE-MIAMI]</span></em><span lang=ES-MX\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;\n"
                + "  mso-fareast-language:ES-MX;mso-no-proof:yes'><o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=277 colspan=12 style='width:276.7pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;background:#D9D9D9;padding:0cm 5.4pt 0cm 5.4pt;\n"
                + "  height:28.05pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center'><em><span lang=ES\n"
                + "  style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";\n"
                + "  font-style:normal;mso-bidi-font-style:italic'>" + mensaje + "</span></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String get1r2c(String c1, String c2) {
        return " <tr style='mso-yfti-irow:6;height:27.65pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:27.65pt'>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><span lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'>" + c1
                + "<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=277 colspan=12 style='width:276.7pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:27.65pt'>\n"
                + "  <p class=MsoNormal><em><span lang=ES style='font-size:10.0pt;mso-bidi-font-size:\n"
                + "  12.0pt;font-family:\"Arial Narrow\";font-style:normal;mso-bidi-font-style:italic'>" + c2
                + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String get1r3c(String c1, String c2, String c3, String inter) {
        return " <tr style='mso-yfti-irow:7;height:20.85pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><span lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'>" + c1 + "<o:p></o:p></span></p>\n"
                + "  <p class=MsoNormal style='margin-right:2.85pt;mso-element:frame;mso-element-frame-hspace:\n"
                + "  7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:paragraph;\n"
                + "  mso-element-anchor-horizontal:column;mso-element-top:.05pt;mso-height-rule:\n"
                + "  exactly'><span lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'>"
                +inter+"<b style='mso-bidi-font-weight:normal'><o:p></o:p></b></span></p>\n"
                + "  </td>\n"
                + "  <td width=138 colspan=6 style='width:138.2pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal><em><span lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:\n"
                + "  12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c2 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + "  <td width=139 colspan=6 style='width:138.5pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:20.85pt'>\n"
                + "  <p class=MsoNormal><em><span lang=ES-MX style='font-size:10.0pt;mso-bidi-font-size:\n"
                + "  12.0pt;font-family:\"Arial Narrow\";mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>" + c3 + "<o:p></o:p></span></em></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String getTitulo(String sintesis, String imgFile, String leyendaImg, String situacionActual) {
        
        return " <tr style='mso-yfti-irow:3;height:17.5pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  background:#A6A6A6;padding:0cm 5.4pt 0cm 5.4pt;height:17.5pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-layout-grid-align:\n"
                + "  none;text-autospace:none;mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:Arial;color:white;mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'><o:p>&nbsp;</o:p></span></span></p>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-layout-grid-align:\n"
                + "  none;text-autospace:none'><span class=MsoIntenseEmphasis><span lang=ES-MX\n"
                + "  style='font-size:11.0pt;mso-bidi-font-size:10.0pt;font-family:Arial;\n"
                + "  color:windowtext;mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
                + "  italic'>AVISO: " + sintesis + "</span></span><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:Arial;color:windowtext;mso-ansi-language:ES-MX;font-style:\n"
                + "  normal;mso-bidi-font-style:italic'><o:p></o:p></span></span></p>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-layout-grid-align:\n"
                + "  none;text-autospace:none;mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:Arial;color:white;mso-ansi-language:ES-MX;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'><span style=\"mso-spacerun:yes\"> </span><o:p></o:p></span></span></p>\n"
                + "  </td>\n"
                + " </tr>\n"
                + " <tr style='mso-yfti-irow:4;height:228.6pt'>\n"
                + "  <td width=301 colspan=9 style='width:301.2pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:228.6pt'>\n"
                + "  <p class=MsoNormal align=center style='margin-right:2.85pt;text-align:center;\n"
                + "  mso-element:frame;mso-element-frame-hspace:7.05pt;mso-element-wrap:around;\n"
                + "  mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:column;\n"
                + "  mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES-MX\n"
                + "  style='mso-ansi-language:ES-MX;mso-fareast-language:ES-MX;mso-no-proof:yes'><!--[if gte vml 1]><v:shapetype\n"
                + "   id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\"\n"
                + "   path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">\n"
                + "   <v:stroke joinstyle=\"miter\"/>\n"
                + "   <v:formulas>\n"
                + "    <v:f eqn=\"if lineDrawn pixelLineWidth 0\"/>\n"
                + "    <v:f eqn=\"sum @0 1 0\"/>\n"
                + "    <v:f eqn=\"sum 0 0 @1\"/>\n"
                + "    <v:f eqn=\"prod @2 1 2\"/>\n"
                + "    <v:f eqn=\"prod @3 21600 pixelWidth\"/>\n"
                + "    <v:f eqn=\"prod @3 21600 pixelHeight\"/>\n"
                + "    <v:f eqn=\"sum @0 0 1\"/>\n"
                + "    <v:f eqn=\"prod @6 1 2\"/>\n"
                + "    <v:f eqn=\"prod @7 21600 pixelWidth\"/>\n"
                + "    <v:f eqn=\"sum @8 21600 0\"/>\n"
                + "    <v:f eqn=\"prod @7 21600 pixelHeight\"/>\n"
                + "    <v:f eqn=\"sum @10 21600 0\"/>\n"
                + "   </v:formulas>\n"
                + "   <v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>\n"
                + "   <o:lock v:ext=\"edit\" aspectratio=\"t\"/>\n"
                + "  </v:shapetype><v:shape id=\"Imagen_x0020_1\" o:spid=\"_x0000_i1025\" type=\"#_x0000_t75\"\n"
                + "   style='width:291pt;height:259pt;visibility:visible'>\n"
                + "   <v:imagedata src=\"" + imgFile + "\" />\n"
                + "  </v:shape><![endif]--><![if !vml]><img width=293 height=261\n"
                + "  src=\"" + imgFile + "\" v:shapes=\"Imagen_x0020_1\"><![endif]></span><span\n"
                + "  lang=ES-MX style='font-size:10.0pt;font-family:\"Arial Narrow\";mso-bidi-font-family:\n"
                + "  Arial;mso-ansi-language:ES-MX'><o:p></o:p></span></p>\n"
                + "  <p class=MsoNormal align=center style='margin-right:2.85pt;text-align:center;\n"
                + "  mso-element:frame;mso-element-frame-hspace:7.05pt;mso-element-wrap:around;\n"
                + "  mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:column;\n"
                + "  mso-element-top:.05pt;mso-height-rule:exactly'><span lang=ES-MX\n"
                + "  style='font-size:10.0pt;font-family:\"Arial Narrow\";mso-bidi-font-family:Arial;\n"
                + "  mso-ansi-language:ES-MX'>" + leyendaImg + "<o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=213 colspan=10 style='width:212.6pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n"
                + "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n"
                + "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:228.6pt'>\n"
                + "  <p class=MsoNormal style='text-align:justify'><em><b style='mso-bidi-font-weight:\n"
                + "  normal'><u><span lang=ES style='font-size:10.0pt;mso-bidi-font-size:12.0pt;\n"
                + "  font-family:\"Arial Narrow\";font-style:normal;mso-bidi-font-style:italic'>SITUACI&Oacute;N\n"
                + "  ACTUAL:<o:p></o:p></span></u></b></em></p>\n"
                + "  <p class=MsoNormal style='text-align:justify'><em><span lang=ES\n"
                + "  style='font-size:10.0pt;mso-bidi-font-size:12.0pt;font-family:\"Arial Narrow\";\n"
                + "  font-style:normal;mso-bidi-font-style:italic'><o:p>&nbsp;</o:p></span></em></p>\n"
                + "  <p class=MsoNormal><span lang=ES style='font-size:10.0pt;font-family:\"Arial Narrow\"'>"
                + situacionActual + "</span></p>\n"
                + "  </td>\n"
                + " </tr>"
                + " <tr style='mso-yfti-irow:5;height:26.55pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  background:#BFBFBF;padding:0cm 5.4pt 0cm 5.4pt;height:26.55pt'>\n"
                + "  <p class=MsoNormal align=center style='margin-right:2.85pt;text-align:center;\n"
                + "  mso-element:frame;mso-element-frame-hspace:7.05pt;mso-element-wrap:around;\n"
                + "  mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:column;\n"
                + "  mso-element-top:.05pt;mso-height-rule:exactly'><b style='mso-bidi-font-weight:\n"
                + "  normal'><span lang=ES style='font-size:10.0pt;font-family:\"Arial Narrow\";\n"
                + "  mso-bidi-font-family:Arial'>SECCI&Oacute;N A. CONDICIONES ACTUALES<o:p></o:p></span></b></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String getEncabezado(String backgrndImg, String titulo, String textoFecha,
            String avisoNum, String horaEmision, String leyendaSistema) {
        return "<body bgcolor=white background=\"" + backgrndImg + "\"\n"
                + "lang=EN-US link=blue vlink=purple style='tab-interval:35.4pt'>\n"
                + "<!--[if gte mso 9]><xml>\n"
                + " <v:background id=\"_x0000_s1025\" o:bwmode=\"white\" o:targetscreensize=\"800,600\">\n"
                + "  <v:fill src=\"" + backgrndImg + "\" o:title=\"NUEVA PLANTILLA\"\n"
                + "   type=\"frame\"/>\n"
                + " </v:background></xml><![endif]-->\n"
                + "\n"
                + "<div class=WordSection1>\n"
                + "\n"
                + "<table class=MsoNormalTable border=1 cellspacing=0 cellpadding=0 align=left\n"
                + " width=650 style='margin-left:9.0pt;border-collapse:collapse;mso-table-layout-alt:\n" //514
                + " fixed;border:none;mso-border-alt:solid windowtext .5pt;mso-table-overlap:never;\n"
                + " mso-yfti-tbllook:480;mso-table-lspace:7.05pt;margin-left:4.05pt;mso-table-rspace:\n"
                + " 7.05pt;margin-right:4.05pt;mso-table-anchor-vertical:paragraph;mso-table-anchor-horizontal:\n"
                + " column;mso-table-left:left;mso-table-top:.05pt;mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n"
                + " mso-border-insideh:.5pt solid windowtext;mso-border-insidev:.5pt solid windowtext'>\n"
                + " <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:41.8pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid #262626 1.0pt;\n"
                + "  mso-border-alt:solid #262626 .75pt;padding:0cm 5.4pt 0cm 5.4pt;height:41.8pt'>\n"
                + "  <p class=MsoNormal style='mso-layout-grid-align:none;text-autospace:none;\n"
                + "  mso-element:frame;mso-element-frame-hspace:7.05pt;mso-element-wrap:around;\n"
                + "  mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:column;\n"
                + "  mso-element-top:.05pt;mso-height-rule:exactly'><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:14.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:\"Arial Rounded MT Bold\";mso-bidi-font-family:Arial;\n"
                + "  color:#C00000;mso-ansi-language:ES-MX;font-weight:normal;mso-bidi-font-weight:\n"
                + "  bold;font-style:normal;mso-bidi-font-style:italic'>" + titulo + "</span></span><span lang=ES-MX style='font-size:14.0pt;\n"
                + "  mso-bidi-font-size:10.0pt;font-family:\"Arial Rounded MT Bold\";mso-bidi-font-family:\n"
                + "  Arial;color:#C00000;mso-ansi-language:ES-MX;mso-bidi-font-weight:bold;\n"
                + "  mso-bidi-font-style:italic'><o:p></o:p></span></p>\n"
                + "  </td>\n"
                + "  <td width=277 colspan=12 style='width:276.7pt;border:solid #262626 1.0pt;\n"
                + "  border-left:none;mso-border-left-alt:solid #262626 .75pt;mso-border-alt:solid #262626 .75pt;\n"
                + "  padding:0cm 5.4pt 0cm 5.4pt;height:41.8pt'>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;text-autospace:none'><b><span\n"
                + "  lang=ES style='font-size:10.0pt;font-family:Arial;color:#C00000;mso-fareast-language:\n"
                + "  ES-MX'><o:p>&nbsp;</o:p></span></b></p>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;text-autospace:none'><b><span\n"
                + "  lang=ES style='font-size:10.0pt;font-family:Arial;color:#C00000;mso-fareast-language:\n"
                + "  ES-MX'>COMISI&Oacute;N NACIONAL DEL AGUA</span></b><span lang=ES-MX\n"
                + "  style='color:#C00000;mso-ansi-language:ES-MX;mso-fareast-language:ES-MX'><o:p></o:p></span></p>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;page-break-after:avoid;\n"
                + "  mso-outline-level:8'><b style='mso-bidi-font-weight:normal'><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:Arial;color:#C00000;mso-fareast-language:\n"
                + "  ES-MX'>Servicio Meteorol&oacute;gico Nacional</span></b><span lang=ES\n"
                + "  style='font-size:10.0pt;font-family:Arial;color:#C00000;mso-fareast-language:\n"
                + "  ES-MX;mso-bidi-font-weight:bold'><o:p></o:p></span></p>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;mso-element:frame;\n"
                + "  mso-element-frame-hspace:7.05pt;mso-element-wrap:around;mso-element-anchor-vertical:\n"
                + "  paragraph;mso-element-anchor-horizontal:column;mso-element-top:.05pt;\n"
                + "  mso-height-rule:exactly'><span lang=ES style='mso-no-proof:yes'><o:p>&nbsp;</o:p></span></p>\n"
                + "  </td>\n"
                + " </tr>\n"
                + " <tr style='mso-yfti-irow:1;height:34.9pt'>\n"
                + "  <td width=237 colspan=7 style='width:237.1pt;border:solid #262626 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid #262626 .75pt;mso-border-alt:solid #262626 .75pt;\n"
                + "  background:#A6A6A6;padding:0cm 5.4pt 0cm 5.4pt;height:34.9pt'>\n"
                + "  <p class=MsoNormal style='mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><b style='mso-bidi-font-weight:\n"
                + "  normal'><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:10.0pt;\n"
                + "  font-family:Arial;mso-ansi-language:ES-MX'>M&eacute;xico, D.F. a " + textoFecha + "<o:p></o:p></span></b></p>\n"
                + "  <p class=MsoNormal style='mso-layout-grid-align:none;text-autospace:none;\n"
                + "  mso-element:frame;mso-element-frame-hspace:7.05pt;mso-element-wrap:around;\n"
                + "  mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:column;\n"
                + "  mso-element-top:.05pt;mso-height-rule:exactly'><b style='mso-bidi-font-weight:\n"
                + "  normal'><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:10.0pt;\n"
                + "  font-family:Arial;mso-ansi-language:ES-MX'>Aviso No. " + avisoNum + "<span\n"
                + "  class=MsoIntenseEmphasis><span style='color:windowtext;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'><o:p></o:p></span></span></span></b></p>\n"
                + "  </td>\n"
                + "  <td width=277 colspan=12 style='width:276.7pt;border-top:none;border-left:\n"
                + "  none;border-bottom:solid #262626 1.0pt;border-right:solid #262626 1.0pt;\n"
                + "  mso-border-top-alt:solid #262626 .75pt;mso-border-left-alt:solid #262626 .75pt;\n"
                + "  mso-border-alt:solid #262626 .75pt;background:#A6A6A6;padding:0cm 5.4pt 0cm 5.4pt;\n"
                + "  height:34.9pt'>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;mso-layout-grid-align:\n"
                + "  none;text-autospace:none;mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;font-family:\n"
                + "  Arial;color:windowtext;mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:\n"
                + "  italic'>Emisi&oacute;n: " + horaEmision + " horas (tiempo del Centro)<o:p></o:p></span></span></p>\n"
                + "  <p class=MsoNormal align=right style='text-align:right;mso-layout-grid-align:\n"
                + "  none;text-autospace:none;mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:10.0pt;font-family:\n"
                + "  \"Arial Narrow\";mso-bidi-font-family:Arial;color:#C00000;mso-ansi-language:\n"
                + "  ES-MX;font-weight:normal;mso-bidi-font-weight:bold;font-style:normal;\n"
                + "  mso-bidi-font-style:italic'>(" + leyendaSistema + ")</span></span><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:\"Arial Narrow\";mso-bidi-font-family:Arial;color:windowtext;\n"
                + "  mso-ansi-language:ES-MX;font-style:normal;mso-bidi-font-style:italic'><o:p></o:p></span></span></p>\n"
                + "  </td>\n"
                + " </tr>"
                + "<tr style='mso-yfti-irow:2;height:16.3pt'>\n"
                + "  <td width=514 colspan=19 style='width:513.8pt;border:solid windowtext 1.0pt;\n"
                + "  border-top:none;mso-border-top-alt:solid #262626 .75pt;mso-border-alt:solid windowtext .5pt;\n"
                + "  mso-border-top-alt:solid #262626 .75pt;padding:0cm 5.4pt 0cm 5.4pt;\n"
                + "  height:16.3pt'>\n"
                + "  <p class=MsoNormal align=center style='text-align:center;mso-layout-grid-align:\n"
                + "  none;text-autospace:none;mso-element:frame;mso-element-frame-hspace:7.05pt;\n"
                + "  mso-element-wrap:around;mso-element-anchor-vertical:paragraph;mso-element-anchor-horizontal:\n"
                + "  column;mso-element-top:.05pt;mso-height-rule:exactly'><i style='mso-bidi-font-style:\n"
                + "  normal'><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:10.0pt;\n"
                + "  font-family:\"Arial Narrow\";mso-bidi-font-family:Arial;color:#002060;\n"
                + "  mso-ansi-language:ES-MX;mso-bidi-font-weight:bold'>El Servicio Meteorol&oacute;gico\n"
                + "  Nacional dependiente de la CONAGUA (fuente oficial del Gobierno de M&eacute;xico) en\n"
                + "  el marco del Sistema Nacional de Protecci&oacute;n Civil y en Coordinaci&oacute;n con el\n"
                + "  CMRE de la Organizaci&oacute;n Meteorol&oacute;gica Mundial de Miami, Fl. emite el siguiente aviso:</span></i><span\n"
                + "  class=MsoIntenseEmphasis><span lang=ES-MX style='font-size:9.0pt;mso-bidi-font-size:\n"
                + "  10.0pt;font-family:Arial;color:#002060;mso-ansi-language:ES-MX;font-weight:\n"
                + "  normal;mso-bidi-font-weight:bold;font-style:normal;mso-bidi-font-style:italic'><o:p></o:p></span></span></p>\n"
                + "  </td>\n"
                + " </tr>";
    }

    private static String header = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\n" +
"xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
"xmlns:w=\"urn:schemas-microsoft-com:office:word\"\n" +
"xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\n" +
"xmlns:mv=\"http://macVmlSchemaUri\" xmlns=\"http://www.w3.org/TR/REC-html40\">\n" +
"\n" +
"<head>\n" +
"<meta name=Title content=\"\">\n" +
"<meta name=Keywords content=\"\">\n" +
"<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n" +
"<meta name=ProgId content=Word.Document>\n" +
"<meta name=Generator content=\"Microsoft Word 14\">\n" +
"<meta name=Originator content=\"Microsoft Word 14\">\n" +
"<!--[if !mso]>\n" +
"<style>\n" +
"v\\:* {behavior:url(#default#VML);}\n" +
"o\\:* {behavior:url(#default#VML);}\n" +
"w\\:* {behavior:url(#default#VML);}\n" +
".shape {behavior:url(#default#VML);}\n" +
"</style>\n" +
"<![endif]--><!--[if gte mso 9]><xml>\n" +
" <o:DocumentProperties>\n" +
"  <o:Author>Administrador</o:Author>\n" +
"  <o:LastAuthor></o:LastAuthor>\n" +
"  <o:Revision>2</o:Revision>\n" +
"  <o:TotalTime>0</o:TotalTime>\n" +
"  <o:LastPrinted>2013-09-19T03:27:00Z</o:LastPrinted>\n" +
"  <o:Created>2014-04-10T16:57:00Z</o:Created>\n" +
"  <o:LastSaved>2014-04-10T16:57:00Z</o:LastSaved>\n" +
"  <o:Pages>2</o:Pages>\n" +
"  <o:Words>1317</o:Words>\n" +
"  <o:Characters>7248</o:Characters>\n" +
"  <o:Company>CNA</o:Company>\n" +
"  <o:Lines>60</o:Lines>\n" +
"  <o:Paragraphs>17</o:Paragraphs>\n" +
"  <o:CharactersWithSpaces>8548</o:CharactersWithSpaces>\n" +
"  <o:Version>14.0</o:Version>\n" +
" </o:DocumentProperties>\n" +
" <o:OfficeDocumentSettings>\n" +
"  <o:DoNotOrganizeinFolder/>\n" +
"  <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
"  <o:TargetScreenSize>800x600</o:TargetScreenSize>\n" +
" </o:OfficeDocumentSettings>\n" +
"</xml><![endif]-->\n" +
"<!--[if gte mso 9]><xml>\n" +
" <w:WordDocument>\n" +
"  <w:DisplayBackgroundShape/>\n" +
"  <w:HideSpellingErrors/>\n" +
"  <w:HideGrammaticalErrors/>\n" +
"  <w:SpellingState>Clean</w:SpellingState>\n" +
"  <w:GrammarState>Clean</w:GrammarState>\n" +
"  <w:TrackMoves>false</w:TrackMoves>\n" +
"  <w:TrackFormatting/>\n" +
"  <w:HyphenationZone>21</w:HyphenationZone>\n" +
"  <w:PunctuationKerning/>\n" +
"  <w:ValidateAgainstSchemas/>\n" +
"  <w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid>\n" +
"  <w:IgnoreMixedContent>false</w:IgnoreMixedContent>\n" +
"  <w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText>\n" +
"  <w:DoNotPromoteQF/>\n" +
"  <w:LidThemeOther>EN-US</w:LidThemeOther>\n" +
"  <w:LidThemeAsian>JA</w:LidThemeAsian>\n" +
"  <w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript>\n" +
"  <w:Compatibility>\n" +
"   <w:BreakWrappedTables/>\n" +
"   <w:SnapToGridInCell/>\n" +
"   <w:WrapTextWithPunct/>\n" +
"   <w:UseAsianBreakRules/>\n" +
"   <w:UseWord2002TableStyleRules/>\n" +
"   <w:DontUseIndentAsNumberingTabStop/>\n" +
"   <w:FELineBreak11/>\n" +
"   <w:WW11IndentRules/>\n" +
"   <w:DontAutofitConstrainedTables/>\n" +
"   <w:AutofitLikeWW11/>\n" +
"   <w:HangulWidthLikeWW11/>\n" +
"   <w:UseNormalStyleForList/>\n" +
"   <w:DontVertAlignCellWithSp/>\n" +
"   <w:DontBreakConstrainedForcedTables/>\n" +
"   <w:DontVertAlignInTxbx/>\n" +
"   <w:Word11KerningPairs/>\n" +
"   <w:CachedColBalance/>\n" +
"  </w:Compatibility>\n" +
"  <m:mathPr>\n" +
"   <m:mathFont m:val=\"Cambria Math\"/>\n" +
"   <m:brkBin m:val=\"before\"/>\n" +
"   <m:brkBinSub m:val=\"&#45;-\"/>\n" +
"   <m:smallFrac m:val=\"off\"/>\n" +
"   <m:dispDef/>\n" +
"   <m:lMargin m:val=\"0\"/>\n" +
"   <m:rMargin m:val=\"0\"/>\n" +
"   <m:defJc m:val=\"centerGroup\"/>\n" +
"   <m:wrapIndent m:val=\"1440\"/>\n" +
"   <m:intLim m:val=\"subSup\"/>\n" +
"   <m:naryLim m:val=\"undOvr\"/>\n" +
"  </m:mathPr></w:WordDocument>\n" +
"</xml><![endif]--><!--[if gte mso 9]><xml>\n" +
" <w:LatentStyles DefLockedState=\"false\" DefUnhideWhenUsed=\"false\"\n" +
"  DefSemiHidden=\"false\" DefQFormat=\"false\" LatentStyleCount=\"276\">\n" +
"  <w:LsdException Locked=\"false\" QFormat=\"true\" Name=\"Normal\"/>\n" +
"  <w:LsdException Locked=\"false\" QFormat=\"true\" Name=\"heading 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 6\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 7\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 8\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"heading 9\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   QFormat=\"true\" Name=\"caption\"/>\n" +
"  <w:LsdException Locked=\"false\" QFormat=\"true\" Name=\"Title\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"1\" Name=\"Default Paragraph Font\"/>\n" +
"  <w:LsdException Locked=\"false\" QFormat=\"true\" Name=\"Subtitle\"/>\n" +
"  <w:LsdException Locked=\"false\" QFormat=\"true\" Name=\"Strong\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"20\" QFormat=\"true\" Name=\"Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"99\" Name=\"Normal (Web)\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"99\" Name=\"No List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"99\" Name=\"Balloon Text\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"99\" SemiHidden=\"true\"\n" +
"   Name=\"Note Level 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"1\" QFormat=\"true\" Name=\"Note Level 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Note Level 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Note Level 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Note Level 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Note Level 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Note Level 7\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Note Level 8\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Note Level 9\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Placeholder Text\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"No Spacing\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Medium List 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"99\" SemiHidden=\"true\"\n" +
"   Name=\"Colorful Shading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"34\" QFormat=\"true\"\n" +
"   Name=\"Colorful List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"29\" QFormat=\"true\"\n" +
"   Name=\"Colorful Grid\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"30\" QFormat=\"true\"\n" +
"   Name=\"Light Shading Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Light List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Light Grid Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Shading 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Shading 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Medium List 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Revision\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"List Paragraph\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Quote\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Intense Quote\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Colorful Shading Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Colorful List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Colorful Grid Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Medium List 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Colorful Shading Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Colorful List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Colorful Grid Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Medium List 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Colorful Shading Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Colorful List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Colorful Grid Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Medium List 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Colorful Shading Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Colorful List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Colorful Grid Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Medium List 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Medium List 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Medium Grid 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Grid 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Grid 3 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Dark List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Colorful Shading Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Colorful List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Colorful Grid Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Light Shading Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Light List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Light Grid Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Medium Shading 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Medium Shading 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"19\" QFormat=\"true\"\n" +
"   Name=\"Medium List 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"21\" QFormat=\"true\"\n" +
"   Name=\"Medium List 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"31\" QFormat=\"true\"\n" +
"   Name=\"Medium Grid 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"32\" QFormat=\"true\"\n" +
"   Name=\"Medium Grid 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"33\" QFormat=\"true\"\n" +
"   Name=\"Medium Grid 3 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"37\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"Dark List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"Colorful Shading Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"19\" QFormat=\"true\"\n" +
"   Name=\"Subtle Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"21\" QFormat=\"true\"\n" +
"   Name=\"Intense Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"31\" QFormat=\"true\"\n" +
"   Name=\"Subtle Reference\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"32\" QFormat=\"true\"\n" +
"   Name=\"Intense Reference\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"33\" QFormat=\"true\" Name=\"Book Title\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"37\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"Bibliography\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"TOC Heading\"/>\n" +
" </w:LatentStyles>\n" +
"</xml><![endif]-->\n" +
"<style>\n" +
"<!--\n" +
" /* Font Definitions */\n" +
"@font-face\n" +
"	{font-family:Arial;\n" +
"	panose-1:2 11 6 4 2 2 2 2 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:-536859905 -1073711037 9 0 511 0;}\n" +
"@font-face\n" +
"	{font-family:\"Courier New\";\n" +
"	panose-1:2 7 3 9 2 2 5 2 4 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:-536859905 -1073711037 9 0 511 0;}\n" +
"@font-face\n" +
"	{font-family:Wingdings;\n" +
"	panose-1:5 0 0 0 0 0 0 0 0 0;\n" +
"	mso-font-charset:2;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:0 268435456 0 0 -2147483648 0;}\n" +
"@font-face\n" +
"	{font-family:Wingdings;\n" +
"	panose-1:5 0 0 0 0 0 0 0 0 0;\n" +
"	mso-font-charset:2;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:0 268435456 0 0 -2147483648 0;}\n" +
"@font-face\n" +
"	{font-family:Calibri;\n" +
"	panose-1:2 15 5 2 2 2 4 3 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:-520092929 1073786111 9 0 415 0;}\n" +
"@font-face\n" +
"	{font-family:Cambria;\n" +
"	panose-1:2 4 5 3 5 4 6 3 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:-536870145 1073743103 0 0 415 0;}\n" +
"@font-face\n" +
"	{font-family:\"Arial Rounded MT Bold\";\n" +
"	panose-1:2 15 7 4 3 5 4 3 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:3 0 0 0 1 0;}\n" +
"@font-face\n" +
"	{font-family:\"Arial Narrow\";\n" +
"	panose-1:2 11 6 6 2 2 2 3 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:647 2048 0 0 159 0;}\n" +
"@font-face\n" +
"	{font-family:Tahoma;\n" +
"	panose-1:2 11 6 4 3 5 4 4 2 4;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:auto;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:-520082689 -1073717157 41 0 66047 0;}\n" +
" /* Style Definitions */\n" +
"p.MsoNormal, li.MsoNormal, div.MsoNormal\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:12.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"h1\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 1 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	text-align:center;\n" +
"	mso-pagination:widow-orphan;\n" +
"	page-break-after:avoid;\n" +
"	mso-outline-level:1;\n" +
"	font-size:9.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-font-kerning:0pt;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"h2\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 2 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	text-align:center;\n" +
"	mso-pagination:widow-orphan;\n" +
"	page-break-after:avoid;\n" +
"	mso-outline-level:2;\n" +
"	font-size:8.0pt;\n" +
"	mso-bidi-font-size:9.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:X-NONE;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"h3\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 3 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin-top:12.0pt;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:3.0pt;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	page-break-after:avoid;\n" +
"	mso-outline-level:3;\n" +
"	font-size:13.0pt;\n" +
"	font-family:Cambria;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"h4\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 4 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin-top:12.0pt;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:3.0pt;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	page-break-after:avoid;\n" +
"	mso-outline-level:4;\n" +
"	font-size:14.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"h5\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 5 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin-top:12.0pt;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:3.0pt;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	mso-outline-level:5;\n" +
"	font-size:13.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;\n" +
"	font-style:italic;}\n" +
"p.MsoHeading8, li.MsoHeading8, div.MsoHeading8\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo 8 Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin-top:12.0pt;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:3.0pt;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	mso-outline-level:8;\n" +
"	font-size:12.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-style:italic;}\n" +
"p.MsoHeader, li.MsoHeader, div.MsoHeader\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-link:\"Encabezado Car\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	tab-stops:center 220.95pt right 441.9pt;\n" +
"	font-size:12.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"p.MsoFooter, li.MsoFooter, div.MsoFooter\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-link:\"Pie de p\\00E1gina Car\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	tab-stops:center 220.95pt right 441.9pt;\n" +
"	font-size:12.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"p.MsoTitle, li.MsoTitle, div.MsoTitle\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-link:\"T\\00EDtulo Car\";\n" +
"	mso-style-next:Normal;\n" +
"	margin-top:12.0pt;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:3.0pt;\n" +
"	margin-left:0cm;\n" +
"	text-align:center;\n" +
"	mso-pagination:widow-orphan;\n" +
"	mso-outline-level:1;\n" +
"	font-size:16.0pt;\n" +
"	font-family:Cambria;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-font-kerning:14.0pt;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"p.MsoBodyText2, li.MsoBodyText2, div.MsoBodyText2\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-link:\"Texto de cuerpo 2 Car\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	text-align:justify;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:X-NONE;\n" +
"	mso-fareast-language:ES;}\n" +
"a:link, span.MsoHyperlink\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-parent:\"\";\n" +
"	color:blue;\n" +
"	text-decoration:underline;\n" +
"	text-underline:single;}\n" +
"a:visited, span.MsoHyperlinkFollowed\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-parent:\"\";\n" +
"	color:purple;\n" +
"	text-decoration:underline;\n" +
"	text-underline:single;}\n" +
"p\n" +
"	{mso-style-priority:99;\n" +
"	mso-margin-top-alt:auto;\n" +
"	margin-right:0cm;\n" +
"	mso-margin-bottom-alt:auto;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:12.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES-MX;\n" +
"	mso-fareast-language:ES-MX;}\n" +
"p.MsoAcetate, li.MsoAcetate, div.MsoAcetate\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-priority:99;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-link:\"Texto de globo Car\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:8.0pt;\n" +
"	font-family:Tahoma;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"p.MsoNoSpacing, li.MsoNoSpacing, div.MsoNoSpacing\n" +
"	{mso-style-priority:1;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:11.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-fareast-font-family:Calibri;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES-MX;\n" +
"	mso-fareast-language:EN-US;}\n" +
"span.MsoIntenseEmphasis\n" +
"	{mso-style-priority:21;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	color:#4F81BD;\n" +
"	font-weight:bold;\n" +
"	font-style:italic;}\n" +
"span.Ttulo1Car\n" +
"	{mso-style-name:\"T\\00EDtulo 1 Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 1\";\n" +
"	mso-ansi-font-size:9.0pt;\n" +
"	mso-bidi-font-size:9.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-ascii-font-family:Arial;\n" +
"	mso-hansi-font-family:Arial;\n" +
"	mso-bidi-font-family:Arial;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"span.Ttulo2Car\n" +
"	{mso-style-name:\"T\\00EDtulo 2 Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 2\";\n" +
"	mso-ansi-font-size:8.0pt;\n" +
"	mso-bidi-font-size:9.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-ascii-font-family:Arial;\n" +
"	mso-hansi-font-family:Arial;\n" +
"	mso-bidi-font-family:Arial;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"span.Textodecuerpo2Car\n" +
"	{mso-style-name:\"Texto de cuerpo 2 Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"Texto de cuerpo 2\";\n" +
"	font-family:Arial;\n" +
"	mso-ascii-font-family:Arial;\n" +
"	mso-hansi-font-family:Arial;\n" +
"	mso-bidi-font-family:Arial;\n" +
"	mso-fareast-language:ES;}\n" +
"span.apple-converted-space\n" +
"	{mso-style-name:apple-converted-space;\n" +
"	mso-style-unhide:no;}\n" +
"span.TtuloCar\n" +
"	{mso-style-name:\"T\\00EDtulo Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:T\\00EDtulo;\n" +
"	mso-ansi-font-size:16.0pt;\n" +
"	mso-bidi-font-size:16.0pt;\n" +
"	font-family:Cambria;\n" +
"	mso-ascii-font-family:Cambria;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:Cambria;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-font-kerning:14.0pt;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"span.apple-style-span\n" +
"	{mso-style-name:apple-style-span;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-parent:\"\";}\n" +
"span.EncabezadoCar\n" +
"	{mso-style-name:\"Encabezado Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:Encabezado;\n" +
"	mso-ansi-font-size:12.0pt;\n" +
"	mso-bidi-font-size:12.0pt;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"span.PiedepginaCar\n" +
"	{mso-style-name:\"Pie de p\\00E1gina Car\";\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"Pie de p\\00E1gina\";\n" +
"	mso-ansi-font-size:12.0pt;\n" +
"	mso-bidi-font-size:12.0pt;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"p.Default, li.Default, div.Default\n" +
"	{mso-style-name:Default;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-parent:\"\";\n" +
"	margin:0cm;\n" +
"	margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	mso-layout-grid-align:none;\n" +
"	text-autospace:none;\n" +
"	font-size:12.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:Calibri;\n" +
"	color:black;\n" +
"	mso-ansi-language:ES-MX;\n" +
"	mso-fareast-language:ES-MX;}\n" +
"span.Ttulo3Car\n" +
"	{mso-style-name:\"T\\00EDtulo 3 Car\";\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 3\";\n" +
"	mso-ansi-font-size:13.0pt;\n" +
"	mso-bidi-font-size:13.0pt;\n" +
"	font-family:Cambria;\n" +
"	mso-ascii-font-family:Cambria;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:Cambria;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"span.Ttulo4Car\n" +
"	{mso-style-name:\"T\\00EDtulo 4 Car\";\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 4\";\n" +
"	mso-ansi-font-size:14.0pt;\n" +
"	mso-bidi-font-size:14.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-ascii-font-family:Calibri;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:Calibri;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;}\n" +
"span.Ttulo5Car\n" +
"	{mso-style-name:\"T\\00EDtulo 5 Car\";\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 5\";\n" +
"	mso-ansi-font-size:13.0pt;\n" +
"	mso-bidi-font-size:13.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-ascii-font-family:Calibri;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:Calibri;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-weight:bold;\n" +
"	font-style:italic;}\n" +
"span.TextodegloboCar\n" +
"	{mso-style-name:\"Texto de globo Car\";\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-priority:99;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"Texto de globo\";\n" +
"	mso-ansi-font-size:8.0pt;\n" +
"	mso-bidi-font-size:8.0pt;\n" +
"	font-family:Tahoma;\n" +
"	mso-ascii-font-family:Tahoma;\n" +
"	mso-hansi-font-family:Tahoma;\n" +
"	mso-bidi-font-family:Tahoma;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;}\n" +
"span.Ttulo8Car\n" +
"	{mso-style-name:\"T\\00EDtulo 8 Car\";\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-unhide:no;\n" +
"	mso-style-locked:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-style-link:\"T\\00EDtulo 8\";\n" +
"	mso-ansi-font-size:12.0pt;\n" +
"	mso-bidi-font-size:12.0pt;\n" +
"	font-family:Calibri;\n" +
"	mso-ascii-font-family:Calibri;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:Calibri;\n" +
"	mso-bidi-font-family:\"Times New Roman\";\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:ES;\n" +
"	font-style:italic;}\n" +
"span.SpellE\n" +
"	{mso-style-name:\"\";\n" +
"	mso-spl-e:yes;}\n" +
"span.GramE\n" +
"	{mso-style-name:\"\";\n" +
"	mso-gram-e:yes;}\n" +
" /* Page Definitions */\n" +
"@page\n" +
"	{}\n" +
"@page WordSection1\n" +
"	{size:612.1pt 792.1pt;\n" +
"	margin:2.0cm 42.55pt 2.0cm 42.55pt;\n" +
"	mso-header-margin:35.45pt;\n" +
"	mso-footer-margin:35.45pt;\n" +
"	border:solid #262626 1.0pt;\n" +
"	mso-border-alt:solid #262626 .75pt;\n" +
"	padding:24.0pt 24.0pt 24.0pt 24.0pt;\n" +
"	mso-paper-source:0;}\n" +
"div.WordSection1\n" +
"	{page:WordSection1;}\n" +
" /* List Definitions */\n" +
"@list l0\n" +
"	{mso-list-id:-227;\n" +
"	mso-list-template-ids:955830696;}\n" +
"@list l0:level1\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\"\";\n" +
"	mso-level-tab-stop:0cm;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:0cm;\n" +
"	text-indent:0cm;\n" +
"	font-family:Symbol;}\n" +
"@list l0:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:36.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:54.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l0:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:72.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:90.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";}\n" +
"@list l0:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:108.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:126.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l0:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0FA;\n" +
"	mso-level-tab-stop:144.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:162.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l0:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:180.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:198.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l0:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:216.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:234.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";}\n" +
"@list l0:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:252.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:270.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l0:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0FA;\n" +
"	mso-level-tab-stop:288.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:306.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l1\n" +
"	{mso-list-id:749304768;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:-618123888 1434646496 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l1:level1\n" +
"	{mso-level-start-at:0;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:-;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Arial Narrow\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";}\n" +
"@list l1:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l1:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l1:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l1:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l1:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l1:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l1:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l1:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l2\n" +
"	{mso-list-id:848058456;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:2012409950 -1180401508 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l2:level1\n" +
"	{mso-level-start-at:0;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:-;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:25.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Arial;\n" +
"	mso-fareast-font-family:\"Times New Roman\";}\n" +
"@list l2:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:61.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l2:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:97.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l2:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:133.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l2:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:169.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l2:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:205.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l2:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:241.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l2:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:277.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l2:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:313.1pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l3\n" +
"	{mso-list-id:1188183254;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:-384942520 -1941514060 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l3:level1\n" +
"	{mso-level-start-at:0;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	mso-ansi-font-size:8.0pt;\n" +
"	font-family:Symbol;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:Arial;\n" +
"	color:windowtext;}\n" +
"@list l3:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l3:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l3:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l3:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l3:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l3:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l3:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l3:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l4\n" +
"	{mso-list-id:1530795276;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:-1053284918 1340661272 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l4:level1\n" +
"	{mso-level-start-at:5;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0D8;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:34.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:Arial;}\n" +
"@list l4:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:70.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l4:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:106.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l4:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:142.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l4:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:178.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l4:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:214.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l4:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:250.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l4:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:286.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l4:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:322.5pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l5\n" +
"	{mso-list-id:1611623616;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:-169559542 -771612632 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l5:level1\n" +
"	{mso-level-start-at:5;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0D8;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:54.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:Arial;}\n" +
"@list l5:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:90.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l5:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:126.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l5:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:162.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l5:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:198.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l5:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:234.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l5:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:270.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l5:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:306.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l5:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:342.0pt;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l6\n" +
"	{mso-list-id:1613168690;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:2028375050 -335510630 207769896 -78509296 -585062602 1450608248 946754590 2040168020 10511874 -690207472;}\n" +
"@list l6:level1\n" +
"	{mso-level-text:\"%1\\)\";\n" +
"	mso-level-tab-stop:36.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level2\n" +
"	{mso-level-text:\"%2\\)\";\n" +
"	mso-level-tab-stop:72.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level3\n" +
"	{mso-level-text:\"%3\\)\";\n" +
"	mso-level-tab-stop:108.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level4\n" +
"	{mso-level-text:\"%4\\)\";\n" +
"	mso-level-tab-stop:144.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level5\n" +
"	{mso-level-text:\"%5\\)\";\n" +
"	mso-level-tab-stop:180.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level6\n" +
"	{mso-level-text:\"%6\\)\";\n" +
"	mso-level-tab-stop:216.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level7\n" +
"	{mso-level-text:\"%7\\)\";\n" +
"	mso-level-tab-stop:252.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level8\n" +
"	{mso-level-text:\"%8\\)\";\n" +
"	mso-level-tab-stop:288.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l6:level9\n" +
"	{mso-level-text:\"%9\\)\";\n" +
"	mso-level-tab-stop:324.0pt;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7\n" +
"	{mso-list-id:1624851241;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:-820483474 -876153158 134873113 134873115 134873103 134873113 134873115 134873103 134873113 134873115;}\n" +
"@list l7:level1\n" +
"	{mso-level-number-format:roman-upper;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:45.0pt;\n" +
"	text-indent:-36.0pt;}\n" +
"@list l7:level2\n" +
"	{mso-level-number-format:alpha-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:63.0pt;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7:level3\n" +
"	{mso-level-number-format:roman-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:right;\n" +
"	margin-left:99.0pt;\n" +
"	text-indent:-9.0pt;}\n" +
"@list l7:level4\n" +
"	{mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:135.0pt;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7:level5\n" +
"	{mso-level-number-format:alpha-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:171.0pt;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7:level6\n" +
"	{mso-level-number-format:roman-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:right;\n" +
"	margin-left:207.0pt;\n" +
"	text-indent:-9.0pt;}\n" +
"@list l7:level7\n" +
"	{mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:243.0pt;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7:level8\n" +
"	{mso-level-number-format:alpha-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	margin-left:279.0pt;\n" +
"	text-indent:-18.0pt;}\n" +
"@list l7:level9\n" +
"	{mso-level-number-format:roman-lower;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:right;\n" +
"	margin-left:315.0pt;\n" +
"	text-indent:-9.0pt;}\n" +
"@list l8\n" +
"	{mso-list-id:1769353050;\n" +
"	mso-list-type:hybrid;\n" +
"	mso-list-template-ids:1613027400 1462931760 134873091 134873093 134873089 134873091 134873093 134873089 134873091 134873093;}\n" +
"@list l8:level1\n" +
"	{mso-level-start-at:5;\n" +
"	mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0D8;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-bidi-font-family:Arial;}\n" +
"@list l8:level2\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l8:level3\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l8:level4\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l8:level5\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l8:level6\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"@list l8:level7\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0B7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Symbol;}\n" +
"@list l8:level8\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:o;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:\"Courier New\";}\n" +
"@list l8:level9\n" +
"	{mso-level-number-format:bullet;\n" +
"	mso-level-text:\\F0A7;\n" +
"	mso-level-tab-stop:none;\n" +
"	mso-level-number-position:left;\n" +
"	text-indent:-18.0pt;\n" +
"	font-family:Wingdings;}\n" +
"ol\n" +
"	{margin-bottom:0cm;}\n" +
"ul\n" +
"	{margin-bottom:0cm;}\n" +
"-->\n" +
"</style>\n" +
"<!--[if gte mso 10]>\n" +
"<style>\n" +
" /* Style Definitions */\n" +
"table.MsoNormalTable\n" +
"	{mso-style-name:\"Tabla normal\";\n" +
"	mso-tstyle-rowband-size:0;\n" +
"	mso-tstyle-colband-size:0;\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-priority:99;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";}\n" +
"table.MsoTableGrid\n" +
"	{mso-style-name:\"Tabla con cuadr\\00EDcula\";\n" +
"	mso-tstyle-rowband-size:0;\n" +
"	mso-tstyle-colband-size:0;\n" +
"	mso-style-unhide:no;\n" +
"	border:solid windowtext 1.0pt;\n" +
"	mso-border-alt:solid windowtext .5pt;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-border-insideh:.5pt solid windowtext;\n" +
"	mso-border-insidev:.5pt solid windowtext;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";}\n" +
"table.MsoTableMediumList2Accent1\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	border:solid #4F81BD 1.0pt;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:11.0pt;\n" +
"	font-family:Cambria;\n" +
"	color:black;\n" +
"	mso-ansi-language:ES;\n" +
"	mso-fareast-language:EN-US;}\n" +
"table.MsoTableMediumList2Accent1FirstRow\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:3.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	font-size:12.0pt;\n" +
"	mso-ansi-font-size:12.0pt;\n" +
"	mso-bidi-font-size:12.0pt;}\n" +
"table.MsoTableMediumList2Accent1LastRow\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList2Accent1FirstCol\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList2Accent1LastCol\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList2Accent1OddColumn\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D3DFEE;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList2Accent1OddRow\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D3DFEE;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList2Accent1NWCell\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:nw-cell;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;}\n" +
"table.MsoTableMediumList2Accent1SWCell\n" +
"	{mso-style-name:\"Lista mediana 2 - \\00C9nfasis 1\";\n" +
"	mso-table-condition:sw-cell;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:cell-none;}\n" +
"table.MsoTableColorfulGridAccent1\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-border-insideh:.5pt solid white;\n" +
"	mso-tstyle-shading:#DBE5F1;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	color:black;}\n" +
"table.MsoTableColorfulGridAccent1FirstRow\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#B8CCE4;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableColorfulGridAccent1LastRow\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#B8CCE4;\n" +
"	color:black;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableColorfulGridAccent1FirstCol\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#365F91;\n" +
"	color:white;}\n" +
"table.MsoTableColorfulGridAccent1LastCol\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#365F91;\n" +
"	color:white;}\n" +
"table.MsoTableColorfulGridAccent1OddColumn\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#A7BFDE;}\n" +
"table.MsoTableColorfulGridAccent1OddRow\n" +
"	{mso-style-name:\"Cuadr\\00EDcula multicolor - \\00C9nfasis 1\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:73;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#A7BFDE;}\n" +
"table.MsoTableLightShadingAccent5\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	border-top:solid #4BACC6 1.0pt;\n" +
"	border-left:none;\n" +
"	border-bottom:solid #4BACC6 1.0pt;\n" +
"	border-right:none;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	color:#31849B;}\n" +
"table.MsoTableLightShadingAccent5FirstRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableLightShadingAccent5LastRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableLightShadingAccent5FirstCol\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableLightShadingAccent5LastCol\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableLightShadingAccent5OddColumn\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableLightShadingAccent5OddRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumShading1Accent5\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	border:solid #78C0D4 1.0pt;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-border-insideh:1.0pt solid #78C0D4;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";}\n" +
"table.MsoTableMediumShading1Accent5FirstRow\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#4BACC6;\n" +
"	mso-tstyle-border-top:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-left:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-right:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	color:white;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumShading1Accent5LastRow\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:2.25pt double #78C0D4;\n" +
"	mso-tstyle-border-left:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-right:1.0pt solid #78C0D4;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumShading1Accent5FirstCol\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumShading1Accent5LastCol\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumShading1Accent5OddColumn\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;}\n" +
"table.MsoTableMediumShading1Accent5OddRow\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumShading1Accent5EvenRow\n" +
"	{mso-style-name:\"Sombreado mediano 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:even-row;\n" +
"	mso-style-priority:63;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.MsoTableMediumList1Accent5\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	border-top:solid #4BACC6 1.0pt;\n" +
"	border-left:none;\n" +
"	border-bottom:solid #4BACC6 1.0pt;\n" +
"	border-right:none;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	color:black;}\n" +
"table.MsoTableMediumList1Accent5FirstRow\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4BACC6;\n" +
"	font-family:\"Cambria Math\";\n" +
"	mso-ascii-font-family:\"Cambria Math\";\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-hansi-font-family:\"Cambria Math\";\n" +
"	mso-bidi-font-family:\"Times New Roman\";}\n" +
"table.MsoTableMediumList1Accent5LastRow\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4BACC6;\n" +
"	color:#1F497D;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumList1Accent5FirstCol\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumList1Accent5LastCol\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4BACC6;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4BACC6;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.MsoTableMediumList1Accent5OddColumn\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;}\n" +
"table.MsoTableMediumList1Accent5OddRow\n" +
"	{mso-style-name:\"Lista mediana 1 - \\00C9nfasis 5\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:65;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D2EAF1;}\n" +
"table.Sombreadoclaro-nfasis11\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	border-top:solid #4F81BD 1.0pt;\n" +
"	border-left:none;\n" +
"	border-bottom:solid #4F81BD 1.0pt;\n" +
"	border-right:none;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Times New Roman\";\n" +
"	color:#365F91;}\n" +
"table.Sombreadoclaro-nfasis11FirstRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.Sombreadoclaro-nfasis11LastRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:1.0pt solid #4F81BD;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	mso-para-margin-top:0cm;\n" +
"	mso-para-margin-bottom:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	line-height:normal;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.Sombreadoclaro-nfasis11FirstCol\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.Sombreadoclaro-nfasis11LastCol\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-ansi-font-weight:bold;\n" +
"	mso-bidi-font-weight:bold;}\n" +
"table.Sombreadoclaro-nfasis11OddColumn\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D3DFEE;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Sombreadoclaro-nfasis11OddRow\n" +
"	{mso-style-name:\"Sombreado claro - \\00C9nfasis 11\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:60;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:#D3DFEE;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-tstyle-rowband-size:1;\n" +
"	mso-tstyle-colband-size:1;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	border:solid black 1.0pt;\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:Cambria;\n" +
"	color:black;}\n" +
"table.Listamedia21FirstRow\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:first-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:3.0pt solid black;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;\n" +
"	font-size:12.0pt;\n" +
"	mso-ansi-font-size:12.0pt;\n" +
"	mso-bidi-font-size:12.0pt;}\n" +
"table.Listamedia21LastRow\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:last-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:1.0pt solid black;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21FirstCol\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:first-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:1.0pt solid black;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21LastCol\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:last-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-left:1.0pt solid black;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21OddColumn\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:odd-column;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:silver;\n" +
"	mso-tstyle-border-left:cell-none;\n" +
"	mso-tstyle-border-right:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21OddRow\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:odd-row;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:silver;\n" +
"	mso-tstyle-border-top:cell-none;\n" +
"	mso-tstyle-border-bottom:cell-none;\n" +
"	mso-tstyle-border-insideh:cell-none;\n" +
"	mso-tstyle-border-insidev:cell-none;}\n" +
"table.Listamedia21NWCell\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:nw-cell;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-shading:white;}\n" +
"table.Listamedia21SWCell\n" +
"	{mso-style-name:\"Lista media 21\";\n" +
"	mso-table-condition:sw-cell;\n" +
"	mso-style-priority:66;\n" +
"	mso-style-unhide:no;\n" +
"	mso-tstyle-border-top:cell-none;}\n" +
"</style>\n" +
"<![endif]--><!--[if gte mso 9]><xml>\n" +
" <o:shapedefaults v:ext=\"edit\" spidmax=\"2050\"/>\n" +
"</xml><![endif]--><!--[if gte mso 9]><xml>\n" +
" <o:shapelayout v:ext=\"edit\">\n" +
"  <o:idmap v:ext=\"edit\" data=\"1\"/>\n" +
" </o:shapelayout></xml><![endif]-->\n" +
"</head>\n" +
"";

}
