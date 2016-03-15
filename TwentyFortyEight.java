// This program is a recreation of the popular 2048 game, by implementing usage of JavaFX
// DATE: 8/14/15
// Author: Cameron Dobos
// 2048 GUI Project
/* 2048 is a single-player puzzle game created in March 2014 by 19-year-old Italian web developer Gabriele Cirulli.
 * The objective is to slide numbered tiles on a grid to combine them and create a tile with the number 2048.
 * You use the directional arrow keys to shift all of the tiles in the specified direction to merge them, if they are the same number.
 */
 
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class TwentyFortyEight extends Application
{
	int[][][] GridValues = new int[11][4][4];	// Initialize 4x4 grid array(s) - allows for up to 10 undo moves
	int[][] sfx = new int[4][4];				// Special effects flags (sfx==1): new tile; (sfx==2): tile combined
	int[] score = new int[11];					// Score array for keeping track of up to 10 undo moves
	int hiscore = 0 , moves = 0;				// Move counter
	int i = 0 , col = 0 , row = 0;				// Variables used for incrementing arrays and testing integer values
	int xplay = 0;								// Flags (xplay=1): player wants to extend play
	boolean z;									// Dummy variable for misc. use

	// Define all of the main graphic entities being used
	GridPane grdBase = new GridPane();		// Grid pane where all game tile movement and animation lives
	StackPane spMain = new StackPane();		// Invisible stack (for stacking grid panes, rectangles, labels, etc.)
	BorderPane bpInner = new BorderPane();	// Inner pane (top title bar of scene added to main border pane)
	BorderPane bpMain = new BorderPane();	// Main border pane (contains bpInner and spMain, loaded into scene scMain)
	Scene scMain= new Scene(bpMain);		// The scene, which is the game window

 	public static void main(String[] args) 
 	{	
		launch(args);	// Executes the start routine, which sets the Main stage
	}

 	@Override public void start(Stage primaryStage) // Creates Stage (overrides start method in the application class)
	{ 
 		// This routine generates the 2048 display (a SCENE at the end of the routine) to include the following items:
 		//   Border Pane (bpMain) - includes Help
 		//   Border Pane (bpInner) - positioned at top of bpMain, includes Title, Score, and Move counter
 		//   Grid Pane (grdBase) - this is the backdrop made up of a 4 x 4 grid of rectangles which is the 2048 play board
 		//   Stack Pane (spMain) - the stack pane on top of grdBase allows additional rectangles to be stacked later,
 		//							which are the moving 2048 tiles.
		
 		// ** bpMain ** - border pane located in scMain scene at the end of this routine

		// Add buttons to bpMain
		Button btSave = new Button("Save (Alt+S)");
		btSave.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		btSave.setOnAction(new EventHandler<ActionEvent>() 
			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		save_game();			// Calls method to save the game and output it to a .dat file
		    	}
			});
		
		Button btLoad = new Button("Load (Alt+L)");
		btLoad.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		btLoad.setOnAction(new EventHandler<ActionEvent>() 
			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		load_game();			// Calls load method to scan, recreate and output values and tiles
		    	}
			});
		
		Button btUndo = new Button("Undo (Ctrl+Z)");
		btUndo.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		btUndo.setOnAction(new EventHandler<ActionEvent>() 
			{
				@Override public void handle(ActionEvent event) 
				{
					undo_move();			// Calls method to undo move
				}
			});
		
		Button btHelp = new Button("Help? (Alt+H)");
		btHelp.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		btHelp.setOnAction(new EventHandler<ActionEvent>() 
			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		display_help();			// Calls method to display help window with information
		    	}
			});
		
		Button btExit = new Button("Exit (Alt+X)");
		btExit.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		btExit.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override public void handle(ActionEvent event) 
			{
				// Calls method which will first determine if a high score was achieved (then displaying high score window), and then shows the 'Game Over' window
				// If high score was not achieved, then goes straight to 'game over' window
				displayHScore();			
			}
		});

		HBox bxButtons = new HBox(30);					// Add a help box at the bottom center of bpMain
		bxButtons.setAlignment(Pos.CENTER); 			// SETS Position to Center
		bxButtons.getChildren().addAll(btSave,btLoad,btUndo,btHelp,btExit); 
		bpMain.setBottom(bxButtons);
	
		// ** bpInner **  - border pane located at top of bpMain
		bpMain.setTop(bpInner);

 		Label name = new Label("2048");							// Title added to bpIbnner
		name.setFont(Font.font("Impact", FontWeight.BOLD,36));	// Set the font of the "2048" title text
		bpInner.setLeft(name);									// Left justify the title
		
		// Retrieve High Score 
		BufferedReader loadhscore;
		try
		{
			loadhscore = new BufferedReader(new FileReader("HighScore.dat"));
			String gHScore = null;
			try
			{
				gHScore = loadhscore.readLine();					// Starts reading off of file
				int val = Integer.parseInt(gHScore.substring(0,5));	// Converts integer values to a String for scanning
				// 10,000 was added to stored number as a formatting tool for ease of recovering the data here
				hiscore = val - 10000;
			}
			catch (IOException e)						// Auto-generated catch block
				{ e.printStackTrace(); }
			try
			{
				loadhscore.close();						// Closes highscore window
			}
			catch (IOException e)						// Auto-generated catch block
				{ e.printStackTrace(); }
		}
		catch (FileNotFoundException e)					// Auto-generated catch block
			{ e.printStackTrace(); }
			
		HBox hBox = new HBox(64);								// Display box added to bpInner with items spaced 64 pixels apart
		Label lbHScore = new Label("High Score: \n" + hiscore);	// Display the high score
		lbHScore.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		Label lbScore = new Label("Score:      \n" + score[0]);	// Display the score
		lbScore.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		Label lbMoves = new Label("Move Count: \n" + moves);	// Display the number of moves
		lbMoves.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		hBox.getChildren().addAll(lbHScore, lbScore, lbMoves);	// Adds all of the buttons to the HBox
		
		bpInner.setRight(hBox);									// Right justify the horizontal box

		// ** grdBase ** - create the entire background grid in dark tan with 4 x 4 light tan colored rectangles (add to stack pane next)
		grdBase.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");		
		grdBase.setPadding(new Insets(18,9,9,18));	// Spacing around 4x4 group of light tan rectangles in dark tan spMain below (tp,rt,bt,lt)
													//   (rt & bt are 9 since Hgap and Vgap are set to 18 below)
		grdBase.setHgap(18);						// Sets horizontal gap between rectangles (9 pixel border around each except 1st row)
		grdBase.setVgap(18);						// Sets vertical gap between rectangles (9 pixel border around each except 1st col)
		
		for(col=0; col<=3; col++)					// Add 4x4 grid of 16 rectangles in grdBase
		{
			for(row=0; row<=3; row++)
			{
				Rectangle recBase = new Rectangle(0,0,132,132);	// (start position x,y,width,height)
				recBase.setFill(Color.rgb(204, 192, 179));  	// Color.rgb(204, 192, 179
				recBase.setArcHeight(10);						// Creates the 'smooth' curves of the tiles
				recBase.setArcWidth(10);
				grdBase.add(recBase,col,row);
			}
		}
					
		// ** spMain ** - this is the invisible stack pane object located in the middle of bpMain
		spMain.setPadding(new Insets(0,0,4,0));		// White space gap around Stack Pane (top, right, bottom, left)
		spMain.getChildren().add(grdBase);			// Add the 4x4 grid of light tan colored rectangles to the Stack Pane spMain
		bpMain.setCenter(spMain);					// Add Stack Pane to middle of bpMain
		bpMain.setPadding(new Insets(4,18,4,18));	// Place a white border around bpMain inside the Scene window

		// ** scMain ** - create scene and adjusts stage - this is the window created that IS the 2048 game.
		primaryStage.initStyle(StageStyle.UNDECORATED);		// Window style has no border or min/max/exit buttons in top right
		primaryStage.setResizable(false);					// Do not allow window to be resized
		primaryStage.setScene(scMain); 				// Place the scene in the stage
		primaryStage.show(); 						// Display the stage
		
		gen_tile(); gen_tile();		// Generate 2 starting Tiles
		
	}	

	public TwentyFortyEight()	// ***** MAIN PROGRAM LOOP *****
	{
		// Handles Keyboard Key Presses
		scMain.setOnKeyPressed(new EventHandler<KeyEvent>()
		{	@Override
	        public void handle(KeyEvent event) 	// *** KEYPRESS HANDLING ***
			{
				switch (event.getCode())
				{
            	case UP:
                	z=MoveUcheck();
            		if (z==true) {StoreBoard(); MoveU(); gen_tile(); UpdateDisplay(); }	// Should be self-explanatory for move functions
            		break;
                case DOWN:
                	z=MoveDcheck();
            		if (z==true) {StoreBoard(); MoveD(); gen_tile(); UpdateDisplay(); }
                	break;
                case LEFT:  
                	z=MoveLcheck();
            		if (z==true) {StoreBoard(); MoveL(); gen_tile(); UpdateDisplay(); }
                	break;
                case RIGHT: 
                	z=MoveRcheck();
            		if (z==true) {StoreBoard(); MoveR(); gen_tile(); UpdateDisplay(); }
                	break;
				case H: 
					if (event.isAltDown()) display_help();	// Help
					break;
				case L: 
					if (event.isAltDown()) load_game();		// Loads 2048.dat
					break;
				case S: 
					if (event.isAltDown()) save_game();		// Saves to 2048.dat
					break;
				case T: 
					if (event.isControlDown()) GridValues[0][0][0]=2048;// A test case for checking win window. (Ctrl+T)
					break;
				case X: 
					if (event.isAltDown()) displayHScore();	// Exits the game, checking for new high score before doing so
					break;
				case Z:
					if(event.isControlDown()) undo_move();	// Undoes move
					break;
				default:
					break;
	            }
        		WinLoseCheck();	// After an event, it checks to see if the player won or lost.
			}
		});
	}

	boolean MoveUcheck()
	{
		// Checks to see if 'Up' is a valid move
		z = false;
		for(col = 0; col <= 3; col++)			// Inspect all columns from left to right
		{
			for(row = 0; row <= 2; row++)		// Inspect each row from top down
			{
				for(i = row+1; i <= 3; i++)		// Inspect each row below the current selected row
				{
					// Is there a tile with a value > 0 that can move up into a tile with a value of zero
					z = z || ((GridValues[0][col][row] == 0) && (GridValues[0][col][i] > 0));	
				}
				// Is there a tile that can be combined to the neighboring tile
				// (a neighboring tile of zero permits a move in the statement above)
			z = z || ((GridValues[0][col][row+1] == GridValues[0][col][row]) && (GridValues[0][col][row] > 0));
			}
		}
		if (z==true) return true;
		return false;
	}
	
	boolean MoveDcheck()
	{
		// Checks to see if 'Down' is a valid move
		z = false;
		for(col = 0; col <= 3; col++)			// Inspect all columns from left to right
		{
			for(row = 3; row >= 1; row--)		// Inspect each row from bottom up
			{
				for(i = row-1; i >= 0; i--)		// Inspect each row above the current selected row
				{
					// Is there a tile with a value > 0 that can move down into a tile with a value of zero
					z = z || ((GridValues[0][col][row] == 0) && (GridValues[0][col][i] > 0));	
				}
				// Is there a tile that can be combined to the neighboring tile
				// (a neighboring tile of zero permits a move in the statement above)
			z = z || ((GridValues[0][col][row] == GridValues[0][col][row-1]) && (GridValues[0][col][row-1] > 0));
			}
		}
		if (z==true) return true;
		return false;
	}

	boolean MoveLcheck()
	{
		// Checks to see if 'Left' is a valid move
		z = false;
		for(row = 0; row <= 3; row++)			// Inspect all rows from top to bottom
		{
			for(col = 0; col <= 2; col++)		// Inspect each column from left to right
			{
				for(i = col+1; i <= 3; i++)		// Inspect each column to the right of the current selected column
				{
					// Is there a tile with a value > 0 that can move left into a tile with a value of zero
					z = z || ((GridValues[0][col][row] == 0) && (GridValues[0][i][row] > 0));	
				}
				// Is there a tile that can be combined to the neighboring tile
				// (a neighboring tile of zero permits a move in the statement above)
			z = z || ((GridValues[0][col+1][row] == GridValues[0][col][row]) && (GridValues[0][col][row] > 0));
			}
		}
		if (z==true) return true;
		return false;
	}
	
	boolean MoveRcheck()
	{
		// Checks to see if 'Right' is a valid move
		z = false;
		for(row = 0; row <= 3; row++)			// Inspect all rows from top to bottom
		{
			for(col = 3; col >= 1; col--)		// Inspect each column from right to left
			{
				for(i = col-1; i >= 0; i--)		// Inspect each column to the left of the current selected column
				{
					// Is there a tile with a value > 0 that can move right into a tile with a value of zero
					z = z || ((GridValues[0][col][row] == 0) && (GridValues[0][i][row] > 0));	
				}
				// Is there a tile that can be combined to the neighboring tile
				// (a neighboring tile of zero permits a move in the statement above)
			z = z || ((GridValues[0][col][row] == GridValues[0][col-1][row]) && (GridValues[0][col-1][row] > 0));
			}
		}
		if (z==true) return true;
		return false;
	}
	
	void StoreBoard()
	{
		for (i = 9; i >=0; i--)
		{
			for(col = 0; col <= 3; col++)
			{		
				for(row = 0; row <= 3; row++)
				{
					// For every grid and grid value associated with that grid, they're backed up by one position. 
					// 'Instances' are stored in a 3rd array dimension
					GridValues[i+1][col][row] = GridValues[i][col][row]; 
				}
			}
			score[i+1] = score[i]; // Also stores the previous scores with an array
		}
	}

	void MoveU()
	{
		// Was able to reuse my old console code
		// Shifts values in the 'Up' direction and checks to make sure all cases of combining or moving are capable of being executed
		for (col=0; col <= 3; col++)	
		{	
			if((GridValues[0][col][0]+GridValues[0][col][1]+GridValues[0][col][2]+GridValues[0][col][3]) != 0)
			{
				while(GridValues[0][col][0] == 0)
				{
					GridValues[0][col][0] = GridValues[0][col][1];
					GridValues[0][col][1] = GridValues[0][col][2];
					GridValues[0][col][2] = GridValues[0][col][3];
					GridValues[0][col][3] = 0;
					
				}
				while((GridValues[0][col][1] == 0) && ((GridValues[0][col][2] + GridValues[0][col][3]) != 0))
				{
					GridValues[0][col][1] = GridValues[0][col][2];
					GridValues[0][col][2] = GridValues[0][col][3];
					GridValues[0][col][3] = 0;
					
				}
				if((GridValues[0][col][2] == 0) && (GridValues[0][col][3] != 0))
				{
					GridValues[0][col][2] = GridValues[0][col][3];
					GridValues[0][col][3] = 0;
					
				}
			
	
				// If statement for if two compared array values are the same. Then combine and move values.
				if(GridValues[0][col][0] == GridValues[0][col][1])
				{
					GridValues[0][col][0] += GridValues[0][col][1];
					score[0] = score[0] + GridValues[0][col][0];
					GridValues[0][col][1] = GridValues[0][col][2];
					GridValues[0][col][2] = GridValues[0][col][3];
					GridValues[0][col][3] = 0;
					if (GridValues[0][col][0] != 0) sfx[col][0]=2;
					
					
					if(GridValues[0][col][1] == GridValues[0][col][2])
					{
						GridValues[0][col][1] += GridValues[0][col][2];
						score[0] = score[0] + GridValues[0][col][1];
						GridValues[0][col][2] = 0;
						if (GridValues[0][col][1] != 0) sfx[col][1]=2;
						
					}
				}
				if(GridValues[0][col][1] == GridValues[0][col][2])
				{
					GridValues[0][col][1] += GridValues[0][col][2];
					score[0] = score[0] + GridValues[0][col][1];
					GridValues[0][col][2] = GridValues[0][col][3];
					GridValues[0][col][3] = 0;
					if (GridValues[0][col][1] != 0) sfx[col][1]=2;
				}
				if(GridValues[0][col][2] == GridValues[0][col][3])
				{
					GridValues[0][col][2] += GridValues[0][col][3];
					score[0] = score[0] + GridValues[0][col][2];
					GridValues[0][col][3] = 0;
					if (GridValues[0][col][2] != 0) sfx[col][2]=2;
				}
			}
		}
		moves++;
	}
	
	void MoveD()
	{
		// Was able to reuse my old console code
		// Shifts values in the 'Down' direction and checks to make sure all cases of combining or moving are capable of being executed
		for (col=0; col <= 3; col++)	
		{	
			if((GridValues[0][col][0]+GridValues[0][col][1]+GridValues[0][col][2]+GridValues[0][col][3]) != 0)
			{
				while(GridValues[0][col][3] == 0)
				{
					GridValues[0][col][3] = GridValues[0][col][2];
					GridValues[0][col][2] = GridValues[0][col][1];
					GridValues[0][col][1] = GridValues[0][col][0];
					GridValues[0][col][0] = 0;
				}
				while((GridValues[0][col][2] == 0) && ((GridValues[0][col][1] + GridValues[0][col][0]) != 0))
				{
					GridValues[0][col][2] = GridValues[0][col][1];
					GridValues[0][col][1] = GridValues[0][col][0];
					GridValues[0][col][0] = 0;
				}
				if((GridValues[0][col][1] == 0) && (GridValues[0][col][0] != 0))
				{
					GridValues[0][col][1] = GridValues[0][col][0];
					GridValues[0][col][0] = 0;
				}
			
			
				// If statement for if two compared array values are the same. Then combine and move values.
				if(GridValues[0][col][3] == GridValues[0][col][2])
				{
					GridValues[0][col][3] += GridValues[0][col][2];
					score[0] = score[0] + GridValues[0][col][3];
					GridValues[0][col][2] = GridValues[0][col][1];
					GridValues[0][col][1] = GridValues[0][col][0];
					GridValues[0][col][0] = 0;
					if (GridValues[0][col][3] != 0) sfx[col][3]=2;
				
					if(GridValues[0][col][2] == GridValues[0][col][1])
					{
						GridValues[0][col][2] += GridValues[0][col][1];
						score[0] = score[0] + GridValues[0][col][2];
						GridValues[0][col][1] = 0;
						if (GridValues[0][col][2] != 0) sfx[col][2]=2;
					}
				}
				if(GridValues[0][col][2] == GridValues[0][col][1])
				{
					GridValues[0][col][2] += GridValues[0][col][1];
					score[0] = score[0] + GridValues[0][col][2];
					GridValues[0][col][1] = GridValues[0][col][0];
					GridValues[0][col][0] = 0;
					if (GridValues[0][col][2] != 0) sfx[col][2]=2;
				}
				if(GridValues[0][col][1] == GridValues[0][col][0])
				{
					GridValues[0][col][1] += GridValues[0][col][0];
					score[0] = score[0] + GridValues[0][col][1];
					GridValues[0][col][0] = 0;
					if (GridValues[0][col][1] != 0) sfx[col][1]=2;
				}
			}
		}
		moves++;
	}

	void MoveL()
	{
		// Was able to reuse my old console code
		// Shifts values in the 'Left' direction and checks to make sure all cases of combining or moving are capable of being executed
		for (row=0; row <= 3; row++)	
		{	
			if((GridValues[0][0][row]+GridValues[0][1][row]+GridValues[0][2][row]+GridValues[0][3][row]) != 0)
			{
				while(GridValues[0][0][row] == 0)
				{
					GridValues[0][0][row] = GridValues[0][1][row];
					GridValues[0][1][row] = GridValues[0][2][row];
					GridValues[0][2][row] = GridValues[0][3][row];
					GridValues[0][3][row] = 0;
					
				}
				while((GridValues[0][1][row] == 0) && (GridValues[0][2][row] + GridValues[0][3][row]) != 0)
				{
					GridValues[0][1][row] = GridValues[0][2][row];
					GridValues[0][2][row] = GridValues[0][3][row];
					GridValues[0][3][row] = 0;
					
				}
				if((GridValues[0][2][row]) == 0 && GridValues[0][3][row] != 0)
				{
					GridValues[0][2][row] = GridValues[0][3][row];
					GridValues[0][3][row] = 0;
					
				}
			
		
				// If statement for if two compared array values are the same. Then combine and move values.
				if(GridValues[0][0][row] == GridValues[0][1][row])
				{
					GridValues[0][0][row] += GridValues[0][1][row];
					score[0] = score[0] + GridValues[0][0][row];
					GridValues[0][1][row] = GridValues[0][2][row];
					GridValues[0][2][row] = GridValues[0][3][row];
					GridValues[0][3][row] = 0;
					if (GridValues[0][0][row] != 0) sfx[0][row]=2;
					
					if(GridValues[0][1][row] == GridValues[0][2][row])
					{
						GridValues[0][1][row] += GridValues[0][2][row];
						score[0] = score[0] + GridValues[0][1][row];
						GridValues[0][2][row] = 0;
						if (GridValues[0][1][row] != 0) sfx[1][row]=2;
					}
				}
				if(GridValues[0][1][row] == GridValues[0][2][row])
				{
					GridValues[0][1][row] += GridValues[0][2][row];
					score[0] = score[0] + GridValues[0][1][row];
					GridValues[0][2][row] = GridValues[0][3][row];
					GridValues[0][3][row] = 0;
					if (GridValues[0][1][row] != 0) sfx[1][row]=2;
				}
				if(GridValues[0][2][row] == GridValues[0][3][row])
				{
					GridValues[0][2][row] += GridValues[0][3][row];
					score[0] = score[0] + GridValues[0][2][row];
					GridValues[0][3][row] = 0;
					if (GridValues[0][2][row] != 0) sfx[2][row]=2;
				}
			}
		}
		moves++;
	}
	
	void MoveR()
	{
		// Was able to reuse my old console code
		// Shifts values in the 'Right' direction and checks to make sure all cases of combining or moving are capable of being executed
		for (row=0; row <= 3; row++)	
		{
			if((GridValues[0][0][row]+GridValues[0][1][row]+GridValues[0][2][row]+GridValues[0][3][row]) != 0)
			{
				while(GridValues[0][3][row] == 0)
				{
					GridValues[0][3][row] = GridValues[0][2][row];
					GridValues[0][2][row] = GridValues[0][1][row];
					GridValues[0][1][row] = GridValues[0][0][row];
					GridValues[0][0][row] = 0;
				}
				while((GridValues[0][2][row] == 0) && ((GridValues[0][1][row] + GridValues[0][0][row]) != 0))
				{
					GridValues[0][2][row] = GridValues[0][1][row];
					GridValues[0][1][row] = GridValues[0][0][row];
					GridValues[0][0][row] = 0;
					
				}
				if((GridValues[0][1][row]) == 0 && (GridValues[0][0][row] != 0))
				{
					GridValues[0][1][row] = GridValues[0][0][row];
					GridValues[0][0][row] = 0;
					
				}
			
			
				// If statement for if two compared array values are the same. Then combine and move values.
				if(GridValues[0][3][row] == GridValues[0][2][row])
				{
					GridValues[0][3][row] += GridValues[0][2][row];
					score[0] = score[0] + GridValues[0][3][row];
					GridValues[0][2][row] = GridValues[0][1][row];
					GridValues[0][1][row] = GridValues[0][0][row];
					GridValues[0][0][row] = 0;
					if (GridValues[0][3][row] != 0) sfx[3][row]=2;
					
					if(GridValues[0][2][row] == GridValues[0][1][row])
					{
						GridValues[0][2][row] += GridValues[0][1][row];
						score[0] = score[0] + GridValues[0][2][row];
						GridValues[0][1][row] = 0;
						if (GridValues[0][2][row] != 0) sfx[2][row]=2;
					}
				}
				if(GridValues[0][2][row] == GridValues[0][1][row])
				{
					GridValues[0][2][row] += GridValues[0][1][row];
					score[0] = score[0] + GridValues[0][2][row];
					GridValues[0][1][row] = GridValues[0][0][row];
					GridValues[0][0][row] = 0;
					if (GridValues[0][2][row] != 0) sfx[2][row]=2;
				}
				if(GridValues[0][1][row] == GridValues[0][0][row])
				{
					GridValues[0][1][row] += GridValues[0][0][row];
					score[0] = score[0] + GridValues[0][1][row];
					GridValues[0][0][row] = 0;
					if (GridValues[0][1][row] != 0) sfx[1][row]=2;
				}
			}
		}
		moves++;
	}
	
	void UpdateDisplay()	// Displays the window for the help button (include instructions and button combos)
	{
		// First updates the High Score if necessary, and then saves to file.
		if (score[0] > hiscore)
		{
		hiscore = score[0];
		}
		
		// Remove and Add rectangles to the grdBase depending on what values exist in GridValues.
		grdBase.getChildren().clear();
		for(col=0; col<=3; col++)
		{
			for(row=0; row<=3; row++)
			{
				grdBase = Tile.UpdateTile(grdBase, col, row, GridValues[0][col][row], sfx[col][row]);
				sfx[col][row] = 0;
			}
		}
		
		HBox hBox = new HBox(64);								// Display box added to bpInner with items spaced 64 pixels apart
		bpInner.setRight(hBox);									// Right justify the horizontal box
		Label lbHScore = new Label("High Score: \n" + hiscore);	// Display the score
		lbHScore.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		Label lbScore = new Label("Score:      \n" + score[0]);	// Display the score
		lbScore.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		Label lbMoves = new Label("Move Count: \n" + moves);		// Display the number of moves
		lbMoves.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		hBox.getChildren().addAll(lbHScore, lbScore, lbMoves);

	}

	void WinLoseCheck()
	{
		
	// Lose = No tiles with a zero, and no available move.
	// Win = At least 1 tile contains the value 2048.
		
		boolean gridfull = true; 
		boolean winner = false;
		
		for (col=0; col<=3; col++)
		{
			for (row=0; row<=3; row++)
			{
				if (GridValues[0][col][row] == 2048)	// Check to see if the player won
				{
					winner = true;
				}
				if (GridValues[0][col][row] == 0)		// Check to see if the grid is NOT full yet
				{
					gridfull = false;
				}
			}
		}
		
		if (gridfull == true)	// Check to see if any of the tiles can be combined if the grid is full
		{
			for (col=0; col<=3; col++)
			{
				for (row=0; row<=2; row++)
				{
					if (GridValues[0][col][row]==GridValues[0][col][row+1])
					{
						gridfull = false;
					}
				}
			}
			for (row=0; row<=3; row++)
			{
				for (col=0; col<=2; col++)
				{
					if (GridValues[0][col][row]==GridValues[0][col+1][row])
					{
						gridfull = false;
					}
				}
			}
		
		}

		// Display Win Window
		if ((winner == true) && (xplay == 0))
		{
			Stage winStage = new Stage(StageStyle.UNDECORATED);
			winStage.setResizable(false);
			
			// Plays 'tada' sound
			Media tada = new Media(new File("tada.wav").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(tada);
			mediaPlayer.play();
				
			StackPane winPane = new StackPane();
			winPane.setPrefSize(600,350);
			winPane.setStyle("-fx-border-color: black");
			winPane.setPadding(new Insets(18,18,72,18));	// (top, right, bottom, left)
			
			// Creates a rather nice-looking window congratulating the player
			Label gCont1 = new Label("!! CONGRATULATIONS !!\n!!! You Won !!!"); 
			gCont1.setFont(Font.loadFont("file:BLKCHCRY.ttf", 36));
			gCont1.setTextAlignment(TextAlignment.CENTER);
			StackPane.setAlignment(gCont1, Pos.TOP_CENTER);
			Label gCont2 = new Label("\n\nWould you like to Continue?\n(You can play until the board is full)"); 
			gCont2.setFont(Font.loadFont("file:BLKCHCRY.ttf", 24));
			gCont2.setTextAlignment(TextAlignment.CENTER);
			StackPane.setAlignment(gCont2, Pos.CENTER);
			
			Button btYes = new Button("Yes! For glory!");	// Turns off the winner check and lets the player keep playing.
			btYes.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
					+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
			btYes.setOnAction(new EventHandler<ActionEvent>() 
			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		xplay = 1;
		    		winStage.close();
		    		return;
		    	}
			});
			
			Button btNo = new Button("No thanks!");	// Calls high score method in case player achieved a new high score. Then exits.
			btNo.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
					+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
			btNo.setOnAction(new EventHandler<ActionEvent>() 
			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		winStage.close();
		    		displayHScore();
		    	}
			});
			
			HBox winBox = new HBox(100);	// Creates HBox for Yes and No buttons
			winBox.getChildren().addAll(btYes, btNo);
			winBox.setAlignment(Pos.BOTTOM_CENTER);

			winPane.getChildren().addAll(gCont1, gCont2, winBox);
			
			Scene winScene = new Scene(winPane);
			winStage.setScene(winScene);	
			winStage.show();
		}

		// Display 'game over' window after playing a sound. One of my own, and a little louder than 'tada'.
		if (gridfull == true)
		{
			Media tada = new Media(new File("Layton.wav").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(tada);
			mediaPlayer.play();

			displayHScore();
		}	
	}

	void gen_tile()			// Generates a random tile on the board (needs to be for every turn)
	{	
		boolean gotnum=false;
		double gnum=0.0;
		while(gotnum == false)
		{
			row = (int)(Math.random()*4);
			col = (int)(Math.random()*4);
			if(GridValues[0][col][row] == 0)
			{
				gnum = Math.random();
				if(gnum < 0.89)		// Probability function for determining if 2 or 4 is generated
					GridValues[0][col][row] = 2;
				else
					GridValues[0][col][row] = 4;
				gotnum=true;
				sfx[col][row] = 1;	// Sets special effect to "1" to indicate newly added tile
				grdBase = Tile.UpdateTile(grdBase, col, row, GridValues[0][col][row], sfx[col][row]);
			}
		}
	}
	
	void undo_move()		// Undoes a move by taking off the "top pancake" and showing the next one underneath(works with backup_board)
	{
		i=0;
		for (col=0; col<=3; col++)
		{
			for (row=0; row<=3; row++)
			{
				i += GridValues[1][col][row];	// If all values are 0, then we know there are no more undo moves left
			}
		}
		
		if (i==0)	// No more moves to Undo
		{
			// Plays a sound to signify no more undoes are available
			Media nope = new Media(new File("BUZZ.wav").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(nope);
			mediaPlayer.play();
		}
		
		if (i>0)	// Move can be undone
		{
			for (i=1; i<=10; i++)
			{
				for (col=0; col<=3; col++)
				{
					for (row=0; row<=3; row++)
					{
						GridValues[i-1][col][row]=GridValues[i][col][row];	// Undoes moves by loading up previous values 
					}
				}
				score[i-1] = score[i]; // Also stores the previous scores
			}
			for (col=0; col<=3; col++)
			{
				for (row=0; row<=3; row++)
				{
					GridValues[10][col][row]=0;		// Clears last grid values since move was undone
				}
			}
			score[10]=0;							// Clears last grid score value
			moves--;								// Decrements moves
		}
		UpdateDisplay();							// Updates display with new values and tiles
	}
	
	void save_game() 		// Saves game to a file
	{	
		int x;
		PrintWriter saver;
		try
		{
			saver = new PrintWriter("2048.dat", "UTF-8");	// Creates new file or overwrites old file of same name
			saver.println("Stored 2048 game:");
			for (i=0; i<=10; i++)
			{
				for (row=0; row<=3; row++)	
				{
					for (col=0; col<=3; col++)
					{
						// Add 10,000 to each stored number as a formatting tool for ease of recovering the data in the load routine
						x = 10000 + GridValues[i][col][row];
						saver.print(x + ",");	// Types up all values of grid in lines of 16 values for each grid
					}
				}
				// Add 10,000 to each stored number as a formatting tool for ease of recovering the data in the load routine
				x = 10000 + score[i];	// Saves score
				saver.println(x);
			}
			// Add 10,000 to each stored number as a formatting tool for ease of recovering the data in the load routine
			x = 10000 + moves;			// Saves moves
			saver.print(x + ",");
			
			x = 10000 + xplay;			// Saves the number which determines if the player is on extended play or regular play
			saver.print(x);
			
			saver.close();				// Closes file and stops writing
			
		} catch (FileNotFoundException e)			// Auto-generated catch block
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)	// Auto-generated catch block
		{
			e.printStackTrace();
		}

		// Plays sound when done saving file
		Media dataSaved = new Media(new File("Data_Acquire.wav").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(dataSaved);
		mediaPlayer.play();

	}
	
	@SuppressWarnings("resource")
	void load_game()  		// Loads game from previously saved file
	{
		// Requires try/catch statements for file existence, ability to input/output to file correctly, and scan information
		int ln, cnt, str, stp, val;
		BufferedReader loader;
		try
		{
			loader = new BufferedReader(new FileReader("2048.dat"));
			String gData = null;
			try
			{
				for (ln = 0; ln <= 12; ln++)
				{
					gData = loader.readLine();
					// lines 0 the text file contains the title of the file
					if (ln == 0 && (!(gData.substring(0, 17)).equals("Stored 2048 game:")))
					{
						return;	// REturns if the file exists, but is not the correct file or is corrupted
					}
					// lines 1 through 11 in the text file contains the data for the current move and the 10 previous moves (including score)
					if (ln > 0 && ln < 12)	
					{
						cnt=-1;
						for (row=0; row<=3; row++)	
						{
							for (col=0; col<=3; col++)
							{
								// Scans every 5 characters inbetween commas and then subtracts 10000 to get values for grid positions
								cnt++; str = cnt*6; stp = str+5;
								val = Integer.parseInt(gData.substring(str,stp));
								// 10,000 was added to stored number as a formatting tool for ease of recovering the data here
								GridValues[ln-1][col][row] = val - 10000;
							}
						}
						cnt++; str = cnt*6; stp = str+5;
						val = Integer.parseInt(gData.substring(str,stp));
						// 10,000 was added to stored number as a formatting tool for ease of recovering the data here
						score[ln-1] = val - 10000;
					}
					// lines 12 in the text file contains the data for the number of moves
					if (ln == 12)
					{
						val = Integer.parseInt(gData.substring(0,5));
						// 10,000 was added to stored number as a formatting tool for ease of recovering the data here
						moves = val - 10000;
						
						val = Integer.parseInt(gData.substring(6,11));
						// 10,000 was added to stored number as a formatting tool for ease of recovering the data here
						xplay = val - 10000;

						// Plays sound when file is successfully loaded
						Media sndLoaded = new Media(new File("PRescue.wav").toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(sndLoaded);
						mediaPlayer.play();

						mediaPlayer.play();
					}
				}
			} 
			catch (IOException e)  // Auto-generated catch block in case there is no data to read (readline)
				{ e.printStackTrace(); }
			try
			{
				loader.close();
				
			} 
			catch (IOException e) // Auto-generated catch block in case there is no data (BufferedReader)
			{ e.printStackTrace(); }
		} 
		catch (FileNotFoundException e) // Auto-generated catch block if there is no file found
		{
			// Plays a sound, because there's no save file
			Media nope = new Media(new File("BUZZ.wav").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(nope);
			mediaPlayer.play();
			
			e.printStackTrace();
		}
		
		// Update display with loaded grid, score, move, and extended play values.
		UpdateDisplay();
		
	}

	void display_help()		// Displays the window for the help button (include instructions)
	{
		// Creates simple StackPane, stage, scene, label (with information), and close button
		StackPane helpPane = new StackPane();
		helpPane.setPrefSize(600,600);
		helpPane.setPadding(new Insets (18,18,18,18));
		helpPane.setStyle("-fx-border-color: black");
		Scene helpScene = new Scene(helpPane);
		
		// did this to get rid of the existing buttons and Window's style box/border
		Stage helpStage = new Stage(StageStyle.UNDECORATED);	
		helpStage.setResizable(false);
		helpStage.setScene(helpScene);
		
		// Had difficulty with spacing words so they would stay on screen and not be shown as '...'. Maybe should've used a TextField?
		Label info = new Label(	"Salutations! 2048 is a single-player puzzle game created in\n" +
								"March 2014 by 19-year-old Italian web developer Gabriele\n" +
								"Cirulli.\n\n" +
								"The objective is to slide numbered tiles using the arrow\n" +
								"keys: [Up]  [Down]  [Left]  [Right] to combine tiles of\n" +
								"equal value.\n" +
								"The grid begins with 2 randomly generated tiles, each of\n" +
								"a value 2 or 4.  On each turn 1 additional tile is \n" +
								"randomly added to the grid.\n\n" +
								"You can undo up to 10 moves!\n" +
								"You win once a tile = 2048!\n\n" +
								"                          Good luck! ^_^"); 
		info.setFont(Font.font(null,20));	// Set the font of the help screen text

		StackPane.setAlignment(info, Pos.TOP_CENTER);
		
		// Creates button that closes help window and lets the player resume play
		Button btClose = new Button("Close");
		btClose.setStyle("-fx-border-color: rgb(187,173,160); -fx-border-radius: 5,5,5,5;"
				+ " -fx-background-color: rgb(187,173,160); -fx-background-radius: 5,5,5,5;");
		StackPane.setAlignment(btClose, Pos.BOTTOM_RIGHT);
		btClose.setOnAction(new EventHandler<ActionEvent>() 

			{
		    	@Override public void handle(ActionEvent event) 
		    	{
		    		helpStage.close();
		    	}
			});
		
		helpPane.getChildren().addAll(info, btClose);
		
		helpStage.show();

	}

	void displayHScore()		// Shows the game over screen after checking for highscore and displaying highscore screen 
	{
		
	if (hiscore == score[0])	// If there is a new high score, then display it.
		{
		
		// Plays 'tada' sound when high score is achieved
		Media tada = new Media(new File("tada.wav").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(tada);
		mediaPlayer.play();
		
		PrintWriter savehscore;
		try
			{
				// Saves current score to highscore file
				savehscore = new PrintWriter("HighScore.dat", "UTF-8");
				// Add 10,000 to the value as a formatting tool for ease of recovering the data when loaded
				int x = (10000 + hiscore);
				savehscore.println(x);
				savehscore.close();
			}
			catch (FileNotFoundException e)			// Auto-generated catch block
				{ e.printStackTrace(); }
			catch (UnsupportedEncodingException e)	// Auto-generated catch block
				{ e.printStackTrace(); }
		
			// Creates and displays High Score for five seconds
			StackPane hsPane = new StackPane();
			hsPane.setPrefSize(600,400);
			hsPane.setStyle("-fx-border-color: black");
			Scene hsScene = new Scene(hsPane);
			Stage hsStage = new Stage(StageStyle.UNDECORATED);
			hsStage.setScene(hsScene);
			hsStage.show();
		
			Label hScore = new Label("NEW HIGH SCORE: \n" + hiscore); 
			hScore.setFont(Font.loadFont("file:BLKCHCRY.ttf", 48));	// Imported one of my favorite fonts
			hScore.setTextAlignment(TextAlignment.CENTER);
			hsPane.getChildren().add(hScore);
		
			// Made the duration for 5 seconds before closing the high score window and calling the exit game method
			PauseTransition delay5 = new PauseTransition(Duration.seconds(5));
			delay5.setOnFinished( event -> {hsStage.close(); exit_game();});
			delay5.play();
		}
		else
		{
			exit_game();	// If no high score, then goes straight to exit game
		}
	}
	
	void exit_game()		// Shows the game over screen 
	{
		
		// Creates and displays 'Game over'
		StackPane endPane = new StackPane();
		endPane.setPrefSize(300,200);
		endPane.setStyle("-fx-border-color: black");
		Scene endScene = new Scene(endPane);
		Stage endStage = new Stage(StageStyle.UNDECORATED);
		endStage.setScene(endScene);
		endStage.show();
		
		Label gOver = new Label("Game over!"); 
		gOver.setFont(Font.loadFont("file:BLKCHCRY.ttf", 48));
		endPane.getChildren().add(gOver);
		StackPane.setAlignment(gOver,Pos.CENTER);
		
		// Displays game over screen for 2 seconds before closing program
		PauseTransition delay2 = new PauseTransition(Duration.seconds(2));
		delay2.setOnFinished( event -> System.exit(0));
		delay2.play();
	}

}