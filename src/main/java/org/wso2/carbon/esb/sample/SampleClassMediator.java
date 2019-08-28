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
    public static String File_Count_Path = System.getProperty("user.dir") + "/file_count.json";

    public boolean mediate(MessageContext mc) {

        Boolean line_count = false;
        if (mc.getProperty("line_count").equals("true")) {
            line_count = true;
        }
        String account_flow = mc.getProperty("account_flow").toString();
        double lineCount = 0;
        int file_count = 0;
        int Account_file_count = 0;
        int Exchange_rate_count = 0;
        int Loan_file_count = 0;
        int Customer_file_count = 0;
        String file_path = (String) mc.getProperty("file_path");

        File input_file = new File(file_path);
        if (line_count == true) {
            JSONObject obj = new JSONObject();
            if (input_file.exists()) {
                Path path = Paths.get(file_path);
                switch (account_flow) {
                    case "Account":
                        mc.setProperty("Account_send_mail", false);
                        break;
                    case "ExchangeRate":
                        mc.setProperty("ExchangeRate_send_mail", false);
                        break;
                    case "Loan":
                        mc.setProperty("Loan_send_mail", false);
                        break;
                    case "Customer":
                        mc.setProperty("Customer_send_mail", false);
                        break;
                }

                try {
                    lineCount = Files.lines(path).count();
                    file_count = (int) Math.ceil(lineCount / 10000);
                } catch (IOException e) {
                    log.error("Error in json conversion", e);
                }

                File count_file = new File(File_Count_Path);

//            String[] file_url = file_path.split("/");
//            String file_name = file_url[file_url.length - 1];

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
                            obj.put("Account", file_count);
                            break;
                        case "ExchangeRate":
                            obj.put("ExchangeRate", file_count);
                            break;
                        case "Loan":
                            obj.put("Loan", file_count);
                            break;
                        case "Customer":
                            obj.put("Customer", file_count);
                            break;
                    }
                } else {
                    switch (account_flow) {
                        case "Account":
                            Account_file_count = file_count;
                            break;
                        case "ExchangeRate":
                            Exchange_rate_count = file_count;
                            break;
                        case "Loan":
                            Loan_file_count = file_count;
                            break;
                        case "Customer":
                            Customer_file_count = file_count;
                            break;
                    }
                    obj.put("Account", Account_file_count);
                    obj.put("ExchangeRate", Exchange_rate_count);
                    obj.put("Loan", Loan_file_count);
                    obj.put("Customer", Customer_file_count);
                }
                try {
                    FileWriter fw = new FileWriter(File_Count_Path);
                    fw.write(obj.toString());
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } else {
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
                    obj.put("Account", (int) obj.get("Account") - 1);
                    break;
                case "ExchangeRate":
                    obj.put("ExchangeRate", (int) obj.get("ExchangeRate") - 1);
                    break;
                case "Loan":
                    obj.put("Loan", (int) obj.get("Loan") - 1);
                    break;
                case "Customer":
                    obj.put("Customer", (int) obj.get("Customer") - 1);
                    break;
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

        if (Integer.parseInt((String) obj.get("Account")) == 0) {
            mc.setProperty("Account_send_mail", true);
        }

        if (Integer.parseInt((String) obj.get("ExchangeRate")) == 0) {
            mc.setProperty("ExchangeRate_send_mail", true);
        }

        if (Integer.parseInt((String) obj.get("Loan")) == 0) {
            mc.setProperty("Loan_send_mail", true);
        }

        if (Integer.parseInt((String) obj.get("Customer")) == 0) {
            mc.setProperty("Customer_send_mail", true);
        }

        return true;
    }

    public void init(SynapseEnvironment synapseEnvironment) {

    }

    public void destroy() {

    }
}

