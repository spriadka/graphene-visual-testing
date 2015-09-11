/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToSuiteEvent;


/**
 *
 * @author spriadka
 */
public interface MaskHandler {
    void uploadMasks(CrawlMaskToSuiteEvent crawlMaskEvent);
    void deleteMasks();
}
