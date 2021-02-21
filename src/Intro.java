import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A class that describes the game's splash screen
 * @author Dmitriy Stepanov
 */
public class Intro extends JPanel implements MouseListener  {
	private boolean leftClick = false;
	private BufferedImage intro, play, background, title;
	private final Tetris tetris;

	/**
	 * Constructor - creating a new game screen saver
	 * @param tetris - screen the game
	 * @see Intro#Intro(Tetris)
	 */
	public Intro(Tetris tetris){
		try {
			intro = ImageIO.read(Board.class.getResource("/intro.png"));
			background = ImageIO.read(Board.class.getResource("/background.jpg")); 
			title = ImageIO.read(Board.class.getResource("/title.png"));
			play = ImageIO.read(Board.class.getResource("/play.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Timer timer = new Timer(1000 / 60, e -> repaint());
		timer.start();
		this.tetris = tetris;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(leftClick)
			tetris.startTetris();
		g.drawImage(background, 0, 0, null);
		g.drawImage(intro, Tetris.WIDTH / 2 - intro.getWidth() / 2,
				Tetris.HEIGHT / 2 - intro.getHeight() / 2 - 200, null);
		g.drawImage(title, Tetris.WIDTH / 2 - title.getWidth() / 2,
				Tetris.HEIGHT / 2 - title.getHeight() / 2 - 10, null);
		g.drawImage(play, Tetris.WIDTH / 2 - play.getWidth() / 2,
				Tetris.HEIGHT / 2 - play.getHeight() / 2 + 120, null);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftClick = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftClick = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}