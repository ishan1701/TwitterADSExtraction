package twitterads;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import static twitterads.Constants.*;
import org.apache.http.HttpResponse;
import static twitterads.Utilities.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class TwitterDataExtraction {
    public static void main(String[] args) throws URISyntaxException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException {
       CommonsHttpOAuthConsumer consumer= new CommonsHttpOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
       consumer.setTokenWithSecret(ACCESS_TOKEN,ACCESS_TOKEN_SECRET);

       //Creating API URL
       URIBuilder accountPath=new URIBuilder(BASE_URL).setPath(VERSION+ACCOUNT_URI);
        System.out.println(accountPath.toString());
       //Creating
       //HttpClient client=HttpClientBuilder.create().build();
        HttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()).build();
       List<Account> accountList=Utilities.fetchTwitterAccount(consumer,client,accountPath.toString(),CONSUMER_KEY);
       for(Account acc:accountList){
          // System.out.println(acc.id+"--->"+acc.accountName+"--->"+acc.consumerKey);
           client=HttpClientBuilder.create()
                   .setDefaultRequestConfig(RequestConfig.custom()
                           .setCookieSpec(CookieSpecs.STANDARD).build()).build();
          URIBuilder campaignPath=new URIBuilder(BASE_URL).setPath(VERSION+ACCOUNT_URI+"/"+acc.id+CAMPAIGN_URI);
           fetchAccountCampaigns(consumer,client,campaignPath,acc);

       }






    }
}
