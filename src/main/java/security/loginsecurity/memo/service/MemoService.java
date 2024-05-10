package security.loginsecurity.memo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.loginsecurity.memo.domain.entity.Memo;
import security.loginsecurity.memo.domain.repository.MemoRepository;
import security.loginsecurity.memo.dto.MemoDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemoService {
    private final MemoRepository memoRepository;

    @Autowired
    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    // 특정 날짜에 대한 메모 리스트를 가져오는 메서드
    public List<MemoDto> getMemosByDate(LocalDate date) {
        List<Memo> memos = memoRepository.findAllByDate(date);
        return memos.stream()
                .map(m -> new MemoDto(m.getId(), m.getDate(), m.getContent()))
                .collect(Collectors.toList());
    }

    // 특정 ID로 메모를 조회하는 메서드
    public MemoDto getMemoById(Long id) {
        Optional<Memo> memo = memoRepository.findById(id);
        return memo.map(m -> new MemoDto(m.getId(), m.getDate(), m.getContent())).orElse(null);
    }

    // 새로운 메모를 저장하거나 기존 메모를 업데이트하는 메서드
    public void saveOrUpdateMemo(MemoDto memoDto) {
        // 날짜 기준으로 기존 메모 검색
        List<Memo> existingMemos = memoRepository.findAllByDate(memoDto.getDate());

        // 기존 메모가 있으면 업데이트하고 없으면 새로 생성
        if (!existingMemos.isEmpty()) {
            Memo existingMemo = existingMemos.get(0);  // 날짜당 하나만 있다고 가정하고 첫 번째 항목 선택
            existingMemo.setContent(memoDto.getContent());
            memoRepository.save(existingMemo);
        } else {
            Memo newMemo = new Memo(memoDto.getDate(), memoDto.getContent());
            memoRepository.save(newMemo);
        }
    }

    // 메모 업데이트 메서드 (ID 기준)
    public LocalDate updateMemo(Long id, String content) {
        Optional<Memo> optionalMemo = memoRepository.findById(id);
        if (optionalMemo.isPresent()) {
            Memo memo = optionalMemo.get();
            memo.setContent(content);
            memoRepository.save(memo);
            return memo.getDate();
        }
        return null;  // 메모가 없을 경우 null 반환
    }
}
