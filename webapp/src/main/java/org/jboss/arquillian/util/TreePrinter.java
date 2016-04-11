/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.util;

import org.jboss.arquillian.model.routing.Node;

/**
 *
 * @author spriadka
 */
public class TreePrinter {
    
    public static String print(Node node){
        int dashes = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(node.getWord().getValue()).append("\n");
        if (node.hasChildren()){
            for (Node child : node.getChildren()){
                builder.append(printWithDashes(dashes + 4, child)).append("\n");
            }
        }
        return builder.toString();
    }
    
    private static String printWithDashes(int dashes,Node node){
        StringBuilder builder = new StringBuilder();
        builder.append(new String(new char[dashes]).replace('\0', '*')).append(" ").append(node.getWord().getValue()).append("\n");
        if (node.hasChildren()){
            for (Node child : node.getChildren()){
                builder.append(printWithDashes(dashes + 4, child)).append("\n");
            }
        }
        return builder.toString();
    }
    
}
