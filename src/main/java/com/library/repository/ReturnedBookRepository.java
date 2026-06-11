package com.library.repository;

import com.library.entity.ReturnedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnedBookRepository
        extends JpaRepository<ReturnedBook, Long> {
}
