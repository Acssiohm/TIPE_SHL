import com.zakgof.velvetvideo.IAudioDecoderStream;
import com.zakgof.velvetvideo.IAudioFrame;
import com.zakgof.velvetvideo.IDemuxer;
import com.zakgof.velvetvideo.IVelvetVideoLib;
import com.zakgof.velvetvideo.impl.VelvetVideoLib;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.min;

public class SoundExtracting {

	public SoundExtracting() {
		try {
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File("video.wav"));

			// Display information about the wav file
			wavFile.display();

			// Get the number of audio channels in the wav file
			int numChannels = wavFile.getNumChannels();

			// Create a buffer of 100 frames
			double[][] buffer = new double[numChannels][100];

			ArrayList<Double> values = new ArrayList<>();

			int framesRead = -1;
			while (framesRead != 0) {
				framesRead = wavFile.readFrames(buffer, 100);
				for( int i = 0; i < framesRead ; i++){
					values.add(buffer[0][i]);
				}
			}

			wavFile.close();

			int V = 10;
			int N = values.size()/V;

			double [] E = new double[N];
			for (int n = 0; n < N ; n++){
				E[n] = 0;
				for(int i = 0; i < V; i++){
					E[n] += values.get(n * V + i)*values.get(n * V + i);
				}
				E[n] /= V;
			}

			int L = 10;
			double max_AE = 0;
			double mean_AE = 0;
			double [] AE = new double[N];
			for (int n = 0; n < N; n++){
				AE[n] = 0;
				int Lp = min(N-n, L);
				for(int l = 0; l < Lp; l++){
					AE[n] += E[n+l];
				}
				AE[n] /= Lp;
				if(AE[n] > max_AE){
					max_AE = AE[n];
				}
				mean_AE += AE[n]/N;
			}

			double [] NE = new double[N];
			for (int i = 0; i < N; i++){
				NE[i] = AE[i]/max_AE;
			}
			double Paudio = 6*mean_AE/max_AE;

			boolean [] phi = new boolean[N];
			for (int n = 0; n < N; n++){
				phi[n] = (NE[n] >= Paudio);
			}
			ArrayList<Integer> ExcitingClipStart = new ArrayList<>();
			ArrayList<Integer> ExcitingClipEnd = new ArrayList<>();
			int n = 0;
			int start = 0;
			int Th = 100;
			while (n < N){
				start = n;
				while(n < N && phi[n]){
					n++;
				}
				if(n-start >= Th){
					ExcitingClipStart.add(start);
					ExcitingClipEnd.add(n);
				}
				while(n < N && !phi[n]){
					n++;
				}
			}
			System.out.println(ExcitingClipStart);
			System.out.println(ExcitingClipEnd);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
