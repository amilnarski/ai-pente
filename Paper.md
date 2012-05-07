# Pente
### Aaron Miller & Gabe Weintraub
#### http://github.com/amilnarski/ai-pente

## Description of Pente

Pente, named for the Greek word for the number five, is a turn-based strategy board game created by Gary Gabrel in 1977. The game is derived from the Japanese game Ninuki-Renju, which itself is a variant of the games Renju and Gomoku. The games are all played on the same board as Go, a 19x19 grid. Players take turns placing black and white stones on the open intersections of the grid. Pente can be played with up to four players, each using different colored stones, and occasionaly in a two-versus-two team format, but traditionally it is a two player game.
Players can win in two fashions, either by connecting five or more stones in a row, or by capturing five or more pairs of an opponent’s stones. The connected stones can be arranged vertically, horizontally or diagonally. Similarly, a player makes a capture by surrounding a pair of an opponent’s stones vertically, horizontally or diagonally. The captured stones are removed from the board and the exposed intersections are considered free again. A capture cannot be caused by moving stones into a surrounded position. Both modes of winning are considered equally valid and the game ends immediately when either is achieved. In a game with more than two players, a capture can contain stones from separate opponents. Because this typically increases the frequency of captures, most rules increase the winning number of captures from five to seven.
The first player begins the game by placing a stone on the center intersection of the board. Under standard rules, either player can then place stones on any open intersection on the board. An advanced player plays with a considerable advantage by moving first, so under tournament rules the starting player’s second move is required to be three or more intersections away from the center of the board in order to neutralize the advantage. All other standard rules apply.

## Summary of Existing Work

### Game Tree Search

Pente has been the subject of a surprising amount of AI work. The earliest mention discovered was a 1986 article in _Nibble_ where a computer player used a weighted table to determine its next move [1]. The table itself was non-static, and the computer could update its table based on the moves that its opponent made in response. This update between observed results and a prediction is reminiscent of a primitive reinforcement learner. More recent work has been done which implements more advanced players in various languages. An analysis of players suggests that Negascout and AlphaBeta provide the best results out of the players that were implemented (others include SSS-2, MTD-f, and AlphaBeta with transposition table) [2]. 
Pente itself is not a difficult game, and a relatively shallow depth of 4 is reported to be sufficient for the computer player to beat a human [1]. This proves promising for the results of the AlphaBeta player, as Pente has a large branching factor, due to the 361 positions that may be played. The relatively shallow branching factor should mean that a successful AlphaBeta is possible without many more optimizations than those afforded by pruning. In determining the depth of AlphaBeta's search should ideally be at least 10, so that a player is able to see a terminal result at the bottom of the search tree, and is not forced to rely on an evaluation function for all nodes on the frontier.
Pente is assumed to be biased in favor of the first player. There are claims that, assuming perfect play, the first player is guaranteed to win, but this has not been proven by any formal study. It has been proven, however, in the case of Gomoku [4] and Renju [5].

### Reinforcement Learning

Existing work has also been done with Pente and Reinforcement Learning. A thesis from Universitiet Utrecht found that a learning rate of 0.001 was most effective for a neural network trained to play Pente [3]. This value was adopted for the learning rate of the Q Learning player developed for this project. The thesis states that no research has been performed on Pente and temporal difference learning. General knowledge of reinforcement learning suggests that a self-taught reinforcement learner could become an effective opponent, especially if the learning rate is dampened over time to promote convergence. The successful neural network played well after 10,000 games, with error an order of magnitude less than other trials with different learning rates. We hope that a learning rate of 0.001 will produce good results with fewer runs than a smaller learning rate. 

### Multi-Player Pente

Although standard Pente is played with only two players, the game can be expanded to include up to four. Multi-player games are well suited for a learning-based approach [6].


## Experimental Results

### Pente

Our Pente implementation played standard Pente rules for the majority of the the project. In the interest of making further headway on the AI players capturing an opponent's pieces was scrapped. This greatly simplified the game's logic for undoing moves, and eliminated a bug which was returning incorrect feature values.
Pente is evaluated using 8 features:
1. Number of Black pieces
2. Number of White pieces
3. Number of Black doubles
4. Number of White doubles
5. Number of Black triples
6. Number of White triples
7. Number of Black quadruples
8. Number of White quadruples
These 8 features are multiplied by the following weight vector to generate the value of the game: {0.1, -0.1, 0.2, -0.2, 0.3, -0.3, 0.4, -0.4}. The Black player plays with the goal of maximizing the value of the game, and the White player tries to minimize the value of the game.

