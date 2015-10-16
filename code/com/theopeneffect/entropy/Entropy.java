package com.theopeneffect.entropy;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * An extremely simple class to introduce additional entropy based on unpredictable
 * computational time of variable-length computational routines.
 * 
 * Computational 'events' (the wasteTime() method) act to do an unknown amount of work,
 * so that time gap can be measured and used as entropy for the system. Even computations
 * of a known length and difficulty take variable amounts of time given the nature of
 * modern runtime environments. Timing the runtime of these events allows the introduction
 * of entropy based on properties outside the program, such as processor load, computer
 * performance, and thread scheduling. 
 * 
 * Furthermore, available environment states (such as memory availability) provide additional
 * entropy.
 * 
 * At its core, this class utilizes Java's Random object to further mix internal state
 * and easily provide non-biased numbers. All top-level state modifications are based
 * on XOR, as XOR does not favor either zeroes or ones, and so will not cause incremental
 * decay towards a lower-entropy state.
 * 
 * This class focuses far more on the unpredictability of output rather than the even
 * distribution of results, but experimentally this class has shown to produce well-distributed
 * results.
 * 
 * In 1,000 trials, each consisting of averaging the result from 100,000 calls to Entropy.nextDouble()
 * and Random.nextDouble() (both initialized with no constructor arguments), the averages of
 * Entropy tended to be, on average, moderately farther away from the expected value of 0.5. 
 * However, All differences from both classes were less than 0.8% from the expected value, which
 * for most purposes is a negligible bias. Additionally, biases did not favor either above
 * or below the expected value. 
 * 
 * System.nanoTime() used for additional granularity in timing unknown-length computational
 * events.
 * 
 * This code is provided free of charge, and free of warranty. 
 * This code is not guaranteed to meet any standard of fitness, nor to produce specific results.
 * 
 * This project is released under the MIT license. 
 * As such, it can be used commercially. 
 * 
 * @author Maxwell Sanchez
 * @version 1.0
 *
 */
public class Entropy 
{
	// State variables for default initialization values of the Entropy object
	// All have an equal number of ones and zeroes when represented in binary
	private long state1 = 0x967ca962dd134c55L;
	private long state2 = 0x8e678ec4fa4a3721L;
	private long state3 = 0x741677f32bf14850L;
	private long state4 = 0x82359b9bbd5e1708L;
	
	// Updated whenever wasteTime() is called, to introduce additional entropy from variables
	// used for consuming CPU cycles in calculations.
	private long stateMixinFromWaste = 0x3955170223037924L;
	
	// The Runtime object to use for lookups regarding memory availability for additional entropy
	private Runtime runtime;
	
	// The initial instantiation time of an Entropy object
	private long startTimeNS;
	
	/**
	 * Constructor sets initial states dependent on timing and avalanche timing several related 
	 * computational events.
	 * 
	 * Results of these timings are used to set a reasonably mixed initial state well-removed 
	 * from start-time seeding.
	 */
	public Entropy()
	{
		startTimeNS = System.nanoTime();
		
		runtime = Runtime.getRuntime();
		
		wasteTime();
		
		long startMix1NS = System.nanoTime();
		
		long time1 = System.nanoTime() - startMix1NS;
		wasteTime();
		long time2 = System.nanoTime() - startMix1NS;
		wasteTime();
		long time3 = System.nanoTime() - startMix1NS;
		wasteTime();
		long time4 = System.nanoTime() - startMix1NS;
		wasteTime();
		
		// Many new random objects are created to exploit the changing system time used for seeding Random
		Random mixer = new Random();
		
		// All time variables have a value in a predictable range, move it to an unpredicted location
		// for fairer mixing
		long mix1 = time1 << mixer.nextInt(64);
		mix1 += time2 << mixer.nextInt(64);
		mix1 += time3 << mixer.nextInt(64);
		mix1 += time4 << mixer.nextInt(64);
		
		state1 ^= new Random().nextLong() ^ mix1;
		
		long startMix2NS = System.nanoTime();
		
		long time5 = System.nanoTime() - startMix2NS;
		wasteTime();
		long time6 = System.nanoTime() - startMix2NS;
		wasteTime();
		long time7 = System.nanoTime() - startMix2NS;
		wasteTime();
		long time8 = System.nanoTime() - startMix2NS;
		wasteTime();
		
		mixer = new Random();
		
		long mix2 = time6 << mixer.nextInt(64);
		mix2 += time7 << mixer.nextInt(64);
		mix2 += time5 << mixer.nextInt(64);
		mix2 += time8 << mixer.nextInt(64);
		
		state2 ^= new Random().nextLong() ^ mix2;
		
		long startMix3NS = System.nanoTime();
		
		long time9 = System.nanoTime() - startMix3NS;
		wasteTime();
		long time10 = System.nanoTime() - startMix3NS;
		wasteTime();
		long time11 = System.nanoTime() - startMix3NS;
		wasteTime();
		long time12 = System.nanoTime() - startMix3NS;
		wasteTime();
		
		mixer = new Random();
		
		long mix3 = time11 << mixer.nextInt(64);
		mix3 += time9 << mixer.nextInt(64);
		mix3 += time12 << mixer.nextInt(64);
		mix3 += time10 << mixer.nextInt(64);
		
		state3 ^= new Random().nextLong() ^ mix3;

		long startMix4NS = System.nanoTime();
		
		long time13 = System.nanoTime() - startMix4NS;
		wasteTime();
		long time14 = System.nanoTime() - startMix4NS;
		wasteTime();
		long time15 = System.nanoTime() - startMix4NS;
		wasteTime();
		long time16 = System.nanoTime() - startMix4NS;
		wasteTime();
		
		mixer = new Random();
		
		long mix4 = time16 << mixer.nextInt(64);
		mix4 += time13 << mixer.nextInt(64);
		mix4 += time14 << mixer.nextInt(64);
		mix4 += time15 << mixer.nextInt(64);
		
		mix1 ^= runtime.freeMemory();
		
		state4 ^= new Random().nextLong() ^ mix4;
	}
	
