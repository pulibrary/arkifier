/**
 * Arkifier.java
 */
package edu.princeton.diglib.arkifier;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.ws.http.HTTPException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * Handles Princeton METS specific NOID/ARK functions, as opposed to ARKService
 * which is a generic wrapper for a NOID/ARK service.
 * 
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Apr 7, 2011
 */
public class Arkifier {
    private boolean mint = false;
    private boolean bind = false;
    private boolean simulate = false;
    private ARKService arkService;
    private static String uri;
    private static String naan;
    private static String pudlObjectUri;
    private static Builder bob = null;

    private final static Logger logger = (Logger) LoggerFactory.getLogger(Arkifier.class.getName());

    /**
     * @param dir
     */
    public Arkifier() {
        Properties props = new Properties();
        try {
            InputStream propsStream;
            ClassLoader cl;
            cl = Arkifier.class.getClassLoader();
            propsStream = cl.getResourceAsStream("arkifier.properties");
            props.load(propsStream);
            uri = props.getProperty("Arkifier.uri");
            naan = props.getProperty("Arkifier.naan");
            pudlObjectUri = props.getProperty("Arkifier.pudlObjectUri");
            this.arkService = new ARKService(uri, naan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("Initialized Arkifier");
    }

    /**
     * Should we mint? False by default.
     * 
     * @param mint
     */
    public void setMint(boolean mint) {
        this.mint = mint;
    }

    /**
     * Should we bind? False by default.
     * 
     * @param bind
     */
    public void setBind(boolean bind) {
        this.bind = bind;
    }

    /**
     * Should we simulate? False by default.
     * 
     * @param simulate
     */
    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    /**
     * Run the Arkifier we've built on a file or recursively through a
     * directory.
     * 
     * @throws MissingObjidException
     * @throws XMLStreamException
     * 
     * @return A Map with file paths as keys and the action (ACTIONS) taken as
     *         values.
     */
    public HashMap<String, ArrayList<ACTIONS>> run(String node) {
        // Compile a list of METS files, recursively
        Stack<File> metsFiles = new Stack<File>();
        compileList(new File(node), new MetsFileFilter(), metsFiles);

        HashMap<String, ArrayList<ACTIONS>> report;
        report = new HashMap<String, ArrayList<ACTIONS>>(metsFiles.size());

        // Now filter the list
        ObjidExtractor oe = new ObjidExtractor();
        while (!metsFiles.empty()) {
            File current = metsFiles.pop();

            String id = null;
            try {
                id = oe.getObjid(current);
            } catch (MissingObjidException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            } catch (XMLStreamException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
            String currentPath = current.getPath();
            String currentRelative = current.getPath().substring(node.length() + 1);

            ArrayList<ACTIONS> actions = new ArrayList<ACTIONS>(2);

            boolean idIsNOID = stringIsNOID(id);

            String noid = null;
            if (mint && !idIsNOID) {
                if (bob == null) {
                    bob = new Builder();
                }
                /*
                 * Parse the record here, before we even interact with the
                 * minter, so that if there is a problem with the file we exit
                 * and avoid minting a NOID for no reason.
                 * 
                 * (We only need to parse the document if we're going to mint a
                 * new NOID and write it to the DOM.)
                 */
                Document doc = null;
                try {
                    doc = bob.build(current);
                } catch (ValidityException e) {
                    logger.error(e.getMessage());
                    System.exit(-1);
                } catch (ParsingException e) {
                    logger.error(e.getMessage());
                    System.exit(-1);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    System.exit(-1);
                }

                if (simulate) {
                    actions.add(ACTIONS.MINT);
                    logger.debug("[SIMULATION] : Mint new NOID for " + currentRelative);
                    noid = "{newNOID}";
                } else {
                    // update report
                    actions.add(ACTIONS.MINT);

                    // mint
                    try {
                        noid = arkService.mint();
                        logger.debug("Minted new NOID for " + currentRelative);
                    } catch (HTTPException ex) {
                        String msg;
                        msg = "Unable to either bind ARK. HTTP status: " + ex.getStatusCode();
                        logger.error(msg);
                        System.exit(-1);
                    }

                    // update doc
                    Attribute objidAttr = doc.getRootElement().getAttribute("OBJID");
                    objidAttr.setValue(noid);

                    // write back out
                    FileOutputStream out;
                    Serializer ser;
                    try {
                        out = new FileOutputStream(current);
                        ser = new Serializer(out, "UTF-8");
                        ser.setIndent(3);
                        ser.setMaxLength(120);
                        ser.write(doc);
                        ser.flush();
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        logger.error(e.getMessage());
                        logger.error("NOID: " + noid + " was minted for " + currentRelative
                                + " but the document was not updated");
                        System.exit(-1);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage());
                        logger.error("NOID: " + noid + " was minted for " + currentRelative
                                + " but the document was not updated");
                        System.exit(-1);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        logger.error("NOID: " + noid + " was minted for " + currentRelative
                                + " but the document was not updated");
                        System.exit(-1);
                    }
                }
            } else if (idIsNOID) {
                noid = id;
            } else {
                noid = null;
            }

            if (bind && noid != null) {
                String targetURI = pudlObjectUri + noid;
                if (simulate) {
                    actions.add(ACTIONS.BIND);
                    logger.debug("[SIMULATION] : Bind " + noid + " for " + currentRelative + " to " + targetURI);
                } else {
                    try {
                        actions.add(ACTIONS.BIND);
                        arkService.bind(noid, targetURI);
                        logger.debug("Bound ARK for " + currentRelative);
                    } catch (HTTPException ex) {
                        String msg;
                        msg = "Unable to bind ARK. HTTP status: " + ex.getStatusCode();
                        logger.error(msg);
                        System.exit(-1);
                    }
                }
            }

            if (actions.isEmpty()) {
                actions.add(ACTIONS.NO_ACTION);
            }

            actions.trimToSize();
            
            String msg;
            if (actions.contains(ACTIONS.MINT) && actions.contains(ACTIONS.BIND)) {
                msg = "Minted NOID and bound ARK";
            } else if (actions.contains(ACTIONS.MINT) && !actions.contains(ACTIONS.BIND)) {
                msg = "Minted NOID";
            } else if (!actions.contains(ACTIONS.MINT) && actions.contains(ACTIONS.BIND)) {
                msg = "Bound ARK";
            } else {
                msg = null;
            }
            
            if (msg != null) {
                if (simulate) {
                    msg = "[SIMULATION] : " + msg;
                }
                logger.info(msg + " for " + currentRelative);
            }
            report.put(currentPath, actions);

        }
        return report;
    }

    /*
     * true if we think this is NOID. Not ideal, but it works...
     */
    private static boolean stringIsNOID(String candidate) {
        boolean noStops = !(candidate.contains("/") || candidate.contains("-"));
        boolean rightLength = candidate.length() == 9;
        return noStops && rightLength;
    };

    /*
     * Build our list of METS Files
     */
    private static void compileList(File node, FileFilter metsFilter, Stack<File> metsFiles) {
        if (node.isFile() && metsFilter.accept(node)) {
            metsFiles.push(node);
        } else if (node.isDirectory()) {
            for (File subNode : node.listFiles(metsFilter)) {
                compileList(subNode, metsFilter, metsFiles);
            }
        }
    }

    /**
     * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
     * @since Apr 2, 2011
     */
    protected class MetsFileFilter implements FileFilter {
        private static final String METS_EXTENSION = ".mets";

        @Override
        public boolean accept(File file) {
            boolean hasMetsExtension = file.isFile() && file.getName().endsWith(METS_EXTENSION);
            boolean notWorkDir = file.isDirectory() && !file.getName().equals("work");
            boolean isVisible = !file.isHidden();
            return (hasMetsExtension || notWorkDir) && isVisible;
        }

    }

    /**
     * @author <a href="jstroop@princeton.edu">Jon Stroop</a>
     * @since May 27, 2011
     * 
     */
    public enum ACTIONS {
        MINT, BIND, NO_ACTION;
    }
}
