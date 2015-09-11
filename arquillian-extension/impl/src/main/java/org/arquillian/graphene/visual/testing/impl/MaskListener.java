/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.impl;

import org.arquillian.graphene.visual.testing.api.event.CrawlMaskDoneEvent;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
public class MaskListener {

    private Logger LOGGER = Logger.getLogger(MaskListener.class);
    
    public void masksCrawled(@Observes CrawlMaskDoneEvent event){
        LOGGER.info("Masks crawled");
    }

    
}
