/*
 * Simple test code that separates the web service calls from the rest of the
 * code for testing. 
 */

package com.pfizer.mrbt.genomics.TransmartClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author henstockpv
 */
public class SimpleWebServiceTest {
    
    public SimpleWebServiceTest() {
        
    }
    
    public String fetchResult(String path) throws UniformInterfaceException, ClientHandlerException {
        Client client = Client.create();
        //client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        client.setFollowRedirects(true);
        WebResource webRes = client.resource(path);
        Builder clientReqBuilder = webRes.accept(MediaType.APPLICATION_XML);
        
        String xmlResult = clientReqBuilder.get(String.class);
        return (String) xmlResult;
    }
    
    public static void main(String[] argv) {
        SimpleWebServiceTest swst = new SimpleWebServiceTest();
        // amazon old stage
        String path0 = "http://amre1al306.pcld.pfizer.com:8080/transmartPfizer/webservice/getSecureModelInfoByDataType?dataType=1&user=henstockpv";

        // attempted new production
        String path2 = "http://transmart.pfizer.com/transmart/webservice/getSecureModelInfoByDataType?dataType=1&user=henstockpv";
        String path = "http://transmart.pfizer.com/transmart/webservice/resultDataForFilteredByModelIdGeneAndRangeRev?modelId=1604717513&range=100000&geneName=TNF";
        String path4 = "http://transmart.pfizer.com/transmart/webservice/getGeneByPosition?stop=31646498&start=31441503&chromosome=6";
        String path3 = "http://transmart.pfizer.com/transmart/webservice//getRecombinationRatesForGene?&range=100000&geneName=TNF";
        String path5 = "http://170.116.232.209/transmart/webservice/getSecureModelInfoByDataType?dataType=1&user=henstockpv";

        // new prod
        String path6 = "http://amrndhl1038.pfizer.com:10080/transmart/webservice/getSecureModelInfoByDataType?dataType=1&user=henstockpv";
        System.out.println("query: [" + path6 + "]");
        String xmlp = swst.fetchResult(path6);
        System.out.println("Result:\n" + xmlp);

        // new stage
        String path7 = "http://amrndhl1040.pfizer.com:8000/transmart/webservice/getSecureModelInfoByDataType?dataType=1&user=henstockpv";
        System.out.println("query: [" + path7 + "]");
        String xmls = swst.fetchResult(path7);
        System.out.println("Result:\n" + xmls);
    }
}
