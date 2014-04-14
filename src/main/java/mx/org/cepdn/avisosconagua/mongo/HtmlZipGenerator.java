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

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author serch
 */
public class HtmlZipGenerator {
    private final HtmlGenerator html;
    private final String link;
    private final String name;
    private final String nameZip;
    private final String adviceID;
    private boolean isOK = false;
    
    
    public HtmlZipGenerator(String adviceID){
        html = new HtmlGenerator(adviceID);
        this.adviceID = adviceID;
        name = adviceID + ".html";
        nameZip = adviceID + ".zip";
        link = "/getFile/" + nameZip;
    }
    
    public String getPrevious(){
        return html.getPrevious();
    }
    
    public String getTitle(){
        return html.getTitle();
    }
    
    public void generate() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String localFolder = "./"+adviceID+"/";
            ZipOutputStream zout = new ZipOutputStream(baos);
            zout.setLevel(9);
            zout.putNextEntry(new ZipEntry(name));
            zout.write(html.generate(true).getBytes("ISO8859-1"));
            zout.putNextEntry(new ZipEntry(localFolder));
            if (html.getPrincipalFile()!=null){
                GridFS gridfs = MongoInterface.getInstance().getImagesFS();
                GridFSDBFile imageForOutput = gridfs.findOne(html.getPrincipalFile());
                zout.putNextEntry(new ZipEntry(localFolder + html.getPrincipalFile()));
                imageForOutput.writeTo(zout);
            }
            if (html.getPronosticoFile()!=null){
                GridFS gridfs = MongoInterface.getInstance().getImagesFS();
                GridFSDBFile imageForOutput = gridfs.findOne(html.getPronosticoFile());
                zout.putNextEntry(new ZipEntry(localFolder + html.getPronosticoFile()));
                imageForOutput.writeTo(zout);
            }
//            ArrayList<String> lista = MongoInterface.getInstance().listFilesFromAdvice(adviceID);
//            for (String filename : lista) {
//                GridFS gridfs = MongoInterface.getInstance().getImagesFS();
//                GridFSDBFile imageForOutput = gridfs.findOne(filename);
//                String fnpart[] = filename.split(":");
//                zout.putNextEntry(new ZipEntry(localFolder + fnpart[1]));
//                imageForOutput.writeTo(zout);
//            }
            zout.close();
            GridFS fs = MongoInterface.getInstance().getGeneratedFS();
            fs.remove(nameZip);
        GridFSInputFile infile =  fs.createFile(baos.toByteArray());
        infile.setContentType("application/zip");
        infile.setFilename(nameZip);
        infile.save();
        isOK = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public boolean isOK() {
        return isOK;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return nameZip;
    }
    
}
