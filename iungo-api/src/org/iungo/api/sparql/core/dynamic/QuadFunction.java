package org.iungo.api.sparql.core.dynamic;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.jena.sparql.core.Quad;

public interface QuadFunction extends Function<Quad, Stream<Quad>> {

//	static final ContextKeys CONTEXT_KEYS = new ContextKeys(QuadFunction.class);
	
	static final String CONTEXT_ROOT = QuadFunction.class.getSimpleName();
	
	static final String CLASS_NAME = CONTEXT_ROOT + ".className";
}
