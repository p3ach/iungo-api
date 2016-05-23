package org.iungo.api.dataset.bulkload;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Quad;

import com.fasterxml.jackson.databind.JsonNode;

public interface JSONToQuads {
	
	public static final Node json = NodeFactory.createURI("urn:iungo:json");
	
	public static final Resource JSON_NULL = ResourceFactory.createResource("urn:iungo:json/Null");
	
	public static final Property JSON_type = ResourceFactory.createProperty("urn:iungo:json/type");
	
	void add(final Quad quad);
	
	void add(final Triple triple);
	
	default Node genericNode(final JsonNode jsonNode) {
		Node result = null;
		if (jsonNode.isArray()) {
			result = arrayNode(jsonNode);
		} else if (jsonNode.isBoolean()) {
			result = NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(jsonNode.asBoolean()));
		} else if (jsonNode.isObject()) {
			result = objectNode(jsonNode);
		} else if (jsonNode.isNumber()) {
			result = NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(jsonNode.asDouble()));
		} else if (jsonNode.isTextual()) {
			result = NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(jsonNode.asText()));
		} else if (jsonNode.isNull()) {
			result = JSON_NULL.asNode();
		} else {
			result = NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(jsonNode.getNodeType().toString()));
			throw new UnsupportedOperationException(jsonNode.getNodeType().toString());
		}
		return result;
	}

	default Node arrayNode(JsonNode jsonNode) {
		final Node bNode = NodeFactory.createBlankNode();
		Integer index = 0;
		Iterator<JsonNode> i = jsonNode.elements();
		while (i.hasNext()) {
			JsonNode e = i.next();
			add(new Triple(bNode, NodeFactory.createURI("urn:iungo:json/array/element/" + ++index), genericNode(e)));
		}
		return bNode;
	}

	default Node objectNode(JsonNode jsonNode) {
		final Node bNode = NodeFactory.createBlankNode();
		final Iterator<Entry<String, JsonNode>> i = jsonNode.fields();
		while (i.hasNext()) {
			final Entry<String, JsonNode> e = i.next();
			add(new Triple(bNode, NodeFactory.createURI("urn:iungo:json/object/" + e.getKey()), genericNode(e.getValue())));
		}
		return bNode;
	}

}
