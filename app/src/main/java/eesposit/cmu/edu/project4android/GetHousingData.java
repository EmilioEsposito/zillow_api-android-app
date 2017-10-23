package eesposit.cmu.edu.project4android;

/**
 * Created by Emilio on 10/31/2016.
 */

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Emilio
 */
public class GetHousingData {
    HousingValue hv = null;

    /**
     *
     * @param address string of the house number and street name
     * @param citystatezip string of the city, state, and zip
     * @param hv reference to the UI class
     */
    public void query(String address, String citystatezip, HousingValue hv) {
        this.hv = hv;

        String[] params = {address, citystatezip};

        new AsyncZillowQuery().execute(params);
    }

    private class AsyncZillowQuery extends AsyncTask<String[], Void, HashMap<String,String>> {
        @Override
        protected HashMap doInBackground(String[]... strings) {

            //recover the params string array parameters
            String[] params = strings[0];

            //run the query
            //params[0]=address and params[1]=citystatezip
            return query(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(HashMap<String,String> dataMap) {
            //debug
            System.out.println("debug line:"+dataMap);
            hv.dataReady(dataMap);
        }
        //Task 1 server
//        private String baseURL = "http://haunted-blood-37092.herokuapp.com/HouseValueServlet?";
        //Task 2 server
        private String baseURL = "http://fast-springs-33257.herokuapp.com/HouseValueServlet?";
        private String houseValueStr;
        private String neighborhoodValueStr;
        private String percDiffStr;

        /**
         * wrapper method for both fetchRemoteXML and parseXMLResponse
         * @param address string of the house number and street name
         * @param citystatezip string of the city, state, and zip
         * @return a hashmap of the data elements we will use to modify the UI
         */
        private HashMap query(String address, String citystatezip) {

            //fetch the XML response string from the webservice
            String remoteXMLStr = fetchRemoteXML(address, citystatezip);

            //convert the str into an XML document
            Document xmlDoc = parseXMLResponse(remoteXMLStr);

            //Retrieve the desired data from the xml doc
            houseValueStr = xmlDoc.getElementsByTagName("houseValue").item(0).getTextContent();
            neighborhoodValueStr = xmlDoc.getElementsByTagName("neighborhoodValue").item(0).getTextContent();
            percDiffStr = xmlDoc.getElementsByTagName("percDiff").item(0).getTextContent();

            //Store the data in a HashMap
            HashMap<String,String> dataMap = new HashMap<String,String>();

            //debug
            System.out.println("debug line:"+houseValueStr);

            dataMap.put("houseValue", houseValueStr);
            dataMap.put("neighborhoodValue", neighborhoodValueStr);
            dataMap.put("percDiff", percDiffStr);

            return dataMap;
        }

        /**
         *
         * @param address string of the house number and street name
         * @param citystatezip string of the city, state, and zip
         * @return the XML string response from my heroku web server
         */
        private String fetchRemoteXML(String address, String citystatezip) {

            String respStrXML = "";
            try {
                //build URL with proper encoding
                URL url = new URL(baseURL +
                        "address=" +
                        URLEncoder.encode(address, "UTF-8") +
                        "&citystatezip=" +
                        URLEncoder.encode(citystatezip, "UTF-8"));
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "XMLresponse"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    respStrXML += str;
                }
                in.close();
            } catch (IOException e) {
                System.out.println("Something wrong happened with the connection...");
            }

            //return the XML string
            return respStrXML;
        }

        /**
         *
         * @param respStrXML the XML string response from my heroku web server
         * @return the same XML document, but in Document format instead of string
         */
        private Document parseXMLResponse(String respStrXML) {

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();

                //store parsed doc in zillowResults
                Document serverXMLresp = builder.parse(new InputSource(new StringReader(respStrXML)));

                return serverXMLresp;
            } catch (ParserConfigurationException ex) {
                System.out.println(ex.getMessage());
            } catch (SAXException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return null;
        }


    }
}
