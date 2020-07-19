import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Board} class represents the state of a board, it contains the
 * representation of the tiles in a 2D-matrix tiles, and n which is the side
 * length of the board.
 * It supports multiple operations to efficiently represent current state,
 * such as <em>hamming</em> and <em>manhattan</em> which represent the
 * score of a board, the goal board has a score of 0, the higher the score
 * the further away the state is from goal board.
 */
public class Board {
    private final int[][] tiles;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = tiles.clone();
        n = tiles[0].length;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    // for example: 3 2 1 4 5 6 8 7 0
    //              1 . 1 . . . 1 . 1
    // hamming distance = 4
    public int hamming() {
        int h = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                /*
                // tile (n, n) must always be 0
                if (i == n - 1 && j == n - 1 && tiles[i][j] != 0) {
                    h++;
                    break;
                }
                */
                if (tiles[i][j] == 0) continue;
                if (tiles[i][j] != (n * i) + j + 1) h++;
            }
        }
        return h;
    }

    // sum of Manhattan distances between tiles and goal
    // Manhattan distance is the sum of vertical and horizontal distances between
    // tiles out of place and their correct positions
    // for example: 3 2 1 4 5 6 8 7 0
    //              2 0 2 0 0 0 1 1 .
    // manhattan distance = 2 + 2 + 1 + 1 = 6
    public int manhattan() {
        int m = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) continue;
                if (tiles[i][j] != (n * i) + j + 1) {
                    int row = (int) Math.ceil((double) tiles[i][j] / n) - 1;
                    int col = (tiles[i][j] - 1) % n;
                    m += Math.abs(row - i) + Math.abs(col - j);
                }
            }
        }
        return m;
    }

    // is this board the goal board?
    public boolean isGoal() {
        // loop on all tiles, if a tile is out of place it returns false,
        // if loop is over it return true
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == n - 1 && j == n - 1 && tiles[i][j] == 0) break;
                if (tiles[i][j] != (n * i) + j + 1) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y == this) return true;
        if (y instanceof Board && ((Board) y).dimension() == this.n) {
            Board that = (Board) y;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (this.tiles[i][j] != that.tiles[i][j]) return false;
                }
            }
            return true;
        }
        return false;
    }

    // all neighboring boards
    // neighboring boards are boards obtained from a specific board when moving
    // tiles surrounding empty tile
    public Iterable<Board> neighbors() {

        int[][] tiles = this.tiles.clone();

        Iterable<Board> neighbors = () -> {
            // find zero tile coordinates
            int zi = 0, zj = 0;

            outerloop:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (tiles[i][j] == 0) {
                        zi = i;
                        zj = j;
                        break outerloop;
                    }
                }
            }

            List<Board> neighbors1 = new ArrayList<>();

            // check if empty tile has upper row
            if (zi - 1 >= 0) {
                // swap with upper row
                int[][] tilesCopy1 = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        tilesCopy1[i][j] = tiles[i][j];
                    }
                }
                tilesCopy1[zi - 1][zj] = tiles[zi][zj];
                tilesCopy1[zi][zj] = tiles[zi - 1][zj];
                neighbors1.add(new Board(tilesCopy1));
            }

            // check if empty tile has lower row
            if (zi + 1 < n) {
                // swap with lower row
                int[][] tilesCopy = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        tilesCopy[i][j] = tiles[i][j];
                    }
                }
                tilesCopy[zi + 1][zj] = tiles[zi][zj];
                tilesCopy[zi][zj] = tiles[zi + 1][zj];
                neighbors1.add(new Board(tilesCopy));
            }

            // check if empty tile has left column
            if (zj - 1 >= 0) {
                // swap with left column
                int[][] tilesCopy1 = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        tilesCopy1[i][j] = tiles[i][j];
                    }
                }
                tilesCopy1[zi][zj - 1] = tiles[zi][zj];
                tilesCopy1[zi][zj] = tiles[zi][zj - 1];
                neighbors1.add(new Board(tilesCopy1));
            }

            // check if empty tile has right column
            if (zj + 1 < n) {
                // swap with right column
                int[][] tilesCopy = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        tilesCopy[i][j] = tiles[i][j];
                    }
                }
                tilesCopy[zi][zj + 1] = tiles[zi][zj];
                tilesCopy[zi][zj] = tiles[zi][zj + 1];
                neighbors1.add(new Board(tilesCopy));
            }

            return neighbors1.iterator();
        };

        return neighbors;
    }

    public int[][] getTiles() {
        return tiles;
    }
}