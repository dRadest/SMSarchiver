package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*
 * 
 * Application interface that lets you browse the file system for the conversation record
 * and convert it to an html which can be opened with a web browser
 * 
 * */

public class Main extends Application {
	//holder for a file
	File file = null;
	@Override
	public void start(Stage primaryStage) {
		try {

			// preparing all visual elements
			final Button openButton = new Button("Open file");
			final Button convertButton = new Button("Convert!");
			final Button reverseButton = new Button("Reverse");
			Text selectedText = new Text("Selected:");
			Text selectedField = new Text("None");

			// create filechooser
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("TXT Files", "*.txt"),
					new ExtensionFilter("XML Files", "*.xml"));

			// behavior of a Open file button
			openButton.setOnAction(
					new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent e) {
							file = fileChooser.showOpenDialog(primaryStage);
							if(file != null) {
								// display file name
								String fileName = file.getName();
								selectedField.setText(fileName);
							}
						}
					});
			
			// behavior of a Reverse button
			reverseButton.setOnAction(
			        new EventHandler<ActionEvent>() {
			            @Override
			            public void handle(ActionEvent event) {
			            	/* if file hasn't been chose, warn the user to choose a file first */
			            	if(file == null) {
				                final Stage dialog = new Stage();
				                dialog.initModality(Modality.APPLICATION_MODAL);
				                dialog.initOwner(primaryStage);
				                VBox dialogVbox = new VBox(20);
				                Button okButton = new Button("OK");
				                dialogVbox.getChildren().addAll(new Text("Pick a file first"), okButton);
				                dialogVbox.setAlignment(Pos.CENTER);
				                okButton.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent arg0) {
										dialog.close();	
									}
				                });
				                Scene dialogScene = new Scene(dialogVbox, 300, 200);
				                dialog.setScene(dialogScene);
				                dialog.show();
			            	} else { /* call the method to reverse the file */
			            		reverse(file);
			            	}
			            }
			         });
			
			// behavior of a Convert button
			convertButton.setOnAction(
			        new EventHandler<ActionEvent>() {
			            @Override
			            public void handle(ActionEvent event) {
			            	if(file == null) { /* show the popup window */
				                final Stage dialog = new Stage();
				                dialog.initModality(Modality.APPLICATION_MODAL);
				                dialog.initOwner(primaryStage);
				                VBox dialogVbox = new VBox(20);
				                Button okButton = new Button("OK");
				                dialogVbox.getChildren().addAll(new Text("Pick a file first"), okButton);
				                dialogVbox.setAlignment(Pos.CENTER);
				                okButton.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent arg0) {
										dialog.close();	
									}
				                });
				                Scene dialogScene = new Scene(dialogVbox, 300, 200);
				                dialog.setScene(dialogScene);
				                dialog.show();
			            	} else { /* call the method to convert the file */
			            		convertToHTML(file);
			            	}
			            }
			         });

			// Creating a Grid Pane 
			GridPane gridPane = new GridPane();   

			// adding elements to the grid pane
			gridPane.add(selectedText, 0, 0);
			gridPane.add(selectedField, 1, 0);
			gridPane.add(openButton, 0, 1);
			gridPane.add(reverseButton, 1, 1);
			gridPane.add(convertButton, 2, 1);

			// Setting size for the pane 
			gridPane.setMinSize(500, 500); 

			// Setting the padding    
			gridPane.setPadding(new Insets(10, 10, 10, 10));  

			// Setting the vertical and horizontal gaps between the columns 
			gridPane.setVgap(5); 
			gridPane.setHgap(5);       

			// Setting the Grid alignment 
			gridPane.setAlignment(Pos.CENTER); ; 

			// Creating a scene object 
			Scene scene = new Scene(gridPane); 

			// Setting title to the Stage 
			primaryStage.setTitle("FileChooser Excercise"); 

			// Adding scene to the stage 
			primaryStage.setScene(scene); 

			// Displaying the contents of the stage 
			primaryStage.show();

		} catch(Exception e) { /* not catching exceptions in reality */
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/* reverses the lines of the input file 
	 * we use this method to reverse the order of messages in the original file */
	public static void reverse(File inputFile) {
		BufferedReader reader = null;
		OutputStreamWriter writer = null;
		// save temporary file in the same directory as the input file
		String path = inputFile.getPath();
		int index = path.indexOf(".txt"); /* TODO: handle StringIndexOutOfBounds exception */
		String tempFilePath = path.substring(0, index) + "Temp.txt";
		File outputFile = new File(tempFilePath);
		try {
			// instantiate the reader and writer
			reader = new BufferedReader(new FileReader(inputFile));
			writer = new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
			// reverse the lines using stack
			String line = null;
			Stack<String> stk = new Stack<String>();
			while((line = reader.readLine()) != null) {
				stk.push(line);
			}
			while(!stk.isEmpty()) {
				line = stk.pop();
				writer.write(line + "\n");
			}
			// close reader and writer
			reader.close();
			writer.close();
		} catch (Exception e) { /* pretend catching exception */
			e.printStackTrace();
		}
		
		// delete the "old" input file and rename the newly created file using that name
		Path inputFilePath = Paths.get(path);
		try {
			Files.delete(inputFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		outputFile.renameTo(inputFile);
	}
	
	    /* converts the lines of the input file */
		public static void convert(File inputFile) {
			// regex pattern to match
			String regex = "<sms\\saddress=\"(.+)\"\\stime=\"(.+)\"\\sdate=\"(\\d+)\"\\stype=\"(1|2)\"\\sbody=\"(.*)\"\\sread=\"(\\d)\"\\sservice_center=\"(.*)?\"\\sname=\"(.+)\"\\s\\/>";
			Pattern p = Pattern.compile(regex);
			// date to be compared with the date we catch
			String writeDate = null;
			// declare reader and writer
			BufferedReader reader = null;
			BufferedWriter writer = null;
			// construct a path for the new file
			String parent = inputFile.getParent();
			String outputFilePath = parent + "\\outputFile.txt";
			try {
				// String object to hold the current line 
				String line = null;
				// instantiate reader and writer
				reader = new BufferedReader(new FileReader(inputFile));
				writer = new BufferedWriter(new FileWriter(outputFilePath));
				while((line = reader.readLine()) != null) {
					Matcher m = p.matcher(line);
					if(m.find()) {
						String date = m.group(3);
						String type = m.group(4);
						String body = m.group(5);
						String name = m.group(8);
						
						long milis = Long.parseLong(date);
						Date holDate = new Date(milis);
						/* convert the captured date to a string of desired format (i.e MMM dd, YYYY) 
						 * and compare it to a current date 
						 * if it doesn't match, assign the new date and write it in a file 
						*/
						String dateString = dateToString(holDate);
						if(!dateString.equals(writeDate)) {
							writeDate = dateString;
							writer.write(writeDate);
							writer.newLine();
						}
						// depending on a type caught, write the appropriate name
						if(type.equals("1")) {
							writer.write(name + ": " + body);
						} else if(type.equals("2")) {
							writer.write("Me: " + body);
						}
						writer.newLine();
						
					}
					
					
				}
				// close reader and writer
				writer.close();
				reader.close();
			} catch (Exception e) { /* fake exception handling */
				e.printStackTrace();
			}
			
		}
		
		// converts a Date object into a String of desired format
		private static String dateToString(Date date) {
			String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
			Format formatter = new SimpleDateFormat("M d, y");
		    String s = formatter.format(date);
		    int monthNumber = 0;
		    /* check if month is a two digit one (Oct, Nov or Dec) 
		     * and format the string accordingly */
		    if(s.charAt(1) == ' ') { /* is NOT two digit */
		    	monthNumber = Character.getNumericValue(s.charAt(0));
		    	s = s.substring(2);
		    } else {
		    	monthNumber = Integer.parseInt(s.substring(0, 2));
		    	s = s.substring(3);
		    }
		    
		    String output = months[monthNumber-1] + " " + s;
		    return output;
		}
		
		// converts a Date object into an hour String
		private static String getHour(Date date) {
			Format formatter = new SimpleDateFormat("HH:mm");
		    return formatter.format(date);
		}
		
		/* converts the lines of the input file */
		public static void convertToHTML(File inputFile) {
			// regex pattern to match
			String regex = "<sms\\saddress=\"(.+)\"\\stime=\"(.+)\"\\sdate=\"(\\d+)\"\\stype=\"(1|2)\"\\sbody=\"(.*)\"\\sread=\"(\\d)\"\\sservice_center=\"(.*)?\"\\sname=\"(.+)\"\\s\\/>";
			Pattern p = Pattern.compile(regex);
			// date to be compared with the date we catch
			String writeDate = null;
			// declare reader and writer
			BufferedReader reader = null;
			OutputStreamWriter writer = null;
			// construct a path for the new file
			String parent = inputFile.getParent();
			String outputFilePath = parent + "\\convOutput.html";
			
			/* prepare the initial HTML */
			
			try {
				// instantiate reader and writer
				reader = new BufferedReader(new FileReader(inputFile));
				writer = new OutputStreamWriter(new FileOutputStream(outputFilePath),"UTF-8");
				
				// write initial lines in HTML file
				writer.write("<!DOCTYPE html>\n");
				writer.write("<html lang = \"sl-si\">\n");
				writer.write("<head>\n");
				writer.write("<meta charset = \"UTF-8\">\n");
				writer.write("<!DOCTYPE html>\n");
				writer.write("<title>Conversation</title>\n");
				writer.write("<link rel = \"stylesheet\" type = \"text/css\" href = \"constyle.css\" />\n");
				writer.write("</head>\n");
				writer.write("<body>\n");
				
				// String object to hold the current line 
				String line = null;
				while((line = reader.readLine()) != null) {
					Matcher m = p.matcher(line);
					if(m.find()) {
						String date = m.group(3);
						String type = m.group(4);
						String body = m.group(5);
						//String name = m.group(8);
						
						long milis = Long.parseLong(date);
						Date holDate = new Date(milis);
						/* convert the captured date to a string of desired format (i.e MMM dd, YYYY) 
						 * and compare it to a current date 
						 * if it doesn't match, assign the new date and write it in a file */
						String dateString = dateToString(holDate);
						if(!dateString.equals(writeDate)) {
							writeDate = dateString;
							writer.write("<p class=\"datemark\">"+writeDate+"</p>\n");
						}
						writer.write("<div class=\"clearfix\">\n");
						// depending on a type caught, write the appropriate name
						if(type.equals("1")) {
							writer.write("<p class=\"speech-bubble-left\">\n");
						} else if(type.equals("2")) {
							writer.write("<p class=\"speech-bubble-right\">\n");
						}
						// get hour from date
						String hourstamp = getHour(holDate);
						writer.write(body + "<br/><code>" + hourstamp +  "</code>\n");
						writer.write("</p>\n");
						writer.write("</div>\n");
						
					}
					
					
				}
				// finish HTML file
				writer.write("</body>\n");
				writer.write("</html>");
				// close reader and writer
				writer.close();
				reader.close();

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
}
