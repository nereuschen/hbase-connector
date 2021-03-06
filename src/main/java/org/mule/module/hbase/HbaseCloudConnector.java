/**
is * Mule HBase Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.hbase;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.hbase.api.BloomFilterType;
import org.mule.module.hbase.api.CompressionType;
import org.mule.module.hbase.api.HBaseService;
import org.mule.module.hbase.api.impl.RPCHBaseService;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RowLock;

/**
 * <p>
 * HBase connector
 * </p>
 * <p>
 * It delegates each operation on a {@link HBaseService} and it accepts custom
 * configuration in a Key-Value fashion
 * </p>
 * 
 * @author Pablo Martin Grigolatto
 * @since Apr 18, 2011
 */
@Connector(namespacePrefix = "hbase")
public class HbaseCloudConnector implements Initialisable
{
    @Property(name = "facade-ref", optional = true)
    private HBaseService facade;

    /**
     * HBase internal configuration properties. Consult HBase documentation.
     */
    @Property(name = "properties-ref", optional = true)
    private Map<String, String> properties;

    public HbaseCloudConnector()
    {
        properties = Collections.emptyMap();
    }

    // ------------ Admin Operations

    /**
     * Answers if the HBase server is reachable
     * 
     * {@code <hbase:is-alive-server />}
     * 
     * @return true if the server can be reached and the master node is alive, false
     *         otherwise.
     */
    @Operation
    public boolean isAliveServer()
    {
        return facade.alive();
    }

    /**
     * Creates a new table given its name. The descriptor must be unique and not
     * reserved.
     * 
     * {@code <hbase:create-table tableName="#[head:name]" />}
     * 
     * @param tableName the descriptor for the new table.
     */
    @Operation
    public void createTable(@Parameter(optional = false) final String tableName)
    {
        facade.createTable(tableName);
    }

    /**
     * Answers if a given table exists, regardless it is enabled or not
     * 
     * {@code <hbase:exists-table tableName="#[header:tableName]" />}
     * 
     * @param tableName the table name
     * @return true only if the table exists, false otherwise
     */
    @Operation
    public boolean existsTable(@Parameter(optional = false) final String tableName)
    {
        return facade.existsTable(tableName);
    }

    /**
     * Disables and deletes an existent table.
     * 
     * {@code <hbase:delete-table tableName="#[header:tableName]" />}
     * 
     * @param tableName name of table to delete
     */
    @Operation
    public void deleteTable(@Parameter(optional = false) final String tableName)
    {
        facade.deleteTable(tableName);
    }

    /**
     * Answers if the given existent table is enabled.
     * 
     * {@code <hbase:is-enabled-table tableName="#[header:tableName]" />}
     * 
     * @param tableName name of the table to query for its enabling state
     * @return true only if the table was disabled. False otherwise
     */
    @Operation
    public boolean isEnabledTable(@Parameter(optional = false) final String tableName)
    {
        return !facade.isDisabledTable(tableName);
    }

    /**
     * Enables an existent table.
     * 
     * {@code <hbase:enable-table tableName="#[header:tableName]" />}
     * 
     * @param tableName name of the table to enable
     */
    @Operation
    public void enableTable(@Parameter(optional = false) final String tableName)
    {
        facade.enableTable(tableName);
    }

    /**
     * Disables an existent table
     * 
     * {@code <hbase:disable-table tableName="#[header:tableName]" />}
     * 
     * @param tableName the table name to disable
     */
    @Operation
    public void disableTable(@Parameter(optional = false) final String tableName)
    {
        facade.disabeTable(tableName);
    }

    /**
     * Adds a column family to a table given a table and column name. This operation
     * gracefully handles necessary table disabling and enabled.
     * 
     * {@code <hbase:add-column-family tableName="#[header:tableName]" columnFamilyName="#[header:columnFamiliyName]" />}
     * 
     * @param tableName the name of the target table
     * @param columnFamilyName the name of the column
     * @param maxVersions the optional maximum number of versions the column family
     *            supports
     * @param inMemory if all the column values will be stored in the region's cache
     * @param scope replication scope: 0 for locally scoped data (data for this column family will not be replicated) and 1 for globally scoped data (data will be replicated to all peers.)) 
     */
    @Operation
    public void addColumnFamily(@Parameter(optional = false) final String tableName,
                                @Parameter(optional = false) final String columnFamilyName,
                                @Parameter(optional = true) final Integer maxVersions,
                                @Parameter(optional = true, defaultValue = "false") final Boolean inMemory,
                                @Parameter(optional = true) final Integer scope)
    {
        facade.addColumn(tableName, columnFamilyName, maxVersions, inMemory, scope);
    }

