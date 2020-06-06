package twitterads;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Campaigns {
    public String campaignId;
    public String campaignName;
    public String startDate;
    public String endDate;
    public String total_budget_amount_local_micro;
    public String daily_budget_amount_local_micro;
    public String funding_instrument_id;
    public String currency;
    public Account account;

    Campaigns(String campaignId,String campaignName,String startDate,String endDate,String total_budget_amount_local_micro,String daily_budget_amount_local_micro,String funding_instrument_id,String currency,Account account){
        this.campaignId=campaignId;
        this.campaignName=campaignName;
        this.startDate=startDate;
        this.endDate=endDate;
        this.total_budget_amount_local_micro=total_budget_amount_local_micro;
        this.daily_budget_amount_local_micro=daily_budget_amount_local_micro;
        this.funding_instrument_id=funding_instrument_id;
        this.currency=currency;
        this.account=account;
    }
    public static List<Campaigns> generateCampaignsExtractionDate(String campaignId, String campaignName, String startDate, String endDate, String total_budget_amount_local_micro, String daily_budget_amount_local_micro, String funding_instrument_id,String currency, Account account){
        List<Campaigns> campgainsList=new ArrayList<Campaigns>();
        LocalDate start=LocalDate.parse(startDate);
        LocalDate end=LocalDate.parse(endDate);
        final int NEXT_DAYS=7;
        final int DATE_DIFF=8;

        long dateDiff=ChronoUnit.DAYS.between(start,end);
        if(dateDiff>DATE_DIFF)
            end=start.plusDays(NEXT_DAYS);

        while (ChronoUnit.DAYS.between(start,LocalDate.parse(endDate))>-1){
            campgainsList.add(new Campaigns(campaignId,campaignName,start.toString(),end.toString(),total_budget_amount_local_micro,daily_budget_amount_local_micro,funding_instrument_id,currency,account));
            //System.out.println("Adding new campaigns in the list");
            start=start.plusDays(7);
            end=start.plusDays(7);
        }
        return campgainsList;
    }


}
