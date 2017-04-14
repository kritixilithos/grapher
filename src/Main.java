import processing.core.PApplet;

import kritixilithos.grapher.*;

public class Main extends PApplet {
	Grapher grapher;

	public static void main(String[] args) {
		PApplet.main("Main");
	}

	public void settings() {
		size(400, 400);
	}

	public void setup() {
		grapher = new Grapher(this);
		grapher.setXStartEnd(-3, 3);
		grapher.addPoints(x -> grapher.mult(grapher.power(grapher.eval(2.f), x, false), grapher.eval(cos(PI * x))));
		grapher.addPoints(x -> grapher.power(grapher.eval(6-x*x), 0.5f, true), color(255, 0, 0), color(255, 255, 0));
		grapher.init();
	}
}