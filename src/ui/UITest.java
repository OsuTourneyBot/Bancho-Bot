package ui;


import javafx.application.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;

public class UITest extends Application
{
	//CLASS WIDE SCOPE AREA
	Label responseLbl;
	

	
	//the gang of three methods...init() start() stop()
	
	public void init()//can do set up here for other things in the program
	{
		System.out.println("Inside init() method...");
	}
	
	//next method is the start(Stage s) method. We override this, as it is analogous
	// to our JFrame constructor. 
	public void start(Stage myStage)
	{
		System.out.println("Inside start, this is where we would assemble components...");
		
		myStage.setTitle("JavaFX: Example of Button Listeners!");
		
		//often seen in JavaFX apps...create a ROOT NODE pane, which is like the parent
		// of all the other nodes in the app.
		//REVISION: add two arguments for horizontal and vertical gap spacing
		
		FlowPane rootNode = new FlowPane(10,10);//hGap and vGap
		
		//also set the alignment
		rootNode.setAlignment(Pos.CENTER);
		
		//make a Scene!
		Scene myScene = new Scene(rootNode, 400, 200);//sizes the scene
		
		//put the scene object on the stage.
		myStage.setScene(myScene);
		
		//label for instruction
		responseLbl = new Label("Push one of the buttons!");
		
		//create two Buttons
		Button btn1 = new Button("1");
		Button btn2 = new Button("2");
		
		//EVENT HANDLING. Use Anonymous inner class
		btn1.setOnAction(new EventHandler<ActionEvent>() 
				{
			    public void handle(ActionEvent ev)
			    {
			    	//set the label text
			    	responseLbl.setText("Button 1 was pushed.");
			    }//end of the handle method
				}//end of the ANONYMOUS INNER CLASS			
				
				);//closing parenthesis of setOnAction()
		
		
		//for button 2
		btn2.setOnAction(new EventHandler<ActionEvent>() 
		{
	    public void handle(ActionEvent ev)
	    {
	    	//set the label text
	    	responseLbl.setText("Button 2 was pushed.");
	    }//end of the handle method
		}//end of the ANONYMOUS INNER CLASS			
		
		);//closing parenthesis of setOnAction()
		
		//now add the label and the buttons to the scene graph using the addAll() method
		
		rootNode.getChildren().addAll(btn1,btn2,responseLbl);		
		
		
		//last line..RAISE THE CURTAIN!
		myStage.show();
		
	}//end start
	
	//override the stop() method if necessary
	public void stop()
	{
		System.out.println("Inside the stop() method...");
	}
	

	public static void start(String[] args)
	{
		// only line of code you need in the main in a JavaFX program is this...
		launch(args);
	}
	//end main
}

class name
{

}


