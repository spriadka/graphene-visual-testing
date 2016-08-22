/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.serialization.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.Arrays;

/**
 *
 * @author spriadka
 */
public class CustomSerializationStrategy implements ExclusionStrategy{
    
    private final Class<?> classToSkip;
    
    private String[] fieldsToSkip;
    
    private CustomSerializationStrategy(Class<?> typeToSkip, String[] fields){
        this.classToSkip = typeToSkip;
        this.fieldsToSkip = fields;
    }
    
    public static CustomSerializationStrategy newInstance(Class<?> clazz, String[] fields){
        return new CustomSerializationStrategy(clazz, fields);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == classToSkip && Arrays.binarySearch(fieldsToSkip, f.getName()) >= 0);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz == classToSkip;
    }
    
}
