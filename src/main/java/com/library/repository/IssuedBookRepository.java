package com.library.repository;

import com.library.entity.IssuedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedBookRepository
        extends JpaRepository<IssuedBook, Long> {
}