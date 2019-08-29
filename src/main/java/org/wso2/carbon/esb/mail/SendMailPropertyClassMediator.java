package org.wso2.carbon.esb.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendMailPropertyClassMediator extends AbstractMediator implements
        ManagedLifecycle {

    private static final Log log = LogFactory.getLog(SendMailPropertyClassMediator.class);

    public boolean mediate(MessageContext mc) {

        Boolean lineCount = false;
        if (mc.getProperty("lineCount").equals("true")) {
            lineCount = true;
        }
        String accountFlow = getPropertyString("accountFlow", mc);
        String filePath = getPropertyString("filePath", mc);

        if (lineCount == true) {
            updateFileCount(filePath, mc, accountFlow);
        } else {
            checkFileCount(mc, accountFlow);
        }

        return true;
    }

    public void init(SynapseEnvironment synapseEnvironment) {

    }

    public void destroy() {

    }

    private String getPropertyString(String property, MessageContext context) {

        Object val = context.getProperty(property);
        if (val != null)
            return val.toString();
        else
            return null;
    }

    private int deductCountValue(String property, MessageContext context) {

        Object val = context.getProperty(property);
        if (val != null)
            if (Integer.parseInt(val.toString()) > 1)
                return Integer.parseInt(val.toString()) - 1;
            else
                return 0;
        else {
            log.debug(property + " Value is null thus setting default value of 1000");
            return 1000;

        }

    }

    //This value is checked in the mediation flow to decide whether files are finished processing
    private void setMailProperty(int count, MessageContext context, String property) {

        if (count == 0) {
            context.setProperty(property, "true");
            log.debug(property + " is set to true");
        } else {
            context.setProperty(property, "false");
            log.debug(property + " is set to false");
        }
    }

    //Update the property value which contains the file count of each mediation
    private void updateFileCount(String filePath, MessageContext mc, String accountFlow) {

        double lineCount = 0;
        int fileCount = 0;
        File inputFile = new File(filePath);
        //JSONObject obj = new JSONObject();
        if (inputFile.exists()) {
            Path path = Paths.get(filePath);

            try {
                lineCount = Files.lines(path).count();
                fileCount = (int) Math.ceil(lineCount / 10000);
                log.debug(accountFlow + "file is split in to " + filePath + " parts");
            } catch (IOException e) {
                log.error("Error in json conversion", e);
            }

            switch (accountFlow) {
                case "Account":
                    mc.setProperty("accountFileCount", Integer.parseInt(String.valueOf(fileCount)));
                    mc.setProperty("accountSendMail", "false");
                    break;
                case "ExchangeRate":
                    mc.setProperty("exchangeRateFileCount", Integer.parseInt(String.valueOf(fileCount)));
                    mc.setProperty("exchangeRateSendMail", "false");
                    break;
                case "Loan":
                    mc.setProperty("loanFileCount", Integer.parseInt(String.valueOf(fileCount)));
                    mc.setProperty("loanSendMail", "false");
                    break;
                case "Customer":
                    mc.setProperty("customerFileCount", Integer.parseInt(String.valueOf(fileCount)));
                    mc.setProperty("customerSendMail", "false");
                    break;
            }

        } else {
            switch (accountFlow) {
                case "Account":
                    mc.setProperty("accountFileCount", "0");
                    break;
                case "ExchangeRate":
                    mc.setProperty("exchangeRateFileCount", "0");
                    break;
                case "Loan":
                    mc.setProperty("loanFileCount", "0");

                    break;
                case "Customer":
                    mc.setProperty("customerFileCount", "0");
                    break;
            }
        }

    }

    // check the value of the file count and to set the mail sending property
    private void checkFileCount(MessageContext mc, String accountFlow) {

        int accountFileCount = deductCountValue("accountFileCount", mc);
        int exchangeRateFileCount = deductCountValue("exchangeRateFileCount", mc);
        int loanFileCount = deductCountValue("loanFileCount", mc);
        int customerFileCount = deductCountValue("customerFileCount", mc);
        log.debug("check file count of " + accountFlow + " flow");

        switch (accountFlow) {
            case "Account":
                mc.setProperty("accountFileCount", Integer.toString(accountFileCount));
                setMailProperty(accountFileCount, mc, "accountSendMail");
                break;
            case "ExchangeRate":
                mc.setProperty("exchangeRateFileCount", Integer.toString(exchangeRateFileCount));
                setMailProperty(exchangeRateFileCount, mc, "exchangeRateSendMail");
                break;
            case "Loan":
                mc.setProperty("loanFileCount", Integer.toString(loanFileCount));
                setMailProperty(loanFileCount, mc, "loanSendMail");
                break;
            case "Customer":
                mc.setProperty("customerFileCount", Integer.toString(customerFileCount));
                setMailProperty(customerFileCount, mc, "customerSendMail");
                break;
        }

    }

}