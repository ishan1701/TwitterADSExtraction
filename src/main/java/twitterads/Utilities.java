package twitterads;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import twitterads.udf.ExtractCampignsStats;

import static twitterads.Constants.*;
import static twitterads.udf.ParseCampaignsJSON.*;
import static twitterads.Funding.fetchFundData;
//https://ads-api.twitter.com/accounts
//working https://ads-api.twitter.com/7/accounts
public class Utilities extends Thread{

    public static List<Account> fetchTwitterAccount(CommonsHttpOAuthConsumer consumer, HttpClient client,String twitterAccountURI,String consumerkey){
        List<Account> accountList=new ArrayList<Account>();
        //System.out.println(twitterAccountURI);
        HttpGet request=new HttpGet(twitterAccountURI);
        try {
            consumer.sign(request);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse response= client.execute(request);
            HttpEntity entity =response.getEntity();

            if(entity!=null){
                Reader jr = new InputStreamReader(entity.getContent());

                /*Fetch of the data as json and traversal through
                        and create Account object.
                Adding to List and return*/

                JsonObject responseJson=  new Gson().fromJson(jr,JsonObject.class);
                JsonElement accounts=responseJson.get("data");
                if(accounts.isJsonArray()){
                    JsonArray accountsList=accounts.getAsJsonArray();
                    for(JsonElement element:accountsList){
                        JsonObject accountObject=element.getAsJsonObject();
                        String id=accountObject.getAsJsonPrimitive("id").getAsString();
                        String accountName=accountObject.getAsJsonPrimitive("name").getAsString();

                        accountList.add(new Account(id,accountName,consumerkey));

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountList;
    }
    public static List<Campaigns> fetchAccountCampaigns(CommonsHttpOAuthConsumer consumer,HttpClient client,URIBuilder campaignsURI,Account acc) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException, URISyntaxException {

        List<Campaigns> campaignsList=new ArrayList<>();
        String next_token="NO_VALUE";
        while(next_token!=null) {
            //System.out.println(client + "-->" + campaignsURI);
            client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
            HttpGet request = null;
            HttpResponse response = null;
            if(next_token.equals("NO_VALUE"))
                request=new HttpGet(campaignsURI.toString());
            else
                request=new HttpGet(campaignsURI.setParameter("cursor", next_token).toString());
            consumer.sign(request);

            //System.out.println("the response is" + response);
            response = client.execute(request);
           // System.out.println("ran sucessfully");
            System.out.println(response.getStatusLine().getStatusCode());
            //System.out.println("________________________________________________");

            //System.out.println("the respose code is " + response.getStatusLine().getStatusCode());
            while (response.getStatusLine().getStatusCode() == 429) {
                System.out.println("GOT 429 Error");
                System.out.println("the value is" + response.getStatusLine().getStatusCode());
                Thread.sleep(SLEEP_TIME);
                response = client.execute(request);
                //entity = response.getEntity();
            }
            HttpEntity entity = response.getEntity();
            Reader jr = new InputStreamReader(entity.getContent());

                /*Fetch of the data as json and traversal through
                        and create Campaign object.
                Adding to List and return*/

            JsonObject responseJson = new Gson().fromJson(jr, JsonObject.class);
            JsonElement data = responseJson.get("data");
           // System.out.println(data);

            //Parse the campaign and get the details campaign
            //like start_date, end_date, funding_instruments etc

            List<Campaigns>  campaignsListRecieved=parseCampaign(data,acc);
            campaignsList.addAll(campaignsListRecieved);

            JsonElement incommingToken=responseJson.get("next_cursor");
           // System.out.println("---------------------------->"+incommingToken);
            //System.out.println(incommingToken.getClass().getSimpleName()+"--->"+incommingToken.getClass().getCanonicalName()+"-->"+incommingToken.getClass().getName());
            if(incommingToken.getClass().getSimpleName().equals("JsonNull"))
                next_token=null;
            else
                next_token = responseJson.getAsJsonObject().getAsJsonPrimitive("next_cursor").getAsString();


        }
        return campaignsList;

    }
    public static String fetchCampaignStats(Campaigns campaignObject) throws URISyntaxException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException {

        //FETCH CAMPAIGN FUNDING INSTRUMENT DATA
        Funding funding=fetchFundData(campaignObject);
        //System.out.println("funding data is"+funding.spend+"----"+funding.fundingSource);




     /*   Fetch STATS data related to Campaigns
        1.BILLING;
        2.ENGAGEMENTS;
        3.MEDIA;
        4.VIDEO
        */

        List<String> metrics = new ArrayList<>();
        metrics.add(METRIC_GROUPS_BILLING);
        metrics.add(METRIC_GROUPS_ENGAGEMENT);
        metrics.add(METRIC_GROUPS_MEDIA);
        metrics.add(METRIC_GROUPS_VIDEO);

        Map<String, String> campaignStat = new HashMap<>();

        for (String metric : metrics) {
        /*
        Step 1: create new Oauth consumer.
        Step 2: create API URL. Taking BASE_URL and Account
        STEP 3: Create the parameters list to add in URI
        STEP 4: Create HTTP client
        STEP 5: SIGN the url ith the OAUTH
        STEP 6: HTTPCLient.execute
        STEP 7: CHECK THE RESPONSE CODE. IF 429 SLEEP FOR 15 MINS
        STEP 8: CREATE HTTPEntity TO GET CONTENT
        STEP 9: READ THE CONTENT IN InputStreamReader
        STEP 10: Get the data in JSONObject using Google GSON library. fromJson is the method
        STEP 11:  Create the JSONElement object with data key
        * */

            CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

            URIBuilder statsURL = new URIBuilder(BASE_URL).setPath(VERSION + STATS + ACCOUNT_URI + "/" + campaignObject.account.id);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("entity", "CAMPAIGN"));
            parameters.add(new BasicNameValuePair("entity_ids", campaignObject.campaignId));
            parameters.add(new BasicNameValuePair("start_time", campaignObject.startDate));
            parameters.add(new BasicNameValuePair("end_time", campaignObject.endDate));
            parameters.add(new BasicNameValuePair("granularity", GRANULARITY));
            parameters.add(new BasicNameValuePair("placement", PLACEMENT));
            parameters.add(new BasicNameValuePair("metric_groups", metric));

            statsURL.setParameters(parameters);
           // System.out.println("Executing the METRIC part--->" + campaignObject.campaignId + "----->" + statsURL.toString());
           // HttpClient client = HttpClientBuilder.create().build();
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build()).build();

            HttpGet request = new HttpGet(statsURL.toString());

            HttpResponse response = null;
            consumer.sign(request);

            response = client.execute(request);


            while (response.getStatusLine().getStatusCode() == 429) {
                System.out.println("GOT 429 Error");
                System.out.println("the value is" + response.getStatusLine().getStatusCode());
                Thread.sleep(SLEEP_TIME);
                response = client.execute(request);
            }
            HttpEntity entity = response.getEntity();
            Reader jsonResponse = new InputStreamReader(entity.getContent());

            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            JsonElement jsonElement = jsonObject.get("data");

            campaignStat.put("accountid",campaignObject.account.id);
            campaignStat.put("accountname",campaignObject.account.accountName);
            campaignStat.put("consumerkey",campaignObject.account.consumerKey);
            campaignStat.put("campaignid",campaignObject.campaignId);
            campaignStat.put("campaignname",campaignObject.campaignName);
            campaignStat.put("startdate",campaignObject.startDate);
            campaignStat.put("enddate",campaignObject.endDate);
            campaignStat.put("daily_budget_amount_local_micro",campaignObject.daily_budget_amount_local_micro);
            campaignStat.put("total_budget_amount_local_micro",campaignObject.total_budget_amount_local_micro);
            campaignStat.put("currency",campaignObject.currency);
            campaignStat.put("funding_source",funding.fundingSource);
            campaignStat.put("spend",funding.spend);
            campaignStat.put("cdate",LocalDate.now().toString());


            Map<String, String> statMap=null;
            statMap = ExtractCampignsStats.checkMetricType(jsonElement, metric);
            //System.out.println(statMap.values());

            campaignStat.putAll(statMap);


        }

        /*  STEP 1: Create ObjectMapper
        STEP 2: Intialize  OnjectNode and later assign object using ObjectMapper
        STEP 3: Create ArrayNode using ObjectMapper object
*/
        ObjectMapper mapper=new ObjectMapper();
        ObjectNode node=null;
        ArrayNode arrayNode=mapper.createArrayNode();

        node= mapper.createObjectNode();

        String [] stats=FINAL_COLUMNS.split("\\|");
        for(String dataStat:stats){
            //System.out.println(campaignStat.get(dataStat));
            node.put(dataStat,campaignStat.get(dataStat));

        }
        arrayNode.add(node);
        String line=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
       // String line=mapper.writeValueAsString(arrayNode);
      return line;





    }

}
