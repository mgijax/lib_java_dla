package org.jax.mgi.shr.dla.seqloader;

import java.util.Vector;

public interface OrganismChecker {
    public boolean checkOrganism(String record);
    public Vector getDeciderCounts();
}