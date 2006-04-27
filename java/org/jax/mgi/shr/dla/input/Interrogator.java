package org.jax.mgi.shr.dla.input;

import java.util.*;

/**
   * An abstract class that provides the loadExpressions method signature
   * to load a map of controlled vocab keys to String expressions.
   * Provides a method to determine if a mapped expression matches a given String
   * @has a Mapping of controlled vocab to String expressions
   * @does Provides the loadExpressions method signature to be implemented by sub
   *       classes. Determines if an expression, determined by a given
   *       controlled vocabulary String, matches a given String
   * @company The Jackson Laboratory
   * @author sc
   * @version 1.0
   */

public abstract class Interrogator {

    // a mapping of controlled vocab to a String expression
    protected HashMap expressions = new HashMap();

    /**
    * abstract method to be implemented by each subclass to load a HashMap
    * of expressions
    * @assumes Nothing
    * @effects Nothing
    */
    protected abstract void loadExpressions();

    /**
    * Determines whether the expression mapped to "expressionKey" matches "input"
    * @assumes Nothing
    * @effects Nothing
    * @param input the String we want to match
    * @param expressionKey controlled vocab for determining expression to apply to
    *        "input"
    * @return true if expression determined by "expressionKey" matches "input"
    * @throws Nothing
    */

    public boolean isMatch (String input, String expressionKey) {
       if(input.indexOf(
             (String)expressions.get(expressionKey)) >  -1) {
           return true;
       }
       else {
           return false;
       }
  }
}

