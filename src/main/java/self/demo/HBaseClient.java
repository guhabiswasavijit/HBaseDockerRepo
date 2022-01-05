package self.demo;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class HBaseClient {
	static Logger log = Logger.getLogger(HBaseClient.class.getName());
	static final String NAME_NODE = "hdfs://hbaseJdbcJavaCli:8082";
	public static void main(String[] args) {
		Configuration config = new Configuration();
		config.set("fs.defaultFS", NAME_NODE);
		config.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		config.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());
		config.setInt("hbase.master.port",16000);
		config.set("hbase.master.info.bindAddress","hbaseJdbcJavaCli");
		config.set("hbase.zookeeper.quorum","zookeeper-server");
		config.setInt("hbase.zookeeper.property.clientPort",2181);
		try {
			  HBaseAdmin.available(config);
			  log.info("Coonected to HBase Admin");
			  Connection connection = ConnectionFactory.createConnection(config);
			  TableDescriptor tableDescriptor  = TableDescriptorBuilder
                      .newBuilder(TableName.valueOf("employee"))
                      .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder("personal".getBytes()).build())
                      .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder("professional".getBytes()).build())
                      .build();
			  connection.getAdmin().createTable(tableDescriptor);
			  log.info("Created table to employee");
			  Table tab = connection.getTable(TableName.valueOf("employee"));
              Put row1 = new Put(Bytes.toBytes("row1")); 
              row1.addColumn("personal".getBytes(),"name".getBytes(), "raju".getBytes());
              row1.addColumn("personal".getBytes(),"city".getBytes(), "hyderabad".getBytes());
              row1.addColumn("professional".getBytes(),"designation".getBytes(), "manager".getBytes());
              row1.addColumn("professional".getBytes(),"salary".getBytes(), "50000".getBytes());
              tab.put(row1);
              tab.close();
              log.info("Inserted data successfully");
   
		} catch (MasterNotRunningException ex) {
			log.error(ex);
		} catch (ZooKeeperConnectionException ex) {
			log.error(ex);
		} catch (IOException ex) {
			log.error(ex);
		}
	}

}
