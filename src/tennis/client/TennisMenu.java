package tennis.client;

import esy.es.tennis.net.SenderReceiverUDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;
import static esy.es.tennis.shared.TennisAppConstants.playersList;

public class TennisMenu extends JFrame
{
    //region GUI components
    private JPanel mainPanel;
    private JLabel nickNameLabel;
    private JList<String> playersJList;
    private JLabel statusLabel;
    private JTextArea chatTextArea;
    private JButton playButton;
    private JButton refreshButton;
    private JTextField chatTextField;
    private JButton sendButton;
    private DefaultListModel<String> listModel;
    //endregion
    private TennisGame tennisGame = null;
    private SenderReceiverUDP handler;
    private String nickName;
    private ExecutorService service = Executors.newCachedThreadPool();


    public TennisMenu(SenderReceiverUDP handler, String nickName) throws HeadlessException
    {
        super("Tennis Menu");
        this.handler = handler;
        this.nickName = nickName;
        nickNameLabel.setText(nickName);
        statusLabel.setText("Choose a player from the list to play with");

        setContentPane(mainPanel);
        listModel = new DefaultListModel<String>();
        playersJList.setModel(listModel);

        setSize(850, 550);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        refreshButton.addActionListener(e -> {
            service.execute(() -> {
                handler.send(playersList);
            });
        });

        playButton.setEnabled(false);

        playButton.addActionListener(e -> {
            handler.send(askPlay + separator + playersJList.getSelectedValue());
        });

        playersJList.addListSelectionListener(e -> {
            if (playersJList.getSelectedIndex() != -1 && !playersJList.getSelectedValue().equals(nickName))
                playButton.setEnabled(true);
            else
                playButton.setEnabled(false);

            this.repaint();
        });

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);

                int result = JOptionPane.showConfirmDialog(TennisMenu.this, "Do you really want to exit?", "Question", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION)
                {
                    handler.send(disconnect);
                    System.exit(1);
                }
            }
        });

        service.execute(this::listenServer);
    }

    public void listenServer()
    {
        handler.send(playersList);
        String message = null;

        do
        {
            try
            {
                message = handler.receive();
                displayMessage("Received message from server: " + message);
                processMessage(message);
            }
            catch (IOException e)
            {
                displayMessage("An I/O error occurred while reading from stream");
            }
        } while ( message == null || !message.equals(disconnect) );
    }

    private void processMessage(String message)
    {
        String[] words = message.split(Pattern.quote(separator));

        if (words.length > 0)
        {
            switch (words[0])
            {
                case playersList:
                    setListItems(words);
                    break;
                case askPlay:
                    int result = JOptionPane.showConfirmDialog(this, "The player " + words[1] + " wants to play with you!");
                        if (result == JOptionPane.OK_OPTION)
                            handler.send( respPlay + separator + yes +separator + words[1] );
                        else
                            handler.send( respPlay + separator + no + separator + words[1] );
                    break;
                case respPlay:
                    if (words[1].equals(yes))       // player accepted to play
                    {
                        // create the board and start playing the game
                        if (tennisGame == null)
                        {
                            tennisGame = new TennisGame(handler, nickName, words[2], this);
                            handler.send(createdBoard + separator + words[2]);
                        }
                        else
                            handler.send(occupied + separator + words[2]);
                    }
                    else if (words[1].equals(no))
                        JOptionPane.showMessageDialog(this, "Player " + words[2] + " refused to play against you!", "Answer",
                                JOptionPane.INFORMATION_MESSAGE);
                    break;
                case createdBoard:
                    if (tennisGame == null)
                    {
                        tennisGame = new TennisGame(handler, nickName, words[1], this);
                    }
                    else
                        handler.send(occupied + separator + words[1]);
                    break;
                default:
                    if (tennisGame != null)
                        tennisGame.processMessage( words );     // let tennis game object process game related objects
                    else
                        displayMessage("Unused message: " + message);
                    break;
            }
        }
    }

    private void setListItems( final String[] items )
    {
        SwingUtilities.invokeLater( () -> {
            listModel.removeAllElements();
            for (int i = 1; i < items.length; i++)
            {
                String item = items[i];
                String[] data = item.split(Pattern.quote(separator2));
                if (data.length == 2)
                {
                    listModel.addElement(data[0]);
                    //playersJList.setListData();   ****
                }
            }
        });
    }

    private void displayMessage(final String s)
    {
        SwingUtilities.invokeLater(() -> statusLabel.setText(s));
    }

    public void setTennisGame(TennisGame tennisGame)
    {
        this.tennisGame = tennisGame;
    }
}
