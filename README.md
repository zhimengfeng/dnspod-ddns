# dnspod-ddns

腾讯云域名动态解析程序。

java版本。
采用小米路由器做主路由，通过小米路由器的管理页面获取公网IP。

启动方式:`java -Dfile.encoding=UTF-8 -jar DNSPod-DDNS-1.0.jar > run.log 2>&1`;
如果需要开机启动，那么启动时需要指定一个延迟时间，等待服务器网络初始化完成，否则第一次解析会失败，不过不影响后续解析。`java -Dfile.encoding=UTF-8 -jar DNSPod-DDNS-1.0.jar 10 > run.log 2>&1`

程序暂时默认30分钟检测一次IP地址（通过小米路由器，主路由拨号模式），如果IP地址发生改变，则调用dnspod的[修改解析记录](https://cloud.tencent.com/document/product/302/8511)接口更新域名指向的公网IP。
并且指定每隔一日，将进行一次强制更新。

程序需要配置的部分均在`src/main/resource/config.yml`中，并有详细说明。
