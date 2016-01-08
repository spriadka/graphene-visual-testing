/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.generator;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final String CREATE_SEQUENCE = "CREATE SEQUENCE public." + SEQUENCE_NAME;
    
    private boolean sequenceExists(SessionImplementor si, Object o) {
        try (PreparedStatement ps = si.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement("SELECT COUNT(*) FROM pg_class WHERE relname='seq_mask_id'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("RESULTS OF QUERY: " + rs.getInt(1));
                    int countVal = rs.getInt(1);
                    return (countVal >= 1);
                }
                return false;
            }
        } catch (SQLException sqle) {
            LOGGER.error("EXISTS: " + sqle);
            return false;
        }
    }

    @Override
    public Serializable generate(SessionImplementor si, Object o) throws HibernateException {
        Serializable result = null;
        if (!sequenceExists(si, o)) {
            try {
                LOGGER.info("CREATING SEQUENCE");
                si.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement().execute(CREATE_SEQUENCE);
                LOGGER.info("SEQUENCE CREATED, SQL: " + CREATE_SEQUENCE);
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        try (PreparedStatement ps = si.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement(SELECT_NEXTVAL)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int nextVal = rs.getInt(1);
                    result = PREFIX.concat("" + nextVal);
                    return result;
                }
                return null;
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
            return null;
        }
    }

}
