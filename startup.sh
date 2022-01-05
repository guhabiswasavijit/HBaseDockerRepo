#!/bin/bash
touch /run/openrc/softlevel
/etc/init.d/sshd start
hdfs --daemon start namenode
hdfs --daemon start datanode
hdfs --daemon start portmap
hdfs --daemon start nfs3
hdfs dfs -mkdir /hbase