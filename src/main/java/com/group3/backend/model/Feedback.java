package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import com.group3.backend.utils.Constants;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content",length = Constants.MAX_CONTENT_UPLOAD_LENGTH)
    private String content;

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
