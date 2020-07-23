import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/**
 * The {@code Solver} is a solver class to solve any solvable N-puzzle tiles game
 * using A* algorithm, with extra optimizations to improve efficiency.
 * It's used through calling <em>solve</em> method, which solves the initial
 * board passed to the constructor, if it's solvable then steps of the solution
 * is printed out to console, otherwise it's declared that it's not solvable.
 */
public class Solver {

    // steps of solution
    private Iterable<Board> solution;

    // solution node is saved to construct steps from it
    private SearchNode solNode = null;

    // initial board passed to constructor
    private Board initial;

    private boolean solvable = false;

    // node class used in A* search, it contains parent of current node, which
    // is used in optimizations, current board (state), moves to get to this
    // node, and score of node which is sum of manhattan distance and number 
    // of moves
    private class SearchNode implements Comparable <SearchNode> {
        SearchNode parent;
        Board state;
        int moves;
        Integer score;

        public SearchNode(Board state, SearchNode parent, int moves) {
            this.state = state;
            this.parent = parent;
            this.moves = moves;
            this.score = state.manhattan() + moves;
        }

        // comparator that compares nodes with respect to score
        public int compareTo(SearchNode o) {
            return this.score.compareTo(o.score);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        this.initial = initial;
        isSolvable();

        // A* algorithm: regular BFS is done on nodes, except that nodes are
        // kept in a MinPQ, which always gives priority to nodes with smaller
        // score (nearest to goal board)
        if (solvable) {
            MinPQ<SearchNode> pq = new MinPQ<>();
            // inserting initial board
            pq.insert(new SearchNode(initial, null, 0));

            while (!pq.isEmpty()) {
                // node with minimum score is removed from priority queue
                SearchNode curr = pq.delMin();

                // break if reached goal
                if(curr.state.isGoal()) {
                    solNode = curr;
                    break;
                } else {
                    // add all neighbors to priority queue
                    for (Board neighbor : curr.state.neighbors()) {
                        // critical optimization: an infinite loop can be
                        // created if neighbor's state is the same as parent's
                        // state by moving same tile back to its original position.
                        if (curr.parent != null && neighbor.equals(curr.parent.state)) continue;

                        // insert in pq with moves = current moves + 1
                        pq.insert(new SearchNode(neighbor, curr, curr.moves + 1));
                    }
                }
            }
            this.solution = solution();
        }
    }

    // is the initial board solvable?
    // TODO: improve complexity
    public void isSolvable() {
        int n = initial.dimension();
        int blankRow = 0;
        int[] tiles = new int[n * n - 1];
        int addptr = 0;
        int inversions = 0;

        // convert 2D-matrix into 1D-matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (initial.getTiles()[i][j] == 0) {
                    blankRow = i + 1;
                    continue;
                }
                tiles[addptr++] = initial.getTiles()[i][j];
            }
        }

        // inversion is the number of tiles less than current tile coming
        // after its position
        for (int i = 0; i < n * n - 1; i++) {
                for (int j = i + 1; j < n * n - 1; j++) {
                    if (tiles[j] < tiles[i]) inversions++;
            }
        }

        /*
        In general, for a given grid of width N, we can find out check if a
        N*N – 1 puzzle is solvable or not by following below simple rules :
            • If N is odd, then puzzle instance is solvable if number of
            inversions is even in the input state.
            • If N is even, puzzle instance is solvable if:
                - the blank is on an even row counting from the bottom and
                 number of inversions is odd.
                - the blank is on an odd row counting from the bottom
                 and number of inversions is even.
            • For all other cases, the puzzle instance is not solvable.

        src: https://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
         */
        if (n % 2 == 1 && inversions % 2 == 0) solvable = true;
        else if (n % 2 == 0) {
            if ((n - blankRow + 1) % 2 == 0 && inversions % 2 == 1) solvable = true;
            else if ((n - blankRow + 1) % 2 == 1 && inversions % 2 == 0) solvable = true;
        } else {
            solvable = false;
        }
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (solNode == null) {
            return -1;
        }
        return solNode.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solNode == null) return null;
        Iterable<Board> sol = () -> {
            List<Board> s = new ArrayList<>();
            SearchNode curr = solNode;
            while (curr != null) {
                s.add(0, curr.state);
                curr = curr.parent;
            }
            return s.iterator();
        };
        return sol;
    }

    public void solve() {
        if (solvable) {
            System.out.println("Solving state: Solvable\n");
            for(Board board : this.solution) {
                System.out.println(board.toString());
            }
            System.out.println("Number of moves: " + solNode.moves);
        } else {
            System.out.println("Solving State: Unsolvable.\n");
        }
    }

    // driver code
    public static void main(String[] args) throws IOException {
        
        // read data from text file provided in arguments
        // note that file must be put in project folder
        if (args.length > 0) {
            File input = new File(System.getProperty("user.dir") + args[0]);
            Scanner in = new Scanner(input);
            int n = in.nextInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.nextInt();
                }
            }

            Board initial = new Board(tiles);
            Solver solver = new Solver(initial);
            solver.solve();
        }
        else throw new IllegalArgumentException("Not enough arguments");
    }
}