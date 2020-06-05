package twitterads;

public class Account {

    public String id;
    public String accountName;
    public String consumerKey;

    Account(String id, String name, String consumerKey){
        this.id=id;
        this.accountName=name;
        this.consumerKey=consumerKey;
    }
}
