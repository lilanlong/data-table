package com.lll.datatable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSONObject;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;

/**
 * @author: LLL
 * @since: 2024/07/23  10:22
 * @description: DataTable
 */
public class DataTable implements AutoCloseable {

    private String tableName;
    private final List<JSONObject> dataTable;
    private List<String> columnNameList;
    private static final Db db;
    private static final String TABLE_PREFIX = "LLL";
    private static final String PRIMARY_KEY = "lll_id";

    static {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:h2:mem:DataTable");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 'x'");
        db = DbUtil.use(dataSource);
    }

    public DataTable(List<JSONObject> dataTable) throws SQLException {
        this.dataTable = dataTable;
        if (CollectionUtil.isNotEmpty(dataTable)) {
            String tableName = TABLE_PREFIX + IdUtil.getSnowflakeNextIdStr();
            this.tableName = tableName;
            this.getColumnNameList(dataTable);
            StringBuilder subCreateTableColumnSqlSb = new StringBuilder();
            StringBuilder subInsertIntoColumnSqlSb = new StringBuilder();
            StringBuilder subInsertIntoPlaceholderSqlSb = new StringBuilder();
            for (String columnName : columnNameList) {
                subCreateTableColumnSqlSb.append(columnName).append(" ").append("text").append(",").append(" ");
                subInsertIntoColumnSqlSb.append(columnName).append(",");
                subInsertIntoPlaceholderSqlSb.append("?").append(",");
            }
            subCreateTableColumnSqlSb.append(PRIMARY_KEY + " int, primary key (" + PRIMARY_KEY + ")");
            subInsertIntoColumnSqlSb.append(PRIMARY_KEY);
            subInsertIntoPlaceholderSqlSb.append("?");
            db.execute("CREATE TABLE " + tableName + " (" + subCreateTableColumnSqlSb + ")");
            String subInsertIntoColumnSql = subInsertIntoColumnSqlSb.toString();
            String subInsertIntoPlaceholderSql = subInsertIntoPlaceholderSqlSb.toString();
            for (int j = 0; j < dataTable.size(); j++) {
                JSONObject jsonObj = dataTable.get(j);
                Object[] args = new Object[columnNameList.size() + 1];
                for (int i = 0; i < columnNameList.size(); i++) {
                    String columnName = columnNameList.get(i);
                    args[i] = jsonObj.get(columnName);
                }
                args[columnNameList.size()] = j;
                db.execute("INSERT INTO " + tableName + "(" + subInsertIntoColumnSql + ") VALUES(" + subInsertIntoPlaceholderSql + ")", args);
            }
        }
    }

    public List<JSONObject> select(String subFilterSql) throws SQLException {
        if (StrUtil.isBlank(subFilterSql)) {
            return dataTable;
        }
        List<JSONObject> dataTable = new ArrayList<>();
        String sql = "SELECT " + PRIMARY_KEY + " FROM " + tableName + " WHERE " + subFilterSql;
        List<Entity> dataList = db.query(sql);
        if (CollectionUtil.isNotEmpty(dataList)) {
            dataList.forEach(dl -> {
                dataTable.add(this.dataTable.get(dl.getInt(PRIMARY_KEY)));
            });
        }
        return dataTable;
    }

    private void getColumnNameList(List<JSONObject> dataTable) {
        this.columnNameList = new ArrayList<>(dataTable.get(0).keySet());
    }

    @Override
    public void close() throws Exception {
        if (StrUtil.isNotBlank(tableName)) {
            db.execute("DROP TABLE " + tableName + " IF EXISTS");
        }
    }

}
