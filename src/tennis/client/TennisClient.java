package tennis.client;

import esy.es.tennis.net.SenderReceiverUDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;

/**
 * Tennis client
 */
public class TennisClient extends JFrame
{
    private JPanel mainPanel;
    private String hostName;
    //private Socket connection;
    //private DatagramSocket socket;
//    private ObjectOutputStream output;
//    private ObjectInputStream input;
    private ExecutorService executorService;
    private Board board;
    private SenderReceiverUDP handler;

    public TennisClient(SenderReceiverUDP handler)
    {
        super("Tennis Game");
        this.handler = handler;
        setContentPane(mainPanel);

        //this.hostName = serverName;
        executorService = Executors.newCachedThreadPool();

        ConcretePalette first = new ConcretePalette(boardWidth / 2 - paletteWidth / 2, boardHeight - paletteHeight * 2, // this player's palette
                paletteWidth, paletteHeight, Color.RED);
        ConcretePalette second = new ConcretePalette(boardWidth / 2 - paletteWidth / 2, 0,
                paletteWidth, paletteHeight, Color.BLUE);
        Ball ball = new ConcreteBall(boardWidth / 2 - ballDiameter / 2, boardHeight / 2 - ballDiameter / 2, ballDiameter, Color.GREEN);

        board = new Board(first, second, ball);
        board.setBackground(Color.BLACK);
        mainPanel.add(board);
        setVisible(true);
        setSize(boardWidth, boardHeight + 30);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    executorService.execute(() -> {
                        String mess = (e.getKeyCode() == KeyEvent.VK_LEFT ? movePaletteLeft : movePaletteRight)
                                + separator + board.getFirstPalette().getX();
                        //sendData(mess.getBytes());
                        try
                        {
                            handler.send(mess);
                        }
                        catch (IOException e1)
                        {
                            displayMessage("I/O exception occurred");
                        }
                    });
                }
            }
        });
    }

    public void runClient()
    {
        try
        {
            //socket = new DatagramSocket();
            //connection = new Socket(hostName, PORT_NUMBER);
            //getStreams();
            processConnection();
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            displayMessage("An I/O error occurred");
            System.exit(1);
        }
//        finally
//        {
//            closeConnection();
//        }
    }

//    private void getStreams() throws IOException
//    {
//        output = new ObjectOutputStream(connection.getOutputStream());
//        output.flush();
//
//        input = new ObjectInputStream(connection.getInputStream());
//    }

//    private void sendData( byte[] data )
//    {
//        try
//        {
//            DatagramPacket toSend = new DatagramPacket(data, data.length, InetAddress.getByName(hostName), UDP_PORT_NUMBER);
//            socket.send(toSend);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

//    private String receiveMessage() throws IOException
//    {
//        byte[] buffer = new byte[100];
//        DatagramPacket receivePacket = new DatagramPacket( buffer, 0, buffer.length );
//        socket.receive(receivePacket);
//        return new String(receivePacket.getData(), 0, receivePacket.getLength());
//    }

    private void processConnection() throws IOException
    {
        String message = null;

        // send a simple message to connect
        String mess = "Connection message";
        handler.send(mess);

        do
        {
            try
            {
                message = handler.receive();
                //displayMessage("Received message from server: " + message);
                processMessage(message);

            }
            catch (IOException e)
            {
                displayMessage("An I/O error occurred while reading from stream");
                return;
            }
//            catch (ClassNotFoundException e)
//            {
//                //e.printStackTrace();
//                displayMessage("\nInvalid object received");
//            }
        } while ( message == null || !message.equals(disconnect) );
    }

    private void processMessage( String message )
    {
        String[] words = message.split(Pattern.quote(separator));

        if (words.length != 0)
        {
            switch (words[0])
            {
                case notification:
                    displayMessage(words[1]);
                    break;
                case updateBoard:
                    //SwingUtilities.invokeLater(() -> {
                        board.update(Integer.parseInt(words[1]), Integer.parseInt(words[2]), Integer.parseInt(words[3]), Integer.parseInt(words[4]));
                    //});
                case ballMove:
                    board.updateBallLocation(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                    break;
            }
        }
    }

//    private void closeConnection()
//    {
//        try
//        {
//            output.close();
//            input.close();
//            connection.close();
//        }
//        catch (IOException e)
//        {
//            //e.printStackTrace();
//            displayMessage("An I/O error occurred while closing connection");
//        }
//    }

    private void displayMessage( final String message )
    {
        //SwingUtilities.invokeLater(() -> {
            System.out.println(message);
        //});
    }

//    private void sendMessage( String message )
//    {
//        try
//        {
//            output.writeObject(message);
//            output.flush();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
}
