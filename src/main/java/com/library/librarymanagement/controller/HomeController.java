package com.library.librarymanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.library.entity.Member;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import java.time.LocalDate;
import com.library.repository.IssuedBookRepository;
import com.library.entity.IssuedBook;
import com.library.entity.ReturnedBook;
import com.library.repository.ReturnedBookRepository;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;

    private final BookRepository bookRepository;

    private final IssuedBookRepository issuedBookRepository;

    private final ReturnedBookRepository returnedBookRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("issuedBooks", issuedBookRepository.findAll());
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("issuedBooksCount", issuedBookRepository.count());
        model.addAttribute("totalMembers", memberRepository.count());

        long availableBooks =
            bookRepository.findAll()
                .stream()
                .mapToLong(Book::getAvailableQuantity)
                .sum();

        model.addAttribute("availableBooks", availableBooks);

        return "dashboard";
    }
    @PostMapping("/issuebook")
    public String saveIssuedBook(
            Long bookId,
            String bookName,
            Long memberId,
            LocalDate dueDate) {

        Book book = bookRepository
            .findById(bookId)
            .orElseThrow();

        Member member = memberRepository
            .findById(memberId)
            .orElseThrow();

        if (book.getAvailableQuantity() == null ||
         book.getAvailableQuantity() <= 0) {
            return "redirect:/books";
        }

        IssuedBook issuedBook = new IssuedBook();

        issuedBook.setBookId(book.getId());
        issuedBook.setBookName(book.getTitle());

        issuedBook.setMemberId(member.getId());
        issuedBook.setMemberName(member.getName());

        issuedBook.setIssueDate(LocalDate.now());
        issuedBook.setDueDate(dueDate);

        issuedBookRepository.save(issuedBook);

        book.setAvailableQuantity(
            book.getAvailableQuantity() - 1
        );

        bookRepository.save(book);

        return "redirect:/";
    }

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books",
                bookRepository.findAll());
        return "books";
    }

    @GetMapping("/deletebook/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books";
    }

    @GetMapping("/addbooks")
    public String addBooks() {
        return "addbooks";
    }
    @PostMapping("/addbooks")
    public String saveBook(@ModelAttribute Book book) {
        book.setAvailableQuantity(
                book.getQuantity()
        );
        bookRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("members",
                memberRepository.findAll());
        return "members";
    }

    @GetMapping("/deletemember/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberRepository.deleteById(id);
            return "redirect:/members";
    }

    @GetMapping("/addmembers")
    public String addMembers() {
        return "addmembers";
    }
    @PostMapping("/addmembers")
    public String saveMember(@ModelAttribute Member member) {
        member.setJoinedDate(LocalDate.now());
        memberRepository.save(member);
    return "redirect:/members";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/issuebook/{id}")
    public String issueBook(
        @PathVariable Long id, Model model) {
            Book book = bookRepository.findById(id)
                .orElseThrow();
            model.addAttribute("book", book);
            model.addAttribute(
                "members",
                memberRepository.findAll()
            );
            return "issuebook";
        }
    
        
    @GetMapping("/returnbooks")
    public String returnBooks(Model model) {

        model.addAttribute(
            "returnedBooks",
            returnedBookRepository.findAll()
        );

        return "returnbooks";
    }

    @GetMapping("/returnbook/{id}")
    public String returnBook(
            @PathVariable Long id) {

        IssuedBook issuedBook = 
            issuedBookRepository.findById(id)
                .orElseThrow();

        ReturnedBook returnedBook = 
            new ReturnedBook();

        returnedBook.setBookId(
            issuedBook.getBookId());

        returnedBook.setBookName(
            issuedBook.getBookName());

        returnedBook.setMemberId(
            issuedBook.getMemberId());

        returnedBook.setMemberName(
            issuedBook.getMemberName());

        returnedBook.setIssueDate(
            issuedBook.getIssueDate());

        returnedBook.setReturnDate(
            LocalDate.now());

        returnedBookRepository.save(
            returnedBook);

        Book book = 
            bookRepository.findById(
                issuedBook.getBookId())
                    .orElseThrow();

        book.setAvailableQuantity(
            book.getAvailableQuantity() + 1
        );

        bookRepository.save(book);

        issuedBookRepository.delete(
            issuedBook);

        return "redirect:/";
    }

}

