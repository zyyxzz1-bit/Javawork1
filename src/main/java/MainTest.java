import com.example.entity.Student;
import com.example.utils.JDBCTool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;

public class MainTest {
    public static void main(String[] args) {
        try {
            // 1. 获取数据库连接
            Connection conn = JDBCTool.getConnection();
            System.out.println("=== 数据库连接成功 ===");

            // 2. 测试保存 (Insert)
            Student student = new Student();
            student.setId(1);
            student.setName("李华");
            student.setMajor("计算机科学与技术");
            student.setAge(21);
            student.setEnrollmentTime(Date.valueOf("2026-10-11"));
            student.setIsGraduation(false);
            // tuition 字段使用 BigDecimal.valueOf 包装，解决类型匹配问题
            student.setTuition(BigDecimal.valueOf(6000.00));

            // 调用 save 方法
            int saveRows = JDBCTool.save(student, conn);
            System.out.println("学生数据保存影响行数: " + saveRows);

            // 3. 测试根据 ID 查询 (Select)
            Student queryStudent = JDBCTool.getOneById("1", Student.class, conn);
            System.out.println("查询到学生信息: " + queryStudent);

            // 4. 测试更新 (Update)
            if (queryStudent != null) {
                // 修改对象的某些属性
                queryStudent.setAge(22);
                queryStudent.setMajor("软件工程");

                // 调用 update 方法
                int updateRows = JDBCTool.update(queryStudent, conn);
                System.out.println("更新影响行数: " + updateRows);

                // 再次查询验证更新结果
                Student updatedStudent = JDBCTool.getOneById("1", Student.class, conn);
                System.out.println("更新后的学生信息: " + updatedStudent);
            }

            // 5. 测试删除 (Delete)
            int deleteRows = JDBCTool.delete(queryStudent, conn);
            System.out.println("删除影响行数: " + deleteRows);

            Student deletedCheck = JDBCTool.getOneById("1", Student.class, conn);
            System.out.println("删除后查询结果 (应为null): " + deletedCheck);

            // 6. 关闭连接
            JDBCTool.close(conn, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}