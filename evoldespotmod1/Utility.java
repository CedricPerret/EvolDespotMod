
package evoldespotmod1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Collections;


public class Utility {
	Random randomGenerator;
	
    public Utility(Random pRandomGenerator){
    	this.randomGenerator = pRandomGenerator;
    }
    
    //Random number
    public double randomDouble(){
    	return this.randomGenerator.nextDouble();
    }
    
    // Probability test
    public boolean testProb(double p){
        if(this.randomGenerator.nextDouble() > p){
            return false;                                                           //Nothing
        }
        else{
           return true;                                                            //Event        
        }
    }
    
    //Copy List
    public List<Individual> copyListInd(List<Individual> indList){
    	List<Individual> indListRes = new ArrayList();
    	for (int i = 0; i < indList.size(); i++) {
			indListRes.add(indList.get(i));
		}
    	return(indListRes);
    }
    
    //Random sample
    
    public int randomSample(int arrayMin, int arrayMax){
    	int indexRes = arrayMin + (int)(Math.floor(this.randomGenerator.nextDouble()*(1+arrayMax-arrayMin)));
    	return(indexRes);
    }
    
    //Random sample other (polymoirphise, surchage?)
    public int randomSampleOther(int arrayLength, int index){
        if(arrayLength == 1){System.out.println("BUG randomSampleOther List too short");}
        ArrayList<Integer> resList = new ArrayList();
        for(int i=0; i<arrayLength; i++){resList.add(i);}
        resList.remove(index);
        int indexRes = (int)(Math.floor(this.randomGenerator.nextDouble()*(arrayLength-1)));
        return resList.get(indexRes);
        }
    
    
    //Random sampleSize other
    public int[] randomSampleOtherList(int arrayLength, int sampleSize, int index){
        if(arrayLength == 1){System.out.println("BUG randomSampleOther List too short");}
        ArrayList<Integer> resList = new ArrayList();
        for(int i=0; i<arrayLength; i++){resList.add(i);}
        resList.remove(index);
        int indexTemp;
        int[] indexL = new int[sampleSize];
        for(int i=0; i<sampleSize; i++){
            indexTemp = (int)(Math.floor(this.randomGenerator.nextDouble()*(resList.size())));
            indexL[i]= resList.get(indexTemp);
            resList.remove(indexTemp);
        }
        return indexL;
        }
    //Random sampleSize
    public int[] randomSampleList(int arrayLength, int sampleSize){
        if(arrayLength == 1){System.out.println("BUG randomSampleOther List too short");}
        ArrayList<Integer> resList = new ArrayList();
        for(int i=0; i<arrayLength; i++){resList.add(i);}
        int indexTemp;
        int[] indexL = new int[sampleSize];
        for(int i=0; i<sampleSize; i++){
            indexTemp = (int)(Math.floor(this.randomGenerator.nextDouble()*(resList.size())));
            indexL[i]= resList.get(indexTemp);
            resList.remove(indexTemp);
        }
        return indexL;
        }
    
    //Sample in list
    public int randomSampleList(double[] array){
    	double[] array2 = new double[array.length];
    	array2[0] = array[0];
    	for (int i = 1; i < array2.length; i++) {
			array2[i] = array[i] + array2[i-1];
		}
    	double indexSample = this.randomGenerator.nextDouble()*array2[array2.length-1];
    	return(probSample(array2,indexSample));
    	
    }

    public int probSample(double[] pArrayProb, double pKey){             //Binary search algorithm for sampling probability
    	int lower = 0;
        int upper = pArrayProb.length-1;
        int mid;
        while (lower < upper){ 
            mid = (int)Math.floor((lower + upper )/2);   
            if((pArrayProb[mid] - pKey) > 0){
                upper = mid;
            }
            else{
                lower = mid+1;
            }
        }
        return lower;
    }
    
    
    
}
