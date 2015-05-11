package tennis.client;

import esy.es.tennis.net.Receiver;
import esy.es.tennis.net.Sender;
import esy.es.tennis.net.SenderReceiverUDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class NicknameForm extends JFrame
{
    private JPanel mainPanel;
    private JTextField enterField = new JTextField(200);
    private JButton enterButton = new JButton("Play");
    private final int width = 400;
    private final int height = 350;
    private SenderReceiverUDP handler;
    private String serverHost;
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private String nickName;

    public NicknameForm( String serverHost )
    {
        super("Tennis Game");
        this.serverHost = serverHost;
        try
        {
            handler = new SenderReceiverUDP(InetAddress.getByName(serverHost), UDP_PORT_NUMBER);
        }
        catch (SocketException e)
        {
            JOptionPane.showMessageDialog(null, "Failure to create the socket object", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (UnknownHostException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        try
        {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {System.out.print("error");}

        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        JPanel nickNamePanel = new JPanel(new GridLayout(3, 1, 0, 10));
        nickNamePanel.setBackground(new Color(115, 175, 232));
        JLabel label = new JLabel("Enter your nickname", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.TRUETYPE_FONT, 16));
        nickNamePanel.add(label);
        nickNamePanel.add(enterField);
        ActionListener enterListener = new EnterNickNameListener();
        enterField.addActionListener(enterListener);
        enterButton.addActionListener(enterListener);
        nickNamePanel.add(enterButton);
        nickNamePanel.setSize(200, 85);
        nickNamePanel.setLocation(width / 2 - nickNamePanel.getWidth() / 2, height / 2 - nickNamePanel.getHeight());

        mainPanel.add(nickNamePanel);

        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        service.execute(this::listenServer);
    }

    private void listenServer()
    {
        try
        {
            String message;
            do
            {
                message = handler.receive();

                System.out.println("Received message: " + message);

                if (message.equals(invalidNick))
                {
                    JOptionPane.showMessageDialog(null, "This nickname is already in use", "Invalid Nickname", JOptionPane.ERROR_MESSAGE);
                    SwingUtilities.invokeLater(() -> enterField.setText(""));
                    handler.setDestinationAddress(InetAddress.getByName(serverHost));
                    handler.setDestinationPort(UDP_PORT_NUMBER);
                }
                else
                {
                    if (nickName != null)
                    {
                        SwingUtilities.invokeLater(this::dispose);
                        TennisMenu app = new TennisMenu(handler, nickName);
                        break;
                    }
                }
            } while ( message.equals(invalidNick) );
        }
                    /*catch (SocketException se)
                    {
                        JOptionPane.showMessageDialog(NicknameForm.this, "Could not connect to server. Please retry", "Error", JOptionPane.ERROR_MESSAGE);
                    }*/
        catch (IOException e1)
        {
            JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class EnterNickNameListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            nickName = enterField.getText();

            if (!nickName.isEmpty())
            {
                //service.execute(() -> {
                    handler.send(connect + "|" + nickName);
                System.out.println("message sent: " + connect + "|" + nickName);
               // });
            }
            else
                JOptionPane.showMessageDialog(null, "Nickname should not be empty", "Invalid Nickname", JOptionPane.ERROR_MESSAGE);
        }
    }
}
