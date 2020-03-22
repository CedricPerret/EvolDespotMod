package evoldespotmod1;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import org.apache.commons.math3.distribution.NormalDistribution;


public class Individual implements Comparable<Individual>{
	//Utility for calcul
	public Utility utility;
    //Fairness preference
    private double z;
    //Tolerance preference
    private double tau;
    //Mindset
    private double m;
    //Opinion
    private double o;
    //Influence
    private double alpha;
    //Fitness
    private double w;
    //List of ingoing neighbours
    private List<Individual> nVIn;
    //List of outgoing neighbours
    private List<Individual> nVOut;
    //Social position (= distance to leader)
    private int s;
    //Share 
    private double p;
    //Satisfaction
    private int dissatisfied;
    //Mutation rate
    private double mu;
    //Mutation effect
    private double sigma;
    
    
    public static int nbreIndividual = 0;
    public static int getNbreIndividual(){
        return nbreIndividual;
    }
    
    public Individual(){
        System.out.println("Creation of a default individual");
        z = -1;
        tau = -1;
        m = -1;
        o = -1;
        alpha = -1;
        w = -1;
        nVIn = new ArrayList<>();
        nVOut = new ArrayList<>();
        s = -1;
        p = -1;
        dissatisfied = -1;
        nbreIndividual++;
    }
    
    public Individual(Utility pUtility, double pZ, double pTau, double pM, double pO, double pAlpha, double pW,double pMu, double pSigma){
        utility = pUtility;
    	z = pZ;
        tau = pTau;
        m = pM;
        o = pO;
        alpha = pAlpha;
        w = pW;
        nVIn = new ArrayList<>();
        nVOut = new ArrayList<>();
        s = -1;
        p = -1;
        dissatisfied = -1;
        mu = pMu;
        sigma = pSigma;
        nbreIndividual++;
    }
    
    //Setters
    public void setZ(double pZ){z = pZ;}
    public void setTau(double pTau){tau = pTau;}
    public void setM(double pM){m = pM;}
    public void setO(double pO){o = pO;}
    public void setAlpha(double pAlpha){alpha = pAlpha;}
    //Never w below 0
    public void setW(double pW){if(pW>=0){w = pW;}else{w = 0;}}
    public void setS(int pS){s = pS;}
    public void setP(double pP){p = pP;}
    public void setDissatisfied(int pDissatisfied){dissatisfied = pDissatisfied;}

    
    //Getters
    public double getZ(){return z;}
    public double getTau(){return tau;}
    public double getM(){return m;}
    public double getO(){return o;}
    public double getAlpha(){return alpha;}
    public double getW(){return w;}
    public List<Individual> getNVIn(){return nVIn;}
    public List<Individual> getNVOut(){return nVOut;}
    public int getS(){return s;}
    public double getP(){return p;}
    public int getDissatisfied(){return dissatisfied;}
  

    
    public String description(){return "z = " + z + "tau = " + tau + "\nm = " + m +"\no = " + o +"\nalpha = " + alpha + "\nw = " + w + "\ns = " + s + "\np = " + p;}
    
    //Mutation method
    public void mutation(){
		if(this.utility.testProb(this.mu) == true){
			double min = 0.0;
			double max = 1.0;
			NormalDistribution mutDis = new NormalDistribution(this.z,this.sigma);
        	double res = mutDis.sample();
        	if(res <min){res = min;}
        	if(res > max){res = max;}
        	this.z = res;
		}
    }
    
	//Comparator nV
    @Override
    public int compareTo(Individual obj)
    {
        // compareTo returns a negative number if this is less than obj, 
        // a positive number if this is greater than obj, 
        // and 0 if they are equal.
        if(this.nVIn.size() < obj.nVIn.size())
          return 1;
        else if(obj.nVIn.size() < this.nVIn.size())
          return -1;
          return 0;
    }
    
}



    
