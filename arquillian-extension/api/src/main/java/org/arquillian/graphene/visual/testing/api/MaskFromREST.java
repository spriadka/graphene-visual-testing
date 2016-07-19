/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api;

import org.jboss.rusheye.suite.MaskType;

/**
 *
 * @author spriadka
 * Class that represents Mask object received from REST service and to be compatible with RushEye 
 */
public class MaskFromREST {

    public static class Builder {

        private String id = "";
        private String sourceUrl = "";
        private MaskType maskType = MaskType.SELECTIVE_ALPHA;
        private String name = "";
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        
        public Builder url(String url){
            this.sourceUrl = url;
            return this;
        }
        
        public Builder type(MaskType type){
            this.maskType = type;
            return this;
        }
        
        public Builder name(String name){
            this.name = name;
            return this;
        }
        
        public MaskFromREST build(){
            return new MaskFromREST(this);
        }
    }
    
    private String id;
    private String sourceUrl;
    private MaskType maskType = MaskType.SELECTIVE_ALPHA;
    private String name;
    
    private MaskFromREST(Builder builder) {
        this.id = builder.id;
        this.sourceUrl = builder.sourceUrl;
        this.name = builder.name;
        this.maskType = builder.maskType;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the sourceUrl
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @param sourceUrl the sourceUrl to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * @return the maskType
     */
    public MaskType getMaskType() {
        return maskType;
    }

    /**
     * @param maskType the maskType to set
     */
    public void setMaskType(MaskType maskType) {
        this.maskType = maskType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
