import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SnakeLadderGame extends JFrame {

    private final BoardPanel board = new BoardPanel();
    private final JLabel status = new JLabel(
        "Player 1's turn. Roll the dice!"
    );
    private final JLabel diceLabel = new JLabel("Dice: -");
    private final JButton rollButton = new JButton("Roll Dice");
    private final Random random = new Random();

    private int player1 = 1;
    private int player2 = 1;
    private int currentPlayer = 1;
    private boolean gameOver = false;

    // Snakes: head -> tail
    private final int[][] snakes = {
        {99, 54}, {70, 55}, {52, 42},
        {25, 2}, {95, 72}, {64, 36}
    };

    // Ladders: bottom -> top
    private final int[][] ladders = {
        {6, 25}, {11, 40}, {17, 69},
        {46, 90}, {60, 85}, {63, 81}
    };

    public SnakeLadderGame() {
        setTitle("Snake and Ladder Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        status.setFont(new Font("Arial", Font.BOLD, 17));
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        add(status, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout());

        diceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rollButton.setFont(new Font("Arial", Font.BOLD, 16));

        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 15));

        controls.add(diceLabel);
        controls.add(rollButton);
        controls.add(restartButton);

        add(controls, BorderLayout.SOUTH);

        rollButton.addActionListener(e -> rollDice());
        restartButton.addActionListener(e -> restartGame());

        setSize(800, 850);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void rollDice() {
        if (gameOver) {
            return;
        }

        int dice = random.nextInt(6) + 1;
        diceLabel.setText("Dice: " + dice);

        int position = currentPlayer == 1 ? player1 : player2;
        int nextPosition = position + dice;

        if (nextPosition > 100) {
            status.setText(
                "Player " + currentPlayer +
                " needs an exact roll to reach 100!"
            );
            switchPlayer();
            return;
        }

        position = nextPosition;
        String message = "Player " + currentPlayer +
                         " rolled " + dice + ".";

        // Check ladders
        for (int[] ladder : ladders) {
            if (position == ladder[0]) {
                position = ladder[1];
                message += " Climbed a ladder to " + position + "!";
                break;
            }
        }

        // Check snakes
        for (int[] snake : snakes) {
            if (position == snake[0]) {
                position = snake[1];
                message += " Bitten by a snake! Back to "
                           + position + "!";
                break;
            }
        }

        if (currentPlayer == 1) {
            player1 = position;
        } else {
            player2 = position;
        }

        board.repaint();

        if (position == 100) {
            status.setText(message + " Player " +
                           currentPlayer + " wins!");
            gameOver = true;
            rollButton.setEnabled(false);
            JOptionPane.showMessageDialog(
                this, "Congratulations! Player " +
                      currentPlayer + " wins!"
            );
            return;
        }

        status.setText(message);
        switchPlayer();
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;

        if (!gameOver) {
            status.setText(
                status.getText() + " Player " +
                currentPlayer + "'s turn."
            );
        }
    }

    private void restartGame() {
        player1 = 1;
        player2 = 1;
        currentPlayer = 1;
        gameOver = false;

        diceLabel.setText("Dice: -");
        status.setText("Player 1's turn. Roll the dice!");
        rollButton.setEnabled(true);

        board.repaint();
    }

    private class BoardPanel extends JPanel {

        private final Color[] colors = {
            new Color(255, 245, 180),
            new Color(200, 235, 200)
        };

        BoardPanel() {
            setPreferredSize(new Dimension(700, 700));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int size = Math.min(getWidth(), getHeight()) / 10;
            int offsetX = (getWidth() - size * 10) / 2;
            int offsetY = (getHeight() - size * 10) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(1));

            for (int number = 1; number <= 100; number++) {
                int rowFromBottom = (number - 1) / 10;
                int colInRow = (number - 1) % 10;

                int col = rowFromBottom % 2 == 0
                    ? colInRow : 9 - colInRow;

                int row = 9 - rowFromBottom;

                int x = offsetX + col * size;
                int y = offsetY + row * size;

                g2.setColor(colors[(row + col) % 2]);
                g2.fillRect(x, y, size, size);

                g2.setColor(Color.GRAY);
                g2.drawRect(x, y, size, size);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.drawString(
                    String.valueOf(number),
                    x + 4, y + 13
                );
            }

            // Draw ladders
            for (int[] ladder : ladders) {
                Point a = getCenter(
                    ladder[0], size, offsetX, offsetY
                );
                Point b = getCenter(
                    ladder[1], size, offsetX, offsetY
                );

                g2.setColor(new Color(0, 130, 0));
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(a.x - 5, a.y, b.x - 5, b.y);
                g2.drawLine(a.x + 5, a.y, b.x + 5, b.y);
                g2.setStroke(new BasicStroke(1));
            }

            // Draw snakes
            for (int[] snake : snakes) {
                Point a = getCenter(
                    snake[0], size, offsetX, offsetY
                );
                Point b = getCenter(
                    snake[1], size, offsetX, offsetY
                );

                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(a.x, a.y, b.x, b.y);
                g2.setStroke(new BasicStroke(1));

                g2.setColor(Color.RED);
                g2.fillOval(a.x - 5, a.y - 5, 10, 10);
            }

            // Draw players
            drawPlayer(
                g2, player1, size, offsetX, offsetY,
                Color.BLUE, -7
            );
            drawPlayer(
                g2, player2, size, offsetX, offsetY,
                Color.MAGENTA, 7
            );

            g2.dispose();
        }

        private Point getCenter(
            int number, int size, int offsetX, int offsetY
        ) {
            int rowFromBottom = (number - 1) / 10;
            int colInRow = (number - 1) % 10;

            int col = rowFromBottom % 2 == 0
                ? colInRow : 9 - colInRow;

            int row = 9 - rowFromBottom;

            return new Point(
                offsetX + col * size + size / 2,
                offsetY + row * size + size / 2
            );
        }

        private void drawPlayer(
            Graphics2D g2, int position, int size,
            int offsetX, int offsetY, Color color, int shift
        ) {
            Point p = getCenter(
                position, size, offsetX, offsetY
            );

            g2.setColor(color);
            g2.fillOval(
                p.x - 9 + shift,
                p.y - 9,
                18, 18
            );

            g2.setColor(Color.BLACK);
            g2.drawOval(
                p.x - 9 + shift,
                p.y - 9,
                18, 18
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeLadderGame::new);
    }
}