### The AlphaBeta Player
The AlphaBeta players uses a design similar to that presented in class. As stated above, AlphaBeta has been demonstrated to play reasonably well with a depth limit of 4. Our game has been able to reach depths of 22, which means that it considers O(360^22) states before selecting a best move, but the current implementation of AlphaBeta has slowed down considerably. The moves returned by the new moves returned by AlphaBeta are more correct, so the reduced speed is acceptable.
The AlphaBeta player currently keeps track of its best move at the shallowest depth. This should return the highest valued move that is one ply away from the current game state. Our AlphaBeta player could be improved significantly by conspiring only those moves that are within five spaces of another piece. With this design change, AlphaBeta still will only consider moves which will change the value of the features. All moves outside of this area have the same value as a move inside it, so they do not need to be considered. This change would significantly reduce the branching factor for AlphaBeta, especially in the early plus when there is a large number of possible moves in the player’s current design.

### The Q Learner Player
The Reinforcement Player uses Q Learning to update its policy and find its best move. The Q Learner uses the same set of features, but its weights are initialized to 0.5 for its own features, and -0.5 for its opponent's features. This difference should help the Q Learner choose moves that favor it's features and minimize those of its opponent.
The Q Learner could benefit from the reduction in the number of moves like AlphaBeta, but the benefit is not as pronounced during play. Instead of leading to a quality of moves in-game, it should allow the Q Learner to play it’s games more quickly, reducing the total training time the player would need.

### Results

As neither player is complete enough to test in a significant way, this section will be somewhat anemic. The AlphaBeta player does not play well enough to beat a human player, even when run at depths greater than 3. This is most likely due to a bug in the AlphaBeta code. The Q Learning player does have weights which update successfully, but it will return Null move objects if more than ~20 games are played. This has prevented any serious testing of the Q Learner. Until that point, weights do change based on the result of the game. This suggests that if the Null-move bug could be resolved, we may have a successful Reinforcement Learner. See the end of this paper with output for both the AlphaBeta Player and the Q Learning Player.

## Conclusion

Pente remains a very good choice for implementing Artificial Intelligence players. The challenge stems mostly from the difficulty of implementing the game correctly, and the most difficult aspect of game implementation Is undoing state. In retrospect, I would like to rewrite our game implementation before continuing more work on the AI Players. Creating a solid foundation is key to the success of this project; captures and bugs in determining the value of the game were not speeding the development of the AI and complicated debugging. 
Another way to approach this project successfully would be to develop the players In a simpler game like Tic Tac Toe. Once the players work on that small game, develop the larger game and then apply the players. This strategy would demonstrate that the principles of AI are understood and appropriately de-emphasize the importance of the game implementation.


## References

[1] Farmer, Eric. _Pente from the Apple // to today_. http://possiblywrong.wordpress.com/2010/05/31/pente-from-the-apple-to-today/.
[2] Kron et al. _Pente_. pages.cs.wisc.edu/~mjr/Pente/Pente.ppt.
[3] Muijrers, Valentijen. _Training a Back-Propagation Network with Temporal Difference Learning and a database for the board game Pente_. http://igitur-archive.library.uu.nl/student-theses/2011-0502-200628/BachelorScriptie%20(5).pdf.
[4] Allis et al. _Go-Moku and Threat-Space Search_. Report CS 93-02, Department of Computer Science, University of Limburg, Maastricht, The Netherlands. (1993).
[5] Wagner et al. "Solving Renju." _ICGA Journal_, 24(1):30–34. (2001)

## Appendix
### AlphaBeta Run with Depth 3
-----------------------------------------------------------------------------------
	 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18

0	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
1	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
2	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
3	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
4	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
5	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
6	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
7	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
8	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
9	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
10	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
11	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
12	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
13	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
14	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
15	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
16	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
17	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
18	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
-----------------------------------------------------------------------------------

