package org.jax.mgi.shr.dla.loader.seq.genetrap;

import org.jax.mgi.shr.exception.MGIException;

/**
 * Determines a point coordinate for a DNA-based sequence tag
 * @has  
 * @does Using vector end info, reverse complement info (true/false), and strand
 * determine a point coordinate for a coordinate range.
 * @company The Jackson Laboratory 
 * @author sc
 * @version 1.0
 */

public class PointCoordSelector {
       
    public Integer select (String vectorEnd, Boolean isReverseComp, 
	String strand, Integer start, Integer end) throws MGIException {
	Integer pointCoord = null;
	Integer larger = null;
	Integer smaller = null;
	
	if (vectorEnd == null || isReverseComp == null || strand == null || 
	    start == null || end == null) {
	    throw new MGIException("One or more attributes is null: " + 
		vectorEnd + " " + isReverseComp.toString() + " " + strand + " " +
		start.toString() + " " + end.toString());
	}
	// determine largest/smallest coordinate
	if (start.intValue() > end.intValue()) {
	    larger = start; smaller = end;
	}
	else {
	    larger = end; smaller = start;
	}
	
	// select point coordinate for non-reverse complemented sequence tag
	if ( isReverseComp.equals(Boolean.FALSE) ) {
	    if (strand.equals("-") ) {
	        pointCoord = larger;
	    }
	    else {
		pointCoord = smaller;
	    }
	}
	// select point coordinate for reverse complemented sequence tag
	else {
	    // if vector end is UPSTREAM
	    if (vectorEnd.equals(GeneTrapConstants.UPSTREAM) ) {
	        if (strand.equals("-") ) {
	            pointCoord = smaller;
	        }
	        // if strand == "+"
	        else {
		    pointCoord = larger;
            }
        }
	    // if vector end is DOWNSTREAM
	    else {
		if (strand.equals("-")) {
		    pointCoord = larger;
		}
		// if strand == "+"
		else {
		    pointCoord = smaller;
		}
	    }
	}
	return pointCoord;
    }
}
