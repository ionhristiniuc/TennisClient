package tennis.client;

import javax.swing.*;
import java.awt.*;

import static esy.es.tennis.shared.TennisAppConstants.*;

/**
 * Tennis board
 */
public class Board extends JPanel
{
    private Palette firstPalette;
    private Palette secondPalette;
    private Ball ball;

    public Board( Palette first, Palette second, Ball ball )
    {
        setLayout(null);
        setBackground(Color.WHITE);

        firstPalette = first;
        secondPalette = second;
        this.ball = ball;

        setSize(boardWidth, boardHeight);
        setPreferredSize( new Dimension(boardWidth, boardHeight));
    }

    public Palette getFirstPalette()
    {
        return firstPalette;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        ball.draw(g);
        firstPalette.draw(g);
        secondPalette.draw(g);
    }

    public void updateBallLocation( int ballX, int ballY )
    {
        ball.setX(ballX);
        ball.setY(ballY);
        SwingUtilities.invokeLater(this::repaint);
    }

    public void update( int ballX, int ballY, int firstPaletteX, int secondPaletteX )
    {
        ball.setX(ballX);
        ball.setY(ballY);
        firstPalette.setX(firstPaletteX);
        secondPalette.setX(secondPaletteX);
        SwingUtilities.invokeLater(this::repaint);
    }
}
