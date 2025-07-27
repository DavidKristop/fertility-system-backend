package com.group3.backend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocuSealContract {
    private String submissionName;
    private String documentName;
    private String documentHtml;
    private String submitterRole;
    private String submitterEmail;
}
