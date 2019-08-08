package com.dadapush.client.log4j2;

import com.dadapush.client.ApiClient;
import com.dadapush.client.ApiException;
import com.dadapush.client.Configuration;
import com.dadapush.client.api.DaDaPushMessageApi;
import com.dadapush.client.model.MessagePushRequest;
import com.dadapush.client.model.ResultOfMessagePushResponse;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@SuppressWarnings("unused")
@Plugin(name = "DaDaPush", category = "Core", elementType = "appender", printObject = true)
public class DaDaPushAppender extends AbstractAppender {

  private static volatile DaDaPushAppender instance;

  private DaDaPushMessageApi apiInstance;
  private String channelToken;
  private Boolean failOnError;
  private Layout<? extends Serializable> titleLayout;
  private Layout<? extends Serializable> contentLayout;

  public DaDaPushAppender(String name, Filter filter,
      String basePath,
      String channelToken,
      Boolean failOnError,
      Layout<? extends Serializable> titleLayout,
      Layout<? extends Serializable> contentLayout
  ) {
    super(name, filter, contentLayout,true, Property.EMPTY_ARRAY);
    this.titleLayout = titleLayout;
    this.contentLayout = contentLayout;
    this.channelToken = channelToken;
    this.failOnError = failOnError;
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(basePath);
    apiInstance = new DaDaPushMessageApi(apiClient);
  }

  @PluginFactory
  public static DaDaPushAppender createAppender(
      @PluginAttribute("name") String name,
      @PluginAttribute("basePath") String basePath,
      @PluginAttribute("channelToken") String channelToken,
      @PluginAttribute("failOnError") Boolean failOnError,
      @PluginAttribute("title") String title,
      @PluginElement("Layout") Layout<? extends Serializable> contentLayout,
      @PluginElement("Filters") Filter filter) {
    Layout<? extends Serializable> titleLayout;
    if (title != null) {
      titleLayout = PatternLayout.newBuilder().withPattern(title)
          .withCharset(Charset.forName("UTf-8")).build();
    } else {
      titleLayout = PatternLayout.newBuilder().withPattern("[%-5level] %logger{36}")
          .withCharset(Charset.forName("UTf-8")).build();
    }
    if (contentLayout == null) {
      contentLayout = PatternLayout.newBuilder().withPattern("Time: %d{YYYY-MM-dd HH:mm:ss.SSS}\nLevel: %-5level\nLoggerName: %logger{36}\nMessage: %msg%n")
          .withCharset(Charset.forName("UTf-8")).build();
    }
    if (basePath == null) {
      basePath = "https://www.dadapush.com";
    }
    if (failOnError == null) {
      failOnError = false;
    }
    instance = new DaDaPushAppender(name, filter, basePath, channelToken, failOnError, titleLayout,
        contentLayout);
    return instance;
  }

  public static DaDaPushAppender getInstance() {
    return instance;
  }

  public void append(LogEvent logEvent) {
    try {
      if (StringUtils.isEmpty(channelToken)) {
        getStatusLogger().warn("not set channelToken");
        return;
      }
      String title = new String(titleLayout.toByteArray(logEvent));
      String content = new String(contentLayout.toByteArray(logEvent));

      MessagePushRequest body = new MessagePushRequest();
      body.setTitle(StringUtils.substring(title, 0, 50));
      body.setContent(StringUtils.substring(content, 0, 500));
      body.setNeedPush(true);
      try {
        ResultOfMessagePushResponse result = apiInstance.createMessage(body, channelToken);
        if (result.getCode() == 0) {
          getStatusLogger()
              .info("send notification success, messageId=" + result.getData().getMessageId());
        } else {
          getStatusLogger().warn(
              "send notification fail, detail: " + result.getCode() + " " + result.getErrmsg());
        }
      } catch (ApiException e) {
        if (!failOnError) {
          getStatusLogger().error("send notification fail", e);
        } else {
          getStatusLogger().error("send notification fail", e);
        }
      }
    } catch (Exception e) {
      getStatusLogger().error("send notification fail, iLoggingEvent=" + logEvent, e);
    }
  }
}
