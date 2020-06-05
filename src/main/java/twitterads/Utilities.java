package twitterads;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import static twitterads.Constants.*;
import static twitterads.udf.ParseCampaignsJSON.*;
import static twitterads.Funding.fetchFundData;
//https://ads-api.twitter.com/accounts
//working https://ads-api.twitter.com/7/accounts
public class Utilities extends Thread{

    public static List<Account> fetchTwitterAccount(CommonsHttpOAuthConsumer consumer, HttpClient client,String twitterAccountURI,String consumerkey){
        List<Account> accountList=new ArrayList<Account>();
        System.out.println(twitterAccountURI);
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
    public static void fetchAccountCampaigns(CommonsHttpOAuthConsumer consumer,HttpClient client,URIBuilder campaignsURI,Account acc) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException, URISyntaxException {
        String next_token="NO_VALUE";
        while(next_token!=null) {
            System.out.println(client + "-->" + campaignsURI);
            client = HttpClientBuilder.create().build();
            HttpGet request = null;
            HttpResponse response = null;
            if(next_token.equals("NO_VALUE"))
                request=new HttpGet(campaignsURI.toString());
            else
                request=new HttpGet(campaignsURI.setParameter("cursor", next_token).toString());
            consumer.sign(request);

            System.out.println("the response is" + response);
            response = client.execute(request);
            System.out.println("ran sucessfully");
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println("________________________________________________");

            System.out.println("the respose code is " + response.getStatusLine().getStatusCode());
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

            parseCampaign(data,acc);

            JsonElement incommingToken=responseJson.get("next_cursor");
            System.out.println("---------------------------->"+incommingToken);
            System.out.println(incommingToken.getClass().getSimpleName()+"--->"+incommingToken.getClass().getCanonicalName()+"-->"+incommingToken.getClass().getName());
            if(incommingToken.getClass().getSimpleName().equals("JsonNull"))
                next_token=null;
            else
                next_token = responseJson.getAsJsonObject().getAsJsonPrimitive("next_cursor").getAsString();


        }


    }
    public static void fetchCampaignStats(String metricGroup,Campaigns campaignObject) throws URISyntaxException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException {

        //FETCH CAMPAIGN FUNDING INSTRUMENT DATA
        Funding funding=fetchFundData(campaignObject);
        System.out.println("funding data is"+funding.spend+"----"+funding.fundingSource);


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

        Billing billing=null;
        Engagement engagement=null;
        Video video=null;
        Media media=null;

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

            //Creating API URL
            //URIBuilder campaignPath=new URIBuilder(BASE_URL).setPath(VERSION+ACCOUNT_URI+"/"+acc.id+CAMPAIGN_URI);
            //&entity=CAMPAIGN&entity_ids=d1k94&start_time=2020-02-11&end_time=2020-02-18&granularity=DAY&placement=ALL_ON_TWITTER&metric_groups=MEDIA
            URIBuilder statsURL = new URIBuilder(BASE_URL).setPath(VERSION + STATS + ACCOUNT_URI + "/" + campaignObject.account.id);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("entity", "CAMPAIGN"));
            parameters.add(new BasicNameValuePair("entity_ids", campaignObject.campaignId));
            parameters.add(new BasicNameValuePair("start_time", campaignObject.startDate));
            parameters.add(new BasicNameValuePair("end_time", campaignObject.endDate));
            parameters.add(new BasicNameValuePair("granularity",GRANULARITY));
            parameters.add(new BasicNameValuePair("placement",PLACEMENT));
            parameters.add(new BasicNameValuePair("metric_groups", metric));

            statsURL.setParameters(parameters);
            System.out.println("Executing the METRIC part--->" + campaignObject.campaignId + "----->" + statsURL.toString());
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(statsURL.toString());
            HttpResponse response = null;
            consumer.sign(request);

            response = client.execute(request);


            while(response.getStatusLine().getStatusCode()==429){
                System.out.println("GOT 429 Error");
                System.out.println("the value is" + response.getStatusLine().getStatusCode());
                Thread.sleep(SLEEP_TIME);
                response = client.execute(request);
            }
            HttpEntity entity=response.getEntity();
            Reader jsonResponse=new InputStreamReader(entity.getContent());

            JsonObject jsonObject=new Gson().fromJson(jsonResponse,JsonObject.class);
            JsonElement jsonElement=jsonObject.get("data");
            List<String> statsList=new ArrayList<>();
            Map<String,Object> campaignStat=new HashMap<>();

            Object object= CampaignsStats.checkMetricType(jsonElement,metric);
            campaignStat.put(metric,object);

            Object object1= campaignStat.get(metric);



            if(object1 instanceof Billing) {
                billing = (Billing) object1;
                System.out.println(billing.billed_engagements);
            }
            if(object1 instanceof Engagement) {
                engagement = (Engagement) object1;
                System.out.println(engagement.impressions+"-----------------"+engagement.app_clicks);

            }

            if(object1 instanceof Media){
                media=(Media) object1;
                System.out.println(media.media_engagements);

            }
            if(object1 instanceof Video){
                video=(Video) object1;
                System.out.println(video.video_3s100pct_views);
                Thread.sleep(1000000);
            }








        }
    }

}
