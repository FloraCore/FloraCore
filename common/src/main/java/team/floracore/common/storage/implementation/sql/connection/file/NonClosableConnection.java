package team.floracore.common.storage.implementation.sql.connection.file;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A wrapper around a {@link java.sql.Connection} which blocks usage of the default {@link #close()} method.
 */
public class NonClosableConnection implements Connection {
    private final Connection delegate;

    public NonClosableConnection(Connection delegate) {
        this.delegate = delegate;
    }

    public final void shutdown() throws SQLException {
        this.delegate.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this.delegate)) {
            return (T) this.delegate;
        }
        return this.delegate.unwrap(iface);
    }    @Override
    public final void close() throws SQLException {
        // do nothing
    }

    @Override
    public final boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this.delegate) || this.delegate.isWrapperFor(iface);
    }

    // Forward to the delegate connection
    @Override
    public Statement createStatement() throws SQLException {
        return this.delegate.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.delegate.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.delegate.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.delegate.nativeSQL(sql);
    }



    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.delegate.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.delegate.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        this.delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.delegate.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.delegate.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.delegate.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.delegate.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.delegate.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.delegate.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.delegate.setCatalog(catalog);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.delegate.getTransactionIsolation();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.delegate.setTransactionIsolation(level);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.delegate.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.delegate.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.delegate.setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.delegate.getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.delegate.setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.delegate.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.delegate.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.delegate.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.delegate.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.delegate.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.delegate.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.delegate.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.delegate.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.delegate.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.delegate.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.delegate.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.delegate.setClientInfo(name, value);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.delegate.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.delegate.getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.delegate.setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.delegate.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.delegate.createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.delegate.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.delegate.setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.delegate.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.delegate.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.delegate.getNetworkTimeout();
    }

}
