package twitterads;

public class Constants {

    private Constants(){

    }
    public static final String CONSUMER_KEY="T22UtnwkCK1mYYOl2Y13gejFq";
    public static final String CONSUMER_SECRET="FT4ArrOI1R3AbRjKPlXMf8D6fzxdnLsBpCGgaqeajBqwFMiIlc";
    public static final String ACCESS_TOKEN="1317958368-xHQsb0Ztw0mZXZ5BmeaVpnVhyFowdzAfmlOF5Do";
    public static final String ACCESS_TOKEN_SECRET="VapIhnCIkMSwqd0ZUEgGeXENPlu0zHl0gJUkw0ADeEDfS";


    public static final long SLEEP_TIME=900000;

    public static final String BASE_URL="https://ads-api.twitter.com";
    public static final String VERSION="7";
    public static final String ACCOUNT_URI="/accounts";
    public static final String CAMPAIGN_URI="/campaigns";

    public static final String START_DATE="2020-01-01";
    public static final String END_DATE="2020-05-01";

    public static final String STATS="/stats";

    public static final String GRANULARITY="DAY";
    public static final String PLACEMENT="ALL_ON_TWITTER";

    public static final String METRIC_GROUPS_ENGAGEMENT="ENGAGEMENT";
    public static final String METRIC_GROUPS_MEDIA="MEDIA";
    public static final String METRIC_GROUPS_VIDEO="VIDEO";
    public static final String METRIC_GROUPS_BILLING="BILLING";

    public static final String FUNDING_URI="/funding_instruments";

    public static final String FINAL_COLUMNS="accountid|accountname|consumerkey|campaignid|campaignname|cdate|startdate|enddate|daily_budget_amount_local_micro|total_budget_amount_local_micro|currency|funding_source|spend|impressions|engagements|billed_engagements|retweets|replies|follows|clicks|media_engagements|likes|url_clicks|app_clicks|card_engagements|tweets_send|qualified_impressions|video_views_25|video_views_75|video_views_100|video_total_views|video_3s100pct_views|video_cta_clicks|video_content_starts|video_mrc_views|media_views|billed_charge_local_micro";

    public static final String VIDEO_STATS="video_views_25|video_views_75|video_views_100|video_total_views|video_3s100pct_views|video_cta_clicks|video_content_starts|video_mrc_views";
    public static final String BILLING_STATS="billed_engagements|billed_charge_local_micro";
    public static final String ENGAGEMENT_STATS="impressions|engagements|retweets|replies|follows|clicks|likes|url_clicks|app_clicks|card_engagements|tweets_send|qualified_impressions";
    public static final String MEDIA_STATS="media_engagements|media_views";

}
