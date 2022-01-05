package self.demo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.thrift.generated.ColumnDescriptor;
import org.apache.hadoop.hbase.thrift.generated.Hbase;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class HBaseThriftClient {
	static Logger log = Logger.getLogger(HBaseThriftClient.class.getName());
	public static void main(String[] args) {
		try {
            TTransport socket = new TSocket("hbaseJdbcJavaCli", 9090);
            TProtocol protocol = new TBinaryProtocol(socket, true, true);
            Hbase.Client client = new Hbase.Client(protocol);
            socket.open();
            log.debug("open socket connection");
            try {
            	log.debug("creating tables...");
            	ByteBuffer buffer = ByteBuffer.wrap("Employee".getBytes());
            	ArrayList<ColumnDescriptor> columns = new ArrayList<>(2);
                ColumnDescriptor col;
                col = new ColumnDescriptor();
                col.name = ByteBuffer.wrap(Bytes.toBytes("entry:"));
                col.timeToLive = Integer.MAX_VALUE;
                col.maxVersions = 10;
                columns.add(col);
                col = new ColumnDescriptor();
                col.name = ByteBuffer.wrap(Bytes.toBytes("unused:"));
                col.timeToLive = Integer.MAX_VALUE;
                columns.add(col);
            	client.createTable(buffer, columns);            
            } catch (Exception ex) {
                log.error(ex);
            }
            socket.close();
            log.debug("close socket connection");
        } catch (TTransportException ex) {
            log.error(ex);
        } 
	}

}
