An implementation of Tic Tac Toe in C#

This implementation involves the creation of a Tic Tac Toe board in a 2-D char array. A DisplayField method is called after each person makes a turn in order to show an updated version of the playing field. There is a large do-while loop which performs multiple operations. The loop starts by checking which player's turn it is and then writing a message to the console asking that player to enter their choice. Then, that choice will attempt to parse and if the parsing succeeds, a switch statement will occur. Within the switch statement, a separate method is called to determine if the space should be occupied by an X or an O. This method will assign an X or an O depending on the player at the time and if the space is available. After a choice is made and the space becomes occupied, there will be another call to a method, CheckWin(), which will check all possible combinations of winning. Finally, if this test does fail (i.e. there are no possible ways of winning), there will be another check to see if the counter variable is equal to 10. This variable was originally assigned to the value of before the do-while loop. After each iteration of the do-while loop, the value will count up by 1. A counter value of 10 means that all spaces have been occupied and the game has reached a draw. In the cases of the game drawing, the program will successfully terminate. In order to play again, run the program.