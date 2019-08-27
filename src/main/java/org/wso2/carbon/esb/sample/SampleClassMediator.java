package org.wso2.carbon.esb.sample;

import org.json.simple.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SampleClassMediator extends AbstractMediator implements
        ManagedLifecycle {

    private static final Log log = LogFactory.getLog(SampleClassMediator.class);
    public static String File_Count_Path = "/user/shanaka/";

    public boolean mediate(MessageContext mc) {

        Boolean line_count = (Boolean) mc.getProperty("line_count");
        String account_flow = mc.getProperty("account_flow").toString();
        long lineCount = 0;
        int file_count = 0;
        int Account_file_count = 0;
        int Exchange_rate_count = 0;
        int Loan_file_count = 0;
        int Customer_file_count = 0;
        String file_path = (String) mc.getProperty("file_path");

        File input_file = new File(file_path);
        if (line_count == true && input_file.exists()) {
            Path path = Paths.get(file_path);
            try {
                lineCount = Files.lines(path).count();
                file_count = (int) Math.ceil(lineCount / 10000);
            } catch (IOException e) {
                log.error("Error in json conversion", e);
            }

            File count_file = new File(File_Count_Path);

//            String[] file_url = file_path.split("/");
//            String file_name = file_url[file_url.length - 1];

            JSONObject obj = new JSONObject();
            if (count_file.exists()) {

                Reader reader = null;
                try {
                    reader = new FileReader(File_Count_Path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                JSONParser parser = new JSONParser();
                try {
                    obj = (JSONObject) parser.parse(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                switch (account_flow) {
                    case "Account":
                        obj.put("Accounts", Account_file_count);
                    case "Exchange":
                        obj.put("ExchangeRates", Exchange_rate_count);
                    case "Loan":
                        obj.put("Loan", Loan_file_count);
                    case "Customer":
                        obj.put("Customer", Customer_file_count);
                }
            } else {
                switch (account_flow) {
                    case "Account":
                        Account_file_count = file_count;
                    case "Exchange":
                        Exchange_rate_count = file_count;
                    case "Loan":
                        Loan_file_count = file_count;
                    case "Customer":
                        Customer_file_count = file_count;
                        obj.put("Accounts", Account_file_count);
                        obj.put("ExchangeRates", Exchange_rate_count);
                        obj.put("Loan", Loan_file_count);
                        obj.put("Customer", Customer_file_count);
                }
            }
            try {
                FileWriter fw = new FileWriter(File_Count_Path);
                fw.write(obj.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (line_count = false) {
            JSONObject obj = new JSONObject();
            Reader reader = null;
            try {
                reader = new FileReader(File_Count_Path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JSONParser parser = new JSONParser();
            try {
                obj = (JSONObject) parser.parse(reader);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (account_flow) {
                case "Account":
                    obj.put("Accounts", (int) obj.get("Accounts") - 1);
                case "Exchange":
                    obj.put("ExchangeRates", (int) obj.get("ExchangeRates") - 1);
                case "Loan":
                    obj.put("Loan", (int) obj.get("Loan") - 1);
                case "Customer":
                    obj.put("Customer", (int) obj.get("Customer") - 1);
            }
            try {
                FileWriter fw = new FileWriter(File_Count_Path);
                fw.write(obj.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject obj = new JSONObject();
        Reader reader = null;
        try {
            reader = new FileReader(File_Count_Path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        try {
            obj = (JSONObject) parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((int) obj.get("Accounts") == 0) {
            mc.setProperty("Account_send_mail", true);
        }
        if ((int) obj.get("ExchangeRates") == 0) {

            mc.setProperty("ExchangeRates_send_mail", true);
        }
        if ((int) obj.get("Loan") == 0) {
            mc.setProperty("Loan_send_mail", true);
        }
        if ((int) obj.get("Customer") == 0) {
            mc.setProperty("Customer_send_mail", true);
        }

        return true;
    }

    public void init(SynapseEnvironment synapseEnvironment) {

    }

    public void destroy() {

    }
}

