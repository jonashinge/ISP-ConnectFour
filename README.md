ISP-ConnectFour
===============

Intelligent Systems Programming, Spring 2013 - Connect Four (Assignment 1)

Usage: java ShowGame GameLogic1 GameLogic2 [cols rows]
GameLogic{1,2} may be:
	human		 - Indicates a human will be playing.
	GameLogic	 - Specifies a GameLogic class extending IGameLogic to use as a competitor.
	cols/rows	 - Must be integers greater than 0 and default to 7 and 6 respectively.