    /**
     * Answers if column family exists.
     * 
     * {@code <hbase:exists-column-family tableName="#[header:tableName]" columnFamilyName="#[header:columnFamiliyName]" />}
     * 
     * @param tableName the target table name
     * @param columnFamilyName the target column family name
     * @return true if the column exists, false otherwise
     */
    @Operation
    public boolean existsColumnFamily(@Parameter(optional = false) final String tableName,
                                      @Parameter(optional = false) final String columnFamilyName)
    {
        return facade.existsColumn(tableName, columnFamilyName);
    }

    /**
     * Changes one or more properties of a column family in a table. This operation
     * gracefully handles necessary table disabling and enabled.
     * 
     * {@code <hbase:modify-column-family tableName="#[header:tableName]"
     *       columnFamilyName="#[header:columnFamiliyName]" blocksize="#[header:blockSize]"
     *       compactionCompressionType="LZO" />}
     *       
     * @param tableName required the target table
     * @param columnFamilyName required the target column family
     * @param maxVersions the new max amount of versions
     * @param blocksize the the new block size
     * @param compressionType the new compression type
     * @param compactionCompressionType the new compaction compression type
     * @param inMemory new value for if values are stored in Region's cache
     * @param timeToLive new ttl
     * @param blockCacheEnabled new value of enabling block cache
     * @param bloomFilterType new value of bloom filter type
     * @param replicationScope new value for replication scope
     * @param values other custom parameters values
     */
    @Operation
    public void modifyColumnFamily(@Parameter(optional = false) final String tableName,
                                   @Parameter(optional = false) final String columnFamilyName,
                                   @Parameter(optional = true) final Integer maxVersions,
                                   @Parameter(optional = true) final Integer blocksize,
                                   @Parameter(optional = true) final CompressionType compressionType,
                                   @Parameter(optional = true) final CompressionType compactionCompressionType,
                                   @Parameter(optional = true) final Boolean inMemory,
                                   @Parameter(optional = true) final Integer timeToLive,
                                   @Parameter(optional = true) final Boolean blockCacheEnabled,
                                   @Parameter(optional = true) final BloomFilterType bloomFilterType,
                                   @Parameter(optional = true) final Integer replicationScope,
                                   @Parameter(optional = true) final Map<String, String> values)
    {
        facade.modifyColumn(tableName, columnFamilyName, maxVersions, blocksize, compressionType,
            compactionCompressionType, inMemory, timeToLive, blockCacheEnabled, bloomFilterType,
            replicationScope, values);
    }

    /**
     * Delete a column family
     * 
     *  {@code <hbase:delete-column-family tableName="#[header:tableName]"
     *       columnFamilyName="#[header:columnFamiliyName]" blocksize="#[header:blockSize]"
     *       compactionCompressionType="LZO" />}
     *       
     * @param tableName required the target table
     * @param columnFamilyName required the target column family
     */
    @Operation
    public void deleteColumnFamily(@Parameter(optional = false) final String tableName,
                                   @Parameter(optional = false) final String columnFamilyName)
    {
        facade.deleteColumn(tableName, columnFamilyName);
    }

    // ------------ Row Operations

    /**
     * Answers the values at the given row - (table, row) combination
     * 
     * {@code <hbase:get-values tableName="#[header:tableName]" rowKey="#[header:rowKey]" />}
     * 
     * @param tableName required the target table
     * @param rowKey    
     * @param maxVersions
     * @param timestamp
     * @return the result
     */
    @Operation
    public Result getValues(@Parameter(optional = false) final String tableName,
                            @Parameter(optional = false) final String rowKey,
                            @Parameter(optional = true) final Integer maxVersions,
                            @Parameter(optional = true) final Long timestamp)
    {
        return facade.get(tableName, rowKey, maxVersions, timestamp);
    }