Best move is now: BoardCoords: (4, 0)	GameCoords: (5, 1)
Returning move: BoardCoords: (4, 0)	GameCoords: (5, 1)
-----------------------------------------------------------------------------------
	 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18

0	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
1	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
2	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
3	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
4	[B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
5	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
6	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
7	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
8	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
9	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
10	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
11	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
12	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
13	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
14	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
15	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
16	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
17	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
18	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
-----------------------------------------------------------------------------------

Turns: 2
Turn: WHITE
Over: false
Winner: null
Feature 0: 1.0
Feature 1: 1.0
Feature 2: 0.0
Feature 3: 0.0
Feature 4: 0.0
Feature 5: 0.0
Feature 6: 0.0
Feature 7: 0.0
Capture: false
	----UNDO DATA----
Move: BoardCoords: (4, 0)	GameCoords: (5, 1)

Best move is now: BoardCoords: (8, 0)	GameCoords: (9, 1)
Returning move: BoardCoords: (8, 0)	GameCoords: (9, 1)
-----------------------------------------------------------------------------------
	 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18

0	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
1	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
2	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
3	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
4	[B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
5	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
6	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
7	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
8	[W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
9	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
10	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
11	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
12	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
13	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
14	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
15	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
16	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
17	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
18	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
-----------------------------------------------------------------------------------

Turns: 3
Turn: BLACK
Over: false
Winner: null
Feature 0: 1.0
Feature 1: 2.0
Feature 2: 0.0
Feature 3: 0.0
Feature 4: 0.0
Feature 5: 0.0
Feature 6: 0.0
Feature 7: 0.0
Capture: false
	----UNDO DATA----
Move: BoardCoords: (8, 0)	GameCoords: (9, 1)

Best move is now: BoardCoords: (8, 9)	GameCoords: (9, 10)
Returning move: BoardCoords: (8, 9)	GameCoords: (9, 10)
-----------------------------------------------------------------------------------
	 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18

0	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
1	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
2	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
3	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
4	[B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
5	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
6	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
7	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
8	[W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [W] [B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
9	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
10	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
11	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
12	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
13	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
14	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
15	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
16	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
17	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
18	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
-----------------------------------------------------------------------------------

Turns: 4
Turn: WHITE
Over: false
Winner: null
Feature 0: 2.0
Feature 1: 2.0
Feature 2: 0.0
Feature 3: 0.0
Feature 4: 0.0
Feature 5: 0.0
Feature 6: 0.0
Feature 7: 0.0
Capture: false
	----UNDO DATA----
Move: BoardCoords: (8, 9)	GameCoords: (9, 10)

#### Truncated

Best move is now: BoardCoords: (13, 14)	GameCoords: (14, 15)
Returning move: BoardCoords: (13, 14)	GameCoords: (14, 15)
-----------------------------------------------------------------------------------
	 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18

0	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
1	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
2	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
3	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
4	[B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
5	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
6	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
7	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
8	[W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [W] [B] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
9	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
10	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
11	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
12	[W] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] 
13	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [B] [ ] [ ] [ ] [ ] 
14	[ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [B] [W] [B] [B] 
15	[W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [W] [B] [W] [B] 
16	[B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [W] [B] [W] 
17	[B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] 
18	[W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [B] [W] [ ] [ ] 
-----------------------------------------------------------------------------------

Turns: 84
Turn: WHITE
Over: true
Winner: BLACK
Feature 0: 42.0
Feature 1: 42.0
Feature 2: 19.0
Feature 3: 15.0
Feature 4: 0.0
Feature 5: 3.0
Feature 6: 2.0
Feature 7: 1.0
Capture: false
	----UNDO DATA----
Move: BoardCoords: (13, 14)	GameCoords: (14, 15)

### Sample Q Learner Run (1 Game)
Number of Moves: 360
Move Returned: BoardCoords: (0, 0)	GameCoords: (1, 1)
Number of Moves: 359
Move Returned: BoardCoords: (7, 7)	GameCoords: (8, 8)
1.0, 2.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 358
Move Returned: BoardCoords: (0, 1)	GameCoords: (1, 2)
2.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 357
Move Returned: BoardCoords: (7, 8)	GameCoords: (8, 9)
2.0, 3.0, 1.0, 3.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 356
Move Returned: BoardCoords: (1, 0)	GameCoords: (2, 1)
3.0, 3.0, 3.0, 3.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 355
Move Returned: BoardCoords: (8, 7)	GameCoords: (9, 8)
3.0, 4.0, 3.0, 6.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 354
exploring
Move Returned: BoardCoords: (8, 17)	GameCoords: (9, 18)
4.0, 4.0, 3.0, 6.0, 0.0, 0.0, 0.0, 0.0
Number of Moves: 353
Move Returned: BoardCoords: (6, 7)	GameCoords: (7, 8)
4.0, 5.0, 3.0, 6.0, 0.0, 1.0, 0.0, 0.0
Number of Moves: 352
Move Returned: BoardCoords: (1, 1)	GameCoords: (2, 2)
5.0, 5.0, 6.0, 6.0, 0.0, 1.0, 0.0, 0.0
Number of Moves: 351
Move Returned: BoardCoords: (6, 8)	GameCoords: (7, 9)
5.0, 6.0, 6.0, 7.0, 0.0, 2.0, 0.0, 0.0
Number of Moves: 350
Move Returned: BoardCoords: (0, 2)	GameCoords: (1, 3)
6.0, 6.0, 6.0, 7.0, 1.0, 2.0, 0.0, 0.0
Number of Moves: 349
Move Returned: BoardCoords: (7, 6)	GameCoords: (8, 7)
6.0, 7.0, 6.0, 8.0, 1.0, 3.0, 0.0, 0.0
Number of Moves: 348
Move Returned: BoardCoords: (1, 2)	GameCoords: (2, 3)
7.0, 7.0, 7.0, 8.0, 2.0, 3.0, 0.0, 0.0
Number of Moves: 347
Move Returned: BoardCoords: (7, 9)	GameCoords: (8, 10)
7.0, 8.0, 7.0, 10.0, 2.0, 2.0, 0.0, 1.0
Number of Moves: 346
Move Returned: BoardCoords: (2, 1)	GameCoords: (3, 2)
8.0, 8.0, 8.0, 10.0, 3.0, 2.0, 0.0, 1.0
Number of Moves: 345
Move Returned: BoardCoords: (6, 6)	GameCoords: (7, 7)
8.0, 9.0, 8.0, 9.0, 3.0, 4.0, 0.0, 1.0
Number of Moves: 344
Move Returned: BoardCoords: (2, 0)	GameCoords: (3, 1)
9.0, 9.0, 7.0, 9.0, 5.0, 4.0, 0.0, 1.0
Number of Moves: 343
exploring
Move Returned: BoardCoords: (6, 9)	GameCoords: (7, 10)
9.0, 10.0, 7.0, 9.0, 5.0, 4.0, 0.0, 2.0
Number of Moves: 342
Move Returned: BoardCoords: (7, 16)	GameCoords: (8, 17)
10.0, 10.0, 8.0, 9.0, 5.0, 4.0, 0.0, 2.0
Number of Moves: 341
Move Returned: BoardCoords: (7, 5)	GameCoords: (8, 6)
10.0, 11.0, 8.0, 10.0, 5.0, 4.0, 0.0, 2.0
1336400071838
One, 0.4266498929357205, -0.6001816755161493, 0.4331925902560383, -0.5981011299470623, 0.48905942684490944, -0.5242931916464063, 0.5, -0.5023195613818285
Two, -0.40936197653125017, 0.5906380234687498, -0.4008400238487503, 0.5852693614787499, -0.48110758994125, 0.5256389216449998, -0.5, 0.50262595791375
WEIGHTS
	Max: 0.4266498929357205	Min: -0.40936197653125017
	Max: -0.6001816755161493	Min: 0.5906380234687498
	Max: 0.4331925902560383	Min: -0.4008400238487503
	Max: -0.5981011299470623	Min: 0.5852693614787499
	Max: 0.48905942684490944	Min: -0.48110758994125
	Max: -0.5242931916464063	Min: 0.5256389216449998
	Max: 0.5	Min: -0.5
	Max: -0.5023195613818285	Min: 0.50262595791375
