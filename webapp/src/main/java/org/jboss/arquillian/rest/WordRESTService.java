/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.managers.WordManager;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */

@RequestScoped
@Path("/words")
public class WordRESTService {
    
    @Inject
    private WordManager manager;
    
    private Logger LOGGER = Logger.getLogger(WordRESTService.class);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addWords(Word word){
        Word response = manager.addWord(word);
        Response toReturn =  Response.ok(response.toJSON(),MediaType.APPLICATION_JSON).build();
        LOGGER.info(toReturn);
        return toReturn;
    }
    
    @GET
    @Path("/{wordId:[0-9][0-9]*}")
    public Response getWord(@PathParam("wordId")long wordId){
        Word response = manager.getWord(wordId);
        return response != null ? Response.ok(response,MediaType.APPLICATION_JSON).build() : Response.serverError().build();
    }
}
