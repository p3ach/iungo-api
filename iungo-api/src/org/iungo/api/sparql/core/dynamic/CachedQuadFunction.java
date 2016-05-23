package org.iungo.api.sparql.core.dynamic;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.sparql.core.Quad;

public interface CachedQuadFunction extends QuadFunction {
	
	static Supplier<Stream<Quad>> EMPTY_QUAD_STREAM_SUPPLIER = new Supplier<Stream<Quad>>() {
		@Override
		public Stream<Quad> get() {
			return Stream.empty();
		}
	};
	
	static class SetSupplier implements Supplier<Stream<Quad>> {
		
		protected final Set<Quad> quads;

		public SetSupplier(final Set<Quad> quads) {
			super();
			this.quads = quads;
		}

		@Override
		public Stream<Quad> get() {
			return quads.stream();
		}
	}
	
	/**
	 * The concurrent map used to maintain the cache. 
	 */
	ConcurrentMap<Quad, Supplier<Stream<Quad>>> cache();
	
	/**
	 * Subclasses should override where a broader Quad key can be defined as by default it returns the Quad passed in.
	 * If this returns null then apply(Quad) will return an empty Stream. 
	 */
	default Quad key(final Quad quad) {
		return quad;
	}
	
	/**
	 * Subclasses need to override as otherwise it will return an empty Set.
	 */
	default Supplier<Stream<Quad>> miss(final Quad quad) {
		return EMPTY_QUAD_STREAM_SUPPLIER;
	}
	
	default Stream<Quad> apply(final Quad quad) {
		Stream<Quad> quads;
		final Quad key = key(quad);
		if (key == null) {
			quads = Stream.empty();
		} else {
			Supplier<Stream<Quad>> supplier = cache().get(key);
			if (supplier == null) {
				supplier = miss(quad);
				cache().put(key, supplier);
			}
			quads = supplier.get();
		}
		return quads;
	}

}
