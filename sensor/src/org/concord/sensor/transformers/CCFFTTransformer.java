package org.concord.probe.transformers;

import org.concord.datapipe.*;
import org.concord.waba.extra.util.*;

public class CCFFTTransformer extends CCTransformer
{
	int 				dataDim = 128;
	float 				[]dataFFT = null;
	int					dataPointer = 0;
	public DataDesc		dDesc = new DataDesc();
	public DataEvent	dEvent = new DataEvent();
	float 				[]normKoeff = null;
	public CCFFTTransformer(String name){
		super(name);
		dDesc.setChPerSample(1);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setData(dataFFT);
		dEvent.setNumbSamples(dataDim/2);
	}

	public boolean dataArrived(DataEvent dataEvent){
		float[] data = dataEvent.getData();
		if(data == null) return false;
		float t0 = dataEvent.getTime();
		float dt = dataEvent.getDataDesc().getDt();
		int    chPerSample = dataEvent.getDataDesc().getChPerSample();

		int ndata = dataEvent.getNumbSamples()*chPerSample;
		int nOffset = dataEvent.getDataOffset();
		float  dtChannel = dt / (float)chPerSample;
		boolean doFFT = false;
		for(int i = 0; i < ndata; i+=chPerSample){
			float t = t0 + dtChannel*(float)i;
			if(!doFFT) dataFFT[dataPointer++] = data[nOffset+i];
			if(dataPointer >= dataDim){
				doFFT = true;
			}
		}
		if(doFFT){
			dataPointer = 0;
			float maxData = 0.0f;
			float summ = 0.0f;
			float summ2 = 0.0f;
			for(int k = 0; k < dataDim; k++){
				float d = dataFFT[k];
				summ += d;
				summ2 += d*d;
				if(maxData < d) maxData = d;
			}
			float ave = summ/dataDim;
			for(int k = 0; k < dataDim; k++){
				dataFFT[k] = dataFFT[k] - ave;
			}
			float disp = Maths.sqrt(summ2/(float)dataDim - ave*ave)/ave;
			org.concord.waba.extra.util.FFT.realft(dataFFT,dataDim,1);			
			
			//			float maxKoeff = 0.0f;
			for(int k = 1; k <= dataDim;k+=2){
				float nk = Maths.sqrt(dataFFT[k]*dataFFT[k]+dataFFT[k+1]*dataFFT[k+1]);
				if(k == 1) nk /= 2.0;
				normKoeff[(k - 1)/2] = nk;
				//				if(nk > maxKoeff) maxKoeff = nk;
			}
			notifyDataListeners(dataEvent);
		}
		
		
		
		return true;
	}
	public boolean idle(DataEvent e){
		return true;
	}
	public boolean startSampling(DataEvent e){
		if(dataFFT == null){
			dataFFT = new float[dataDim*2];
		}
		if(normKoeff == null){
			normKoeff = new float[dataDim/2];
		}
		dataPointer = 0;
		dDesc.setDt(1.0f/(dataDim*e.getDeltaT()));//frequency increment
		return true;
	}
}
