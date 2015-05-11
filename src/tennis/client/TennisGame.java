package tennis.client;

import esy.es.tennis.net.SenderReceiverUDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static esy.es.tennis.shared.TennisAppConstants.*;

/**
 * Tennis client
 */
public class TennisGame extends JFrame
{
    private JPanel mainPanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JLabel thisPlayerLabel;
    private JLabel otherPlayerLabel;
    private JLabel thisPlayerScore;
    private JLabel otherPlayerScore;
    private String hostName;
    private ExecutorService executorService;
    private Board board;
    private SenderReceiverUDP handler;
    private String nickName;
    private String enemyNickName;
    private TennisMenu tennisMenu;
    private boolean gameRunning = false;

    public TennisGame(SenderReceiverUDP handler, String nickName, String secondPlayer, TennisMenu tennisMenu)
    {
        super("Tennis Game");
        this.handler = handler;
        this.tennisMenu = tennisMenu;
        setContentPane(mainPanel);
        this.nickName = nickName;
        this.enemyNickName = secondPlayer;
        thisPlayerLabel.setText(nickName);
        otherPlayerLabel.setText(secondPlayer);

        executorService = Executors.newCachedThreadPool();

        ConcretePalette first = new ConcretePalette(boardWidth / 2 - paletteWidth / 2, boardHeight - paletteHeight * 2, // this player's palette
                paletteWidth, paletteHeight, Color.RED);
        ConcretePalette second = new ConcretePalette(boardWidth / 2 - paletteWidth / 2, 0,
                paletteWidth, paletteHeight, Color.BLUE);
        Ball ball = new ConcreteBall(boardWidth / 2 - ballDiameter / 2, boardHeight / 2 - ballDiameter / 2, ballDiameter, Color.WHITE);

        board = new Board(first, second, ball);
        board.setBackground(Color.BLACK);
        mainPanel.add(board);
        setVisible(true);
        //setSize(boardWidth, boardHeight + 30);
        this.pack();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.requestFocusInWindow();

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

                        handler.send(mess);
                    });
                }
            }
        });

        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                TennisGame.this.requestFocusInWindow();
            }
        });

        startButton.addActionListener(e -> {
            if (!gameRunning)
                handler.send(startGame + separator + enemyNickName);
        });

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                handler.send( gameStopped + separator + enemyNickName );
                TennisGame.this.dispose();
                tennisMenu.setTennisGame(null);
            }
        });
    }

    public void processMessage(String[] message)
    {
        if (message.length != 0)
        {
            switch (message[0])
            {
                case startGame:
                    int result = JOptionPane.showConfirmDialog(this, "Do you want to start the game know?", "Start the Game",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION)
                    {
                        handler.send(startGameSec + separator + message[1]);
                        gameRunning = true;
                    }
                    break;
                case startGameSec:
                    gameRunning = true;
                    break;
                case gameStopped:
                    JOptionPane.showMessageDialog(null, "Second player has leaved the board", "Game ended", JOptionPane.INFORMATION_MESSAGE);
                    tennisMenu.setTennisGame( null );
                    SwingUtilities.invokeLater(this::dispose);
                    break;
                case notification:
                    displayMessage(message[1]);
                    break;
                case updateBoard:
                    //SwingUtilities.invokeLater(() -> {
                    board.update(Integer.parseInt(message[1]), Integer.parseInt(message[2]), Integer.parseInt(message[3]), Integer.parseInt(message[4]));
                    //});
                    break;
                case ballMove:
                    board.updateBallLocation(Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                    break;
            }
        }
    }

    private void displayMessage( final String message )
    {
        //SwingUtilities.invokeLater(() -> {
            System.out.println(message);
        //});
    }
}
