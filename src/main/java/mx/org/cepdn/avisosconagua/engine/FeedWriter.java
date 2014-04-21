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

package mx.org.cepdn.avisosconagua.engine;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.CapUtil;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import mx.org.cepdn.avisosconagua.mongo.MongoInterface;
import mx.org.cepdn.avisosconagua.util.Utils;
/**
 * Feed Writer using google cap-library utilities.
 * @author Hasdai Pacheco
 */
public class FeedWriter {
    private SyndFeed feed;
    private CapXmlBuilder builder;
    private static MongoInterface mi = MongoInterface.getInstance();
    private List<Alert> alerts;

    /**
     * Creates a new instance of a FeedWriter.
     * @param feedType Type for the feed, must be in supported types, defaults to "atom_1.0".
     */
    public FeedWriter(String feedType) {
        initFeed(feedType,"","");
    }
    
    /**
     * Creates a new instance of a FeedWriter.
     * @param feedType Type for the feed, must be in supported types, defaults to "atom_1.0".
     * @param feedTitle Title for the feed.
     * @param feedAuthor Author of the feed.
     */
    public FeedWriter (String feedType, String feedTitle, String feedAuthor) {
        initFeed(feedType, feedTitle, feedAuthor);
    }
    
    /**
     * Initializes feed writer.
     * @param feedType Type for the feed, must be in supported types, defaults to "atom_1.0".
     * @param feedTitle Title for the feed.
     * @param feedAuthor Author of the feed.
     */
    private void initFeed(String feedType, String feedTitle, String feedAuthor) {
        builder = new CapXmlBuilder(2);
        alerts = new ArrayList<>();
        feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setTitle(feedTitle);
        feed.setAuthor(feedAuthor);
        feed.setUri("http://smn.cna.gob.mx/capalert");
    }
    
    /**
     * Sets the title of the feed.
     * @param title Title.
     */
    public void setTitle(String title) {
        feed.setTitle(title);
    }
    
    /**
     * Sets the URI of the feed.
     * @param uri The URI.
     */
    public void setURI(String uri) {
        feed.setUri(uri);
    }
    
    /**
     * Sets the author of the feed.
     * @param author The autor.
     */
    public void setAuthor(String author) {
        feed.setAuthor(author);
    }
    
    /**
     * Sets publish date of the feed.
     * @param date The publish date.
     */
    public void setPubDate(Date date) {
        feed.setPublishedDate(date);
    }
    
    /**
     * Sets the list of alerts to generate the feed.
     * @param alertList List of alerts to be included in the feed.
     */
    public void setAlertList(List<Alert> alertList) {
        alerts = alertList;
    }
    
    /**
     * Gets the list of alerts defined for this feed.
     * @return List of alerts in the feed.
     */
    public List<Alert> getAlertList() {
        return alerts;
    }
    
    /**
     * Adds a CAP Alert to the list of alerts of this feed.
     * @param alert Alert to add to the feed.
     * @return 
     */
    public boolean addAlert(Alert alert) {
        return alerts.add(alert);
    }
    
    /**
     * Adds the provided alerts to the list of alerts for this feed.
     * @param allAlerts
     * @return 
     */
    public boolean addAllAlerts(List<Alert> allAlerts) {
        return alerts.addAll(allAlerts);
    }
    
    /**
     * Gets the XML for the feed.
     * @return String containing the XML code for the feed.
     */
    public String getXML() {
        String ret = "";
        List<SyndEntry> entries = new ArrayList<>();
        for(Alert alert : alerts) {
            if (!alert.getInfoList().isEmpty()) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setUri(alert.getIdentifier());
                entry.setTitle(alert.getInfo(0).getEvent());
                try {
                    entry.setUpdatedDate(Utils.isoformater.parse(alert.getSent()));
                } catch (ParseException ex) {

                }

                SyndContent cnt = new SyndContentImpl();
                cnt.setType("text/xml");
                cnt.setValue(CapUtil.stripXmlPreamble(builder.toXml(alert)));

                entry.setContents(Collections.singletonList(cnt));            
                entries.add(entry);
            }
        }
        feed.setEntries(entries);
        
        try {
            SyndFeedOutput sfo = new SyndFeedOutput();
            ret = sfo.outputString(feed);
        } catch (FeedException ex) {
            System.out.println("There was an exception writing feed contents");
        }
        
        return ret;
    }
}
