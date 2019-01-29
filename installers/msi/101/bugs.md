
+sound plays when scene changes even though it has been muted on a different scene
	solved:
		added a global variable to Main which checks if it is muted or not then plays the sound accordingly 0 or 1
/////////
adressed+sometimes when the game is finished and new game is clicked
program shuts down
solved:
	fixed Main line 36: string[] suffix had a wrong element present
////////
adressed+score sometimes displays negative numbers before being stable 
solved:
	fixed Game scorecalculator(): added if score<0 after the calculation.
at 0
///////
adressed+on game screen main menu button is not displayed
solved:
	changed game.fxml to display buttons at top next to labels
///////
adressed+set scene's not resizable
solved:
	set not resizable to prevent scene spawn location bug
///////
adressed+{clicking during folding results in multi folding animation

?during number of fast clicks ui does not register the real score(cannot keep up with changes)
	possible solution:
		disable clicking during animation.
}solved
	added a boolean variable to Control in order to prevent fast clicking and animation bugs
//////////
!!!!!!!!!!!!!!!!!!
adressed+MEMORY PROBLEM
	condition: 
		""Is your application a game? If you are using Node and its subclasses in the scene graph, then there isn't much you can do. If you don't rely on JavaFX controls as much, you could use GraphicsContext along with Canvas to reduce the effect on memory, as well as gain performance.""
!!!!!!!!!!!!!!!!!!
planned features:
highscores 
proximity indicator
