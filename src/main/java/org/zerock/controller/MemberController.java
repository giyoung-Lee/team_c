package org.zerock.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zerock.domain.Member;
import org.zerock.service.MemberService;

@Controller
@RequestMapping("/member/*")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "member/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "member/register";
    }

    @PostMapping("/register")
    public String insert(@ModelAttribute Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberService.addMember(member, false);
        return "redirect:/";
    }

    @RequestMapping(value = "/emailCheck", method = RequestMethod.POST)
    @ResponseBody
    public String emailCheck(Member member) throws Exception {
        System.out.println(member);
        boolean result = memberService.checkEmail(member.getEmail());
        System.out.println(result);
        if(result) {
            // 중복된 이메일이 존재하지 않으면 false
            return "false";
        } else {
            return "true";
        }
    }
}
