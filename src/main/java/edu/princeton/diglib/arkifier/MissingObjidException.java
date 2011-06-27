package edu.princeton.diglib.arkifier;

/**
 * Thrown when a METS is missing its @OBJID
 * 
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Mar 13, 2011
 */
public class MissingObjidException extends Exception {

    private static final long serialVersionUID = -7949483498231024065L;

    public MissingObjidException(String filePath) {
        super(filePath + " is missing an OBJID");
    }
}
