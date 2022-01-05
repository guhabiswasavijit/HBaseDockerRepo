FROM dockette/jdk8:latest
LABEL maintainer="Avijit GuhaBiswas <guhabiswas.avijit@gmail.com>"
LABEL name="admin/hbase-jdk8"

RUN apk add --no-cache bash wget openssh-server openssh-client vim sudo curl tar openrc\
    && adduser --disabled-password --home /home/admin --shell /bin/bash admin\
	&& echo 'admin ALL=(ALL) NOPASSWD:ALL' >>/etc/sudoers
	
USER root
RUN echo "root:root" | chpasswd
RUN mkdir -p /data/hdfs-nfs/
RUN mkdir -p /opt/bin
WORKDIR /opt
RUN curl -L https://archive.apache.org/dist/hadoop/core/hadoop-2.7.0/hadoop-2.7.0.tar.gz -s -o - | tar -xzf -
RUN mv hadoop-2.7.0 hadoop
ENV HBASE_VERSION=${HBASE_VERSION:-2.4.9}
RUN curl -k -L https://dlcdn.apache.org/hbase/${HBASE_VERSION}/hbase-${HBASE_VERSION}-bin.tar.gz -s -o - | tar -xzf -
RUN mv hbase-${HBASE_VERSION} hbase

ENV HADOOP_HOME /opt/hadoop
RUN echo "export HDFS_NAMENODE_USER=root" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh &&\
    echo "export HDFS_DATANODE_USER=root" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh &&\
	echo "export HDFS_SECONDARYNAMENODE_USER=root" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh &&\
	echo "export JAVA_HOME=/usr/lib/jvm/default-jvm" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh &&\
	echo "export HADOOP_HOME=/opt/hadoop" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh	
	
COPY core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
COPY hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml

ENV PATH /opt/hadoop/bin:/opt/hadoop/sbin:$PATH
ENV HBASE_HOME /opt/hbase
ENV PATH $HBASE_HOME/bin:$PATH

	
RUN ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys && \
    chmod 0600 ~/.ssh/authorized_keys


ADD conf/hbase-site.xml $HBASE_HOME/conf
RUN hdfs namenode -format

WORKDIR /opt/bin
ADD startup.sh startup.sh
RUN chmod 777 startup.sh

EXPOSE 22
EXPOSE 16000
EXPOSE 16010
# hdfs://localhost:8020 
EXPOSE 8020
# HDFS namenode
EXPOSE 50020
# HDFS Web browser
EXPOSE 50070 



CMD rc-service sshd start\
    && hadoop-daemon.sh start namenode\
    && hadoop-daemon.sh start datanode\
    && hdfs dfs -mkdir /hbase\
	&& start-hbase.sh\
	&& hbase shell
