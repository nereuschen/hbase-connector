Mule Hbase Cloud Connector
==========================

Mule Cloud connector to Hbase

Installation
------------

The connector can either be installed for all applications running within the Mule instance or can be setup to be used
for a single application.

*All Applications*

Download the connector from the link above and place the resulting jar file in
/lib/user directory of the Mule installation folder.

*Single Application*

To make the connector available only to single application then place it in the
lib directory of the application otherwise if using Maven to compile and deploy
your application the following can be done:

Add the connector's maven repo to your pom.xml:

    <repositories>
        <repository>
            <id>muleforge-releases</id>
            <name>MuleForge Snapshot Repository</name>
            <url>https://repository.mulesoft.org/releases/</url>
            <layout>default</layout>
        </repsitory>
    </repositories>

Add the connector as a dependency to your project. This can be done by adding
the following under the dependencies element in the pom.xml file of the
application:

    <dependency>
        <groupId>org.mule.modules</groupId>
        <artifactId>mule-module-hbase</artifactId>
        <version>1.1-SNAPSHOT</version>
    </dependency>

Configuration
-------------

You can configure the connector as follows:

    <hbase:config facade="value" properties="value"/>

Here is detailed list of all the configuration attributes:

| attribute | description | optional | default value |
|:-----------|:-----------|:---------|:--------------|
|name|Give a name to this configuration so it can be later referenced by config-ref.|yes||
|facade||yes|
|properties|HBase internal configuration properties. Consult HBase documentation.|yes|




Is Alive Server
---------------

Answers if the HBase server is reachable



     <hbase:is-alive-server />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||

Returns if the server can be reached and the master node is alive, false otherwise.



Create Table
------------

Creates a new table given its name. The descriptor must be unique and not
reserved.



     <hbase:create-table tableName="#[head:name]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the descriptor for the new table.|no||



Exists Table
------------

Answers if a given table exists, regardless it is enabled or not



     <hbase:exists-table tableName="#[header:tableName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the table name|no||

Returns only if the table exists, false otherwise



Delete Table
------------

Disables and deletes an existent table.



     <hbase:delete-table tableName="#[header:tableName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|name of table to delete|no||



Is Enabled Table
----------------

Answers if the given existent table is enabled.



     <hbase:is-enabled-table tableName="#[header:tableName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|name of the table to query for its enabling state|no||

Returns only if the table was disabled. False otherwise



Enable Table
------------

Enables an existent table.



     <hbase:enable-table tableName="#[header:tableName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|name of the table to enable|no||



Disable Table
-------------

Disables an existent table



     <hbase:disable-table tableName="#[header:tableName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the table name to disable|no||



Add Column Family
-----------------

Adds a column family to a table given a table and column name. This operation
gracefully handles necessary table disabling and enabled.



     <hbase:add-column-family tableName="#[header:tableName]" columnFamilyName="#[header:columnFamiliyName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the name of the target table|no||
|columnFamilyName|the name of the column|no||
|maxVersions|the optional maximum number of versions the column family supports|yes||
|inMemory|if all the column values will be stored in the region's cache|yes|false|
|scope|replication scope: 0 for locally scoped data (data for this column family will not be replicated) and 1 for globally scoped data (data will be replicated to all peers.))|yes||



Exists Column Family
--------------------

Answers if column family exists.



     <hbase:exists-column-family tableName="#[header:tableName]" columnFamilyName="#[header:columnFamiliyName]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the target table name|no||
|columnFamilyName|the target column family name|no||

Returns if the column exists, false otherwise



Modify Column Family
--------------------

Changes one or more properties of a column family in a table. This operation
gracefully handles necessary table disabling and enabled.



     <hbase:modify-column-family tableName="#[header:tableName]"
          columnFamilyName="#[header:columnFamiliyName]" blocksize="#[header:blockSize]"
          compactionCompressionType="LZO" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|required the target table|no||
|columnFamilyName|required the target column family|no||
|maxVersions|the new max amount of versions|yes||
|blocksize|the the new block size|yes||
|compressionType|the new compression type|yes||*LZO*, *GZ*, *NONE*, *algorithm*
|compactionCompressionType|the new compaction compression type|yes||*LZO*, *GZ*, *NONE*, *algorithm*
|inMemory|new value for if values are stored in Region's cache|yes||
|timeToLive|new ttl|yes||
|blockCacheEnabled|new value of enabling block cache|yes||
|bloomFilterType|new value of bloom filter type|yes||*NONE*, *ROW*, *ROWCOL*, *bloomType*
|replicationScope|new value for replication scope|yes||
|values|other custom parameters values|yes||



Delete Column Family
--------------------

Delete a column family

 

     <hbase:delete-column-family tableName="#[header:tableName]"
          columnFamilyName="#[header:columnFamiliyName]" blocksize="#[header:blockSize]"
          compactionCompressionType="LZO" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|required the target table|no||
|columnFamilyName|required the target column family|no||



Get Values
----------

Answers the values at the given row - (table, row) combination



     <hbase:get-values tableName="#[header:tableName]" rowKey="#[header:rowKey]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|required the target table|no||
|rowKey||no||
|maxVersions||yes||
|timestamp||yes||

Returns result



Put Value
---------

Saves a value at the specified (table, row, familyName, familyQualifier,
timestamp) combination



     <hbase:put-value tableName="t1" rowKey="r1" columnFamilyName="f1" 
                            columnQualifier="q1" value="v1" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|required the target table|no||
|rowKey||no||
|columnFamilyName|the column family dimension|no||
|columnQualifier|the column qualifier dimension|no||
|timestamp|the version dimension|yes||
|value|the value to put. It must be either a byte array or a serializable object. As a special case, strings are saved always in standard utf-8 format.|no||
|writeToWAL||yes|true|
|lock||yes||



