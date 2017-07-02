package org.skywalking.apm.plugin.httpClient.v4;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.skywalking.apm.agent.core.conf.Config;
import org.skywalking.apm.agent.core.context.ContextCarrier;
import org.skywalking.apm.agent.core.context.ContextManager;
import org.skywalking.apm.agent.core.context.tag.Tags;
import org.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.skywalking.apm.network.trace.component.ComponentsDefine;

/**
 * {@link HttpClientExecuteInterceptor} transport the trace context by call {@link HttpRequest#setHeader(Header)},
 * The {@link Tags#STATUS_CODE} will be set if {@link StatusLine#getStatusCode()} is not equals 200.
 *
 * @author zhangxin
 */
public class HttpClientExecuteInterceptor implements InstanceMethodsAroundInterceptor {

    @Override public void beforeMethod(EnhancedInstance objInst, String methodName, Object[] allArguments,
        Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        if (allArguments[0] == null || allArguments[1] == null) {
            // illegal args, can't trace. ignore.
            return;
        }
        final HttpHost httpHost = (HttpHost)allArguments[0];
        HttpRequest httpRequest = (HttpRequest)allArguments[1];
        final ContextCarrier contextCarrier = new ContextCarrier();
        AbstractSpan span = null;
        String remotePeer = httpHost.getHostName() + ":" + httpHost.getPort();
        try {
            URL url = new URL(httpRequest.getRequestLine().getUri());
            span = ContextManager.createExitSpan(url.getPath(), contextCarrier, remotePeer);
        } catch (MalformedURLException e) {
            throw e;
        }

        span.setComponent(ComponentsDefine.HTTPCLIENT);
        Tags.URL.set(span, httpRequest.getRequestLine().getUri());
        SpanLayer.asHttp(span);

        httpRequest.setHeader(Config.Plugin.Propagation.HEADER_NAME, contextCarrier.serialize());
    }

    @Override public Object afterMethod(EnhancedInstance objInst, String methodName, Object[] allArguments,
        Class<?>[] argumentsTypes, Object ret) throws Throwable {
        if (allArguments[0] == null || allArguments[1] == null) {
            return ret;
        }

        HttpResponse response = (HttpResponse)ret;
        int statusCode = response.getStatusLine().getStatusCode();
        AbstractSpan span = ContextManager.activeSpan();
        if (statusCode != 200) {
            span.errorOccurred();
            Tags.STATUS_CODE.set(span, statusCode + "");
        }

        ContextManager.stopSpan();
        return ret;
    }

    @Override public void handleMethodException(EnhancedInstance objInst, String methodName, Object[] allArguments,
        Class<?>[] argumentsTypes, Throwable t) {
        AbstractSpan activeSpan = ContextManager.activeSpan();
        activeSpan.errorOccurred();
        activeSpan.log(t);
    }
}
