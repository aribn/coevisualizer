package coeviz.framework;

import java.util.*;
import coeviz.framework.interfaces.*;

public abstract class TeachersDilemma implements Game, Renderable  {

	public static final int outcomeScale = 100;  

	private double V = 0.0;
	private double J = 1.0; 
	private double R = 1.0; 
	private double C = 0.0;
	// SAVE!!!!!
	// TD. 
	// public static final double[] vjrc = { 0, 1, 1, 0 };
	// competition. 
	// private static final double[] vjrc = { 0, 0, 1, 1 };
	// cooperation. 
	// private static final double[] vjrc = { 1, 1, 0, 0 };
	// ask harder: converges quickly with pop 1, 25. mem off. mutation rate:0.1, bias:0, size:1.0. 
	// private static final double[] vjrc = { 0, 1, 0, 1 };
	// ask easier: converges quickly with pop 1, 25. mem off. mutation rate:0.1, bias:0, size:1.0. 
	// private static final double[] vjrc = { 1x, 0, 1, 0 };
	//TD async
	// private static final double[] vjrc = { 0, 0.5, 1, 0 };

	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
		ht.put("V", ""+V); 
		ht.put("J", ""+J); 
		ht.put("R", ""+R); 
		ht.put("C", ""+C); 
		return ht; 
	}
	
	public void setExperimentalVariables(Hashtable ht) {
		try { V = Double.parseDouble((String)ht.get("V")); } catch (Exception e) { System.out.println("Using default value for V"); }
		try { J = Double.parseDouble((String)ht.get("J"));} catch (Exception e) { System.out.println("Using default value for J"); }
		try { R = Double.parseDouble((String)ht.get("R"));} catch (Exception e) { System.out.println("Using default value for R"); }
		try { C = Double.parseDouble((String)ht.get("C"));} catch (Exception e) { System.out.println("Using default value for C"); }
	}
	

	
	public final int[] outcomesInOrder() {
		int [] outcomes = new int[outcomeScale+1]; 
		for (int i=0; i<outcomes.length; i++) outcomes[i] = i;
		return outcomes; 
	}
	
	public final java.awt.Color outcomesInColor(int outcome) {
		float c = new Double(1.0 * outcome / outcomeScale).floatValue(); 
		return new java.awt.Color(c,c,c); 
	}
	
	public final int neutralOutcome() {
		return (outcomeScale/2); 
	}
		
	public final int evaluateCandidate(Candidate c, Test t, Random r) {
		//System.out.println("evaluateCandidate called");
		return (int) Math.round(evaluate(c,t,r)[0] * outcomeScale); 
	}
	
	public final int evaluateTest(Candidate c, Test t, Random r) {
		//System.out.println("evaluateTest called");
		return (outcomeScale - (int) Math.round(evaluate(c,t,r)[1] * outcomeScale)); 
	}

	private final double[] evaluate(Candidate c, Test t, Random r) {
		return generatePayoffs(assessAccuracy(c,t,r), assessDifficulty(c,t,r)); 
	}
	
	private final double[] generatePayoffs(boolean isAccurate, double difficulty) {
		double[] tutee_vjrc = { 1.0, 1.0, 0.0, 0.0 };
		double payoff_tutor, payoff_tutee; 
		
		double probCorrect = 1 - difficulty; 
		
		if (isAccurate) {
			payoff_tutee = (probCorrect*tutee_vjrc[0]) + ((1.0-probCorrect)*tutee_vjrc[1]);
			payoff_tutor = (probCorrect* V) + ((1.0-probCorrect)* J); 
		} else {
			payoff_tutee = (probCorrect*tutee_vjrc[2]) + ((1.0-probCorrect)*tutee_vjrc[3]); 
			payoff_tutor = (probCorrect*R) + ((1.0-probCorrect)* C);
		}
		return new double[] {payoff_tutee, payoff_tutor};
	}
	
	public abstract double assessDifficulty(Candidate c, Test t, Random r);
	public abstract boolean assessAccuracy(Candidate c, Test t, Random r);
}





//////////////////////////////////

// for repeated evaluations: 
/*
 double[] sum_payoffs = {0.0, 0.0}; 
 for (int i=0; i<responseCount; i++) {
	 boolean isAccurate = assessAccuracy(c,t,r); 
	 
	 double payoffs[] = generatePayoffs(isAccurate, x); 
	 sum_payoffs[0] += payoffs[0]; 
	 sum_payoffs[1] += payoffs[1]; 
 }
 double[] avg_payoffs = {(sum_payoffs[0]/responseCount), (sum_payoffs[1]/responseCount)}; 
 
 return avg_payoffs; 
 */
