package org.concord.probe.transformers;

public class TransformersFactory{

public final static int TR_FFT			 	= 0;

    public static String [] transformersNames = {"FFT"};

	public static CCTransformer createTransformer(int trIndex){
		CCTransformer newTransformer = null;
		switch(trIndex){
			case TR_FFT:
				newTransformer = new CCFFTTransformer(transformersNames[TR_FFT]);
				break;
		}
		return newTransformer;
	}

 }
