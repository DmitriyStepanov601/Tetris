import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * A class describing tetris
 * @author Dmitriy Stepanov
 */
public class Tetris {
	public static final int WIDTH = 515, HEIGHT = 642;
	private final Board board;
	private final Intro intro;
	private final JFrame tetris;

	/**
	 * Constructor - creating a new Tetris
	 * @see Tetris#Tetris()
	 */
	public Tetris(){
		tetris = new JFrame("Tetris");
		tetris.setSize(WIDTH, HEIGHT);
		tetris.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tetris.setLocationRelativeTo(null);
		tetris.setResizable(false);

		Image windowIcon = null;
		try {
			windowIcon = ImageIO.read(Tetris.class.getResource("/tetris.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tetris.setIconImage(windowIcon);
		board = new Board();
		intro = new Intro(this);
		tetris.addKeyListener(board);
		tetris.addMouseListener(intro);
		tetris.add(intro);
		tetris.setVisible(true);
	}

	public void startTetris(){
		tetris.remove(intro);
		tetris.addMouseMotionListener(board);
		tetris.addMouseListener(board);
		tetris.add(board);
		board.startGame();
		tetris.revalidate();
	}
	
	public static void main(String[] args) {
		new Tetris();
	}
}