package com.example.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Student {
    private Integer id;
    private String name;
    private String major;
    private Integer age;
    private Date enrollmentTime;
    private Boolean isGraduation;
    private BigDecimal tuition;

    public Student() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Date getEnrollmentTime() { return enrollmentTime; }
    public void setEnrollmentTime(Date enrollmentTime) { this.enrollmentTime = enrollmentTime; }
    public Boolean getIsGraduation() { return isGraduation; }
    public void setIsGraduation(Boolean graduation) { isGraduation = graduation; }
    public BigDecimal getTuition() { return tuition; }
    public void setTuition(BigDecimal tuition) { this.tuition = tuition; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', major='" + major + "', age=" + age +
                ", enrollmentTime=" + enrollmentTime + ", isGraduation=" + isGraduation + ", tuition=" + tuition + "}";
    }
}