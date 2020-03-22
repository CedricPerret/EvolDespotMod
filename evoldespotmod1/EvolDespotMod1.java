package evoldespotmod1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvolDespotMod1 {

    public static void main(String[] args) {

        //Repository for results
        String wd = "C:/Users/40011091/Phd-Thèse/A1-Projects/EvolDespotMod/D. Results/GT version/";
    	//String wd = "H:/DespotLeadMod/";
        String nameFile = "";
        long pSeed;
        PrintWriter pwPop = null;
        PrintWriter pwMatrix = null;
        PrintWriter pwSeed = null;
        
        int iSimul;
        int nSimul = 5;
        int nThread = 2;
        
        /* Random network m0 = 2, p = pClique
         * Centralized network m0 = cliqueSize, pClique > p
         * Centralized network + outgroup, m0 = clique Size, pClique = p
         */
        
		      //NameFile
		        nameFile = wd+ "ModelTol_pOut0.01_pCli0.01_costRevol0.5_genByOpi100_revThr0.1_N1000_GT" +
		                "";
		        //Writers
		        try {pwPop = new PrintWriter(new FileWriter(nameFile + ".txt"));} 
		        catch (IOException ex) {System.out.println(ex);}
		        try {pwMatrix = new PrintWriter(new FileWriter(nameFile + "_matrix" + ".txt"));} 
		        catch (IOException ex) {System.out.println(ex);}
		        try {pwSeed = new PrintWriter(new FileWriter(nameFile + "_seed" + ".txt"));} 
		        catch (IOException ex) {System.out.println(ex);}
		        
		        //Simulations
		        iSimul=0;
			    while(iSimul < nSimul){
			    	pSeed = System.currentTimeMillis() + (long)(iSimul*1000);
			      	pwSeed.write(iSimul + "," + pSeed + "\r\n") ;
			          if(java.lang.Thread.activeCount()<nThread){
			      new Thread(new ModelTolerance(
			      		/*nIndIni = */ 1000,
			                      /* Random network Clique Size = 1, pOutgroup = pClique, leader connected
			                       * Random Network, pOutGroup = pClique,
			                       * Centralized network m0 = cliqueSize, pClique > pOutgroup
			                       * Centralized network + outgroup, m0 = clique Size, pClique = pOutgroup
			                       */
			              /*cliqueSize=*/ 50,
			              /*pOutgroup=*/ 0.01,
			              /*pClique=*/ 0.01,
			              /*nGen=*/ 500000,
			              /*genByOpinion=*/ 100,
			              /*L=*/ 1,
			              /*revolutionThr=*/ 0.1,
			              /*costRevolution=*/ 0.5,
			              /*mu=*/ 0.01,
			              /*sigma=*/ 0.01,
			              /*stepData=*/ 50000,
			              iSimul,
			              /*pwPop = */ pwPop,
			              /*pwMatrix = */ pwMatrix,
			              /*pSeed = */ pSeed
			          )).start();
			      iSimul++;}else{try {
			          java.lang.Thread.sleep(10000);
			         } catch (InterruptedException ex) {
			             Logger.getLogger(EvolDespotMod1.class.getName()).log(Level.SEVERE, null, ex);
			         }
			      }
			    }
			      pwSeed.flush();
			      		
        
//        //Simulator Revolution Threshold simul===================================================
//        
//      //NameFile
//      nameFile = wd + "SimulatorRevThr_iClique0.05max0.5_iOutgroup0.01max0.1_tau0.25_revThr0.1_N500";
//      
//      //Writers
//      try {pwPop = new PrintWriter(new FileWriter(nameFile + ".txt"));} 
//      catch (IOException ex) {System.out.println(ex);}
//      try {pwMatrix = new PrintWriter(new FileWriter(nameFile + "_matrix" + ".txt"));} 
//      catch (IOException ex) {System.out.println(ex);}
//      try {pwSeed = new PrintWriter(new FileWriter(nameFile + "_seed" + ".txt"));} 
//      catch (IOException ex) {System.out.println(ex);}
//      
//
//        
//        for(double iPClique = 0.05; iPClique<=0.5; iPClique = iPClique + 0.05){
//        	System.out.println("Progress = " + (int)(iPClique*(100/0.5)) + "%");
//        	for(double iPOutgroup = 0.01; iPOutgroup<=0.1; iPOutgroup = iPOutgroup + 0.01){
//
//		        iSimul=0;
//		        while(iSimul < nSimul){
//		        	pSeed = System.currentTimeMillis() + (long)(iSimul*1000);
//		        	pwSeed.write(iSimul + "," + pSeed + "\r\n") ;
//		            if(java.lang.Thread.activeCount()<8){
//				        new Thread(new SimulatorRevThr(
//				        		/*nIndIni = */ 500,
//				                        /* Random network Clique Size = 1, pOutgroup = pClique, leader connected
//				                         * Random Network, pOutGroup = pClique,
//				                         * Centralized network m0 = cliqueSize, pClique > pOutgroup
//				                         * Centralized network + outgroup, m0 = clique Size, pClique = pOutgroup
//				                         */
//				                /*cliqueSize=*/ 25,
//				                /*pOutgroup=*/ iPOutgroup,
//				                /*pClique=*/ iPClique,
//				                /*tauIni = */ 0.25,
//				                /*L=*/ 1,
//				                /*revolutionThr=*/ 0.1,
//				                /*stepData=*/ 0.001,
//				                iSimul,
//				                /*pwPop = */ pwPop,
//				                /*pwMatrix = */ pwMatrix,
//				                /*pSeed = */ pSeed
//				            )).start();
//				        	iSimul++;}
//		            else{try {
//		            		java.lang.Thread.sleep(100);
//				        } catch (InterruptedException ex) {
//				               Logger.getLogger(EvolDespotMod1.class.getName()).log(Level.SEVERE, null, ex);
//				        }
//				    }
//		        }
//		        pwSeed.flush(); 
//        	}
//        	
//       }
               
        pwSeed.close();

        
    }
    


}
