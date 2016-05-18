package org.shop.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vprasanna on 5/17/2016.
 */
@Component
public class ProfilingMethodInterceptor implements MethodInterceptor {

    private static final Log log = LogFactory.getLog(ProfilingMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final StopWatch stopWatch = new StopWatch(invocation.getMethod().toGenericString());
        final Log classLogger = LogFactory.getLog(invocation.getMethod().getDeclaringClass());
        stopWatch.start("invocation.proceed()");
        String method = buildMethod(invocation);
        try {
            return invocation.proceed();
        } finally {
            stopWatch.stop();
            classLogger.info(String.format("%s took %d ms", method, stopWatch.getTotalTimeMillis()));
        }
    }

    private String buildMethod(MethodInvocation invocation) {
        StringBuilder builder = new StringBuilder();


        builder.append(invocation.getMethod().getDeclaringClass()).append(".")
                .append(invocation.getMethod().getName())
                .append(buildParamMap(invocation));


        return builder.toString();
    }
    private Map<String, String> buildParamMap(MethodInvocation invocation) {
        Map<String, String> map = new LinkedHashMap<>();
        List objects =  Arrays.asList(invocation.getArguments());
        Arrays.asList(invocation.getMethod().getParameters()).stream()
                .forEach(param ->  {
                    try {
                        //Hack!Hack!Hack!
                        //I cannot have any counter running inside
                        //the labda expression so the trick
                        //map.size() works :)
                        map.put(param.toString(), getValue(objects.get(map.size())));
                    } finally {
                        //Nothing to do
                    }
                });


        return map;
    }

    private String getValue(Object o) {
        if (null == o) {
            return "null";
        } else {
            return o.toString();
        }
    }

}
