package com.example.entity;

public class College {
    private Integer id;
    private String name;
    private String code;

    public College() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    @Override
    public String toString() {
        return "College{id=" + id + ", name='" + name + "', code='" + code + "'}";
    }
}