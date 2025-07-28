package com.group3.backend.service;

import com.group3.backend.dto.request.CreateFeedbackRequest;
import com.group3.backend.dto.response.FeedbackResponse;
import com.group3.backend.model.Feedback;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.User;
import com.group3.backend.repository.FeedbackRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.service.HtmlStringImageUploaderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TreatmentRepository treatmentRepository;
    private final UserRepository userRepository;
    private final HtmlStringImageUploaderService htmlStringImageUploaderService;

    public void createFeedback(CreateFeedbackRequest request) {
        // Lấy treatment và kiểm tra trạng thái
        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
            .orElseThrow(() -> new EntityNotFoundException("Treatment not found"));

        if (!treatment.getStatus().equals(Treatment.Status.COMPLETED)) {
            throw new IllegalStateException("Chỉ được feedback khi treatment đã hoàn thành");
        }

        // Lấy user hiện tại từ context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Xử lý hình ảnh trong nội dung feedback
        String processedContent = htmlStringImageUploaderService.uploadImagesFromHtmlString(request.getContent());
        
        Feedback feedback = Feedback.builder()
            .treatment(treatment)
            .user(patient)
            .content(processedContent)
            .build();

        feedbackRepository.save(feedback);
    }

    public Page<FeedbackResponse> getAllFeedbacks(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    Page<Feedback> feedbacks = feedbackRepository.findAll(pageable);
    
    return feedbacks.map(fb -> {
        FeedbackResponse res = new FeedbackResponse();
        res.setId(fb.getId());
        res.setContent(fb.getContent());
        res.setTreatmentId(fb.getTreatment().getId());
        res.setTreatmentName(fb.getTreatment().getTitle());
        res.setPatientName(fb.getUser().getFullName());
        return res;
    });
}
}
