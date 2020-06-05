package twitterads;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import twitterads.udf.ParseJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static twitterads.Constants.*;

class Billing{

    public String billed_engagements;
    Billing(String billed_engagements){
        this.billed_engagements=billed_engagements;
    }
}

class Engagement{
    public String impressions;
    public String engagements;
    public String retweets;
    public String replies;
    public String follows;
    public String clicks;
    public String likes;
    public String url_clicks;
    public String app_clicks;
    public String card_engagements;
    public String tweets_send;
    public String qualified_impressions;
    Engagement(String impressions,String engagements,String retweets,String replies,String follows,String clicks,String likes,String url_clicks,String app_clicks,String card_engagements,String tweets_send,String qualified_impressions){
        this.impressions=impressions;
        this.engagements=engagements;
        this.retweets=retweets;
        this.replies=replies;
        this.follows=follows;
        this.clicks=clicks;
        this.likes=likes;
        this.url_clicks=url_clicks;
        this.app_clicks=app_clicks;
        this.card_engagements=card_engagements;
        this.tweets_send=tweets_send;
        this.qualified_impressions=qualified_impressions;
    }

}

public class CampaignsStats {

    public String spend;
    public String funding_source;
    public String impressions;
    public String engagements;
    public String retweets;
    public String replies;
    public String follows;
    public String clicks;
    public String likes;
    public String url_clicks;
    public String app_clicks;
    public String card_engagements;
    public String tweets_send;
    public String qualified_impressions;
    public String video_views_25;
    public String video_views_75;
    public String video_views_100;
    public String video_total_views;
    public String video_3s100pct_views;
    public String video_cta_clicks;
    public String video_content_starts;
    public String video_mrc_views;
    public String media_views;
    public String media_engagements;
    public String billed_engagements;

    public static Object checkMetricType(JsonElement element,String metric){

        Object stats=null;
        switch (metric){
            case METRIC_GROUPS_BILLING:
                stats=parseBillingData(element);
                break;
            case METRIC_GROUPS_ENGAGEMENT:
                stats=parseEngagementData(element);
                break;
            case METRIC_GROUPS_MEDIA:
                parseMediaData();
                break;
            case METRIC_GROUPS_VIDEO:
                parseVideoData();
                break;
            default:
                System.out.println("NOT CORRECT VALUE");
                break;
        }
        return stats;
    }


    public static Billing parseBillingData(JsonElement element){
        long billed_engagements=0;
        List<String> billingDataList=new ArrayList<>();

        JsonArray jsonArray=element.getAsJsonArray();
        for (JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                JsonElement billed_engagements_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("billed_engagements");
              /*  if(billed_engagements_object.isJsonArray())
                    billed_engagements=ParseJson.JSONArraySum(billed_engagements_object.getAsJsonArray());
                else {
                    billed_engagements=0;
                }*/
                billed_engagements=billed_engagements_object.isJsonArray()?ParseJson.JSONArraySum(billed_engagements_object.getAsJsonArray()):0;


            }
        }
        //billingDataList.add(Long.toString(billed_engagements));
        //return billingDataList;
        System.out.println("the valus f billed data is"+billed_engagements);
        return new Billing(Long.toString(billed_engagements));
    }


    public static Engagement parseEngagementData(JsonElement element){
        long impressions=0;
        long engagements=0;
        long retweets=0;
        long replies=0;
        long follows=0;
        long clicks=0;
        long likes=0;
        long url_clicks=0;
        long app_clicks=0;
        long card_engagements=0;
        long tweets_send=0;
        long qualified_impressions=0;

        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                JsonElement impressions_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("impressions");
                JsonElement engagements_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("engagements");
                JsonElement retweets_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("retweets");
                JsonElement replies_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("replies");
                JsonElement follows_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("follows");
                JsonElement clicks_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("clicks");
                JsonElement likes_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("likes");
                JsonElement url_clicks_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("url_clicks");
                JsonElement app_clicks_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("app_clicks");
                JsonElement card_engagements_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("card_engagements");
                JsonElement tweets_send_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("tweets_send");
                JsonElement qualified_impressions_object=element2.getAsJsonObject().get("metrics").getAsJsonObject().get("qualified_impressions");


                impressions=impressions_object.isJsonArray()?ParseJson.JSONArraySum(impressions_object.getAsJsonArray()):0;
                engagements=engagements_object.isJsonArray()?ParseJson.JSONArraySum(engagements_object.getAsJsonArray()):0;
                retweets=retweets_object.isJsonArray()?ParseJson.JSONArraySum(retweets_object.getAsJsonArray()):0;
                replies=replies_object.isJsonArray()?ParseJson.JSONArraySum(replies_object.getAsJsonArray()):0;
                follows=follows_object.isJsonArray()?ParseJson.JSONArraySum(follows_object.getAsJsonArray()):0;
                clicks=clicks_object.isJsonArray()?ParseJson.JSONArraySum(clicks_object.getAsJsonArray()):0;
                likes=likes_object.isJsonArray()?ParseJson.JSONArraySum(likes_object.getAsJsonArray()):0;
                url_clicks=url_clicks_object.isJsonArray()?ParseJson.JSONArraySum(url_clicks_object.getAsJsonArray()):0;
                app_clicks=app_clicks_object.isJsonArray()?ParseJson.JSONArraySum(app_clicks_object.getAsJsonArray()):0;
                card_engagements=card_engagements_object.isJsonArray()?ParseJson.JSONArraySum(card_engagements_object.getAsJsonArray()):0;
                tweets_send=tweets_send_object.isJsonArray()?ParseJson.JSONArraySum(tweets_send_object.getAsJsonArray()):0;
                qualified_impressions=qualified_impressions_object.isJsonArray()?ParseJson.JSONArraySum(qualified_impressions_object.getAsJsonArray()):0;


            }
        }
        System.out.println("The impressions is "+impressions);

       return new Engagement(Long.toString(impressions),Long.toString(engagements),Long.toString(retweets),Long.toString(replies),Long.toString(follows),Long.toString(clicks),Long.toString(likes),Long.toString(url_clicks),Long.toString(app_clicks),Long.toString(card_engagements),Long.toString(tweets_send),Long.toString(qualified_impressions));


    }
    public static void parseMediaData(){

    }
    public static void parseVideoData(){

    }
}
