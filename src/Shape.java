import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class describing the shapes of Tetris
 * @author Dmitriy Stepanov
 */
public class Shape {
	private final int color;
	private int x, y;
	private long time, lastTime;
	private final int normal = 600;
	private final int fast = 50;
	private int delay;
	private final BufferedImage block;
	private int[][] coords;
	private final int[][] reference;
	private int deltaX;
	private final Board board;
	private boolean collision = false;
	private boolean moveX = false;
	private int timePassedFromCollision = -1;
	private long deltaTime;

	/**
	 * Constructor - creating a new shape
	 * @param coords - coordinates shape
	 * @param block - image with colors
	 * @param board - game field
	 * @param color - color shape
	 * @see Shape#Shape(int[][],BufferedImage,Board,int)
	 */
	public Shape(int[][] coords, BufferedImage block, Board board, int color){
		this.coords = coords;
		this.block = block;
		this.board = board;
		this.color = color;
		deltaX = 0;
		x = 4;
		y = 0;
		delay = normal;
		time = 0;
		lastTime = System.currentTimeMillis();
		reference = new int[coords.length][coords[0].length];
		System.arraycopy(coords, 0, reference, 0, coords.length);
	}
	
	public void update(){
		moveX = true;
		deltaTime = System.currentTimeMillis() - lastTime;
		time += deltaTime;
        lastTime = System.currentTimeMillis();
		if(collision && timePassedFromCollision > 500) {
			for(int row = 0; row < coords.length; row ++) {
				for(int col = 0; col < coords[0].length; col ++) {
					if(coords[row][col] != 0)
						board.getBoard()[y + row][x + col] = color;
				}
			}
			checkLine();
			board.addScore();
			board.setCurrentShape();
			timePassedFromCollision = -1;
		}
		if(!(x + deltaX + coords[0].length > 10) && !(x + deltaX < 0)) {
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[row].length; col ++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[y + row][x + deltaX + col] != 0) {
							moveX = false;
						}
					}
				}
			}

			if(moveX)
				x += deltaX;
		}
		if (timePassedFromCollision == -1) {
			if(!(y + 1 + coords.length > 20)) {
				for(int row = 0; row < coords.length; row++) {
					for(int col = 0; col < coords[row].length; col ++) {
						if(coords[row][col] != 0) {
							if(board.getBoard()[y + 1 + row][x +  col] != 0) {
								collision();
							}
						}
					}
				}
				if(time > delay) {
					y++;
					time = 0;
				}
			} else {
				collision();
			}
		} else {
            timePassedFromCollision += deltaTime;
        }
		deltaX = 0;
	}

	private void collision() {
        collision = true;
        timePassedFromCollision = 0;
    }
	
	public void render(Graphics g){
		for(int row = 0; row < coords.length; row ++) {
			for(int col = 0; col < coords[0].length; col ++) {
				if(coords[row][col] != 0) {
					g.drawImage(block, col * 30 + x * 30, row * 30 + y * 30, null);
				}
			}		
		}
		for(int row = 0; row < reference.length; row ++) {
			for(int col = 0; col < reference[0].length; col ++) {
				if(reference[row][col] != 0) {
					g.drawImage(block, col * 30 + 360, row * 30 + 160, null);
				}
			}
		}
	}
	
	private void checkLine(){
		int size = board.getBoard().length - 1;
		for(int i = board.getBoard().length - 1; i > 0; i--) {
			int count = 0;
			for(int j = 0; j < board.getBoard()[0].length; j++) {
				if(board.getBoard()[i][j] != 0) {
					count++;
				}
				board.getBoard()[size][j] = board.getBoard()[i][j];
			}
			if(count < board.getBoard()[0].length) {
				size --;
			}
		}
	}
	
	public void rotateShape() {
		int[][] rotatedShape;
		rotatedShape = transposeMatrix(coords);
		rotatedShape = reverseRows(rotatedShape);
		if((x + rotatedShape[0].length > 10) || (y + rotatedShape.length > 20)) {
			return;
		}
		for(int row = 0; row < rotatedShape.length; row++) {
			for(int col = 0; col < rotatedShape[row].length; col ++) {
				if(rotatedShape[row][col] != 0) {
					if(board.getBoard()[y + row][x + col] != 0) {
						return;
					}
				}
			}
		}
		coords = rotatedShape;
	}

    private int[][] transposeMatrix(int[][] matrix){
        int[][] temp = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++){
                temp[j][i] = matrix[i][j];
            }
        }
        return temp;
    }

	private int[][] reverseRows(int[][] matrix){
		int middle = matrix.length/2;
		for(int i = 0; i < middle; i++) {
			int[] temp = matrix[i];
			matrix[i] = matrix[matrix.length - i - 1];
			matrix[matrix.length - i - 1] = temp;
		}
		return matrix;
	}

	public int getColor(){
		return color;
	}
	public void setDeltaX(int deltaX){
		this.deltaX = deltaX;
	}
	public void speedUp(){ 
		delay = fast; 
	}
	public void speedDown(){
		delay = normal;
	}
	public BufferedImage getBlock(){
		return block;
	}
	public int[][] getCoords(){
		return coords;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
}