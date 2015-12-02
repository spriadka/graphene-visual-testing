/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.generator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
public class MaskGenerator implements IdentifierGenerator {

    private final Logger LOGGER = Logger.getLogger(MaskGenerator.class);

    private final String PREFIX = "mask";

    private final String SEQUENCE_NAME = "SEQ_MASK_ID";

    private final String SELECT_NEXTVAL = "SELECT NEXTVAL('seq_mask_id');";

    private final String CREATE_SEQUENCE = "CREATE SEQUENCE " + SEQUENCE_NAME;

    @Override
    public Serializable generate(SessionImplementor si, Object o) throws HibernateException {
        Serializable result = null;
        try(PreparedStatement preparedStatement = si.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement(SELECT_NEXTVAL)){
            try(ResultSet rs = si.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract(preparedStatement)){
                if (rs.next()){
                    int nextVal = rs.getInt(1);
                    result = PREFIX.concat("" + nextVal);
                    LOGGER.info(result);
                }
                return result;
            }
        }
        catch (SQLException sqle){
            LOGGER.error(sqle);
            return null;
        }
    }

}
