/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api;

import org.arquillian.graphene.visual.testing.api.builder.MaskFromRESTBuilder;
import org.jboss.rusheye.suite.HorizontalAlign;
import org.jboss.rusheye.suite.MaskType;
import org.jboss.rusheye.suite.VerticalAlign;

/**
 *
 * @author spriadka
 */
public class MaskFromREST {
    
    private Long id;
    private String sourceUrl;
    private VerticalAlign verticalAlign;
    private HorizontalAlign horizontalAlign;
    private MaskType maskType = MaskType.SELECTIVE_ALPHA;
    private String name;
    
    public MaskFromREST(MaskFromRESTBuilder builder){
        this.id = builder.getId();
        this.sourceUrl = builder.getSourceUrl();
        this.name = builder.getName();
        this.horizontalAlign = builder.getHorizontalAlign();
        this.verticalAlign = builder.getVerticalAlign();
        this.maskType = builder.getMaskType();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
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
     * @return the verticalAlign
     */
    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }

    /**
     * @param verticalAlign the verticalAlign to set
     */
    public void setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    /**
     * @return the horizontalAlign
     */
    public HorizontalAlign getHorizontalAlign() {
        return horizontalAlign;
    }

    /**
     * @param horizontalAlign the horizontalAlign to set
     */
    public void setHorizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
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
