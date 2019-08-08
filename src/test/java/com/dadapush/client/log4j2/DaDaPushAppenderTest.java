package com.dadapush.client.log4j2;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class DaDaPushAppenderTest {

  private static final Logger logger = LogManager.getLogger(DaDaPushAppenderTest.class);

  @Test
  public void append() {
    logger.info("test info");
    logger.error("test error",new RuntimeException("mock exception"));
  }
}
