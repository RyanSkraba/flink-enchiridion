package com.skraba.flink.enchiridion.delta;

import java.util.Arrays;
import java.util.Random;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.util.DataFormatConverters;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;

public class SyntheticDataUtil {

  public static final RowType STORE_ROW_TYPE =
      new RowType(
          Arrays.asList(
              new RowType.RowField("id", new IntType()),
              new RowType.RowField("name", new VarCharType()),
              new RowType.RowField("quantity", new IntType()),
              new RowType.RowField("price", new DoubleType())));

  public static DataGeneratorSource<RowData> getStoreSource(long num) {
    return getStoreSource(RateLimiterStrategy.noOp(), num);
  }

  public static DataGeneratorSource<RowData> getStoreSource(RateLimiterStrategy rate, long num) {

    GeneratorFunction<Long, RowData> generatorFunction =
        new GeneratorFunction<>() {
          private Random rnd;

          @Override
          public RowData map(Long index) throws Exception {
            if (rnd == null) rnd = new Random();
            return GenericRowData.of(
                index.intValue(),
                StringData.fromString("Item#" + rnd.nextInt(100)),
                rnd.nextInt(100),
                rnd.nextDouble() * 10);
          }
        };

    return new DataGeneratorSource<>(
        generatorFunction, num, rate, TypeInformation.of(RowData.class));
  }

  public static RowData convertRowToRowData(RowType rowType, Row row) {
    DataFormatConverters.DataFormatConverter<RowData, Row> CONVERTER =
        DataFormatConverters.getConverterForDataType(
            TypeConversions.fromLogicalToDataType(rowType));
    return CONVERTER.toInternal(row);
  }
}
