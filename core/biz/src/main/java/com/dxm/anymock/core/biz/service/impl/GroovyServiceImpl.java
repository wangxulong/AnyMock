package com.dxm.anymock.core.biz.service.impl;

import com.dxm.anymock.common.base.enums.ResultCode;
import com.dxm.anymock.common.base.exception.BizException;
import com.dxm.anymock.common.base.exception.SysException;
import com.dxm.anymock.core.biz.service.GroovyService;
import com.dxm.anymock.core.biz.HttpMockContext;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class GroovyServiceImpl implements GroovyService {

    private static final Logger logger = LoggerFactory.getLogger(GroovyServiceImpl.class);

    private static final String PRE_IMPORT = "import groovy.security.XmlSlurper\n"
                                           + "import groovy.json.JsonSlurper\n";

    @Autowired
    private GroovyClassLoader groovyClassLoader;

    @Override
    public String exec(Binding binding, String text) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        binding.setProperty("out", printWriter);

        String name = "script" + System.currentTimeMillis() + Math.abs(text.hashCode()) + ".groovy";
        text = PRE_IMPORT + text;
        GroovyCodeSource groovyCodeSource = new GroovyCodeSource(text, name, "/groovy/script");
        Class groovyClass = groovyClassLoader.parseClass(groovyCodeSource);
        Script script = InvokerHelper.createScript(groovyClass, binding);

        Object result;
        try {
            result = script.run();
        } catch (Throwable e) {
            throw new BizException(ResultCode.GROOVY_SCRIPT_EXEC_EXCEPTION, e);
        }

        logger.info(stringWriter.toString());
        if (result == null) {
            return "";
        }

        try {
            if (result.getClass().getMethod("toString").getDeclaringClass().equals(Object.class)) {
                throw new BizException(ResultCode.FAILED_TO_CAST_GROOVY_SCRIPT_EXEC_RESULT_TO_STRING);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method toString not found");
        }
        return result.toString();
    }
}