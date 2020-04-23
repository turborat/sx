package znd

import javax.sound.sampled._
import scala.math._
import x.X._


class Oscillator(freq:Double, loud:Double)
{
	def this(freq:Double) = this(freq,100)

	import Oscillator._

	val line = AudioSystem.getLine(INFO).asInstanceOf[SourceDataLine]
	val wordsPerSecond = AUDIO_FORMAT.getSampleRate * AUDIO_FORMAT.getChannels
	val step = 2 * Pi / wordsPerSecond * freq
  val ampl = ((AUDIO_FORMAT.getSampleSizeInBits * 8) << 8) * loud / 100
  private var frameIndex = 0l ;


	def nextBytes(noob:Array[Byte]) = {
		for(i <- Range(0, noob.length, 4)) {
			val value = (sin((frameIndex + i) * step) * ampl).toInt
			noob(i)   = (value >> 8).toByte			// left channel
			noob(i+1) = value.toByte
			noob(i+2) = noob(i)                 // right channel
			noob(i+3) = noob(i+1)
		}

		frameIndex += noob.length ;

		if (frameIndex >= java.lang.Long.MAX_VALUE / 2) {
			frameIndex %= java.lang.Long.MAX_VALUE / 2 ;
			println("Wrapping frameIndex (" + frameIndex + ")") ;
		}
	}

	def start {
		line.open(AUDIO_FORMAT, OUT_BUF_LEN)
		line.start
		val noob = new Array[Byte](CHUNK_SIZE)
		thread(freq + " Hz") {
			nextBytes(noob)
			line.write(noob, 0, noob.length)
		}
	}
}


object Oscillator
{
	val AUDIO_FORMAT = new AudioFormat(
		 AudioFormat.Encoding.PCM_SIGNED,       // encoding
		 44100,                                 // rate
		 16,                                    // sampleSize
		 2,                                     // channels
		 16 / 8 * 2,                            // (sampleSize/8)*channels
		 44100,                                 // rate
		 true                                   // bigEndian
	)
  println(AUDIO_FORMAT)
  printf("Sample size: %d bits\n", AUDIO_FORMAT.getSampleSizeInBits)

  val OUT_BUF_LEN = 16384
	val CHUNK_SIZE = 2048
	val INFO = new DataLine.Info(classOf[SourceDataLine], AUDIO_FORMAT)
}


object OscillatorMain
{
	def main(args:Array[String]) {
		val osc = new Oscillator(440) { start }
		print("Press return to exit ...")
		Console.readLine
		System.exit(0)
	}
}