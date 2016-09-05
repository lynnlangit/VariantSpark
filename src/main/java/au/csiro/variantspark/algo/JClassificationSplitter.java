package au.csiro.variantspark.algo;

import java.util.Arrays;

import au.csiro.variantspark.metrics.Gini;


/**
 * Fast gini based splitter.
 * NOT MULITHEADED !!! (Caches state to avoid heap allocations)
 * 
 * @author szu004
 *
 */
public class JClassificationSplitter {
	private final int[] leftSplitCounts = new int[2];
	private final int[] rightSplitCounts = new int[2];
	private final double[] leftRigtGini = new double[2];
	private final int[] labels;
	private final int nCategories;
		
	public JClassificationSplitter(int[] labels, int nCategories) {
		this.labels = labels;
		this.nCategories = nCategories;
	}
	
	double gini(int[] counts) {
		switch(counts.length) {
			case 0: return 0.0;
			case 1: return 0.0;
			case 2: {
				int total = counts[0]  + counts[1];
				if (total == 0) return 0.0;
				double p0 = counts[0], p1 = counts[1], pt = total;
				return 1.0 - (p0*p0 + p1*p1)/(pt*pt);
			}
			case 3: {
				int total = counts[0]  + counts[1] + counts[2];
				if (total == 0) return 0.0;
				double p0 = counts[0], p1 = counts[1], p2=counts[2], pt = total;
				return 1.0 - (p0*p0 + p1*p1 + p2*p2)/(pt*pt);
			}	
			case 4: {
				int total = counts[0]  + counts[1] + counts[2] + counts[3];
				if (total == 0) return 0.0;
				double p0 = counts[0], p1 = counts[1], p2=counts[2], p3=counts[3], pt = total;
				return 1.0 - (p0*p0 + p1*p1 + p2*p2 + p3*p3)/(pt*pt);
			}
			default: throw new RuntimeException("Not imlemented");
		}
	 }
	
	// TODO (Add this only works for two classes !!!) 
	double splitGini(int[] left, int[] right, double[] out) {
		 int lt = left[0] + left[1];
		 int rt = right[0] + right[1];	
		 double leftGini = gini(left);
		 double rightGini = gini(right);
		 out[0] = leftGini;
		 out[1] = rightGini;
		 return (leftGini*lt + rightGini*rt)/(lt+rt);
	 } 
	 
	 public SplitInfo findSplit(double[] data,int[] splitIndices) {	
	    SplitInfo result = null;
	    double minGini = 1.0;
	    // let's think about like this 
	    // on the first pass we both calculate the splits as well as determine which split points are actually present
	    // in this dataset
	    // as 0 are most likely we will  do 0 as the initial pass

	    long splitCandidateSet = 7L; 
//		for(int i:splitIndices) {
//			splitCandidateSet|=(1 << (int)data[i]);
//		}
		
		int sp  = 0;
		while(splitCandidateSet != 0L) {
			while (splitCandidateSet != 0L && (splitCandidateSet & 1) == 0) {
				sp ++;
				splitCandidateSet >>= 1;
			}
			splitCandidateSet >>= 1;
			// only run if there is at least one more index to try in this subset
			if (splitCandidateSet != 0L) {
				Arrays.fill(leftSplitCounts, 0);
				Arrays.fill(rightSplitCounts, 0);
				for(int i:splitIndices) {
					if ((int)data[i] <=sp) {
						leftSplitCounts[labels[i]]++;
					} else {
						rightSplitCounts[labels[i]]++;					
					}
				}
				double g = splitGini(leftSplitCounts, rightSplitCounts, leftRigtGini);
				if (g < minGini ) {
					result = new SplitInfo(sp, g, leftRigtGini[0], leftRigtGini[1]);
					minGini = g;
				}
				sp++;
			}
		}
		return result;
	 }
}
