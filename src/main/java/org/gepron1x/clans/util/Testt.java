package org.gepron1x.clans.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


public class Testt {
    public static void main(String[] args) {
        MiniMessage.get().parse("<gasy>K<<>>><L>KLOLLL<<<");
        String host = "45.140.16.24:3306";
        String database = "s1_test";
        String user = "u1_2q1iyu2P9M";
        String password = "^Liks43O2Ko^eFw6VJZiYxwx";
        Jdbi jdbi = Jdbi.create("jdbc:mysql://" + host + "/" + database, user, password);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(handle -> handle.execute("CREATE TABLE IF NOT EXISTS test (`count` INTEGER, `hello` INTEGER)"));
        jdbi.useExtension(JdbiTest.class, dao -> {
            dao.insert(List.of(new TestBean(0, 0), new TestBean(1, 2)));
            dao.drop();
        });
    }
    interface JdbiTest {
        @SqlUpdate("INSERT INTO test (`count`, `test`) VALUES <beans>")
        void insert(@BindBeanList(propertyNames = {"count", "hello"}) List<TestBean> beans);

        @SqlUpdate("DROP TABLE test")
        void drop();
    }

    static class TestBean {
        private int count;
        private int hello;

        TestBean(int count, int hello) {
            this.count = count;
            this.hello = hello;
        }
    }
}
