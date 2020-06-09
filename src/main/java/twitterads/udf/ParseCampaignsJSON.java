package twitterads.udf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import twitterads.Account;
import twitterads.Campaigns;
import twitterads.Constants;
import twitterads.Utilities;
import twitterads.udf.ParseJson.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static twitterads.Campaigns.generateCampaignsExtractionDate;

import static twitterads.Constants.*;

public class ParseCampaignsJSON {

    public static void parseCampaign(JsonElement element, Account account) throws InterruptedException, IOException, OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, URISyntaxException {
        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonObject campaignObject =element1.getAsJsonObject();

            //CHeck if start year is >2018 and campaign is PAUSED, Skill the records
            if(Integer.parseInt(campaignObject.getAsJsonPrimitive("start_time").getAsString().split("-")[0])<Integer.parseInt(START_DATE.split("-")[0]) ||campaignObject.getAsJsonPrimitive("entity_status").getAsString().equals("PAUSED"))
                continue;

            String name=campaignObject.getAsJsonPrimitive("name").getAsString();
            String startDate=ParseJson.splitDate(campaignObject.getAsJsonPrimitive("start_time").getAsString());
            String endDate=ParseJson.splitDate(ParseJson.isDateNUll(ParseJson.isNull(campaignObject,"end_time")));

            String total_budget_amount_local_micro=ParseJson.isNull(campaignObject,"total_budget_amount_local_micro");
            String funding_instrument_id=ParseJson.isNull(campaignObject,"funding_instrument_id");
            String daily_budget_amount_local_micro=ParseJson.isNull(campaignObject,"daily_budget_amount_local_micro");
            String campaignId=campaignObject.getAsJsonPrimitive("id").getAsString();
            String currency=campaignObject.getAsJsonPrimitive("currency").getAsString();
            /*System.out.println(campaignId+"---->"+name+"--->"+startDate+"---->"+total_budget_amount_local_micro+"--->"+endDate+"___"+daily_budget_amount_local_micro+" Account is "+account.id+"--->"+funding_instrument_id);
            Thread.sleep(2000);

*/
            List<Campaigns> campaignsList=generateCampaignsExtractionDate(campaignId,name,startDate,endDate,total_budget_amount_local_micro,daily_budget_amount_local_micro,funding_instrument_id,currency,account);
            for(Campaigns campaign:campaignsList){
               System.out.println(campaign.campaignId+"=="+campaign.startDate+"---"+campaign.endDate);
                // Call the stats APIs to get the BILLING
                Utilities.fetchCampaignStats(campaign);
            }
        }
    }
}
