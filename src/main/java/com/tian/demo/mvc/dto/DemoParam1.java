package com.tian.demo.mvc.dto;

import java.util.Date;

/**
 * 请求参数的封装
 * Created by tianxiong on 2019/4/26.
 */
public class DemoParam1 {
    private Integer age;
    private String name;
    private Date birthday;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "DemoParam1{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
