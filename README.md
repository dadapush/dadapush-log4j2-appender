## Log4j2 DaDaPush Notification Appender

## Usage

add dependency
```xml
<dependency>
  <groupId>com.dadapush.client</groupId>
  <artifactId>dadapush-log4j2-appender</artifactId>
  <version>1.0.0</version>
</dependency>
```

define appender

```xml
  <Appenders>
      <DaDaPush name="DaDaPush"
        basePath="https://www.dadapush.com"
        channelToken="YOUR_CHANNEL_TOKEN"
        title="[%-5level] %logger{36}"
      >
        <PatternLayout
          pattern="Time: %d{YYYY-MM-dd HH:mm:ss.SSS}\nLevel: %-5level\nLoggerName: %logger{36}\nMessage: %msg%n"/>
      </DaDaPush>
      <Async name="DaDaPush_Async">
        <AppenderRef ref="DaDaPush"/>
      </Async>
    </Appenders>
```

use appender
```xml
  <Root level="info">
    <AppenderRef ref="DaDaPush_Async" level="error"/>
  </Root>
```