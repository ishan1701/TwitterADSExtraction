package twitterads;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import static twitterads.Constants.*;

public class Funding {

    String fundingSource;
    String spend;

    Funding(String fundingSource,String spend){
        this.fundingSource=fundingSource;
        this.spend=spend;
    }

    public static Funding fetchFundData(Campaigns campaign) throws URISyntaxException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {

          /*
        Step 1: create new Oauth consumer.
        Step 2: create API URL(URIBuilder). Taking BASE_URL and Account
        STEP 3: Create the parameters list to add in URI -IF ANY
        STEP 4: Create HTTP client and HTTPGET request object
        STEP 5: SIGN the url ith the OAUTH
        STEP 6: HTTPCLient.execute
        STEP 7: CHECK THE RESPONSE CODE. IF 429 SLEEP FOR 15 MINS
        STEP 8: CREATE HTTPEntity TO GET CONTENT
        STEP 9: READ THE CONTENT IN InputStreamReader
        STEP 10: Get the data in JSONObject using Google GSON library. fromJson is the method
        STEP 11:  Create the JSONElement object with data key
        * */
        CommonsHttpOAuthConsumer consumer=new CommonsHttpOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN,ACCESS_TOKEN_SECRET);
        URIBuilder fundingURI=new URIBuilder(BASE_URL).setPath(VERSION+ACCOUNT_URI+"/"+campaign.account.id+FUNDING_URI+"/"+campaign.funding_instrument_id);
        System.out.println(fundingURI.toString());
        //HttpClient client=HttpClientBuilder.create().build();
        HttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()).build();

        HttpGet request=new HttpGet(fundingURI.toString());
        HttpResponse response=null;

        consumer.sign(request);

        response=client.execute(request);



        HttpEntity entity=response.getEntity();
        Reader reader=new InputStreamReader(entity.getContent());

        JsonObject jsonObject=new Gson().fromJson(reader,JsonObject.class);

        JsonElement data=jsonObject.get("data");

        String description=data.getAsJsonObject().get("description").getAsString();
        String funded_amount_local_micro=data.getAsJsonObject().get("funded_amount_local_micro").getAsString();

        return new Funding(description,funded_amount_local_micro);





    }
}
