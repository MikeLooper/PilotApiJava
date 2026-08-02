package com.pilotapi.converters;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class MoneyUserType implements UserType<BigDecimal> {

    @Override
    public int getSqlType() {
        return Types.NUMERIC;
    }

    @Override
    public Class<BigDecimal> returnedClass() {
        return BigDecimal.class;
    }

    @Override
    public boolean equals(BigDecimal x, BigDecimal y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.compareTo(y) == 0;
    }

    @Override
    public int hashCode(BigDecimal x) {
        return x == null ? 0 : x.stripTrailingZeros().hashCode();
    }

    /**
     * Reads as a raw string to avoid JDBC driver locale-formatted parsing failures
     * (e.g. PostgreSQL money returns "$1,007.64"). Currency symbols, spaces, and
     * thousands separators are stripped before constructing the BigDecimal.
     */
    @Override
    public BigDecimal nullSafeGet(ResultSet rs, int position,
                                  SharedSessionContractImplementor session, Object owner) throws SQLException {
        String raw = rs.getString(position);
        if (rs.wasNull() || raw == null) {
            return null;
        }
        return new BigDecimal(raw.replaceAll("[^\\d.\\-]", ""));
    }

    /**
     * Binds as NUMERIC so both PostgreSQL (implicit assignment cast numeric→money)
     * and SQL Server (accepts numeric for money columns) accept the value.
     */
    @Override
    public void nullSafeSet(PreparedStatement st, BigDecimal value, int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.NUMERIC);
        } else {
            st.setBigDecimal(index, value);
        }
    }

    @Override
    public BigDecimal deepCopy(BigDecimal value) {
        return value; // BigDecimal is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(BigDecimal value) {
        return value;
    }

    @Override
    public BigDecimal assemble(Serializable cached, Object owner) {
        return (BigDecimal) cached;
    }
}
