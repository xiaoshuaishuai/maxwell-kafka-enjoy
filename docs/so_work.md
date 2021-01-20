##### redis缓存策略
> 选择DB和redis缓存一致性方案<br/>
>1.**缓存失效**: 数据发生insert/update/delete操作，先修改数据库，删除缓存<br/>
2.**缓存加载**: 数据读取先查询缓存，如果缓存命中，直接返回，如果缓存未命中，查询DB，同时将DB返回数据更新到缓存(可以采取定时任务刷新DB数据到缓存来解决第一次查库问题或者也可以在触发缓存删除之后MQ通知重做缓存)

##### 需要解决问题

1. 数据库有数据，缓存没有数据；
2. 数据库有数据，缓存也有数据，数据不相等；
3. 数据库没有数据，缓存有数据。


1. 对于第一种，在读数据的时候，会自动把数据库的数据写到缓存，因此不一致自动消除(写缓存失败，后续发生缓存穿透的问题，这是另一个问题了)
2. 对于第二种，数据最终变成了不相等，但他们之前在某一个时间点一定是相等的（不管你使用懒加载还是预加载的方式，在缓存加载的那一刻，它一定和数据库一致）。这种不一致，一定是由于你更新数据所引发的。
前面我们讲了更新数据的策略，先更新数据库，然后删除缓存。因此，不一致的原因，一定是数据库更新了，但是删除缓存失败了。
3. 对于第三种，情况和第二种类似，你把数据库的数据删了，但是删除缓存的时候失败了

因此，最终的结论是，需要解决的不一致，产生的原因是更新数据库成功，但是删除缓存失败。

>删除缓存失败解决方案<br/>

1. 删除失败重试消息队列、重试机制、确保缓存尽快删除、可以考虑增加报警通知之类的
2. 缓存创建时默认都给过期时间
3. 综合业务考虑、是否需要定时刷新缓存、比如每天0点刷新某张表的缓存数据

2.3都是1的增强，如果能确保缓存删除，就能解决大部分问题。

##### 对redis缓存提供的功能
* 单表主键引导缓存
>举例：
环境=dev, database=test, table=sys_order, id=1,对应的单条缓存为=**dev:test:sys_order:1:item:1**
环境=test, database=test, table=sys_order, id=2,对应的单条缓存为=**test:test:sys_order:1:item:2**

* 全表缓存
>举例：
环境=dev, database=test, table=sys_order, id=1,对应的单条缓存为=**dev:test:sys_order:2:list**
环境=test, database=test, table=sys_order, id=2,对应的单条缓存为=**test:test:sys_order:2:list**

* 自定义缓存
>举例：
环境=dev, database=test, table=sys_order, id=1, goodsName=牙膏, deleted=0 要求按照goodsName和deleted字段构建缓存, 
对应的单条缓存为=**dev:test:sys_order:3:custom:%E7%89%99%E8%86%8F:0**
字段包含中文会进行URL编码，可以根据需要的字段进行多种组合构建key，缓存的数据支持动态的SQL排序
---