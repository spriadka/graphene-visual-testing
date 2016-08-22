/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import org.jboss.arquillian.managers.NodeManager;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@RequestScoped
@Path("/nodes")
public class NodeRESTService {
    
    @Inject
    private NodeManager nodeManager;
    
    @Inject
    private TestSuiteManager testSuiteManager;
    
    @Context
    private Providers providers;
    
    private final Logger LOGGER = Logger.getLogger(NodeRESTService.class);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNode(@HeaderParam("index")short index,Word word){
       Node toAdd = new Node();
       toAdd.setWord(word);
       toAdd.setIndex(index);
       Node returnNode = nodeManager.addNode(toAdd);
       return Response.ok(returnNode,MediaType.APPLICATION_JSON).build();
    }
    
    @PUT
    @Path("/{parentId:[0-9][0-9]*}/{childId:[0-9][0-9]*}")
    public Response addChildToNode(@PathParam("parentId")Long parentId, @PathParam("childId")Long childId){
        Node parentNode = nodeManager.getNode(parentId);
        Node childNode = nodeManager.getNode(childId);
        int initLazy = parentNode.getChildren().size();
        Set<Node> children = new HashSet<>(parentNode.getChildren());
        children.add(childNode);
        parentNode.setChildren(children);
        childNode.setParent(parentNode);
        nodeManager.updateNode(childNode);
        nodeManager.updateNode(parentNode);
        return Response.ok().build();
    }
    @GET
    @Path("/{nodeId:[0-9][0-9]*}")
    public Response getNode(@PathParam("nodeId")long nodeId){
        Node response = nodeManager.getNode(nodeId);
        return Response.ok(response,MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("/map/{nodeId: [0-9][0-9]*}")
    public Response getMap(@PathParam("nodeId")long nodeId){
        Node fromEm = nodeManager.getNode(nodeId);
        return fromEm != null ? Response.ok(nodeManager.createNodesMap(fromEm),MediaType.APPLICATION_JSON).build() : Response.serverError().build();
    }
    
    @GET
    @Path("/root-node/{testSuiteName: .*}")
    public Response getRootNodeForSuite(@PathParam("testSuiteName")String suiteName){
        Node fromEm = nodeManager.getNode(testSuiteManager.getTestSuite(suiteName).getRootNode().getNodeId());
        return fromEm != null ? Response.ok(fromEm,MediaType.APPLICATION_JSON).build() : Response.serverError().build();
    }
    
}
