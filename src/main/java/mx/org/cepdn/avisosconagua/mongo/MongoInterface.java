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
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author serch
 */
public class MongoInterface {
    private final MongoClient mongoClient;
    private final MongoClientURI mongoClientURI;
    private final DB mongoDB;
    private static MongoInterface instance = null;
    private static final String CAPTURA_COL = "AdviceDataForms";
    private static final String INTERNAL_FORM_ID = "internalId";
    
    public static synchronized MongoInterface getInstance() {
        if (null == instance) {
            try {
                instance = new MongoInterface();
            } catch (UnknownHostException uhe) {
                System.out.println("Can't connect to MongoDB" + uhe.getLocalizedMessage());
            }
        }
        return instance;
    }
    
    private MongoInterface() throws UnknownHostException {
        if (null != System.getenv("MONGOHQ_URL")) {
            mongoClientURI = new MongoClientURI(System.getenv("MONGOHQ_URL"));
        } else {
            //mongodb://conagua:C0n4gu4@192.168.204.147/conagua
            //MONGOHQ_URL=mongodb://heroku:DnZ2AYC8nmtWR3p1Dccs4N9WSLUIrQQTrjcvfLrDlLo8V8yD4Pz6yV5mR5HPuTdEDx2b34v2W0qfufBHUBZlQg@oceanic.mongohq.com:10080/app23903821
            //mongoClientURI = new MongoClientURI("mongodb://heroku:DnZ2AYC8nmtWR3p1Dccs4N9WSLUIrQQTrjcvfLrDlLo8V8yD4Pz6yV5mR5HPuTdEDx2b34v2W0qfufBHUBZlQg@oceanic.mongohq.com:10080/app23903821");
            mongoClientURI = new MongoClientURI("mongodb://conagua:C0n4gu4@192.168.204.147/conagua");
        }
        mongoClient = new MongoClient(mongoClientURI);
        mongoDB = mongoClient.getDB(mongoClientURI.getDatabase());
        mongoDB.authenticate(mongoClientURI.getUsername(), mongoClientURI.getPassword());
    }
    
    public String[] getCollections() {
        Set<String> names = mongoDB.getCollectionNames();
        return names.toArray(new String[0]);
    }

    public BasicDBObject createNewAdvice(String currentId) {
        System.out.println("new advice:"+currentId);
        BasicDBObject newdata = new BasicDBObject(INTERNAL_FORM_ID, currentId);
        mongoDB.getCollection(CAPTURA_COL).insert(newdata);
        return newdata;
    }

    public BasicDBObject getAdvice(String currentId) {
        System.out.println("getAdvice: "+currentId);
        BasicDBObject newdata = new BasicDBObject(INTERNAL_FORM_ID, currentId);
        return (BasicDBObject)mongoDB.getCollection(CAPTURA_COL).findOne(newdata);
    }

    public void savePlainData(String currentId, String formId, HashMap<String, String> parametros) {
        System.out.println("saveAdvice: "+currentId+" screen:"+formId);
        BasicDBObject actual = getAdvice(currentId);
        BasicDBObject interno = new BasicDBObject(parametros);
        actual.append(formId, interno);
        mongoDB.getCollection(CAPTURA_COL).update(getAdvice(currentId), actual);
    }
}
