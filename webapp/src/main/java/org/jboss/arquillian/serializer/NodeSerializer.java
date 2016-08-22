/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.serializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.jboss.arquillian.model.routing.Node;

/**
 *
 * @author spriadka
 */
public class NodeSerializer extends StdSerializer<Node> {

    public NodeSerializer() {
        this(null);
    }

    public NodeSerializer(Class<Node> n) {
        super(n);
    }

    @Override
    public void serialize(Node t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonGenerationException {
        jg.writeStartObject();
        jg.writeNumberField("nodeId", t.getNodeId());
        jg.writeNumberField("index", t.getIndex());
        jg.writeObjectField("word", t.getWord());
        jg.writeBooleanField("root", t.isRoot());
        jg.writeArrayFieldStart("children");
        for (Node child : t.getChildren()) {
            jg.writeStartObject();
            jg.writeNumberField("nodeId", child.getNodeId());
            jg.writeNumberField("index", child.getIndex());
            jg.writeObjectField("word", child.getWord());
            jg.writeEndObject();
        }
        jg.writeEndArray();
        jg.writeEndObject();
    }

}