Delete Values
-------------

Deletes the values at a given row



     <hbase:delete-values tableName="#[variable:tableName]" rowKey="[variable:rowKey]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName||no||
|rowKey||no||
|columnFamilyName|set null to delete all column families in the specified row|yes||
|columnQualifier|the qualifier of the column values to delete. If no qualifier is specified, the operation will affect all the qulifiers for the given column family name to delete. Thus it has only sense if deleteColumnFamilyName is specified|yes||
|timestamp|the timestamp of the values to delete. If no timestamp is specified, the most recent timestamp for the deleted value is used. Only has sense if deleteColumnFamilyName is specified|yes||
|deleteAllVersions|if all versions should be deleted,or only those more recent than the deleteTimestamp. Only has sense if deleteColumnFamilyName and deleteColumnQualifier are specified|yes|false|
|lock||yes||



Scan Table
----------

Scans across all rows in a table, returning a scanner over it



     <hbase:scan-table tableName="#[map-payload:tableName]"
                             columnFamilyName="#[map-payload:columnFamiliyName]" 
                             startRowKey="#[map-payload:firstRowKey]" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|limits the scan to a specific table. This is the only required argument.|no||
|columnFamilyName|limits the scan to a specific column family or null|yes||
|columnQualifier|limits the scan to a specific column or null. Requires a columnFamilyName to be defined.|yes||
|timestamp|limits the scan to a specific timestamp|yes||
|maxTimestamp|get versions of columns only within the specified timestamp range: [timestamp, maxTimestamp)|yes||
|caching|the number of rows for caching|yes||
|cacheBlocks|the number of rows for caching that will be passed to scanners|yes|true|
|maxVersions|limits the number of versions on each column|yes|1|
|startRowKey|limits the beginning of the scan to the specified row inclusive|yes||
|stopRowKey|limits the end of the scan to the specified row exclusive|yes||
|fetchSize|the number of results internally fetched by request to the HBase server. Increase it for improving network efficiency, or decrease it for reducing memory usage|yes|50|

Returns Iterable of Result's. It may be used with a collection splitter.



Increment Value
---------------

Atomically increments the value of at a (table, row, familyName,
familyQualifier) combination. If the cell value does not yet exist it is
initialized to amount.



     <hbase:increment-value tableName="#[map-payload:tableName]"
         columnFamilyName="#[map-payload:columnFamiliyName]"
         columnQualifier="#[map-payload:columQualifier]" 
         rowKey="#[map-payload:rowKey]"
         amount="10"
         writeToWAL="false"/> 
    

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the name of the table that contains the cell to increment.|no||
|rowKey|the row key that contains the cell to increment.|no||
|columnFamilyName|the column family of the cell to increment.|no||
|columnQualifier|the column qualifier of the cell to increment.|no||
|amount|the amount to increment the cell with (or decrement, if the amount is negative).|no||
|writeToWAL|set it to false means that in a fail scenario, you will lose any increments that have not been flushed.|yes|true|

Returns new value, post increment



Check And Put Value
-------------------

Atomically checks if a value at a (table, row,family,qualifier) matches the
given one. If it does, it performs the put.



     <hbase:check-and-put-value tableName="table-name"
    rowKey="row-key" checkColumnFamilyName="f1" checkColumnQualifier="q1"
    checkValue="somevalue" putColumnFamilyName="f2" putColumnQualifier="q2"
    putTimestamp="putTimestamp" value="new putvalue" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the name of the table that contains the cell to check.|no||
|rowKey|the row key that contains the cell to check.|no||
|checkColumnFamilyName|the column family of the cell to check.|no||
|checkColumnQualifier|the column qualifier of the cell to check.|no||
|checkValue|the value to check. It must be either a byte array or a serializable object. As a special case, strings are saved always in standard utf-8 format.|no||
|putColumnFamilyName|the column family of the cell to put.|no||
|putColumnQualifier|the column qualifier of the cell to put.|no||
|putTimestamp|the version dimension to put.|yes||
|value|the value to put. It must be either a byte array or a serializable object. As a special case, strings are saved always in standard utf-8 format.|no||
|putWriteToWAL||yes|true|
|lock||yes||

Returns if the new put was executed, false otherwise



Check And Delete Value
----------------------

Atomically checks if a value at a (table, row,family,qualifier) matches the
given one. If it does, it performs the delete.



    
    <hbase:check-and-delete-value tableName="table-name"
    rowKey="row-key" checkColumnFamilyName="f1" checkColumnQualifier="q1"
    checkValue="somevalue" deleteColumnFamilyName="f2"
    deleteColumnQualifier="q2" deleteTimestamp="putTimestamp" /> 
    

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|tableName|the name of the table that contains the cell to check.|no||
|rowKey|the row key that contains the cell to check.|no||
|checkColumnFamilyName|the column family of the cell to check.|no||
|checkColumnQualifier||no||
|checkValue|the value to check. It must be either a byte array or a serializable object. As a special case, strings are saved always in standard utf-8 format.|no||
|deleteColumnFamilyName||no||
|deleteColumnQualifier|the qualifier of the column values to delete. If no qualifier is specified, the operation will affect all the qulifiers for the given column family name to delete. Thus it has only sense if deleteColumnFamilyName is specified|no||
|deleteTimestamp|the timestamp of the values to delete. If no timestamp is specified, the most recent timestamp for the deleted value is used. Only has sense if deleteColumnFamilyName is specified|yes||
|deleteAllVersions|if all versions should be deleted,or only those more recent than the deleteTimestamp. Only has sense if deleteColumnFamilyName and deleteColumnQualifier are specified|yes|false|
|lock||yes||

Returns if the new delete was executed, false otherwise





































