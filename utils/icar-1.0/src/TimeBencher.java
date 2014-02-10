/**
 * Ce programme détermine la plus petite valeur d'échantillonage temporel
 * du système.
 */
public class TimeBencher {
    
    public static void main (String [] args)
    {
        // JIT/hotspot warmup:
        for (int r = 0; r < 3000; ++ r) System.currentTimeMillis ();

        long time = System.currentTimeMillis (), time_prev = time;

        for (int i = 0; i < 10; ++ i)
        {
            // Busy wait until system time changes:
            while (time == time_prev)
                time = System.currentTimeMillis ();

            System.out.println ("delta = " + (time - time_prev) + " ms");
            time_prev = time;
        }
    }
    
 
}