	/**
	 * Adds additional event-time-based mixing to the state.
	 * Uses same event (wasteTime()) as constructor.
	 * Called after every method call which pushes entropy to the outside world 
	 * (nextLong(), and all dependent methods). 
	 */
	public void entropize()
	{
		long start = System.nanoTime();
		wasteTime();
		Random stateShifter = new Random(System.nanoTime() - start);
		
		state1 ^= stateShifter.nextLong();
		state2 ^= stateShifter.nextLong();
		
		if ((System.nanoTime() - start) % 10 == 0)
		{
			stateShifter.setSeed(stateMixinFromWaste);
		}
		
		state3 ^= stateShifter.nextLong();
		state4 ^= stateShifter.nextLong();
		
		int route = new Random().nextInt(4);
		
		if (route == 0)
		{
			state1 ^= (runtime.freeMemory() << stateShifter.nextInt(64));
		}
		
		else if (route == 1)
		{
			state2 ^= (runtime.freeMemory() << stateShifter.nextInt(64));
		}
		
		else if (route == 3)
		{
			state3 ^= (runtime.freeMemory() << stateShifter.nextInt(64));
		}
		
		else
		{
			state4 ^= (runtime.freeMemory() << stateShifter.nextInt(64));
		}
	}
	
	/**
	 * Allows additional entropy to be introduced from an external source.
	 * 
	 * @param entropy byte[] representation of external entropy.
	 */
	public void addEntropy(byte[] entropy)
	{
		long startTime = System.nanoTime();
		
		int i = 0;
		for (i = 0; i < entropy.length % 8; i+=8)
		{
			// Produce a fully-filled long from the provided entropy
			long mixin = entropy[i] << 56;
			mixin += entropy[i + 1] << 48;
			mixin += entropy[i + 2] << 40;
			mixin += entropy[i + 3] << 32;
			mixin += entropy[i + 4] << 24;
			mixin += entropy[i + 5] << 16;
			mixin += entropy[i + 6] << 8;
			mixin += entropy[i + 7];
			
			// Decide where to mix in the current 8 bytes of entropy
			Random router = new Random(System.nanoTime() - startTime);
			int route = router.nextInt(4);
			
			// Execute choice of mix in location
			if (route % 4 == 0)
			{
				state1 ^= (mixin | state2);
			}
			
			else if (route % 4 == 1)
			{
				state2 ^= (mixin | state3);
			}
			
			else if (route % 4 == 2)
			{
				state3 ^= (mixin | state4);
			}
			
			else
			{
				state4 ^= (mixin | state1);
			}
		}
		
		// Ugly way to use variable 'i' from before without reassignment. Consume final bytes not accounted for.
		for (; i < entropy.length; i++)
		{
			Random router = new Random(System.nanoTime() - startTime);
			int route = router.nextInt(4);
			
			if (route % 4 == 0)
			{
				state1 ^= (new Random(entropy[i]).nextLong());
			}
			
			else if (route % 4 == 1)
			{
				state2 ^= (new Random(entropy[i]).nextLong());
			}
			
			else if (route % 4 == 2)
			{
				state3 ^= (new Random(entropy[i]).nextLong());
			}
			
			else
			{
				state4 ^= (new Random(entropy[i]).nextLong());
			}
		}
	}
	
	/**
	 * Returns a pseudorandom boolean value based on present entropy.
	 * @return boolean A pseudorandom boolean value based on present entropy.
	 */
	public boolean nextBoolean()
	{
		if (nextInt(2) == 1)
		{
			return true;
		}
		return false;
	}

	/**
	 * Fills the provided byte array with psuedorandomly generated bytes. 
	 */
	public void nextBytes(byte[] bytes)
	{
		for (int i = 0; i < bytes.length; i++)
		{
			// 0 - 255 is the range of an unsigned bit, casting an int above 127 will give a two's complement-based negative.
			bytes[i] = (byte)nextInt(256); 
		}
	}

