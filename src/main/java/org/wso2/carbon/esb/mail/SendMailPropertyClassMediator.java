package org.wso2.carbon.esb.sample;

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

public class SampleClassMediator extends AbstractMediator implements
        ManagedLifecycle {

    private static final Log log = LogFactory.getLog(SampleClassMediator.class);


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
            return Integer.parseInt(val.toString()) - 1;
        else
            return 1;
    }

    //This value is checked in the mediation flow to decide whether files are finished processing
    private void setMailProperty(int count, MessageContext context, String property) {

        if (count == 0) {
            context.setProperty(property, "true");
        } else {
            context.setProperty(property, "false");
        }
    }

    //Update the property value which contains the file count of each mediation
    private void updateFileCount(String file_path, MessageContext mc, String account_flow) {

        double lineCount = 0;
        int file_count = 0;
        File input_file = new File(file_path);
        //JSONObject obj = new JSONObject();
        if (input_file.exists()) {
            Path path = Paths.get(file_path);

            try {
                lineCount = Files.lines(path).count();
                file_count = (int) Math.ceil(lineCount / 10000);
            } catch (IOException e) {
                log.error("Error in json conversion", e);
            }

            switch (account_flow) {
                case "Account":
                    mc.setProperty("accountFileCount", file_count);
                    break;
                case "ExchangeRate":
                    mc.setProperty("exchangeRateFileCount", file_count);
                    break;
                case "Loan":
                    mc.setProperty("loanFileCount", file_count);
                    break;
                case "Customer":
                    mc.setProperty("customerFileCount", file_count);
                    break;
            }

        }

    }

    // check the value of the file count and to set the mail sending property
    private void checkFileCount(MessageContext mc, String account_flow) {

        int accountFileCount = deductCountValue("accountFileCount", mc);
        int exchangeRateFileCount = deductCountValue("exchangeRateFileCount", mc);
        int loanFileCount = deductCountValue("loanFileCount", mc);
        int customerFileCount = deductCountValue("customerFileCount", mc);

        switch (account_flow) {
            case "Account":
                mc.setProperty("accountFileCount", Integer.toString(accountFileCount));
                setMailProperty(accountFileCount, mc, "Account_send_mail");
                break;
            case "ExchangeRate":
                mc.setProperty("exchangeRateFileCount", Integer.toString(exchangeRateFileCount));
                setMailProperty(accountFileCount, mc, "ExchangeRate_send_mail");
                break;
            case "Loan":
                mc.setProperty("loanFileCount", Integer.toString(loanFileCount));
                setMailProperty(accountFileCount, mc, "Loan_send_mail");
                break;
            case "Customer":
                mc.setProperty("customerFileCount", Integer.toString(customerFileCount));
                setMailProperty(accountFileCount, mc, "Customer_send_mail");
                break;
        }

    }

}

