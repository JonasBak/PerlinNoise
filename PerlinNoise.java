package perlinNoise;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import vector.Vector;
import vector.VectorCalc;

public class PerlinNoise {
	protected Vector [] vecs;
	
	public double [] noise;
	
	protected final int x, y, sx, sy;
	
	
	public PerlinNoise(int x, int y, int sx, int sy){
		if (x < 1 || y < 1 || sx < 1 || sy < 1)
			throw new IllegalArgumentException("");
		
		//TODO: siste rad/kolonne er repeat
		
		this.x = x + 1;
		this.y = y + 1;
		this.sx = sx;
		this.sy = sy;
		
		System.out.println("Generating vectors");
		generateVectors();
		System.out.println("Generating noise");
		generateNoise();
		System.out.println("Perlin Noise generated");
	}
	
	public double[] getNoise() {
		return noise.clone();
	}
	
	protected void generateVectors(){
		vecs = new Vector [x * y];
		for (int i = 0; i < vecs.length; i++){
			vecs[i] = new RandVec(2);
		}
	}
	
	protected void generateNoise(){
		noise = new double[sx * sy * (x - 1) * (y - 1)];
		System.out.println("Noise length: " + noise.length);
		int i = 0;
		for (double y = 0; y < this.y - 1; y += 1.0d / sy){
			for (double x = 0; x < this.x - 1; x += 1.0d / sx){
			
				//System.out.println(x + " " + y);
				noise[i++] = get(x, y) / 2.0d + 0.5d;
			}
		}
		
	}
	
	protected Vector getVector(int x, int y){
		return vecs[x + y * this.x];
	}
	
	protected double get(double x, double y){
		int x0 = (int)x;
		int x1 = x0 + 1;
		int y0 = (int)y;
		int y1 = y0 + 1;
		
		if (x0 < 0 || x1 > this.x || y0 < 0 || y1 > this.y)
			throw new IllegalArgumentException(x0 + " " + x1 + " " + y0 + " " + y1);

		Vector point = new Vector(x - x0, y - y0);
		
		Vector tl = getVector(x0, y0);
		Vector tr = getVector(x1, y0);
		Vector bl = getVector(x0, y1);
		Vector br = getVector(x1, y1);

		Vector dtl = new Vector(x - x0, y - y0);
		Vector dtr = new Vector(x - x1, y - y0);
		Vector dbl = new Vector(x - x0, y - y1);
		Vector dbr = new Vector(x - x1, y - y1);

		double i0 = VectorCalc.dot(tl, dtl);
		double i1 = VectorCalc.dot(tr, dtr);
		double i2 = VectorCalc.dot(bl, dbl);
		double i3 = VectorCalc.dot(br, dbr);
		
		Vector uv = new Vector(fade(point.get(0)), fade(point.get(1)));

		double a1 = lerp(i0, i1, uv.get(0));
		double a2 = lerp(i2, i3, uv.get(0));
		
		
		
		return  lerp(a1, a2, uv.get(1));
	}
	

	public double lerp(double a, double b, double x) {
	    return a + x * (b - a);
	}
	
	public static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < noise.length; i++)
			ret += noise[i] + ((i + 1) % ((x - 1) * sx) == 0 ? "\n":" ");
		
		return ret;
	}
	
	public void toImage(String name){
		int height = (x - 1) * sx;
		int width = (y - 1) * sy;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 

		System.out.println("Saving to image " + name + ".bmp");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
		    	int i = x + y * width;
		    	int c = (int)(noise[i] * 255);
		    	int rgb = c;
		    	rgb = (rgb << 8) + c; 
		    	rgb = (rgb << 8) + c;
		    	image.setRGB(x, y, rgb);
		     }
		}
		
		File outputFile = new File(name + ".bmp");
		try {
			ImageIO.write(image, "bmp", outputFile);
			System.out.println("yay");
		} catch (IOException e) {
			System.out.println("nay");
			e.printStackTrace();
		}
	}
	
	public void toFile(String name){
		int width = (y - 1) * sy;

		int height = (x - 1) * sx;
		
		String cont [] = new String[height];
		int h = 0;
		
		cont[0] = "";
		for (int i = 0; i < noise.length; i++){
			cont[h] += ((int)(noise[i] * 255)) + ",";
			if ((i + 1) % width == 0 && h + 1 != height){
				h++;
				cont[h] = "";
			}
		}
		
		try{
		    PrintWriter writer = new PrintWriter(name + ".txt", "UTF-8");
		    for (String s : cont)
		    	writer.println(s);
		    
		    writer.close();
		} catch (IOException e) {
		   // do something
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PerlinNoise pn = new PerlinNoise(64, 64, 64, 64);
		
		//System.out.println(pn);
		pn.toImage("mc");
		pn.toFile("mc");
	}
	
}

