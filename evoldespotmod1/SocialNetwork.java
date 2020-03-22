package evoldespotmod1;

import java.util.Arrays;
import java.util.List;

public class SocialNetwork {
	//Utility
	public Utility utility;
	//List of individuals
	public List<Individual> indList;
	//Matrix adjacency
	public int[][] matrixAdjacency;
	
	
    public SocialNetwork(Utility pUtility, List<Individual> pIndList){
    	this.utility = pUtility;
    	this.indList = pIndList;
    	//Create matrix with no edges (0 everywhere)
    	matrixAdjacency = new int[indList.size()][indList.size()];
    	for(int[] row : matrixAdjacency){
    		Arrays.fill(row,0);
    	}
    };
    
    
    
    //Connect (two way)
    public void connect(int x, int y){
    	this.connectDirected(x, y);
    	this.connectDirected(y, x);
    }
    
    //Connect (directed)
    public void connectDirected(int x, int y){
    	if(this.indList.get(x).getNVOut().indexOf(this.indList.get(y)) == -1){
    		this.indList.get(x).getNVOut().add(this.indList.get(y));
    	}
    	if(this.indList.get(y).getNVIn().indexOf(this.indList.get(x)) == -1){
    		this.indList.get(y).getNVIn().add(this.indList.get(x));
    	}
    		this.matrixAdjacency[x][y]=1;
    }
    
    
    //For disconnected nodes, had one random node
    public void checkEmpty(List<Individual> indList){
        for(int i=0; i<indList.size(); i++){
            if(indList.get(i).getNVOut().isEmpty()==true){
                this.connectDirected(i,utility.randomSampleOther(indList.size(),i));
            }
            if(indList.get(i).getNVIn().isEmpty()==true){
                this.connectDirected(utility.randomSampleOther(indList.size(),i),i);
            }
        }
    }
    
    public void connectClique(int[] cliqueIndexList, int leaderIndex){
    	for(int i = 0; i < cliqueIndexList.length; i++){
    		this.connect(leaderIndex,cliqueIndexList[i]);
    	}
    	for (int i = 0; i < cliqueIndexList.length; i++) {
			for (int j = 0; j < cliqueIndexList.length; j++) {
				//No loop
				if(cliqueIndexList[i]==cliqueIndexList[j]){continue;}
				//We use connect even if directed because clique is fully connected
				this.connect(cliqueIndexList[i],cliqueIndexList[j]);
			}
		}
    }
    
    public void clearNetwork (){
    	for (int i = 0; i < this.indList.size(); i++) {
			indList.get(i).getNVIn().clear();
			indList.get(i).getNVOut().clear();
			//Clear Matrix (Disabled to earn computation time)
			//for(int j = 0; j < this.indList.size(); j++){
			//	this.matrixAdjacency[i][j]=0;
			//}
		}
    }
    //List network algortihms-----------------------------------------------------------------------
    
  //Directed Erdos Renyi random graph G(n,p)
    public void networkGenerator_ErdosRenyiRandomGnp(int[] cliqueIndexList, int leaderIndex, double pOutgroup, double pClique){
    	byte clique;
    	//Connect clique
    	this.connectClique(cliqueIndexList,leaderIndex);
    	//Connect new nodes with fixed probability p
        for(int i=1; i< this.indList.size();i++){
        	clique = 0;
        	//Test if the i is from the clique
        	for(int j = 0; j < cliqueIndexList.length; j++){
        		if(i==cliqueIndexList[j]){clique=1; continue;}
        	}
        	//Connect
        	for (int j = 0; j < this.indList.size(); j++) {
        		//No loop
        		if(i==j){continue;}
        		if(clique == 1){
            		if(this.utility.testProb(pClique)==true){this.connectDirected(i,j);}
            		}
            	else{
            		if(this.utility.testProb(pOutgroup)==true){this.connectDirected(i,j);}
            	}
			}
            //Check if empty (then connect to random one)
        }
        this.checkEmpty(this.indList);
    }
    
    

    
    
}
