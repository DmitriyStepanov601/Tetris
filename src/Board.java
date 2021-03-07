import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * A class that describes the game field
 * @author Dmitriy Stepanov
 */
public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
	private final BufferedImage blocks;
	private final BufferedImage background;
	private final BufferedImage pause;
	private final BufferedImage refresh;

	private final int boardHeight = 20;
	private final int boardWidth = 10;
	private final int blockSize = 30;

	private final int[][] board = new int[boardHeight][boardWidth];
	private final Shape[] shapes = new Shape[7];
	private Shape currentShape, nextShape;

	private final Timer looper;
	private int mouseX, mouseY;
	private boolean leftClick = false;

	private final Rectangle stopBounds;
	private final Rectangle refreshBounds;

	private boolean gamePaused = false;
	private boolean gameOver = false;

	private int score = 0;
	private final Random random = new Random();
	private final int bestRecord = 561;
	private int index;

	/**
	 * Constructor - creating a new game field
	 * @see Board#Board()
	 */
	public Board() {
		blocks = loadImage("/tiles.png");
		background = loadImage("/background.jpg");
		pause = loadImage("/pause.png");
		refresh = loadImage("/refresh.png");

		mouseX = 0;
		mouseY = 0;

		stopBounds = new Rectangle(350, 480, pause.getWidth(), pause.getHeight() + pause.getHeight() / 2);
		refreshBounds = new Rectangle(420, 480, refresh.getWidth(), refresh.getHeight() +
				refresh.getHeight() / 2);
		looper = new Timer(1000 / 60, e -> { update(); repaint(); });

		// I shape
		shapes[0] = new Shape(new int[][]{
				{1, 1, 1, 1} },
				blocks.getSubimage(0, 0, blockSize, blockSize), this, 1);

		// T shape
		shapes[1] = new Shape(new int[][]{
				{1, 1, 1}, {0, 1, 0} },
				blocks.getSubimage(blockSize, 0, blockSize, blockSize), this, 2);

		// L shape
		shapes[2] = new Shape(new int[][]{
				{1, 1, 1}, {1, 0, 0} },
				blocks.getSubimage(blockSize * 2, 0, blockSize, blockSize), this, 3);

		// J shape
		shapes[3] = new Shape(new int[][]{
				{1, 1, 1}, {0, 0, 1} },
				blocks.getSubimage(blockSize * 3, 0, blockSize, blockSize), this, 4);

		// S shape
		shapes[4] = new Shape(new int[][]{
				{0, 1, 1}, {1, 1, 0} },
				blocks.getSubimage(blockSize * 4, 0, blockSize, blockSize), this, 5);

		// Z shape
		shapes[5] = new Shape(new int[][]{
				{1, 1, 0}, {0, 1, 1} },
				blocks.getSubimage(blockSize * 5, 0, blockSize, blockSize), this, 6);

		// O shape;
		shapes[6] = new Shape(new int[][]{
				{1, 1}, {1, 1} },
				blocks.getSubimage(blockSize * 6, 0, blockSize, blockSize), this, 7);
	}

	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(Board.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	private final Timer buttonLapse = new Timer(300, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			buttonLapse.stop();
		}});
	
	private void update() {
		if(stopBounds.contains(mouseX, mouseY) && leftClick && !buttonLapse.isRunning() && !gameOver) {
			buttonLapse.start();
			gamePaused = !gamePaused;
		}
		if(refreshBounds.contains(mouseX, mouseY) && leftClick) {
			startGame();
		}
		if(gamePaused || gameOver) { return; }
		currentShape.update();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, null);
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col ++) {
				if(board[row][col] != 0) {
					g.drawImage(blocks.getSubimage((board[row][col] - 1) * blockSize,
							0, blockSize, blockSize), col * blockSize, row * blockSize, null);
				}
			}
		}

		for(int row = 0; row < nextShape.getCoords().length; row ++) {
			for(int col = 0; col < nextShape.getCoords()[0].length; col ++) {
				if(nextShape.getCoords()[row][col] != 0) {
					g.drawImage(nextShape.getBlock(), col * 30 + 360, row * 30 + 50, null);
				}
			}
		}

		currentShape.render(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(new Color(0, 0, 0, 100));

		for(int i = 0; i <= boardHeight; i++) {
			g2d.drawLine(0, i * blockSize, boardWidth * blockSize, i * blockSize);
		}

		for(int j = 0; j <= boardWidth; j++) {
			g2d.drawLine(j * blockSize, 0, j * blockSize, boardHeight * 30);
		}

		if(gamePaused) {
			String gamePausedString = "GAME PAUSED";
			g.setColor(new Color(135, 93, 170));
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString(gamePausedString, 35, Tetris.HEIGHT / 2);
		}

		if(gameOver) {
			String gameOverString = "GAME OVER";
			g.setColor(new Color(12, 12, 12));
			g.setFont(new Font("Arial", Font.BOLD, 32));
			g.drawString(gameOverString, 50, Tetris.HEIGHT / 2);

			if(bestRecord > score) {
				g.setColor(new Color(255, 0, 0));
				g.setFont(new Font("Arial", Font.BOLD, 20));
				g.drawString("Best Record", Tetris.WIDTH - 160, Tetris.HEIGHT / 2 + 80);
				g.drawString(" " + bestRecord + " ", Tetris.WIDTH - 120, Tetris.HEIGHT / 2 + 110);
			} else {
				g.setColor(new Color(255, 0, 0));
				g.setFont(new Font("Arial", Font.BOLD, 20));
				g.drawString("Best Record", Tetris.WIDTH - 160, Tetris.HEIGHT / 2 + 80);
				g.drawString(" " + score + " ", Tetris.WIDTH - 120, Tetris.HEIGHT / 2 + 110);
			}
		}

		if(stopBounds.contains(mouseX, mouseY))
			g.drawImage(pause.getScaledInstance(pause.getWidth() + 3, pause.getHeight() + 3,
					BufferedImage.SCALE_DEFAULT), stopBounds.x + 3, stopBounds.y + 3, null);
		else
			g.drawImage(pause, stopBounds.x, stopBounds.y, null);

		if(refreshBounds.contains(mouseX, mouseY))
			g.drawImage(refresh.getScaledInstance(refresh.getWidth() + 3, refresh.getHeight() + 3,
					BufferedImage.SCALE_DEFAULT), refreshBounds.x + 3, refreshBounds.y + 3, null);
		else
			g.drawImage(refresh, refreshBounds.x, refreshBounds.y, null);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("Score", Tetris.WIDTH - 130, Tetris.HEIGHT / 2);
		g.drawString(" " + score + " ", Tetris.WIDTH - 120, Tetris.HEIGHT / 2 + 30);
	}

	public int[][] getBoard(){
		return board;
	}

	public Shape setNextShape() {
		index = random.nextInt(shapes.length);
		return nextShape = new Shape(shapes[index].getCoords(), shapes[index].getBlock(), this,
				shapes[index].getColor());
	}

	public void setCurrentShape() {
		currentShape = nextShape;
		setNextShape();
		if(currentShape == nextShape) {
			setNextShape();
		}

		for(int row = 0; row < currentShape.getCoords().length; row ++) {
			for(int col = 0; col < currentShape.getCoords()[0].length; col ++) {
				if(currentShape.getCoords()[row][col] != 0) {
					if(board[currentShape.getY() + row][currentShape.getX() + col] != 0)
						gameOver = true;
				}
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP)
			currentShape.rotateShape();
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			currentShape.setDeltaX(1);
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			currentShape.setDeltaX(-1);
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			currentShape.speedUp();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			currentShape.speedDown();
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	
	public void startGame() {
		stopGame();
		setNextShape();
		setCurrentShape();
		gameOver = false;
		looper.start();
	}

	public void stopGame() {
		score = 0;
		for (int[] ints : board) {
			Arrays.fill(ints, 0);
		}
		looper.stop();
	}

	public void addScore(){
		score ++;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
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