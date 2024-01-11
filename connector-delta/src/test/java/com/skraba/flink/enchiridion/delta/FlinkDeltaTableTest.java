package com.skraba.flink.enchiridion.delta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FlinkDeltaTableTest {

  @Test
  public void testDataBoundedSink(@TempDir java.nio.file.Path dst) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setRuntimeMode(RuntimeExecutionMode.BATCH);
    env.enableCheckpointing(10, CheckpointingMode.EXACTLY_ONCE);

    StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

    tEnv.executeSql("CREATE CATALOG myCatalog WITH ('type' = 'delta-catalog')");
    tEnv.executeSql("USE CATALOG myCatalog");

    tEnv.executeSql(
        String.format(
            "CREATE TABLE synthetic ("
                + "    id INT,"
                + "    name STRING,"
                + "    quantity INT,"
                + "    price DOUBLE"
                + ") WITH ('connector' = 'delta', 'table-path' = '%s')",
            dst));

    tEnv.executeSql("INSERT INTO synthetic VALUES (1, 'Item#1', 1, 1)");

    tEnv.executeSql("INSERT INTO synthetic VALUES (2, 'Item#2', 2, 2)").await();

    TableResult res = tEnv.executeSql("SELECT * FROM synthetic");

    int count = 0;
    try (CloseableIterator<Row> it = res.collect()) {
      while (it.hasNext()) {
        count++;
        System.out.println(it.next());
      }
    }

    assertThat(count, is(2));
    assertThat(dst.resolve("_delta_log").toFile(), anExistingDirectory());
  }
}
