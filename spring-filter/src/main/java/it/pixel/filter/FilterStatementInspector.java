package it.pixel.filter;

import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@Primary
@ConditionalOnProperty(prefix = "it.pixel.spring", name = "enable_filter", havingValue = "true")
public class FilterStatementInspector implements StatementInspector {

    private static final Logger LOG = LoggerFactory.getLogger(FilterStatementInspector.class);


    @Override
    public String inspect(String sql) {
        LOG.debug("FilterStatementInspector inspect   [IN] -> {}", sql);
        sql = manageSQL(sql);
        LOG.debug("FilterStatementInspector inspect  [OUT] -> {}", sql);
        return sql;
    }

    @SneakyThrows // a throw of JSQLParserException will never happen because of hibernate pre-parse
    private String manageSQL(String sql) {
        FilterManager filterManager = FilterManager.getInstance();
        if (filterManager.getFiltersStatus() == FilterManager.Status.ENABLED) {
            CCJSqlParserManager manager = new CCJSqlParserManager();
            PlainSelect select = (PlainSelect) ((Select) manager.parse(new StringReader(sql.toUpperCase()))).getSelectBody();
            sql = addConditions(select).toString();
        }
        return sql;
    }

    private PlainSelect addConditions(PlainSelect select) {
        FilterManager filterManager = FilterManager.getInstance();

        FromItem fromItem = select.getFromItem();
        if (fromItem instanceof SubSelect) {
            addConditions((PlainSelect) ((SubSelect) fromItem).getSelectBody());
        }
        String fromAlias = fromItem.getAlias() == null ? null : fromItem.getAlias().getName();
        List<String> aliases = new ArrayList<>();

        if (select.getJoins() != null) {
            for (Join join : select.getJoins()) {
                join.getOnExpressions().forEach(this::checkForSubSelect);
            }
            aliases = select.getJoins()
                    .stream()
                    .map(Join::getRightItem)
                    .filter(x -> filterManager.getFilterableEntity().get(((Table) x).getASTNode().jjtGetFirstToken().image))
                    .map(FromItem::getAlias)
                    .filter(Objects::nonNull)
                    .map(Alias::getName)
                    .collect(Collectors.toList());
        }
        Expression where = select.getWhere();

        if (where != null) {
            checkForSubSelect(where);
        }

        if (fromAlias != null) {
            aliases.add(fromAlias);
        }

        for (String alias : aliases) {
            EqualsTo rightExpression = new EqualsTo();
            rightExpression.setLeftExpression(new Column(alias + "." + FilterManager.FIELD_FLAG_ELIMINATO));
            rightExpression.setRightExpression(new StringValue(FilterManager.FIELD_FLAG_ELIMINATO_VALUE));
            where = new AndExpression(where, rightExpression);
        }

        select.setWhere(where);

        return select;
    }

    private void checkForSubSelect(Expression expression) {
        if (expression == null) {
            return;
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            Expression left = binaryExpression.getLeftExpression();
            Expression right = binaryExpression.getRightExpression();
            checkForSubSelect(left);
            checkForSubSelect(right);
        } else if (expression instanceof InExpression inExpression) {
            Expression left = inExpression.getLeftExpression();
            Expression right = (Expression) inExpression.getRightItemsList();
            checkForSubSelect(left);
            checkForSubSelect(right);
        } else if (expression instanceof IsNullExpression isNullExpression) {
            Expression left = isNullExpression.getLeftExpression();
            checkForSubSelect(left);
        } else if (expression instanceof Function function) {
            List<Expression> left = function.getParameters().getExpressions();
            for (Expression param : left)
                checkForSubSelect(param);
        } else if (expression instanceof SubSelect subSelect) {
            addConditions((PlainSelect) subSelect.getSelectBody());
        }
    }


}
