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
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.gridfs.GridFS;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Set;
import mx.org.cepdn.avisosconagua.util.Utils;

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
    public static final String INTERNAL_FORM_ID = "internalId";
    private static final String IMAGES_COL = "AdviceDataImages";
    private static final String IMAGES_FILES_COL = "AdviceDataImages.files";
    public static final String ADVICE_TYPE = "adviceType";
    public static final String UPDATE_TS = "updatedAt";
    private static final String PUBLISHED_COL = "GeneratedAdvices";
    private static final String GENERATED_COL = "GeneratedFiles";
    private static final String GENERATED_FILES_COL = "GeneratedFiles.files";
    private static final String GENERATED_TITLE = "generatedTitle";

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

    public BasicDBObject createNewAdvice(String currentId, String tipo) {
        System.out.println("new advice:" + currentId);
        BasicDBObject newdata = new BasicDBObject(INTERNAL_FORM_ID, currentId)
                .append(ADVICE_TYPE, tipo).append(UPDATE_TS, new Date());
        mongoDB.getCollection(CAPTURA_COL).insert(newdata);
        return newdata;
    }

    public BasicDBObject getAdvice(String currentId) {
        System.out.println("getAdvice: " + currentId);
        BasicDBObject newdata = new BasicDBObject(INTERNAL_FORM_ID, currentId);
        return (BasicDBObject) mongoDB.getCollection(CAPTURA_COL).findOne(newdata);
    }

    public void savePlainData(String currentId, String formId, HashMap<String, String> parametros) {
        System.out.println("saveAdvice: " + currentId + " screen:" + formId);
        BasicDBObject actual = getAdvice(currentId);
        BasicDBObject interno = new BasicDBObject(parametros);
        actual.append(formId, interno);
        actual.append(UPDATE_TS, new Date());
        mongoDB.getCollection(CAPTURA_COL).update(getAdvice(currentId), actual);
    }

    public GridFS getImagesFS() {
        return new GridFS(mongoDB, IMAGES_COL);
    }

    public GridFS getGeneratedFS() {
        return new GridFS(mongoDB, GENERATED_COL);
    }

    public GridFS getGridFS(String type) {
        String collection = GENERATED_COL;
        if ("getImage".equals(type)) {
            collection = IMAGES_COL;
        }
        return new GridFS(mongoDB, collection);
    }

    public ArrayList<String> getPublisedAdvicesList() {
        DBCollection col = mongoDB.getCollection(GENERATED_COL);
        ArrayList<String> ret = null;
        DBCursor cursor = col.find().sort(new BasicDBObject(UPDATE_TS, -1)).limit(20);
        if (null != cursor) {
            ret = new ArrayList<>();
            for (DBObject object : cursor) {
                String advice = object.get(INTERNAL_FORM_ID) + "|" + object.get(GENERATED_TITLE);
                ret.add(advice);
            }
        }
        return ret;
    }

    ArrayList<String> listFilesFromAdvice(String adviceID) {
        ArrayList<String> ret = new ArrayList<>();
        DBCollection col = mongoDB.getCollection(IMAGES_FILES_COL);
        DBCursor cursor = col.find(new BasicDBObject("filename", new BasicDBObject("$regex", adviceID)));
        if (null != cursor) {
            ret = new ArrayList<>();
            for (DBObject obj : cursor) {
                ret.add((String) obj.get("filename"));

            }
        }
        return ret;
    }

    public void setGenerated(String adviceID, String previous, String title) {
        DBCollection col = mongoDB.getCollection(GENERATED_COL);
        col.insert(new BasicDBObject(INTERNAL_FORM_ID, adviceID)
                .append(GENERATED_TITLE, title)
                .append("previousIssue", previous)
                .append("generationTime", Utils.sdf.format(new Date())));
    }

    public ArrayList<Statistics> getAdviceChain(String currentId) {
        String searchId = currentId;
        DBCollection col = mongoDB.getCollection(GENERATED_COL);

        Deque<String> deque = new ArrayDeque<>();
        while (searchId != null && !searchId.trim().equals("")) {
            deque.push(searchId);
            BasicDBObject current = (BasicDBObject) col.findOne(new BasicDBObject(INTERNAL_FORM_ID, searchId));
            if (null != current) {
                searchId = current.getString("previousIssue");
            } else {
                searchId = null;
            }
        }
        ArrayList<Statistics> ret = new ArrayList<>();
        DBCollection adCol = mongoDB.getCollection(CAPTURA_COL);
        while (!deque.isEmpty()) {
            String curr = deque.pop();
            System.out.println("buscar:" + curr);
            BasicDBObject lobj = (BasicDBObject) adCol.findOne(new BasicDBObject(INTERNAL_FORM_ID, curr));
            if (null != lobj) {
                ret.add(new Statistics(lobj));
            }
        }
        return ret;
    }

}