    /**
     * Saves a value at the specified (table, row, familyName, familyQualifier,
     * timestamp) combination
     * 
     * {@code <hbase:put-value tableName="t1" rowKey="r1" columnFamilyName="f1" 
     *                         columnQualifier="q1" value="v1" />}
     *           
     * @param tableName required the target table
     * @param rowKey
     * @param columnFamilyName the column family dimension
     * @param columnQualifier the column qualifier dimension
     * @param timestamp the version dimension
     * @param value the value to put. It must be either a byte array or a
     *            serializable object. As a special case, strings are saved always in
     *            standard utf-8 format.
     * @param writeToWAL
     * @param lock
     */
    @Operation
    public void putValue(@Parameter(optional = false) final String tableName,
                         @Parameter(optional = false) final String rowKey,
                         @Parameter(optional = false) final String columnFamilyName,
                         @Parameter(optional = false) final String columnQualifier,
                         @Parameter(optional = true) final Long timestamp,
                         @Parameter(optional = false) final Object value,
                         @Parameter(optional = true, defaultValue = "true") final boolean writeToWAL,
                         @Parameter(optional = true) final RowLock lock)
    {
        facade.put(tableName, rowKey, columnFamilyName, columnQualifier, timestamp, value, writeToWAL, lock);
    }

    /**
     * Deletes the values at a given row
     * 
     * {@code <hbase:delete-values tableName="#[variable:tableName]" rowKey="[variable:rowKey]" />}
     *            
     * @param tableName
     * @param rowKey
     * @param columnFamilyName set null to delete all column families in the
     *            specified row
     * @param columnQualifier the qualifier of the column values to delete. If no
     *            qualifier is specified, the operation will affect all the qulifiers
     *            for the given column family name to delete. Thus it has only sense
     *            if deleteColumnFamilyName is specified
     * @param timestamp the timestamp of the values to delete. If no timestamp is
     *            specified, the most recent timestamp for the deleted value is used.
     *            Only has sense if deleteColumnFamilyName is specified
     * @param deleteAllVersions if all versions should be deleted,or only those more
     *            recent than the deleteTimestamp. Only has sense if
     *            deleteColumnFamilyName and deleteColumnQualifier are specified
     * @param lock
     */
    @Operation
    public void deleteValues(@Parameter(optional = false) final String tableName,
                             @Parameter(optional = false) final String rowKey,
                             @Parameter(optional = true) final String columnFamilyName,
                             @Parameter(optional = true) final String columnQualifier,
                             @Parameter(optional = true) final Long timestamp,
                             @Parameter(optional = true, defaultValue = "false") final boolean deleteAllVersions,
                             @Parameter(optional = true) final RowLock lock)
    {
        facade.delete(tableName, rowKey, columnFamilyName, columnQualifier, timestamp, deleteAllVersions,
            lock);
    }

