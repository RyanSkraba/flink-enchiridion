package com.skraba.flink.enchiridion.delta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;

import io.delta.flink.sink.DeltaSink;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FlinkDeltaDataStreamTest {

  @Test
  public void testDataBoundedSink(@TempDir java.nio.file.Path dst) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setRuntimeMode(RuntimeExecutionMode.BATCH);
    env.enableCheckpointing(10, CheckpointingMode.EXACTLY_ONCE);

    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set("parquet.compression", "SNAPPY");
    conf.set("io.delta.standalone.PARQUET_DATA_TIME_ZONE_ID", "UTC");

    DataGeneratorSource<RowData> src = SyntheticDataUtil.getStoreSource(10);

    DeltaSink<RowData> deltaSink =
        DeltaSink.forRowData(new Path(dst.toString()), conf, SyntheticDataUtil.STORE_ROW_TYPE)
            .build();

    env.fromSource(src, WatermarkStrategy.noWatermarks(), "Src")
        .setParallelism(2)
        .sinkTo(deltaSink)
        .setParallelism(3);

    env.execute();

    assertThat(dst.resolve("_delta_log").toFile(), anExistingDirectory());
  }

  public void testDataUnboundedSink(@TempDir java.nio.file.Path dst) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
    env.enableCheckpointing(1000, CheckpointingMode.EXACTLY_ONCE);

    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set("parquet.compression", "SNAPPY");
    conf.set("io.delta.standalone.PARQUET_DATA_TIME_ZONE_ID", "UTC");

    DataGeneratorSource<RowData> src =
        SyntheticDataUtil.getStoreSource(RateLimiterStrategy.perSecond(10), Integer.MAX_VALUE);

    DeltaSink<RowData> deltaSink =
        DeltaSink.forRowData(new Path(dst.toString()), conf, SyntheticDataUtil.STORE_ROW_TYPE)
            .build();

    env.fromSource(src, WatermarkStrategy.noWatermarks(), "Src")
        .setParallelism(2)
        .sinkTo(deltaSink)
        .setParallelism(3);

    // env.execute();

    assertThat(dst.resolve("_delta_log").toFile(), anExistingDirectory());
  }
}
