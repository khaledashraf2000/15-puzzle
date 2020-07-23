# 15-Puzzle Solver

The 15-puzzle is a sliding puzzle that consists of a frame of numbered square tiles in random order with one tile missing.The goal of the puzzle is to place the tiles in order by making sliding moves that use the empty space.

![source: Wikipedia](https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/15-puzzle_magical.svg/1200px-15-puzzle_magical.svg.png)

I used A* search algorithm to implement the solving method. I also used manhattan heruistic function to guide the A* algorithm. I also used `algs4.cs.princeton.edu.MinPQ` class to use a minimum priority queue for the implelemntation of the A* algorithm.

### How to use:
* `cd` to your cloned project directory
* After compiling the `.java` files, type `java Solver YOUR_INPUT_FILE_NAME.txt` into the cmd
* The output would be displayed on the console