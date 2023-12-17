import com.zakgof.velvetvideo.IAudioDecoderStream;
import com.zakgof.velvetvideo.IAudioFrame;
import com.zakgof.velvetvideo.IDemuxer;
import com.zakgof.velvetvideo.IVelvetVideoLib;
import com.zakgof.velvetvideo.impl.VelvetVideoLib;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SoundExtracting {
    public SoundExtracting() {
        int totalFramesRead = 0;
        File fileIn = new File("video.wav");
// somePathName is a pre-existing string whose value was
// based on a user selection.
        try {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            long[] result = new long[1024];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                // Try to read numBytes bytes from the file.
                while ((numBytesRead =
                        audioInputStream.read(audioBytes)) != -1) {
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
                    // Here, do something useful with the audio data that's
                    // now in the audioBytes array...
                    for(int i = 0; i < 1024; i++) {
                        byte [] resb = new byte[4];
                        System.arraycopy(audioBytes, 4 * i, resb, 0, 4);
                        int value = 0;
                        for (byte b : resb) {
                            value = (value << 8) + (b & 0xFF);
                        }
                        result[i] = value;
                        if(4*i >= numBytesRead){
                            result[i] = 0;
                        }
                    }
                    String r = Arrays.toString(result);
                    System.out.print(r.substring(1, r.length() -1));
                    System.out.println(",");
                }
                System.out.println(bytesPerFrame);
            } catch (Exception ex) {
                // Handle the error...
            }
        } catch (Exception e) {
            // Handle the error...
        }
    }
}
