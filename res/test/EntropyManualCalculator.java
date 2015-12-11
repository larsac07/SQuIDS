package no.uib.mof077.shortbytes.decisiontree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EntropyManualCalculator {

	public int lol;

	public static void main(String[] args) {
		int divider = 10;
		double[] values = new double[] {4, 3, 3};
		double entropy = calculateEntropy(divider, values);
		System.out.println("Entropy: " + entropy);
		double infoGain = calculateInformationGain(entropy,divider,new double[][] {{3,1,1},{1,2,2}});
		System.out.println("InfoGain: " + infoGain);
	}

	public static double calculateEntropy(double divider, double[] values) {
		double entropy = 0;
		for (double d : values) {
			entropy += -(d / divider) * (Math.log(d/divider) / Math.log(2));
		}
		File f = new File("asdf.txt");
		FileWriter fw;
		// These
		//Are
		//	orphan comments
		try {
			fw = new FileWriter(f);
			fw.write("adfg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			
		} finally {
			
		}
		return entropy;
	}
	
	public static double calculateInformationGain(double entropy, double divider, double[][] values) {
		double infoGain = entropy;
		for (int i = 0; i < values.length; i++) {
			infoGain -= (values.length/divider) * calculateEntropy(values.length, values[i]);
		}
		return infoGain;
	}
}
