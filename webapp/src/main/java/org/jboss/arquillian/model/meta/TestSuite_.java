/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.meta;

import javax.persistence.criteria.Order;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.arquillian.model.testSuite.TestSuite;
import java.util.List;
import javax.persistence.metamodel.ListAttribute;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;

/**
 *
 * @author spriadka
 */
@StaticMetamodel(TestSuite.class)
public class TestSuite_ {
    public static volatile SingularAttribute<TestSuite,Long> testSuiteID;
    public static volatile SingularAttribute<TestSuite,String> name;
    public static volatile SingularAttribute<TestSuite,Integer> numberOfFunctionalTests;
    public static volatile SingularAttribute<TestSuite,String> numberOfVisualComparisons;
    public static volatile ListAttribute<TestSuite,TestSuiteRun> runs;
    public static volatile ListAttribute<TestSuite,Pattern> patterns;
    public static volatile SingularAttribute<TestSuite,Node> rootNode;
}
