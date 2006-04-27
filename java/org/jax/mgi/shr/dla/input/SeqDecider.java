package org.jax.mgi.shr.dla.input;

/**
 * An abstract class that expects its subclasses to implement the protected
 *   is method to apply a predicate to a String. Keeps counter of total records
 *   for which the predicate is true.
 * @has
 *   <UL>
 *   <LI>a name
 *   <LI>counter for total records for which the predicate is true
 *   </UL>
 * @does
 *   <UL>
 *   <LI>The public isA method calls the  is method implemented by the subclass
 *       to determines if the predicate is true
 *   <LI>maintains and provides access to the counter
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public abstract class SeqDecider {

    // number of records for which the predicate is true
    private int trueCtr = 0;

    // This decider's name
    protected String name;

    /**
     * Constructs a SeqDecider object initializing its name
     * @assumes Nothing
     * @effects Nothing
     * @param name the name of this decider
     * @throws Nothing
     */

    public SeqDecider(String name) {
        this.name = name;
    }

    /**
     * Decides if a predicate is true for 's'. Increments counter
     * @assumes Nothing
     * @effects Nothing
     * @param s the String which to apply the predicate
     * @return true if predicate is true when applied to 's'
     * @throws Nothing
     * this decider
     */

    public boolean isA(String s) {
        boolean answer = is(s);
        if(answer == true) {
             trueCtr++;
        }
        return answer;
    }


    /**
     * gets counter of records for which the predicate is true
     * @assumes Nothing
     * @effects Nothing
     * @return trueCtr number of Strings for which the predicate is true
     */

     public int getTrueCtr() {
        return trueCtr;
    }

    /**
     * get the name of this decider
     * @assumes Nothing
     * @effects Nothing
     * @return name name of this decider
     */

    public String getName( ) {
        return name;
    }

    /**
      * abstract method to be implemented to apply a predicate to a String
      * @assumes Nothing
      * @effects Nothing
      * @param s the String which to apply the predicate
      * @return true if the "is relationship" is satisfied
      */

    protected abstract boolean is(String s);
}
