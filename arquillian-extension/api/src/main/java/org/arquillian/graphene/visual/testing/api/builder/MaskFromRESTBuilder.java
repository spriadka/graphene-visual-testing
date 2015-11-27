/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api.builder;

import org.arquillian.graphene.visual.testing.api.MaskFromREST;
import org.jboss.rusheye.suite.MaskType;

/**
 *
 * @author spriadka
 */
public class MaskFromRESTBuilder extends Builder<MaskFromREST>{
    
    private Long id;
    private String sourceUrl;
    private MaskType maskType = MaskType.SELECTIVE_ALPHA;
    private String name;
    
    public MaskFromRESTBuilder id(Long id){
        this.id = id;
        return this;
    }
    
    public MaskFromRESTBuilder sourceUrl(String sourceUrl){
        this.sourceUrl = sourceUrl;
        return this;
    }
    
    
    public MaskFromRESTBuilder maskType(MaskType maskType){
        this.maskType = maskType;
        return this;
    }
    
    public MaskFromRESTBuilder name(String name){
        this.name = name;
        return this;
    }
    
    public MaskFromREST build(){
        return new MaskFromREST(this);
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the sourceUrl
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @return the maskType
     */
    public MaskType getMaskType() {
        return maskType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
}
