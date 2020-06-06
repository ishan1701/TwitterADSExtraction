package twitterads.udf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import static twitterads.Constants.*;

public class ExtractCampignsStats {


    public static Map checkMetricType(JsonElement element,String metric){

        Map<String,String> statsMap=null;
        switch (metric){
            case METRIC_GROUPS_BILLING:
                statsMap=parseBillingData(element);
                break;
            case METRIC_GROUPS_ENGAGEMENT:
                statsMap=parseEngagementData(element);
                break;
            case METRIC_GROUPS_MEDIA:
                statsMap=parseMediaData(element);
                break;
            case METRIC_GROUPS_VIDEO:
                statsMap=parseVideoData(element);
                break;
            default:
                System.out.println("NOT CORRECT VALUE");
                break;
        }
        return statsMap;
    }



    public static Map parseVideoData(JsonElement element){

        Map<String,String> videoStats=new HashMap<String,String>();
        String [] metrics=VIDEO_STATS.split("\\|");

        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                for(String metric:metrics) {
                    JsonElement jsonElement=element2.getAsJsonObject().get("metrics").getAsJsonObject().get(metric);
                    long value=jsonElement.isJsonArray()?ParseJson.JSONArraySum(jsonElement.getAsJsonArray()):0;
                    videoStats.put(metric,Long.toString(value));
                }
            }
        }

        return videoStats;

    }

    public static Map parseBillingData(JsonElement element){

        Map<String,String> billingStats=new HashMap<String,String>();
        String [] metrics=BILLING_STATS.split("\\|");

        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                for(String metric:metrics) {
                    //System.out.println(metric);
                    JsonElement jsonElement=element2.getAsJsonObject().get("metrics").getAsJsonObject().get(metric);
                    long value=jsonElement.isJsonArray()?ParseJson.JSONArraySum(jsonElement.getAsJsonArray()):0;
                    billingStats.put(metric,Long.toString(value));
                }
            }
        }

        return billingStats;

    }

    public static Map parseEngagementData(JsonElement element){

        Map<String,String> engagementStats=new HashMap<String,String>();
        String [] metrics=ENGAGEMENT_STATS.split("\\|");

        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                for(String metric:metrics) {
                    JsonElement jsonElement=element2.getAsJsonObject().get("metrics").getAsJsonObject().get(metric);
                    long value=jsonElement.isJsonArray()?ParseJson.JSONArraySum(jsonElement.getAsJsonArray()):0;
                    engagementStats.put(metric,Long.toString(value));
                }
            }
        }

        return engagementStats;

    }

    public static Map parseMediaData(JsonElement element){

        Map<String,String> mediaStats=new HashMap<String,String>();
        String [] metrics=MEDIA_STATS.split("\\|");

        JsonArray jsonArray=element.getAsJsonArray();
        for(JsonElement element1:jsonArray){
            JsonArray id_data=element1.getAsJsonObject().getAsJsonArray("id_data");
            for(JsonElement element2:id_data){
                for(String metric:metrics) {
                    JsonElement jsonElement=element2.getAsJsonObject().get("metrics").getAsJsonObject().get(metric);
                    long value=jsonElement.isJsonArray()?ParseJson.JSONArraySum(jsonElement.getAsJsonArray()):0;
                    mediaStats.put(metric,Long.toString(value));
                }
            }
        }

        return mediaStats;

    }
}
