package org.wso2.carbon.esb.sample;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;



public class SampleClassMediator extends AbstractMediator implements
        ManagedLifecycle {

    private static final Log log = LogFactory.getLog(SampleClassMediator.class);
    public static String First_Element = "magasin";
    public static String Second_Element = "infosMagasin";
    public static String LibelleTrancheSurface = "libelleTrancheSurface";
    public static String LibelleTrancheSurfaceEnseigne = "libelleTrancheSurfaceEnseigne";
    public static String LibelleVocationMagasin = "libelleVocationMagasin";

    public boolean mediate(MessageContext mc) {

        Path path = Paths.get("./big_file.txt");
        try {
            long lineCount = Files.lines(path).count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public void init(SynapseEnvironment synapseEnvironment) {

    }

    public void destroy() {

    }
}
