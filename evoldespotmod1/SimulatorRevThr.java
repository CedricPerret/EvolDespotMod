
package evoldespotmod1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class SimulatorRevThr extends Thread {

	//Constructor
    public SimulatorRevThr(int pNIndIni, int pCliqueSize, double pPOutgroup, double pPClique, double pTauIni, double pL,
			double pRevolutionThr, double pStepData,
			int pISimul, PrintWriter pPwPop, PrintWriter pPwMatrix, long pSeed) {
		super();
		this.nIndIni = pNIndIni;
		this.cliqueSize = pCliqueSize;
		this.pOutgroup = pPOutgroup;
		this.pClique = pPClique;
		this.tauIni = pTauIni;
		this.L = pL;
		this.revolutionThr = pRevolutionThr;
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
	private double tauIni;
	private double L;
    private double revolutionThr;
    private double stepData;
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
    	//Choose Leader (now random leader)
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
    	for (int i = 0; i < indList.size(); i++) {
			indList.get(i).setS(2);
		}
    	indList.get(leaderIndex).setS(0);
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
    
    

    @Override
    public void run(){
        //Initialization===========================================================
    	//Writing parameters
    	if(iSimul == 0 && pClique == 0.01 && pOutgroup == 0.01){
            pwPop.write("nIndIni, cliqueSize,  nGen, genByOpinion, tauIni, L, revolutionThr, mu, sigma, stepData"+"\r\n" +
            			nIndIni + cliqueSize + tauIni + L + revolutionThr + stepData + "\r\n" +
            			seed + "\r\n");
            pwPop.flush();}
        //Table for populations
        List<Individual> pop = new ArrayList<>();
        //Table for clique index
        int[] cliqueIndex = new int[cliqueSize];
        
        //Initial population t=0
        for(int i=0; i<nIndIni; i++){                                
            pop.add(new Individual(utility,0,tauIni,0,0,0.5,utility.randomDouble(),0,0));
        }
        //Initial leader
        int leaderIndex = 0;
        //Initial clique
        for(int i=1; i<cliqueSize+1; i++){
        	cliqueIndex[i-1]=i;
        }
        
        
        //Network algorithm
        SocialNetwork socialNetwork1 = new SocialNetwork(utility, pop);
        socialNetwork1.networkGenerator_ErdosRenyiRandomGnp(cliqueIndex,leaderIndex,pOutgroup,pClique);
        
        //Writing adjacency matrix
        /*
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
        */
        //"\r\n"
    	
        //Calcul social position + save sMax
        int sMax;
        sMax = this.socialPositionDirected(pop,cliqueIndex,leaderIndex);
        
      //Calcul of minimum share to normalize
        double pMin;
        double fTotMin = 0;
    	for (int j = 0; j < pop.size(); j++) {
    		fTotMin = fTotMin + Math.exp(-pop.get(j).getS()*1);
		}
    	pMin = Math.exp(-sMax*1)/fTotMin;
        //Simulations===============================================================
        double fTot;
        byte revolution = 0;
        double dissatisfactionMean;
        double oInfluence;
        String resTemp="";
        double diffInfluence;
        double mTemp;
        for (double iStep = 0.; iStep <= 1; iStep = iStep+stepData) {
        	if((iStep%Math.round(1/10)) == 0){System.out.println("Simul " + seed + " Gen " + (iStep*100)/1 + "%");} 
        	//Set up leader z------------------------------------------------------
        	pop.get(leaderIndex).setZ(iStep);
        	//Distribution of ressources--------------------------------------------
        	//Total to normalize
        	fTot = 0;
        	for (int j = 0; j < pop.size(); j++) {
        		fTot = fTot + Math.exp(-pop.get(j).getS()*pop.get(leaderIndex).getZ());
			}
        	for (int j = 0; j < pop.size(); j++) {
        		//Calcul share
        		pop.get(j).setP((Math.exp(-pop.get(j).getS()*pop.get(leaderIndex).getZ()))/(fTot));
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
					for (int k = 0; k < pop.get(j).getNVIn().size(); k++) {
						//diffInfluence = pop.get(j).getNV().get(k).getAlpha()-pop.get(j).getAlpha();
						//if(diffInfluence < 0.1){diffInfluence = 0.1;}		//Threshold of difference of influence
						oInfluence = oInfluence + pop.get(j).getNVIn().get(k).getM();
						
					}
					pop.get(j).setO(
							((L*pop.get(j).getM()) + oInfluence) / ((double)(pop.get(j).getNVIn().size())+L)
							);
					//Check satisfaction (1 dissatisfied, 0 satisfied)
					if(pop.get(j).getO()>pop.get(j).getTau()){pop.get(j).setDissatisfied(1);dissatisfactionMean++;}else{pop.get(j).setDissatisfied(0);}
				}
	        	//Check revolution
	        	if(dissatisfactionMean/((double)pop.size())>revolutionThr){revolution = 1;}else{revolution = 0;}
        	
        	//Writing-------------------------------------------------------------
        	if(revolution==1 || iStep > 0.99) {
	                resTemp = resTemp
	                		+ pop.get(leaderIndex).getZ()
			                +","
			                + pClique
			                +","
			                + pOutgroup
			                +","
			                + iSimul     
			                + "\r\n";
        		pwPop.write(resTemp);
            	resTemp="";
        		break;
        	}
        }
        pwPop.flush();
 
    }
    
    
}
