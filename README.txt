This program is provided free of charge, with no assurance or guarantee that it
will fit any definition of "fit." No warranty, explicit or implied, is included
with this source code and accompanying documentation. 

This software is provided free of charge, and is licensed under the MIT license.
As a result, it can be used in commercial applications. 

This tiny Entropy project was created out of a desire to pull some primitive
external entropy into a Java program without relying on system-specific
sources of entropy, like /dev/random and /dev/urandom.

I don't suggest using this in any cryptographic application without first performing
additional analysis, and possibly reworking portions of the class.

Calls to the entropy-providing methods (like nextLong(), nextInt(), nextDouble(),
nextFloat(), nextBoolean(), nextBytes(byte[]), ...) are orders of magnitude slower
than their java.util.Random counterparts. The majority of external entropy in
this software lies in exploiting the inability to accurately predict exact execution
times for code. Additional tricks are used to ensure that mixing doesn't cause state
decay (like only using XOR for top-level state mixes to avoid binary digit bias),
and to stretch out the derived entropy as much as possible. 

Additionally, minor steps have been taken to amplify the effect of small changes
in input entropy to cause drastic, cascading/avalancing effects on state change,
similar in spirit to hashing functions. 

The Entropy class provides rudimentary means to introduce additional entropy into
the system from external sources. If, for example, one wished to pull entropy from
mouse movement in a GUI application with mouse focus, part of the mouse listener
could call addEntropy(byte[]) and provide a byte[] derived from the mouse movements
(s)he measured. 

The most likely reasonable use case of this software is for obscuring random number
generation, to render source code ineffectual in predicting random numbers generated
by the application running on a remote box. Simply knowing the time at which a program
was run and some sample output could allow an attacker to predict future output knowing
the source code and approximate time the program was run, as the rudimentary seeding of
Java's Random class relies on an incrementing state mask applied to System.nanoTime(). 

With this class, an attack based on seed prediction would have to accurately predict
the timing of arbitrary execution events, which is impossible given the chaotic state
of modern runtime environments. 

Version 1.0 of this class relies primarily on code event timing. Future versions may
introduce other sources of entropy, however user input timings were purposely avoided
to allow Entropy to function in an environment without proper input capturing, and also
to avoid consuming input destined for elsewhere.

A more advanced version of this software will likely make an appearance in Curecoin 2.0.

If you are interested in reproducing the provided graphs or analyzing the data differently,
the raw input data is provided, labeled "Raw Run Data". This data represents 1,000 runs,
each consisting of 100,000 calls to Entropy.nextDouble() compared to Random.nextDouble().
This data represents 100,000,000 total calls to Entropy.nextDouble().

As can be seen from the graphs, no significant difference exists between the proximity of
Java's Random (orange) and Entropy's PRNG functionality to the expected average values.

An additional file, "Large Run Result Data" contains the results from runs consisting of
10,000,000 random calls per run. 

A third file, "Sample Double Output" contains 10,000 consecutive output values from an
instance of Entropy's nextDouble() method, pulled directly after the object's instantiation.