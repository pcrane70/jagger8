package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/6/14
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotNullInvocationListenerDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return NotNullInvocationListener.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
    }
}
