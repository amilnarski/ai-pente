# Pente
### Aaron Miller & Gabe Weintraub
## Description of Pente

Pente, named for the Greek word for the number five, is a turn-based strategy board game created by Gary Gabrel in 1977. The game is derived from the Japanese game Ninuki-Renju, which itself is a variant of the games Renju and Gomoku. The games are all played on the same board as Go, a 19x19 grid. Players take turns placing black and white stones on the open intersections of the grid. Pente can be played with up to four players, each using different colored stones, and occasionaly in a two-versus-two team format, but traditionally it is a two player game.
Players can win in two fashions, either by connecting five or more stones in a row, or by capturing five or more pairs of an opponent’s stones. The connected stones can be arranged vertically, horizontally or diagonally. Similarly, a player makes a capture by surrounding a pair of an opponent’s stones vertically, horizontally or diagonally. The captured stones are removed from the board and the exposed intersections are considered free again. A capture cannot be caused by moving stones into a surrounded position. Both modes of winning are considered equally valid and the game ends immediately when either is achieved. In a game with more than two players, a capture can contain stones from separate opponents. Because this typically increases the frequency of captures, most rules increase the winning number of captures from five to seven.
The first player begins the game by placing a stone on the center intersection of the board. Under standard rules, either player can then place stones on any open intersection on the board. An advanced player plays with a considerable advantage by moving first, so under tournament rules the starting player’s second move is required to be three or more intersections away from the center of the board in order to neutralize the advantage. All other standard rules apply.

## Summary of Existing Work

### Game Tree Search

Pente has been the subject of a surprising amount of AI work. The earliest mention discovered was a 1986 article in _Nibble_ where a computer player used a weighted table to determine its next move [1]. The table itself was non-static, and the computer could update its table based on the moves that its opponent made in response. This update between observed results and a prediction is reminiscent of a primitive reinforcement learner. More recent work has been done which implements more advanced players in various languages. An analysis of players suggests that Negascout and AlphaBeta provide the best results out of the players that were implemented (others include SSS-2, MTD-f, and AlphaBeta with transposition table) [2]. 
Pente itself is not a difficult game, and a relatively shallow depth of 4 is reported to be sufficient for the computer player to beat a human [1]. This proves promising for the results of the AlphaBeta player, as Pente has a large branching factor, due to the 361 positions that may be played. The relatively shallow branching factor should mean that a successful AlphaBeta is possible without many more optimizations than those afforded by pruning. In determining the depth of AlphaBeta's search should ideally be at least 10, so that a player is able to see a terminal result at the bottom of the search tree, and is not forced to rely on an evaluation function for all nodes on the frontier. 

### Reinforcement Learning

Existing work has also been done with Pente and Reinforcement Learning. A thesis from Universitiet Utrecht found that a learning rate of 0.001 was most effective for a neural network trained to play Pente [3]. This value was adopted for the learning rate of the Q Learning player developed for this project. The thesis states that no research has been performed on Pente and temporal difference learning. General knowledge of reinforcement learning suggests that a self-taught reinforcement learner could become an effective opponent, especially if the learning rate is dampened over time to promote convergence. The successful neural network played well after 10,000 games, with error an order of magnitude less than other trials with different learning rates. We hope that a learning rate of 0.001 will produce good results with fewer runs than a smaller learning rate. 

## Experimental Results
Remove captures.
### The AlphaBeta Player
The AlphaBeta Player is not as broken.
### The Q Learner Player
The Q Learner Player is also broken. 
### Results


## Conclusion

## References

[1] Farmer, Eric. _Pente from the Apple // to today_. http://possiblywrong.wordpress.com/2010/05/31/pente-from-the-apple-to-today/.
[2] Kron et al. _Pente_. pages.cs.wisc.edu/~mjr/Pente/Pente.ppt.
[3] Muijrers, Valentijen. _Training a Back-Propagation Network with Temporal Difference Learning and a database for the board game Pente_. http://igitur-archive.library.uu.nl/student-theses/2011-0502-200628/BachelorScriptie%20(5).pdf.
