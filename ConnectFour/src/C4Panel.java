import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class C4Panel provides a widget for displaying a Connect Four Board in a 3.
 *
 * @author  Alan Kaminsky
 * @version 13-Oct-2014
 */
public class C4Panel
        extends JPanel
{

// Hidden constants.

    private static final int W = 60;
    private static final int D = 48;
    private static final int OFFSET = (W - D)/2;

    private static final Color BG_COLOR = new Color (77, 136, 255);
    private static final Color NO_COLOR = new Color (230, 230, 230);
    private static final Color P1_COLOR = new Color (255, 0, 0);
    private static final Color P2_COLOR = new Color (255, 255, 0);
    private static final Color WIN_COLOR = new Color (0, 0, 0);

    private static final BasicStroke WIN_STROKE =
            new BasicStroke (D/4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

// Hidden data members.

    private C4BoardIntf board;

// Exported constructors.

    /**
     * Construct a new Connect Four panel.
     *
     * @param  board  Connect Four board object to be queried when displaying
     *                the panel.
     */
    public C4Panel
    (C4BoardIntf board)
    {
        super();
        this.board = board;

        Dimension dim = new Dimension (W*C4BoardIntf.COLS, W*C4BoardIntf.ROWS);
        setMinimumSize (dim);
        setPreferredSize (dim);
        setMaximumSize (dim);
        setBackground (BG_COLOR);
    }

// Exported operations.

    /**
     * Determine the row on this Connect Four panel that was clicked.
     *
     * @param  e  Mouse event.
     *
     * @return  Row index.
     */
    public int clickToRow
    (MouseEvent e)
    {
        return e.getY()/W;
    }

    /**
     * Determine the column on this Connect Four panel that was clicked.
     *
     * @param  e  Mouse event.
     *
     * @return  Column index.
     */
    public int clickToColumn
    (MouseEvent e)
    {
        return e.getX()/W;
    }

// Hidden operations.

    /**
     * Paint this Connect Four panel in the given graphics context.
     *
     * @param  g  Graphics context.
     */
    protected void paintComponent
    (Graphics g)
    {
        super.paintComponent (g);

        // Clone graphics context.
        Graphics2D g2d = (Graphics2D) g.create();

        // Turn on antialiasing.
        g2d.setRenderingHint
                (RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

        // Display board.
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        ellipse.width = D;
        ellipse.height = D;
        Color color = null;
        synchronized (board)
        {
            // Draw spots.
            for (int r = 0; r < C4BoardIntf.ROWS; ++ r)
            {
                for (int c = 0; c < C4BoardIntf.COLS; ++ c)
                {
                    if (board.hasPlayer1Marker (r, c))
                        color = P1_COLOR;
                    else if (board.hasPlayer2Marker (r, c))
                        color = P2_COLOR;
                    else
                        color = NO_COLOR;
                    ellipse.x = c*W + OFFSET;
                    ellipse.y = r*W + OFFSET;
                    g2d.setColor (color);
                    g2d.fill (ellipse);
                }
            }

            // Draw win line if any.
            int[] coord = board.hasWon();
            if (coord != null)
            {
                g2d.setStroke (WIN_STROKE);
                g2d.setColor (WIN_COLOR);
                Line2D.Double line = new Line2D.Double();
                line.x1 = (coord[1] + 0.5)*W;
                line.y1 = (coord[0] + 0.5)*W;
                line.x2 = (coord[3] + 0.5)*W;
                line.y2 = (coord[2] + 0.5)*W;
                g2d.draw (line);
            }
        }
    }

}
