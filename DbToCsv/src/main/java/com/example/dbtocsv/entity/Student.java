package com.example.dbtocsv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STUDENT")
public class Student
{
    @Id
    private int id;

    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String birthDate;
    private String email;
    private String phone;
}
