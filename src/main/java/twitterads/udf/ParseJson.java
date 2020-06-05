package twitterads.udf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.LocalDate;
import static twitterads.Constants.END_DATE;

public class ParseJson {

    public static String isNull(JsonObject object,String value){
        if(object.get(value)instanceof JsonNull)
            return "NULL";
        else
            return object.getAsJsonPrimitive(value).getAsString();


    }
    public static String isDateNUll(String date){
        if(date.equals("NULL"))
            return END_DATE;
        else
            return date;
    }
    public static String splitDate(String date){
        return date.split("T")[0];
    }

    public static LocalDate convertStringtoDate(String date){
       return LocalDate.parse(date);
    }

    public static long JSONArraySum(JsonArray jsonArray){
        long sum=0;
        for(JsonElement element:jsonArray){
            sum+=element.getAsJsonPrimitive().getAsLong();
        }

        return sum;
    }
}
