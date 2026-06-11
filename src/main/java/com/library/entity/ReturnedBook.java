package com.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "returned_books")
@Getter
@Setter
public class ReturnedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private String bookName;

    private Long memberId;

    private String memberName;

    private LocalDate issueDate;

    private LocalDate returnDate;
}

