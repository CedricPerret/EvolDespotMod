package evoldespotmod1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class ModelTolerance extends Thread {

	//Constructor
    public ModelTolerance(int pNIndIni, int pCliqueSize, double pPOutgroup, double pPClique, int pNGen, int pGenByOpinion, double pL,
			double pRevolutionThr, double pCostRevolution, double pMu, double pSigma, int pStepData,
			int pISimul, PrintWriter pPwPop, PrintWriter pPwMatrix, long pSeed) {
		super();
		this.nIndIni = pNIndIni;
		this.cliqueSize = pCliqueSize;
		this.pOutgroup = pPOutgroup;
		this.pClique = pPClique;
		this.nGen = pNGen;
		this.genByOpinion = pGenByOpinion;
		this.L = pL;
		this.revolutionThr = pRevolutionThr;
		this.costRevolution = pCostRevolution;
		this.mu = pMu;
		this.sigma = pSigma;
		this.stepData = pStepData;
		this.iSimul = pISimul;
		this.pwPop = pPwPop;
		this.pwMatrix = pPwMatrix;
		this.seed = pSeed;
		this.utility = new Utility(new Random(seed));
	}
    //Parameters
	private int nIndIni;
	private int cliqueSize;
	private double pOutgroup;
	private double pClique;
	private int nGen;
	private int genByOpinion;
	private double L;
    private double revolutionThr;
    private double costRevolution;
    private double mu;
    private double sigma;
    private int stepData;
    private int iSimul;
    private PrintWriter pwPop; 
    private PrintWriter pwMatrix;
    private long seed;
    public Utility utility;
    

	// Calcul social position-------------------------------------------------------
    //with s = range
    public int socialPosition(List<Individual> indList, Individual leader){
    	int i = 0;
        int s = 0;
        List<Individual> indListTemp1 = new ArrayList<>();
        List<Individual> indListTemp2 = new ArrayList<>();
    	//Choose Leader
        leader.setS(0);
        indListTemp1.add(leader);
        while(i == 0){
        	s++;
        	for(int j=0; j<indListTemp1.size();j++){
	        	for(int k=0; k<indListTemp1.get(j).getNVOut().size();k++){
	        		if(indListTemp1.get(j).getNVOut().get(k).getS() == -1){
	        			indListTemp1.get(j).getNVOut().get(k).setS(s);
	        			indListTemp2.add(indListTemp1.get(j).getNVOut().get(k));
	        		}else{
	        			continue;
	        		}
	        	}
        	}
        	if(indListTemp2.isEmpty()){i = 1;}
        	indListTemp1.clear();
        	indListTemp1 = utility.copyListInd(indListTemp2);
        	indListTemp2.clear();
        }
        //It does a last turn to check if empty
        return s-1;
    }
    
    //Calcul social position Directed
    public int socialPositionDirected(List<Individual> indList, int[] cliqueIndex, int leaderIndex){
    	//Set everybody to 2
    	for (int i = 0; i < indList.size(); i++) {
			indList.get(i).setS(2);
		}
    	//Reset leader to 0
    	indList.get(leaderIndex).setS(0);
    	//Reset clique to 1
        for (int i = 0; i < cliqueIndex.length; i++) {
			indList.get(cliqueIndex[i]).setS(1);
		}
        int s = 2;
        return s;
    }
   
    //Reproduction Moran Process Neighbors----------------------------------------------------------------
    public void reproMoranProcess(List<Individual> indList, byte revolution, Individual leader){
    	Individual deathInd;
    	Individual birthInd;
    	//Choose random individual to die
    	if(revolution == 1){deathInd = leader;}else{deathInd = indList.get(utility.randomSample(0, indList.size()-1));}
    	//Choose replacement in function of fitness
    	double[] fitnessArray = new double[deathInd.getNVIn().size()];
    	for (int i = 0; i < deathInd.getNVIn().size(); i++) {
			fitnessArray[i]= deathInd.getNVIn().get(i).getW();
		}
    	birthInd = indList.get(utility.randomSampleList(fitnessArray));
    	//Replace
    	deathInd.setZ(birthInd.getZ());
    	//Mutate
    	deathInd.mutation();
    }
    
  //Reproduction Moran Process Random
    public void reproMoranProcessRandom(List<Individual> indList, byte revolution, Individual leader, int nEvent, double[] fitnessArray){
    	Individual deathInd;
    	Individual birthInd;
    	int[] deathIndex = new int[nEvent];
    	List<Individual> deathIndList = new ArrayList<Individual>();
    	//Choose random individual to die (except leader if revolution)
    	deathIndex = utility.randomSampleList(indList.size(), nEvent);
    	//Build list of condemned
    	for (int i = 0; i < deathIndex.length; i++) {
			deathIndList.add(indList.get(deathIndex[i]));
		}	
    	//Do repro
    	for (int i = 0; i < deathIndList.size(); i++) {
			deathInd = deathIndList.get(i);
	    	//Choose replacement in function of fitness
	    	birthInd = indList.get(utility.randomSampleList(fitnessArray));
	    	//Replace
	    	deathInd.setZ(birthInd.getZ());
	    	//Mutate
	    	deathInd.mutation();
    	}
    	
    }
    
    //Formula for share
    public double expNegSample(double z, int s){
    	double res = Math.exp(-s*z*1);
    	return res;
    }
    
    @Override
    public void run(){
        //Initialization===========================================================
    	//Writing parameters
    	if(iSimul == 0){
            pwPop.write("nIndIni, cliqueSize, pOutgroup, pClique, nGen, genByOpinion, L, revolutionThr, costRevol, mu, sigma, stepData"+"\r\n" +
            			nIndIni + "," + cliqueSize + "," + pOutgroup + "," + pClique + "," + nGen + "," + genByOpinion + "," + L + "," + revolutionThr + "," + costRevolution + "," + mu + "," + sigma + "," + stepData + "\r\n" +
            			seed + "\r\n");
            pwPop.flush();}
    	//Table for populations
        List<Individual> pop = new ArrayList<>();
        //Table for clique index
        int[] cliqueIndex = new int[cliqueSize];
        
        //Initial population t=0
        for(int i=0; i<nIndIni; i++){                                
            pop.add(new Individual(utility,utility.randomDouble(),0,0,0,0.5,utility.randomDouble(),mu,sigma));
        }
        //Initial leader
        int leaderIndex = 0;
        //Initial clique
        for(int i=1; i<cliqueSize+1; i++){
        	cliqueIndex[i-1] = i;
        }
        
        //Network algorithm
        SocialNetwork socialNetwork1 = new SocialNetwork(utility, pop);
        socialNetwork1.networkGenerator_ErdosRenyiRandomGnp(cliqueIndex,leaderIndex,pOutgroup,pClique);
        
        //Writing adjacency matrix
        for(int i=0; i<pop.size(); i++){pwMatrix.write(",I" + i);}
        pwMatrix.write("\r\n");
    	for(int i=0; i<pop.size(); i++){
    		pwMatrix.write("I"+ i);
    		for (int j = 0; j < pop.size(); j++) {
				pwMatrix.write("," + socialNetwork1.matrixAdjacency[i][j] );
			}
    		pwMatrix.write("\r\n");
    	}
    	pwMatrix.flush();

        //Calcul social position + save sMax
        int sMax;
        sMax = this.socialPositionDirected(pop,cliqueIndex,leaderIndex);
        
      //Calcul of minimum share to normalize
        double pMin;
        double fTotMin = 0;
    	for (int j = 0; j < pop.size(); j++) {
    		fTotMin = fTotMin + expNegSample(1,pop.get(j).getS());
		}
    	pMin = (expNegSample(1,sMax))/fTotMin;
        //Simulations===============================================================
        double BTot;
        double fTot;
        byte revolution = 0;
        double dissatisfactionMean;
        double oInfluence;
        String resTemp="";
        double diffInfluence;
        double mTemp;
        int revolutionCount = 0;
        List<Integer> revolIndexList = new ArrayList();
    	double[] fitnessArray = new double[nIndIni];
    	double[] shareArray = new double[sMax+1];
    	
        for (int iGen = 0; iGen < nGen; iGen++) {
        	if((iGen%Math.round(nGen/10)) == 0){System.out.println("Simul " + seed + " Gen " + (iGen*100)/nGen + "%");} 
        	//Collective action-----------------------------------------------------
            //Total amount of ressources
        	BTot = 2*pop.size();
     
        	//Distribution of ressources--------------------------------------------
        	//Total to normalize
        	fTot = 0;
        	for (int j = 0; j < pop.size(); j++) {
        		fTot = fTot + expNegSample(pop.get(leaderIndex).getZ(),pop.get(j).getS());
			}
        	//Share in fct of social position
        	for (int s = 0; s <= sMax; s++) {
        		shareArray[s]=(expNegSample(pop.get(leaderIndex).getZ(),s))/(fTot);
			}
        	for (int j = 0; j < pop.size(); j++) {
        		//Calcul share
        		pop.get(j).setP(shareArray[pop.get(j).getS()]);
        	}
        	
        	//Opinion formation-----------------------------------------------------
	        	//Calcul mindset (bounded between 0 and 1)
	        	for (int j = 0; j < pop.size(); j++) {
	        		mTemp = (((1/((double)pop.size()))- pop.get(j).getP())/
	        				((1/((double)pop.size())) - pMin));
	      
	        		if(mTemp>1){mTemp = 1;}
	        		if(mTemp<0){mTemp = 0;}
	        		pop.get(j).setM(mTemp);
				}
	        	//Calcul opinion
	        	dissatisfactionMean = 0;
	        	for (int j = 0; j < pop.size(); j++) {
					oInfluence = 0;
					diffInfluence = 0;
					if(pop.get(j).getNVIn().size() <= 0){System.out.println("NVIN" + pop.get(j).getNVIn());}
					for (int k = 0; k < pop.get(j).getNVIn().size(); k++) {
						//diffInfluence = pop.get(j).getNV().get(k).getAlpha()-pop.get(j).getAlpha();
						//if(diffInfluence < 0.1){diffInfluence = 0.1;}		//Threshold of difference of influence
						oInfluence = oInfluence + pop.get(j).getNVIn().get(k).getM();
						
					}
					pop.get(j).setO(
							((L*pop.get(j).getM()) + oInfluence) / ((double)(pop.get(j).getNVIn().size())+L)
							);
					//Check satisfaction (1 dissatisfied, 0 satisfied)
					if(pop.get(j).getO()>pop.get(j).getZ()){
						pop.get(j).setDissatisfied(1);
						dissatisfactionMean++;
						//Calcul fitness
						pop.get(j).setW(BTot*pop.get(j).getP());
		        		revolIndexList.add(j);
					}else{
							pop.get(j).setDissatisfied(0);
							//Calcul fitness
			        		pop.get(j).setW(BTot*pop.get(j).getP());
					}
				}

	        	//Check revolution
	        	if(dissatisfactionMean/((double)pop.size())>revolutionThr){
	        		revolution = 1;
	        		revolutionCount++;
	        		for(int j=0; j< revolIndexList.size(); j++){
	        			//If revolution, we add cost to defiant individuals
	        			pop.get(j).setW((BTot*pop.get(j).getP()) - costRevolution);
	        			}
	        	}else{
	        		revolution = 0;
	        	}
	        	      		
	        
        	//Writing-------------------------------------------------------------
        	if(iGen==0 || (iGen+1)%stepData==0) {
        		for (int i = 0; i < pop.size(); i++) {
	                resTemp = resTemp + pop.get(i)
	                		+ ","
	                		+ pop.get(i).getZ()
			                +","
			                + pop.get(i).getNVOut().size()
			                +","
			                + pop.get(i).getNVIn().size()
			                +","
			                + pop.get(i).getM()
			                +","
			                + pop.get(i).getO()
			                +","
			                + pop.get(i).getP()
			                +","
			                + pop.get(i).getW()
			                +","
			                + pop.get(i).getS()
			                +","
			                + pop.get(i).getDissatisfied()
			                +","
			                + revolutionCount
			                +","
			                + iGen
			                +","
			                + iSimul     
			                + "\r\n";
        		}
        		pwPop.write(resTemp);
            	resTemp="";
            	revolutionCount = 0;
        	}
        	
        	//Revolution----------------------------------------------------------
        	if(revolution == 1){
        		leaderIndex = this.utility.randomSample(0,revolIndexList.size()-1);
        		cliqueIndex = this.utility.randomSampleOtherList(revolIndexList.size(), cliqueIndex.length, leaderIndex);
        		leaderIndex = revolIndexList.get(leaderIndex);
        		for (int i = 0; i < cliqueIndex.length; i++) {
					cliqueIndex[i] = revolIndexList.get(cliqueIndex[i]);
				}
        		//Rewire the network
        		socialNetwork1.clearNetwork();
        		socialNetwork1.networkGenerator_ErdosRenyiRandomGnp(cliqueIndex, leaderIndex, pOutgroup, pClique);
        		//Calcul social position + save sMax
        		sMax = this.socialPositionDirected(pop,cliqueIndex,leaderIndex);
            	revolution = 0;
        	}
        	//Empty list of revolutionaries
    		revolIndexList.clear();
        	//Reproduction---------------------------------------------------------
    		//Crate fitness array
    		for (int i = 0; i < pop.size(); i++) {
    			fitnessArray[i]= pop.get(i).getW();
    		}
    		for (int i = 0; i < genByOpinion; i++) {
    			this.reproMoranProcessRandom(pop, revolution, pop.get(leaderIndex),1,fitnessArray);
			}
        }
        System.out.println("Simul" + iSimul + "Over");
        pwPop.flush();
    }
}
