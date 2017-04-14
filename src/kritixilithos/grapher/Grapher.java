package kritixilithos.grapher;

import java.util.ArrayList;
import processing.core.PApplet;

public class Grapher {
	private PApplet p;
	//BEGIN CUSTOMISATION

	//graph settings
	private float xStart;
	private float xEnd;
	private float yStart;
	private float yEnd;
	//automatically fit the graph (y-coords)
	private boolean autoFit;
	//distance between each x-coordinate (NOTE: will affect speed of render)
	private float tune;
	private boolean axesOn;

	//saving
	private String imageName = "auto_fit.png";
	private boolean save     = false;

	//colours
	private int background;
	private int axes;
	private int startPoints;
	private int endPoints;


	//NOTE: use eval(expression) except when expression contains power
	//float[]y = eval(x*x-3*x+6);
	//float[]y = power(81-x*x,.5);
	//function(x) -> y
	public float[]function(float x) {
		float[]y = mult(power(eval(2.0f), x, false), eval(p.cos(p.PI * x)));
		return y;
	}

	//END CUSTOMISATION

	private ArrayList graphs;
	//private ArrayList points;
	private float maxY = -Float.MAX_VALUE;
	private float minY = Float.MAX_VALUE;

	private float scaleX;
	private float scaleY;
	private float translateX;
	private float translateY;

	long m;


	public Grapher(PApplet parent) {
		p = parent;

		//default values
		xStart      = -5;
		xEnd        = 5;
		yStart      = -5;
		yEnd        = 5;
		autoFit     = true;
		tune        = 0.01f;//0.01 is recommended
		axesOn      = true;

		background  = p.color(0);
		axes        = p.color(255, 0, 0);
		startPoints = p.color(0, 0, 255);
		endPoints   = p.color(0, 255, 0);

		graphs = new ArrayList();

		m = p.millis();
	}

	public void addPoints(Function f) {
		ArrayList points = new ArrayList();
		points.add(new int[]{startPoints, endPoints});

		scaleX = p.width/(xEnd-xStart);

		//generate the points
		for (float x=xStart; x<xEnd; x+=tune/scaleX) {
			float[] result = f.of(x);
			if (result[0] > maxY) maxY = result[0];
			else if (result[1] > maxY) maxY = result[1];
			if (result[0] < minY) minY = result[0];
			else if (result[1] < minY) maxY = result[1];
			points.add(new float[]{x, result[0], result[1]});
		}

		graphs.add(points);
	}

	public void addPoints(Function f, int start, int end) {
		ArrayList points = new ArrayList();
		points.add(new int[]{start, end});

		scaleX = p.width/(xEnd-xStart);

		//generate the points
		for (float x=xStart; x<xEnd; x+=tune/scaleX) {
			float[] result = f.of(x);
			if (result[0] > maxY) maxY = result[0];
			else if (result[1] > maxY) maxY = result[1];
			if (result[0] < minY) minY = result[0];
			else if (result[1] < minY) maxY = result[1];
			points.add(new float[]{x, result[0], result[1]});
		}

		graphs.add(points);
	}

	public void init() {
		//long m = p.millis();

		//auto-fit
		if (autoFit) {
			yStart = minY;
			yEnd   = maxY;
		}

		scaleY     = p.height/(yEnd-yStart);
		translateX = scaleX*(xEnd+xStart)/-2;
		translateY = scaleY*(yEnd+yStart)/-2;

		//Graph's x,y start and end values
		p.println("x:", xStart, " to ", xEnd);
		p.println("y:", yStart, " to ", yEnd);
		p.println("scaleX:", scaleX);
		p.println("scaleY:", scaleY);


		p.background(background);
		//translate to centre
		p.translate(200, 200);
		//translate according to user customisations
		p.translate(translateX, -translateY);

		//axes
		if(axesOn) {
			p.stroke(axes);
			p.line(0, translateY+p.height/2, 0, translateY-p.height+p.height/2);//y - axis
			p.line(-p.height/2-translateX, 0, p.width-translateX-p.width/2, 0);//x - axis
		}

		//draw the function
		for (int i=0; i<graphs.size(); i++) {
			ArrayList points = (ArrayList) graphs.get(i);
			int[]gradientColors = (int[]) points.get(0);
			int colorStart = gradientColors[0];
			int colorEnd   = gradientColors[1];

			for (int j=1; j<points.size(); j++) {
				float[]point = (float[]) points.get(j);
				float x  = point[0];
				float y0 = point[1];
				float y1 = point[2];

				//gives a gradient colour
				p.stroke(p.lerpColor(colorStart,colorEnd,(x-xStart)/(xEnd-xStart)));

				p.point(x*scaleX, -y0*scaleY);
				p.point(x*scaleX, -y1*scaleY);
			}
		}


		//time-keeping
		p.println(p.millis()-m, "ms");

		if(save)p.save(imageName);
	}

	//MATH FUNCTIONS
	public float[]add(float[]x, float[]y){
		return new float[]{x[0]+y[0],x[1]+y[1]};
	}

	public float[]mult(float[]x, float[]y){
		if(x[0]+x[1]==0&&y[0]+y[1]==0)
			return new float[]{x[0]*y[0],x[1]*y[0]};
		else
			return new float[]{x[0]*y[0],x[1]*y[1]};
	}

	//eval
	public float[]eval(float x) {
		return new float[]{x,x};
	}

	//power
	//TODO: array parameters
	public float[]power(float x[], float e, boolean posAndNeg) {
		//posAndNeg tells us whether we want the negative square root or not
		//because x[0] is positive and x[1] is negative when powered
		float[]y;
		if (posAndNeg) {
			y = new float[]{p.pow(x[0],e),((1/e%2)*2-1)*p.pow(x[0],e)};
		} else {
			y = new float[]{p.pow(x[0],e),p.pow(x[0],e)};
		}
		return y;
	}

	//log base
	public float log_(float b, float x) {
		return p.log(x)/p.log(b);
	}
	//cosecant
	public float csc(float x) {
		return 1/p.sin(x);
	}
	//secant
	public float sec(float x) {
		return 1/p.cos(x);
	}
	//cotangent
	public float cot(float x) {
		return 1/p.tan(x);
	}


	//BEGIN SETTERS
	public void setXStartEnd(float start, float end) {
		xStart = start;
		xEnd   = end;
	}

	public void setAutoFit(boolean fit) {
		autoFit = fit;
	}

	public void setShowAxes(boolean show) {
		axesOn = show;
	}

	public void setBackgroundColor(int c) {
		background = c;
	}

	public void setAxesColor(int c) {
		axes = c;
	}

	public void setStartGradientColor(int c) {
		startPoints = c;
	}

	public void setEndGradientColor(int c) {
		endPoints = c;
	}

	//Graph options
	public void trig() {
		xStart     = -p.PI/2;
		xEnd       =  p.PI/2;
		yStart     =    -6;
		yEnd       =     6;
	}

	public void reset() {
		xStart     = -200;
		xEnd       =  200;
		yStart     = -200;
		yEnd       =  200;
		tune       = 0.01f;
	}
	//END SETTERS
}
