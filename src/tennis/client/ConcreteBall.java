package tennis.client;

import java.awt.*;

/**
 * Tennis ball
 */
public class ConcreteBall extends Ball
{
    private Color color;

    public ConcreteBall(int x, int y, int diameter, Color color)
    {
        super(x, y, diameter);
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public void draw(Graphics g)
    {
        Color temp = g.getColor();
        g.setColor(getColor());
        g.fillOval(getX(), getY(), getDiameter(), getDiameter());
        g.setColor(temp);
    }
}
