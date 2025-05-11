package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public class User {

    @Id
    private Long id;
    private String name;
    private Integer age;
    private String status;

    public User(Long id, String name, Integer age, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.status = status;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Integer age;
        private String status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public User build() {
            return new User(id, name, age, status);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", status='" + status + '\'' +
                '}';
    }
}