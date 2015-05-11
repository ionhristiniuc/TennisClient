package esy.es.tennis.net;

import java.io.IOException;
import java.net.*;

public class SenderReceiverUDP implements Sender, Receiver
{
    private DatagramSocket socket;
    private InetAddress destinationAddress;
    private int destinationPort;
    private int maxLength = 500;

    public SenderReceiverUDP(InetAddress destinationAddress, int destinationPort) throws SocketException
    {
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        socket = new DatagramSocket();
    }

    @Override
    public String receive() throws IOException
    {
        byte[] data = new byte[maxLength];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        socket.receive(receivePacket);
        destinationPort = receivePacket.getPort();  // saving new port
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public String receive( int millis ) throws SocketException
    {
        socket.setSoTimeout(millis);
        try
        {
            return receive();
        }
        catch (SocketException se)
        {
            throw se;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            socket.setSoTimeout(0);
        }

        return null;
    }

    @Override
    public void send(String message)
    {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, destinationAddress, destinationPort);
        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public InetAddress getDestinationAddress()
    {
        return destinationAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress)
    {
        this.destinationAddress = destinationAddress;
    }

    public int getDestinationPort()
    {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort)
    {
        this.destinationPort = destinationPort;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }
}
