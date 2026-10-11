package com.example.utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCTool {

    private static final String URL = "jdbc:mysql://localhost:3306/DateTest?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "20051028";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * 【新增】辅助方法：将驼峰命名转为下划线命名（例如 enrollmentTime -> enrollment_time）
     */
    private static String camelToUnderline(String str) {
        return str.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
    }

    // 方法1：ResultSet 转 List (反射实现)
    public static <T> List<T> resultSetToList(ResultSet rs, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                T obj = clazz.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = rs.getObject(i);

                    for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        // 匹配时忽略下划线和大小写
                        if (field.getName().equalsIgnoreCase(columnName) ||
                                field.getName().equalsIgnoreCase(columnName.replace("_", ""))) {
                            if (columnValue != null) {
                                field.set(obj, columnValue);
                            }
                        }
                    }
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 方法2：保存数据
    public static <T> int save(T obj, Connection connection) {
        Class<?> clazz = obj.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder sqlFields = new StringBuilder();
        StringBuilder sqlValues = new StringBuilder();
        List<Object> params = new ArrayList<>();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if ("id".equalsIgnoreCase(field.getName())) continue;
                Object value = field.get(obj);
                if (value != null) {
                    // 【修改点 1】将属性名转换为下划线后再拼接
                    sqlFields.append(camelToUnderline(field.getName())).append(",");
                    sqlValues.append("?,");
                    params.add(value);
                }
            }
            if (sqlFields.length() > 0) {
                sqlFields.setLength(sqlFields.length() - 1);
                sqlValues.setLength(sqlValues.length() - 1);
            }

            String sql = "INSERT INTO " + tableName + " (" + sqlFields + ") VALUES (" + sqlValues + ")";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                return pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 方法3：更新数据
    public static <T> int update(T obj, Connection connection) {
        Class<?> clazz = obj.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder sqlSet = new StringBuilder();
        List<Object> params = new ArrayList<>();
        Object idValue = null;

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if ("id".equalsIgnoreCase(field.getName())) {
                    idValue = value;
                    continue;
                }
                if (value != null) {
                    // 【修改点 2】将更新字段名转换为下划线后再拼接
                    sqlSet.append(camelToUnderline(field.getName())).append(" = ?,");
                    params.add(value);
                }
            }
            if (sqlSet.length() > 0) {
                sqlSet.setLength(sqlSet.length() - 1);
            }
            params.add(idValue);

            String sql = "UPDATE " + tableName + " SET " + sqlSet + " WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                return pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 方法4：删除数据
    public static <T> int delete(T obj, Connection connection) {
        Class<?> clazz = obj.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(obj);

            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setObject(1, idValue);
                return pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 方法5：根据ID获取单个对象
    public static <T> T getOneById(String id, Class<T> clazz, Connection connection) {
        String tableName = clazz.getSimpleName().toLowerCase();
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<T> list = resultSetToList(rs, clazz);
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}