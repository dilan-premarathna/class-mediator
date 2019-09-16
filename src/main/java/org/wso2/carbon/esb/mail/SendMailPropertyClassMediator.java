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
    private static volatile int accountFileCount;
    private static volatile int exchangeRateFileCount;
    private static volatile int loanFileCount;
    private static volatile int customerFileCount;

    public boolean mediate(MessageContext mc) {

        Boolean lineCount = false;
        if (mc.getProperty("lineCount") != null) {
            if (mc.getProperty("lineCount").equals("true")) {
                lineCount = true;
            }
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

    //This value is checked in the mediation flow to decide whether files are finished processing
    private void setMailProperty(int count, MessageContext context, String property) {

        log.debug("File count value is " + count);
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

        if (inputFile.exists()) {
            Path path = Paths.get(filePath);

            try {
                lineCount = Files.lines(path).count();
                fileCount = (int) Math.ceil(lineCount / 10000);
                log.debug(accountFlow + "file in location " + filePath + " is split in to " + fileCount + " parts");
            } catch (IOException e) {
                log.error("Error in file read", e);
            }

            switch (accountFlow) {
                case "Account":
                    accountFileCount = fileCount;
                    mc.setProperty("accountSendMail", "false");
                    break;
                case "ExchangeRate":
                    exchangeRateFileCount = fileCount;
                    mc.setProperty("exchangeRateSendMail", "false");
                    break;
                case "Loan":
                    loanFileCount = fileCount;
                    mc.setProperty("loanSendMail", "false");
                    break;
                case "Customer":
                    customerFileCount = fileCount;
                    mc.setProperty("customerSendMail", "false");
                    break;
            }

        } else {
            log.debug(accountFlow + " flow does not have a input file");
        }

    }

    // check the value of the file count and to set the mail sending property
    private void checkFileCount(MessageContext mc, String accountFlow) {

        log.debug("check file count of " + accountFlow + " flow");

        switch (accountFlow) {
            case "Account":
                if (accountFileCount > 1) {
                    accountFileCount -= 1;
                } else {
                    accountFileCount = 0;
                }
                setMailProperty(accountFileCount, mc, "accountSendMail");
                break;
            case "ExchangeRate":
                if (exchangeRateFileCount > 1) {
                    exchangeRateFileCount -= 1;
                } else {
                    exchangeRateFileCount = 0;
                }
                setMailProperty(exchangeRateFileCount, mc, "exchangeRateSendMail");
                break;
            case "Loan":
                if (loanFileCount > 1) {
                    loanFileCount -= 1;
                } else {
                    loanFileCount = 0;
                }
                setMailProperty(loanFileCount, mc, "loanSendMail");
                break;
            case "Customer":
                if (customerFileCount > 1) {
                    customerFileCount -= 1;
                } else {
                    customerFileCount = 0;
                }
                setMailProperty(customerFileCount, mc, "customerSendMail");
                break;
        }

    }

}