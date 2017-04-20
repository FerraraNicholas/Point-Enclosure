package csi403;

// Import required Java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;
import java.util.*;

class Point {
    public int x;
    public int y;
    
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
}

// Extend HttpServlet class
public class ReverseList extends HttpServlet {

  // Standard servlet method
  public void init() throws ServletException {
    // Do any required initialization here - likely none
  }

  // Standard servlet method - we will handle a POST operation
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doService(request, response);
  }

  // Standard servlet method - we will not respond to GET
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Set response content type and return an error message
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println("{ 'message' : 'Use POST!'}");
  }

  // Our main worker method
  private void doService(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Get received JSON data from HTTP request
    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
    String jsonStr = "";
    if(br != null) {
      jsonStr = br.readLine();
    }

    // Create JsonReader object
    StringReader strReader = new StringReader(jsonStr);
    JsonReader reader = Json.createReader(strReader);

	// Get the singular JSON object (x:value pair) in this message.    
    JsonObject obj = reader.readObject();

    // From the object get the array xd "inList"
     JsonArray inArray = obj.getJsonArray("inList");

     // Reverse the data in the list
     JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();

    // Set response content type to be JSON
    response.setContentType("application/json");
    // Send back the response JSON message
    PrintWriter out = response.getWriter();

    // Adds all JsonObjects to the LinkedList
    LinkedList<JsonObject> list = new LinkedList<JsonObject>();

    //Variable declaration
    int num;
    int len = inArray.size();
    Point[] points = new Point[len];

    if(inArray.size() == 0){
    	out.print("{ ");
	  	out.print("\"message\" : ");
	  	out.print("\"Malformed JSON\"");
	  	out.print(" }");
    }else{

    try{
	   	//Iterate over inarry and stroe in a linked list of type Json Obejects
	    for(int i = 0; i < inArray.size(); i++) {
	    	//assign the JSON objects to a linked list
	    	list.add(inArray.getJsonObject(i));
		}
	}
    catch(Exception e){
	  out.print("{ ");
	  out.print("\"message\" : ");
	  out.print("\"Malformed JSON\"");
	  out.print(" }");
	  return;
	}

	try{
		//Takes input and convcerts them to individual points in an array
		for (int i = 0; i < inArray.size(); i++) {
			points[i] = new Point(list.get(i).getInt("x"), list.get(i).getInt("y"));
		}
	}
	catch(Exception e){
		out.print("{ ");
		out.print("\"message\" : ");
		out.print("\"Malformed JSON\"");
		out.print(" }");
		return;
	}	

	//Get the number of points in enclosed in the boundry
	num = pointsWithin(points);

	//Add num to the array builder 
    outArrayBuilder.add(num); 

    //Print the contents of outArrayBuilder in JSON format
    out.println("{ \"count\" : " + outArrayBuilder.build().toString() + "}");

  }
}

  // Standard Servlet method
  public void destroy() {
    // Do any required tear-down here, likely nothing.
  }

public static int pointsWithin(Point[] points) {
	//Variable declaration
	int count = 0;
	LinkedList<Point> list = new LinkedList<Point>();
	Point tempPoint;

	//Run thourhg each point on a 19newX9 game board
	for (int i = 0; i < 19; i++) {
	    for (int j = 0; j < 19; j++) {
	        tempPoint = new Point(i, j);

	        //If the point generated is contained in the boundry
	        if (isContained(tempPoint, points) == true) {
	        	//Add it to the linked list
	            list.add(tempPoint);
	        }
	    }   
	}
	//Get the size of the points that were added to the list
	count = list.size();
	//Return the count
	return count;
}


public static boolean isContained(Point test, Point[] points) {
	//Variable declaration
	int i, j;
	boolean result = false;
	int count = 0;

	//Check for intersections
	for (i = 0, j = points.length - 1; i < points.length; j = i++) {
	    if ((points[i].y > test.y) != (points[j].y > test.y)
	            && (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
	        result = !result;
	    }
	}

	//If the point is on the line flag true
	if (isPointOnLine(test, points) == true) {
		//Otherwise it is false
	    result = false;
	}

	//Return the result if the point is on the line or not
	return result;
}

public static boolean isPointOnLine(Point test, Point[] points) {
	//Variable declaration
    Point pointA, pointB;
    int diffX, diffY, newX, newY, cross;

    //Iterate over all the points and assign them accordingly
    for (int i = 0; i < points.length; i++) {
    	//Assign pointA to the index of i
        pointA = points[i];
        //If i is equal to the length - 1
        if (i == points.length - 1) {
        	//Then assign pointB to the first index
            pointB = points[0];
            //otherwise
        } else {
        	//Assign point b
            pointB = points[i + 1];
        }

    //Get the difference between the point passed and the point in the array
    diffX = test.x - pointA.x;
    diffY = test.y - pointA.y;

    //Get the different between the value of x for b and the value of x for A
    newX = pointB.x - pointA.x;
    //Get the difference between the value of y for pointB and the value of y for pointA
    newY = pointB.y - pointA.y;

    //Calculate the cross
    cross = diffX * newY - diffY * newX;

    //If it is zero than the point is on the line
    if (cross == 0) {
    	//Flag the point as true and return the result
        return true;
    }
}
//Otherwise return that is is not on the line
return false;
}	
}

