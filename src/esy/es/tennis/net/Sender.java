package esy.es.tennis.net;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Sender
{
    void send( String message ) throws IOException;
}
