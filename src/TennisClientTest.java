import tennis.client.NicknameForm;

public class TennisClientTest
{
    public static void main(String[] args)
    {
        NicknameForm form = new NicknameForm( args.length != 0 ? args[0] : "localhost" );
    }
}