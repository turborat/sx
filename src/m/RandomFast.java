package m;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;


public class RandomFast
{
	private final AtomicLong seed;

	private final static long multiplier = 0x5DEECE66DL;
	private final static long addend = 0xBL;
	private final static long mask = (1L << 48) - 1;

	public RandomFast()
	{
		this(++seedUniquifier + System.nanoTime());
	}

	private static volatile long seedUniquifier = 8682522807148012L;

	public RandomFast(long seed)
	{
		this.seed = new AtomicLong(0L);
		setSeed(seed);
	}

	synchronized public void setSeed(long seed)
	{
		seed = (seed ^ multiplier) & mask;
		this.seed.set(seed);
		haveNextNextGaussian = false;
	}

	public int next(int bits)
	{
//		System.out.println("Seed: " + seed.get());
		long oldseed, nextseed;
		AtomicLong seed = this.seed;
		do {
			oldseed = seed.get();
			nextseed = (oldseed * multiplier + addend) & mask;
		} while (!seed.compareAndSet(oldseed, nextseed));
		return (int) (nextseed >>> (48 - bits));
	}

	public long getSeed()
	{
		return seed.get() ;
	}

	public void nextBytes(byte[] bytes)
	{
		for (int i = 0, len = bytes.length; i < len; )
			for (int rnd = nextInt(),
						 n = java.lang.Math.min(len - i, Integer.SIZE / Byte.SIZE);
					 n-- > 0; rnd >>= Byte.SIZE)
				bytes[i++] = (byte) rnd;
	}

	public int nextInt()
	{
		return next(32);
	}

	public int nextInt(int n)
	{
		if (n <= 0)
			throw new IllegalArgumentException("n must be positive");

		if ((n & -n) == n)	// i.e., n is a power of 2
			return (int) ((n * (long) next(31)) >> 31);

		int bits, val;
		do {
			bits = next(31);
			val = bits % n;
		} while (bits - val + (n - 1) < 0);
		return val;
	}

	public long nextLong()
	{
		// it's okay that the bottom word remains signed.
		return ((long) (next(32)) << 32) + next(32);
	}

	public boolean nextBoolean()
	{
		return next(1) != 0;
	}

	public float nextFloat()
	{
		return next(24) / ((float) (1 << 24));
	}

	public double nextDouble()
	{
		return (((long) (next(26)) << 27) + next(27))
			/ (double) (1L << 53);
	}

	private double nextNextGaussian;
	private boolean haveNextNextGaussian = false;

	synchronized public double nextGaussian()
	{
		// See Knuth, ACP, Section 3.4.1 Algorithm C.
		if (haveNextNextGaussian) {
			haveNextNextGaussian = false;
			return nextNextGaussian;
		} else {
			double v1, v2, s;
			do {
				v1 = 2 * nextDouble() - 1; // between -1 and 1
				v2 = 2 * nextDouble() - 1; // between -1 and 1
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			nextNextGaussian = v2 * multiplier;
			haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}

	public double rand()
	{
		return nextDouble();
	}


	public double nrand()
	{
		return nextGaussian();
	}

	private static final ObjectStreamField[] serialPersistentFields = {
		new ObjectStreamField("seed", Long.TYPE),
		new ObjectStreamField("nextNextGaussian", Double.TYPE),
		new ObjectStreamField("haveNextNextGaussian", Boolean.TYPE)
	};

	private void readObject(java.io.ObjectInputStream s)
		throws java.io.IOException, ClassNotFoundException
	{

		ObjectInputStream.GetField fields = s.readFields();

		long seedVal = (long) fields.get("seed", -1L);
		if (seedVal < 0)
			throw new java.io.StreamCorruptedException(
				"Random: invalid seed");
		resetSeed(seedVal);
		nextNextGaussian = fields.get("nextNextGaussian", 0.0);
		haveNextNextGaussian = fields.get("haveNextNextGaussian", false);
	}

	synchronized private void writeObject(ObjectOutputStream s)
		throws IOException
	{

		// set the values of the Serializable fields
		ObjectOutputStream.PutField fields = s.putFields();

		// The seed is serialized as a long for historical reasons.
		fields.put("seed", seed.get());
		fields.put("nextNextGaussian", nextNextGaussian);
		fields.put("haveNextNextGaussian", haveNextNextGaussian);

		// save them
		s.writeFields();
	}

//	private static final Unsafe unsafe;
//	private static final long seedOffset;

	private static long seedX ;

//	static {
//		try {
//			Class objectStreamClass = Class.forName("java.io.ObjectStreamClass$FieldReflector");
//			Field unsafeField = objectStreamClass.getDeclaredField("unsafe");
//			unsafeField.setAccessible(true);
//			unsafe = (Unsafe) unsafeField.get(null);
//
//			seedOffset = unsafe.objectFieldOffset
//				(Random.class.getDeclaredField("seed"));
//		} catch (Exception ex) {
//			throw new Error(ex);
//		}
//	}

	private void resetSeed(long seedVal)
	{
	  seedX = seedVal ;
//		unsafe.putObjectVolatile(this, seedOffset, new AtomicLong(seedVal));
	}
}
