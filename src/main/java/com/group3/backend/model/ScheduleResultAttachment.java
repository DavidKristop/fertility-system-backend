package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "schedule_result_attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResultAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "attachment_url", nullable = false)
    private String attachmentUrl;

    @ManyToOne
    @JoinColumn(name = "schedule_result_id")
    private ScheduleResult scheduleResult;
}
