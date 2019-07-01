> **maxwell消费者**，提供全量及增量redis，elasticsearch数据过期和数据同步功能。
#### 依赖
* jdk1.8
* maxwell-1.22.1
* mysql5.7.26
* kafka_2.11-2.0.0
* redis4.0
* elasticsearch
#### 快速开始
一. 启动mysql服务
> service mysqld start
### 
``` mysql配置参考
datadir=/var/lib/mysql
    socket=/var/lib/mysql/mysql.sock
    # Disabling symbolic-links is recommended to prevent assorted security risks
    symbolic-links=0
    log-error=/var/log/mysqld.log
    pid-file=/var/run/mysqld/mysqld.pid
    bind-address =0.0.0.0
    server-id=1
    log-bin=master
    binlog_format=row
    wait_timeout=31536000
    interactive_timeout=31536000
    sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
```
二. 启动zookeeper(kafka依赖,当然你也可以用kafka内置的，取决于怎样配置kafka)

> bin/zkServer.sh start &
``` zoo.cfg配置
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/opt/pkg/java/zookeeper/zookeeper-3.4.13/data
logDir=/opt/pkg/java/zookeeper/zookeeper-3.4.13/log
clientPort=2181
```

三. 启动kafka

> bin/kafka-server-start.sh config/server.properties &
``` server.properties 配置
broker.id=0
dataDir=/opt/pkg/java/kafka_2.11-2.0.0/data
clientPort=9092
listeners=PLAINTEXT://192.168.225.1:9092
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
num.partitions=1
num.recovery.threads.per.data.dir=1
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=192.168.225.1:2181
zookeeper.connection.timeout.ms=6000
```

四. 启动maxwell

> bin/maxwell --config=../config.properties --producer=kafka --kafka.bootstrap.servers=192.168.225.1:9092 --kafka_topic=maxwell &
``` config.properties 配置
log_level=info
producer=kafka
kafka.bootstrap.servers=192.168.225.1:9092
host=192.168.225.1
user=maxwell
password=maxwell
kafka.compression.type=snappy
kafka.retries=0
kafka.acks=1
metrics_type=jmx,slf4j,http
metrics_slf4j_interval=60
http_port=8222
http_path_prefix=/maxwell
```
五. 启动redis
> ./redis-server ../redis.conf
``` redis.conf配置
bind 0.0.0.0
protected-mode no
port 6379
tcp-backlog 511
timeout 0
tcp-keepalive 300
daemonize no
supervised no
pidfile /var/run/redis_6379.pid
loglevel notice
logfile ""
databases 16
always-show-logo yes
save 900 1
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
dir ./
slave-serve-stale-data yes
slave-read-only yes
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-disable-tcp-nodelay no
requirepass foobared
lazyfree-lazy-eviction no
lazyfree-lazy-expire no
lazyfree-lazy-server-del no
slave-lazy-flush no
appendonly no
appendfilename "appendonly.aof"
yre, use "everysec".
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
aof-use-rdb-preamble no
lua-time-limit 5000
yvalue of zero forces the logging of every command.
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
yeshold. These thresholds can be configured using the following directives.
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
list-compress-depth 0
y in order to use this special memory saving encoding.
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit slave 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
```

#### maxwell消息格式
>{"database":"test","table":"sys_order","type":"update","ts":1559640375,"xid":2012,"commit":true,"data":{"id":1,"order_code":"1","category":0,"goods_name":"牙膏","is_send_express":1,"is_deleted":0,"gmt_create":"2019-06-04 17:21:45","gmt_modify":"2019-06-04 17:26:15"},"old":{"is_send_express":0,"gmt_modify":"2019-06-04 17:25:25"}}

