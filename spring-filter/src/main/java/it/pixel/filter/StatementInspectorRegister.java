package it.pixel.filter;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Project: common-library
 * Author: Michele
 * File: StatementInspectorRegister
 * Creation: 02/12/2022
 */
@Component
@ConditionalOnProperty(prefix = "it.pixel.spring", name = "enable_filter", havingValue = "true")
public class StatementInspectorRegister implements HibernatePropertiesCustomizer {
    private final StatementInspector statementInspector;

    @Autowired
    public StatementInspectorRegister(StatementInspector statementInspector) {
        this.statementInspector = statementInspector;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.statement_inspector", this.statementInspector);
    }
}
