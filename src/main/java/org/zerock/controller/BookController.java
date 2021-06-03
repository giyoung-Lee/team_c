package org.zerock.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.*;
import org.zerock.service.*;
import org.zerock.service.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/book/*")
@AllArgsConstructor
@Log4j
public class BookController {
    private final KeepBookService keepBookService;
    private final LogBorrowService logBorrowService;
    private final CategoryService categoryService;
    private final IsbnService isbnService;
    private final LocationService locationService;
    private final BookService bookService;

    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        log.info("list: " + cri);
        model.addAttribute("list", keepBookService.selectKeepBookWithPaging(cri));

        int total = keepBookService.getTotalCount();
        log.info("total: " + total);

        model.addAttribute("pageMaker", new PageDTO(cri, total));

        return "book/list";
    }

    @GetMapping("/get/{id}")
    public String get(@PathVariable("id") Long id, @ModelAttribute("cri") Criteria cri, Model model) {
        log.info("/get/" + id);
        KeepBook keepBook = keepBookService.selectKeepBookById(id);
        model.addAttribute("book", keepBook);

        if(keepBook.getIsBorrowed()) {
            LogBorrow borrowUser = logBorrowService.selectLogBorrowByBookIdAndIsBorrowed(keepBook.getId(), true).get(0);
            model.addAttribute("borrowUser", borrowUser);
        }

        model.addAttribute("borrowLog", logBorrowService.selectLogBorrowByBookIdAndIsBorrowed(keepBook.getId(), false));
        return "book/get";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/return")
    public String returnBook(@RequestParam("id") Long id, @ModelAttribute("cri") Criteria cri,
                             RedirectAttributes rttr) {
        log.info("/return");

        if(logBorrowService.updateLogBorrowToReturned(id)) {
            rttr.addFlashAttribute("result", "success");
        }

        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());

        rttr.addAttribute("type", cri.getType());
        rttr.addAttribute("keyword", cri.getKeyword());

        return "redirect:/book/list";
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public String register(Isbn isbn) {
        log.info("register: " + isbn);

        isbnService.insertIsbn(isbn);

        List<Book> books = new ArrayList<>();
        for(int i = 0; i < isbn.getCount(); i++) {
            Book book = new Book();
            book.setIsbn(isbn.getIsbn());

            books.add(book);
        }

        bookService.insertBook(books);

        return "redirect:/board/list";
    }

    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public String getRegister(Model model) {
        model.addAttribute("categories", categoryService.selectCategory());
        model.addAttribute("locations", locationService.selectLocation());

        return "book/register";
    }
}
