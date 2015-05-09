import tennis.client.NicknameForm;
import tennis.client.TennisClient;

public class TennisClientTest
{
    public static void main(String[] args)
    {
//        TennisClient client = new TennisClient( args.length == 0 ? "localhost" : args[0] );
//        client.runClient();

        NicknameForm form = new NicknameForm( args.length != 0 ? args[0] : "localhost" );
    }
}