    /**
     * Scans across all rows in a table, returning a scanner over it
     *
     * {@code <hbase:scan-table tableName="#[map-payload:tableName]"
     *                          columnFamilyName="#[map-payload:columnFamiliyName]" 
     *                          startRowKey="#[map-payload:firstRowKey]" />}
     * 
     * @param tableName limits the scan to a specific table. This is the only
     *            required argument.
     * @param columnFamilyName limits the scan to a specific column family or null
     * @param columnQualifier limits the scan to a specific column or null. Requires
     *            a columnFamilyName to be defined.
     * @param timestamp limits the scan to a specific timestamp
     * @param maxTimestamp get versions of columns only within the specified
     *            timestamp range: [timestamp, maxTimestamp)
     * @param caching the number of rows for caching
     * @param batch the maximum number of values to return for each call to next() in
     *            the ResultScanner
     * @param cacheBlocks the number of rows for caching that will be passed to
     *            scanners
     * @param maxVersions limits the number of versions on each column
     * @param allVersions get all available versions on each column
     * @param startRowKey limits the beginning of the scan to the specified row
     *            inclusive
     * @param stopRowKey limits the end of the scan to the specified row exclusive
     * @param fetchSize the number of results internally fetched by request to the
     *            HBase server. Increase it for improving network efficiency, or decrease it
     *            for reducing memory usage 
     * @return an Iterable of Result's. It may be used with a collection splitter.  
     */
    @Operation
    public Iterable<Result> scanTable(@Parameter(optional = false) final String tableName,
                                   @Parameter(optional = true) final String columnFamilyName,
                                   @Parameter(optional = true) final String columnQualifier,
                                   @Parameter(optional = true) final Long timestamp,
                                   @Parameter(optional = true) final Long maxTimestamp,
                                   @Parameter(optional = true) final Integer caching,
                                   @Parameter(optional = true, defaultValue = "true") final boolean cacheBlocks,
                                   @Parameter(optional = true, defaultValue = "1") final int maxVersions,
                                   @Parameter(optional = true) final String startRowKey,
                                   @Parameter(optional = true) final String stopRowKey, 
                                   @Parameter(optional = true, defaultValue = "50") int fetchSize)
    {
        return facade.scan(tableName, columnFamilyName, columnQualifier, timestamp, maxTimestamp, caching,
            cacheBlocks, maxVersions, startRowKey, stopRowKey, fetchSize);
    }

    /**
     * Atomically increments the value of at a (table, row, familyName,
     * familyQualifier) combination. If the cell value does not yet exist it is
     * initialized to amount.
     *
     * {@code <hbase:increment-value tableName="#[map-payload:tableName]"
     *      columnFamilyName="#[map-payload:columnFamiliyName]"
     *      columnQualifier="#[map-payload:columQualifier]" 
     *      rowKey="#[map-payload:rowKey]"
     *      amount="10"
     *      writeToWAL="false"/> 
     * }
     * 
     * @param tableName the name of the table that contains the cell to increment.  
     * @param rowKey the row key that contains the cell to increment.
     * @param columnFamilyName the column family of the cell to increment.
     * @param columnQualifier the column qualifier of the cell to increment.
     * @param amount the amount to increment the cell with (or decrement, if the
     * amount is negative).
     * @param writeToWAL set it to false means that in a fail scenario, you will lose
     *            any increments that have not been flushed.
     * @return the new value, post increment
     */
    @Operation
    public long incrementValue(@Parameter(optional = false) final String tableName,
                               @Parameter(optional = false) final String rowKey,
                               @Parameter(optional = false) final String columnFamilyName,
                               @Parameter(optional = false) final String columnQualifier,
                               @Parameter(optional = false) final long amount,
                               @Parameter(optional = true, defaultValue = "true") final boolean writeToWAL)
    {
        return facade.increment(tableName, rowKey, columnFamilyName, columnQualifier, amount, writeToWAL);
    }

    /**
     * Atomically checks if a value at a (table, row,family,qualifier) matches the
     * given one. If it does, it performs the put.
     * 
     * {@code <hbase:check-and-put-value tableName="table-name"
     * rowKey="row-key" checkColumnFamilyName="f1" checkColumnQualifier="q1"
     * checkValue="somevalue" putColumnFamilyName="f2" putColumnQualifier="q2"
     * putTimestamp="putTimestamp" value="new putvalue" />}
     * 
     * @param tableName the name of the table that contains the cell to check. 
     * @param rowKey the row key that contains the cell to check.
     * @param checkColumnFamilyName the column family of the cell to check.
     * @param checkColumnQualifier the column qualifier of the cell to check.
     * @param checkValue the value to check. It must be either a byte array or a
     *            serializable object. As a special case, strings are saved always in
     *            standard utf-8 format.
     * @param putColumnFamilyName the column family of the cell to put.
     * @param putColumnQualifier the column qualifier of the cell to put.
     * @param putTimestamp the version dimension to put.
     * @param value the value to put. It must be either a byte array or a
     *            serializable object. As a special case, strings are saved always in
     *            standard utf-8 format.
     * @param writeToWAL set it to false means that in a fail scenario, you will lose
     *            any increments that have not been flushed.
     * @param lock
     * @return true if the new put was executed, false otherwise
     */
    @Operation
    public boolean checkAndPutValue(@Parameter(optional = false) final String tableName,
                                    @Parameter(optional = false) final String rowKey,
                                    @Parameter(optional = false) final String checkColumnFamilyName,
                                    @Parameter(optional = false) final String checkColumnQualifier,
                                    @Parameter(optional = false) final Object checkValue,
                                    @Parameter(optional = false) final String putColumnFamilyName,
                                    @Parameter(optional = false) final String putColumnQualifier,
                                    @Parameter(optional = true) final Long putTimestamp,
                                    @Parameter(optional = false) final Object value,
                                    @Parameter(optional = true, defaultValue = "true") final boolean putWriteToWAL,
                                    @Parameter(optional = true) final RowLock lock)
    {
        return facade.checkAndPut(tableName, rowKey, checkColumnFamilyName, checkColumnQualifier, checkValue,
            putColumnFamilyName, putColumnQualifier, putTimestamp, value, putWriteToWAL, lock);
    }

