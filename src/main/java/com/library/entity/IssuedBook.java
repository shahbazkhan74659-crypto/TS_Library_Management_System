package com.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "issued_books")
@Getter
@Setter
public class IssuedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private String bookName;

    private Long memberId;

    private String memberName;

    private LocalDate issueDate;

    private LocalDate dueDate;
}

