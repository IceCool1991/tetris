
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victor
 */
public class Board extends JPanel implements ActionListener {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (canMoveTo(currentShape, currentRow, currentCol - 1)) {
                        currentCol--;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMoveTo(currentShape, currentRow, currentCol + 1)) {
                        currentCol++;
                    }
                    break;
                case KeyEvent.VK_UP:
                    Shape rotShape = currentShape.rotateRight();
                    if (canMoveTo(rotShape, currentRow, currentCol)) {
                        System.out.println("rotated");
                        currentShape = rotShape;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
                        currentRow++;
                    }

                    break;
                default:
                    break;

            }
            repaint();
        }
    }

    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;

    private Tetrominoes[][] matrix;
    private int deltaTime;

    private Shape currentShape;

    private int currentRow;
    private int currentCol;

    private Timer timer;

    MyKeyAdapter myKeyAdapter;

    public static final int INIT_ROW = -2;

    public Board() {
        super();
        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        initValues();
        timer = new Timer(deltaTime, this);
        myKeyAdapter = new MyKeyAdapter();
    }

    public void initValues() {
        setFocusable(true);
        cleanBoard();
        deltaTime = 500;
        currentShape = null;
        currentRow = INIT_ROW;
        currentCol = NUM_COLS / 2;
    }

    public void initGame() {
        initValues();
        currentShape = new Shape();
        addKeyListener(myKeyAdapter);
        timer.start();
    }

    public void cleanBoard() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = Tetrominoes.NoShape;
            }
        }
    }

    private boolean canMoveTo(Shape shape, int newRow, int newCol) {
        if ((newCol + shape.getXmin() < 0)
                || (newCol + shape.getXmax() >= NUM_COLS)
                || (newRow + shape.getYmax() >= NUM_ROWS) 
                || hitWithMatrix(shape, newRow, newCol)) {
            return false;
        }
        return true;
    }
    
    private boolean hitWithMatrix(Shape shape, int newRow, int newCol) {
        int[][] squaresArray = shape.getCoordnates();
        for (int point = 0; point <= 3; point++) {
            int row = newRow + squaresArray[point][1];
            int col = newCol + squaresArray[point][0];
            if (row >= 0) {
                if (matrix[row][col] != Tetrominoes.NoShape) {
                    return true;
                }
            }
        }
        return false;
    }
    
    

    // Game Main Loop
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
            currentRow++;
            repaint();
        } else {
            moveCurrentShapeToMatrix();
            currentShape = new Shape();
            currentRow = INIT_ROW;
            currentCol = NUM_COLS / 2;
        }

    }

    private void moveCurrentShapeToMatrix() {
        int[][] squaresArray = currentShape.getCoordnates();
        for (int point = 0; point <= 3; point++) {
            int row = currentRow + squaresArray[point][1];
            int col = currentCol + squaresArray[point][0];
            matrix[row][col] = currentShape.getShape();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (currentShape != null) {
            drawCurrentShape(g);
        }
        drawBorder(g);

    }

    public void drawBorder(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(0, 0, NUM_COLS * squareWidth(),
                NUM_ROWS * squareHeight());
    }

    public void drawBoard(Graphics g) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                drawSquare(g, row, col, matrix[row][col]);
            }
        }
    }

    private void drawCurrentShape(Graphics g) {
        int[][] squaresArray = currentShape.getCoordnates();
        for (int point = 0; point <= 3; point++) {
            drawSquare(g, currentRow + squaresArray[point][1],
                    currentCol + squaresArray[point][0], currentShape.getShape());
        }
    }

    private void drawSquare(Graphics g, int row, int col, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

}