	/**
	 * Returns a pseudorandom double value based on present entropy.
	 * @return double A pseudorandom double value based on present entropy.
	 */
	public double nextDouble()
	{
		double randomDouble = 0.0;
		
		// Just use the long I already wrote generation for to create a double between 0 (inc.) nd 1 (not inc.)
		randomDouble += ((double)nextLong() / (double)Long.MAX_VALUE);
		if (randomDouble < 0) randomDouble *= -1;
		return randomDouble;
	}

	/**
	 * Returns a pseudorandom float value based on present entropy.
	 * @return float A pseudorandom float value based on present entropy.
	 */
	public float nextFloat()
	{
		// Just use the double I already wrote generation for and reduce precision for a float
		return (float)nextDouble();
	}

	/**
	 * Returns a pseudorandom long value based on present entropy.
	 * @return long A pseudorandom long value based on present entropy.
	 */
	public long nextLong()
	{
		// Use Java's Random with seeds determined by state to create and mix a pseudorandom long
		Random shifters = new Random();
		long randomLong = new Random(state3 << shifters.nextInt(64)).nextLong();
		randomLong ^= new Random(state1 << shifters.nextInt(64)).nextInt();
		randomLong ^= new Random(state2).nextInt();
		randomLong ^= (state4 << shifters.nextInt(64));
		randomLong ^= stateMixinFromWaste;
		randomLong ^= new Random(new Random().nextLong() + startTimeNS).nextLong();
		
		// More state mixing
		entropize();
		
		// Even more state mixing
		state1 <<= shifters.nextInt(64);
		state2 <<= shifters.nextInt(64);
		state3 <<= shifters.nextInt(64);
		state4 <<= shifters.nextInt(64);
		state1 ^= (state2 << shifters.nextInt(64) | stateMixinFromWaste);
		state2 ^= (state3 << shifters.nextInt(64) | new Random().nextLong());
		state3 ^= (state4 << shifters.nextInt(64) | state1);
		state4 ^= (state1 << shifters.nextInt(64) | state2);
		long swapState = state4;
		state4 = state1;
		state1 = state2;
		state2 = state3;
		state3 = swapState;
		
		return randomLong;
	}

	/**
	 * Returns a pseudorandom long value based on present entropy below the provided limit.
	 * 
	 * @param limit The (non-inclusive) limit of the returned long.
	 * @return long A pseudorandom long value based on present entropy below the provided limit.
	 */
	public long nextLong(long limit)
	{
		// Wrap a boundless pseudorandom long around the provided limit
		long toLimit = nextLong();
		if (toLimit < 0) toLimit *= -1;
		return toLimit % limit;
	}

	/**
	 * Returns a pseudorandom int value based on present entropy.
	 * @return int A pseudorandom int value based on present entropy.
	 */
	public int nextInt()
	{		
		// Just grab a pseudorandom long and reduce precision
		return (int) nextLong();
	}

	/**
	 * Returns a pseudorandom int value based on present entropy below the provided limit.
	 * 
	 * @param limit The (non-inclusive) int of the returned int.
	 * @return int A pseudorandom int value based on present entropy below the provided limit.
	 */
	public int nextInt(int limit)
	{
		// Wrap a boundless pseudorandom int around a provided limit
		int toLimit = nextInt();
		if (toLimit < 0) toLimit *= -1;
		return toLimit % limit;
	}
	
	/**
	 * A method designed to take an unknown (but miniscule) amount of time to complete.
	 * Also adds slightly to the entropy of the object.
	 */
	private void wasteTime()
	{
		int firstCycleCount = new Random().nextInt(1000) + 2000; // Keep firstCycleCount random but within expected range
		
		long meaninglessState1 = new Random().nextLong();
		long meaninglessState2 = new Random().nextLong();
		
		Random cycleOneRandom = new Random();
		
		for (int i = 0; i < firstCycleCount; i++)
		{
			meaninglessState1 ^= (meaninglessState2 & cycleOneRandom.nextLong() | cycleOneRandom.nextLong());
			meaninglessState2 ^= (cycleOneRandom.nextLong() ^ cycleOneRandom.nextLong());
		}
		
		int secondCycleCount = new Random().nextInt(1000) + 2000; // Keep secondCycleCount random but within expected range
		
		Random cycleTwoRandom = new Random();
		
		for (int i = 0; i < secondCycleCount; i++)
		{
			meaninglessState2 ^= cycleTwoRandom.nextLong();
			meaninglessState1 ^= (cycleTwoRandom.nextLong() | cycleTwoRandom.nextLong());
			
			if (meaninglessState2 % 100 == 0)
			{
				cycleTwoRandom = new Random(new Random().nextLong() ^ meaninglessState1);
			}
		}
		
		// Mind as well put the working variables to good use doing *something*
		stateMixinFromWaste ^= meaninglessState1 ^ meaninglessState2;
	}
}