    /**
     * Atomically checks if a value at a (table, row,family,qualifier) matches the
     * given one. If it does, it performs the delete.
     * 
     * {@code
     * <hbase:check-and-delete-value tableName="table-name"
     * rowKey="row-key" checkColumnFamilyName="f1" checkColumnQualifier="q1"
     * checkValue="somevalue" deleteColumnFamilyName="f2"
     * deleteColumnQualifier="q2" deleteTimestamp="putTimestamp" /> 
     * }
     * @param tableName the name of the table that contains the cell to check. 
     * @param rowKey the row key that contains the cell to check.
     * @param checkColumnFamilyName the column family of the cell to check.
     * @param checkValue the value to check. It must be either a byte array or a
     *            serializable object. As a special case, strings are saved always in
     *            standard utf-8 format.
     * @param deleteColumnFamilyName
     * @param deleteColumnQualifier the qualifier of the column values to delete. If
     *            no qualifier is specified, the operation will affect all the
     *            qulifiers for the given column family name to delete. Thus it has
     *            only sense if deleteColumnFamilyName is specified
     * @param deleteTimestamp the timestamp of the values to delete. If no timestamp
     *            is specified, the most recent timestamp for the deleted value is
     *            used. Only has sense if deleteColumnFamilyName is specified
     * @param deleteAllVersions if all versions should be deleted,or only those more
     *            recent than the deleteTimestamp. Only has sense if
     *            deleteColumnFamilyName and deleteColumnQualifier are specified
     * @param lock
     * @return true if the new delete was executed, false otherwise
     */
    @Operation
    public boolean checkAndDeleteValue(@Parameter(optional = false) final String tableName,
                                       @Parameter(optional = false) final String rowKey,
                                       @Parameter(optional = false) final String checkColumnFamilyName,
                                       @Parameter(optional = false) final String checkColumnQualifier,
                                       @Parameter(optional = false) final Object checkValue,
                                       @Parameter(optional = false) final String deleteColumnFamilyName,
                                       @Parameter(optional = false) final String deleteColumnQualifier,
                                       @Parameter(optional = true) final Long deleteTimestamp,
                                       @Parameter(optional = true, defaultValue = "false") final boolean deleteAllVersions,
                                       @Parameter(optional = true) final RowLock lock)
    {
        return facade.checkAndDelete(tableName, rowKey, checkColumnFamilyName, checkColumnQualifier,
            checkValue, deleteColumnFamilyName, deleteColumnQualifier, deleteTimestamp, deleteAllVersions,
            lock);
    }

    // ------------ Configuration

    public void setFacade(HBaseService facade)
    {
        this.facade = HBaseServiceAdaptor.adapt(facade);
    }

    public HBaseService getFacade()
    {
        return facade;
    }

    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = new HashMap<String, String>(properties);
    }

    /** @see org.mule.api.lifecycle.Initialisable#initialise() */
    public void initialise() throws InitialisationException
    {
        if (facade == null)
        {
            setFacade(new RPCHBaseService());
            facade.addProperties(properties);
        }
    }

}
