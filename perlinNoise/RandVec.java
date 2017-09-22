package perlinNoise;

import java.util.Random;

import vector.Vector;

public class RandVec extends Vector{
	public RandVec(int n){
		super(createCont(n));
		
		normalize();
	}
	
	private static double [] createCont(int n){
		double ret [] = new double[n];
		
		Random rand = new Random();
		
		for (int i = 0; i < n; i++)
			ret[i] = rand.nextDouble() - 0.5d;
		
		return ret;
	}
}
