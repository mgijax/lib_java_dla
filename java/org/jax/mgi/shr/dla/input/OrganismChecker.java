package org.jax.mgi.shr.dla.input;

import java.util.Vector;

public interface OrganismChecker {
    public boolean checkOrganism(String record);
    public Vector getDeciderCounts();
}