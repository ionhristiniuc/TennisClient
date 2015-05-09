package tennis.client;

import java.awt.*;

/**
 * Concrete tennis palette
 */
public class ConcretePalette extends Palette
{
    private Color color;

    public ConcretePalette(int x, int y, int width, int height, Color color)
    {
        super(x, y, width, height);
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
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        g.setColor(temp);
    }
}
