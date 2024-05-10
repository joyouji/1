package security.loginsecurity.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import security.loginsecurity.memo.dto.MemoDto;
import security.loginsecurity.memo.service.MemoService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("memo/memo")
public class MemoController {
    private final MemoService memoService;

    @Autowired
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @GetMapping
    public String showMemo(@RequestParam("date") String date, Model model) {
        LocalDate localDate = LocalDate.parse(date);
        List<MemoDto> memos = memoService.getMemosByDate(localDate);
        model.addAttribute("date", date);
        model.addAttribute("memos", memos);
        return "/memo";
    }

    @PostMapping("/save")
    public String saveOrUpdateMemo(@RequestParam("date") String date, @RequestParam("content") String content) {
        LocalDate localDate = LocalDate.parse(date);
        MemoDto memoDto = new MemoDto(localDate, content);
        memoService.saveOrUpdateMemo(memoDto);
        return "redirect:/memo/memo?date=" + date;
    }

    /*@GetMapping("/edit")
    public String editMemo(@RequestParam("id") Long id, Model model) {
        MemoDto memo = memoService.getMemoById(id);
        model.addAttribute("memo", memo);
        return "/editMemo";
    }*/
    /*@GetMapping("/memo/edit/{id}")
    public String editMemo(@PathVariable("id") Long id, Model model) {
        MemoDto memo = memoService.getMemoById(id);
        if (memo != null) {
            model.addAttribute("memo", memo);
            return "editMemo"; // 수정 폼이 있는 뷰의 이름
        }
        return "redirect:/memo"; // 메모가 없을 경우 메모 리스트로 리다이렉트
    }*/
    /*@GetMapping("/memo/edit/{id}")
    public String editMemo(@PathVariable("id") Long id, Model model) {
        MemoDto memo = memoService.getMemoById(id);
        if (memo != null) {
            model.addAttribute("memo", memo); // `Model` 객체를 통해 메모 데이터를 전달
            return "editMemo"; // 수정 폼 페이지의 이름
        }
        return "redirect:/memo"; // 메모가 없으면 리스트 페이지로 리다이렉트
    }

    @PostMapping("/update")
    public String updateMemo(@RequestParam("id") Long id, @RequestParam("content") String content) {
        LocalDate date = memoService.updateMemo(id, content);
        return "redirect:/memo/memo?date=" + date;
    }*/
    @GetMapping("/edit/{id}")
    public String editMemo(@PathVariable("id") Long id, Model model) {
        MemoDto memo = memoService.getMemoById(id);
        if (memo != null) {
            model.addAttribute("memo", memo); // 기존 메모 내용을 모델에 전달
            return "editMemo"; // 수정 폼 페이지의 이름 반환
        }
        return "redirect:/memo"; // 메모가 없을 경우 리스트로 리다이렉트
    }

    /*@PostMapping("/update")
    public String updateMemo(@RequestParam("id") Long id, @RequestParam("content") String content) {
        memoService.updateMemo(id, content); // 기존 메모 업데이트
        return "redirect:/memo"; // 메모 리스트로 리다이렉트
    }*/
    @PostMapping("/update")
    public String updateMemo(@RequestParam("id") Long id, @RequestParam("content") String content) {
        memoService.updateMemo(id, content); // 기존 메모 업데이트
        return "redirect:/memo/memo?date=" + memoService.getMemoById(id).getDate().toString();
    }
}
